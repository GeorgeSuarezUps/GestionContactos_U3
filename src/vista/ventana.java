package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.ResourceBundle;

public class ventana extends JFrame {
    public static final Color COLOR_PRIMARIO = new Color(30, 58, 138);
    public static final Color COLOR_SECUNDARIO = new Color(37, 99, 235);
    public static final Color COLOR_FONDO = new Color(248, 250, 252);
    public static final Color COLOR_EXITO = new Color(22, 163, 74);
    public static final Color COLOR_ALERTA = new Color(220, 38, 38);

    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_NEGRITA = new Font("Segoe UI", Font.BOLD, 12);

    private JTabbedPane tabbedPane;
    private JLabel lblTituloApp, lblIdioma, lblNombre, lblTelefono, lblEmail, lblCategoria, lblBuscar;
    private JLabel lblNotificacion, lblEstadisticasTotal;
    private JComboBox<String> cmbIdioma, cmbCategoria;
    private JTextField txtNombre, txtTelefono, txtEmail, txtBuscar;
    private JButton btnGuardar, btnEditar, btnActualizar, btnEliminar, btnExportar, btnSimularCarga;
    private JTable tablaContactos;
    private DefaultTableModel tableModel;
    private JProgressBar progressBar;
    private JPopupMenu popupMenu;
    private JMenuItem menuEliminar;

    private ResourceBundle bundle;

