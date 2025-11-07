/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import config.DatabaseConnection;

/**
 *
 * @author PC
 */
public class TestConexion {
    public static void main(String[] args) {
        /**
         * 🔹 Se usa un bloque try-with-resources para asegurar que la conexión
         *     se cierre automáticamente al salir del bloque.
         * 🔹 No es necesario llamar explícitamente a conn.close().
         */
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("✅ Conexión establecida con éxito.");
                
                // 🔹 Crear y ejecutar consulta SQL con PreparedStatement
                String sql = "SELECT * FROM paciente";
                try (PreparedStatement pstmt = conn.prepareStatement(sql); 
                        ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("📋 Listado de pacientes:");
                    while (rs.next()) {
                        int id = rs.getInt("idPaciente");
                        String nombre = rs.getString("nombre");
                        String apellido = rs.getString("apellido");
                        System.out.println("ID: " + id + ", Nombre: " + nombre + ", Apellido: " + apellido);
                    }
                }
            } else {
                System.out.println("❌ No se pudo establecer la conexión.");
            }
        } catch (SQLException e) {
            // 🔹 Manejo de errores en la conexión a la base de datos
            System.err.println("⚠️ Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace completo para depuración
        }
    }
}

