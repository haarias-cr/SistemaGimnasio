package modelo;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EjercicioDAO {

    public List<Ejercicio> listarTodos() {
        List<Ejercicio> lista = new ArrayList<>();
        String sql = "SELECT nombre, categoria, descripcion FROM ejercicios ORDER BY categoria, nombre";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String categoria = rs.getString("categoria");
                String descripcion = rs.getString("descripcion");
                lista.add(new Ejercicio(nombre, categoria, descripcion));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error listando ejercicios: " + e.getMessage());
        }
        return lista;
    }
}
