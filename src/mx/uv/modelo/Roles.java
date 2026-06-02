package mx.uv.modelo;

public enum Roles {
    ADMINISTRADOR("Administrador"),
    CAJERO("Cajero"),;
    private String descripcion;

    private Roles(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getDescripcion() {
        return descripcion;
    }
}
