package vista;

import java.awt.CardLayout;
import java.awt.Color;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controlador.ControladorSimulacion;
import controlador.DatosReporte;
import controlador.DatosSimulacion;
import controlador.VistaSimulacion;

/**
 * Ventana principal de la aplicación (vista).
 *
 * Los componentes se crearon con el diseñador visual de NetBeans (VentanaPrincipal.form).
 * Tiene tres pantallas dentro de un CardLayout: configuración, simulación y reporte.
 *
 * La vista no decide ni calcula nada: cuando la persona hace algo, llama al controlador;
 * y cuando el controlador le pasa datos, los muestra.
 */
public class VentanaPrincipal extends javax.swing.JFrame implements VistaSimulacion {

    private static final Color COLOR_PUEDE_INTERVENIR = new Color(27, 94, 32);
    private static final Color COLOR_NO_PUEDE_INTERVENIR = new Color(110, 110, 110);

    private ControladorSimulacion controlador;

    public VentanaPrincipal() {
        initComponents();
    }

    // =====================================================================
    //  Métodos que usa el controlador (interfaz VistaSimulacion)
    // =====================================================================

    @Override
    public void setControlador(ControladorSimulacion controlador) {
        this.controlador = controlador;
    }

    @Override
    public void cargarOpciones(List<String> climas, List<String> tiposEntidad) {
        cmbClimaInicial.setModel(new DefaultComboBoxModel<>(climas.toArray(new String[0])));
        cmbNuevoClima.setModel(new DefaultComboBoxModel<>(climas.toArray(new String[0])));
        cmbTipoEntidad.setModel(new DefaultComboBoxModel<>(tiposEntidad.toArray(new String[0])));
    }

    @Override
    public void mostrarPantallaConfiguracion(boolean puedeVolverASimulacion) {
        btnCancelarConfig.setVisible(puedeVolverASimulacion);
        lblErroresConfig.setText(" ");
        mostrarTarjeta("configuracion");
        txtPlantas.requestFocusInWindow();
    }

    @Override
    public void mostrarErroresConfiguracion(List<String> errores) {
        if (errores.isEmpty()) {
            lblErroresConfig.setText(" ");
            return;
        }
        StringBuilder html = new StringBuilder("<html><b>Revisá estos datos:</b><ul>");
        for (String error : errores) {
            html.append("<li>").append(error).append("</li>");
        }
        html.append("</ul></html>");
        lblErroresConfig.setText(html.toString());
    }

    @Override
    public void mostrarPantallaSimulacion() {
        mostrarTarjeta("simulacion");
    }

    @Override
    public void actualizarSimulacion(DatosSimulacion datos) {
        lblTurno.setText("Turno: " + datos.getTurno());
        lblClima.setText("Clima: " + datos.getClima());
        lblPlantas.setText("Plantas: " + datos.getPlantas());
        lblConejos.setText("Conejos: " + datos.getConejos());
        lblLobos.setText("Lobos: " + datos.getLobos());

        llenarTabla(tablaEntidades, datos.getFilasEntidades());

        DefaultListModel<String> modeloEventos = new DefaultListModel<>();
        for (String evento : datos.getEventos()) {
            modeloEventos.addElement(evento);
        }
        listaEventos.setModel(modeloEventos);

        btnAvanzarTurno.setEnabled(datos.isPuedeAvanzar());

        boolean intervenir = datos.isPuedeIntervenir();
        cmbNuevoClima.setEnabled(intervenir);
        btnCambiarClima.setEnabled(intervenir);
        cmbTipoEntidad.setEnabled(intervenir);
        txtEnergiaAgregar.setEnabled(intervenir);
        btnAgregarEntidad.setEnabled(intervenir);
        if (!intervenir) {
            txtEnergiaAgregar.setText("");
        }
        lblEstadoIntervencion.setText(datos.getEstadoIntervencion());
        lblEstadoIntervencion.setForeground(intervenir ? COLOR_PUEDE_INTERVENIR : COLOR_NO_PUEDE_INTERVENIR);

        lblMensajeEstado.setText(datos.getMensajeEstado());
    }

