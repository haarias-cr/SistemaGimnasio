package interfaz;

import modelo.Ejercicio;
import modelo.EjercicioDAO;
import modelo.GestorDeDatos;
import modelo.Rutina;
import modelo.RutinaDAO;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Crear rutina con filtros (categoría + búsqueda).
 * - Carga ejercicios desde BD (EjercicioDAO)
 * - Permite elegir series/reps por ejercicio
 * - Guarda en BD con RutinaDAO
 * - Exporta a TXT
 */
public class VentanaRutina extends JFrame {

    private final GestorDeDatos gestor; // lo mantenemos por si lo usas en otras ventanas
    private final Usuario usuario;

    private Rutina rutina;
    private String nombreRutina;

    // Modelo visual
    private final DefaultListModel<String> modeloDisponibles = new DefaultListModel<>();
    private final DefaultListModel<String> modeloSeleccionados = new DefaultListModel<>();

    private JList<String> listaDisponibles;
    private JList<String> listaSeleccionados;

    // Datos
    private final List<Ejercicio> ejerciciosDisponibles = new ArrayList<>();
    private final List<Ejercicio> ejerciciosFiltrados = new ArrayList<>();
    private final List<ItemSeleccion> seleccionados = new ArrayList<>();

    // Filtros
    private JComboBox<String> cbCategoria;
    private JTextField txtBuscar;

    /** Contenedor auxiliar para series/reps por ejercicio seleccionado */
    private static class ItemSeleccion {
        Ejercicio ejercicio;
        int series;
        int repeticiones;

        ItemSeleccion(Ejercicio e, int s, int r) {
            this.ejercicio = e;
            this.series = s;
            this.repeticiones = r;
        }
    }

    public VentanaRutina(GestorDeDatos gestor, Usuario usuario) {
        this.gestor = gestor;
        this.usuario = usuario;

        pedirNombreRutinaYCrear();
        inicializarUI();
        cargarEjerciciosDesdeBD();
    }

    private void pedirNombreRutinaYCrear() {
        nombreRutina = JOptionPane.showInputDialog(this, "Ingrese el nombre de la rutina:");
        if (nombreRutina == null || nombreRutina.trim().isEmpty()) {
            nombreRutina = "Rutina sin nombre";
        }
        this.rutina = new Rutina(nombreRutina, usuario.getNombreUsuario(), LocalDate.now());
    }

    private void inicializarUI() {
        setTitle("Crear Rutina - " + nombreRutina);
        setSize(980, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ---------- Encabezado: filtros ----------
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        cbCategoria = new JComboBox<>(new String[]{
                "Todas", "Abdomen", "Brazo", "Cardio", "Espalda", "Hombro", "Pecho", "Pierna"
        });
        txtBuscar = new JTextField(18);

        filtros.add(new JLabel("Categoría:"));
        filtros.add(cbCategoria);
        filtros.add(new JLabel("Buscar:"));
        filtros.add(txtBuscar);

        cbCategoria.addActionListener(e -> aplicarFiltro());
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
        });

        // ---------- Centro: listas ----------
        listaDisponibles = new JList<>(modeloDisponibles);
        listaDisponibles.setCellRenderer(new EjercicioRenderer());

        listaSeleccionados = new JList<>(modeloSeleccionados);

        JScrollPane spDisp = new JScrollPane(listaDisponibles);
        spDisp.setBorder(new TitledBorder("Ejercicios disponibles (desde BD)"));

        JScrollPane spSel = new JScrollPane(listaSeleccionados);
        spSel.setBorder(new TitledBorder("Rutina actual (con series y reps)"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spDisp, spSel);
        split.setResizeWeight(0.5);

        // ---------- Botonera inferior ----------
        JButton btnAgregar = new JButton("Agregar a rutina");
        btnAgregar.addActionListener(e -> agregarEjercicio());

        JButton btnQuitar = new JButton("Quitar seleccionado");
        btnQuitar.addActionListener(e -> quitarSeleccionado());

        JButton btnLimpiar = new JButton("Limpiar rutina");
        btnLimpiar.addActionListener(e -> limpiarRutina());

        JButton btnGuardar = new JButton("Guardar rutina");
        btnGuardar.addActionListener(e -> guardarRutinaEnBD());

        JButton btnExportar = new JButton("Exportar (TXT)");
        btnExportar.addActionListener(e -> exportarRutinaTXT());

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        acciones.add(btnAgregar);
        acciones.add(btnQuitar);
        acciones.add(btnLimpiar);
        acciones.add(btnGuardar);
        acciones.add(btnExportar);
        acciones.add(btnCerrar);

        add(filtros, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);
    }

    /* ====================== Datos/Filtro ======================= */

    private void cargarEjerciciosDesdeBD() {
        modeloDisponibles.clear();
        ejerciciosDisponibles.clear();
        ejerciciosFiltrados.clear();

        List<Ejercicio> lista = new EjercicioDAO().listarTodos();
        ejerciciosDisponibles.addAll(lista);
        ejerciciosFiltrados.addAll(lista); // empieza sin filtro

        refrescarListaDisponibles();
    }

