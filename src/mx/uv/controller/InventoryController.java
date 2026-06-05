package mx.uv.controller;

import mx.uv.dao.InventoryDao;
import mx.uv.dao.impl.InventoryDaoImpl;
import mx.uv.model.InventoryItem;

import javafx.scene.control.Alert;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class InventoryController {
    private final InventoryDaoImpl dao = new InventoryDao();

    public boolean saveValidation(int idEmpleado, Map<Integer, Integer> cantidades) {
        try {
            int idInv = dao.createInventory(idEmpleado);
            dao.saveItems(idInv, cantidades);
            return true;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
            return false;
        }
    }

    public List<InventoryItem> getLastInventory() {
        try { return dao.getLastInventory(); }
        catch (SQLException e) {
            return List.of();
        }
    }
}
