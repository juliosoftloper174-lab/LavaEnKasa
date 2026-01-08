package com.kasa.CapaPresentacion;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class FrmPrincipal extends JFrame {

    private JPanel pnlContenido;

    public FrmPrincipal() {
        configurarVentana();
        initComponentes();
    }

    private void configurarVentana() {
        setTitle("LavaEnKasa - Dashboard");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initComponentes() {
        // --- 1. HEADER (Superior) ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243)); 
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel lblLogo = new JLabel("LavaEnKasa");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 20));

        JLabel lblUsuario = new JLabel("¡Bienvenido de nuevo, Admin!");
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("SansSerif", Font.PLAIN, 14));

        header.add(lblLogo, BorderLayout.WEST);
        header.add(lblUsuario, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- 2. SIDEBAR (Lateral) ---
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(45, 52, 54)); 
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));

        // Declaración de Botones
        JButton btnInicio = crearBotonMenu("Inicio", "🏠");
        JButton btnClientes = crearBotonMenu("Clientes", "👤");
        JButton btnLavadoras = crearBotonMenu("Lavadoras", "🧺");
        JButton btnAlquileres = crearBotonMenu("Alquileres", "💰");
        JButton btnEgresos = crearBotonMenu("Egresos", "📉");

        // Agregar botones al Sidebar
        sidebar.add(btnInicio);
        sidebar.add(btnClientes);
        sidebar.add(btnLavadoras);
        sidebar.add(btnAlquileres);
        sidebar.add(btnEgresos);

        add(sidebar, BorderLayout.WEST);

        // --- 3. CONTENEDOR CENTRAL ---
        pnlContenido = new JPanel(new BorderLayout());
        pnlContenido.setBackground(new Color(245, 246, 250)); 
        add(pnlContenido, BorderLayout.CENTER);

        // --- 4. EVENTOS DE NAVEGACIÓN ---
        btnInicio.addActionListener(e -> {
            pnlContenido.removeAll();
            JLabel lblBienvenida = new JLabel("Seleccione un módulo del menú lateral", JLabel.CENTER);
            lblBienvenida.setFont(new Font("SansSerif", Font.ITALIC, 18));
            pnlContenido.add(lblBienvenida);
            actualizarUI();
        });

        btnClientes.addActionListener(e -> mostrarPanel(new PanelCliente()));
        btnLavadoras.addActionListener(e -> mostrarPanel(new PanelLavadora()));
        btnAlquileres.addActionListener(e -> mostrarPanel(new PanelAlquiler()));
        
        // El panel de egresos lo comentamos hasta que lo tengamos creado
         btnEgresos.addActionListener(e -> mostrarPanel(new PanelEgreso()));
    }

    private JButton crearBotonMenu(String texto, String icono) {
        JButton btn = new JButton(texto + " " + icono);
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(45, 52, 54));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10"); 
        return btn;
    }

    public void mostrarPanel(JPanel panel) {
        pnlContenido.removeAll();
        pnlContenido.add(panel, BorderLayout.CENTER);
        actualizarUI();
    }
    
    private void actualizarUI() {
        pnlContenido.revalidate();
        pnlContenido.repaint();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) { e.printStackTrace(); }
        
        EventQueue.invokeLater(() -> new FrmPrincipal().setVisible(true));
    }
}