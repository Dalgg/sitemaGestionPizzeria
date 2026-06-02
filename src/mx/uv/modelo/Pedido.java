package mx.uv.modelo;

import java.sql.Date;

public class Pedido {
    private int id;
    private Date fechaPedido;
    private float totalAPagar;
    private Empleado empleado;
    private Usuario cliente ;

    public Pedido() {
    }

    public Pedido(int id, Date fechaPedido, float totalAPagar, Empleado empleado, Usuario cliente) {
        this.id = id;
        this.fechaPedido = fechaPedido;
        this.totalAPagar = totalAPagar;
        this.empleado = empleado;
        this.cliente = cliente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public float getTotalAPagar() {
        return totalAPagar;
    }

    public void setTotalAPagar(float totalAPagar) {
        this.totalAPagar = totalAPagar;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }
}
