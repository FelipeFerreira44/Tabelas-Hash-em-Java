import java.io.*;

public class ExecutorTestes {
    // Tamanhos conforme o enunciado
    private static final int[] TAMANHOS_TABELA = {1000, 10000, 100000};
    private static final int[] TAMANHOS_DADOS = {100000, 1000000, 10000000};
    
    private static final String[] FUNCOES_HASH = {"multiplicacao", "dobramento", "xorshift"};
    private static final String[] TIPOS_REHASH = {"linear", "quadratico", "duplo"};
    
    private ResultadoTeste[] resultados;
    private int totalResultados;
    
    public static void main(String[] args) {
        System.out.println(" EXECUTOR DE TESTES - CONFORMIDADE COM ENUNCIADO");
        System.out.println("==================================================");
        
        ExecutorTestes executor = new ExecutorTestes();
        
        try {
            // Primeiro garantir que temos todos os conjuntos corretos
            System.out.println("🔍 Verificando conjuntos de dados...");
            GeradorDados.gerarTodosConjuntosCorretos();
            
            // Executar testes
            executor.executarTestesConformeEnunciado();
            executor.exportarResultadosCSV();
            executor.gerarRelatorioDetalhado();
            
        } catch (Exception e) {
            System.err.println(" Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public ExecutorTestes() {
        // Calcular número máximo de testes
        int maxTestes = 0;
        for (int tamanhoTabela : TAMANHOS_TABELA) {
            for (int tamanhoDados : TAMANHOS_DADOS) {
                if (tamanhoDados <= tamanhoTabela * 10) { // Critério do enunciado
                    maxTestes += FUNCOES_HASH.length; // Encadeamento
                    if (tamanhoDados <= tamanhoTabela * 0.8) { // Rehashing
                        maxTestes += FUNCOES_HASH.length * TIPOS_REHASH.length;
                    }
                }
            }
        }
        this.resultados = new ResultadoTeste[maxTestes];
        this.totalResultados = 0;
        
        System.out.println(" Configuração:");
        System.out.println("   • Tabelas: " + java.util.Arrays.toString(TAMANHOS_TABELA));
        System.out.println("   • Dados: " + java.util.Arrays.toString(TAMANHOS_DADOS));
        System.out.println("   • Esperados até " + maxTestes + " testes");
    }
    
    public void executarTestesConformeEnunciado() throws IOException {
        System.out.println("\n INICIANDO TESTES CONFORME ENUNCIADO");
        System.out.println("=====================================");
        
        int testesExecutados = 0;
        
        for (int tamanhoTabela : TAMANHOS_TABELA) {
            for (int tamanhoDados : TAMANHOS_DADOS) {
                // Critério: dados não podem ser MUITO maiores que a tabela
                if (tamanhoDados <= tamanhoTabela * 10) {
                    double carga = (tamanhoDados * 100.0) / tamanhoTabela;
                    System.out.printf("\n CENÁRIO: Tabela=%,d | Dados=%,d | Carga=%.1f%%\n", 
                                    tamanhoTabela, tamanhoDados, carga);
                    
                    Registro[] dados = carregarDadosConformeEnunciado(tamanhoDados);
                    
                    if (dados == null || dados.length == 0) {
                        System.out.println("     Dados não disponíveis, pulando...");
                        continue;
                    }
                    
                    // Testar Encadeamento
                    System.out.println("    ENCADEAMENTO:");
                    for (String funcaoHash : FUNCOES_HASH) {
                        testarEncadeamento(tamanhoTabela, dados, funcaoHash);
                        testesExecutados++;
                    }
                    
                    // Testar Rehashing apenas se carga for <= 80%
                    if (tamanhoDados <= tamanhoTabela * 0.8) {
                        System.out.println("    REHASHING:");
                        for (String funcaoHash : FUNCOES_HASH) {
                            for (String tipoRehash : TIPOS_REHASH) {
                                testarRehashing(tamanhoTabela, dados, funcaoHash, tipoRehash);
                                testesExecutados++;
                            }
                        }
                    } else {
                        System.out.println("    Rehashing: Não aplicável (carga > 80%)");
                    }
                } else {
                    System.out.printf("     Pulando - Carga muito alta (%,d dados em %,d tabela)\n", 
                                    tamanhoDados, tamanhoTabela);
                }
            }
        }
        
        System.out.println("\n TESTES CONCLUÍDOS");
        System.out.println("====================");
        System.out.println(" Total de combinações: " + testesExecutados);
        System.out.println(" Resultados coletados: " + totalResultados);
    }
    
    private Registro[] carregarDadosConformeEnunciado(int tamanho) {
        String arquivo = "dados/conjunto_" + tamanho + ".txt";
        try {
            return GeradorDados.carregarDados(arquivo);
        } catch (IOException e) {
            System.out.println("    Erro ao carregar " + arquivo + ": " + e.getMessage());
            return null;
        }
    }
    
    // Os métodos testarEncadeamento, testarRehashing permanecem iguais
    private void testarEncadeamento(int tamanhoTabela, Registro[] dados, String funcaoHash) {
        System.out.printf("     • %s...", funcaoHash);
        
        try {
            TabelaHashEncadeamento tabela = new TabelaHashEncadeamento(tamanhoTabela, funcaoHash);
            
            long inicio = System.nanoTime();
            for (Registro registro : dados) {
                if (registro != null) tabela.inserir(registro);
            }
            long fim = System.nanoTime();
            
            long inicioBusca = System.nanoTime();
            for (Registro registro : dados) {
                if (registro != null) tabela.buscar(registro.getCodigo());
            }
            long fimBusca = System.nanoTime();
            
            Metricas metricas = tabela.calcularMetricas();
            metricas.setTempoInsercao(fim - inicio);
            metricas.setTempoBusca(fimBusca - inicioBusca);
            
            if (totalResultados < resultados.length) {
                resultados[totalResultados++] = new ResultadoTeste(metricas, dados.length);
            }
            
            System.out.printf("  %d colisões, %.2f ms\n", 
                            metricas.getColisoes(), 
                            metricas.getTempoInsercao() / 1_000_000.0);
            
        } catch (Exception e) {
            System.out.printf("  Erro\n");
        }
    }
    
    private void testarRehashing(int tamanhoTabela, Registro[] dados, String funcaoHash, String tipoRehash) {
        System.out.printf("     • %s + %s...", funcaoHash, tipoRehash);
        
        try {
            TabelaHashRehashing tabela = new TabelaHashRehashing(tamanhoTabela, funcaoHash, tipoRehash);
            
            long inicio = System.nanoTime();
            int inseridos = 0;
            for (Registro registro : dados) {
                if (registro != null && tabela.inserir(registro)) {
                    inseridos++;
                }
            }
            long fim = System.nanoTime();
            
            if (inseridos == 0) {
                System.out.printf("  Falhou\n");
                return;
            }
            
            long inicioBusca = System.nanoTime();
            for (Registro registro : dados) {
                if (registro != null) tabela.buscar(registro.getCodigo());
            }
            long fimBusca = System.nanoTime();
            
            Metricas metricas = tabela.calcularMetricas();
            metricas.setTempoInsercao(fim - inicio);
            metricas.setTempoBusca(fimBusca - inicioBusca);
            
            if (totalResultados < resultados.length) {
                resultados[totalResultados++] = new ResultadoTeste(metricas, inseridos);
            }
            
            double percentual = (inseridos * 100.0) / dados.length;
            System.out.printf("  %d colisões, %.1f%%, %.2f ms\n", 
                            metricas.getColisoes(), percentual,
                            metricas.getTempoInsercao() / 1_000_000.0);
            
        } catch (Exception e) {
            System.out.printf("  Erro\n");
        }
    }
    
    private void exportarResultadosCSV() throws IOException {
        System.out.println("\n Exportando resultados para CSV...");
        
        File pastaResultados = new File("resultados");
        pastaResultados.mkdirs();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultados/resultados.csv"))) {
            writer.write("TipoTabela,FuncaoHash,TamanhoTabela,ElementosDados,FatorCarga,Colisoes,");
            writer.write("TempoInsercaoNs,TempoBuscaNs,MaiorLista,SegundaMaiorLista,TerceiraMaiorLista,");
            writer.write("MenorGap,MaiorGap,MediaGaps\n");
            
            for (int i = 0; i < totalResultados; i++) {
                ResultadoTeste resultado = resultados[i];
                Metricas m = resultado.getMetricas();
                writer.write(String.format("%s,%s,%d,%d,%.4f,%d,%d,%d,%d,%d,%d,%d,%d,%.2f\n",
                    m.getTipoTabela(), m.getFuncaoHash(), m.getTamanhoTabela(),
                    resultado.getTamanhoDados(), m.getFatorCarga(), m.getColisoes(),
                    m.getTempoInsercao(), m.getTempoBusca(), m.getMaiorLista(),
                    m.getSegundaMaiorLista(), m.getTerceiraMaiorLista(),
                    m.getMenorGap(), m.getMaiorGap(), m.getMediaGaps()
                ));
            }
        }
        
        System.out.println(" CSV salvo: resultados/resultados.csv");
    }
    
    private void gerarRelatorioDetalhado() {
        System.out.println("\n RELATÓRIO DETALHADO");
        System.out.println("=====================");
        
        if (totalResultados == 0) {
            System.out.println("Nenhum resultado para mostrar.");
            return;
        }
        
        // Análise por tipo de tabela
        int encadeamentos = 0;
        int rehashings = 0;
        long tempoEncadeamento = 0;
        long tempoRehashing = 0;
        long colisoesEncadeamento = 0;
        long colisoesRehashing = 0;
        
        for (int i = 0; i < totalResultados; i++) {
            Metricas m = resultados[i].getMetricas();
            if (m.getTipoTabela().equals("Encadeamento")) {
                encadeamentos++;
                tempoEncadeamento += m.getTempoInsercao();
                colisoesEncadeamento += m.getColisoes();
            } else {
                rehashings++;
                tempoRehashing += m.getTempoInsercao();
                colisoesRehashing += m.getColisoes();
            }
        }
        
        System.out.println(" RESULTADOS GERAIS:");
        System.out.printf("    Encadeamento: %d testes\n", encadeamentos);
        System.out.printf("   Rehashing: %d testes\n", rehashings);
        
        if (encadeamentos > 0) {
            System.out.printf("        Tempo médio: %.2f ms\n", (tempoEncadeamento / encadeamentos) / 1_000_000.0);
            System.out.printf("       Colisões médias: %.0f\n", (double)colisoesEncadeamento / encadeamentos);
        }
        
        if (rehashings > 0) {
            System.out.printf("        Tempo médio: %.2f ms\n", (tempoRehashing / rehashings) / 1_000_000.0);
            System.out.printf("       Colisões médias: %.0f\n", (double)colisoesRehashing / rehashings);
        }
        
        System.out.println("\n CONFORMIDADE COM ENUNCIADO:");
        System.out.println("    Tabelas: 1.000, 10.000, 100.000");
        System.out.println("    Dados: 100.000, 1.000.000, 10.000.000");
        System.out.println("   Funções: Multiplicação, Dobramento, XOR Shift");
        System.out.println("    Estratégias: Encadeamento, Rehashing (Linear, Quadrático, Duplo)");
    }
}