    @Override
    public void mostrarPantallaReporte(DatosReporte datos) {
        lblCausaFin.setText(datos.getCausaFin());
        llenarTabla(tablaResumen, datos.getResumen());
        llenarTabla(tablaPoblaciones, datos.getPoblaciones());
        llenarTabla(tablaHistorial, datos.getHistorial());
        llenarTabla(tablaMaxMin, datos.getMaxMin());
        llenarTabla(tablaPeligrosos, datos.getPeligrosos());
        tabsReporte.setSelectedIndex(0);
        mostrarTarjeta("reporte");
    }

    @Override
    public void setReporteDisponible(boolean disponible) {
        btnVerReporte.setEnabled(disponible);
        itemVerReporte.setEnabled(disponible);
    }

    @Override
    public void mostrarMensaje(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public boolean confirmar(String titulo, String mensaje) {
        int opcion = JOptionPane.showConfirmDialog(this, mensaje, titulo,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return opcion == JOptionPane.YES_OPTION;
    }

    // =====================================================================
    //  Auxiliares de la vista (sólo muestran datos)
    // =====================================================================

    /** Cambia de pantalla dentro del CardLayout que se armó en el diseñador. */
    private void mostrarTarjeta(String nombre) {
        CardLayout tarjetas = (CardLayout) panelTarjetas.getLayout();
        tarjetas.show(panelTarjetas, nombre);
    }

    /** Vacía la tabla y le carga las filas recibidas. */
    private void llenarTabla(JTable tabla, List<Object[]> filas) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelTarjetas = new javax.swing.JPanel();
        panelConfiguracion = new javax.swing.JPanel();
        panelEncabezadoConfig = new javax.swing.JPanel();
        lblTituloConfig = new javax.swing.JLabel();
        lblSubtituloConfig = new javax.swing.JLabel();
        panelCentroConfig = new javax.swing.JPanel();
        panelFormulario = new javax.swing.JPanel();
        lblCfgPlantas = new javax.swing.JLabel();
        txtPlantas = new javax.swing.JTextField();
        lblRangoPlantas = new javax.swing.JLabel();
        lblCfgConejos = new javax.swing.JLabel();
        txtConejos = new javax.swing.JTextField();
        lblRangoConejos = new javax.swing.JLabel();
        lblCfgLobos = new javax.swing.JLabel();
        txtLobos = new javax.swing.JTextField();
        lblRangoLobos = new javax.swing.JLabel();
        lblCfgClima = new javax.swing.JLabel();
        cmbClimaInicial = new javax.swing.JComboBox<>();
        lblAyudaClima = new javax.swing.JLabel();
        lblCfgTurnos = new javax.swing.JLabel();
        txtTurnos = new javax.swing.JTextField();
        lblRangoTurnos = new javax.swing.JLabel();
        lblErroresConfig = new javax.swing.JLabel();
        panelBotonesConfig = new javax.swing.JPanel();
        btnCancelarConfig = new javax.swing.JButton();
        btnIniciar = new javax.swing.JButton();
        lblPieConfig = new javax.swing.JLabel();
        panelSimulacion = new javax.swing.JPanel();
        panelContadores = new javax.swing.JPanel();
        lblTurno = new javax.swing.JLabel();
        lblClima = new javax.swing.JLabel();
        lblPlantas = new javax.swing.JLabel();
        lblConejos = new javax.swing.JLabel();
        lblLobos = new javax.swing.JLabel();
        scrollEntidades = new javax.swing.JScrollPane();
        tablaEntidades = new javax.swing.JTable();
        scrollEventos = new javax.swing.JScrollPane();
        listaEventos = new javax.swing.JList<>();
        panelInferior = new javax.swing.JPanel();
        panelIntervencion = new javax.swing.JPanel();
        lblEstadoIntervencion = new javax.swing.JLabel();
        lblNuevoClima = new javax.swing.JLabel();
        cmbNuevoClima = new javax.swing.JComboBox<>();
        btnCambiarClima = new javax.swing.JButton();
        lblTipoEntidad = new javax.swing.JLabel();
        cmbTipoEntidad = new javax.swing.JComboBox<>();
        lblEnergiaAgregar = new javax.swing.JLabel();
        txtEnergiaAgregar = new javax.swing.JTextField();
        btnAgregarEntidad = new javax.swing.JButton();
        lblRellenoIntervencion = new javax.swing.JLabel();
        panelAcciones = new javax.swing.JPanel();
        lblMensajeEstado = new javax.swing.JLabel();
        panelBotonesSimulacion = new javax.swing.JPanel();
        btnVerReporte = new javax.swing.JButton();
        btnAvanzarTurno = new javax.swing.JButton();
        panelReporte = new javax.swing.JPanel();
        panelEncabezadoReporte = new javax.swing.JPanel();
        lblTituloReporte = new javax.swing.JLabel();
        lblCausaFin = new javax.swing.JLabel();
        panelCentroReporte = new javax.swing.JPanel();
        tabsReporte = new javax.swing.JTabbedPane();
        scrollResumen = new javax.swing.JScrollPane();
        tablaResumen = new javax.swing.JTable();
        scrollPoblaciones = new javax.swing.JScrollPane();
        tablaPoblaciones = new javax.swing.JTable();
        panelHistorial = new javax.swing.JPanel();
        scrollHistorial = new javax.swing.JScrollPane();
        tablaHistorial = new javax.swing.JTable();
        scrollMaxMin = new javax.swing.JScrollPane();
        tablaMaxMin = new javax.swing.JTable();
        scrollPeligrosos = new javax.swing.JScrollPane();
        tablaPeligrosos = new javax.swing.JTable();
        panelBotonesReporte = new javax.swing.JPanel();
        btnVolverSimulacion = new javax.swing.JButton();
        btnNuevaDesdeReporte = new javax.swing.JButton();
        barraMenu = new javax.swing.JMenuBar();
        menuSimulacion = new javax.swing.JMenu();
        itemNuevaSimulacion = new javax.swing.JMenuItem();
        itemVerSimulacion = new javax.swing.JMenuItem();
        separadorMenu = new javax.swing.JPopupMenu.Separator();
        itemSalir = new javax.swing.JMenuItem();
        menuReportes = new javax.swing.JMenu();
        itemVerReporte = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Simulador de Ecosistema");
        setMinimumSize(new java.awt.Dimension(1150, 700));
        getContentPane().setLayout(new java.awt.BorderLayout());

        panelTarjetas.setLayout(new java.awt.CardLayout());

        panelConfiguracion.setBackground(new java.awt.Color(244, 247, 242));
        panelConfiguracion.setLayout(new java.awt.BorderLayout());

        panelEncabezadoConfig.setBackground(new java.awt.Color(46, 125, 50));
        panelEncabezadoConfig.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 20, 14, 20));
        panelEncabezadoConfig.setLayout(new java.awt.GridLayout(2, 1, 0, 2));

