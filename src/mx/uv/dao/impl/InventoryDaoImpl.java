package mx.uv.dao.impl;

import mx.uv.model.InventoryItem;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface InventoryDaoImpl {
    int createInventory(int idEmpleado) throws SQLException;
    void saveItems(int idInventario, Map<Integer, Integer> cantidades) throws SQLException;
    List<InventoryItem> getLastInventory() throws SQLException;
}
