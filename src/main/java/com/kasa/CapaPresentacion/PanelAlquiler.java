package com.kasa.CapaPresentacion;

import com.formdev.flatlaf.FlatClientProperties;
import com.kasa.CapaEntidad.*;
import com.kasa.CapaNegocio.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class PanelAlquiler extends JPanel {

    private AlquilerNegocio negocio = new AlquilerNegocio();
    private ClienteNegocio clienteNeg = new ClienteNegocio();
    private LavadoraNegocio lavadoraNeg = new LavadoraNegocio();

    // Componentes del Formulario
    private JTextField txtBuscarCliente;
    private JComboBox<Cliente> cbCliente;
    private JComboBox<Lavadora> cbLavadora;
    private JComboBox<String> cbTipoCobro;
    private JSpinner spHoras;
    private JComboBox<String> cbPromocion;
    private JLabel lblTotal;
    private JButton btnRegistrar;
    private JButton btnCancelarEdicion;
    
    // Variable para controlar el modo de edición
    private Alquiler alquilerEnEdicion = null;

    // Componentes del Historial
    private JSpinner spAño;
    private JPanel pnlMeses;
    private JTable tablaAlquileres;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalMes;
    private int mesSeleccionado = -1;
    
    // Botones de acción para la tabla
    private JButton btnEditarTabla;
    private JButton btnEliminarTabla;

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

        // ========== BÚSQUEDA DE CLIENTE MEJORADA ==========
        JLabel lblBuscarCliente = new JLabel("Buscar Cliente:");
        lblBuscarCliente.setFont(new Font("SansSerif", Font.BOLD, 13));
        
        txtBuscarCliente = new JTextField();
        txtBuscarCliente.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Escribe nombre, número o dirección...");
        
        // Listener para búsqueda en tiempo real
        txtBuscarCliente.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarClientes(txtBuscarCliente.getText());
            }
        });

        // ComboBox Cliente - SOLO MUESTRA EL NOMBRE
        cbCliente = new JComboBox<>();
        cbCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente) {
                    Cliente c = (Cliente) value;
                    setText(c.getNombre()); // SOLO EL NOMBRE
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
                    String color = l.getEstado().equals("disponible") ? "green" : "red";
                    setText(String.format("<html>%s - <font color='%s'><b>%s</b></font></html>", 
                        l.getNombre(), color, l.getEstado()));
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

        // ========== PANEL DE BOTONES CENTRADO ==========
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlBotones.setOpaque(false);
        
        btnRegistrar = new JButton("Confirmar Alquiler");
        btnRegistrar.setBackground(new Color(33, 150, 243));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.setPreferredSize(new Dimension(220, 40));
        btnRegistrar.addActionListener(e -> registrarOActualizar());
        
        btnCancelarEdicion = new JButton("Cancelar Edición");
        btnCancelarEdicion.setBackground(new Color(231, 76, 60));
        btnCancelarEdicion.setForeground(Color.WHITE);
        btnCancelarEdicion.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnCancelarEdicion.setFocusPainted(false);
        btnCancelarEdicion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelarEdicion.setPreferredSize(new Dimension(180, 40));
        btnCancelarEdicion.setVisible(false);
        btnCancelarEdicion.addActionListener(e -> cancelarEdicion());
        
        pnlBotones.add(btnRegistrar);
        pnlBotones.add(btnCancelarEdicion);

        // Añadir componentes al formulario
        gbc.gridy = 0; pnlForm.add(lblBuscarCliente, gbc);
        gbc.gridy = 1; pnlForm.add(txtBuscarCliente, gbc);
        gbc.gridy = 2; pnlForm.add(new JLabel("Seleccionar Cliente:"), gbc);
        gbc.gridy = 3; pnlForm.add(cbCliente, gbc);
        gbc.gridy = 4; pnlForm.add(new JLabel("Seleccionar Lavadora:"), gbc);
        gbc.gridy = 5; pnlForm.add(cbLavadora, gbc);
        gbc.gridy = 6; pnlForm.add(new JLabel("Modalidad:"), gbc);
        gbc.gridy = 7; pnlForm.add(cbTipoCobro, gbc);
        gbc.gridy = 8; pnlForm.add(new JLabel("Cantidad de Horas / Promoción:"), gbc);
        
        JPanel pnlDetalle = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlDetalle.setOpaque(false);
        pnlDetalle.add(spHoras);
        pnlDetalle.add(cbPromocion);
        gbc.gridy = 9; pnlForm.add(pnlDetalle, gbc);
        
        gbc.gridy = 10; gbc.insets = new Insets(30, 20, 10, 20);
        pnlForm.add(lblTotal, gbc);
        gbc.gridy = 11; pnlForm.add(pnlBotones, gbc);

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

        // Panel superior con título y botones de acción
        JPanel pnlTituloYBotones = new JPanel(new BorderLayout());
        pnlTituloYBotones.setOpaque(false);
        
        JLabel lblTituloTabla = new JLabel("Alquileres del Mes");
        lblTituloTabla.setFont(new Font("SansSerif", Font.BOLD, 18));
        pnlTituloYBotones.add(lblTituloTabla, BorderLayout.WEST);
        
        // Botones de acción
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAcciones.setOpaque(false);
        
        btnEditarTabla = new JButton("✏️ Editar");
        btnEditarTabla.setBackground(new Color(52, 152, 219));
        btnEditarTabla.setForeground(Color.WHITE);
        btnEditarTabla.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnEditarTabla.setFocusPainted(false);
        btnEditarTabla.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditarTabla.setEnabled(false);
        btnEditarTabla.addActionListener(e -> editarSeleccionado());
        
        btnEliminarTabla = new JButton("🗑️ Eliminar");
        btnEliminarTabla.setBackground(new Color(231, 76, 60));
        btnEliminarTabla.setForeground(Color.WHITE);
        btnEliminarTabla.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnEliminarTabla.setFocusPainted(false);
        btnEliminarTabla.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminarTabla.setEnabled(false);
        btnEliminarTabla.addActionListener(e -> eliminarSeleccionado());
        
        pnlAcciones.add(btnEditarTabla);
        pnlAcciones.add(btnEliminarTabla);
        pnlTituloYBotones.add(pnlAcciones, BorderLayout.EAST);
        
        pnlTabla.add(pnlTituloYBotones, BorderLayout.NORTH);

        // Crear tabla SIN columna de acciones
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
        tablaAlquileres.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAlquileres.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tablaAlquileres.getTableHeader().setBackground(new Color(52, 152, 219));
        tablaAlquileres.getTableHeader().setForeground(Color.WHITE);
        
        // Listener para habilitar/deshabilitar botones según selección
        tablaAlquileres.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean haySeleccion = tablaAlquileres.getSelectedRow() != -1;
                btnEditarTabla.setEnabled(haySeleccion);
                btnEliminarTabla.setEnabled(haySeleccion);
            }
        });
        
        // Doble clic para editar
        tablaAlquileres.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaAlquileres.getSelectedRow() != -1) {
                    editarSeleccionado();
                }
            }
        });
        
        // Centrar columnas numéricas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tablaAlquileres.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tablaAlquileres.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tablaAlquileres.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tablaAlquileres.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        
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
    
    // ========== FILTRADO DE CLIENTES (busca por nombre, número y dirección) ==========
    private void filtrarClientes(String criterio) {
        List<Cliente> clientesFiltrados;
        
        if (criterio == null || criterio.trim().isEmpty()) {
            clientesFiltrados = clienteNeg.listarClientes();
        } else {
            clientesFiltrados = clienteNeg.buscarClientes(criterio.trim());
        }
        
        cbCliente.removeAllItems();
        if (clientesFiltrados != null && !clientesFiltrados.isEmpty()) {
            for (Cliente c : clientesFiltrados) {
                cbCliente.addItem(c);
            }
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

    // ========== REGISTRO Y ACTUALIZACIÓN ==========
    private void registrarOActualizar() {
        Cliente c = (Cliente) cbCliente.getSelectedItem();
        Lavadora l = (Lavadora) cbLavadora.getSelectedItem();

        if (c == null || l == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente y una lavadora.", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Alquiler a = alquilerEnEdicion != null ? alquilerEnEdicion : new Alquiler();
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

        String rpta;
        if (alquilerEnEdicion != null) {
            rpta = negocio.validarYActualizar(a);
        } else {
            rpta = negocio.validarYRegistrar(a);
        }
        
        if (rpta.equals("OK")) {
            String mensaje = alquilerEnEdicion != null ? 
                "¡Alquiler actualizado correctamente!" : "¡Alquiler registrado correctamente!";
            
            JOptionPane.showMessageDialog(this, mensaje, 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            cancelarEdicion();
            
            // Si hay un mes seleccionado, actualizar la vista
            if (mesSeleccionado != -1) {
                cargarAlquileresMes(mesSeleccionado, (int) spAño.getValue());
            }
        } else {
            JOptionPane.showMessageDialog(this, rpta, 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ========== EDITAR DESDE TABLA ==========
    private void editarSeleccionado() {
        int filaSeleccionada = tablaAlquileres.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un alquiler de la tabla.", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idAlquiler = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        cargarDatosParaEditar(idAlquiler);
    }
    
    private void cargarDatosParaEditar(int idAlquiler) {
        Alquiler a = negocio.obtenerPorId(idAlquiler);
        
        if (a == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el alquiler.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        alquilerEnEdicion = a;
        
        // Seleccionar cliente
        for (int i = 0; i < cbCliente.getItemCount(); i++) {
            if (cbCliente.getItemAt(i).getId() == a.getIdCliente()) {
                cbCliente.setSelectedIndex(i);
                break;
            }
        }
        
        // Seleccionar lavadora
        for (int i = 0; i < cbLavadora.getItemCount(); i++) {
            if (cbLavadora.getItemAt(i).getId() == a.getIdLavadora()) {
                cbLavadora.setSelectedIndex(i);
                break;
            }
        }
        
        // Tipo de cobro
        cbTipoCobro.setSelectedItem(a.getTipoCobro());
        
        if ("horas".equals(a.getTipoCobro())) {
            spHoras.setValue(a.getCantidadHoras());
        } else {
            cbPromocion.setSelectedItem(a.getNombrePromocion());
        }
        
        // Cambiar interfaz al modo edición
        btnRegistrar.setText("Actualizar Alquiler");
        btnRegistrar.setBackground(new Color(243, 156, 18));
        btnCancelarEdicion.setVisible(true);
        
        // Cambiar a la pestaña de registro
        JTabbedPane tabbedPane = (JTabbedPane) this.getComponent(0);
        tabbedPane.setSelectedIndex(0);
    }
    
    // ========== CANCELAR EDICIÓN ==========
    private void cancelarEdicion() {
        alquilerEnEdicion = null;
        
        // Resetear formulario
        txtBuscarCliente.setText("");
        cargarCombos();
        cbTipoCobro.setSelectedIndex(0);
        spHoras.setValue(3);
        cbPromocion.setSelectedIndex(0);
        
        // Restaurar botones
        btnRegistrar.setText("Confirmar Alquiler");
        btnRegistrar.setBackground(new Color(33, 150, 243));
        btnCancelarEdicion.setVisible(false);
        
        calcularTotalPrevio();
    }
    
    // ========== ELIMINAR DESDE TABLA ==========
    private void eliminarSeleccionado() {
        int filaSeleccionada = tablaAlquileres.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un alquiler de la tabla.", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idAlquiler = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        eliminarAlquiler(idAlquiler);
    }
    
    private void eliminarAlquiler(int idAlquiler) {
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar este alquiler?\nEsta acción no se puede deshacer.",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            String rpta = negocio.validarYEliminar(idAlquiler);
            
            if (rpta.equals("OK")) {
                JOptionPane.showMessageDialog(this,
                    "¡Alquiler eliminado correctamente!",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Recargar tabla
                if (mesSeleccionado != -1) {
                    cargarAlquileresMes(mesSeleccionado, (int) spAño.getValue());
                }
            } else {
                JOptionPane.showMessageDialog(this, rpta,
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
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
        btnEditarTabla.setEnabled(false);
        btnEliminarTabla.setEnabled(false);
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
            btnEditarTabla.setEnabled(false);
            btnEliminarTabla.setEnabled(false);
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