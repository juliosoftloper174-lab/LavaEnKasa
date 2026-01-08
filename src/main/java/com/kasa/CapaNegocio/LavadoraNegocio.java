package com.kasa.CapaNegocio;

import com.kasa.CapaEntidad.Lavadora;
import com.kasa.CapaDatos.LavadoraDAO;
import java.sql.SQLException; // Importante para el catch
import java.util.ArrayList;
import java.util.List;

public class LavadoraNegocio {
    private LavadoraDAO dao;

    public LavadoraNegocio() {
        dao = new LavadoraDAO();
    }

    // MÉTODO PARA REGISTRAR
    public String validarYRegistrar(Lavadora l) {
        try {
            if (l.getNombre() == null || l.getNombre().trim().isEmpty()) {
                return "Error: El nombre de la lavadora es obligatorio.";
            }
            
            // Si el registro es exitoso en el DAO
            if (dao.insertar(l)) {
                return "OK";
            } else {
                return "No se pudo registrar la lavadora.";
            }
        } catch (SQLException e) {
            // AQUÍ ATRAPAMOS EL ERROR que te salía antes
            return "Error de Base de Datos: " + e.getMessage();
        } catch (Exception ex) {
            return "Error inesperado: " + ex.getMessage();
        }
    }

    // MÉTODO PARA EDITAR
    public String validarYEditar(Lavadora l) {
        try {
            if (l.getId() <= 0) {
                return "Error: ID de lavadora no válido.";
            }
            if (l.getNombre().trim().isEmpty()) {
                return "Error: El nombre es obligatorio.";
            }

            if (dao.editar(l)) {
                return "OK";
            } else {
                return "No se realizaron cambios.";
            }
        } catch (SQLException e) {
            // Atrapamos la excepción para que no salga el error de compilación
            return "Error de SQL al editar: " + e.getMessage();
        }
    }

    // MÉTODO PARA LISTAR (El que usarás para llenar las tablas y combos)
    public List<Lavadora> listarLavadoras() {
        try {
            return dao.listar();
        } catch (SQLException e) {
            System.err.println("Error al listar: " + e.getMessage());
            // Devolvemos una lista vacía en lugar de romper el programa
            return new ArrayList<>(); 
        }
    }
}