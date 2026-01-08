package com.kasa.CapaDatos;

import com.kasa.CapaEntidad.Alquiler;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlquilerDAO {
    private Connection cn;

    public AlquilerDAO() {
        try {
            this.cn = Conexion.getConexion();
        } catch (SQLException e) {
            System.err.println("Error conexión AlquilerDAO: " + e.getMessage());
        }
    }

    public boolean registrarAlquiler(Alquiler a) throws SQLException {
        String sql = "INSERT INTO alquileres (id_cliente, id_lavadora, tipo_cobro, cantidad_horas, nombre_promocion, total) VALUES (?, ?, ?, ?, ?, ?)";
        
        this.cn = Conexion.getConexion();
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, a.getIdCliente());
            ps.setInt(2, a.getIdLavadora());
            ps.setString(3, a.getTipoCobro());
            
            if (a.getCantidadHoras() > 0) {
                ps.setInt(4, a.getCantidadHoras());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            
            if (a.getNombrePromocion() != null && !a.getNombrePromocion().isEmpty()) {
                ps.setString(5, a.getNombrePromocion());
            } else {
                ps.setNull(5, java.sql.Types.VARCHAR);
            }
            
            ps.setDouble(6, a.getTotal());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    public List<Alquiler> listarAlquileres() throws SQLException {
        List<Alquiler> lista = new ArrayList<>();
        String sql = "SELECT * FROM alquileres ORDER BY id_alquiler DESC";
        
        Connection cn = Conexion.getConexion();
        
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Alquiler a = new Alquiler();
                a.setIdAlquiler(rs.getInt("id_alquiler"));
                a.setIdCliente(rs.getInt("id_cliente"));
                a.setIdLavadora(rs.getInt("id_lavadora"));
                a.setTipoCobro(rs.getString("tipo_cobro"));
                a.setCantidadHoras(rs.getInt("cantidad_horas"));
                a.setNombrePromocion(rs.getString("nombre_promocion"));
                a.setTotal(rs.getDouble("total"));
                a.setFecha(rs.getTimestamp("fecha"));
                lista.add(a);
            }
        }
        return lista;
    }
    
    // ✅ MÉTODO CORREGIDO - Ahora mapea TODOS los campos
    public List<Alquiler> listarPorMesYAño(int mes, int año) throws SQLException {
        List<Alquiler> lista = new ArrayList<>();
        String sql = "SELECT * FROM alquileres WHERE MONTH(fecha) = ? AND YEAR(fecha) = ? ORDER BY fecha DESC";
        
        this.cn = Conexion.getConexion();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, año);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Alquiler a = new Alquiler();
                    a.setIdAlquiler(rs.getInt("id_alquiler"));
                    a.setIdCliente(rs.getInt("id_cliente"));
                    a.setIdLavadora(rs.getInt("id_lavadora"));
                    a.setTipoCobro(rs.getString("tipo_cobro"));
                    
                    // ✅ ESTAS SON LAS LÍNEAS QUE FALTABAN
                    a.setCantidadHoras(rs.getInt("cantidad_horas"));
                    a.setNombrePromocion(rs.getString("nombre_promocion"));
                    
                    a.setTotal(rs.getDouble("total"));
                    a.setFecha(rs.getTimestamp("fecha")); 
                    lista.add(a);
                }
            }
        }
        return lista;
    }
}