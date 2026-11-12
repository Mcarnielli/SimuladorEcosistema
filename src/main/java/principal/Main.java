package principal;

import controlador.ControladorSimulacion;
import dao.SimulacionDAO;
import vista.VentanaPrincipal;

/**
 * Punto de entrada: crea las piezas (vista, DAO y controlador), las conecta y muestra la ventana.
 */
public class Main {

    public static void main(String[] args) {
        // Aspecto "Nimbus" (el mismo que propone NetBeans al crear un JFrame Form)
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            // si no está Nimbus, se usa el aspecto por defecto
        }

        java.awt.EventQueue.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ControladorSimulacion controlador = new ControladorSimulacion(ventana, new SimulacionDAO());
            ventana.setVisible(true);
            controlador.iniciar();
        });
    }
}
