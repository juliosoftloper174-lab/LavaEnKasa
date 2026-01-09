package com.kasa.CapaPresentacion;

import com.formdev.flatlaf.FlatClientProperties;
import com.kasa.CapaEntidad.*;
import com.kasa.CapaNegocio.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class PanelListaEgresos extends JPanel {

    private EgresoNegocio egresoNeg = new EgresoNegocio();
    private LavadoraNegocio lavadoraNeg = new LavadoraNegocio();

    private JTable tablaEgresos;
    private DefaultTableModel modelo;
    private JComboBox<String> cbMes;
    private JComboBox<Integer> cbAnio;
    private JButton btnFiltrar, btnEditar, btnEliminar, btnActualizar;
    private JLabel lblTotal;

    public PanelListaEgresos() {
        setLayout(new BorderLayout(15, 15));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponentes();
        configurarEventos();
        cargarEgresos();
    }

    private void initComponentes() {
        // Panel Superior: Título y filtros
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setOpaque(false);

        JLabel lblTitulo = new JLabel("Lista de Egresos");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        pnlTop.add(lblTitulo, BorderLayout.NORTH);

        // Panel de filtros
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlFiltros.setBackground(Color.WHITE);
        pnlFiltros.putClientProperty(FlatClientProperties.STYLE, "arc: 15");

        pnlFiltros.add(new JLabel("Mes:"));
        cbMes = new JComboBox<>(new String[]{
            "Todos", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        });
        pnlFiltros.add(cbMes);

        pnlFiltros.add(new JLabel("Año:"));
        cbAnio = new JComboBox<>();
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = anioActual - 5; i <= anioActual + 1; i++) {
            cbAnio.addItem(i);
        }
        cbAnio.setSelectedItem(anioActual);
        pnlFiltros.add(cbAnio);

        btnFiltrar = new JButton("Filtrar");
        btnFiltrar.setBackground(new Color(52, 152, 219));
        btnFiltrar.setForeground(Color.WHITE);
        pnlFiltros.add(btnFiltrar);

        btnActualizar = new JButton("↻ Actualizar");
        btnActualizar.setBackground(new Color(46, 204, 113));
        btnActualizar.setForeground(Color.WHITE);
        pnlFiltros.add(btnActualizar);

        pnlTop.add(pnlFiltros, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // Panel Central: Tabla
        String[] columnas = {"ID", "Fecha", "Monto (S/)", "Tipo", "Lavadora", "Descripción"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaEgresos = new JTable(modelo);
        tablaEgresos.setRowHeight(30);
        tablaEgresos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEgresos.getTableHeader().setReorderingAllowed(false);

        // Ajustar anchos de columnas
        tablaEgresos.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tablaEgresos.getColumnModel().getColumn(1).setPreferredWidth(150); // Fecha
        tablaEgresos.getColumnModel().getColumn(2).setPreferredWidth(100); // Monto
        tablaEgresos.getColumnModel().getColumn(3).setPreferredWidth(150); // Tipo
        tablaEgresos.getColumnModel().getColumn(4).setPreferredWidth(150); // Lavadora
        tablaEgresos.getColumnModel().getColumn(5).setPreferredWidth(300); // Descripción

        JScrollPane scroll = new JScrollPane(tablaEgresos);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        // Panel Inferior: Botones y Total
        JPanel pnlBottom = new JPanel(new BorderLayout(10, 10));
        pnlBottom.setOpaque(false);

        // Botones de acción
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlBotones.setOpaque(false);

        btnEditar = new JButton("✏ Editar");
        btnEditar.setBackground(new Color(241, 196, 15));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFont(new Font("SansSerif", Font.BOLD, 14));
        pnlBotones.add(btnEditar);

        btnEliminar = new JButton("🗑 Eliminar");
        btnEliminar.setBackground(new Color(231, 76, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("SansSerif", Font.BOLD, 14));
        pnlBotones.add(btnEliminar);

        pnlBottom.add(pnlBotones, BorderLayout.WEST);

        // Panel de Total
        JPanel pnlTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlTotal.setBackground(new Color(44, 62, 80));
        pnlTotal.putClientProperty(FlatClientProperties.STYLE, "arc: 15");

        lblTotal = new JLabel("Total: S/ 0.00");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTotal.setForeground(Color.WHITE);
        pnlTotal.add(lblTotal);

        pnlBottom.add(pnlTotal, BorderLayout.EAST);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnFiltrar.addActionListener(e -> filtrarPorMes());
        btnActualizar.addActionListener(e -> cargarEgresos());
        btnEditar.addActionListener(e -> editarEgreso());
        btnEliminar.addActionListener(e -> eliminarEgreso());

        // Doble clic en la tabla para editar
        tablaEgresos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editarEgreso();
                }
            }
        });
    }

    private void cargarEgresos() {
        modelo.setRowCount(0);
        List<Egreso> lista = egresoNeg.listarEgresos();
        
        if (lista != null && !lista.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            double total = 0.0;

            for (Egreso e : lista) {
                String nombreLavadora = obtenerNombreLavadora(e.getIdLavadoraAfectada());
                
                modelo.addRow(new Object[]{
                    e.getIdEgreso(),
                    sdf.format(e.getFecha()),
                    String.format("%.2f", e.getMonto()),
                    formatearTipo(e.getTipo()),
                    nombreLavadora,
                    e.getDescripcion()
                });

                total += e.getMonto();
            }

            lblTotal.setText(String.format("Total: S/ %.2f", total));
        } else {
            lblTotal.setText("Total: S/ 0.00");
        }
    }

    private void filtrarPorMes() {
        int mesSeleccionado = cbMes.getSelectedIndex(); // 0 = Todos
        int anioSeleccionado = (Integer) cbAnio.getSelectedItem();

        modelo.setRowCount(0);

        List<Egreso> lista;
        if (mesSeleccionado == 0) {
            // Mostrar todos
            lista = egresoNeg.listarEgresos();
        } else {
            // Filtrar por mes
            lista = egresoNeg.listarEgresosPorMes(mesSeleccionado, anioSeleccionado);
        }

        if (lista != null && !lista.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            double total = 0.0;

            for (Egreso e : lista) {
                String nombreLavadora = obtenerNombreLavadora(e.getIdLavadoraAfectada());
                
                modelo.addRow(new Object[]{
                    e.getIdEgreso(),
                    sdf.format(e.getFecha()),
                    String.format("%.2f", e.getMonto()),
                    formatearTipo(e.getTipo()),
                    nombreLavadora,
                    e.getDescripcion()
                });

                total += e.getMonto();
            }

            lblTotal.setText(String.format("Total: S/ %.2f", total));
        } else {
            lblTotal.setText("Total: S/ 0.00");
            JOptionPane.showMessageDialog(this, 
                "No se encontraron egresos para el período seleccionado.", 
                "Sin resultados", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editarEgreso() {
        int filaSeleccionada = tablaEgresos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, seleccione un egreso de la tabla.", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idEgreso = (Integer) modelo.getValueAt(filaSeleccionada, 0);
        Egreso egresoActual = egresoNeg.obtenerEgresoPorId(idEgreso);

        if (egresoActual != null) {
            DialogEditarEgreso dialog = new DialogEditarEgreso(
                (JFrame) SwingUtilities.getWindowAncestor(this), 
                egresoActual, 
                lavadoraNeg.listarLavadoras()
            );
            dialog.setVisible(true);

            if (dialog.fueActualizado()) {
                cargarEgresos(); // Refrescar la tabla
                JOptionPane.showMessageDialog(this, "Egreso actualizado correctamente.");
            }
        }
    }

    private void eliminarEgreso() {
        int filaSeleccionada = tablaEgresos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, seleccione un egreso de la tabla.", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar este egreso?\nEsta acción no se puede deshacer.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            int idEgreso = (Integer) modelo.getValueAt(filaSeleccionada, 0);
            String resultado = egresoNeg.eliminarEgreso(idEgreso);

            if (resultado.equals("OK")) {
                JOptionPane.showMessageDialog(this, "Egreso eliminado correctamente.");
                cargarEgresos();
            } else {
                JOptionPane.showMessageDialog(this, resultado, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String obtenerNombreLavadora(int idLavadora) {
        if (idLavadora <= 0) {
            return "N/A";
        }

        List<Lavadora> lavadoras = lavadoraNeg.listarLavadoras();
        if (lavadoras != null) {
            for (Lavadora l : lavadoras) {
                if (l.getId() == idLavadora) {
                    return l.getNombre();
                }
            }
        }
        return "Desconocida";
    }

    private String formatearTipo(String tipo) {
        switch (tipo) {
            case "compra":
                return "Compra";
            case "reparacion_moto":
                return "Reparación Moto";
            case "reparacion_lavadora":
                return "Reparación Lavadora";
            default:
                return tipo;
        }
    }
}