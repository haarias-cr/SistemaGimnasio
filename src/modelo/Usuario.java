package modelo;

import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreUsuario;
    private String contrasena;
    private String nombreCompleto;

    public Usuario(String nombreUsuario, String contrasena, String nombreCompleto) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
    }

    public boolean autenticar(String usuario, String contrasena) {
        return this.nombreUsuario.equals(usuario) &&
                (contrasena.isEmpty() || this.contrasena.equals(contrasena));
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    @Override
    public String toString() {
        return nombreCompleto; // O puedes usar nombreUsuario si prefieres mostrar el nombre de usuario
    }
}
