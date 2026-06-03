package mx.uv.dao;

import mx.uv.model.OrderItem;
import mx.uv.model.OrderStatus;
import mx.uv.model.Order;
import mx.uv.model.OrderStatusLog;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface OrderDao {
    int createOrder(int idEmpleado, int idCliente) throws SQLException;
    void addItem(int idPedido, int idProducto, int cantidad, double precio) throws SQLException;
    void removeItem(int idPedido, int idProducto) throws SQLException;
    void changeStatus(int idPedido, OrderStatus estado, int idEmpleado) throws SQLException;
    List<Order> search(Integer idCliente, Date fecha, OrderStatus estado) throws SQLException;
    Order findById(int id) throws SQLException;
    List<OrderItem> getItems(int idPedido) throws SQLException;
    List<OrderStatusLog> getStatusLog(int idPedido) throws SQLException;
}
