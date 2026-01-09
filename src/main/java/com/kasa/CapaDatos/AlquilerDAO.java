package com.kasa.CapaDatos;

import com.kasa.CapaEntidad.Alquiler;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date; // Si aún no está

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

        this.cn = Conexion.getConexion();
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearAlquiler(rs));
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
                lista.add(mapearAlquiler(rs));
            }
        }
    }
    return lista;
}
    
    
    
    // ===============================================
// NUEVOS MÉTODOS PARA AGREGAR AL AlquilerDAO
// ===============================================

/**
 * Filtra alquileres por rango de fechas
 */
public List<Alquiler> listarPorRangoFechas(Date fechaInicio, Date fechaFin) throws SQLException {
    List<Alquiler> lista = new ArrayList<>();
    String sql = "SELECT * FROM alquileres WHERE DATE(fecha) BETWEEN ? AND ? ORDER BY fecha DESC";
    
    this.cn = Conexion.getConexion();
    try (PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setDate(1, fechaInicio);
        ps.setDate(2, fechaFin);
        
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearAlquiler(rs));
            }
        }
    }
    return lista;
}

/**
 * Filtra alquileres por cliente
 */
public List<Alquiler> listarPorCliente(int idCliente) throws SQLException {
    List<Alquiler> lista = new ArrayList<>();
    String sql = "SELECT * FROM alquileres WHERE id_cliente = ? ORDER BY fecha DESC";
    
    this.cn = Conexion.getConexion();
    try (PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setInt(1, idCliente);
        
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearAlquiler(rs));
            }
        }
    }
    return lista;
}

/**
 * Filtra alquileres por lavadora
 */
public List<Alquiler> listarPorLavadora(int idLavadora) throws SQLException {
    List<Alquiler> lista = new ArrayList<>();
    String sql = "SELECT * FROM alquileres WHERE id_lavadora = ? ORDER BY fecha DESC";
    
    this.cn = Conexion.getConexion();
    try (PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setInt(1, idLavadora);
        
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearAlquiler(rs));
            }
        }
    }
    return lista;
}

/**
 * Filtra alquileres por tipo de cobro
 */
public List<Alquiler> listarPorTipoCobro(String tipoCobro) throws SQLException {
    List<Alquiler> lista = new ArrayList<>();
    String sql = "SELECT * FROM alquileres WHERE tipo_cobro = ? ORDER BY fecha DESC";
    
    this.cn = Conexion.getConexion();
    try (PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setString(1, tipoCobro);
        
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearAlquiler(rs));
            }
        }
    }
    return lista;
}

/**
 * Búsqueda general (busca en cliente, lavadora, promoción)
 * Nota: Este método requiere JOIN con las tablas clientes y lavadoras
 */
public List<Alquiler> buscarAlquileres(String termino) throws SQLException {
    List<Alquiler> lista = new ArrayList<>();
    String sql = "SELECT a.* FROM alquileres a " +
                 "LEFT JOIN clientes c ON a.id_cliente = c.id_cliente " +
                 "LEFT JOIN lavadoras l ON a.id_lavadora = l.id_lavadora " +
                 "WHERE c.nombre LIKE ? OR c.apellido LIKE ? OR " +
                 "l.numero_lavadora LIKE ? OR a.nombre_promocion LIKE ? " +
                 "ORDER BY a.fecha DESC";
    
    this.cn = Conexion.getConexion();
    try (PreparedStatement ps = cn.prepareStatement(sql)) {
        String patron = "%" + termino + "%";
        ps.setString(1, patron);
        ps.setString(2, patron);
        ps.setString(3, patron);
        ps.setString(4, patron);
        
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearAlquiler(rs));
            }
        }
    }
    return lista;
}

/**
 * Filtro combinado (todos los criterios opcionales)
 */
public List<Alquiler> filtrarAlquileres(Integer idCliente, Integer idLavadora, 
                                         String tipoCobro, Date fechaInicio, Date fechaFin) throws SQLException {
    List<Alquiler> lista = new ArrayList<>();
    StringBuilder sql = new StringBuilder("SELECT * FROM alquileres WHERE 1=1");
    List<Object> parametros = new ArrayList<>();
    
    if (idCliente != null) {
        sql.append(" AND id_cliente = ?");
        parametros.add(idCliente);
    }
    if (idLavadora != null) {
        sql.append(" AND id_lavadora = ?");
        parametros.add(idLavadora);
    }
    if (tipoCobro != null && !tipoCobro.isEmpty()) {
        sql.append(" AND tipo_cobro = ?");
        parametros.add(tipoCobro);
    }
    if (fechaInicio != null) {
        sql.append(" AND DATE(fecha) >= ?");
        parametros.add(fechaInicio);
    }
    if (fechaFin != null) {
        sql.append(" AND DATE(fecha) <= ?");
        parametros.add(fechaFin);
    }
    
    sql.append(" ORDER BY fecha DESC");
    
    this.cn = Conexion.getConexion();
    try (PreparedStatement ps = cn.prepareStatement(sql.toString())) {
        for (int i = 0; i < parametros.size(); i++) {
            Object param = parametros.get(i);
            if (param instanceof Integer) {
                ps.setInt(i + 1, (Integer) param);
            } else if (param instanceof String) {
                ps.setString(i + 1, (String) param);
            } else if (param instanceof Date) {
                ps.setDate(i + 1, (Date) param);
            }
        }
        
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearAlquiler(rs));
            }
        }
    }
    return lista;
}

