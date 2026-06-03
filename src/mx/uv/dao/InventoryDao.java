package mx.uv.dao;

import mx.uv.model.InventoryItem;
import mx.uv.model.Inventory;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface InventoryDao {
    int createInventory(int idEmpleado) throws SQLException;
    void saveItems(int idInventario, Map<Integer, Integer> cantidades) throws SQLException;
    List<InventoryItem> getLastInventory() throws SQLException;
}
