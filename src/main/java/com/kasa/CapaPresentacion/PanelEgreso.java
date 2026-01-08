package com.kasa.CapaPresentacion;

import com.formdev.flatlaf.FlatClientProperties;
import com.kasa.CapaEntidad.*;
import com.kasa.CapaNegocio.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelEgreso extends JPanel {

    private EgresoNegocio egresoNeg = new EgresoNegocio();
    private LavadoraNegocio lavadoraNeg = new LavadoraNegocio();

    private JTextField txtMonto;
    private JComboBox<String> cbTipo;
    private JComboBox<Lavadora> cbLavadora;
    private JTextArea txtDescripcion;
    private JButton btnRegistrar;

    public PanelEgreso() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponentes();
        cargarLavadoras();
        configurarEventos();
    }

    private void initComponentes() {
        // Título
        JLabel lblTitulo = new JLabel("Registrar Nuevo Gasto / Egreso");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(lblTitulo, BorderLayout.NORTH);

        // Contenedor Formulario
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 1. Monto
        txtMonto = new JTextField();
        txtMonto.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0.00");

        // 2. Tipo de Egreso
        cbTipo = new JComboBox<>(new String[]{"compra", "reparacion_moto", "reparacion_lavadora"});

        // 3. Selección de Lavadora (Solo para reparaciones)
        cbLavadora = new JComboBox<>();
        cbLavadora.setEnabled(false); // Inicia desactivado

        // 4. Descripción
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);

        // 5. Botón
        btnRegistrar = new JButton("Registrar Egreso");
        btnRegistrar.setBackground(new Color(231, 76, 60)); // Color Rojo para gastos
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 16));

        // Añadir al Layout
        gbc.gridy = 0; pnlForm.add(new JLabel("Monto (S/):"), gbc);
        gbc.gridy = 1; pnlForm.add(txtMonto, gbc);
        
        gbc.gridy = 2; pnlForm.add(new JLabel("Tipo de Gasto:"), gbc);
        gbc.gridy = 3; pnlForm.add(cbTipo, gbc);
        
        gbc.gridy = 4; pnlForm.add(new JLabel("Lavadora afectada (Solo para reparación):"), gbc);
        gbc.gridy = 5; pnlForm.add(cbLavadora, gbc);
        
        gbc.gridy = 6; pnlForm.add(new JLabel("Descripción del gasto:"), gbc);
        gbc.gridy = 7; pnlForm.add(scrollDesc, gbc);
        
        gbc.gridy = 8; gbc.insets = new Insets(30, 20, 10, 20);
        pnlForm.add(btnRegistrar, gbc);

        add(pnlForm, BorderLayout.CENTER);
    }

    private void configurarEventos() {
        // Activar/Desactivar combo de lavadoras según el tipo
        cbTipo.addActionListener(e -> {
            boolean esLavadora = cbTipo.getSelectedItem().toString().equals("reparacion_lavadora");
            cbLavadora.setEnabled(esLavadora);
            if (!esLavadora) cbLavadora.setSelectedIndex(-1);
        });

        btnRegistrar.addActionListener(e -> registrar());
    }

    private void cargarLavadoras() {
        cbLavadora.removeAllItems();
        List<Lavadora> lista = lavadoraNeg.listarLavadoras();
        if (lista != null) {
            for (Lavadora l : lista) {
                cbLavadora.addItem(l);
            }
        }
        cbLavadora.setSelectedIndex(-1); // Ninguna seleccionada por defecto
    }

    private void registrar() {
        try {
            double monto = Double.parseDouble(txtMonto.getText());
            String tipo = cbTipo.getSelectedItem().toString();
            String desc = txtDescripcion.getText();
            
            Egreso e = new Egreso();
            e.setMonto(monto);
            e.setTipo(tipo);
            e.setDescripcion(desc);

            if (tipo.equals("reparacion_lavadora")) {
                Lavadora lav = (Lavadora) cbLavadora.getSelectedItem();
                if (lav != null) {
                    e.setIdLavadoraAfectada(lav.getId());
                }
            }

            String rpta = egresoNeg.validarYRegistrar(e);
            
            if (rpta.equals("OK")) {
                JOptionPane.showMessageDialog(this, "Egreso registrado correctamente.");
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, rpta, "Aviso", JOptionPane.WARNING_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un monto válido.");
        }
    }

    private void limpiarCampos() {
        txtMonto.setText("");
        txtDescripcion.setText("");
        cbTipo.setSelectedIndex(0);
        cbLavadora.setSelectedIndex(-1);
    }
}