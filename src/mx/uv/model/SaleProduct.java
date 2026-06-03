package mx.uv.model;

import java.awt.*;

public class SaleProduct extends Product {
    private float precio;
    private String restricciones;
    private Image imagen;

    public SaleProduct() {}

    public SaleProduct(int id, String nombre, int cantidad, boolean estadoDisponible, String descripcion, String codigo, float precio, String restricciones, Image imagen) {
        super(id, nombre, cantidad, estadoDisponible, descripcion, codigo);
        this.precio = precio;
        this.restricciones = restricciones;
        this.imagen = imagen;
    }

    public SaleProduct(int id, String nombre, int cantidad, boolean estadoDisponible, String descripcion, String codigo, Image imagen, float precio) {
        super(id, nombre, cantidad, estadoDisponible, descripcion, codigo);
        this.imagen = imagen;
        this.precio = precio;
    }

    public float getPrice() {
        return precio;
    }

    public void setPrice(float precio) {
        this.precio = precio;
    }

    public String getRestrictions() {
        return restricciones;
    }

    public void setRestrictions(String restricciones) {
        this.restricciones = restricciones;
    }

    public Image getImage() {
        return imagen;
    }

    public void setImage(Image imagen) {
        this.imagen = imagen;
    }
}
