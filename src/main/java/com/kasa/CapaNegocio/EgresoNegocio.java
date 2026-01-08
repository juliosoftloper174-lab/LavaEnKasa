package com.kasa.CapaNegocio;

import com.kasa.CapaEntidad.Egreso;
import com.kasa.CapaDatos.EgresoDAO;

public class EgresoNegocio {
    private EgresoDAO dao;

    public EgresoNegocio() {
        dao = new EgresoDAO();
    }

    /**
     * Valida y registra un egreso. 
     * Implementa la "Opción B": Permite reparaciones sin bloquear por estado previo.
     */
    public String validarYRegistrar(Egreso e) {
        try {
            // 1. Validación de Monto: No existen gastos de 0 o negativos.
            if (e.getMonto() <= 0) {
                return "Error: El monto debe ser mayor a cero.";
            }

            // 2. Validación de Tipo: Asegurar que el campo no esté vacío.
            if (e.getTipo() == null || e.getTipo().isEmpty()) {
                return "Error: Debe seleccionar un tipo de egreso.";
            }

            // 3. Validación de Lavadora (Lógica Condicional):
            // Solo exigimos el ID si el tipo es estrictamente "reparacion_lavadora".
            if (e.getTipo().equalsIgnoreCase("reparacion_lavadora")) {
                if (e.getIdLavadoraAfectada() <= 0) {
                    return "Error: Para reparaciones debe seleccionar una lavadora de la lista.";
                }
            }

            // 4. Validación de Descripción:
            if (e.getDescripcion() == null || e.getDescripcion().trim().length() < 5) {
                return "Error: Por favor, ingrese una descripción más detallada (mín. 5 caracteres).";
            }

            // Si pasa las validaciones, procedemos al DAO.
            // Nota: El DAO se encarga de que, si es reparación, la lavadora quede "disponible".
            if (dao.registrarEgreso(e)) {
                return "OK";
            } else {
                return "Error: No se pudo insertar el registro en la base de datos.";
            }

        } catch (Exception ex) {
            return "Error de sistema en Capa Negocio: " + ex.getMessage();
        }
    }
}