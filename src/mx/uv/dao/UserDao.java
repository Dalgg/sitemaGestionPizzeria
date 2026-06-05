package mx.uv.dao;

import mx.uv.model.User;
import mx.uv.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao implements mx.uv.dao.impl.UserDaoImpl {

    @Override
    public void insert(User u) throws SQLException {
        String sql = "INSERT INTO usuarios(nombres,apellidos,telefono,email,calle,numero_casa,codigo_postal,ciudad,tipo_usuario) " +
                     "VALUES(?,?,?,?,?,?,?,?,'cliente')";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, u);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public void update(User u) throws SQLException {
        String sql = "UPDATE usuarios SET nombres=?,apellidos=?,telefono=?,email=?,calle=?,numero_casa=?,codigo_postal=?,ciudad=? " +
                     "WHERE id=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setParams(ps, u);
            ps.setInt(9, u.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        if (customerHasOrders(id)) {
            throw new SQLException("El cliente tiene pedidos y no puede eliminarse.");
        }
        String sql = "UPDATE usuarios SET activo=0 WHERE id=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id=? AND activo=1";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUsuario(rs);
            }
        }
        return null;
    }

    @Override
    public List<User> searchCustomers(String filtro) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE tipo_usuario='cliente' AND activo=1 AND " +
                "(LOWER(nombres) LIKE LOWER(?) OR LOWER(apellidos) LIKE LOWER(?) OR telefono LIKE ? OR LOWER(calle) LIKE LOWER(?))";
        List<User> lista = new ArrayList<>();
        String like = "%" + filtro + "%";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like); ps.setString(2, like);
            ps.setString(3, like); ps.setString(4, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapUsuario(rs));
            }
        }
        return lista;
    }

    @Override
    public List<User> listCustomers() throws SQLException {
        String sql = "SELECT * FROM v_clientes_activos ORDER BY nombres";
        List<User> lista = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapUsuario(rs));
        }
        return lista;
    }

    @Override
    public boolean customerHasOrders(int idCliente) throws SQLException {
        String sql = "SELECT fn_cliente_tiene_pedidos(?) AS resultado";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean("resultado");
            }
        }
    }

    private void setParams(PreparedStatement ps, User u) throws SQLException {
        ps.setString(1, u.getFirstName());
        ps.setString(2, u.getLastName());
        ps.setString(3, u.getPhone());
        ps.setString(4, u.getEmail());
        ps.setString(5, u.getStreet());
        ps.setInt   (6, u.getHouseNumber());
        ps.setInt   (7, u.getZipCode());
        ps.setString(8, u.getCity());
    }

    private User mapUsuario(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setFirstName(rs.getString("nombres"));
        u.setLastName(rs.getString("apellidos"));
        u.setPhone(rs.getString("telefono"));
        u.setEmail(rs.getString("email"));
        u.setStreet(rs.getString("calle"));
        u.setHouseNumber(rs.getInt("numero_casa"));
        u.setZipCode(rs.getInt("codigo_postal"));
        u.setCity(rs.getString("ciudad"));

        try { u.setActive(rs.getBoolean("activo")); }
        catch (SQLException ignored) { u.setActive(true); }
        return u;
    }
}