    public ventana() {
        bundle = ResourceBundle.getBundle("recursos.mensajes", new Locale("es", "EC"));
        inicializarComponentes();
        aplicarIdioma(new Locale("es", "EC"));
    }

    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COLOR_FONDO);

        // --- HEADER ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(COLOR_PRIMARIO);
        panelHeader.setBorder(new EmptyBorder(12, 20, 12, 20));

        lblTituloApp = new JLabel();
        lblTituloApp.setFont(FUENTE_TITULO);
        lblTituloApp.setForeground(Color.WHITE);
        lblTituloApp.setIcon(crearIconoVectorial(Color.WHITE, "APP"));
        lblTituloApp.setIconTextGap(10);
        panelHeader.add(lblTituloApp, BorderLayout.WEST);

        JPanel panelLang = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelLang.setOpaque(false);
        lblIdioma = new JLabel();
        lblIdioma.setFont(FUENTE_NEGRITA);
        lblIdioma.setForeground(Color.WHITE);
        lblIdioma.setIcon(crearIconoVectorial(Color.WHITE, "LANG"));
        cmbIdioma = new JComboBox<>(new String[]{"Español (ES)", "English (EN)", "Português (PT)"});
        cmbIdioma.setFont(FUENTE_NORMAL);
        panelLang.add(lblIdioma);
        panelLang.add(cmbIdioma);
        panelHeader.add(panelLang, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);

        // --- PESTAÑAS ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FUENTE_NEGRITA);

        JPanel panelContactos = new JPanel(new BorderLayout(10, 10));
        panelContactos.setBorder(new EmptyBorder(10, 15, 10, 15));
        panelContactos.setBackground(COLOR_FONDO);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)), "Datos del Contacto"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblNombre = new JLabel(); lblNombre.setFont(FUENTE_NEGRITA);
        txtNombre = new JTextField(15); txtNombre.setFont(FUENTE_NORMAL);
        lblTelefono = new JLabel(); lblTelefono.setFont(FUENTE_NEGRITA);
        txtTelefono = new JTextField(12); txtTelefono.setFont(FUENTE_NORMAL);
        lblEmail = new JLabel(); lblEmail.setFont(FUENTE_NEGRITA);
        txtEmail = new JTextField(15); txtEmail.setFont(FUENTE_NORMAL);
        lblCategoria = new JLabel(); lblCategoria.setFont(FUENTE_NEGRITA);
        cmbCategoria = new JComboBox<>(); cmbCategoria.setFont(FUENTE_NORMAL);

        gbc.gridx = 0; gbc.gridy = 0; panelFormulario.add(lblNombre, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; panelFormulario.add(txtNombre, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; panelFormulario.add(lblTelefono, gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0; panelFormulario.add(txtTelefono, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; panelFormulario.add(lblEmail, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; panelFormulario.add(txtEmail, gbc);
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0; panelFormulario.add(lblCategoria, gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 1.0; panelFormulario.add(cmbCategoria, gbc);

        // Panel de Botones con color sólido garantizado
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelBotones.setOpaque(false);
        btnGuardar = crearBotonEstilizado(COLOR_SECUNDARIO, "ADD");
        btnEditar = crearBotonEstilizado(new Color(217, 119, 6), "EDIT");
        btnActualizar = crearBotonEstilizado(COLOR_EXITO, "OK");
        btnActualizar.setEnabled(false);
        btnEliminar = crearBotonEstilizado(COLOR_ALERTA, "DEL");
        btnExportar = crearBotonEstilizado(new Color(15, 118, 110), "EXP");
        btnSimularCarga = crearBotonEstilizado(new Color(71, 85, 105), "TEST");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnExportar);
        panelBotones.add(btnSimularCarga);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        panelFormulario.add(panelBotones, gbc);

        panelContactos.add(panelFormulario, BorderLayout.NORTH);

        // Tabla y Búsqueda
        JPanel panelTablaContenedor = new JPanel(new BorderLayout(5, 8));
        panelTablaContenedor.setOpaque(false);

        JPanel panelBusqueda = new JPanel(new BorderLayout(8, 0));
        panelBusqueda.setOpaque(false);
        lblBuscar = new JLabel(); lblBuscar.setFont(FUENTE_NEGRITA);
        lblBuscar.setIcon(crearIconoVectorial(COLOR_PRIMARIO, "SEARCH"));
        txtBuscar = new JTextField(); txtBuscar.setFont(FUENTE_NORMAL);
        panelBusqueda.add(lblBuscar, BorderLayout.WEST);
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelTablaContenedor.add(panelBusqueda, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"", "", "", "", ""}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaContactos = new JTable(tableModel);
        tablaContactos.setFont(FUENTE_NORMAL);
        tablaContactos.setRowHeight(26);
        tablaContactos.getTableHeader().setFont(FUENTE_NEGRITA);
        tablaContactos.getTableHeader().setBackground(new Color(226, 232, 240));

        popupMenu = new JPopupMenu();
        menuEliminar = new JMenuItem();
        popupMenu.add(menuEliminar);
        tablaContactos.setComponentPopupMenu(popupMenu);

        panelTablaContenedor.add(new JScrollPane(tablaContactos), BorderLayout.CENTER);
        panelContactos.add(panelTablaContenedor, BorderLayout.CENTER);

        // Pestaña Estadísticas
        JPanel panelEstadisticas = new JPanel(new BorderLayout());
        panelEstadisticas.setBackground(Color.WHITE);
        panelEstadisticas.setBorder(new EmptyBorder(30, 30, 30, 30));
        lblEstadisticasTotal = new JLabel();
        lblEstadisticasTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panelEstadisticas.add(lblEstadisticasTotal, BorderLayout.NORTH);

        tabbedPane.addTab("", crearIconoVectorial(COLOR_PRIMARIO, "USERS"), panelContactos);
        tabbedPane.addTab("", crearIconoVectorial(COLOR_PRIMARIO, "STATS"), panelEstadisticas);
        add(tabbedPane, BorderLayout.CENTER);

        // Footer
        JPanel panelFooter = new JPanel(new BorderLayout(10, 0));
        panelFooter.setBackground(new Color(241, 245, 249));
        panelFooter.setBorder(new EmptyBorder(8, 15, 8, 15));

        lblNotificacion = new JLabel(" ");
        lblNotificacion.setFont(FUENTE_NEGRITA);
        panelFooter.add(lblNotificacion, BorderLayout.WEST);

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(200, 18));
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        panelFooter.add(progressBar, BorderLayout.EAST);

        add(panelFooter, BorderLayout.SOUTH);

        getRootPane().registerKeyboardAction(e -> txtNombre.requestFocus(),
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> btnExportar.doClick(),
                KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private JButton crearBotonEstilizado(Color bg, String tipoIcono) {
        JButton btn = new JButton();
        btn.setFont(FUENTE_NEGRITA);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(6, 12, 6, 12));
        btn.setIcon(crearIconoVectorial(Color.WHITE, tipoIcono));
        btn.setIconTextGap(6);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Generador de iconos vectoriales nativos (Unidad 2: Iconografía)
    private Icon crearIconoVectorial(Color color, String tipo) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f));
                switch (tipo) {
                    case "ADD" -> { g2.drawLine(x + 6, y + 2, x + 6, y + 10); g2.drawLine(x + 2, y + 6, x + 10, y + 6); }
                    case "EDIT" -> { g2.drawRect(x + 2, y + 2, 8, 8); g2.drawLine(x + 2, y + 10, x + 10, y + 2); }
                    case "OK" -> { g2.drawLine(x + 2, y + 6, x + 5, y + 10); g2.drawLine(x + 5, y + 10, x + 11, y + 2); }
                    case "DEL" -> { g2.drawLine(x + 2, y + 2, x + 10, y + 10); g2.drawLine(x + 10, y + 2, x + 2, y + 10); }
                    case "EXP" -> { g2.drawRect(x + 2, y + 4, 8, 7); g2.drawLine(x + 6, y + 1, x + 6, y + 7); }
                    case "TEST" -> { g2.fillOval(x + 3, y + 3, 6, 6); }
                    case "SEARCH" -> { g2.drawOval(x + 1, y + 1, 7, 7); g2.drawLine(x + 7, y + 7, x + 11, y + 11); }
                    case "LANG" -> { g2.drawOval(x + 1, y + 1, 10, 10); g2.drawLine(x + 1, y + 6, x + 11, y + 6); }
                    default -> { g2.fillRoundRect(x + 2, y + 2, 9, 9, 3, 3); }
                }
                g2.dispose();
            }
            @Override public int getIconWidth() { return 13; }
            @Override public int getIconHeight() { return 13; }
        };
    }

    public void aplicarIdioma(Locale locale) {
        bundle = ResourceBundle.getBundle("recursos.mensajes", locale);
        setTitle(bundle.getString("app.title"));
        lblTituloApp.setText(bundle.getString("app.title"));
        lblIdioma.setText(bundle.getString("lbl.language"));

        tabbedPane.setTitleAt(0, bundle.getString("tab.contacts"));
        tabbedPane.setTitleAt(1, bundle.getString("tab.stats"));

        lblNombre.setText(bundle.getString("lbl.name"));
        lblTelefono.setText(bundle.getString("lbl.phone"));
        lblEmail.setText(bundle.getString("lbl.email"));
        lblCategoria.setText(bundle.getString("lbl.category"));
        lblBuscar.setText(bundle.getString("lbl.search"));

        btnGuardar.setText(bundle.getString("btn.add"));
        btnEditar.setText(bundle.getString("btn.edit"));
        btnActualizar.setText(bundle.getString("btn.update"));
        btnEliminar.setText(bundle.getString("btn.delete"));
        btnExportar.setText(bundle.getString("btn.export"));
        btnSimularCarga.setText(bundle.getString("btn.loadtest"));
        menuEliminar.setText(bundle.getString("btn.delete"));

        cmbCategoria.removeAllItems();
        cmbCategoria.addItem(bundle.getString("cat.family"));
        cmbCategoria.addItem(bundle.getString("cat.work"));
        cmbCategoria.addItem(bundle.getString("cat.friends"));

        tableModel.setColumnIdentifiers(new Object[]{
                bundle.getString("col.name"),
                bundle.getString("col.phone"),
                bundle.getString("col.email"),
                bundle.getString("col.category"),
                bundle.getString("col.date")
        });
    }

    public ResourceBundle getBundle() { return bundle; }
    public JComboBox<String> getCmbIdioma() { return cmbIdioma; }
    public JComboBox<String> getCmbCategoria() { return cmbCategoria; }
    public JTextField getTxtNombre() { return txtNombre; }
    public JTextField getTxtTelefono() { return txtTelefono; }
    public JTextField getTxtEmail() { return txtEmail; }
    public JTextField getTxtBuscar() { return txtBuscar; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnActualizar() { return btnActualizar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnExportar() { return btnExportar; }
    public JButton getBtnSimularCarga() { return btnSimularCarga; }
    public JTable getTablaContactos() { return tablaContactos; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JLabel getLblNotificacion() { return lblNotificacion; }
    public JLabel getLblEstadisticasTotal() { return lblEstadisticasTotal; }
    public JProgressBar getProgressBar() { return progressBar; }
    public JMenuItem getMenuEliminar() { return menuEliminar; }
}