import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import interfaz.*;


public class Main {
    public static void main(String[] args) {

        // 1) Intentar FlatLaf (moderno). Requiere flatlaf-*.jar en el classpath.
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Throwable t) {
            // 2) Si no está el jar o falla, usar el Look&Feel del sistema
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignore) { /* nada */ }
        }

        // 3) Arrancar la app en el EDT
        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}
