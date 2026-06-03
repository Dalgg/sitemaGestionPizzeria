package mx.uv.model;

public class User {
    private int id;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String email;
    private String ciudad;
    private String calle;
    private int codigoPostal;
    private int numeroCasa;
    private boolean estadoActivo;
    public User() {}

    public User(int id, String nombres, String apellidos, String telefono, String email, String ciudad, String calle, int codigoPostal, int numeroCasa, boolean estadoActivo) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.ciudad = ciudad;
        this.calle = calle;
        this.codigoPostal = codigoPostal;
        this.numeroCasa = numeroCasa;
        this.estadoActivo = estadoActivo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return nombres;
    }

    public void setFirstName(String nombres) {
        this.nombres = nombres;
    }

    public String getLastName() {
        return apellidos;
    }

    public void setLastName(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getPhone() {
        return telefono;
    }

    public void setPhone(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return ciudad;
    }

    public void setCity(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getStreet() {
        return calle;
    }

    public void setStreet(String calle) {
        this.calle = calle;
    }

    public int getZipCode() {
        return codigoPostal;
    }

    public void setZipCode(int codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public int getHouseNumber() {
        return numeroCasa;
    }

    public void setHouseNumber(int numeroCasa) {
        this.numeroCasa = numeroCasa;
    }

    public boolean isActive() {
        return estadoActivo;
    }

    public void setActive(boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }
}