        lblTituloConfig.setText("Configurar un nuevo ecosistema");
        lblTituloConfig.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTituloConfig.setForeground(new java.awt.Color(255, 255, 255));
        panelEncabezadoConfig.add(lblTituloConfig);

        lblSubtituloConfig.setText("Elegí con cuántas plantas, conejos y lobos empieza la simulación.");
        lblSubtituloConfig.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblSubtituloConfig.setForeground(new java.awt.Color(232, 245, 233));
        panelEncabezadoConfig.add(lblSubtituloConfig);

        panelConfiguracion.add(panelEncabezadoConfig, java.awt.BorderLayout.NORTH);

        panelCentroConfig.setBackground(new java.awt.Color(244, 247, 242));
        panelCentroConfig.setLayout(new java.awt.GridBagLayout());

        panelFormulario.setBackground(new java.awt.Color(255, 255, 255));
        panelFormulario.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos iniciales"));
        panelFormulario.setLayout(new java.awt.GridBagLayout());

        lblCfgPlantas.setText("Plantas iniciales");
        lblCfgPlantas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(lblCfgPlantas, gridBagConstraints);

        txtPlantas.setColumns(8);
        txtPlantas.setText("15");
        txtPlantas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(txtPlantas, gridBagConstraints);

        lblRangoPlantas.setText("Entre 5 y 30");
        lblRangoPlantas.setForeground(new java.awt.Color(110, 110, 110));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(lblRangoPlantas, gridBagConstraints);

        lblCfgConejos.setText("Conejos iniciales");
        lblCfgConejos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(lblCfgConejos, gridBagConstraints);

        txtConejos.setColumns(8);
        txtConejos.setText("8");
        txtConejos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(txtConejos, gridBagConstraints);

        lblRangoConejos.setText("Entre 2 y 15");
        lblRangoConejos.setForeground(new java.awt.Color(110, 110, 110));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(lblRangoConejos, gridBagConstraints);

