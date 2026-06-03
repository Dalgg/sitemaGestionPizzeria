package mx.uv.model;

import java.sql.Timestamp;

public class OrderStatusLog {
    private int id;
    private Timestamp fechaHora;
    private Order pedido;
    private OrderStatus estadoPedido;
    private Employee empleado;

    public OrderStatusLog() {}

    public OrderStatusLog(int id, Timestamp fechaHora, Order pedido, OrderStatus estadoPedido, Employee empleado) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.pedido = pedido;
        this.estadoPedido = estadoPedido;
        this.empleado = empleado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Timestamp getDateTime() { return fechaHora; }
    public void setDateTime(Timestamp fechaHora) { this.fechaHora = fechaHora; }
    public Order getOrder() { return pedido; }
    public void setOrder(Order pedido) { this.pedido = pedido; }
    public OrderStatus getStatus() { return estadoPedido; }
    public void setStatus(OrderStatus estadoPedido) { this.estadoPedido = estadoPedido; }
    public Employee getEmployee() { return empleado; }
    public void setEmployee(Employee empleado) { this.empleado = empleado; }
}
