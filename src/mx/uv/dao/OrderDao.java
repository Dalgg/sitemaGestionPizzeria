package mx.uv.dao;

import mx.uv.model.*;
import mx.uv.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDao implements mx.uv.dao.impl.OrderDaoImpl {

    @Override
    public int createOrder(int idEmpleado, int idCliente) throws SQLException {
        String sql = "{CALL sp_realizar_pedido(?,?,?)}";
        try (Connection con = Database.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idEmpleado);
            cs.setInt(2, idCliente);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.execute();
            return cs.getInt(3);
        }
    }

    @Override
    public void addItem(int idPedido, int idProducto, int cantidad, double precio) throws SQLException {
        String sql = "INSERT INTO detalle_pedido(id_pedido,id_producto,cantidad,precio_unitario) VALUES(?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE cantidad=cantidad+VALUES(cantidad)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido); ps.setInt(2, idProducto);
            ps.setInt(3, cantidad); ps.setDouble(4, precio);
            ps.executeUpdate();
        }
    }

    @Override
    public void removeItem(int idPedido, int idProducto) throws SQLException {
        String sql = "DELETE FROM detalle_pedido WHERE id_pedido=? AND id_producto=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido); ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    @Override
    public void changeStatus(int idPedido, OrderStatus estado, int idEmpleado) throws SQLException {
        String sql = "{CALL sp_cambiar_estado_pedido(?,?,?)}";
        try (Connection con = Database.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idPedido);
            cs.setString(2, estado.getValue());
            cs.setInt(3, idEmpleado);
            cs.execute();
        }
    }

    @Override
    public List<Order> search(Integer idCliente, java.sql.Date fecha, OrderStatus estado) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT p.*,u.nombres AS cli_nom,u.apellidos AS cli_ap," +
                "ue.nombres AS emp_nom,ue.apellidos AS emp_ap,e.rol,e.nombre_usuario " +
                "FROM pedidos p " +
                "JOIN usuarios u ON p.id_cliente=u.id " +
                "JOIN empleados e ON p.id_empleado=e.id_usuario " +
                "JOIN usuarios ue ON e.id_usuario=ue.id " +
                "WHERE p.activo=1");
        List<Object> params = new ArrayList<>();
        if (idCliente != null) { sb.append(" AND p.id_cliente=?"); params.add(idCliente); }
        if (fecha != null)     { sb.append(" AND DATE(p.fecha)=?"); params.add(fecha); }
        if (estado != null)    { sb.append(" AND p.estado=?"); params.add(estado.getValue()); }
        sb.append(" ORDER BY p.fecha DESC");

        List<Order> lista = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object v = params.get(i);
                if (v instanceof Integer) ps.setInt(i+1, (Integer) v);
                else if (v instanceof java.sql.Date) ps.setDate(i+1, (java.sql.Date) v);
                else ps.setString(i+1, v.toString());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapPedido(rs));
            }
        }
        return lista;
    }

    @Override
    public Order findById(int id) throws SQLException {
        String sql = "SELECT p.*,u.nombres AS cli_nom,u.apellidos AS cli_ap," +
                "ue.nombres AS emp_nom,ue.apellidos AS emp_ap,e.rol,e.nombre_usuario " +
                "FROM pedidos p " +
                "JOIN usuarios u ON p.id_cliente=u.id " +
                "JOIN empleados e ON p.id_empleado=e.id_usuario " +
                "JOIN usuarios ue ON e.id_usuario=ue.id " +
                "WHERE p.id=? AND p.activo=1";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapPedido(rs);
            }
        }
        return null;
    }

    @Override
    public List<OrderItem> getItems(int idPedido) throws SQLException {
        String sql = "SELECT dp.*,pr.nombre,pr.codigo,pr.descripcion,pr.restricciones,pr.cantidad AS stock " +
                "FROM detalle_pedido dp JOIN productos pr ON dp.id_producto=pr.id WHERE dp.id_pedido=?";
        List<OrderItem> lista = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SaleProduct p = new SaleProduct();
                    p.setId(rs.getInt("id_producto"));
                    p.setCode(rs.getString("codigo"));
                    p.setName(rs.getString("nombre"));
                    p.setDescription(rs.getString("descripcion"));
                    p.setRestrictions(rs.getString("restricciones"));
                    p.setPrice(rs.getFloat("precio_unitario"));
                    p.setQuantity(rs.getInt("stock"));
                    Order ped = new Order(); ped.setId(idPedido);
                    OrderItem dp = new OrderItem(ped, p, rs.getInt("cantidad"), rs.getFloat("precio_unitario"));
                    lista.add(dp);
                }
            }
        }
        return lista;
    }

    @Override
    public List<OrderStatusLog> getStatusLog(int idPedido) throws SQLException {
        String sql = "SELECT b.*,u.nombres,u.apellidos,e.nombre_usuario,e.rol " +
                "FROM bitacora_pedido b JOIN empleados e ON b.id_empleado=e.id_usuario " +
                "JOIN usuarios u ON e.id_usuario=u.id WHERE b.id_pedido=? ORDER BY b.fecha_hora";
        List<OrderStatusLog> lista = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Employee emp = new Employee(rs.getString("nombre_usuario"), "", Role.valueOf(rs.getString("rol").toUpperCase()));
                    emp.setFirstName(rs.getString("nombres")); emp.setLastName(rs.getString("apellidos"));
                    Order ped = new Order(); ped.setId(idPedido);
                    OrderStatusLog reg = new OrderStatusLog(rs.getInt("id"), rs.getTimestamp("fecha_hora"),
                            ped, OrderStatus.fromValue(rs.getString("estado")), emp);
                    lista.add(reg);
                }
            }
        }
        return lista;
    }

    private Order mapPedido(ResultSet rs) throws SQLException {
        User cliente = new User();
        cliente.setId(rs.getInt("id_cliente"));
        cliente.setFirstName(rs.getString("cli_nom"));
        cliente.setLastName(rs.getString("cli_ap"));

        Employee emp = new Employee(rs.getString("nombre_usuario"), "", Role.valueOf(rs.getString("rol").toUpperCase()));
        emp.setId(rs.getInt("id_empleado"));
        emp.setFirstName(rs.getString("emp_nom")); emp.setLastName(rs.getString("emp_ap"));

        return new Order(rs.getInt("id"), rs.getTimestamp("fecha"),
                rs.getDouble("total"), emp, cliente, OrderStatus.fromValue(rs.getString("estado")));
    }
}
