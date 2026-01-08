package com.kasa.CapaDatos;

import com.kasa.CapaEntidad.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    
    public ClienteDAO() {
        // Constructor vacío: la conexión se pide bajo demanda
    }
    
    // 1. Insertar Cliente
    public boolean insertar(Cliente c) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, numero, direccion) VALUES (?, ?, ?)";
        try (Connection cn = Conexion.getConexion(); 
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getNumero());
            ps.setString(3, c.getDireccion());
            return ps.executeUpdate() > 0;
        }
    }
    
    // 2. Listar todos los Clientes
    public List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id_cliente"));
                c.setNombre(rs.getString("nombre"));
                c.setNumero(rs.getString("numero"));
                c.setDireccion(rs.getString("direccion"));
                lista.add(c);
            }
        }
        return lista;
    }
    
    // 3. Buscar Cliente por ID
    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente();
                    c.setId(rs.getInt("id_cliente"));
                    c.setNombre(rs.getString("nombre"));
                    c.setNumero(rs.getString("numero"));
                    c.setDireccion(rs.getString("direccion"));
                    return c;
                }
            }
        }
        return null;
    }
    
    // 4. Editar Cliente
    public boolean editar(Cliente c) throws SQLException {
        String sql = "UPDATE clientes SET nombre = ?, numero = ?, direccion = ? WHERE id_cliente = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getNumero());
            ps.setString(3, c.getDireccion());
            ps.setInt(4, c.getId());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    // 5. Eliminar Cliente ⬅️ NUEVO
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    // 6. Buscar Clientes por Criterio (para el filtro) ⬅️ NUEVO (OPCIONAL)
    public List<Cliente> buscarPorCriterio(String criterio) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE " +
                     "nombre LIKE ? OR numero LIKE ? OR direccion LIKE ?";
        
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            String parametro = "%" + criterio + "%";
            ps.setString(1, parametro);
            ps.setString(2, parametro);
            ps.setString(3, parametro);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente();
                    c.setId(rs.getInt("id_cliente"));
                    c.setNombre(rs.getString("nombre"));
                    c.setNumero(rs.getString("numero"));
                    c.setDireccion(rs.getString("direccion"));
                    lista.add(c);
                }
            }
        }
        return lista;
    }
}