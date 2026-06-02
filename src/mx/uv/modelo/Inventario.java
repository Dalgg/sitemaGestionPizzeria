package mx.uv.modelo;

public class Inventario {
    private int id;
    private int cantidad;
    private String tipo;

    public Inventario() {
    }

    public Inventario(int id, int cantidad, String tipo) {
        this.id = id;
        this.cantidad = cantidad;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
