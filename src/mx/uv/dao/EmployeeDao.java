package mx.uv.dao;

import mx.uv.model.Employee;
import java.sql.SQLException;
import java.util.List;

public interface EmployeeDao {
    void insert(Employee empleado) throws SQLException;
    void update(Employee empleado) throws SQLException;
    void delete(int idUsuario) throws SQLException;
    Employee findById(int idUsuario) throws SQLException;
    Employee authenticate(String username, String contrasenia) throws SQLException;
    List<Employee> listEmployees(String filtro) throws SQLException;
}
