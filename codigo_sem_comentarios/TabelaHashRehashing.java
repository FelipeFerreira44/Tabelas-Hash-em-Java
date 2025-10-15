public class TabelaHashRehashing implements TabelaHash {
    private Registro[] tabela;
    private int colisoes;
    private int elementosInseridos;
    private String funcaoHash;
    private String tipoRehash;
    private static final int MAX_TENTATIVAS = 1000;
    
    public TabelaHashRehashing(int tamanho, String funcaoHash, String tipoRehash) {
        this.tabela = new Registro[tamanho];
        this.colisoes = 0;
        this.elementosInseridos = 0;
        this.funcaoHash = funcaoHash;
        this.tipoRehash = tipoRehash;
    }
    
    @Override
    public boolean inserir(Registro registro) {
        if (registro == null) {
            throw new IllegalArgumentException("Registro não pode ser nulo");
        }
        
        if (elementosInseridos >= tabela.length * 0.75) {
            return false;
        }
        
        int tentativa = 0;
        int indice;
        
        do {
            indice = calcularIndice(registro.getCodigoInt(), tentativa);
            
            if (tabela[indice] == null) {
                tabela[indice] = registro;
                elementosInseridos++;
                return true;
            } else if (tabela[indice].getCodigo().equals(registro.getCodigo())) {
                return false;
            } else {
                colisoes++;
                tentativa++;
            }
            
            if (tentativa >= Math.min(MAX_TENTATIVAS, tabela.length)) {
                return false;
            }
            
        } while (true);
    }
    
    @Override
    public Registro buscar(String codigo) {
        if (codigo == null || codigo.length() != 9) {
            return null;
        }
        
        int codigoInt = Integer.parseInt(codigo);
        int tentativa = 0;
        int indice;
        
        do {
            indice = calcularIndice(codigoInt, tentativa);
            
            if (tabela[indice] == null) {
                return null;
            } else if (tabela[indice].getCodigo().equals(codigo)) {
                return tabela[indice];
            }
            
            tentativa++;
            if (tentativa >= Math.min(MAX_TENTATIVAS, tabela.length)) {
                return null;
            }
            
        } while (true);
    }
    
    private int calcularIndice(int codigo, int tentativa) {
        int hash = calcularHash(codigo);
        
        switch (tipoRehash) {
            case "linear":
                return rehashLinear(hash, tentativa, tabela.length);
            case "quadratico":
                return rehashQuadratico(hash, tentativa, tabela.length);
            case "duplo":
                return rehashDuplo(codigo, hash, tentativa, tabela.length);
            default:
                return rehashLinear(hash, tentativa, tabela.length);
        }
    }
    
    private int calcularHash(int key) {
        switch (funcaoHash) {
            case "multiplicacao":
                return hashMultiplicacao(key, tabela.length);
            case "dobramento":
                return hashDobramento(key, tabela.length);
            case "xorshift":
                return hashXORShift(key, tabela.length);
            default:
                return hashMultiplicacao(key, tabela.length);
        }
    }
    
    private int hashMultiplicacao(int key, int tableSize) {
        double A = (Math.sqrt(5) - 1) / 2;
        double valor = key * A;
        valor = valor - Math.floor(valor);
        return (int) (tableSize * valor);
    }
    
    private int hashDobramento(int key, int tableSize) {
        String keyStr = String.format("%09d", key);
        int soma = 0;
        
        for (int i = 0; i < keyStr.length(); i += 3) {
            int fim = Math.min(i + 3, keyStr.length());
            String parte = keyStr.substring(i, fim);
            soma += Integer.parseInt(parte);
        }
        
        return Math.abs(soma % tableSize);
    }
    
    private int hashXORShift(int key, int tableSize) {
        key = key ^ (key >>> 16);
        key = key ^ (key << 8);
        key = key ^ (key >>> 4);
        return Math.abs(key % tableSize);
    }
    
    private int rehashLinear(int hash, int tentativa, int tableSize) {
        return (hash + tentativa) % tableSize;
    }
    
    private int rehashQuadratico(int hash, int tentativa, int tableSize) {
        return (hash + tentativa * tentativa) % tableSize;
    }
    
    private int rehashDuplo(int key, int hash1, int tentativa, int tableSize) {
        int hash2 = 1 + (key % (tableSize - 1));
        return (hash1 + tentativa * hash2) % tableSize;
    }
    
    @Override
    public int getColisoes() {
        return colisoes;
    }
    
    @Override
    public int getTamanho() {
        return tabela.length;
    }
    
    @Override
    public int getElementosInseridos() {
        return elementosInseridos;
    }
    
    @Override
    public Metricas calcularMetricas() {
        Metricas metricas = new Metricas("Rehashing-" + tipoRehash, funcaoHash, tabela.length);
        metricas.setColisoes(colisoes);
        metricas.setElementosInseridos(elementosInseridos);
        
        metricas.setMaiorLista(1);
        metricas.setSegundaMaiorLista(1);
        metricas.setTerceiraMaiorLista(1);
        
        calcularGaps(metricas);
        
        return metricas;
    }
    
    private void calcularGaps(Metricas metricas) {
        int[] gaps = new int[tabela.length];
        int gapCount = 0;
        int gapAtual = 0;
        boolean encontrouPrimeiro = false;
        
        for (Registro registro : tabela) {
            if (registro != null) {
                if (encontrouPrimeiro) {
                    gaps[gapCount++] = gapAtual;
                }
                gapAtual = 0;
                encontrouPrimeiro = true;
            } else if (encontrouPrimeiro) {
                gapAtual++;
            }
        }
        
        if (gapCount > 0) {
            int menor = Integer.MAX_VALUE;
            int maior = Integer.MIN_VALUE;
            double soma = 0;
            
            for (int i = 0; i < gapCount; i++) {
                int gap = gaps[i];
                if (gap < menor) menor = gap;
                if (gap > maior) maior = gap;
                soma += gap;
            }
            
            metricas.setMenorGap(menor);
            metricas.setMaiorGap(maior);
            metricas.setMediaGaps(soma / gapCount);
        } else {
            metricas.setMenorGap(0);
            metricas.setMaiorGap(0);
            metricas.setMediaGaps(0);
        }
    }
}
