package com.kasa.CapaPresentacion;

import com.formdev.flatlaf.FlatClientProperties;
import com.kasa.CapaEntidad.Cliente;
import com.kasa.CapaNegocio.ClienteNegocio;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class PanelCliente extends JPanel {

    private ClienteNegocio negocio = new ClienteNegocio();
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtNombre, txtNumero, txtDireccion, txtBuscar;
    private JButton btnGuardar, btnNuevo, btnEliminar;
    private int idClienteSeleccionado = -1;

    public PanelCliente() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponentes();
        cargarDatos();
    }

    private void initComponentes() {
        // =============== PANEL SUPERIOR: TÍTULO Y BÚSQUEDA ===============
        JPanel pnlSuperior = new JPanel(new BorderLayout(15, 0));
        pnlSuperior.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestión de Clientes");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(45, 52, 54));

        // Panel de búsqueda con estilo
        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBusqueda.setOpaque(false);
        
        JLabel lblBuscar = new JLabel("🔍");
        lblBuscar.setFont(new Font("SansSerif", Font.PLAIN, 18));
        
        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(300, 38));
        txtBuscar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar cliente...");
        txtBuscar.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtrarTabla(txtBuscar.getText());
            }
        });

        pnlBusqueda.add(lblBuscar);
        pnlBusqueda.add(txtBuscar);

        pnlSuperior.add(lblTitulo, BorderLayout.WEST);
        pnlSuperior.add(pnlBusqueda, BorderLayout.EAST);
        add(pnlSuperior, BorderLayout.NORTH);

        // =============== PANEL IZQUIERDO: FORMULARIO ===============
        JPanel pnlIzquierdo = new JPanel(new BorderLayout(0, 15));
        pnlIzquierdo.setOpaque(false);
        pnlIzquierdo.setPreferredSize(new Dimension(350, 0));

        // Card del formulario
        JPanel pnlFormulario = new JPanel(new GridBagLayout());
        pnlFormulario.setBackground(Color.WHITE);
        pnlFormulario.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        pnlFormulario.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Título del formulario
        JLabel lblTituloForm = new JLabel("Datos del Cliente");
        lblTituloForm.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTituloForm.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        pnlFormulario.add(lblTituloForm, gbc);

        // Campos del formulario
        gbc.insets = new Insets(8, 0, 8, 0);
        
        gbc.gridy = 1;
        pnlFormulario.add(crearLabel("Nombre Completo:"), gbc);
        
        gbc.gridy = 2;
        txtNombre = crearCampoTexto("Ingrese el nombre del cliente");
        pnlFormulario.add(txtNombre, gbc);
        
        gbc.gridy = 3;
        pnlFormulario.add(crearLabel("Número de Teléfono:"), gbc);
        
        gbc.gridy = 4;
        txtNumero = crearCampoTexto("Ingrese el número de teléfono");
        pnlFormulario.add(txtNumero, gbc);
        
        gbc.gridy = 5;
        pnlFormulario.add(crearLabel("Dirección:"), gbc);
        
        gbc.gridy = 6;
        txtDireccion = crearCampoTexto("Ingrese la dirección");
        pnlFormulario.add(txtDireccion, gbc);

        // Panel de botones
        JPanel pnlBotones = new JPanel(new GridLayout(3, 1, 0, 10));
        pnlBotones.setOpaque(false);
        
        btnGuardar = crearBoton("💾 Guardar Cliente", new Color(46, 204, 113), Color.WHITE);
        btnGuardar.addActionListener(e -> guardar());
        
        btnNuevo = crearBoton("📝 Nuevo Cliente", new Color(52, 152, 219), Color.WHITE);
        btnNuevo.addActionListener(e -> limpiar());
        
        btnEliminar = crearBoton("🗑️ Eliminar Cliente", new Color(231, 76, 60), Color.WHITE);
        btnEliminar.addActionListener(e -> eliminar());
        btnEliminar.setEnabled(false);

        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnNuevo);
        pnlBotones.add(btnEliminar);

        gbc.gridy = 7;
        gbc.insets = new Insets(25, 0, 0, 0);
        pnlFormulario.add(pnlBotones, gbc);

        pnlIzquierdo.add(pnlFormulario, BorderLayout.CENTER);
        add(pnlIzquierdo, BorderLayout.WEST);

        // =============== PANEL CENTRAL: TABLA ===============
        JPanel pnlCentral = new JPanel(new BorderLayout(0, 15));
        pnlCentral.setOpaque(false);

        // Card de la tabla
        JPanel pnlTabla = new JPanel(new BorderLayout());
        pnlTabla.setBackground(Color.WHITE);
        pnlTabla.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        pnlTabla.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título de la tabla
        JLabel lblTituloTabla = new JLabel("Lista de Clientes Registrados");
        lblTituloTabla.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTituloTabla.setForeground(new Color(52, 73, 94));
        lblTituloTabla.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        pnlTabla.add(lblTituloTabla, BorderLayout.NORTH);

        // Crear tabla
        modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre Completo", "Teléfono", "Dirección"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabla = new JTable(modelo);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setRowHeight(35);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(240, 240, 240));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(new Color(52, 152, 219));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setReorderingAllowed(false);

        // Configurar anchos de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(0).setMaxWidth(80);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(250);

        // Centrar columnas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tabla.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Alinear texto a la izquierda para nombre y dirección
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        tabla.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        tabla.getColumnModel().getColumn(3).setCellRenderer(leftRenderer);

        // Listener de selección
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                seleccionarFila();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollTabla.getViewport().setBackground(Color.WHITE);
        
        pnlTabla.add(scrollTabla, BorderLayout.CENTER);
        pnlCentral.add(pnlTabla, BorderLayout.CENTER);
        add(pnlCentral, BorderLayout.CENTER);
    }

    // =============== MÉTODOS AUXILIARES PARA CREAR COMPONENTES ===============
    
    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }

    private JTextField crearCampoTexto(String placeholder) {
        JTextField campo = new JTextField();
        campo.setPreferredSize(new Dimension(0, 38));
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        campo.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        return campo;
    }

    private JButton crearBoton(String texto, Color fondo, Color textoColor) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setBackground(fondo);
        boton.setForeground(textoColor);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(0, 40));
        boton.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        return boton;
    }

    // =============== MÉTODOS DE LÓGICA ===============

    private void seleccionarFila() {
        int fila = tabla.getSelectedRow();
        if (fila != -1) {
            int filaReal = tabla.convertRowIndexToModel(fila);
            idClienteSeleccionado = Integer.parseInt(modelo.getValueAt(filaReal, 0).toString());
            txtNombre.setText(modelo.getValueAt(filaReal, 1).toString());
            txtNumero.setText(modelo.getValueAt(filaReal, 2).toString());
            txtDireccion.setText(modelo.getValueAt(filaReal, 3).toString());
            
            btnGuardar.setText("💾 Actualizar Cliente");
            btnEliminar.setEnabled(true);
        }
    }

    private void guardar() {
        // Validaciones básicas
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        Cliente c = new Cliente();
        c.setNombre(txtNombre.getText().trim());
        c.setNumero(txtNumero.getText().trim());
        c.setDireccion(txtDireccion.getText().trim());

        String rpta;
        if (idClienteSeleccionado == -1) {
            rpta = negocio.validarYRegistrar(c);
        } else {
            c.setId(idClienteSeleccionado);
            rpta = negocio.validarYEditar(c);
        }

        if (rpta.equals("OK")) {
            String mensaje = idClienteSeleccionado == -1 ? 
                "¡Cliente registrado exitosamente!" : "¡Cliente actualizado exitosamente!";
            JOptionPane.showMessageDialog(this, mensaje, 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(this, rpta, 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

private void eliminar() {
    if (idClienteSeleccionado == -1) {
        JOptionPane.showMessageDialog(this, 
            "Seleccione un cliente para eliminar", 
            "Advertencia", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Está seguro de eliminar este cliente?\nEsta acción no se puede deshacer.",
        "Confirmar Eliminación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);
    
    if (confirmacion == JOptionPane.YES_OPTION) {
        // ✅ Llamar al método de la capa de negocio
        String rpta = negocio.validarYEliminar(idClienteSeleccionado);
        
        // ✅ Evaluar la respuesta
        if (rpta.equals("OK")) {
            JOptionPane.showMessageDialog(this, 
                "¡Cliente eliminado exitosamente!", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarDatos(); // ✅ Recargar la tabla para reflejar el cambio
        } else {
            JOptionPane.showMessageDialog(this, 
                rpta, // ✅ Mostrar el mensaje de error específico
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

    private void cargarDatos() {
        modelo.setRowCount(0);
        List<Cliente> lista = negocio.listarClientes();
        if (lista != null && !lista.isEmpty()) {
            for (Cliente c : lista) {
                modelo.addRow(new Object[]{
                    c.getId(), 
                    c.getNombre(), 
                    c.getNumero(), 
                    c.getDireccion()
                });
            }
        }
    }

    
    /**
 * Filtra visualmente la tabla en memoria.
 * NO consulta la base de datos, solo oculta/muestra filas ya cargadas.
 * Esta es una funcionalidad de UI, no de lógica de negocio.
 * Esto es para Filtrar Tabla
 */
    
    private void filtrarTabla(String consulta) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);
        
        if (consulta.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + consulta));
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtNumero.setText("");
        txtDireccion.setText("");
        idClienteSeleccionado = -1;
        tabla.clearSelection();
        btnGuardar.setText("💾 Guardar Cliente");
        btnEliminar.setEnabled(false);
        txtNombre.requestFocus();
    }
}