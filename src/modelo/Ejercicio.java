package modelo;

import java.io.Serializable;

public class Ejercicio implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String nombre;
    protected String tipo;
    protected String descripcion;

    public Ejercicio(String nombre, String tipo, String descripcion) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String mostrarDetalle() {
        return nombre + " (" + tipo + "): " + descripcion;
    }
}
