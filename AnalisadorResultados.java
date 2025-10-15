import java.io.*;
import java.util.*;

public class AnalisadorResultados {
    
    public static void main(String[] args) {
        try {
            analisarResultadosCSV();
            gerarRelatorioCompleto();
            System.out.println("Análise concluída! Verifique os arquivos em resultados/");
        } catch (IOException e) {
            System.err.println("Erro na análise: " + e.getMessage());
        }
    }
    
    public static void analisarResultadosCSV() throws IOException {
        System.out.println("=== ANÁLISE DETALHADA DOS RESULTADOS ===");
        
        List<String[]> resultados = carregarResultadosCSV();
        
        if (resultados.isEmpty()) {
            System.out.println("Nenhum resultado encontrado para análise.");
            return;
        }
        
        analisarDesempenhoGeral(resultados);
        analisarFuncoesHash(resultados);
        analisarEstrategiasColisao(resultados);
        analisarGaps(resultados);
        analisarMaioresListas(resultados);
    }
    
    private static List<String[]> carregarResultadosCSV() throws IOException {
        List<String[]> resultados = new ArrayList<>();
        
        File arquivo = new File("resultados/resultados.csv");
        if (!arquivo.exists()) {
            System.out.println("Arquivo resultados.csv não encontrado!");
            return resultados;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            boolean primeira = true;
            
            while ((linha = reader.readLine()) != null) {
                if (primeira) {
                    primeira = false;
                    continue;
                }
                
                String[] dados = linha.split(",");
                if (dados.length >= 14) {
                    resultados.add(dados);
                }
            }
        }
        
        System.out.println("Carregados " + resultados.size() + " resultados para análise");
        return resultados;
    }
    
    private static void analisarDesempenhoGeral(List<String[]> resultados) {
        System.out.println("\n--- DESEMPENHO GERAL ---");
        
        Map<String, List<String[]>> porTipo = new HashMap<>();
        for (String[] r : resultados) {
            String tipo = r[0]; // TipoTabela
            porTipo.computeIfAbsent(tipo, k -> new ArrayList<>()).add(r);
        }
        
        for (Map.Entry<String, List<String[]>> entry : porTipo.entrySet()) {
            String tipo = entry.getKey();
            List<String[]> lista = entry.getValue();
            
            double tempoMedio = lista.stream()
                .mapToLong(r -> safeParseLong(r[6])) // TempoInsercaoNs
                .average().orElse(0) / 1_000_000.0;
                
            double colisoesMedias = lista.stream()
                .mapToInt(r -> safeParseInt(r[5])) // Colisoes
                .average().orElse(0);
                
            double fatorCargaMedio = lista.stream()
                .mapToDouble(r -> safeParseDouble(r[4])) // FatorCarga
                .average().orElse(0);
            
            System.out.printf("%-20s: %6.2f ms, %8.1f colisões, carga %.2f\n", 
                            tipo, tempoMedio, colisoesMedias, fatorCargaMedio);
        }
    }
    
    private static void analisarFuncoesHash(List<String[]> resultados) {
        System.out.println("\n--- COMPARAÇÃO FUNÇÕES HASH ---");
        
        Map<String, List<String[]>> porFuncao = new HashMap<>();
        for (String[] r : resultados) {
            String funcao = r[1]; // FuncaoHash
            porFuncao.computeIfAbsent(funcao, k -> new ArrayList<>()).add(r);
        }
        
        for (Map.Entry<String, List<String[]>> entry : porFuncao.entrySet()) {
            String funcao = entry.getKey();
            List<String[]> lista = entry.getValue();
            
            double tempoMedio = lista.stream()
                .mapToLong(r -> safeParseLong(r[6]))
                .average().orElse(0) / 1_000_000.0;
                
            double colisoesMedias = lista.stream()
                .mapToInt(r -> safeParseInt(r[5]))
                .average().orElse(0);
            
            System.out.printf("%-15s: %6.2f ms, %8.1f colisões\n", funcao, tempoMedio, colisoesMedias);
        }
    }
    
