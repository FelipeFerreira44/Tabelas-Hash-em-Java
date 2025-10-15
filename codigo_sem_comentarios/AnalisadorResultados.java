import java.io.*;
import java.util.*;

public class AnalisadorResultados {
    
    public static void main(String[] args) {
        try {
            analisarResultadosCSV();
        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
    
    public static void analisarResultadosCSV() throws IOException {
        List<String[]> resultados = carregarResultadosCSV();
        
        if (resultados.isEmpty()) {
            System.out.println("Nenhum resultado encontrado");
            return;
        }
        
        analisarDesempenhoGeral(resultados);
        analisarFuncoesHash(resultados);
    }
    
    private static List<String[]> carregarResultadosCSV() throws IOException {
        List<String[]> resultados = new ArrayList<>();
        
        File arquivo = new File("resultados/resultados.csv");
        if (!arquivo.exists()) {
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
        
        return resultados;
    }
    
    private static void analisarDesempenhoGeral(List<String[]> resultados) {
        Map<String, List<String[]>> porTipo = new HashMap<>();
        for (String[] r : resultados) {
            String tipo = r[0];
            porTipo.computeIfAbsent(tipo, k -> new ArrayList<>()).add(r);
        }
        
        for (Map.Entry<String, List<String[]>> entry : porTipo.entrySet()) {
            String tipo = entry.getKey();
            List<String[]> lista = entry.getValue();
            
            double tempoMedio = lista.stream()
                .mapToLong(r -> safeParseLong(r[6]))
                .average().orElse(0) / 1_000_000.0;
                
            double colisoesMedias = lista.stream()
                .mapToInt(r -> safeParseInt(r[5]))
                .average().orElse(0);
            
            System.out.printf("%s: %.2f ms, %.1f colisões\n", tipo, tempoMedio, colisoesMedias);
        }
    }
    
    private static void analisarFuncoesHash(List<String[]> resultados) {
        Map<String, List<String[]>> porFuncao = new HashMap<>();
        for (String[] r : resultados) {
            String funcao = r[1];
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
            
            System.out.printf("%s: %.2f ms, %.1f colisões\n", funcao, tempoMedio, colisoesMedias);
        }
    }
    
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
}
