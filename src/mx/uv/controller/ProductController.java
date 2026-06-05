package mx.uv.controller;

import mx.uv.dao.ProductDao;
import mx.uv.dao.impl.ProductDaoImpl;
import mx.uv.model.SaleProduct;

import javafx.scene.control.Alert;
import java.sql.SQLException;
import java.util.List;

public class ProductController {
    private final ProductDaoImpl dao = new ProductDao();

    public boolean save(SaleProduct p, boolean esNuevo) {
        try {
            if (p.getCode() == null || p.getCode().trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "El código es requerido.").showAndWait();
                return false;
            }
            if (p.getName() == null || p.getName().trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "El nombre es requerido.").showAndWait();
                return false;
            }
            if (esNuevo) dao.insert(p);
            else         dao.update(p);
            return true;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
            return false;
        }
    }

    public boolean delete(int id) {
        try {
            if (dao.productInOrders(id)) {
                new Alert(Alert.AlertType.WARNING, "El producto está en pedidos y no puede eliminarse.").showAndWait();
                return false;
            }
            dao.delete(id);
            return true;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
            return false;
        }
    }

    public List<SaleProduct> search(String filtro) {
        try {
            if (filtro == null || filtro.trim().isEmpty()) return dao.listAvailable();
            return dao.search(filtro.trim());
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
            return List.of();
        }
    }

    public List<SaleProduct> listAvailable() {
        try { return dao.listAvailable(); }
        catch (SQLException e) { return List.of(); }
    }
}
