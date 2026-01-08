package com.kasa.CapaNegocio;

import com.kasa.CapaEntidad.Cliente;
import com.kasa.CapaDatos.ClienteDAO;
import java.sql.SQLException;
import java.util.List;

public class ClienteNegocio {
    
    private ClienteDAO dao;
    
    public ClienteNegocio() {
        dao = new ClienteDAO();
    }
    
    // 1. Método para Registrar
    public String validarYRegistrar(Cliente c) {
        try {
            if (c.getNombre().trim().isEmpty()) {
                return "Error: El nombre es obligatorio.";
            }
            if (c.getNumero().trim().isEmpty()) {
                return "Error: El número es obligatorio.";
            }
            
            if (dao.insertar(c)) {
                return "OK";
            } else {
                return "Error: No se pudo registrar el cliente.";
            }
        } catch (SQLException e) {
            return "Error de Base de Datos: " + e.getMessage();
        }
    }
    
    // 2. Método para Editar
    public String validarYEditar(Cliente c) {
        try {
            if (c.getId() <= 0) {
                return "Error: ID de cliente no válido.";
            }
            if (c.getNombre().trim().isEmpty()) {
                return "Error: El nombre es obligatorio.";
            }
            if (c.getNumero().trim().isEmpty()) {
                return "Error: El número es obligatorio.";
            }
            
            if (dao.editar(c)) {
                return "OK";
            } else {
                return "Error: No se realizaron cambios.";
            }
        } catch (SQLException e) {
            return "Error de Base de Datos: " + e.getMessage();
        }
    }
    
    // 3. Método para Eliminar ⬅️ NUEVO
    public String validarYEliminar(int idCliente) {
        try {
            if (idCliente <= 0) {
                return "Error: ID de cliente no válido.";
            }
            
            // Validar que el cliente existe antes de eliminar
            Cliente clienteExistente = dao.buscarPorId(idCliente);
            if (clienteExistente == null) {
                return "Error: El cliente no existe en la base de datos.";
            }
            
            if (dao.eliminar(idCliente)) {
                return "OK";
            } else {
                return "Error: No se pudo eliminar el cliente.";
            }
        } catch (SQLException e) {
            return "Error de Base de Datos: " + e.getMessage();
        }
    }
    
    // 4. Método para Listar
    public List<Cliente> listarClientes() {
        try {
            return dao.listar();
        } catch (SQLException e) {
            System.err.println("Error al obtener lista: " + e.getMessage());
            return null;
        }
    }
    
    // 5. Método para Buscar (OPCIONAL - para filtrado desde negocio) ⬅️ NUEVO
    public List<Cliente> buscarClientes(String criterio) {
        try {
            if (criterio == null || criterio.trim().isEmpty()) {
                return listarClientes();
            }
            return dao.buscarPorCriterio(criterio);
        } catch (SQLException e) {
            System.err.println("Error al buscar clientes: " + e.getMessage());
            return null;
        }
    }
}