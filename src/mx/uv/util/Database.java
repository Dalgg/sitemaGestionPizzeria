package mx.uv.util;

import mx.uv.model.Role;
import mx.uv.controller.SessionController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/pizzeria?useSSL=false&serverTimezone=America/Mexico_City&allowPublicKeyRetrieval=true";
    private static final String ADMIN_USER = "app_admin";
    private static final String ADMIN_PASS = "Admin@Pizzeria2024";
    private static final String CASHIER_USER = "app_cajero";
    private static final String CASHIER_PASS = "Cajero@Pizzeria2024";

    private Database() {}

    public static Connection getConnection() throws SQLException {
        Role role = null;
        if (SessionController.getInstance().getCurrentEmployee() != null) {
            role = SessionController.getInstance().getCurrentEmployee().getRole();
        }
        return getConnection(role);
    }

    public static Connection getConnection(Role role) throws SQLException {
        String user;
        String pass;
        if (role == Role.CAJERO) {
            user = CASHIER_USER;
            pass = CASHIER_PASS;
        } else {
            user = ADMIN_USER;
            pass = ADMIN_PASS;
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, user, pass);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado", e);
        }
    }
}