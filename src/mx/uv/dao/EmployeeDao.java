package mx.uv.dao;

import mx.uv.model.Employee;
import mx.uv.model.Role;
import mx.uv.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao implements mx.uv.dao.impl.EmployeeDaoImpl {

    @Override
    public void insert(Employee e) throws SQLException {
        Connection con = Database.getConnection();
        con.setAutoCommit(false);
        try {
            String sqlU = "INSERT INTO usuarios(nombres,apellidos,telefono,email,calle,numero_casa,codigo_postal,ciudad,tipo_usuario) VALUES(?,?,?,?,?,?,?,?,'empleado')";
            int idUsuario;
            try (PreparedStatement ps = con.prepareStatement(sqlU, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, e.getFirstName()); ps.setString(2, e.getLastName());
                ps.setString(3, e.getPhone()); ps.setString(4, e.getEmail());
                ps.setString(5, e.getStreet()); ps.setInt(6, e.getHouseNumber());
                ps.setInt(7, e.getZipCode()); ps.setString(8, e.getCity());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next(); idUsuario = rs.getInt(1); e.setId(idUsuario);
                }
            }
            String sqlE = "INSERT INTO empleados(id_usuario,nombre_usuario,contrasenia,rol) VALUES(?,?,SHA2(?,256),?)";
            try (PreparedStatement ps = con.prepareStatement(sqlE)) {
                ps.setInt(1, idUsuario); ps.setString(2, e.getUsername());
                ps.setString(3, e.getPassword());
                ps.setString(4, e.getRole().name().toLowerCase());
                ps.executeUpdate();
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    @Override
    public void update(Employee e) throws SQLException {
        Connection con = Database.getConnection();
        con.setAutoCommit(false);
        try {
            String sqlU = "UPDATE usuarios SET nombres=?,apellidos=?,telefono=?,email=?,calle=?,numero_casa=?,codigo_postal=?,ciudad=? WHERE id=?";
            try (PreparedStatement ps = con.prepareStatement(sqlU)) {
                ps.setString(1, e.getFirstName()); ps.setString(2, e.getLastName());
                ps.setString(3, e.getPhone()); ps.setString(4, e.getEmail());
                ps.setString(5, e.getStreet()); ps.setInt(6, e.getHouseNumber());
                ps.setInt(7, e.getZipCode()); ps.setString(8, e.getCity());
                ps.setInt(9, e.getId());
                ps.executeUpdate();
            }
            String sqlE;
            if (e.getPassword() != null && !e.getPassword().isEmpty()) {
                sqlE = "UPDATE empleados SET nombre_usuario=?,contrasenia=SHA2(?,256),rol=? WHERE id_usuario=?";
                try (PreparedStatement ps = con.prepareStatement(sqlE)) {
                    ps.setString(1, e.getUsername()); ps.setString(2, e.getPassword());
                    ps.setString(3, e.getRole().name().toLowerCase()); ps.setInt(4, e.getId());
                    ps.executeUpdate();
                }
            } else {
                sqlE = "UPDATE empleados SET nombre_usuario=?,rol=? WHERE id_usuario=?";
                try (PreparedStatement ps = con.prepareStatement(sqlE)) {
                    ps.setString(1, e.getUsername());
                    ps.setString(2, e.getRole().name().toLowerCase()); ps.setInt(3, e.getId());
                    ps.executeUpdate();
                }
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    @Override
    public void delete(int idUsuario) throws SQLException {
        String sql = "UPDATE usuarios SET activo=0 WHERE id=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    @Override
    public Employee findById(int idUsuario) throws SQLException {
        String sql = "SELECT u.*,e.nombre_usuario,e.contrasenia,e.rol FROM usuarios u " +
                "JOIN empleados e ON u.id=e.id_usuario WHERE u.id=? AND u.activo=1";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapEmpleado(rs);
            }
        }
        return null;
    }

    @Override
    public Employee authenticate(String username, String contrasenia) throws SQLException {
        String sql = "SELECT u.*,e.nombre_usuario,e.contrasenia,e.rol FROM usuarios u " +
                "JOIN empleados e ON u.id=e.id_usuario " +
                "WHERE e.nombre_usuario=? AND e.contrasenia=SHA2(?,256) AND u.activo=1";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username); ps.setString(2, contrasenia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapEmpleado(rs);
            }
        }
        return null;
    }

    @Override
    public List<Employee> listEmployees(String filtro) throws SQLException {
        boolean hayFiltro = filtro != null && !filtro.isEmpty();
        String sql = "SELECT u.*,e.nombre_usuario,e.contrasenia,e.rol FROM usuarios u " +
                "JOIN empleados e ON u.id=e.id_usuario WHERE u.activo=1" +
                (hayFiltro ? " AND (LOWER(u.nombres) LIKE LOWER(?) OR LOWER(u.apellidos) LIKE LOWER(?) OR u.telefono LIKE ?)" : "");
        List<Employee> lista = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (hayFiltro) {
                String like = "%" + filtro + "%";
                ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapEmpleado(rs));
            }
        }
        return lista;
    }

    private Employee mapEmpleado(ResultSet rs) throws SQLException {
        Employee e = new Employee(
                rs.getString("nombre_usuario"),
                rs.getString("contrasenia"),
                Role.valueOf(rs.getString("rol").toUpperCase())
        );
        e.setId(rs.getInt("id"));
        e.setFirstName(rs.getString("nombres"));
        e.setLastName(rs.getString("apellidos"));
        e.setPhone(rs.getString("telefono"));
        e.setEmail(rs.getString("email"));
        e.setStreet(rs.getString("calle"));
        try { e.setHouseNumber(rs.getInt("numero_casa")); } catch (SQLException ignored) {}
        try { e.setZipCode(rs.getInt("codigo_postal")); } catch (SQLException ignored) {}
        e.setCity(rs.getString("ciudad"));
        e.setActive(rs.getBoolean("activo"));
        return e;
    }

    public boolean usernameExists(String username, Integer excludeId) throws SQLException {
        String sql = "SELECT 1 FROM empleados e " +
                "INNER JOIN usuarios u ON e.id_usuario = u.id " +
                "WHERE e.nombre_usuario = ? AND u.activo = 1";
        if (excludeId != null) {
            sql += " AND e.id_usuario != ?";
        }
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
