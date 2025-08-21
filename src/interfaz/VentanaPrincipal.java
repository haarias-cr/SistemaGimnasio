package interfaz;

import javax.swing.*;

import modelo.GestorDeDatos;
import modelo.Rutina;
import modelo.Usuario;

public class VentanaPrincipal extends JFrame {
    private GestorDeDatos gestor;
    private Usuario usuarioActual;
    private Rutina rutinaActual;

    public VentanaPrincipal(GestorDeDatos gestor, Usuario usuarioActual) {
        this.gestor = gestor;
        this.usuarioActual = usuarioActual;

        setTitle("Gimnasio Fidness - Menú Principal");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JButton botonVerRutinas = new JButton("Ver rutinas");
        botonVerRutinas.setBounds(100, 30, 200, 40);
        add(botonVerRutinas);

        JButton botonCrearRutina = new JButton("Crear rutina");
        botonCrearRutina.setBounds(100, 90, 200, 40);
        add(botonCrearRutina);

        JButton botonSalir = new JButton("Cerrar sesión");
        botonSalir.setBounds(100, 150, 200, 40);
        add(botonSalir);

        botonVerRutinas.addActionListener(e -> {
            new VentanaEjercicios().setVisible(true);
        });

        botonCrearRutina.addActionListener(e -> {
            VentanaRutina ventanaRutina = new VentanaRutina(gestor, usuarioActual);
            ventanaRutina.setVisible(true);
            ventanaRutina.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    rutinaActual = ventanaRutina.getRutina();
                }
            });
        });

        botonSalir.addActionListener(e -> {
            dispose();
            new VentanaLogin().setVisible(true);
        });
    }
}
