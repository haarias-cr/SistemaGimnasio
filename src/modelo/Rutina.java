package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Rutina implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private ArrayList<Ejercicio> ejercicios;
    private String creador;
    private LocalDate fechaCreacion;

    public Rutina(String nombre, String creador, LocalDate fechaCreacion) {
        this.nombre = nombre;
        this.creador = creador;
        this.fechaCreacion = fechaCreacion;
        this.ejercicios = new ArrayList<>();
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCreador() {
        return creador;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void agregarEjercicio(Ejercicio e) {
        ejercicios.add(e);
    }

    public void eliminarEjercicio(Ejercicio e) {
        ejercicios.remove(e);
    }

    public ArrayList<Ejercicio> getEjercicios() {
        return ejercicios;
    }

    public String exportarRutina() {
        StringBuilder sb = new StringBuilder();
        sb.append("modelo.Rutina: ").append(nombre).append("\n");
        sb.append("Creada por: ").append(creador).append(" el ").append(fechaCreacion).append("\n\n");
        for (Ejercicio e : ejercicios) {
            sb.append(e.mostrarDetalle()).append("\n");
        }
        return sb.toString();
    }

    public boolean estaVacia() {
        return ejercicios.isEmpty();
    }
}
