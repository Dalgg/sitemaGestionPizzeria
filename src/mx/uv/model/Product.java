package mx.uv.model;

public abstract class Product {
    private int id;
    private String nombre;
    private int cantidad;
    private boolean estadoDisponible;
    private String descripcion;
    private String codigo;

    public Product() {}

    public Product(int id, String nombre, int cantidad, boolean estadoDisponible, String descripcion, String codigo) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.estadoDisponible = estadoDisponible;
        this.descripcion = descripcion;
        this.codigo = codigo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return nombre; }
    public void setName(String nombre) { this.nombre = nombre; }

    public int getQuantity() { return cantidad; }
    public void setQuantity(int cantidad) { this.cantidad = cantidad; }

    public boolean isAvailable() { return estadoDisponible; }
    public void setAvailable(boolean estadoDisponible) { this.estadoDisponible = estadoDisponible; }

    public String getDescription() { return descripcion; }
    public void setDescription(String descripcion) { this.descripcion = descripcion; }

    public String getCode() { return codigo; }
    public void setCode(String codigo) { this.codigo = codigo; }

    @Override
    public String toString() { return nombre; }
}
