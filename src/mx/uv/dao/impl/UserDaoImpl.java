package mx.uv.dao.impl;

import mx.uv.model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDaoImpl {
    void insert(User usuario) throws SQLException;
    void update(User usuario) throws SQLException;
    void delete(int id) throws SQLException;
    User findById(int id) throws SQLException;
    List<User> searchCustomers(String filtro) throws SQLException;
    List<User> listCustomers() throws SQLException;
    boolean customerHasOrders(int idCliente) throws SQLException;
}
