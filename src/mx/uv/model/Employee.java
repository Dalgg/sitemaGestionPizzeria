package mx.uv.model;

public class Employee extends User {
    private String nombreUsuario;
    private String contrasenia;
    private Role rol;

    public Employee(String nombreUsuario, String contrasenia, Role rol) {
        this.nombreUsuario = nombreUsuario;
        this.contrasenia = contrasenia;
        this.rol = rol;
    }

    public Employee(int id, String nombres, String apellidos, String telefono, String email, String ciudad, String calle, int codigoPostal, int numeroCasa, boolean estadoActivo, String nombreUsuario, String contrasenia, Role rol) {
        super(id, nombres, apellidos, telefono, email, ciudad, calle, codigoPostal, numeroCasa, estadoActivo);
        this.nombreUsuario = nombreUsuario;
        this.contrasenia = contrasenia;
        this.rol = rol;
    }

    public String getUsername() {
        return nombreUsuario;
    }

    public void setUsername(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPassword() {
        return contrasenia;
    }

    public void setPassword(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public Role getRole() {
        return rol;
    }

    public void setRole(Role rol) {
        this.rol = rol;
    }
}
