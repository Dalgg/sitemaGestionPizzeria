package mx.uv.dao.impl;

import mx.uv.dao.InventoryDao;
import mx.uv.model.InventoryItem;
import mx.uv.model.Inventory;
import mx.uv.model.SaleProduct;
import mx.uv.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryDaoImpl implements InventoryDao {

    @Override
    public int createInventory(int idEmpleado) throws SQLException {
        String sql = "INSERT INTO inventarios(id_empleado) VALUES(?)";
        try (Connection con = Database.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idEmpleado);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next(); return rs.getInt(1);
            }
        }
    }

    @Override
    public void saveItems(int idInventario, Map<Integer, Integer> cantidades) throws SQLException {
        String sql = "INSERT INTO detalle_inventario(id_inventario,id_producto,cantidad_contada,diferencia) VALUES(?,?,?,fn_diferencia_inventario(?,?))";
        Connection con = Database.getConexion();
        con.setAutoCommit(false);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Map.Entry<Integer, Integer> entry : cantidades.entrySet()) {
                ps.setInt(1, idInventario);
                ps.setInt(2, entry.getKey());
                ps.setInt(3, entry.getValue());
                ps.setInt(4, entry.getKey());
                ps.setInt(5, entry.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    @Override
    public List<InventoryItem> getLastInventory() throws SQLException {
        String sql = "SELECT di.*,p.nombre,p.codigo,p.precio,p.cantidad AS stock_sistema " +
                "FROM detalle_inventario di " +
                "JOIN productos p ON di.id_producto=p.id " +
                "WHERE di.id_inventario=(SELECT MAX(id) FROM inventarios) " +
                "ORDER BY p.nombre";
        List<InventoryItem> lista = new ArrayList<>();
        try (Connection con = Database.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SaleProduct p = new SaleProduct();
                p.setId(rs.getInt("id_producto"));
                p.setCode(rs.getString("codigo"));
                p.setName(rs.getString("nombre"));
                p.setPrice(rs.getFloat("precio"));
                p.setQuantity(rs.getInt("stock_sistema"));

                Inventory inv = new Inventory(rs.getInt("id_inventario"), rs.getInt("cantidad_contada"), "");
                InventoryItem di = new InventoryItem(inv, p, null);
                lista.add(di);
            }
        }
        return lista;
    }
}
