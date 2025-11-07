/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import config.DatabaseConnection;
import entities.GrupoSanguineo;
import entities.HistoriaClinica;
import entities.Paciente;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author PC
 */
public class PacienteDAO implements GenericDAO<Paciente> {

    @Override
    public void insertar(Paciente paciente) {
        //consulta sql
        String sql = "INSERT INTO paciente (idPaciente, nombre, apellido, dni, fechaNacimiento, historiaClinica) VALUES (?, ?, ?, ?, ?, ?)";

        //Conexion y consulta, con resources para cerrar la transaccion automaticamente
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paciente.getId());
            stmt.setString(2, paciente.getNombre());
            stmt.setString(3, paciente.getApellido());
            stmt.setInt(4, paciente.getDni());
            stmt.setObject(5, paciente.getFechaNacimiento());
            stmt.setInt(6, paciente.getHistoriaClinica().getId());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo agregar el paciente.");
            } else {
                System.out.println("Paciente agregado ID: " + paciente.getId());
            }

        } catch (SQLException e) {
            System.out.println("Error al agregar el producto: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Paciente paciente) {
        String sql = "UPDATE paciente SET nombre = ?, apellido = ?, dni = ?, fechaNacimiento = ?, historiaClinica = ?  WHERE idPaciente = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paciente.getNombre());
            stmt.setString(2, paciente.getApellido());
            stmt.setInt(3, paciente.getDni());
            stmt.setObject(4, paciente.getFechaNacimiento());
            stmt.setInt(5, paciente.getHistoriaClinica().getId());
            stmt.setInt(6, paciente.getId());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se encontro el paciente.");
            } else {
                System.out.println("Paciente actualizado ID: " + paciente.getId());
            }

        } catch (SQLException e) {
            System.out.println("Error al actualizar el paciente: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id){
        String sql = "DELETE FROM paciente WHERE idPaciente = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se encontro el paciente.");
            } else {
                System.out.println("Paciente actualizado ID: " + id);
            }

        } catch (SQLException e) {
            System.out.println("Error al actualizar el paciente: " + e.getMessage());
        }
    }

    @Override
    public Paciente getById(int id) {
        String sql = "SELECT * FROM paciente "
                + "INNER JOIN historiaclinica ON paciente.historiaClinica = historiaclinica.id "
                + "WHERE idPaciente = ?";
        
        Paciente paciente = null;
        
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                //instancia la historia clinica con el metodo auxiliar para poder pasarla como argumento a paciente              
                HistoriaClinica hc = HistoriaClinicaDAO.mapHistoriaResult(result);
                
                //recupero de datos para instanciar paciente
                String nombre = result.getString("nombre");
                String apellido = result.getString("apellido");
                int dni = result.getInt("dni");
                LocalDate fechaNacimiento = result.getObject("fechaNacimiento", LocalDate.class);
                paciente = new Paciente(id, nombre, apellido, dni, fechaNacimiento, hc);
                
            } else {
                throw new SQLException("No se encontro el paciente.");
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar al paciente: " + e.getMessage());
        }
        return paciente;
    }

    @Override
    public List<Paciente> getAll(){
        List<Paciente> listaPacientes = new ArrayList<>();
        
        String sql = "SELECT * FROM paciente "
                + "INNER JOIN historiaclinica ON paciente.historiaClinica = historiaclinica.id";
        
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                               
                HistoriaClinica hc = HistoriaClinicaDAO.mapHistoriaResult(result);
                
                int id = result.getInt("idPaciente");
                String nombre = result.getString("nombre");
                String apellido = result.getString("apellido");
                int dni = result.getInt("dni");
                LocalDate fechaNacimiento = result.getObject("fechaNacimiento", LocalDate.class);
                
                listaPacientes.add(new Paciente(id, nombre, apellido, dni, fechaNacimiento, hc));
                
            } 

        } catch (SQLException e) {
            System.out.println("Error al buscar pacientes: " + e.getMessage());
        }
        return listaPacientes;   
    }

}
