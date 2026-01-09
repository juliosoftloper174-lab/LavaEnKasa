package com.kasa.CapaNegocio;

import com.kasa.CapaEntidad.Alquiler;
import com.kasa.CapaDatos.AlquilerDAO;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlquilerNegocio {
    private AlquilerDAO dao;
    
    public AlquilerNegocio() {
        dao = new AlquilerDAO();
    }
    
    // =========================================
    // MÉTODOS EXISTENTES (sin cambios)
    // =========================================
    
    public String validarYRegistrar(Alquiler a) {
        try {
            if (a.getIdCliente() <= 0 || a.getIdLavadora() <= 0) {
                return "Error: Debe seleccionar un cliente y una lavadora válidos.";
            }
            if ("horas".equals(a.getTipoCobro())) {
                if (a.getCantidadHoras() < 3 || a.getCantidadHoras() > 13) {
                    return "Error: El alquiler por horas debe ser entre 3 y 13 horas.";
                }
                a.setTotal(a.getCantidadHoras() * 6.0);
                a.setNombrePromocion(null);
            } 
            else if ("promocion".equals(a.getTipoCobro())) {
                a.setTotal(30.0);
                a.setCantidadHoras(0);
                if (a.getNombrePromocion() == null || a.getNombrePromocion().isEmpty()) {
                    return "Error: Debe seleccionar el nombre de la promoción.";
                }
            }
            if (dao.registrarAlquiler(a)) {
                return "OK";
            } else {
                return "Error: No se pudo insertar el registro en la base de datos.";
            }
        } catch (SQLException ex) {
            return "Error de Base de Datos: " + ex.getMessage();
        } catch (Exception e) {
            return "Error de sistema: " + e.getMessage();
        }
    }
    
    public List<Alquiler> listarTodo() {
        try {
            return dao.listarAlquileres();
        } catch (SQLException e) {
            System.err.println("Error en Negocio (listarTodo): " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<Alquiler> obtenerHistorial(int mes, int año) {
        try {
            return dao.listarPorMesYAño(mes, año);
        } catch (SQLException e) {
            System.err.println("Error al obtener historial: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // =========================================
    // NUEVOS MÉTODOS PARA FILTROS Y BÚSQUEDA
    // =========================================
    
    /**
     * Filtra alquileres por rango de fechas
     */
    public List<Alquiler> filtrarPorFechas(Date fechaInicio, Date fechaFin) {
        try {
            if (fechaInicio == null || fechaFin == null) {
                System.err.println("Error: Las fechas no pueden ser nulas");
                return new ArrayList<>();
            }
            if (fechaInicio.after(fechaFin)) {
                System.err.println("Error: La fecha inicial no puede ser posterior a la final");
                return new ArrayList<>();
            }
            return dao.listarPorRangoFechas(fechaInicio, fechaFin);
        } catch (SQLException e) {
            System.err.println("Error al filtrar por fechas: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Filtra alquileres por cliente
     */
    public List<Alquiler> filtrarPorCliente(int idCliente) {
        try {
            if (idCliente <= 0) {
                System.err.println("Error: ID de cliente inválido");
                return new ArrayList<>();
            }
            return dao.listarPorCliente(idCliente);
        } catch (SQLException e) {
            System.err.println("Error al filtrar por cliente: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Filtra alquileres por lavadora
     */
    public List<Alquiler> filtrarPorLavadora(int idLavadora) {
        try {
            if (idLavadora <= 0) {
                System.err.println("Error: ID de lavadora inválido");
                return new ArrayList<>();
            }
            return dao.listarPorLavadora(idLavadora);
        } catch (SQLException e) {
            System.err.println("Error al filtrar por lavadora: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Filtra alquileres por tipo de cobro
     */
    public List<Alquiler> filtrarPorTipoCobro(String tipoCobro) {
        try {
            if (tipoCobro == null || tipoCobro.trim().isEmpty()) {
                System.err.println("Error: Tipo de cobro inválido");
                return new ArrayList<>();
            }
            if (!tipoCobro.equals("horas") && !tipoCobro.equals("promocion")) {
                System.err.println("Error: Tipo de cobro debe ser 'horas' o 'promocion'");
                return new ArrayList<>();
            }
            return dao.listarPorTipoCobro(tipoCobro);
        } catch (SQLException e) {
            System.err.println("Error al filtrar por tipo de cobro: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Búsqueda general de alquileres
     */
    public List<Alquiler> buscar(String termino) {
        try {
            if (termino == null || termino.trim().isEmpty()) {
                return listarTodo(); // Si no hay término, devuelve todos
            }
            return dao.buscarAlquileres(termino.trim());
        } catch (SQLException e) {
            System.err.println("Error en búsqueda: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Filtro combinado (todos los criterios opcionales)
     */
    public List<Alquiler> filtrarCombinado(Integer idCliente, Integer idLavadora, 
                                           String tipoCobro, Date fechaInicio, Date fechaFin) {
        try {
            // Validación de fechas si ambas están presentes
            if (fechaInicio != null && fechaFin != null && fechaInicio.after(fechaFin)) {
                System.err.println("Error: La fecha inicial no puede ser posterior a la final");
                return new ArrayList<>();
            }
            
            // Validación de tipo de cobro si está presente
            if (tipoCobro != null && !tipoCobro.isEmpty()) {
                if (!tipoCobro.equals("horas") && !tipoCobro.equals("promocion")) {
                    System.err.println("Error: Tipo de cobro inválido");
                    return new ArrayList<>();
                }
            }
            
            return dao.filtrarAlquileres(idCliente, idLavadora, tipoCobro, fechaInicio, fechaFin);
        } catch (SQLException e) {
            System.err.println("Error en filtro combinado: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // =========================================
    // MÉTODOS PARA ESTADÍSTICAS (DASHBOARD)
    // =========================================
    
    /**
     * Obtiene el total de ingresos en un periodo
     */
    public double calcularIngresosTotales(Date fechaInicio, Date fechaFin) {
        try {
            if (fechaInicio == null || fechaFin == null) {
                System.err.println("Error: Las fechas no pueden ser nulas");
                return 0.0;
            }
            return dao.obtenerTotalIngresos(fechaInicio, fechaFin);
        } catch (SQLException e) {
            System.err.println("Error al calcular ingresos: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Cuenta la cantidad de alquileres en un periodo
     */
    public int contarAlquileres(Date fechaInicio, Date fechaFin) {
        try {
            if (fechaInicio == null || fechaFin == null) {
                System.err.println("Error: Las fechas no pueden ser nulas");
                return 0;
            }
            return dao.contarAlquileres(fechaInicio, fechaFin);
        } catch (SQLException e) {
            System.err.println("Error al contar alquileres: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Obtiene estadísticas agrupadas por tipo de cobro
     * Retorna: [tipoCobro, cantidad, totalIngresos]
     */
    public List<Object[]> obtenerEstadisticasTipoCobro(Date fechaInicio, Date fechaFin) {
        try {
            if (fechaInicio == null || fechaFin == null) {
                System.err.println("Error: Las fechas no pueden ser nulas");
                return new ArrayList<>();
            }
            return dao.obtenerEstadisticasPorTipoCobro(fechaInicio, fechaFin);
        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Calcula el promedio de ingresos por alquiler en un periodo
     */
    public double calcularPromedioIngresos(Date fechaInicio, Date fechaFin) {
        try {
            int cantidad = contarAlquileres(fechaInicio, fechaFin);
            if (cantidad == 0) return 0.0;
            
            double total = calcularIngresosTotales(fechaInicio, fechaFin);
            return total / cantidad;
        } catch (Exception e) {
            System.err.println("Error al calcular promedio: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
 * Valida y actualiza un alquiler existente
 */
public String validarYActualizar(Alquiler a) {
    try {
        if (a.getIdAlquiler() <= 0) {
            return "Error: ID de alquiler no válido.";
        }
        
        if (a.getIdCliente() <= 0 || a.getIdLavadora() <= 0) {
            return "Error: Debe seleccionar un cliente y una lavadora válidos.";
        }
        
        if ("horas".equals(a.getTipoCobro())) {
            if (a.getCantidadHoras() < 3 || a.getCantidadHoras() > 13) {
                return "Error: El alquiler por horas debe ser entre 3 y 13 horas.";
            }
            a.setTotal(a.getCantidadHoras() * 6.0);
            a.setNombrePromocion(null);
        } 
        else if ("promocion".equals(a.getTipoCobro())) {
            a.setTotal(30.0);
            a.setCantidadHoras(0);
            if (a.getNombrePromocion() == null || a.getNombrePromocion().isEmpty()) {
                return "Error: Debe seleccionar el nombre de la promoción.";
            }
        }
        
        if (dao.actualizar(a)) {
            return "OK";
        } else {
            return "Error: No se pudo actualizar el registro.";
        }
    } catch (SQLException ex) {
        return "Error de Base de Datos: " + ex.getMessage();
    } catch (Exception e) {
        return "Error de sistema: " + e.getMessage();
    }
}

/**
 * Valida y elimina un alquiler
 */
public String validarYEliminar(int idAlquiler) {
    try {
        if (idAlquiler <= 0) {
            return "Error: ID de alquiler no válido.";
        }
        
        // Verificar que el alquiler existe
        Alquiler alquilerExistente = dao.buscarPorId(idAlquiler);
        if (alquilerExistente == null) {
            return "Error: El alquiler no existe en la base de datos.";
        }
        
        if (dao.eliminar(idAlquiler)) {
            return "OK";
        } else {
            return "Error: No se pudo eliminar el registro.";
        }
    } catch (SQLException ex) {
        return "Error de Base de Datos: " + ex.getMessage();
    } catch (Exception e) {
        return "Error de sistema: " + e.getMessage();
    }
}

/**
 * Obtiene un alquiler por su ID
 */
public Alquiler obtenerPorId(int idAlquiler) {
    try {
        return dao.buscarPorId(idAlquiler);
    } catch (SQLException e) {
        System.err.println("Error al obtener alquiler: " + e.getMessage());
        return null;
    }
}
}