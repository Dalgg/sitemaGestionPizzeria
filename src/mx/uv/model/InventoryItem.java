package mx.uv.model;

import java.sql.Date;

public class InventoryItem {
    private Inventory inventario;
    private Product producto;
    private Date fecha;

    public InventoryItem() {
    }

    public InventoryItem(Inventory inventario, Product producto, Date fecha) {
        this.inventario = inventario;
        this.producto = producto;
        this.fecha = fecha;
    }

    public Inventory getInventory() {
        return inventario;
    }

    public void setInventory(Inventory inventario) {
        this.inventario = inventario;
    }

    public Product getProduct() {
        return producto;
    }

    public void setProduct(Product producto) {
        this.producto = producto;
    }

    public Date getDate() {
        return fecha;
    }

    public void setDate(Date fecha) {
        this.fecha = fecha;
    }
}
