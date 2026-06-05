package mx.uv.dao;

import mx.uv.model.SaleProduct;
import mx.uv.util.Database;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao implements mx.uv.dao.impl.ProductDaoImpl {

    @Override
    public void insert(SaleProduct p) throws SQLException {
        String sql = "INSERT INTO productos(codigo,nombre,descripcion,precio,restricciones,foto,cantidad) VALUES(?,?,?,?,?,?,?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, p);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public void update(SaleProduct p) throws SQLException {
        String sql = "UPDATE productos SET nombre=?,descripcion=?,precio=?,restricciones=?,foto=?,cantidad=? WHERE id=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getRestrictions());
            ps.setBytes(5, imagenToBytes(p.getImage()));
            ps.setInt(6, p.getQuantity());
            ps.setInt(7, p.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "UPDATE productos SET activo=0 WHERE id=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public SaleProduct findById(int id) throws SQLException {
        String sql = "SELECT * FROM productos WHERE id=? AND activo=1";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapProducto(rs);
            }
        }
        return null;
    }

    @Override
    public List<SaleProduct> search(String filtro) throws SQLException {
        String sql = "SELECT * FROM productos WHERE activo=1 AND (LOWER(nombre) LIKE LOWER(?) OR codigo LIKE ?)";
        List<SaleProduct> lista = new ArrayList<>();
        String like = "%" + filtro + "%";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like); ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapProducto(rs));
            }
        }
        return lista;
    }

    @Override
    public List<SaleProduct> listAvailable() throws SQLException {
        String sql = "SELECT * FROM v_productos_disponibles ORDER BY nombre";
        List<SaleProduct> lista = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapProductoVista(rs));
        }
        return lista;
    }

    @Override
    public boolean productInOrders(int id) throws SQLException {
        String sql = "SELECT fn_producto_en_pedidos(?) AS resultado";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean("resultado");
            }
        }
    }

    private void setParams(PreparedStatement ps, SaleProduct p) throws SQLException {
        ps.setString(1, p.getCode());
        ps.setString(2, p.getName());
        ps.setString(3, p.getDescription());
        ps.setDouble(4, p.getPrice());
        ps.setString(5, p.getRestrictions());
        ps.setBytes(6, imagenToBytes(p.getImage()));
        ps.setInt(7, p.getQuantity());
    }

    private SaleProduct mapProducto(ResultSet rs) throws SQLException {
        SaleProduct p = new SaleProduct();
        p.setId(rs.getInt("id"));
        p.setCode(rs.getString("codigo"));
        p.setName(rs.getString("nombre"));
        p.setDescription(rs.getString("descripcion"));
        p.setPrice(rs.getFloat("precio"));
        p.setRestrictions(rs.getString("restricciones"));
        p.setQuantity(rs.getInt("cantidad"));
        p.setAvailable(true);
        byte[] bytes = rs.getBytes("foto");
        if (bytes != null) {
            try { p.setImage(ImageIO.read(new ByteArrayInputStream(bytes))); } catch (IOException ignored) {}
        }
        return p;
    }

    private SaleProduct mapProductoVista(ResultSet rs) throws SQLException {
        SaleProduct p = new SaleProduct();
        p.setId(rs.getInt("id"));
        p.setCode(rs.getString("codigo"));
        p.setName(rs.getString("nombre"));
        p.setDescription(rs.getString("descripcion"));
        p.setPrice(rs.getFloat("precio"));
        p.setRestrictions(rs.getString("restricciones"));
        p.setQuantity(rs.getInt("cantidad"));
        p.setAvailable(true);
        return p;
    }

    private byte[] imagenToBytes(Image img) {
        if (img == null) return null;
        try {
            BufferedImage bi;
            if (img instanceof BufferedImage) bi = (BufferedImage) img;
            else {
                bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                bi.getGraphics().drawImage(img, 0, 0, null);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "PNG", baos);
            return baos.toByteArray();
        } catch (IOException e) { return null; }
    }
    public int getStock(int idProducto) throws SQLException {
        String sql = "SELECT cantidad FROM productos WHERE id = ? AND activo = 1";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("cantidad") : 0;
            }
        }
    }

    public boolean updateStock(int idProducto, int cantidadADescontar) throws SQLException {
        String sql = "UPDATE productos SET cantidad = cantidad - ? WHERE id = ? AND cantidad >= ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cantidadADescontar);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidadADescontar);
            int rows = ps.executeUpdate();
            return rows > 0; // true si se pudo descontar
        }
    }
}
