package mx.uv.model;

public class Supply extends Product {
    private int stockMinimo;
    private String unidadMedida;
    public Supply() {
    }

    public Supply(int id, String nombre, int cantidad, boolean estadoDisponible, String descripcion, String codigo, int stockMinimo, String unidadMedida) {
        super(id, nombre, cantidad, estadoDisponible, descripcion, codigo);
        this.stockMinimo = stockMinimo;
        this.unidadMedida = unidadMedida;
    }

    public int getMinStock() {
        return stockMinimo;
    }

    public void setMinStock(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getMeasureUnit() {
        return unidadMedida;
    }

    public void setMeasureUnit(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
}
