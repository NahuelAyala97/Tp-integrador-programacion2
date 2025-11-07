/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.DatabaseConnection;
import entities.GrupoSanguineo;
import entities.HistoriaClinica;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PC
 */
public class HistoriaClinicaDAO implements GenericDAO<HistoriaClinica> {

    @Override
    public void insertar(HistoriaClinica historia) {
        //consulta sql
        String sql = "INSERT INTO historiaClinica (id, nroHistoria, grupoSanguineo, antecedentes, medicacionActual, observaciones) VALUES (?, ?, ?, ?, ?, ?)";

        //Conexion y consulta, con resources para cerrar la transaccion automaticamente
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, historia.getId());
            stmt.setInt(2, historia.getNroHistoria());
            stmt.setString(3, historia.getGrupoSanguineo().getValor());
            stmt.setString(4, historia.getAntecedentes());
            stmt.setString(5, historia.getMedicacionActual());
            stmt.setString(6, historia.getObservaciones());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo agregar la historia clinica.");
            } else {
                System.out.println("Historia clinica agregada ID: " + historia.getId());
            }

        } catch (SQLException e) {
            System.out.println("Error al agregar la historia clinica: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(HistoriaClinica historia) {
        String sql = "UPDATE historiaclinica SET nroHistoria = ?, grupoSanguineo = ?, antecedentes = ?, medicacionActual = ?, observaciones = ?  WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, historia.getNroHistoria());
            stmt.setString(2, historia.getGrupoSanguineo().getValor());
            stmt.setString(3, historia.getAntecedentes());
            stmt.setString(4, historia.getMedicacionActual());
            stmt.setString(5, historia.getObservaciones());
            stmt.setInt(6, historia.getId());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se encontro la historia clinica.");
            } else {
                System.out.println("Historia clinica actualizada ID: " + historia.getId());
            }

        } catch (SQLException e) {
            System.out.println("Error al actualizar la historia clinica: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM historiaclinica WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se encontro la historia clinica.");
            } else {
                System.out.println("Historia clinica actualizado ID: " + id);
            }

        } catch (SQLException e) {
            System.out.println("Error al actualizar la historia clinica: " + e.getMessage());
        }
    }

    @Override
    public HistoriaClinica getById(int id) {
        String sql = "SELECT * FROM historiaclinica WHERE id = ?";

        HistoriaClinica hc = null;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                
                hc = this.mapHistoriaResult(result);
            } else {
                throw new SQLException("No se encontro la historia clinica.");
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar la historia clinica: " + e.getMessage());
        }
        return hc;
    }

    @Override
    public List<HistoriaClinica> getAll(){
        List<HistoriaClinica> listaHistorias = new ArrayList<>();
        
        String sql = "SELECT * FROM historiaclinica";
        
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            

            ResultSet result = stmt.executeQuery();

            while (result.next()) {              
                listaHistorias.add(HistoriaClinicaDAO.mapHistoriaResult(result));
                
            } 

        } catch (SQLException e) {
            System.out.println("Error al buscar pacientes: " + e.getMessage());
        }
        return listaHistorias; 
    }

    //metodo auxiliar para instanciar una historia con un result
    public static HistoriaClinica mapHistoriaResult(ResultSet result) throws SQLException{
        
        int idHistoria = result.getInt("id");
        int nroHistoria = result.getInt("nroHistoria");
        GrupoSanguineo gs = GrupoSanguineo.fromValor(result.getString("grupoSanguineo"));
        String antecedentes = result.getString("antecedentes");
        String medicacion = result.getString("medicacionActual");
        String observaciones = result.getString("observaciones");

        return new HistoriaClinica(idHistoria, nroHistoria, gs, antecedentes, medicacion, observaciones);

    }

}
