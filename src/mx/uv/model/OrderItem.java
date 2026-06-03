package mx.uv.model;

public class OrderItem {
    private Order pedido;
    private Product producto;
    private int cantidad;
    private float precio;

    public OrderItem() {
    }

    public OrderItem(Order pedido, Product producto, int cantidad, float precio) {
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public Order getOrder() {
        return pedido;
    }

    public void setOrder(Order pedido) {
        this.pedido = pedido;
    }

    public Product getProduct() {
        return producto;
    }

    public void setProduct(Product producto) {
        this.producto = producto;
    }

    public int getQuantity() {
        return cantidad;
    }

    public void setQuantity(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrice() {
        return precio;
    }

    public void setPrice(float precio) {
        this.precio = precio;
    }
}
