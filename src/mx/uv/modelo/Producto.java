package mx.uv.modelo;

public abstract class Producto {
    private int id;
    private String nombre;
    private int cantidad;
    private boolean estadoDisponible;
    private String descripcion;
    private String codigo;

    public Producto() {
    }

    public Producto(int id, String nombre, int cantidad, boolean estadoDisponible, String descripcion, String codigo) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.estadoDisponible = estadoDisponible;
        this.descripcion = descripcion;
        this.codigo = codigo;
    }
}
