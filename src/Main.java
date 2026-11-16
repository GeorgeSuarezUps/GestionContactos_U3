import controlador.logica_ventana;
import modelo.personaDAO;
import vista.ventana;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            ventana vista = new ventana();
            personaDAO modelo = new personaDAO();
            new logica_ventana(vista, modelo);
            vista.setVisible(true);
        });
    }
}