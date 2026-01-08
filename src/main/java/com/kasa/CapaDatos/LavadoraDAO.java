package com.kasa.CapaDatos;

import com.kasa.CapaEntidad.Lavadora;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LavadoraDAO {
    private Connection cn;

    public LavadoraDAO() {
        try {
            this.cn = Conexion.getConexion();
        } catch (SQLException e) {
            System.err.println("Error en LavadoraDAO (Conexión): " + e.getMessage());
        }
    }

    // MÉTODO INSERTAR (Ya lo tenías, pero asegúrate que esté así)
    public boolean insertar(Lavadora l) throws SQLException {
        String sql = "INSERT INTO lavadoras (nombre, estado, ruta_foto, descripcion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, l.getNombre());
            ps.setString(2, l.getEstado());
            ps.setString(3, l.getRutaFoto());
            ps.setString(4, l.getDescripcion());
            return ps.executeUpdate() > 0;
        }
    }

    // MÉTODO EDITAR (Faltante)
    public boolean editar(Lavadora l) throws SQLException {
        // Actualizamos todos los campos basados en el ID
        String sql = "UPDATE lavadoras SET nombre = ?, estado = ?, ruta_foto = ?, descripcion = ? WHERE id_lavadora = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, l.getNombre());
            ps.setString(2, l.getEstado());
            ps.setString(3, l.getRutaFoto());
            ps.setString(4, l.getDescripcion());
            ps.setInt(5, l.getId()); // Usamos el ID para saber cuál editar
            
            return ps.executeUpdate() > 0;
        }
    }

    // MÉTODO LISTAR (Faltante - Vital para ver tus 5 lavadoras en las tablas)
    public List<Lavadora> listar() throws SQLException {
    List<Lavadora> lista = new ArrayList<>();
    String sql = "SELECT * FROM lavadoras";
    
    // REPARACIÓN: Pedimos la conexión de nuevo aquí para asegurar que esté abierta
    this.cn = Conexion.getConexion(); 
    
    // Usamos un bloque try-with-resources para manejar el PreparedStatement y el ResultSet
    try (PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        
        while (rs.next()) {
            Lavadora l = new Lavadora();
            // Asegúrate que estos nombres coincidan exactos con tu phpMyAdmin
            l.setId(rs.getInt("id_lavadora")); 
            l.setNombre(rs.getString("nombre"));
            l.setEstado(rs.getString("estado"));
            l.setRutaFoto(rs.getString("ruta_foto"));
            l.setDescripcion(rs.getString("descripcion"));
            lista.add(l);
        }
    } // Aquí se cierran ps y rs automáticamente, pero cn se mantiene según tu clase Conexion
    return lista;
}
}