/**
 * Obtiene el total de ingresos en un rango de fechas
 */
public double obtenerTotalIngresos(Date fechaInicio, Date fechaFin) throws SQLException {
    String sql = "SELECT COALESCE(SUM(total), 0) as total_ingresos FROM alquileres " +
                 "WHERE DATE(fecha) BETWEEN ? AND ?";
    
    this.cn = Conexion.getConexion();
    try (PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setDate(1, fechaInicio);
        ps.setDate(2, fechaFin);
        
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total_ingresos");
            }
        }
    }
    return 0.0;
}

/**
 * Cuenta alquileres en un rango de fechas
 */
public int contarAlquileres(Date fechaInicio, Date fechaFin) throws SQLException {
    String sql = "SELECT COUNT(*) as cantidad FROM alquileres " +
                 "WHERE DATE(fecha) BETWEEN ? AND ?";
    
    this.cn = Conexion.getConexion();
    try (PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setDate(1, fechaInicio);
        ps.setDate(2, fechaFin);
        
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("cantidad");
            }
        }
    }
    return 0;
}

/**
 * Obtiene estadísticas por tipo de cobro
 */
public List<Object[]> obtenerEstadisticasPorTipoCobro(Date fechaInicio, Date fechaFin) throws SQLException {
    List<Object[]> estadisticas = new ArrayList<>();
    String sql = "SELECT tipo_cobro, COUNT(*) as cantidad, SUM(total) as total_ingresos " +
                 "FROM alquileres WHERE DATE(fecha) BETWEEN ? AND ? " +
                 "GROUP BY tipo_cobro ORDER BY total_ingresos DESC";
    
    this.cn = Conexion.getConexion();
    try (PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setDate(1, fechaInicio);
        ps.setDate(2, fechaFin);
        
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] fila = new Object[3];
                fila[0] = rs.getString("tipo_cobro");
                fila[1] = rs.getInt("cantidad");
                fila[2] = rs.getDouble("total_ingresos");
                estadisticas.add(fila);
            }
        }
    }
    return estadisticas;
}

/**
 * Método auxiliar para mapear ResultSet a objeto Alquiler
 * Evita duplicación de código
 */
private Alquiler mapearAlquiler(ResultSet rs) throws SQLException {
    Alquiler a = new Alquiler();
    a.setIdAlquiler(rs.getInt("id_alquiler"));
    a.setIdCliente(rs.getInt("id_cliente"));
    a.setIdLavadora(rs.getInt("id_lavadora"));
    a.setTipoCobro(rs.getString("tipo_cobro"));
    a.setCantidadHoras(rs.getInt("cantidad_horas"));
    a.setNombrePromocion(rs.getString("nombre_promocion"));
    a.setTotal(rs.getDouble("total"));
    a.setFecha(rs.getTimestamp("fecha"));
    return a;
}

/**
 * Actualiza un alquiler existente
 */
public boolean actualizar(Alquiler a) throws SQLException {
    String sql = "UPDATE alquileres SET id_cliente = ?, id_lavadora = ?, tipo_cobro = ?, " +
                 "cantidad_horas = ?, nombre_promocion = ?, total = ? WHERE id_alquiler = ?";
    
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
        ps.setInt(7, a.getIdAlquiler());
        
        return ps.executeUpdate() > 0;
    }
}

/**
 * Elimina un alquiler por su ID
 */
public boolean eliminar(int idAlquiler) throws SQLException {
    String sql = "DELETE FROM alquileres WHERE id_alquiler = ?";
    
    this.cn = Conexion.getConexion();
    
    try (PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setInt(1, idAlquiler);
        return ps.executeUpdate() > 0;
    }
}

/**
 * Busca un alquiler por su ID
 */
public Alquiler buscarPorId(int idAlquiler) throws SQLException {
    String sql = "SELECT * FROM alquileres WHERE id_alquiler = ?";
    
    this.cn = Conexion.getConexion();
    
    try (PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setInt(1, idAlquiler);
        
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapearAlquiler(rs);
            }
        }
    }
    return null;
}

}