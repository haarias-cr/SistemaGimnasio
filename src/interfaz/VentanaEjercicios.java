package interfaz;

import modelo.RutinaDAO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaEjercicios extends JFrame {

    private final DefaultListModel<RutinaDAO.RutinaDetalle> modeloRutinas = new DefaultListModel<>();
    private final JList<RutinaDAO.RutinaDetalle> listaRutinas = new JList<>(modeloRutinas);

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Orden", "Ejercicio", "Categoría", "Series", "Reps", "Descripción"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
        @Override public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0, 3, 4 -> Integer.class;
                default -> String.class;
            };
        }
    };
    private final JTable tabla = new JTable(modeloTabla);

    private final TitledBorder borderTabla = new TitledBorder("Detalle de rutina");

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Botón nuevo
    private final JButton btnEliminar = new JButton("Eliminar rutina");

    public VentanaEjercicios() {
        setTitle("Ver Rutinas (desde BD)");
        setSize(980, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel izquierdo: lista de rutinas con render bonito
        listaRutinas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaRutinas.setCellRenderer(new RutinaRenderer());
        JScrollPane spLista = new JScrollPane(listaRutinas);
        spLista.setBorder(new TitledBorder("Rutinas guardadas (BD)"));

        // Panel derecho: tabla de ejercicios
        tabla.setRowHeight(22);
        tabla.setAutoCreateRowSorter(true);
        configurarAnchurasTabla();
        JScrollPane spTabla = new JScrollPane(tabla);
        spTabla.setBorder(borderTabla);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spLista, spTabla);
        split.setResizeWeight(0.35);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarRutinas());

        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(e -> eliminarSeleccionada());

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        sur.add(btnRefrescar);
        sur.add(btnEliminar);
        sur.add(btnCerrar);

        add(split, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);

        // eventos
        listaRutinas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnEliminar.setEnabled(listaRutinas.getSelectedValue() != null);
                mostrarDetalleSeleccionado();
            }
        });

        // carga inicial
        cargarRutinas();
    }

    private void configurarAnchurasTabla() {
        var cols = tabla.getColumnModel();
        cols.getColumn(0).setPreferredWidth(55);   // orden
        cols.getColumn(1).setPreferredWidth(170);  // ejercicio
        cols.getColumn(2).setPreferredWidth(110);  // categoría
        cols.getColumn(3).setPreferredWidth(55);   // series
        cols.getColumn(4).setPreferredWidth(55);   // reps
        cols.getColumn(5).setPreferredWidth(360);  // descripción
    }

    private void cargarRutinas() {
        modeloRutinas.clear();
        modeloTabla.setRowCount(0);
        borderTabla.setTitle("Detalle de rutina");
        repaint();
        try {
            List<RutinaDAO.RutinaDetalle> rutinas = new RutinaDAO().listarConDetalle();
            for (RutinaDAO.RutinaDetalle r : rutinas) {
                modeloRutinas.addElement(r);
            }
            if (!rutinas.isEmpty()) {
                listaRutinas.setSelectedIndex(0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error cargando rutinas desde BD:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void mostrarDetalleSeleccionado() {
        modeloTabla.setRowCount(0);
        RutinaDAO.RutinaDetalle r = listaRutinas.getSelectedValue();
        if (r == null) return;

        borderTabla.setTitle("Detalle de rutina  —  #" + r.id + "  •  " + r.nombre);
        tabla.getParent().repaint();

        for (RutinaDAO.ItemDetalle d : r.items) {
            modeloTabla.addRow(new Object[]{
                    d.orden, d.ejercicio, d.categoria, d.series, d.repeticiones, d.descripcion
            });
        }
    }

    private void eliminarSeleccionada() {
        RutinaDAO.RutinaDetalle r = listaRutinas.getSelectedValue();
        if (r == null) return;

        String fecha = (r.fecha != null) ? FMT.format(r.fecha) : "";
        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar la rutina?\n\n" +
                        "ID: #" + r.id + "\n" +
                        "Nombre: " + r.nombre + "\n" +
                        "Usuario: " + r.usuario + "\n" +
                        "Fecha: " + fecha + "\n\n" +
                        "Esta acción es permanente.",
                "Confirmar eliminación",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (op != JOptionPane.OK_OPTION) return;

        try {
            new RutinaDAO().eliminarRutina(r.id);
            JOptionPane.showMessageDialog(this, "Rutina #" + r.id + " eliminada.");
            cargarRutinas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar la rutina:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /** Renderer HTML para mostrar ID, nombre, usuario y fecha con mejor formato */
    private static class RutinaRenderer extends DefaultListCellRenderer {
        private static final DateTimeFormatter FMT_R = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            JLabel lb = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof RutinaDAO.RutinaDetalle r) {
                String fecha = (r.fecha != null) ? FMT_R.format(r.fecha) : "";
                String html = """
                        <html>
                          <div style='padding:4px;'>
                            <span style='font-weight:bold;'>#%d — %s</span><br>
                            <span style='color:gray;'>%s • %s</span>
                          </div>
                        </html>
                        """.formatted(r.id, escape(r.nombre), escape(r.usuario), fecha);
                lb.setText(html);
                lb.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            }
            return lb;
        }

        private static String escape(String s) {
            if (s == null) return "";
            return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
}
