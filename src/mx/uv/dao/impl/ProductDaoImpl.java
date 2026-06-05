package mx.uv.dao.impl;

import mx.uv.model.SaleProduct;
import java.sql.SQLException;
import java.util.List;

public interface ProductDaoImpl {
    void insert(SaleProduct producto) throws SQLException;
    void update(SaleProduct producto) throws SQLException;
    void delete(int id) throws SQLException;
    SaleProduct findById(int id) throws SQLException;
    List<SaleProduct> search(String filtro) throws SQLException;
    List<SaleProduct> listAvailable() throws SQLException;
    boolean productInOrders(int idProducto) throws SQLException;
}
