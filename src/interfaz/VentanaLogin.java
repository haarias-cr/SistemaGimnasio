package interfaz;

import javax.swing.*;

import modelo.GestorDeDatos;
import modelo.Usuario;


public class VentanaLogin extends JFrame {
    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private JButton botonLogin;
    private JButton botonRegistro;
    private GestorDeDatos gestor;

    public VentanaLogin() {
        gestor = new GestorDeDatos();

        setTitle("Login - Gimnasio Fidness");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel etiquetaUsuario = new JLabel("Usuario:");
        etiquetaUsuario.setBounds(30, 30, 80, 25);
        add(etiquetaUsuario);

        campoUsuario = new JTextField();
        campoUsuario.setBounds(120, 30, 150, 25);
        add(campoUsuario);

        JLabel etiquetaContrasena = new JLabel("Contraseña:");
        etiquetaContrasena.setBounds(30, 70, 80, 25);
        add(etiquetaContrasena);

        campoContrasena = new JPasswordField();
        campoContrasena.setBounds(120, 70, 150, 25);
        add(campoContrasena);

        botonLogin = new JButton("Iniciar sesión");
        botonLogin.setBounds(100, 110, 130, 30);
        add(botonLogin);

        botonRegistro = new JButton("Registrarse");
        botonRegistro.setBounds(100, 150, 130, 30);
        add(botonRegistro);

        // Acción del botón "Iniciar sesión"
        botonLogin.addActionListener(e -> {
            String usuario = campoUsuario.getText().trim();
            String contrasena = new String(campoContrasena.getPassword()).trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor complete ambos campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Usuario u = gestor.autenticarUsuario(usuario, contrasena);

            if (u != null) {
                JOptionPane.showMessageDialog(this, "¡Bienvenido " + u.getNombreCompleto() + "!");
                dispose();
                new VentanaPrincipal(gestor, u).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrecta.", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Acción del botón "Registrarse"
        botonRegistro.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog(this, "Ingrese su nombre completo:");
            if (nombre == null || nombre.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre completo no puede estar vacío.");
                return;
            }

            String usuario = JOptionPane.showInputDialog(this, "Ingrese un nombre de usuario:");
            if (usuario == null || usuario.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre de usuario no puede estar vacío.");
                return;
            }

            if (gestor.usuarioExiste(usuario)) {
                JOptionPane.showMessageDialog(this, "Ese nombre de usuario ya está en uso.", "Usuario existente", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String contrasena = JOptionPane.showInputDialog(this, "Ingrese una contraseña (mínimo 4 caracteres):");
            if (contrasena == null || contrasena.trim().length() < 4) {
                JOptionPane.showMessageDialog(this, "La contraseña es muy corta.", "Contraseña inválida", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                gestor.registrarUsuario(usuario.trim(), contrasena.trim(), nombre.trim());
                JOptionPane.showMessageDialog(this, "Usuario registrado con éxito.\nAhora puede iniciar sesión.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en registro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
