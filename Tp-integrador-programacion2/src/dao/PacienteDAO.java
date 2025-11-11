package dao;

import entities.HistoriaClinica;
import entities.Paciente;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO implements GenericDAO<Paciente> {


    private static final String SQL_INSERT = "INSERT INTO paciente (idPaciente, nombre, apellido, dni, fechaNacimiento, historiaClinica) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE paciente SET nombre = ?, apellido = ?, dni = ?, fechaNacimiento = ?, historiaClinica = ? WHERE idPaciente = ?";
    private static final String SQL_DELETE = "DELETE FROM paciente WHERE idPaciente = ?"; // Eliminación física
    private static final String SQL_SELECT_ID = "SELECT * FROM paciente WHERE idPaciente = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM paciente";
    private static final String SQL_SELECT_BY_DNI = "SELECT * FROM paciente WHERE dni = ?";
    
    // Verifica si una Historia Clinica ya tiene un Paciente asignado.
    private static final String SQL_CHECK_HC_ASSIGNED = "SELECT idPaciente FROM paciente WHERE historiaClinica = ?";

    private final HistoriaClinicaDAO historiaClinicaDao; 

    public PacienteDAO(HistoriaClinicaDAO historiaClinicaDao) {
        this.historiaClinicaDao = historiaClinicaDao;
    }
    
    @Override
    public void insertar(Paciente paciente, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, paciente.getId());
            stmt.setString(2, paciente.getNombre());
            stmt.setString(3, paciente.getApellido());
            stmt.setInt(4, paciente.getDni());
            stmt.setObject(5, paciente.getFechaNacimiento());
            stmt.setInt(6, paciente.getHistoriaClinica().getId());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Error al insertar Paciente. Ninguna fila afectada.");
            }
            

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    paciente.setId(rs.getInt(1)); 
                }
            }
        }
    }

    @Override
    public void actualizar(Paciente paciente, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, paciente.getNombre());
            stmt.setString(2, paciente.getApellido());
            stmt.setInt(3, paciente.getDni());
            stmt.setObject(4, paciente.getFechaNacimiento());
            stmt.setInt(5, paciente.getHistoriaClinica().getId());
            stmt.setInt(6, paciente.getId());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar Paciente. ID no encontrado.");
            }
        }
    }

    @Override
    public void eliminar(int id, Connection conn) throws SQLException {
        // ELIMINACIÓN FÍSICA
        try (PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setInt(1, id);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Error al ELIMINAR FÍSICAMENTE Paciente. ID no encontrado.");
            }
        }
    }
    
    @Override
    public Paciente getById(int id, Connection conn) throws SQLException {
        Paciente paciente = null;
        String sql = SQL_SELECT_ID + " INNER JOIN historiaclinica ON paciente.historiaClinica = historiaclinica.id";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    paciente = mapPacienteResult(result, id);
                }
            }
        } 
        return paciente; 
    }

    @Override
    public List<Paciente> getAll(Connection conn) throws SQLException {
        List<Paciente> listaPacientes = new ArrayList<>();
        String sql = SQL_SELECT_ALL + " INNER JOIN historiaclinica ON paciente.historiaClinica = historiaclinica.id";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet result = stmt.executeQuery()) {
            while (result.next()) {
                int id = result.getInt("idPaciente");
                listaPacientes.add(mapPacienteResult(result, id));
            }
        }
        return listaPacientes;
    }

    @Override
    public Paciente buscarPorCampoUnicoInt(int dni, Connection conn) throws SQLException {
        Paciente paciente = null;
        String sql = SQL_SELECT_BY_DNI + " INNER JOIN historiaclinica ON paciente.historiaClinica = historiaclinica.id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dni);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("idPaciente");
                    paciente = mapPacienteResult(result, id);
                }
            }
        }
        return paciente;
    }

          
        // Recuperar datos de Paciente
        String nombre = result.getString("nombre");
        String apellido = result.getString("apellido");
        int dni = result.getInt("dni");
        LocalDate fechaNacimiento = result.getObject("fechaNacimiento", LocalDate.class);
        
        return new Paciente(idPaciente, nombre, apellido, dni, fechaNacimiento, hc);
    }
}