    private static void analisarEstrategiasColisao(List<String[]> resultados) {
        System.out.println("\n--- ESTRATÉGIAS DE COLISÃO ---");
        
        Map<String, List<String[]>> porRehash = new HashMap<>();
        for (String[] r : resultados) {
            String tipo = r[0]; // TipoTabela
            if (tipo.startsWith("Rehashing")) {
                String tipoRehash = tipo.split("-")[1];
                porRehash.computeIfAbsent(tipoRehash, k -> new ArrayList<>()).add(r);
            }
        }
        
        for (Map.Entry<String, List<String[]>> entry : porRehash.entrySet()) {
            String rehash = entry.getKey();
            List<String[]> lista = entry.getValue();
            
            double tempoMedio = lista.stream()
                .mapToLong(r -> safeParseLong(r[6]))
                .average().orElse(0) / 1_000_000.0;
                
            double colisoesMedias = lista.stream()
                .mapToInt(r -> safeParseInt(r[5]))
                .average().orElse(0);
            
            System.out.printf("Rehashing %-10s: %6.2f ms, %8.1f colisões\n", rehash, tempoMedio, colisoesMedias);
        }
    }
    
    private static void analisarGaps(List<String[]> resultados) {
        System.out.println("\n--- ANÁLISE DE GAPS ---");
        
        List<String[]> encadeamentos = new ArrayList<>();
        for (String[] r : resultados) {
            if (r[0].equals("Encadeamento")) {
                encadeamentos.add(r);
            }
        }
        
        if (!encadeamentos.isEmpty()) {
            double menorGapMedio = encadeamentos.stream()
                .mapToInt(r -> safeParseInt(r[11])) // MenorGap
                .average().orElse(0);
                
            double maiorGapMedio = encadeamentos.stream()
                .mapToInt(r -> safeParseInt(r[12])) // MaiorGap
                .average().orElse(0);
                
            double mediaGaps = encadeamentos.stream()
                .mapToDouble(r -> safeParseDouble(r[13])) // MediaGaps
                .average().orElse(0);
            
            System.out.printf("Gaps médios - Menor: %.1f, Maior: %.1f, Média: %.1f\n", 
                            menorGapMedio, maiorGapMedio, mediaGaps);
            
            // Encontrar pior distribuição
            String[] piorDistribuicao = null;
            int maxGap = 0;
            for (String[] r : encadeamentos) {
                int gap = safeParseInt(r[12]);
                if (gap > maxGap) {
                    maxGap = gap;
                    piorDistribuicao = r;
                }
            }
            
            if (piorDistribuicao != null) {
                System.out.printf("Pior distribuição: %s (tabela %s) com gap máximo de %d\n", 
                                piorDistribuicao[1], piorDistribuicao[2], maxGap);
            }
        }
    }
    
    private static void analisarMaioresListas(List<String[]> resultados) {
        System.out.println("\n--- MAIORES LISTAS ENCADEADAS ---");
        
        List<String[]> encadeamentos = new ArrayList<>();
        for (String[] r : resultados) {
            if (r[0].equals("Encadeamento")) {
                encadeamentos.add(r);
            }
        }
        
        if (!encadeamentos.isEmpty()) {
            // Ordenar por maior lista
            encadeamentos.sort((a, b) -> {
                int listaA = safeParseInt(a[8]); // MaiorLista
                int listaB = safeParseInt(b[8]);
                return Integer.compare(listaB, listaA);
            });
            
            System.out.println("Top 3 maiores listas encadeadas:");
            for (int i = 0; i < Math.min(3, encadeamentos.size()); i++) {
                String[] r = encadeamentos.get(i);
                System.out.printf("%d. %s (tabela %s, carga %s%%) - Listas: %s, %s, %s\n", 
                                i + 1, r[1], r[2], r[4], r[8], r[9], r[10]);
            }
        }
    }
    
