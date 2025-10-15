import java.io.*;
import java.util.Random;

public class GeradorDados {
    private static final long SEED = 12345L;
    
    public static Registro[] gerarDados(int quantidade, long seed) {
        System.out.println(" Gerando " + quantidade + " registros...");
        Random random = new Random(seed);
        Registro[] dados = new Registro[quantidade];
        
        for (int i = 0; i < quantidade; i++) {
            int numero = random.nextInt(1000000000);
            String codigo = String.format("%09d", numero);
            dados[i] = new Registro(codigo);
        }
        
        System.out.println(" Gerados " + dados.length + " registros válidos");
        return dados;
    }
    
    public static void salvarDados(Registro[] dados, String arquivo) throws IOException {
        System.out.println(" Salvando em: " + arquivo);
        
        File pasta = new File("dados");
        if (!pasta.exists()) {
            pasta.mkdirs();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            for (int i = 0; i < dados.length; i++) {
                writer.write(dados[i].getCodigo());
                writer.newLine();
                
                // Mostrar progresso a cada 50.000 registros
                if (i > 0 && i % 50000 == 0) {
                    System.out.println("    Progresso: " + i + "/" + dados.length);
                    writer.flush(); // Forçar escrita para evitar perda de dados
                }
            }
            writer.flush(); // Garantir que tudo foi escrito
        }
        
        System.out.println(" Salvos " + dados.length + " registros em " + arquivo);
    }
    
    public static Registro[] carregarDados(String arquivo) throws IOException {
        System.out.println(" Carregando: " + arquivo);
        
        File arquivoObj = new File(arquivo);
        if (!arquivoObj.exists()) {
            throw new FileNotFoundException("Arquivo não encontrado: " + arquivo);
        }
        
        // Contar linhas primeiro
        int linhas = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            while (reader.readLine() != null) {
                linhas++;
            }
        }
        
        System.out.println("    Encontradas " + linhas + " linhas");
        
        // Carregar dados
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
        
        System.out.println(" Carregados " + dados.length + " registros");
        return dados;
    }
    
    public static void gerarTodosConjuntosCorretos() throws IOException {
        System.out.println(" GERANDO CONJUNTOS COMPLETOS CORRETOS");
        System.out.println("=======================================");
        
        int[] tamanhos = {100000, 1000000, 10000000}; // Conforme o enunciado
        
        for (int tamanho : tamanhos) {
            String arquivo = "dados/conjunto_" + tamanho + ".txt";
            File arquivoObj = new File(arquivo);
            
            if (arquivoObj.exists()) {
                // Verificar se o arquivo está completo
                int linhasExistentes = contarLinhas(arquivo);
                if (linhasExistentes == tamanho) {
                    System.out.println(" " + arquivo + " já existe e está completo (" + linhasExistentes + " linhas)");
                    continue;
                } else {
                    System.out.println("  " + arquivo + " existe mas está incompleto (" + linhasExistentes + "/" + tamanho + " linhas)");
                    System.out.println("    Recriando arquivo...");
                }
            } else {
                System.out.println(" Criando " + arquivo + "...");
            }
            
            // Gerar dados
            Registro[] dados = gerarDados(tamanho, SEED);
            salvarDados(dados, arquivo);
            
            // Verificar se foi salvo corretamente
            int linhasSalvas = contarLinhas(arquivo);
            if (linhasSalvas == tamanho) {
                System.out.println(" " + arquivo + " criado com sucesso! (" + linhasSalvas + " linhas)");
            } else {
                System.out.println(" Problema: " + arquivo + " tem " + linhasSalvas + " linmas, deveria ter " + tamanho);
            }
            
            System.out.println("---");
        }
        
        System.out.println("✨ TODOS OS CONJUNTOS FORAM GERADOS/VERIFICADOS!");
    }
    
    private static int contarLinhas(String arquivo) throws IOException {
        int linhas = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            while (reader.readLine() != null) {
                linhas++;
            }
        }
        return linhas;
    }
}
