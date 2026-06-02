package mx.uv.modelo;

import java.sql.Date;

public class DetalleInventario {
    private Inventario inventario;
    private Producto producto;
    private Date fecha;

    public DetalleInventario() {
    }

    public DetalleInventario(Inventario inventario, Producto producto, Date fecha) {
        this.inventario = inventario;
        this.producto = producto;
        this.fecha = fecha;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
