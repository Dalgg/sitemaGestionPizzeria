package mx.uv.model;

public enum Role {
    ADMINISTRADOR("Administrador"),
    CAJERO("Cajero"),;
    private String descripcion;

    private Role(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getDescription() {
        return descripcion;
    }
}