        lblCfgLobos.setText("Lobos iniciales");
        lblCfgLobos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(lblCfgLobos, gridBagConstraints);

        txtLobos.setColumns(8);
        txtLobos.setText("3");
        txtLobos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(txtLobos, gridBagConstraints);

        lblRangoLobos.setText("Entre 1 y 5");
        lblRangoLobos.setForeground(new java.awt.Color(110, 110, 110));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(lblRangoLobos, gridBagConstraints);

        lblCfgClima.setText("Clima inicial");
        lblCfgClima.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(lblCfgClima, gridBagConstraints);

        cmbClimaInicial.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(cmbClimaInicial, gridBagConstraints);

        lblAyudaClima.setText("Afecta a plantas, conejos y lobos");
        lblAyudaClima.setForeground(new java.awt.Color(110, 110, 110));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(lblAyudaClima, gridBagConstraints);

        lblCfgTurnos.setText("Turnos de la simulación");
        lblCfgTurnos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(lblCfgTurnos, gridBagConstraints);

        txtTurnos.setColumns(8);
        txtTurnos.setText("20");
        txtTurnos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(txtTurnos, gridBagConstraints);

        lblRangoTurnos.setText("Entre 10 y 50");
        lblRangoTurnos.setForeground(new java.awt.Color(110, 110, 110));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(lblRangoTurnos, gridBagConstraints);

        lblErroresConfig.setText(" ");
        lblErroresConfig.setForeground(new java.awt.Color(183, 28, 28));
        lblErroresConfig.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelFormulario.add(lblErroresConfig, gridBagConstraints);

        panelBotonesConfig.setOpaque(false);
        panelBotonesConfig.setLayout(new java.awt.FlowLayout(2, 10, 5));

