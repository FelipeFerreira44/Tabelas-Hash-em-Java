public class ResultadoTeste {
    private Metricas metricas;
    private int tamanhoDados;
    
    public ResultadoTeste(Metricas metricas, int tamanhoDados) {
        this.metricas = metricas;
        this.tamanhoDados = tamanhoDados;
    }
    
    public Metricas getMetricas() { return metricas; }
    public int getTamanhoDados() { return tamanhoDados; }
}