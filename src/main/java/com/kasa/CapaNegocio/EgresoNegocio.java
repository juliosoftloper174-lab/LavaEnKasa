package com.kasa.CapaNegocio;

import com.kasa.CapaEntidad.Egreso;
import com.kasa.CapaDatos.EgresoDAO;
import java.util.List;

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
            if (dao.registrarEgreso(e)) {
                return "OK";
            } else {
                return "Error: No se pudo insertar el registro en la base de datos.";
            }

        } catch (Exception ex) {
            return "Error de sistema en Capa Negocio: " + ex.getMessage();
        }
    }

    /**
     * Lista todos los egresos
     */
    public List<Egreso> listarEgresos() {
        try {
            return dao.listarEgresos();
        } catch (Exception ex) {
            System.err.println("Error al listar egresos: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Lista egresos de un mes específico
     */
    public List<Egreso> listarEgresosPorMes(int mes, int anio) {
        try {
            return dao.listarEgresosPorMes(mes, anio);
        } catch (Exception ex) {
            System.err.println("Error al listar egresos por mes: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Obtiene un egreso por ID
     */
    public Egreso obtenerEgresoPorId(int id) {
        try {
            return dao.obtenerEgresoPorId(id);
        } catch (Exception ex) {
            System.err.println("Error al obtener egreso: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Valida y actualiza un egreso existente
     */
    public String validarYActualizar(Egreso e) {
        try {
            // Validaciones similares al registro
            if (e.getIdEgreso() <= 0) {
                return "Error: ID de egreso inválido.";
            }

            if (e.getMonto() <= 0) {
                return "Error: El monto debe ser mayor a cero.";
            }

            if (e.getTipo() == null || e.getTipo().isEmpty()) {
                return "Error: Debe seleccionar un tipo de egreso.";
            }

            if (e.getTipo().equalsIgnoreCase("reparacion_lavadora")) {
                if (e.getIdLavadoraAfectada() <= 0) {
                    return "Error: Para reparaciones debe seleccionar una lavadora de la lista.";
                }
            }

            if (e.getDescripcion() == null || e.getDescripcion().trim().length() < 5) {
                return "Error: Por favor, ingrese una descripción más detallada (mín. 5 caracteres).";
            }

            if (dao.actualizarEgreso(e)) {
                return "OK";
            } else {
                return "Error: No se pudo actualizar el registro en la base de datos.";
            }

        } catch (Exception ex) {
            return "Error de sistema: " + ex.getMessage();
        }
    }

    /**
     * Elimina un egreso
     */
    public String eliminarEgreso(int id) {
        try {
            if (id <= 0) {
                return "Error: ID de egreso inválido.";
            }

            if (dao.eliminarEgreso(id)) {
                return "OK";
            } else {
                return "Error: No se pudo eliminar el egreso.";
            }

        } catch (Exception ex) {
            return "Error de sistema: " + ex.getMessage();
        }
    }

    /**
     * Calcula el total de egresos de un mes
     */
    public double calcularTotalMes(int mes, int anio) {
        try {
            return dao.calcularTotalMes(mes, anio);
        } catch (Exception ex) {
            System.err.println("Error al calcular total: " + ex.getMessage());
            return 0.0;
        }
    }
}