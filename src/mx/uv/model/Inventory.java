package mx.uv.model;

public class Inventory {
    private int id;
    private int cantidad;
    private String tipo;

    public Inventory() {
    }

    public Inventory(int id, int cantidad, String tipo) {
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

    public int getQuantity() {
        return cantidad;
    }

    public void setQuantity(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getType() {
        return tipo;
    }

    public void setType(String tipo) {
        this.tipo = tipo;
    }
}