    private static void gerarRelatorioCompleto() throws IOException {
        System.out.println("\n=== GERANDO RELATÓRIO COMPLETO ===");
        
        List<String[]> resultados = carregarResultadosCSV();
        
        // Garantir que a pasta resultados existe
        new File("resultados").mkdirs();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultados/relatorio_completo.txt"))) {
            writer.write("RELATÓRIO COMPLETO - ANÁLISE DE TABELAS HASH\n");
            writer.write("============================================\n\n");
            
            // Resumo Executivo
            writer.write("RESUMO EXECUTIVO\n");
            writer.write("----------------\n");
            writer.write(String.format("Total de testes executados: %d\n\n", resultados.size()));
            
            int encadeamentos = 0;
            int rehashings = 0;
            for (String[] r : resultados) {
                if (r[0].equals("Encadeamento")) {
                    encadeamentos++;
                } else {
                    rehashings++;
                }
            }
            
            writer.write(String.format("• Encadeamentos: %d testes\n", encadeamentos));
            writer.write(String.format("• Rehashings: %d testes\n\n", rehashings));
            
            // Estatísticas Gerais
            writer.write("ESTATÍSTICAS GERAIS\n");
            writer.write("-------------------\n");
            
            double tempoTotal = 0;
            double colisoesTotal = 0;
            for (String[] r : resultados) {
                tempoTotal += safeParseLong(r[6]) / 1_000_000.0;
                colisoesTotal += safeParseInt(r[5]);
            }
            
            writer.write(String.format("Tempo médio de inserção: %.2f ms\n", tempoTotal / resultados.size()));
            writer.write(String.format("Colisões médias: %.1f\n\n", colisoesTotal / resultados.size()));
            
            // Melhores Combinações
            writer.write("MELHORES COMBINAÇÕES\n");
            writer.write("---------------------\n");
            
            // Ordenar por tempo
            resultados.sort((a, b) -> Long.compare(safeParseLong(a[6]), safeParseLong(b[6])));
            
            writer.write("Mais Rápidas (top 5):\n");
            for (int i = 0; i < Math.min(5, resultados.size()); i++) {
                String[] r = resultados.get(i);
                writer.write(String.format("%d. %s + %s: %.2f ms (%s colisões)\n", 
                    i + 1, r[0], r[1], 
                    safeParseLong(r[6]) / 1_000_000.0, r[5]));
            }
            writer.write("\n");
            
            // Ordenar por colisões
            resultados.sort((a, b) -> Integer.compare(safeParseInt(a[5]), safeParseInt(b[5])));
            
            writer.write("Menos Colisões (top 5):\n");
            for (int i = 0; i < Math.min(5, resultados.size()); i++) {
                String[] r = resultados.get(i);
                writer.write(String.format("%d. %s + %s: %s colisões (%.2f ms)\n", 
                    i + 1, r[0], r[1], r[5],
                    safeParseLong(r[6]) / 1_000_000.0));
            }
            writer.write("\n");
            
            // Piores casos
            resultados.sort((a, b) -> Integer.compare(safeParseInt(b[5]), safeParseInt(a[5])));
            
            writer.write("PIORES CASOS (evitar):\n");
            for (int i = 0; i < Math.min(3, resultados.size()); i++) {
                String[] r = resultados.get(i);
                writer.write(String.format("%d. %s + %s: %s colisões (%.2f ms)\n", 
                    i + 1, r[0], r[1], r[5],
                    safeParseLong(r[6]) / 1_000_000.0));
            }
            writer.write("\n");
            
            // Conclusões
            writer.write("CONCLUSÕES\n");
            writer.write("--------------------------\n");
            writer.write(" RECOMENDADO:\n");
            writer.write("   • Encadeamento com XOR Shift\n");
            writer.write("   • Encadeamento com Multiplicação\n");
            writer.write("   • Rehashing Quadrático com XOR Shift (apenas carga <= 50%)\n\n");
            
            writer.write(" EVITAR:\n");
            writer.write("   • Dobramento (muito lento)\n");
            writer.write("   • Rehashing Linear com Dobramento\n");
            writer.write("   • Rehashing com carga > 50%\n\n");
            
            writer.write("  Melhor:\n");
            writer.write("   ENCADEAMENTO + XOR SHIFT\n");
            writer.write("   - Mais rápida\n");
            writer.write("   - Colisões moderadas\n");
            writer.write("   - Suporta qualquer carga\n");
            writer.write("   - Performance consistente\n");
        }
        
        System.out.println("Relatório completo gerado em: resultados/relatorio_completo.txt");
    }
    
    // Métodos auxiliares para parsing seguro
    private static int safeParseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private static long safeParseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private static double safeParseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}