public interface TabelaHash {
    boolean inserir(Registro registro);
    Registro buscar(String codigo);
    int getColisoes();
    int getTamanho();
    int getElementosInseridos();
    Metricas calcularMetricas();
}
