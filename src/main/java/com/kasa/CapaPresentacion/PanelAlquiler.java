package com.kasa.CapaPresentacion;

import com.formdev.flatlaf.FlatClientProperties;
import com.kasa.CapaEntidad.*;
import com.kasa.CapaNegocio.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class PanelAlquiler extends JPanel {

    private AlquilerNegocio negocio = new AlquilerNegocio();
    private ClienteNegocio clienteNeg = new ClienteNegocio();
    private LavadoraNegocio lavadoraNeg = new LavadoraNegocio();

    // Componentes del Formulario
    private JComboBox<Cliente> cbCliente;
    private JComboBox<Lavadora> cbLavadora;
    private JComboBox<String> cbTipoCobro;
    private JSpinner spHoras;
    private JComboBox<String> cbPromocion;
    private JLabel lblTotal;
    private JButton btnRegistrar;

    // Componentes del Historial
    private JSpinner spAño;
    private JPanel pnlMeses;
    private JTable tablaAlquileres;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalMes;
    private int mesSeleccionado = -1;

    public PanelAlquiler() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Crear pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "tabHeight: 40");
        
        // Pestaña 1: Registrar Alquiler
        JPanel panelRegistro = crearPanelRegistro();
        tabbedPane.addTab("  Registrar Alquiler  ", panelRegistro);
        
        // Pestaña 2: Historial
        JPanel panelHistorial = crearPanelHistorial();
        tabbedPane.addTab("  Historial de Alquileres  ", panelHistorial);

        add(tabbedPane, BorderLayout.CENTER);
        
        cargarCombos();
    }

    // ===================== PANEL DE REGISTRO =====================
    private JPanel crearPanelRegistro() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel lblTitulo = new JLabel("Registrar Nuevo Alquiler");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // Formulario
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // ComboBox Cliente con Renderer
        cbCliente = new JComboBox<>();
        cbCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente) {
                    setText(((Cliente) value).getNombre());
                }
                return this;
            }
        });

        // ComboBox Lavadora con Renderer
        cbLavadora = new JComboBox<>();
        cbLavadora.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Lavadora) {
                    Lavadora l = (Lavadora) value;
                    setText(l.getNombre() + " (" + l.getEstado() + ")");
                }
                return this;
            }
        });

        // Tipo de Cobro
        cbTipoCobro = new JComboBox<>(new String[]{"horas", "promocion"});
        cbTipoCobro.addActionListener(e -> {
            actualizarEstadoCampos();
            calcularTotalPrevio();
        });

        // Spinner Horas
        spHoras = new JSpinner(new SpinnerNumberModel(3, 3, 13, 1));
        spHoras.addChangeListener(e -> calcularTotalPrevio());

        // ComboBox Promoción
        cbPromocion = new JComboBox<>(new String[]{"diurna", "nocturna"});
        cbPromocion.addActionListener(e -> calcularTotalPrevio());

        // Label Total
        lblTotal = new JLabel("TOTAL A COBRAR: S/ 18.00");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTotal.setForeground(new Color(46, 204, 113));

        // Botón Registrar
        btnRegistrar = new JButton("Confirmar Alquiler");
        btnRegistrar.setBackground(new Color(33, 150, 243));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.addActionListener(e -> registrar());

        // Añadir componentes al formulario
        gbc.gridy = 0; pnlForm.add(new JLabel("Seleccionar Cliente:"), gbc);
        gbc.gridy = 1; pnlForm.add(cbCliente, gbc);
        gbc.gridy = 2; pnlForm.add(new JLabel("Seleccionar Lavadora:"), gbc);
        gbc.gridy = 3; pnlForm.add(cbLavadora, gbc);
        gbc.gridy = 4; pnlForm.add(new JLabel("Modalidad:"), gbc);
        gbc.gridy = 5; pnlForm.add(cbTipoCobro, gbc);
        gbc.gridy = 6; pnlForm.add(new JLabel("Cantidad de Horas / Promoción:"), gbc);
        
        JPanel pnlDetalle = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlDetalle.setOpaque(false);
        pnlDetalle.add(spHoras);
        pnlDetalle.add(cbPromocion);
        gbc.gridy = 7; pnlForm.add(pnlDetalle, gbc);
        
        gbc.gridy = 8; gbc.insets = new Insets(30, 20, 10, 20);
        pnlForm.add(lblTotal, gbc);
        gbc.gridy = 9; pnlForm.add(btnRegistrar, gbc);

        panel.add(pnlForm, BorderLayout.CENTER);
        
        actualizarEstadoCampos();
        calcularTotalPrevio();
        
        return panel;
    }

    // ===================== PANEL DE HISTORIAL =====================
    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel Superior: Selector de Año y Meses
        JPanel pnlSuperior = new JPanel(new BorderLayout(10, 10));
        pnlSuperior.setOpaque(false);

        // Selector de Año
        JPanel pnlAño = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlAño.setOpaque(false);
        JLabel lblAño = new JLabel("Seleccionar Año:");
        lblAño.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        int añoActual = Calendar.getInstance().get(Calendar.YEAR);
        spAño = new JSpinner(new SpinnerNumberModel(añoActual, 2020, 2030, 1));
        spAño.setFont(new Font("SansSerif", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) spAño.getEditor()).getTextField().setEditable(false);
        spAño.addChangeListener(e -> {
            mesSeleccionado = -1;
            actualizarMeses();
        });
        
        pnlAño.add(lblAño);
        pnlAño.add(spAño);
        pnlSuperior.add(pnlAño, BorderLayout.NORTH);

        // Panel de Meses (Grid 4x3)
        pnlMeses = new JPanel(new GridLayout(3, 4, 10, 10));
        pnlMeses.setBackground(Color.WHITE);
        pnlMeses.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlMeses.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        
        String[] nombresMeses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                                 "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        
        for (int i = 0; i < 12; i++) {
            final int mes = i + 1;
            JButton btnMes = new JButton(nombresMeses[i]);
            btnMes.setFont(new Font("SansSerif", Font.BOLD, 14));
            btnMes.setBackground(new Color(240, 240, 240));
            btnMes.setForeground(Color.DARK_GRAY);
            btnMes.setFocusPainted(false);
            btnMes.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnMes.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
            
            btnMes.addActionListener(e -> seleccionarMes(mes, btnMes));
            pnlMeses.add(btnMes);
        }
        
        pnlSuperior.add(pnlMeses, BorderLayout.CENTER);
        panel.add(pnlSuperior, BorderLayout.NORTH);

        // Panel Central: Tabla de Alquileres
        JPanel pnlTabla = new JPanel(new BorderLayout(10, 10));
        pnlTabla.setBackground(Color.WHITE);
        pnlTabla.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlTabla.putClientProperty(FlatClientProperties.STYLE, "arc: 15");

        JLabel lblTituloTabla = new JLabel("Alquileres del Mes");
        lblTituloTabla.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTituloTabla.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        pnlTabla.add(lblTituloTabla, BorderLayout.NORTH);

        // Crear tabla
        modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Cliente", "Lavadora", "Tipo", "Horas", "Promoción", "Total", "Fecha"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaAlquileres = new JTable(modeloTabla);
        tablaAlquileres.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaAlquileres.setRowHeight(30);
        tablaAlquileres.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tablaAlquileres.getTableHeader().setBackground(new Color(52, 152, 219));
        tablaAlquileres.getTableHeader().setForeground(Color.WHITE);
        
        // Centrar columnas numéricas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tablaAlquileres.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        tablaAlquileres.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Tipo
        tablaAlquileres.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Horas
        tablaAlquileres.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Total
        
        JScrollPane scrollTabla = new JScrollPane(tablaAlquileres);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        pnlTabla.add(scrollTabla, BorderLayout.CENTER);

        // Panel Inferior: Total del Mes
        lblTotalMes = new JLabel("TOTAL DEL MES: S/ 0.00");
        lblTotalMes.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTotalMes.setForeground(new Color(46, 204, 113));
        lblTotalMes.setHorizontalAlignment(SwingConstants.CENTER);
        lblTotalMes.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        pnlTabla.add(lblTotalMes, BorderLayout.SOUTH);

        panel.add(pnlTabla, BorderLayout.CENTER);

        return panel;
    }

    // ===================== MÉTODOS DE LÓGICA =====================
    
    private void cargarCombos() {
        try {
            cbCliente.removeAllItems();
            cbLavadora.removeAllItems();

            List<Cliente> clientes = clienteNeg.listarClientes();
            if (clientes != null) {
                for (Cliente c : clientes) cbCliente.addItem(c);
            }

            List<Lavadora> lavadoras = lavadoraNeg.listarLavadoras();
            if (lavadoras != null && !lavadoras.isEmpty()) {
                for (Lavadora l : lavadoras) {
                    cbLavadora.addItem(l);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar combos: " + e.getMessage());
        }
    }

    private void actualizarEstadoCampos() {
        boolean esHoras = cbTipoCobro.getSelectedItem().toString().equals("horas");
        spHoras.setEnabled(esHoras);
        cbPromocion.setEnabled(!esHoras);
    }

    private void calcularTotalPrevio() {
        if (cbTipoCobro.getSelectedItem().equals("horas")) {
            int h = (int) spHoras.getValue();
            lblTotal.setText("TOTAL A COBRAR: S/ " + (h * 6.0));
        } else {
            lblTotal.setText("TOTAL A COBRAR: S/ 30.00");
        }
    }

    private void registrar() {
        Cliente c = (Cliente) cbCliente.getSelectedItem();
        Lavadora l = (Lavadora) cbLavadora.getSelectedItem();

        if (c == null || l == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente y una lavadora.", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Alquiler a = new Alquiler();
        a.setIdCliente(c.getId());
        a.setIdLavadora(l.getId());
        a.setTipoCobro(cbTipoCobro.getSelectedItem().toString());
        
        if (a.getTipoCobro().equals("horas")) {
            a.setCantidadHoras((int) spHoras.getValue());
            a.setNombrePromocion(null);
        } else {
            a.setNombrePromocion(cbPromocion.getSelectedItem().toString());
            a.setCantidadHoras(0);
        }

        String rpta = negocio.validarYRegistrar(a);
        if (rpta.equals("OK")) {
            JOptionPane.showMessageDialog(this, "¡Alquiler registrado correctamente!", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            // Si hay un mes seleccionado, actualizar la vista
            if (mesSeleccionado != -1) {
                cargarAlquileresMes(mesSeleccionado, (int) spAño.getValue());
            }
        } else {
            JOptionPane.showMessageDialog(this, rpta, 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarMeses() {
        // Resetear todos los botones
        Component[] componentes = pnlMeses.getComponents();
        for (Component comp : componentes) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(new Color(240, 240, 240));
                btn.setForeground(Color.DARK_GRAY);
            }
        }
        
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        lblTotalMes.setText("TOTAL DEL MES: S/ 0.00");
    }

    private void seleccionarMes(int mes, JButton botonMes) {
        mesSeleccionado = mes;
        
        // Resetear color de todos los botones
        Component[] componentes = pnlMeses.getComponents();
        for (Component comp : componentes) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(new Color(240, 240, 240));
                btn.setForeground(Color.DARK_GRAY);
            }
        }
        
        // Marcar el botón seleccionado
        botonMes.setBackground(new Color(52, 152, 219));
        botonMes.setForeground(Color.WHITE);
        
        // Cargar alquileres del mes
        cargarAlquileresMes(mes, (int) spAño.getValue());
    }

    private void cargarAlquileresMes(int mes, int año) {
        modeloTabla.setRowCount(0);
        
        List<Alquiler> alquileres = negocio.obtenerHistorial(mes, año);
        
        if (alquileres.isEmpty()) {
            lblTotalMes.setText("TOTAL DEL MES: S/ 0.00");
            return;
        }
        
        double totalMes = 0.0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (Alquiler a : alquileres) {
            Object[] fila = new Object[8];
            fila[0] = a.getIdAlquiler();
            fila[1] = obtenerNombreCliente(a.getIdCliente());
            fila[2] = obtenerNombreLavadora(a.getIdLavadora());
            fila[3] = a.getTipoCobro();
            fila[4] = a.getCantidadHoras() > 0 ? a.getCantidadHoras() : "-";
            fila[5] = a.getNombrePromocion() != null ? a.getNombrePromocion() : "-";
            fila[6] = "S/ " + String.format("%.2f", a.getTotal());
            fila[7] = a.getFecha() != null ? sdf.format(a.getFecha()) : "-";
            
            modeloTabla.addRow(fila);
            totalMes += a.getTotal();
        }
        
        lblTotalMes.setText("TOTAL DEL MES: S/ " + String.format("%.2f", totalMes));
    }

    // Métodos auxiliares para obtener nombres
    private String obtenerNombreCliente(int idCliente) {
        for (int i = 0; i < cbCliente.getItemCount(); i++) {
            Cliente c = cbCliente.getItemAt(i);
            if (c.getId() == idCliente) {
                return c.getNombre();
            }
        }
        return "Cliente #" + idCliente;
    }

    private String obtenerNombreLavadora(int idLavadora) {
        for (int i = 0; i < cbLavadora.getItemCount(); i++) {
            Lavadora l = cbLavadora.getItemAt(i);
            if (l.getId() == idLavadora) {
                return l.getNombre();
            }
        }
        return "Lavadora #" + idLavadora;
    }
}