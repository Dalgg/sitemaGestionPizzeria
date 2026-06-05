package mx.uv.controller;

import mx.uv.dao.OrderDao;
import mx.uv.dao.impl.OrderDaoImpl;
import mx.uv.model.Order;
import mx.uv.model.OrderItem;
import mx.uv.model.OrderStatus;
import mx.uv.model.OrderStatusLog;

import javafx.scene.control.Alert;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class OrderController {
    private final OrderDaoImpl dao = new OrderDao();

    public int createOrder(int idEmpleado, int idCliente) {
        try {
            return dao.createOrder(idEmpleado, idCliente);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error al crear pedido: " + e.getMessage()).showAndWait();
            return -1;
        }
    }

    public boolean addItem(int idPedido, int idProducto, int cantidad, double precio) {
        try {
            dao.addItem(idPedido, idProducto, cantidad, precio);
            return true;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
            return false;
        }
    }

    public boolean removeItem(int idPedido, int idProducto) {
        try {
            dao.removeItem(idPedido, idProducto);
            return true;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
            return false;
        }
    }

    public boolean changeStatus(int idPedido, OrderStatus estado, int idEmpleado) {
        try {
            dao.changeStatus(idPedido, estado, idEmpleado);
            return true;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
            return false;
        }
    }

    public List<Order> search(Integer idCliente, Date fecha, OrderStatus estado) {
        try {
            return dao.search(idCliente, fecha, estado);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
            return List.of();
        }
    }

    public Order findById(int id) {
        try { return dao.findById(id); }
        catch (SQLException e) { return null; }
    }

    public List<OrderItem> getItems(int idPedido) {
        try { return dao.getItems(idPedido); }
        catch (SQLException e) { return List.of(); }
    }

    public List<OrderStatusLog> getStatusLog(int idPedido) {
        try { return dao.getStatusLog(idPedido); }
        catch (SQLException e) { return List.of(); }
    }
}
