package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface GenericDAO<T> {

    void insertar(T entidad, Connection conn) throws SQLException;

    void actualizar(T entidad, Connection conn) throws SQLException;

    void eliminar(long id, Connection conn) throws SQLException;

    T getById(long id, Connection conn) throws SQLException;

    List<T> getAll(Connection conn) throws SQLException;

    // si el campo único es numérico, que también acepte long
    T buscarPorCampoUnicoLong(long valor, Connection conn) throws SQLException;
}
