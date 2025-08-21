package util;

import modelo.Ejercicio;
import modelo.EjercicioBrazo;
import modelo.EjercicioCardio;
import modelo.EjercicioPierna;
import modelo.Rutina;
import modelo.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.time.LocalDate;

public class PruebaSerializacion {
    public static void main(String[] args) {
        // Crear objetos de prueba
        Usuario usuario = new Usuario("prueba", "1234", "modelo.Usuario de Prueba");

        EjercicioPierna sentadilla = new EjercicioPierna("Sentadilla", "Flexiona las rodillas bajando el cuerpo.");
        EjercicioCardio cinta = new EjercicioCardio("Cinta de correr", "Corre o camina sobre una cinta.");

        Rutina rutina = new Rutina("modelo.Rutina de prueba", usuario.getNombreUsuario(), LocalDate.now());
        rutina.agregarEjercicio(sentadilla);
        rutina.agregarEjercicio(cinta);

        ArrayList<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuario);

        ArrayList<Rutina> rutinas = new ArrayList<>();
        rutinas.add(rutina);

        // Guardar en archivos
        try (ObjectOutputStream outUsuarios = new ObjectOutputStream(new FileOutputStream("usuarios.dat"));
             ObjectOutputStream outRutinas = new ObjectOutputStream(new FileOutputStream("rutinas.dat"))) {

            outUsuarios.writeObject(usuarios);
            outRutinas.writeObject(rutinas);

            System.out.println("✅ Datos guardados correctamente.");
        } catch (IOException e) {
            System.err.println("❌ Error al guardar: " + e.getMessage());
        }

        // Leer desde archivos
        try (ObjectInputStream inUsuarios = new ObjectInputStream(new FileInputStream("usuarios.dat"));
             ObjectInputStream inRutinas = new ObjectInputStream(new FileInputStream("rutinas.dat"))) {

            ArrayList<Usuario> usuariosLeidos = (ArrayList<Usuario>) inUsuarios.readObject();
            ArrayList<Rutina> rutinasLeidas = (ArrayList<Rutina>) inRutinas.readObject();

            System.out.println("📂 Usuarios cargados:");
            for (Usuario u : usuariosLeidos) {
                System.out.println("- " + u.getNombreUsuario() + " (" + u.getNombreCompleto() + ")");
            }

            System.out.println("\n📂 Rutinas cargadas:");
            for (Rutina r : rutinasLeidas) {
                System.out.println("- " + r.getNombre() + " creada por " + r.getCreador());
                for (Ejercicio e : r.getEjercicios()) {
                    System.out.println("   • " + e.mostrarDetalle());
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error al leer archivos: " + e.getMessage());
        }
    }
}
