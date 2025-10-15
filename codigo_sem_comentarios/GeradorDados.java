import java.io.*;
import java.util.Random;

public class GeradorDados {
    private static final long SEED = 12345L;
    
    public static Registro[] gerarDados(int quantidade, long seed) {
        Random random = new Random(seed);
        Registro[] dados = new Registro[quantidade];
        
        for (int i = 0; i < quantidade; i++) {
            int numero = random.nextInt(1000000000);
            String codigo = String.format("%09d", numero);
            dados[i] = new Registro(codigo);
        }
        
        return dados;
    }
    
    public static void salvarDados(Registro[] dados, String arquivo) throws IOException {
        File pasta = new File("dados");
        if (!pasta.exists()) {
            pasta.mkdirs();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            for (int i = 0; i < dados.length; i++) {
                writer.write(dados[i].getCodigo());
                writer.newLine();
                
                if (i % 10000 == 0) {
                    writer.flush();
                }
            }
        }
    }
    
    public static Registro[] carregarDados(String arquivo) throws IOException {
        int linhas = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            while (reader.readLine() != null) {
                linhas++;
            }
        }
        
        Registro[] dados = new Registro[linhas];
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            int index = 0;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().length() == 9) {
                    dados[index++] = new Registro(linha.trim());
                }
            }
        }
        
        return dados;
    }
    
    public static void gerarTodosConjuntos() throws IOException {
        int[] tamanhos = {100000, 1000000, 10000000};
        
        for (int tamanho : tamanhos) {
            String arquivo = "dados/conjunto_" + tamanho + ".txt";
            if (!new File(arquivo).exists()) {
                Registro[] dados = gerarDados(tamanho, SEED);
                salvarDados(dados, arquivo);
            }
        }
    }
}
