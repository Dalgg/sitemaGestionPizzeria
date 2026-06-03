package mx.uv.model;

import java.sql.Timestamp;

public class Order {
    private int id;
    private Timestamp fechaPedido;
    private double totalAPagar;
    private Employee empleado;
    private User cliente;
    private OrderStatus estado;

    public Order() {}

    public Order(int id, Timestamp fechaPedido, double totalAPagar, Employee empleado, User cliente, OrderStatus estado) {
        this.id = id;
        this.fechaPedido = fechaPedido;
        this.totalAPagar = totalAPagar;
        this.empleado = empleado;
        this.cliente = cliente;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Timestamp getOrderDate() { return fechaPedido; }
    public void setOrderDate(Timestamp fechaPedido) { this.fechaPedido = fechaPedido; }

    public double getTotal() { return totalAPagar; }
    public void setTotal(double totalAPagar) { this.totalAPagar = totalAPagar; }

    public Employee getEmployee() { return empleado; }
    public void setEmployee(Employee empleado) { this.empleado = empleado; }

    public User getCustomer() { return cliente; }
    public void setCustomer(User cliente) { this.cliente = cliente; }

    public OrderStatus getStatus() { return estado; }
    public void setStatus(OrderStatus estado) { this.estado = estado; }

    @Override
    public String toString() { return "Order #" + id; }
}
