package mx.uv.model;

public enum OrderStatus {
    EN_PROCESO("en_proceso"),
    ENTREGADO("entregado"),
    CANCELADO("cancelado");

    private final String valor;

    OrderStatus(String valor) { this.valor = valor; }

    public String getValue() { return valor; }

    public static OrderStatus fromValue(String valor) {
        for (OrderStatus e : values()) {
            if (e.valor.equalsIgnoreCase(valor)) return e;
        }
        return EN_PROCESO;
    }

    @Override
    public String toString() {
        return switch (this) {
            case EN_PROCESO -> "En Proceso";
            case ENTREGADO  -> "Entregado";
            case CANCELADO  -> "Cancelado";
        };
    }
}
