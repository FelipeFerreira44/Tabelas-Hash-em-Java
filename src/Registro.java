public class Registro {
    private String codigo;
    
    public Registro(String codigo) {
        if (codigo == null || codigo.length() != 9) {
            throw new IllegalArgumentException("Código deve ter exatamente 9 dígitos");
        }
        this.codigo = codigo;
    }
    
    public String getCodigo() { 
        return codigo; 
    }
    
    public int getCodigoInt() {
        return Integer.parseInt(codigo);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Registro registro = (Registro) obj;
        return codigo.equals(registro.codigo);
    }
    
    @Override
    public int hashCode() {
        return codigo.hashCode();
    }
    
    @Override
    public String toString() {
        return codigo;
    }
}
