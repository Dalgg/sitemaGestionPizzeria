package mx.uv.modelo;

public enum EstadoPedido {
    ENTREGADO("Entregado"),
    NO_ENTREGADO("No Entregado"),
    EN_PROCESO("En Processo");
    private String descripcion;
    private EstadoPedido(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getDescripcion() {
        return descripcion;
    }
}
