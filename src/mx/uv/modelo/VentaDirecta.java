package mx.uv.modelo;

import java.awt.*;

public class VentaDirecta extends Producto {
    private float precio;
    private String restricciones;
    private Image imagen;

    public VentaDirecta() {}

    public VentaDirecta(int id, String nombre, int cantidad, boolean estadoDisponible, String descripcion, String codigo, float precio, String restricciones, Image imagen) {
        super(id, nombre, cantidad, estadoDisponible, descripcion, codigo);
        this.precio = precio;
        this.restricciones = restricciones;
        this.imagen = imagen;
    }

    public VentaDirecta(int id, String nombre, int cantidad, boolean estadoDisponible, String descripcion, String codigo, Image imagen, float precio) {
        super(id, nombre, cantidad, estadoDisponible, descripcion, codigo);
        this.imagen = imagen;
        this.precio = precio;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getRestricciones() {
        return restricciones;
    }

    public void setRestricciones(String restricciones) {
        this.restricciones = restricciones;
    }

    public Image getImagen() {
        return imagen;
    }

    public void setImagen(Image imagen) {
        this.imagen = imagen;
    }
}
