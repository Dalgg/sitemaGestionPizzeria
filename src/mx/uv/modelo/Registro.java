package mx.uv.modelo;

import java.time.LocalDateTime;

public class Registro {
    private int id;
    private LocalDateTime fechaHora;
    private Pedido pedido;
    private EstadoPedido estadoPedido;

    public Registro() {
    }

    public Registro(int id, LocalDateTime fechaHora, Pedido pedido, EstadoPedido estadoPedido) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.pedido = pedido;
        this.estadoPedido = estadoPedido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public EstadoPedido getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(EstadoPedido estadoPedido) {
        this.estadoPedido = estadoPedido;
    }
}
