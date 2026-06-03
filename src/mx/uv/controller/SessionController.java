package mx.uv.controller;

import mx.uv.dao.impl.EmployeeDaoImpl;
import mx.uv.model.Employee;

import java.sql.SQLException;

public class SessionController {
    private static SessionController instance;
    private Employee empleadoActual;

    private SessionController() {}

    public static SessionController getInstance() {
        if (instance == null) instance = new SessionController();
        return instance;
    }

    public Employee authenticate(String username, String contrasenia) {
        try {
            empleadoActual = new EmployeeDaoImpl().authenticate(username, contrasenia);
            return empleadoActual;
        } catch (SQLException e) {
            return null;
        }
    }

    public Employee getCurrentEmployee() { return empleadoActual; }

    public void logout() { empleadoActual = null; }
}
