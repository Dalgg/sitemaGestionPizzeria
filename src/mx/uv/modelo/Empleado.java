package mx.uv.modelo;

public class Empleado extends Usuario {
    private String nombreUsuario;
    private String contrasenia;
    private Roles rol;

    public Empleado(String nombreUsuario, String contrasenia, Roles rol) {
        this.nombreUsuario = nombreUsuario;
        this.contrasenia = contrasenia;
        this.rol = rol;
    }

    public Empleado(int id, String nombres, String apellidos, String telefono, String email, String ciudad, String calle, int codigoPostal, int numeroCasa, boolean estadoActivo, String nombreUsuario, String contrasenia, Roles rol) {
        super(id, nombres, apellidos, telefono, email, ciudad, calle, codigoPostal, numeroCasa, estadoActivo);
        this.nombreUsuario = nombreUsuario;
        this.contrasenia = contrasenia;
        this.rol = rol;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public Roles getRol() {
        return rol;
    }

    public void setRol(Roles rol) {
        this.rol = rol;
    }
}
