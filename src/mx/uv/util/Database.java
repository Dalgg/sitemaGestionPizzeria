package mx.uv.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL    = "jdbc:mysql://localhost:3306/pizzeria?useSSL=false&serverTimezone=America/Mexico_City&allowPublicKeyRetrieval=true";
    private static final String USER   = "app_admin";
    private static final String PASS   = "Admin@Pizzeria2024";

    private static Connection conexion;

    private Database() {}

    public static Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASS);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL no encontrado", e);
            }
        }
        return conexion;
    }

    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException ignored) {}
    }
}
