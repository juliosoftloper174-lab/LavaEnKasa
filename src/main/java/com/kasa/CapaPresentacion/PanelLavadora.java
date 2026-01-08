package com.kasa.CapaPresentacion;

import com.formdev.flatlaf.FlatClientProperties;
import com.kasa.CapaEntidad.Lavadora;
import com.kasa.CapaNegocio.LavadoraNegocio;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.util.List;

public class PanelLavadora extends JPanel {

    private LavadoraNegocio negocio = new LavadoraNegocio();
    private CardLayout cardLayout = new CardLayout();
    private JPanel pnlVistas;
    
    // Componentes Formulario
    private JTextField txtNombre, txtRutaFoto;
    private JTextArea txtDescripcion;
    private JComboBox<String> cbEstado;
    private JTable tabla;
    private DefaultTableModel modelo;
    private int idLavadoraSeleccionada = -1;

    // Componentes Carrusel
    private JLabel lblFotoCarrusel, lblNombreCarrusel, lblEstadoCarrusel;
    private JTextArea txtDescCarrusel;
    private List<Lavadora> listaLocal;
    private int indiceCarrusel = 0;

    public PanelLavadora() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponentes();
        cargarDatos();
    }

    private void initComponentes() {
        // =============== CABECERA CON TÍTULO Y BOTONES DE VISTA ===============
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Gestión de Lavadoras");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(45, 52, 54));
        
        // Panel de botones para cambiar de vista
        JPanel pnlSwitch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSwitch.setOpaque(false);
        
        JButton btnVerGestion = crearBotonVista("⚙️ Gestión", new Color(52, 152, 219));
        JButton btnVerCarrusel = crearBotonVista("🖼️ Carrusel", new Color(155, 89, 182));
        
        btnVerGestion.addActionListener(e -> cardLayout.show(pnlVistas, "GESTION"));
        btnVerCarrusel.addActionListener(e -> {
            actualizarCarrusel();
            cardLayout.show(pnlVistas, "CARRUSEL");
        });

        pnlSwitch.add(btnVerGestion);
        pnlSwitch.add(btnVerCarrusel);
        
        pnlHeader.add(lblTitulo, BorderLayout.WEST);
        pnlHeader.add(pnlSwitch, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // =============== CONTENEDOR DE VISTAS (CardLayout) ===============
        pnlVistas = new JPanel(cardLayout);
        pnlVistas.setOpaque(false);
        
        pnlVistas.add(crearPanelGestion(), "GESTION");
        pnlVistas.add(crearPanelCarrusel(), "CARRUSEL");
        
        add(pnlVistas, BorderLayout.CENTER);
    }

    // =============== PANEL DE GESTIÓN (FORMULARIO + TABLA) ===============
    private JPanel crearPanelGestion() {
        JPanel pnlGestion = new JPanel(new BorderLayout(20, 0));
        pnlGestion.setOpaque(false);

        // --- PANEL IZQUIERDO: FORMULARIO ---
        JPanel pnlIzquierdo = new JPanel(new BorderLayout());
        pnlIzquierdo.setOpaque(false);
        pnlIzquierdo.setPreferredSize(new Dimension(350, 0));

        JPanel pnlFormulario = new JPanel(new GridBagLayout());
        pnlFormulario.setBackground(Color.WHITE);
        pnlFormulario.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        pnlFormulario.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Título del formulario
        JLabel lblTituloForm = new JLabel("Datos de la Lavadora");
        lblTituloForm.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTituloForm.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        pnlFormulario.add(lblTituloForm, gbc);

        // Campos del formulario
        gbc.insets = new Insets(8, 0, 8, 0);
        
        gbc.gridy = 1;
        pnlFormulario.add(crearLabel("Nombre de la Lavadora:"), gbc);
        
        gbc.gridy = 2;
        txtNombre = crearCampoTexto("Ej: La Nuevas, Frankeisntein");
        pnlFormulario.add(txtNombre, gbc);
        
        gbc.gridy = 3;
        pnlFormulario.add(crearLabel("Estado:"), gbc);
        
        gbc.gridy = 4;
        cbEstado = new JComboBox<>(new String[]{"disponible", "malograda", "alquilada"});
        cbEstado.setPreferredSize(new Dimension(0, 38));
        cbEstado.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cbEstado.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        pnlFormulario.add(cbEstado, gbc);
        
        gbc.gridy = 5;
        pnlFormulario.add(crearLabel("Imagen de la Lavadora:"), gbc);
        
        // Panel para ruta de foto y botón seleccionar
        gbc.gridy = 6;
        JPanel pnlFoto = new JPanel(new BorderLayout(5, 0));
        pnlFoto.setOpaque(false);
        
        txtRutaFoto = new JTextField();
        txtRutaFoto.setEditable(false);
        txtRutaFoto.setFont(new Font("SansSerif", Font.PLAIN, 11));
        txtRutaFoto.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Sin imagen seleccionada");
        txtRutaFoto.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        
        JButton btnSeleccionarFoto = new JButton("📁");
        btnSeleccionarFoto.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnSeleccionarFoto.setPreferredSize(new Dimension(45, 38));
        btnSeleccionarFoto.setFocusPainted(false);
        btnSeleccionarFoto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSeleccionarFoto.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btnSeleccionarFoto.addActionListener(e -> seleccionarImagen());
        
        pnlFoto.add(txtRutaFoto, BorderLayout.CENTER);
        pnlFoto.add(btnSeleccionarFoto, BorderLayout.EAST);
        pnlFormulario.add(pnlFoto, gbc);
        
        gbc.gridy = 7;
        pnlFormulario.add(crearLabel("Descripción:"), gbc);
        
        gbc.gridy = 8;
        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setBorder(BorderFactory.createEmptyBorder());
        scrollDesc.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        pnlFormulario.add(scrollDesc, gbc);

        // Botones
        JPanel pnlBotones = new JPanel(new GridLayout(2, 1, 0, 10));
        pnlBotones.setOpaque(false);
        
        JButton btnGuardar = crearBoton("💾 Guardar Lavadora", new Color(46, 204, 113), Color.WHITE);
        btnGuardar.addActionListener(e -> guardar());
        
        JButton btnNuevo = crearBoton("📝 Nueva Lavadora", new Color(52, 152, 219), Color.WHITE);
        btnNuevo.addActionListener(e -> limpiar());

        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnNuevo);

        gbc.gridy = 9;
        gbc.insets = new Insets(20, 0, 0, 0);
        pnlFormulario.add(pnlBotones, gbc);

        pnlIzquierdo.add(pnlFormulario, BorderLayout.CENTER);

        // --- PANEL DERECHO: TABLA ---
        JPanel pnlDerecho = new JPanel(new BorderLayout());
        pnlDerecho.setOpaque(false);

        JPanel pnlTabla = new JPanel(new BorderLayout());
        pnlTabla.setBackground(Color.WHITE);
        pnlTabla.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        pnlTabla.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTituloTabla = new JLabel("Lista de Lavadoras Registradas");
        lblTituloTabla.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTituloTabla.setForeground(new Color(52, 73, 94));
        lblTituloTabla.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        pnlTabla.add(lblTituloTabla, BorderLayout.NORTH);

        // Crear tabla
        modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Estado", "Foto"}, 0
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

        // Configurar anchos
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(0).setMaxWidth(80);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(200);

        // Centrar columnas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tabla.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Render con colores para el estado
        tabla.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("SansSerif", Font.BOLD, 12));
                
                if (!isSelected) {
                    String estado = value.toString().toLowerCase();
                    switch (estado) {
                        case "disponible":
                            c.setBackground(new Color(46, 204, 113, 50));
                            c.setForeground(new Color(39, 174, 96));
                            break;
                        case "malograda":
                            c.setBackground(new Color(231, 76, 60, 50));
                            c.setForeground(new Color(192, 57, 43));
                            break;
                        case "alquilada":
                            c.setBackground(new Color(241, 196, 15, 50));
                            c.setForeground(new Color(243, 156, 18));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        // Listener de selección
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    idLavadoraSeleccionada = (int) modelo.getValueAt(fila, 0);
                    txtNombre.setText(modelo.getValueAt(fila, 1).toString());
                    cbEstado.setSelectedItem(modelo.getValueAt(fila, 2).toString());
                    txtRutaFoto.setText(modelo.getValueAt(fila, 3).toString());
                    
                    // Buscar descripción
                    if (listaLocal != null) {
                        listaLocal.stream()
                            .filter(l -> l.getId() == idLavadoraSeleccionada)
                            .findFirst()
                            .ifPresent(l -> txtDescripcion.setText(l.getDescripcion()));
                    }
                }
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollTabla.getViewport().setBackground(Color.WHITE);
        
        pnlTabla.add(scrollTabla, BorderLayout.CENTER);
        pnlDerecho.add(pnlTabla, BorderLayout.CENTER);

        pnlGestion.add(pnlIzquierdo, BorderLayout.WEST);
        pnlGestion.add(pnlDerecho, BorderLayout.CENTER);
        
        return pnlGestion;
    }

    // =============== PANEL DE CARRUSEL ===============
    private JPanel crearPanelCarrusel() {
        JPanel pnlCarrusel = new JPanel(new BorderLayout(20, 20));
        pnlCarrusel.setBackground(Color.WHITE);
        pnlCarrusel.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        pnlCarrusel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Título del carrusel
        JLabel lblTituloCarrusel = new JLabel("Galería de Lavadoras", JLabel.CENTER);
        lblTituloCarrusel.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTituloCarrusel.setForeground(new Color(52, 73, 94));
        pnlCarrusel.add(lblTituloCarrusel, BorderLayout.NORTH);

        // Panel central con la imagen
        JPanel pnlCentroCarrusel = new JPanel(new BorderLayout(15, 15));
        pnlCentroCarrusel.setOpaque(false);

        // Label de la foto
        lblFotoCarrusel = new JLabel("Sin Imagen", JLabel.CENTER);
        lblFotoCarrusel.setPreferredSize(new Dimension(500, 400));
        lblFotoCarrusel.setBackground(new Color(245, 245, 245));
        lblFotoCarrusel.setOpaque(true);
        lblFotoCarrusel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblFotoCarrusel.setForeground(Color.GRAY);
        lblFotoCarrusel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        pnlCentroCarrusel.add(lblFotoCarrusel, BorderLayout.CENTER);

        // Panel de información
        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setOpaque(false);
        pnlInfo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        lblNombreCarrusel = new JLabel("Nombre de Lavadora", JLabel.CENTER);
        lblNombreCarrusel.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblNombreCarrusel.setForeground(new Color(52, 73, 94));
        lblNombreCarrusel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblEstadoCarrusel = new JLabel("Estado: -", JLabel.CENTER);
        lblEstadoCarrusel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblEstadoCarrusel.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtDescCarrusel = new JTextArea(3, 40);
        txtDescCarrusel.setEditable(false);
        txtDescCarrusel.setLineWrap(true);
        txtDescCarrusel.setWrapStyleWord(true);
        txtDescCarrusel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtDescCarrusel.setBackground(new Color(250, 250, 250));
        txtDescCarrusel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane scrollDesc = new JScrollPane(txtDescCarrusel);
        scrollDesc.setBorder(BorderFactory.createEmptyBorder());

        pnlInfo.add(lblNombreCarrusel);
        pnlInfo.add(Box.createVerticalStrut(10));
        pnlInfo.add(lblEstadoCarrusel);
        pnlInfo.add(Box.createVerticalStrut(15));
        pnlInfo.add(scrollDesc);

        pnlCentroCarrusel.add(pnlInfo, BorderLayout.SOUTH);
        pnlCarrusel.add(pnlCentroCarrusel, BorderLayout.CENTER);

        // Botones de navegación
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlBotones.setOpaque(false);
        
        JButton btnAnterior = crearBoton("⬅️ Anterior", new Color(52, 152, 219), Color.WHITE);
        btnAnterior.setPreferredSize(new Dimension(150, 45));
        btnAnterior.addActionListener(e -> {
            if (indiceCarrusel > 0) {
                indiceCarrusel--;
                actualizarCarrusel();
            }
        });
        
        JButton btnSiguiente = crearBoton("Siguiente ➡️", new Color(52, 152, 219), Color.WHITE);
        btnSiguiente.setPreferredSize(new Dimension(150, 45));
        btnSiguiente.addActionListener(e -> {
            if (listaLocal != null && indiceCarrusel < listaLocal.size() - 1) {
                indiceCarrusel++;
                actualizarCarrusel();
            }
        });

        pnlBotones.add(btnAnterior);
        pnlBotones.add(btnSiguiente);
        pnlCarrusel.add(pnlBotones, BorderLayout.SOUTH);

        return pnlCarrusel;
    }

    // =============== MÉTODOS AUXILIARES ===============
    
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

    private JButton crearBotonVista(String texto, Color fondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setBackground(fondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(140, 35));
        boton.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        return boton;
    }

    // =============== LÓGICA DE NEGOCIO ===============

    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String nombre = f.getName().toLowerCase();
                return nombre.endsWith(".jpg") || nombre.endsWith(".jpeg") || 
                       nombre.endsWith(".png") || nombre.endsWith(".gif");
            }
            @Override
            public String getDescription() {
                return "Imágenes (*.jpg, *.jpeg, *.png, *.gif)";
            }
        });
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtRutaFoto.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void guardar() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        Lavadora l = new Lavadora();
        l.setNombre(txtNombre.getText().trim());
        l.setEstado(cbEstado.getSelectedItem().toString());
        l.setRutaFoto(txtRutaFoto.getText().trim());
        l.setDescripcion(txtDescripcion.getText().trim());

        String rpta;
        if (idLavadoraSeleccionada == -1) {
            rpta = negocio.validarYRegistrar(l);
        } else {
            l.setId(idLavadoraSeleccionada);
            rpta = negocio.validarYEditar(l);
        }

        if (rpta.equals("OK")) {
            String mensaje = idLavadoraSeleccionada == -1 ? 
                "¡Lavadora registrada exitosamente!" : "¡Lavadora actualizada exitosamente!";
            JOptionPane.showMessageDialog(this, mensaje, 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(this, rpta, 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        listaLocal = negocio.listarLavadoras();
        
        if (listaLocal != null && !listaLocal.isEmpty()) {
            for (Lavadora l : listaLocal) {
                String rutaCorta = l.getRutaFoto();
                if (rutaCorta != null && rutaCorta.length() > 30) {
                    rutaCorta = "..." + rutaCorta.substring(rutaCorta.length() - 27);
                }
                modelo.addRow(new Object[]{
                    l.getId(), 
                    l.getNombre(), 
                    l.getEstado(), 
                    rutaCorta != null ? rutaCorta : "Sin imagen"
                });
            }
        }
    }

    private void actualizarCarrusel() {
        if (listaLocal == null || listaLocal.isEmpty()) {
            lblNombreCarrusel.setText("No hay lavadoras registradas");
            lblEstadoCarrusel.setText("");
            txtDescCarrusel.setText("");
            lblFotoCarrusel.setIcon(null);
            lblFotoCarrusel.setText("Sin Imagen");
            return;
        }

        Lavadora l = listaLocal.get(indiceCarrusel);
        lblNombreCarrusel.setText(l.getNombre());
        lblEstadoCarrusel.setText("Estado: " + l.getEstado());
        txtDescCarrusel.setText(l.getDescripcion() != null ? l.getDescripcion() : "Sin descripción");
        
        // Cargar imagen
        if (l.getRutaFoto() != null && !l.getRutaFoto().isEmpty()) {
            File archivo = new File(l.getRutaFoto());
            if (archivo.exists()) {
                ImageIcon icon = new ImageIcon(l.getRutaFoto());
                Image img = icon.getImage().getScaledInstance(500, 400, Image.SCALE_SMOOTH);
                lblFotoCarrusel.setIcon(new ImageIcon(img));
                lblFotoCarrusel.setText("");
            } else {
                lblFotoCarrusel.setIcon(null);
                lblFotoCarrusel.setText("Imagen no encontrada");
            }
        } else {
            lblFotoCarrusel.setIcon(null);
            lblFotoCarrusel.setText("Sin Imagen");
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtRutaFoto.setText("");
        cbEstado.setSelectedIndex(0);
        idLavadoraSeleccionada = -1;
        tabla.clearSelection();
        txtNombre.requestFocus();
    }
}