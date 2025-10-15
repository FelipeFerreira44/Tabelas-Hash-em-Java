public class Metricas {
    private long tempoInsercao;
    private long tempoBusca;
    private int colisoes;
    private int elementosInseridos;
    private int tamanhoTabela;
    private int maiorLista;
    private int segundaMaiorLista;
    private int terceiraMaiorLista;
    private int menorGap;
    private int maiorGap;
    private double mediaGaps;
    private String tipoTabela;
    private String funcaoHash;
    
    public Metricas(String tipoTabela, String funcaoHash, int tamanhoTabela) {
        this.tipoTabela = tipoTabela;
        this.funcaoHash = funcaoHash;
        this.tamanhoTabela = tamanhoTabela;
        this.menorGap = Integer.MAX_VALUE;
        this.maiorGap = Integer.MIN_VALUE;
    }
    
    public long getTempoInsercao() { return tempoInsercao; }
    public void setTempoInsercao(long tempoInsercao) { this.tempoInsercao = tempoInsercao; }
    
    public long getTempoBusca() { return tempoBusca; }
    public void setTempoBusca(long tempoBusca) { this.tempoBusca = tempoBusca; }
    
    public int getColisoes() { return colisoes; }
    public void setColisoes(int colisoes) { this.colisoes = colisoes; }
    
    public int getElementosInseridos() { return elementosInseridos; }
    public void setElementosInseridos(int elementosInseridos) { this.elementosInseridos = elementosInseridos; }
    
    public int getMaiorLista() { return maiorLista; }
    public void setMaiorLista(int maiorLista) { this.maiorLista = maiorLista; }
    
    public int getSegundaMaiorLista() { return segundaMaiorLista; }
    public void setSegundaMaiorLista(int segundaMaiorLista) { this.segundaMaiorLista = segundaMaiorLista; }
    
    public int getTerceiraMaiorLista() { return terceiraMaiorLista; }
    public void setTerceiraMaiorLista(int terceiraMaiorLista) { this.terceiraMaiorLista = terceiraMaiorLista; }
    
    public int getMenorGap() { return menorGap; }
    public void setMenorGap(int menorGap) { this.menorGap = menorGap; }
    
    public int getMaiorGap() { return maiorGap; }
    public void setMaiorGap(int maiorGap) { this.maiorGap = maiorGap; }
    
    public double getMediaGaps() { return mediaGaps; }
    public void setMediaGaps(double mediaGaps) { this.mediaGaps = mediaGaps; }
    
    public String getTipoTabela() { return tipoTabela; }
    public String getFuncaoHash() { return funcaoHash; }
    public int getTamanhoTabela() { return tamanhoTabela; }
    
    public double getFatorCarga() {
        return (double) elementosInseridos / tamanhoTabela;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Tipo: %s, Função: %s, Tamanho: %d, Elementos: %d, FatorCarga: %.2f, Colisões: %d, TempoInsercao: %d ns",
            tipoTabela, funcaoHash, tamanhoTabela, elementosInseridos, getFatorCarga(), colisoes, tempoInsercao
        );
    }
}