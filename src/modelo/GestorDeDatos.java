package modelo;

import java.io.*;
import java.util.ArrayList;

public class GestorDeDatos {
    private ArrayList<Usuario> usuarios;
    private ArrayList<Rutina> rutinasPublicadas;

    private final String ARCHIVO_USUARIOS = "usuarios.dat";
    private final String ARCHIVO_RUTINAS = "rutinas.dat";

    public GestorDeDatos() {
        usuarios = cargarUsuarios();
        rutinasPublicadas = cargarRutinas();

        if (usuarios.isEmpty()) {
            usuarios.add(new Usuario("admin", "1234", "Administrador"));
        }
    }

    // ----------- USUARIOS -----------

    public Usuario autenticarUsuario(String nombreUsuario, String contrasena) {
        for (Usuario u : usuarios) {
            if (u.autenticar(nombreUsuario, contrasena)) {
                return u;
            }
        }
        return null;
    }

    public boolean usuarioExiste(String nombreUsuario) {
        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                return true;
            }
        }
        return false;
    }

    public void registrarUsuario(String usuario, String contrasena, String nombreCompleto) {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        if (contrasena == null || contrasena.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
        }
        if (usuarioExiste(usuario)) {
            throw new IllegalArgumentException("El usuario ya existe.");
        }

        Usuario nuevo = new Usuario(usuario, contrasena, nombreCompleto);
        usuarios.add(nuevo);
        guardarUsuarios();
    }

    private void guardarUsuarios() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARCHIVO_USUARIOS))) {
            out.writeObject(usuarios);
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Usuario> cargarUsuarios() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) return new ArrayList<>();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return (ArrayList<Usuario>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ----------- RUTINAS -----------

    public void guardarRutina(Rutina r, String nombreArchivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            writer.write(r.exportarRutina());
        } catch (IOException e) {
            System.err.println("Error al exportar rutina: " + e.getMessage());
        }
    }

    public void agregarRutina(Rutina r) {
        rutinasPublicadas.add(r);
        guardarRutinas();
    }

    public ArrayList<Rutina> obtenerRutinas() {
        return rutinasPublicadas;
    }

    private void guardarRutinas() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARCHIVO_RUTINAS))) {
            out.writeObject(rutinasPublicadas);
        } catch (IOException e) {
            System.err.println("Error al guardar rutinas: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Rutina> cargarRutinas() {
        File archivo = new File(ARCHIVO_RUTINAS);
        if (!archivo.exists()) return new ArrayList<>();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return (ArrayList<Rutina>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar rutinas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ----------- EJERCICIOS DISPONIBLES -----------

    public ArrayList<Ejercicio> getEjerciciosDisponibles() {
        ArrayList<Ejercicio> lista = new ArrayList<>();
        lista.add(new EjercicioPierna("Sentadilla", "Flexiona las rodillas bajando el cuerpo."));
        lista.add(new EjercicioPierna("Prensa de piernas", "Empuja el peso con las piernas desde una posición sentada."));
        lista.add(new EjercicioPierna("Zancadas", "Paso largo hacia adelante flexionando ambas rodillas."));
        lista.add(new EjercicioPierna("Peso muerto rumano", "Baja el peso con piernas semirrígidas manteniendo la espalda recta."));

        lista.add(new EjercicioBrazo("Curl de bíceps", "Levanta la mancuerna hacia el hombro."));
        lista.add(new EjercicioBrazo("Fondos de tríceps", "Baja el cuerpo flexionando los codos con los brazos apoyados."));
        lista.add(new EjercicioBrazo("Curl martillo", "Versión del curl con agarre neutro."));
        lista.add(new EjercicioBrazo("Extensión de tríceps", "Extiende los brazos por encima de la cabeza con mancuerna."));

        lista.add(new EjercicioCardio("Cinta de correr", "Corre o camina sobre una cinta con velocidad ajustable."));
        lista.add(new EjercicioCardio("Bicicleta estática", "modelo.Ejercicio cardiovascular sentado pedaleando."));
        lista.add(new EjercicioCardio("Escaladora", "Simula subir escaleras constantemente."));
        lista.add(new EjercicioCardio("Saltos con cuerda", "Salta la cuerda repetidamente a ritmo constante."));
        return lista;
    }
}
