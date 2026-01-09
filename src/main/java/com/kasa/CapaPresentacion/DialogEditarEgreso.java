package com.kasa.CapaPresentacion;

import com.formdev.flatlaf.FlatClientProperties;
import com.kasa.CapaEntidad.*;
import com.kasa.CapaNegocio.EgresoNegocio;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DialogEditarEgreso extends JDialog {

    private EgresoNegocio egresoNeg = new EgresoNegocio();
    private Egreso egresoActual;
    private boolean actualizado = false;

    private JTextField txtMonto;
    private JComboBox<String> cbTipo;
    private JComboBox<Lavadora> cbLavadora;
    private JTextArea txtDescripcion;
    private JButton btnGuardar, btnCancelar;

    public DialogEditarEgreso(JFrame parent, Egreso egreso, List<Lavadora> lavadoras) {
        super(parent, "Editar Egreso", true);
        this.egresoActual = egreso;

        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE);

        initComponentes(lavadoras);
        cargarDatos();
        configurarEventos();
    }

    private void initComponentes(List<Lavadora> lavadoras) {
        // Título
        JLabel lblTitulo = new JLabel("Editar Egreso #" + egresoActual.getIdEgreso());
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        add(lblTitulo, BorderLayout.NORTH);

        // Formulario
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Monto
        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(new JLabel("Monto (S/):"), gbc);
        
        txtMonto = new JTextField();
        txtMonto.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0.00");
        gbc.gridy = 1;
        pnlForm.add(txtMonto, gbc);

        // Tipo
        gbc.gridy = 2;
        pnlForm.add(new JLabel("Tipo de Gasto:"), gbc);
        
        cbTipo = new JComboBox<>(new String[]{"compra", "reparacion_moto", "reparacion_lavadora"});
        gbc.gridy = 3;
        pnlForm.add(cbTipo, gbc);

        // Lavadora
        gbc.gridy = 4;
        pnlForm.add(new JLabel("Lavadora (solo si aplica):"), gbc);
        
        cbLavadora = new JComboBox<>();
        if (lavadoras != null) {
            for (Lavadora l : lavadoras) {
                cbLavadora.addItem(l);
            }
        }
        cbLavadora.setSelectedIndex(-1);
        cbLavadora.setEnabled(false);
        gbc.gridy = 5;
        pnlForm.add(cbLavadora, gbc);

        // Descripción
        gbc.gridy = 6;
        pnlForm.add(new JLabel("Descripción:"), gbc);
        
        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        gbc.gridy = 7;
        pnlForm.add(scrollDesc, gbc);

        add(pnlForm, BorderLayout.CENTER);

        // Botones
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlBotones.setBackground(Color.WHITE);
        pnlBotones.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 15));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(149, 165, 166));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 14));
        pnlBotones.add(btnCancelar);

        btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(new Color(46, 204, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("SansSerif", Font.BOLD, 14));
        pnlBotones.add(btnGuardar);

        add(pnlBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        txtMonto.setText(String.valueOf(egresoActual.getMonto()));
        cbTipo.setSelectedItem(egresoActual.getTipo());
        txtDescripcion.setText(egresoActual.getDescripcion());

        // Si es reparación de lavadora, seleccionar la lavadora correspondiente
        if (egresoActual.getTipo().equals("reparacion_lavadora") && 
            egresoActual.getIdLavadoraAfectada() > 0) {
            
            cbLavadora.setEnabled(true);
            
            for (int i = 0; i < cbLavadora.getItemCount(); i++) {
                Lavadora l = cbLavadora.getItemAt(i);
                if (l.getId() == egresoActual.getIdLavadoraAfectada()) {
                    cbLavadora.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void configurarEventos() {
        // Activar/desactivar combo lavadoras según tipo
        cbTipo.addActionListener(e -> {
            boolean esLavadora = cbTipo.getSelectedItem().toString().equals("reparacion_lavadora");
            cbLavadora.setEnabled(esLavadora);
            if (!esLavadora) {
                cbLavadora.setSelectedIndex(-1);
            }
        });

        btnGuardar.addActionListener(e -> guardarCambios());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void guardarCambios() {
        try {
            double monto = Double.parseDouble(txtMonto.getText());
            String tipo = cbTipo.getSelectedItem().toString();
            String descripcion = txtDescripcion.getText();

            egresoActual.setMonto(monto);
            egresoActual.setTipo(tipo);
            egresoActual.setDescripcion(descripcion);

            // Actualizar lavadora si aplica
            if (tipo.equals("reparacion_lavadora")) {
                Lavadora lav = (Lavadora) cbLavadora.getSelectedItem();
                if (lav != null) {
                    egresoActual.setIdLavadoraAfectada(lav.getId());
                }
            } else {
                egresoActual.setIdLavadoraAfectada(0);
            }

            String resultado = egresoNeg.validarYActualizar(egresoActual);

            if (resultado.equals("OK")) {
                actualizado = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, resultado, "Error de validación", JOptionPane.WARNING_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un monto válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean fueActualizado() {
        return actualizado;
    }
}