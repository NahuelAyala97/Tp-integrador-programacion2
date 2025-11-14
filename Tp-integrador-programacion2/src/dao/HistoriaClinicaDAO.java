package dao;

import entities.GrupoSanguineo;
import entities.HistoriaClinica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoriaClinicaDAO implements GenericDAO<HistoriaClinica> {

    private static final String SQL_INSERT =
            "INSERT INTO historiaclinica (grupoSanguineo, antecedentes, medicacionActual, observaciones) " +
            "VALUES (?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE historiaclinica SET grupoSanguineo = ?, antecedentes = ?, " +
            "medicacionActual = ?, observaciones = ? WHERE id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM historiaclinica WHERE id = ?";

    private static final String SQL_SELECT_ID =
            "SELECT * FROM historiaclinica WHERE id = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT * FROM historiaclinica";

    private static final String SQL_SELECT_BY_NRO =
            "SELECT * FROM historiaclinica WHERE nroHistoria = ?";

    @Override
    public void insertar(HistoriaClinica historia, Connection conn) throws SQLException {

        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, historia.getGrupoSanguineo().getValor());
            stmt.setString(2, historia.getAntecedentes());
            stmt.setString(3, historia.getMedicacionActual());
            stmt.setString(4, historia.getObservaciones());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    historia.setId(rs.getLong(1)); // ahora long
                }
            }

            historia.setNroHistoria(obtenerNroHistoriaGenerado(conn, historia.getId()));
        }
    }

    private long obtenerNroHistoriaGenerado(Connection conn, long idHistoria) throws SQLException {
        String sql = "SELECT nroHistoria FROM historiaclinica WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idHistoria);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong("nroHistoria");
            }
        }

        throw new SQLException("No se encontró nroHistoria para el ID: " + idHistoria);
    }

    @Override
    public void actualizar(HistoriaClinica historia, Connection conn) throws SQLException {

        try (PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setString(1, historia.getGrupoSanguineo().getValor());
            stmt.setString(2, historia.getAntecedentes());
            stmt.setString(3, historia.getMedicacionActual());
            stmt.setString(4, historia.getObservaciones());
            stmt.setLong(5, historia.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(long id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public HistoriaClinica getById(long id, Connection conn) throws SQLException {

        try (PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_ID)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapHistoriaResult(rs);
            }
        }

        return null;
    }

    @Override
    public List<HistoriaClinica> getAll(Connection conn) throws SQLException {

        List<HistoriaClinica> lista = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapHistoriaResult(rs));
            }
        }

        return lista;
    }

    // MÉTODO CORRECTO EXIGIDO POR GenericDAO
    @Override
    public HistoriaClinica buscarPorCampoUnicoLong(long nroHistoria, Connection conn) throws SQLException {

        try (PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_NRO)) {
            stmt.setLong(1, nroHistoria);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapHistoriaResult(rs);
            }
        }

        return null;
    }

    // Mapeo a entidad
    private HistoriaClinica mapHistoriaResult(ResultSet rs) throws SQLException {

        long id = rs.getLong("id");
        long nro = rs.getLong("nroHistoria");
        GrupoSanguineo gs = GrupoSanguineo.fromValor(rs.getString("grupoSanguineo"));
        String antecedentes = rs.getString("antecedentes");
        String medicacion = rs.getString("medicacionActual");
        String observaciones = rs.getString("observaciones");

        return new HistoriaClinica(id, nro, gs, antecedentes, medicacion, observaciones);
    }
}
