public class TabelaHashEncadeamento implements TabelaHash {
    private No[] tabela;
    private int colisoes;
    private int elementosInseridos;
    private String funcaoHash;
    
    private static class No {
        Registro registro;
        No proximo;
        
        No(Registro registro) {
            this.registro = registro;
            this.proximo = null;
        }
    }
    
    public TabelaHashEncadeamento(int tamanho, String funcaoHash) {
        this.tabela = new No[tamanho];
        this.colisoes = 0;
        this.elementosInseridos = 0;
        this.funcaoHash = funcaoHash;
    }
    
    @Override
    public boolean inserir(Registro registro) {
        if (registro == null) {
            throw new IllegalArgumentException("Registro não pode ser nulo");
        }
        
        int indice = calcularIndice(registro.getCodigoInt());
        No novoNo = new No(registro);
        
        if (tabela[indice] == null) {
            tabela[indice] = novoNo;
        } else {
            colisoes++;
            No atual = tabela[indice];
            int colisoesNaLista = 0;
            
            while (atual.proximo != null) {
                atual = atual.proximo;
                colisoesNaLista++;
            }
            atual.proximo = novoNo;
            colisoes += colisoesNaLista;
        }
        
        elementosInseridos++;
        return true;
    }
    
    @Override
    public Registro buscar(String codigo) {
        if (codigo == null || codigo.length() != 9) {
            return null;
        }
        
        int codigoInt = Integer.parseInt(codigo);
        int indice = calcularIndice(codigoInt);
        
        No atual = tabela[indice];
        while (atual != null) {
            if (atual.registro.getCodigo().equals(codigo)) {
                return atual.registro;
            }
            atual = atual.proximo;
        }
        
        return null;
    }
    
    private int calcularIndice(int codigo) {
        switch (funcaoHash) {
            case "multiplicacao":
                return hashMultiplicacao(codigo, tabela.length);
            case "dobramento":
                return hashDobramento(codigo, tabela.length);
            case "xorshift":
                return hashXORShift(codigo, tabela.length);
            default:
                return hashMultiplicacao(codigo, tabela.length);
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
        Metricas metricas = new Metricas("Encadeamento", funcaoHash, tabela.length);
        metricas.setColisoes(colisoes);
        metricas.setElementosInseridos(elementosInseridos);
        
        calcularMaioresListas(metricas);
        calcularGaps(metricas);
        
        return metricas;
    }
    
    private void calcularMaioresListas(Metricas metricas) {
        int primeira = 0, segunda = 0, terceira = 0;
        
        for (No no : tabela) {
            int tamanhoLista = 0;
            No atual = no;
            
            while (atual != null) {
                tamanhoLista++;
                atual = atual.proximo;
            }
            
            if (tamanhoLista > primeira) {
                terceira = segunda;
                segunda = primeira;
                primeira = tamanhoLista;
            } else if (tamanhoLista > segunda) {
                terceira = segunda;
                segunda = tamanhoLista;
            } else if (tamanhoLista > terceira) {
                terceira = tamanhoLista;
            }
        }
        
        metricas.setMaiorLista(primeira);
        metricas.setSegundaMaiorLista(segunda);
        metricas.setTerceiraMaiorLista(terceira);
    }
    
    private void calcularGaps(Metricas metricas) {
        int[] gaps = new int[tabela.length];
        int gapCount = 0;
        int gapAtual = 0;
        boolean encontrouPrimeiro = false;
        
        for (No no : tabela) {
            if (no != null) {
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