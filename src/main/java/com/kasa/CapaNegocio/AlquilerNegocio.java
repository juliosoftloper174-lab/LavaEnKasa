package com.kasa.CapaNegocio;

import com.kasa.CapaEntidad.Alquiler;
import com.kasa.CapaDatos.AlquilerDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlquilerNegocio {
    private AlquilerDAO dao;

    public AlquilerNegocio() {
        dao = new AlquilerDAO();
    }

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

    // --- ESTE ES EL MÉTODO QUE TE FALTA ---
    public List<Alquiler> listarTodo() {
        try {
            return dao.listarAlquileres();
        } catch (SQLException e) {
            System.err.println("Error en Negocio (listarTodo): " + e.getMessage());
            return new ArrayList<>(); // Devuelve lista vacía si falla
        }
    }
    
    
    // Añade esto a AlquilerNegocio.java
public List<Alquiler> obtenerHistorial(int mes, int año) {
    try {
        return dao.listarPorMesYAño(mes, año);
    } catch (SQLException e) {
        System.err.println("Error al obtener historial: " + e.getMessage());
        return new ArrayList<>();
    }
}
}