    private void aplicarFiltro() {
        String cat = String.valueOf(cbCategoria.getSelectedItem());
        String q = txtBuscar.getText().toLowerCase().trim();

        ejerciciosFiltrados.clear();
        for (Ejercicio e : ejerciciosDisponibles) {
            boolean okCat = cat.equals("Todas") || e.getTipo().equalsIgnoreCase(cat);
            boolean okTxt = q.isEmpty()
                    || e.getNombre().toLowerCase().contains(q)
                    || (e.getDescripcion() != null && e.getDescripcion().toLowerCase().contains(q));
            if (okCat && okTxt) ejerciciosFiltrados.add(e);
        }
        refrescarListaDisponibles();
    }

    private void refrescarListaDisponibles() {
        modeloDisponibles.clear();
        for (Ejercicio e : ejerciciosFiltrados) {
            String etiqueta = e.getNombre() + " (" + e.getTipo() + "): " + e.getDescripcion();
            modeloDisponibles.addElement(etiqueta);
        }
        // (el renderer ya está configurado en inicializarUI)
    }

    /* ====================== Acciones ======================= */

    private void agregarEjercicio() {
        int idx = listaDisponibles.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un ejercicio de la lista izquierda.");
            return;
        }
        Ejercicio e = ejerciciosFiltrados.get(idx);

        // Pedir series/reps
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        JSpinner spSeries = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1));
        JSpinner spReps = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        panel.add(new JLabel("Series:"));
        panel.add(spSeries);
        panel.add(new JLabel("Repeticiones:"));
        panel.add(spReps);

        int op = JOptionPane.showConfirmDialog(this, panel, "Configurar series y repeticiones",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (op != JOptionPane.OK_OPTION) return;

        int series = (Integer) spSeries.getValue();
        int reps = (Integer) spReps.getValue();

        seleccionados.add(new ItemSeleccion(e, series, reps));
        rutina.agregarEjercicio(e);
        modeloSeleccionados.addElement(e.getNombre() + " [" + series + " x " + reps + "]");
    }

    private void quitarSeleccionado() {
        int idx = listaSeleccionados.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un ejercicio de la lista derecha.");
            return;
        }
        ItemSeleccion it = seleccionados.remove(idx);
        modeloSeleccionados.remove(idx);
        rutina.eliminarEjercicio(it.ejercicio);
    }

    private void limpiarRutina() {
        seleccionados.clear();
        modeloSeleccionados.clear();
        this.rutina = new Rutina(nombreRutina, usuario.getNombreUsuario(), LocalDate.now());
    }

    private void guardarRutinaEnBD() {
        if (seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La rutina está vacía. Agrega ejercicios antes de guardar.");
            return;
        }
        try {
            // Construir items para RutinaDAO (orden = índice + 1)
            List<RutinaDAO.Item> items = new ArrayList<>();
            IntStream.range(0, seleccionados.size()).forEach(i -> {
                ItemSeleccion it = seleccionados.get(i);
                items.add(new RutinaDAO.Item(
                        it.ejercicio.getNombre(),
                        it.series,
                        it.repeticiones,
                        i + 1
                ));
            });

            RutinaDAO dao = new RutinaDAO();
            int rutinaId = dao.crearRutinaConEjercicios(
                    nombreRutina,
                    usuario.getNombreUsuario(),
                    usuario.getNombreCompleto(),
                    LocalDate.now(),
                    items
            );

            JOptionPane.showMessageDialog(this, "Rutina guardada (ID: " + rutinaId + ").");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error guardando rutina:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportarRutinaTXT() {
        if (seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La rutina está vacía. Agrega ejercicios antes de exportar.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Exportar rutina a TXT");
        int sel = fc.showSaveDialog(this);
        if (sel == JFileChooser.APPROVE_OPTION) {
            String ruta = fc.getSelectedFile().getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".txt")) ruta += ".txt";
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
                bw.write("Rutina: " + nombreRutina + "\n");
                bw.write("Creada por: " + usuario.getNombreUsuario() + "  Fecha: " + LocalDate.now() + "\n\n");
                for (ItemSeleccion it : seleccionados) {
                    bw.write("- " + it.ejercicio.getNombre() + " [" + it.series + " x " + it.repeticiones + "]\n");
                    bw.write("  " + it.ejercicio.getTipo() + ": " + it.ejercicio.getDescripcion() + "\n");
                }
                JOptionPane.showMessageDialog(this, "Rutina exportada:\n" + ruta);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exportando: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public Rutina getRutina() {
        return rutina;
    }

    /* ====================== Renderer ======================= */

    static class EjercicioRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            JLabel lb = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String text = String.valueOf(value);

            // Formato: "Nombre (Tipo): Descripción"
            int p1 = text.indexOf('(');
            int p2 = text.indexOf("): ");
            if (p1 > 0 && p2 > p1) {
                String nom = text.substring(0, p1).trim();
                String tipo = text.substring(p1 + 1, p2).trim();
                String desc = text.substring(p2 + 3).trim();

                lb.setText("<html><b>" + escape(nom) + "</b> " +
                        "<span style='color:gray'>(" + escape(tipo) + ")</span>" +
                        "<br><span style='font-size:10px'>" + escape(desc) + "</span></html>");
                lb.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            }
            return lb;
        }

        private static String escape(String s) {
            if (s == null) return "";
            return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
}
