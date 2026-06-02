package mx.uv.modelo;

public class Insumos extends Producto {
    private int stockMinimo;
    private String unidadMedida;
    public Insumos() {
    }

    public Insumos(int id, String nombre, int cantidad, boolean estadoDisponible, String descripcion, String codigo, int stockMinimo, String unidadMedida) {
        super(id, nombre, cantidad, estadoDisponible, descripcion, codigo);
        this.stockMinimo = stockMinimo;
        this.unidadMedida = unidadMedida;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
}
