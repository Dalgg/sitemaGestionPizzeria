package mx.uv.controller;

import mx.uv.dao.UserDao;
import mx.uv.dao.EmployeeDao;
import mx.uv.dao.impl.EmployeeDaoImpl;
import mx.uv.dao.impl.UserDaoImpl;
import mx.uv.model.Employee;
import mx.uv.model.User;

import javafx.scene.control.Alert;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDao usuarioDAO = new UserDaoImpl();
    private final EmployeeDao empleadoDAO = new EmployeeDaoImpl();

    public boolean saveCustomer(User u, boolean esNuevo) {
        try {
            if (esNuevo) usuarioDAO.insert(u);
            else         usuarioDAO.update(u);
            return true;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
            return false;
        }
    }

    public boolean saveEmployee(Employee e, boolean esNuevo) {
        try {
            if (esNuevo) empleadoDAO.insert(e);
            else         empleadoDAO.update(e);
            return true;
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
            return false;
        }
    }

    public boolean deleteUser(int id, int idSesionActiva) {
        if (id == idSesionActiva) {
            new Alert(Alert.AlertType.WARNING, "No puede eliminar su propia cuenta.").showAndWait();
            return false;
        }
        try {
            if (usuarioDAO.customerHasOrders(id)) {
                new Alert(Alert.AlertType.WARNING, "El cliente tiene pedidos registrados y no puede eliminarse.").showAndWait();
                return false;
            }
            usuarioDAO.delete(id);
            return true;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
            return false;
        }
    }

    public List<User> searchCustomers(String filtro) {
        try {
            if (filtro == null || filtro.trim().isEmpty()) return usuarioDAO.listCustomers();
            return usuarioDAO.searchCustomers(filtro.trim());
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
            return List.of();
        }
    }

    public List<Employee> searchEmployees(String filtro) {
        try {
            return empleadoDAO.listEmployees(filtro);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
            return List.of();
        }
    }

    public List<User> listCustomers() {
        try {
            return usuarioDAO.listCustomers();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
            return List.of();
        }
    }
}
