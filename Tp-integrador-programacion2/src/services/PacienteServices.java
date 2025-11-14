package service;

import config.DatabaseConnection;
import dao.HistoriaClinicaDAO;
import dao.PacienteDAO;
import entities.Paciente;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Interfaz Genérica (La adaptamos para que lance solo SQLException)
interface GenericService<T> {
    void insertar(T entity) throws SQLException;
    void actualizar(T entity) throws SQLException;
    void eliminar(Long id) throws SQLException;
    T getById(Long id) throws SQLException; // Ya no lanzamos NotFoundException
    List<T> getAll() throws SQLException;
}

public class PacienteService implements GenericService<Paciente> {
    
    private final PacienteDAO pacienteDao;
    private final HistoriaClinicaDAO historiaClinicaDao;
    
    // Constructor con inyección de dependencias
    public PacienteService(PacienteDAO pacienteDao, HistoriaClinicaDAO historiaClinicaDao) {
        this.pacienteDao = pacienteDao;
        this.historiaClinicaDao = historiaClinicaDao;
    }

    // --- ORQUESTACIÓN TRANSACCIONAL Y CRUD ---

    /**
     * Crea un Paciente (A) y su HistoriaClinica (B) en una única transacción.
     * PROPAGA SQLException.
     */
    @Override
    public void insertar(Paciente paciente) throws SQLException {
        // Validación básica (No hay excepciones de negocio, solo se verifica null)
        if (paciente.getDni() == null || paciente.getHistoriaClinica() == null) {
            // Usamos una excepción estándar más genérica en caso de validación fallida
            throw new IllegalArgumentException("El DNI y la Historia Clínica son campos obligatorios."); 
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // 🔹 INICIAR TRANSACCIÓN
            
            // 1. Insertar Paciente (A). El DAO le asigna el ID generado.
            pacienteDao.insertar(paciente, conn); 

            // 2. Establecer la relación 1:1: Asignar el ID de Paciente (A) a la HistoriaClinica (B)
            paciente.getHistoriaClinica().setPacienteId(paciente.getId()); 

            // 3. Insertar HistoriaClinica (B)
            historiaClinicaDao.insertar(paciente.getHistoriaClinica(), conn); 
            
            conn.commit(); // ✅ ÉXITO: CONFIRMAR TRANSACCIÓN
            
        } catch (SQLException e) {
            // 4. FALLO: REVERTIR TRANSACCIÓN
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException rb) {
                    // Si el rollback falla, se propaga la excepción original, pero el error de rollback se loguea
                    System.err.println("Error crítico al intentar rollback: " + rb.getMessage());
                }
            }
            // Propagamos la SQLException original para que el Main la maneje
            throw e; 
            
        } finally {
            // 5. CERRAR RECURSOS Y RESTAURAR AUTOCOMMIT
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Restaurar estado
                    conn.close();
                }
            } catch (SQLException c) {
                // Se ignora o se loguea el error al cerrar, no afecta la transacción principal
                System.err.println("Error al cerrar la conexión: " + c.getMessage());
            }
        }
    }

    /**
     * Implementa la baja lógica (UPDATE SET eliminado=TRUE) de forma transaccional.
     * PROPAGA SQLException.
     */
    @Override
    public void eliminar(Long id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // 🔹 INICIAR TRANSACCIÓN

            // 1. Recuperar Paciente y su HC para obtener el ID de B
            Paciente paciente = pacienteDao.getById(id, conn);
            
            if (paciente == null) {
                 // Si no se encuentra, usamos una excepción estándar.
                throw new SQLException("Paciente con ID " + id + " no encontrado para eliminación."); 
            }

            // 2. Ejecutar baja lógica en Paciente (A)
            pacienteDao.eliminar(id, conn);
            
            // 3. Ejecutar baja lógica en HistoriaClinica (B)
            Long historiaId = paciente.getHistoriaClinica().getId();
            historiaClinicaDao.eliminar(historiaId, conn);
            
            conn.commit(); // ✅ ÉXITO
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException rb) {
                    System.err.println("Error crítico al intentar rollback: " + rb.getMessage());
                }
            }
            throw e; // Propagamos la excepción
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException c) { /* ... */ }
        }
    }

    /**
     * Búsqueda por ID (Simple, no transaccional)
     * PROPAGA SQLException.
     */
    @Override
    public Paciente getById(Long id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            Paciente paciente = pacienteDao.getById(id, conn);
            
            if (paciente == null) {
                // Si no se encuentra, lanzamos SQLException que se interpretará en el Main
                throw new SQLException("Paciente con ID " + id + " no encontrado.");
            }
            return paciente;
            
        } catch (SQLException e) {
            throw e; // Propagamos la excepción
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException c) { /* ... */ }
        }
    }

    /**
     * Implementa la búsqueda por un campo relevante (DNI).
     * PROPAGA SQLException.
     */
    public Paciente buscarPorDni(String dni) throws SQLException {
        if (dni == null || dni.trim().isEmpty()) {
             throw new IllegalArgumentException("El DNI no puede ser vacío.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            Paciente p = pacienteDao.buscarPorCampoUnico(dni, conn);
            
            if (p == null) {
                throw new SQLException("Paciente con DNI " + dni + " no encontrado.");
            }
            return p;
        } catch (SQLException e) {
            throw e; // Propagamos la excepción
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException c) { /* ... */ }
        }
    }
    
    // --- MÉTODOS PENDIENTES ---

    @Override
    public void actualizar(Paciente entity) throws SQLException {
        // Implementar lógica de validación y transacción similar a 'insertar' (actualizando A y B)
    }

    @Override
    public List<Paciente> getAll() throws SQLException {
        Connection conn = null;
        try {
             conn = DatabaseConnection.getConnection();
             return pacienteDao.getAll(conn);
        } catch (SQLException e) {
             throw e;
        } finally {
             try {
                if (conn != null) conn.close();
            } catch (SQLException c) { /* ... */ }
        }
    }
}