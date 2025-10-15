import java.io.*;

public class ExecutorTestes {
    private static final int[] TAMANHOS_TABELA = {1000, 10000, 100000};
    private static final int[] TAMANHOS_DADOS = {100000, 1000000, 10000000};
    private static final String[] FUNCOES_HASH = {"multiplicacao", "dobramento", "xorshift"};
    private static final String[] TIPOS_REHASH = {"linear", "quadratico", "duplo"};
    
    private ResultadoTeste[] resultados;
    private int totalResultados;
    
    public static void main(String[] args) {
        ExecutorTestes executor = new ExecutorTestes();
        
        try {
            File pastaDados = new File("dados");
            if (!pastaDados.exists() || pastaDados.listFiles().length == 0) {
                pastaDados.mkdirs();
                GeradorDados.gerarTodosConjuntos();
            }
            
            executor.executarTodosTestes();
            executor.exportarResultadosCSV();
            executor.gerarRelatorio();
            
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
    
    public ExecutorTestes() {
        this.resultados = new ResultadoTeste[100];
        this.totalResultados = 0;
    }
    
    public void executarTodosTestes() throws IOException {
        for (int tamanhoTabela : TAMANHOS_TABELA) {
            for (int tamanhoDados : TAMANHOS_DADOS) {
                if (tamanhoDados <= tamanhoTabela * 0.7) {
                    Registro[] dados = carregarDados(tamanhoDados);
                    
                    if (dados == null || dados.length == 0) {
                        continue;
                    }
                    
                    for (String funcaoHash : FUNCOES_HASH) {
                        testarEncadeamento(tamanhoTabela, dados, funcaoHash);
                    }
                    
                    if (tamanhoDados <= tamanhoTabela * 0.6) {
                        for (String funcaoHash : FUNCOES_HASH) {
                            for (String tipoRehash : TIPOS_REHASH) {
                                testarRehashing(tamanhoTabela, dados, funcaoHash, tipoRehash);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private Registro[] carregarDados(int tamanho) throws IOException {
        String arquivo = "dados/conjunto_" + tamanho + ".txt";
        return GeradorDados.carregarDados(arquivo);
    }
    
    private void testarEncadeamento(int tamanhoTabela, Registro[] dados, String funcaoHash) {
        TabelaHashEncadeamento tabela = new TabelaHashEncadeamento(tamanhoTabela, funcaoHash);
        
        long inicioInsercao = System.nanoTime();
        for (Registro registro : dados) {
            if (registro != null) {
                tabela.inserir(registro);
            }
        }
        long fimInsercao = System.nanoTime();
        
        long inicioBusca = System.nanoTime();
        for (Registro registro : dados) {
            if (registro != null) {
                tabela.buscar(registro.getCodigo());
            }
        }
        long fimBusca = System.nanoTime();
        
        Metricas metricas = tabela.calcularMetricas();
        metricas.setTempoInsercao(fimInsercao - inicioInsercao);
        metricas.setTempoBusca(fimBusca - inicioBusca);
        
        if (totalResultados < resultados.length) {
            resultados[totalResultados++] = new ResultadoTeste(metricas, dados.length);
        }
    }
    
    private void testarRehashing(int tamanhoTabela, Registro[] dados, String funcaoHash, String tipoRehash) {
        TabelaHashRehashing tabela = new TabelaHashRehashing(tamanhoTabela, funcaoHash, tipoRehash);
        
        long inicioInsercao = System.nanoTime();
        int inseridos = 0;
        for (Registro registro : dados) {
            if (registro != null && tabela.inserir(registro)) {
                inseridos++;
            }
        }
        long fimInsercao = System.nanoTime();
        
        if (inseridos == 0) {
            return;
        }
        
        long inicioBusca = System.nanoTime();
        for (Registro registro : dados) {
            if (registro != null) {
                tabela.buscar(registro.getCodigo());
            }
        }
        long fimBusca = System.nanoTime();
        
        Metricas metricas = tabela.calcularMetricas();
        metricas.setTempoInsercao(fimInsercao - inicioInsercao);
        metricas.setTempoBusca(fimBusca - inicioBusca);
        
        if (totalResultados < resultados.length) {
            resultados[totalResultados++] = new ResultadoTeste(metricas, inseridos);
        }
    }
    
    private void exportarResultadosCSV() throws IOException {
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
    }
    
    private void gerarRelatorio() {
        System.out.println("Total de resultados: " + totalResultados);
        
        for (int i = 0; i < totalResultados; i++) {
            System.out.println(resultados[i].getMetricas());
        }
    }
}
