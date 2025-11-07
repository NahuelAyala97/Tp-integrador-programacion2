/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Datos de conexión - Se configuran directamente en el código
    private static final String URL = "jdbc:mariadb://localhost:3306/bdd_tpi";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try {
            // 🔹 Carga del driver JDBC de MariaDB una sola vez
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // 🔹 Se lanza una excepción en caso de que el driver no esté disponible
            throw new RuntimeException("Error: No se encontró el driver JDBC.", e);
        }
    }

    /**
     * 🔹 Método para obtener una conexión a la base de datos.
     * @return Connection si la conexión es exitosa.
     * @throws SQLException Si hay un problema al conectarse.
     */
    public static Connection getConnection() throws SQLException {
        // Validación adicional para asegurarse de que las credenciales no estén vacías
        if (URL == null || URL.isEmpty() || USER == null || USER.isEmpty() || PASSWORD == null) {
            throw new SQLException("Configuración de la base de datos incompleta o inválida.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}