        btnCancelarConfig.setText("Volver a la simulación en curso");
        btnCancelarConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarConfigActionPerformed(evt);
            }
        });
        panelBotonesConfig.add(btnCancelarConfig);

        btnIniciar.setText("Iniciar simulación");
        btnIniciar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarActionPerformed(evt);
            }
        });
        panelBotonesConfig.add(btnIniciar);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(14, 8, 6, 8);
        panelFormulario.add(panelBotonesConfig, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panelCentroConfig.add(panelFormulario, gridBagConstraints);

        panelConfiguracion.add(panelCentroConfig, java.awt.BorderLayout.CENTER);

        lblPieConfig.setText("El avance se guarda automáticamente en la carpeta \"datos\" del proyecto.");
        lblPieConfig.setForeground(new java.awt.Color(110, 110, 110));
        lblPieConfig.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 20, 10, 20));
        panelConfiguracion.add(lblPieConfig, java.awt.BorderLayout.SOUTH);

        panelTarjetas.add(panelConfiguracion, "configuracion");

        panelSimulacion.setBackground(new java.awt.Color(244, 247, 242));
        panelSimulacion.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 10, 10));
        panelSimulacion.setLayout(new java.awt.BorderLayout(10, 10));

        panelContadores.setBackground(new java.awt.Color(244, 247, 242));
        panelContadores.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 6, 10));
        panelContadores.setLayout(new java.awt.GridLayout(1, 5, 10, 0));

        lblTurno.setText("Turno: -");
        lblTurno.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblTurno.setBackground(new java.awt.Color(255, 255, 255));
        lblTurno.setOpaque(true);
        lblTurno.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        lblTurno.setForeground(new java.awt.Color(33, 33, 33));
        panelContadores.add(lblTurno);

        lblClima.setText("Clima: -");
        lblClima.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblClima.setBackground(new java.awt.Color(255, 255, 255));
        lblClima.setOpaque(true);
        lblClima.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        lblClima.setForeground(new java.awt.Color(33, 33, 33));
        panelContadores.add(lblClima);

        lblPlantas.setText("Plantas: 0");
        lblPlantas.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblPlantas.setBackground(new java.awt.Color(255, 255, 255));
        lblPlantas.setOpaque(true);
        lblPlantas.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        lblPlantas.setForeground(new java.awt.Color(33, 33, 33));
        lblPlantas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/planta.png"))); // NOI18N
        panelContadores.add(lblPlantas);

        lblConejos.setText("Conejos: 0");
        lblConejos.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblConejos.setBackground(new java.awt.Color(255, 255, 255));
        lblConejos.setOpaque(true);
        lblConejos.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        lblConejos.setForeground(new java.awt.Color(33, 33, 33));
        lblConejos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/conejo.png"))); // NOI18N
        panelContadores.add(lblConejos);

        lblLobos.setText("Lobos: 0");
        lblLobos.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblLobos.setBackground(new java.awt.Color(255, 255, 255));
        lblLobos.setOpaque(true);
        lblLobos.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        lblLobos.setForeground(new java.awt.Color(33, 33, 33));
        lblLobos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/lobo.png"))); // NOI18N
        panelContadores.add(lblLobos);

        panelSimulacion.add(panelContadores, java.awt.BorderLayout.NORTH);

        scrollEntidades.setBorder(javax.swing.BorderFactory.createTitledBorder("Entidades vivas"));

        tablaEntidades.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tipo", "Nombre", "Energía", "Edad", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaEntidades.setFillsViewportHeight(true);
        tablaEntidades.setRowHeight(24);
        scrollEntidades.setViewportView(tablaEntidades);

        panelSimulacion.add(scrollEntidades, java.awt.BorderLayout.CENTER);

        scrollEventos.setBorder(javax.swing.BorderFactory.createTitledBorder("Eventos del último turno"));
        scrollEventos.setPreferredSize(new java.awt.Dimension(470, 300));

        listaEventos.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        scrollEventos.setViewportView(listaEventos);

        panelSimulacion.add(scrollEventos, java.awt.BorderLayout.EAST);

        panelInferior.setOpaque(false);
        panelInferior.setLayout(new java.awt.BorderLayout(0, 6));

        panelIntervencion.setBackground(new java.awt.Color(255, 255, 255));
        panelIntervencion.setBorder(javax.swing.BorderFactory.createTitledBorder("Intervención del jugador (cada 3 turnos)"));
        panelIntervencion.setLayout(new java.awt.GridBagLayout());

        lblEstadoIntervencion.setText("La intervención se habilita cada 3 turnos.");
        lblEstadoIntervencion.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 8, 8);
        panelIntervencion.add(lblEstadoIntervencion, gridBagConstraints);

        lblNuevoClima.setText("Nuevo clima:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelIntervencion.add(lblNuevoClima, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelIntervencion.add(cmbNuevoClima, gridBagConstraints);

        btnCambiarClima.setText("Cambiar clima");
        btnCambiarClima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarClimaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelIntervencion.add(btnCambiarClima, gridBagConstraints);

        lblTipoEntidad.setText("Agregar:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 30, 6, 8);
        panelIntervencion.add(lblTipoEntidad, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelIntervencion.add(cmbTipoEntidad, gridBagConstraints);

        lblEnergiaAgregar.setText("Energía (opcional, 1-100):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelIntervencion.add(lblEnergiaAgregar, gridBagConstraints);

        txtEnergiaAgregar.setColumns(4);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelIntervencion.add(txtEnergiaAgregar, gridBagConstraints);

        btnAgregarEntidad.setText("Agregar entidad");
        btnAgregarEntidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarEntidadActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelIntervencion.add(btnAgregarEntidad, gridBagConstraints);

        lblRellenoIntervencion.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 8);
        panelIntervencion.add(lblRellenoIntervencion, gridBagConstraints);

        panelInferior.add(panelIntervencion, java.awt.BorderLayout.CENTER);

        panelAcciones.setOpaque(false);
        panelAcciones.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 4, 0, 4));
        panelAcciones.setLayout(new java.awt.BorderLayout(10, 0));

        lblMensajeEstado.setText(" ");
        lblMensajeEstado.setForeground(new java.awt.Color(110, 110, 110));
        panelAcciones.add(lblMensajeEstado, java.awt.BorderLayout.CENTER);

        panelBotonesSimulacion.setOpaque(false);
        panelBotonesSimulacion.setLayout(new java.awt.FlowLayout(2, 10, 5));

        btnVerReporte.setText("Ver reporte final");
        btnVerReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerReporteActionPerformed(evt);
            }
        });
        panelBotonesSimulacion.add(btnVerReporte);

        btnAvanzarTurno.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        btnAvanzarTurno.setText("Avanzar turno  ");
        btnAvanzarTurno.setToolTipText("");
        btnAvanzarTurno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAvanzarTurnoActionPerformed(evt);
            }
        });
        panelBotonesSimulacion.add(btnAvanzarTurno);

        panelAcciones.add(panelBotonesSimulacion, java.awt.BorderLayout.EAST);

        panelInferior.add(panelAcciones, java.awt.BorderLayout.SOUTH);

        panelSimulacion.add(panelInferior, java.awt.BorderLayout.SOUTH);

        panelTarjetas.add(panelSimulacion, "simulacion");

        panelReporte.setBackground(new java.awt.Color(244, 247, 242));
        panelReporte.setLayout(new java.awt.BorderLayout(0, 10));

        panelEncabezadoReporte.setBackground(new java.awt.Color(46, 125, 50));
        panelEncabezadoReporte.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 20, 14, 20));
        panelEncabezadoReporte.setLayout(new java.awt.GridLayout(2, 1, 0, 2));

        lblTituloReporte.setText("Reporte final de la simulación");
        lblTituloReporte.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTituloReporte.setForeground(new java.awt.Color(255, 255, 255));
        panelEncabezadoReporte.add(lblTituloReporte);

        lblCausaFin.setText(" ");
        lblCausaFin.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblCausaFin.setForeground(new java.awt.Color(232, 245, 233));
        panelEncabezadoReporte.add(lblCausaFin);

        panelReporte.add(panelEncabezadoReporte, java.awt.BorderLayout.NORTH);

        panelCentroReporte.setOpaque(false);
        panelCentroReporte.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
        panelCentroReporte.setLayout(new java.awt.BorderLayout());

        tabsReporte.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        tablaResumen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Dato", "Valor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaResumen.setFillsViewportHeight(true);
        tablaResumen.setRowHeight(24);
        scrollResumen.setViewportView(tablaResumen);

        tabsReporte.addTab("Resumen", scrollResumen);

        tablaPoblaciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Población", "Iniciales", "Nacimientos", "Agregados por el jugador", "Muertes", "Finales"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaPoblaciones.setFillsViewportHeight(true);
        tablaPoblaciones.setRowHeight(24);
        scrollPoblaciones.setViewportView(tablaPoblaciones);

        tabsReporte.addTab("Nacimientos y muertes", scrollPoblaciones);

        panelHistorial.setLayout(new java.awt.BorderLayout(0, 8));

        scrollHistorial.setBorder(javax.swing.BorderFactory.createTitledBorder("Población turno a turno"));

        tablaHistorial.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Turno", "Plantas", "Conejos", "Lobos"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaHistorial.setFillsViewportHeight(true);
        tablaHistorial.setRowHeight(24);
        scrollHistorial.setViewportView(tablaHistorial);

        panelHistorial.add(scrollHistorial, java.awt.BorderLayout.CENTER);

        scrollMaxMin.setBorder(javax.swing.BorderFactory.createTitledBorder("Máximos y mínimos"));
        scrollMaxMin.setPreferredSize(new java.awt.Dimension(400, 130));

        tablaMaxMin.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Población", "Máximo", "Turno del máximo", "Mínimo", "Turno del mínimo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaMaxMin.setFillsViewportHeight(true);
        tablaMaxMin.setRowHeight(24);
        scrollMaxMin.setViewportView(tablaMaxMin);

        panelHistorial.add(scrollMaxMin, java.awt.BorderLayout.SOUTH);

        tabsReporte.addTab("Historial", panelHistorial);

        tablaPeligrosos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nivel de peligro", "Entidad", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaPeligrosos.setFillsViewportHeight(true);
        tablaPeligrosos.setRowHeight(24);
        scrollPeligrosos.setViewportView(tablaPeligrosos);

        tabsReporte.addTab("Peligrosos", scrollPeligrosos);

        panelCentroReporte.add(tabsReporte, java.awt.BorderLayout.CENTER);

        panelReporte.add(panelCentroReporte, java.awt.BorderLayout.CENTER);

        panelBotonesReporte.setOpaque(false);
        panelBotonesReporte.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 10, 10));
        panelBotonesReporte.setLayout(new java.awt.FlowLayout(2, 10, 5));

        btnVolverSimulacion.setText("Ver simulación");
        btnVolverSimulacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverSimulacionActionPerformed(evt);
            }
        });
        panelBotonesReporte.add(btnVolverSimulacion);

        btnNuevaDesdeReporte.setText("Nueva simulación");
        btnNuevaDesdeReporte.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnNuevaDesdeReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaDesdeReporteActionPerformed(evt);
            }
        });
        panelBotonesReporte.add(btnNuevaDesdeReporte);

        panelReporte.add(panelBotonesReporte, java.awt.BorderLayout.SOUTH);

        panelTarjetas.add(panelReporte, "reporte");

        getContentPane().add(panelTarjetas, java.awt.BorderLayout.CENTER);

        menuSimulacion.setText("Simulación");

        itemNuevaSimulacion.setText("Nueva simulación");
        itemNuevaSimulacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemNuevaSimulacionActionPerformed(evt);
            }
        });
        menuSimulacion.add(itemNuevaSimulacion);

        itemVerSimulacion.setText("Ver simulación en curso");
        itemVerSimulacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemVerSimulacionActionPerformed(evt);
            }
        });
        menuSimulacion.add(itemVerSimulacion);
        menuSimulacion.add(separadorMenu);

        itemSalir.setText("Salir");
        itemSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemSalirActionPerformed(evt);
            }
        });
        menuSimulacion.add(itemSalir);

        barraMenu.add(menuSimulacion);

        menuReportes.setText("Reportes");

        itemVerReporte.setText("Ver reporte final");
        itemVerReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemVerReporteActionPerformed(evt);
            }
        });
        menuReportes.add(itemVerReporte);

        barraMenu.add(menuReportes);

        setJMenuBar(barraMenu);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void itemNuevaSimulacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemNuevaSimulacionActionPerformed
        controlador.nuevaSimulacion();
    }//GEN-LAST:event_itemNuevaSimulacionActionPerformed

    private void itemVerSimulacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemVerSimulacionActionPerformed
        controlador.verSimulacion();
    }//GEN-LAST:event_itemVerSimulacionActionPerformed

    private void itemSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSalirActionPerformed
        dispose();
    }//GEN-LAST:event_itemSalirActionPerformed

    private void itemVerReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemVerReporteActionPerformed
        controlador.verReporte();
    }//GEN-LAST:event_itemVerReporteActionPerformed

    private void btnCancelarConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarConfigActionPerformed
        controlador.cancelarConfiguracion();
    }//GEN-LAST:event_btnCancelarConfigActionPerformed

    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarActionPerformed
        // La vista sólo junta lo que escribió la persona y se lo pasa al controlador,
        // que es el que valida (campos vacíos, formato y rangos).
        controlador.iniciarSimulacion(txtPlantas.getText(), txtConejos.getText(), txtLobos.getText(),
                (String) cmbClimaInicial.getSelectedItem(), txtTurnos.getText());
    }//GEN-LAST:event_btnIniciarActionPerformed

    private void btnCambiarClimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarClimaActionPerformed
        controlador.cambiarClima((String) cmbNuevoClima.getSelectedItem());
    }//GEN-LAST:event_btnCambiarClimaActionPerformed

    private void btnAgregarEntidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarEntidadActionPerformed
        controlador.agregarEntidad((String) cmbTipoEntidad.getSelectedItem(), txtEnergiaAgregar.getText());
    }//GEN-LAST:event_btnAgregarEntidadActionPerformed

    private void btnVerReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerReporteActionPerformed
        controlador.verReporte();
    }//GEN-LAST:event_btnVerReporteActionPerformed

    private void btnAvanzarTurnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAvanzarTurnoActionPerformed
        controlador.avanzarTurno();
    }//GEN-LAST:event_btnAvanzarTurnoActionPerformed

    private void btnVolverSimulacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverSimulacionActionPerformed
        controlador.verSimulacion();
    }//GEN-LAST:event_btnVolverSimulacionActionPerformed

    private void btnNuevaDesdeReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaDesdeReporteActionPerformed
        controlador.nuevaSimulacion();
    }//GEN-LAST:event_btnNuevaDesdeReporteActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar barraMenu;
    private javax.swing.JButton btnAgregarEntidad;
    private javax.swing.JButton btnAvanzarTurno;
    private javax.swing.JButton btnCambiarClima;
    private javax.swing.JButton btnCancelarConfig;
    private javax.swing.JButton btnIniciar;
    private javax.swing.JButton btnNuevaDesdeReporte;
    private javax.swing.JButton btnVerReporte;
    private javax.swing.JButton btnVolverSimulacion;
    private javax.swing.JComboBox<String> cmbClimaInicial;
    private javax.swing.JComboBox<String> cmbNuevoClima;
    private javax.swing.JComboBox<String> cmbTipoEntidad;
    private javax.swing.JMenuItem itemNuevaSimulacion;
    private javax.swing.JMenuItem itemSalir;
    private javax.swing.JMenuItem itemVerReporte;
    private javax.swing.JMenuItem itemVerSimulacion;
    private javax.swing.JLabel lblAyudaClima;
    private javax.swing.JLabel lblCausaFin;
    private javax.swing.JLabel lblCfgClima;
    private javax.swing.JLabel lblCfgConejos;
    private javax.swing.JLabel lblCfgLobos;
    private javax.swing.JLabel lblCfgPlantas;
    private javax.swing.JLabel lblCfgTurnos;
    private javax.swing.JLabel lblClima;
    private javax.swing.JLabel lblConejos;
    private javax.swing.JLabel lblEnergiaAgregar;
    private javax.swing.JLabel lblErroresConfig;
    private javax.swing.JLabel lblEstadoIntervencion;
    private javax.swing.JLabel lblLobos;
    private javax.swing.JLabel lblMensajeEstado;
    private javax.swing.JLabel lblNuevoClima;
    private javax.swing.JLabel lblPieConfig;
    private javax.swing.JLabel lblPlantas;
    private javax.swing.JLabel lblRangoConejos;
    private javax.swing.JLabel lblRangoLobos;
    private javax.swing.JLabel lblRangoPlantas;
    private javax.swing.JLabel lblRangoTurnos;
    private javax.swing.JLabel lblRellenoIntervencion;
    private javax.swing.JLabel lblSubtituloConfig;
    private javax.swing.JLabel lblTipoEntidad;
    private javax.swing.JLabel lblTituloConfig;
    private javax.swing.JLabel lblTituloReporte;
    private javax.swing.JLabel lblTurno;
    private javax.swing.JList<String> listaEventos;
    private javax.swing.JMenu menuReportes;
    private javax.swing.JMenu menuSimulacion;
    private javax.swing.JPanel panelAcciones;
    private javax.swing.JPanel panelBotonesConfig;
    private javax.swing.JPanel panelBotonesReporte;
    private javax.swing.JPanel panelBotonesSimulacion;
    private javax.swing.JPanel panelCentroConfig;
    private javax.swing.JPanel panelCentroReporte;
    private javax.swing.JPanel panelConfiguracion;
    private javax.swing.JPanel panelContadores;
    private javax.swing.JPanel panelEncabezadoConfig;
    private javax.swing.JPanel panelEncabezadoReporte;
    private javax.swing.JPanel panelFormulario;
    private javax.swing.JPanel panelHistorial;
    private javax.swing.JPanel panelInferior;
    private javax.swing.JPanel panelIntervencion;
    private javax.swing.JPanel panelReporte;
    private javax.swing.JPanel panelSimulacion;
    private javax.swing.JPanel panelTarjetas;
    private javax.swing.JScrollPane scrollEntidades;
    private javax.swing.JScrollPane scrollEventos;
    private javax.swing.JScrollPane scrollHistorial;
    private javax.swing.JScrollPane scrollMaxMin;
    private javax.swing.JScrollPane scrollPeligrosos;
    private javax.swing.JScrollPane scrollPoblaciones;
    private javax.swing.JScrollPane scrollResumen;
    private javax.swing.JPopupMenu.Separator separadorMenu;
    private javax.swing.JTable tablaEntidades;
    private javax.swing.JTable tablaHistorial;
    private javax.swing.JTable tablaMaxMin;
    private javax.swing.JTable tablaPeligrosos;
    private javax.swing.JTable tablaPoblaciones;
    private javax.swing.JTable tablaResumen;
    private javax.swing.JTabbedPane tabsReporte;
    private javax.swing.JTextField txtConejos;
    private javax.swing.JTextField txtEnergiaAgregar;
    private javax.swing.JTextField txtLobos;
    private javax.swing.JTextField txtPlantas;
    private javax.swing.JTextField txtTurnos;
    // End of variables declaration//GEN-END:variables
}
