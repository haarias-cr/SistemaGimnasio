package modelo;

import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos para Rutinas.
 * Esquema esperado (opción normalizada):
 *
 *  - usuarios(id, username, password, nombre_completo)
 *  - ejercicios(id, nombre, categoria, descripcion)
 *  - rutinas(id, nombre, usuario_id, fecha_creacion)
 *  - rutina_ejercicio(rutina_id, ejercicio_id, orden, series, repeticiones)
 *
 *  FK: rutina_ejercicio.rutina_id -> rutinas.id ON DELETE CASCADE
 *      rutina_ejercicio.ejercicio_id -> ejercicios.id
 */
public class RutinaDAO {

    /** DTO de rutina con su lista de ejercicios */
    public static class RutinaDetalle {
        public int id;
        public String nombre;
        public String usuario;     // username
        public LocalDate fecha;
        public List<ItemDetalle> items = new ArrayList<>();

        @Override
        public String toString() {
            return "#" + id + " — " + nombre + " (" + usuario + ", " + fecha + ")";
        }
    }

    /** DTO de cada ejercicio dentro de una rutina */
    public static class ItemDetalle {
        public int orden;
        public String ejercicio;
        public String categoria;
        public String descripcion;
        public int series;
        public int repeticiones;
    }

    /** DTO para recibir desde la GUI los items a guardar */
    public static class Item {
        public final String ejercicioNombre;
        public final int series;
        public final int repeticiones;
        public final int orden;

        public Item(String ejercicioNombre, int series, int repeticiones, int orden) {
            this.ejercicioNombre = ejercicioNombre;
            this.series = series;
            this.repeticiones = repeticiones;
            this.orden = orden;
        }
    }

    /* ===================== Helpers privados ===================== */

    /** Asegura que exista un usuario en la tabla usuarios y devuelve su id. */
    private int ensureUsuarioId(Connection conn, String username, String nombreCompleto) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM usuarios WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO usuarios (username, password, nombre_completo) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, "dummy"); // ajusta si haces login contra BD
            ps.setString(3, (nombreCompleto != null && !nombreCompleto.isBlank()) ? nombreCompleto : username);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("No fue posible crear/obtener usuario_id para " + username);
    }

    /** Obtiene el id del ejercicio por nombre en el catálogo ejercicios. */
    private int getEjercicioId(Connection conn, String nombre) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM ejercicios WHERE nombre = ?")) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Ejercicio no encontrado: " + nombre);
    }

    /* ===================== Operaciones públicas ===================== */

    /**
     * Crea una rutina (cabecera) y sus items en rutina_ejercicio dentro de una transacción.
     * Devuelve el id de la nueva rutina.
     */
    public int crearRutinaConEjercicios(String nombreRutina,
                                        String username,
                                        String nombreCompleto,
                                        LocalDate fechaCreacion,
                                        List<Item> items) throws SQLException {

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int usuarioId = ensureUsuarioId(conn, username, nombreCompleto);

                int rutinaId;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO rutinas (nombre, usuario_id, fecha_creacion) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, nombreRutina);
                    ps.setInt(2, usuarioId);
                    ps.setDate(3, Date.valueOf(fechaCreacion));
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("No se obtuvo id de la rutina");
                        rutinaId = rs.getInt(1);
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO rutina_ejercicio (rutina_id, ejercicio_id, orden, series, repeticiones) " +
                                "VALUES (?, ?, ?, ?, ?)")) {
                    for (Item it : items) {
                        int ejercicioId = getEjercicioId(conn, it.ejercicioNombre);
                        ps.setInt(1, rutinaId);
                        ps.setInt(2, ejercicioId);
                        ps.setInt(3, it.orden);
                        ps.setInt(4, it.series);
                        ps.setInt(5, it.repeticiones);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                conn.commit();
                return rutinaId;

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    /**
     * Lista las rutinas con sus ejercicios asociados.
     */
    public List<RutinaDetalle> listarConDetalle() throws SQLException {
        List<RutinaDetalle> result = new ArrayList<>();

        String sqlRutinas = """
                SELECT r.id, r.nombre, u.username, r.fecha_creacion
                FROM rutinas r
                JOIN usuarios u ON u.id = r.usuario_id
                ORDER BY r.id DESC
                """;

        String sqlItems = """
                SELECT re.orden, e.nombre AS ejercicio, e.categoria, e.descripcion,
                       re.series, re.repeticiones
                FROM rutina_ejercicio re
                JOIN ejercicios e ON e.id = re.ejercicio_id
                WHERE re.rutina_id = ?
                ORDER BY re.orden ASC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psR = conn.prepareStatement(sqlRutinas);
             ResultSet rsR = psR.executeQuery()) {

            while (rsR.next()) {
                RutinaDetalle r = new RutinaDetalle();
                r.id = rsR.getInt("id");
                r.nombre = rsR.getString("nombre");
                r.usuario = rsR.getString("username");
                Date f = rsR.getDate("fecha_creacion");
                r.fecha = (f != null) ? f.toLocalDate() : null;

                // cargar items de la rutina
                try (PreparedStatement psI = conn.prepareStatement(sqlItems)) {
                    psI.setInt(1, r.id);
                    try (ResultSet rsI = psI.executeQuery()) {
                        while (rsI.next()) {
                            ItemDetalle d = new ItemDetalle();
                            d.orden = rsI.getInt("orden");
                            d.ejercicio = rsI.getString("ejercicio");
                            d.categoria = rsI.getString("categoria");
                            d.descripcion = rsI.getString("descripcion");
                            d.series = rsI.getInt("series");
                            d.repeticiones = rsI.getInt("repeticiones");
                            r.items.add(d);
                        }
                    }
                }
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Elimina una rutina por id. Gracias a ON DELETE CASCADE en rutina_ejercicio,
     * sus detalles se eliminan automáticamente.
     */
    public void eliminarRutina(int rutinaId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM rutinas WHERE id = ?")) {
            ps.setInt(1, rutinaId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("No existe una rutina con id=" + rutinaId);
            }
        }
    }
}
