package util;

import modelo.Ejercicio;
import modelo.EjercicioDAO;

import java.util.List;

/**
 * Prueba simple: conecta y lista algunos ejercicios por consola.
 */
public class PruebaBD {
    public static void main(String[] args) {
        // Fuerza la conexión
        DBConnection.getConnection();

        // Prueba de lectura
        List<Ejercicio> ejercicios = new EjercicioDAO().listarTodos();
        System.out.println("Total ejercicios: " + ejercicios.size());
        for (int i = 0; i < Math.min(10, ejercicios.size()); i++) {
            Ejercicio e = ejercicios.get(i);
            System.out.println(" - " + e.getNombre() + " (" + e.getTipo() + "): " + e.getDescripcion());
        }
    }
}
