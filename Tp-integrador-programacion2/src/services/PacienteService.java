package services;

import config.DatabaseConnection;
import dao.HistoriaClinicaDAO;
import dao.PacienteDAO;
import entities.HistoriaClinica;
import entities.Paciente;
import entities.GrupoSanguineo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;


public class PacienteService implements GenericService<Paciente> {

    private final PacienteDAO pacienteDAO;
    private final HistoriaClinicaDAO historiaDAO;

    public PacienteService() {
        this.historiaDAO = new HistoriaClinicaDAO();
        this.pacienteDAO = new PacienteDAO(historiaDAO);
    }

    // ====================================================================
    // 1. INSERTAR (TRANSACCIONAL)
    // ====================================================================

    @Override
    public void insertar(Paciente p) throws SQLException {

        if (p.getDni() <= 0 || p.getNombre() == null || p.getHistoriaClinica() == null) {
            throw new SQLException("Error de validación: Datos de Paciente o HC incompletos/inválidos.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 


            if (pacienteDAO.buscarPorCampoUnicoLong(p.getDni(), conn) != null) {
                 throw new SQLException("Violación de Unicidad: Ya existe un paciente con DNI " + p.getDni());
            }


            if (pacienteDAO.isHistoriaClinicaAsignada((int) p.getHistoriaClinica().getId(), conn)) {
                 throw new SQLException("Violación 1:1: La Historia Clínica ID " + p.getHistoriaClinica().getId() + " ya está asignada a otro paciente.");
            }
            

            historiaDAO.insertar(p.getHistoriaClinica(), conn); 
  
            pacienteDAO.insertar(p, conn);

            conn.commit();
        
        } catch (SQLException ex) {
 
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rb) { 
            }
            throw ex; 
        } finally {

            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException c) { 
        }
    }

    // ====================================================================
    // 2. ACTUALIZAR (TRANSACCIONAL) - Implementa GenericService.actualizar
    // ====================================================================

    @Override
    public void actualizar(Paciente p) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 

            // 1. Actualizar HistoriaClinica (B)
            historiaDAO.actualizar(p.getHistoriaClinica(), conn);

            // 2. Actualizar Paciente (A)
            pacienteDAO.actualizar(p, conn);

            conn.commit();

        } catch (SQLException ex) {
            // 4. FALLO: REVERTIR
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rb) { /* ... */ }
            }
            throw ex; // Propagar la excepción original
        } finally {
            // 5. CERRAR RECURSOS
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException c) { /* ... */ }
        }
    }

    // ====================================================================
    // 3. ELIMINAR (TRANSACCIONAL - DELETE FÍSICO) - Implementa GenericService.eliminar
    // ====================================================================
    
    @Override
    public void eliminar(Long id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 

            // 1. Obtener Paciente (A) para obtener el ID de HC (B)
            Paciente p = pacienteDAO.getById(id, conn);
            if (p == null) {
                throw new SQLException("Paciente ID " + id + " no encontrado para eliminación.");
            }
            
            long hcId = p.getHistoriaClinica().getId();

            // 2. Eliminar Paciente (A)
            pacienteDAO.eliminar(id, conn);

            // 3. Eliminar HistoriaClinica (B)
            historiaDAO.eliminar(hcId, conn);
            
            conn.commit(); 

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rb) { /* ... */ }
            }
            throw ex; 
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException c) { /* ... */ }
        }
    }
    
    // ====================================================================
    // 4. LECTURA (Implementa GenericService)
    // ====================================================================

    @Override
    public Paciente getById(Long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return pacienteDAO.getById(id, conn);
        }
    }
    
    @Override
    public List<Paciente> getAll() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return pacienteDAO.getAll(conn);
        }
    }

    // =====================================================
    // MÉTODOS AUXILIARES Y LÓGICA DE NEGOCIO (AppMenu)
    // =====================================================
    
    public Paciente buscarPorDni(String dniStr) throws SQLException {
        // Se asume que el DNI debe ser parseado a Long para el DAO
        long dni = Long.parseLong(dniStr); 
        try (Connection conn = DatabaseConnection.getConnection()) {
            return pacienteDAO.buscarPorCampoUnicoLong(dni, conn);
        }
    }

    // Métodos para la lógica de HC automática
    private HistoriaClinica obtenerHistoriaClinicaAutomatica(Connection conn) throws SQLException {

        return crearNuevaHistoriaClinica(conn); 
    }

    private HistoriaClinica crearNuevaHistoriaClinica(Connection conn) throws SQLException {

        HistoriaClinica nueva = new HistoriaClinica(
                0L, 0L, GrupoSanguineo.O_POSITIVO, "", "", "Historia generada automáticamente"
        );
        historiaDAO.insertar(nueva, conn);

        return nueva;
    }
}
