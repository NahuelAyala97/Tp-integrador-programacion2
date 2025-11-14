package dao;

import entities.GrupoSanguineo;
import entities.HistoriaClinica;
import entities.Paciente;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO implements GenericDAO<Paciente> {

    private static final String SQL_INSERT =
            "INSERT INTO paciente (nombre, apellido, dni, fechaNacimiento, historiaClinica) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE paciente SET nombre = ?, apellido = ?, dni = ?, fechaNacimiento = ?, historiaClinica = ? " +
            "WHERE idPaciente = ?";

    private static final String SQL_DELETE =
            "DELETE FROM paciente WHERE idPaciente = ?";

    private static final String SQL_SELECT_BASE =
            "SELECT p.idPaciente, p.nombre, p.apellido, p.dni, p.fechaNacimiento, " +
            "h.id AS hc_id, h.nroHistoria, h.grupoSanguineo, h.antecedentes, h.medicacionActual, h.observaciones " +
            "FROM paciente p INNER JOIN historiaclinica h ON p.historiaClinica = h.id ";

    private static final String SQL_SELECT_BY_ID =
            SQL_SELECT_BASE + "WHERE p.idPaciente = ?";

    private static final String SQL_SELECT_ALL =
            SQL_SELECT_BASE;

    private static final String SQL_SELECT_BY_DNI =
            SQL_SELECT_BASE + "WHERE p.dni = ?";

    private final HistoriaClinicaDAO historiaClinicaDao;

    public PacienteDAO(HistoriaClinicaDAO historiaClinicaDao) {
        this.historiaClinicaDao = historiaClinicaDao;
    }

    @Override
    public void insertar(Paciente paciente, Connection conn) throws SQLException {
        try (PreparedStatement stmt =
                     conn.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, paciente.getNombre());
            stmt.setString(2, paciente.getApellido());
            stmt.setInt(3, paciente.getDni());
            stmt.setObject(4, paciente.getFechaNacimiento());
            stmt.setLong(5, paciente.getHistoriaClinica().getId());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Error al insertar Paciente. Ninguna fila afectada.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    paciente.setId(rs.getLong(1));
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
            stmt.setLong(5, paciente.getHistoriaClinica().getId());
            stmt.setLong(6, paciente.getId());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar Paciente. ID no encontrado.");
            }
        }
    }

    @Override
    public void eliminar(long id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setLong(1, id);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar Paciente. ID no encontrado.");
            }
        }
    }

    @Override
    public Paciente getById(long id, Connection conn) throws SQLException {
        Paciente paciente = null;

        try (PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    paciente = mapPacienteResult(rs);
                }
            }
        }

        return paciente;
    }

    @Override
    public List<Paciente> getAll(Connection conn) throws SQLException {
        List<Paciente> lista = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapPacienteResult(rs));
            }
        }
        return lista;
    }

    @Override
    public Paciente buscarPorCampoUnicoLong(long dni, Connection conn) throws SQLException {
        Paciente paciente = null;

        try (PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_DNI)) {

            stmt.setLong(1, dni);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    paciente = mapPacienteResult(rs);
                }
            }
        }
        return paciente;
    }

    private Paciente mapPacienteResult(ResultSet rs) throws SQLException {

        long idPaciente = rs.getLong("idPaciente");
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        int dni = rs.getInt("dni");
        LocalDate fechaNacimiento = rs.getObject("fechaNacimiento", LocalDate.class);

        long hcId = rs.getLong("hc_id");
        long nroHistoria = rs.getLong("nroHistoria");
        GrupoSanguineo gs = GrupoSanguineo.fromValor(rs.getString("grupoSanguineo"));
        String antecedentes = rs.getString("antecedentes");
        String medicacion = rs.getString("medicacionActual");
        String observaciones = rs.getString("observaciones");

        HistoriaClinica hc = new HistoriaClinica(
                hcId,
                nroHistoria,
                gs,
                antecedentes,
                medicacion,
                observaciones
        );

        return new Paciente(idPaciente, nombre, apellido, dni, fechaNacimiento, hc);
    }
}
