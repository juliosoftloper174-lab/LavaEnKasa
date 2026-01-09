package com.kasa.CapaDatos;
import com.kasa.CapaEntidad.Egreso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EgresoDAO {
    private Connection cn;

    public EgresoDAO() {
        try { this.cn = Conexion.getConexion(); } catch (SQLException e) {}
    }

    public boolean registrarEgreso(Egreso e) throws SQLException {
        String sql = "INSERT INTO egresos (monto, tipo, id_lavadora_afectada, descripcion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDouble(1, e.getMonto());
            ps.setString(2, e.getTipo());
            
            // Si es reparación de lavadora, guardamos el ID, si no, mandamos NULL a la BD
            if (e.getTipo().equals("reparacion_lavadora")) {
                ps.setInt(3, e.getIdLavadoraAfectada());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            
            ps.setString(4, e.getDescripcion());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Lista todos los egresos ordenados por fecha descendente
     */
    public List<Egreso> listarEgresos() throws SQLException {
        List<Egreso> lista = new ArrayList<>();
        String sql = "SELECT id_egreso, fecha, monto, tipo, id_lavadora_afectada, descripcion " +
                     "FROM egresos ORDER BY fecha DESC";
        
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Egreso e = new Egreso();
                e.setIdEgreso(rs.getInt("id_egreso"));
                e.setFecha(rs.getTimestamp("fecha"));
                e.setMonto(rs.getDouble("monto"));
                e.setTipo(rs.getString("tipo"));
                e.setIdLavadoraAfectada(rs.getInt("id_lavadora_afectada"));
                e.setDescripcion(rs.getString("descripcion"));
                lista.add(e);
            }
        }
        return lista;
    }

    /**
     * Lista egresos filtrados por mes y año
     */
    public List<Egreso> listarEgresosPorMes(int mes, int anio) throws SQLException {
        List<Egreso> lista = new ArrayList<>();
        String sql = "SELECT id_egreso, fecha, monto, tipo, id_lavadora_afectada, descripcion " +
                     "FROM egresos " +
                     "WHERE MONTH(fecha) = ? AND YEAR(fecha) = ? " +
                     "ORDER BY fecha DESC";
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, anio);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Egreso e = new Egreso();
                    e.setIdEgreso(rs.getInt("id_egreso"));
                    e.setFecha(rs.getTimestamp("fecha"));
                    e.setMonto(rs.getDouble("monto"));
                    e.setTipo(rs.getString("tipo"));
                    e.setIdLavadoraAfectada(rs.getInt("id_lavadora_afectada"));
                    e.setDescripcion(rs.getString("descripcion"));
                    lista.add(e);
                }
            }
        }
        return lista;
    }

    /**
     * Obtiene un egreso por su ID
     */
    public Egreso obtenerEgresoPorId(int id) throws SQLException {
        String sql = "SELECT id_egreso, fecha, monto, tipo, id_lavadora_afectada, descripcion " +
                     "FROM egresos WHERE id_egreso = ?";
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Egreso e = new Egreso();
                    e.setIdEgreso(rs.getInt("id_egreso"));
                    e.setFecha(rs.getTimestamp("fecha"));
                    e.setMonto(rs.getDouble("monto"));
                    e.setTipo(rs.getString("tipo"));
                    e.setIdLavadoraAfectada(rs.getInt("id_lavadora_afectada"));
                    e.setDescripcion(rs.getString("descripcion"));
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * Actualiza un egreso existente
     */
    public boolean actualizarEgreso(Egreso e) throws SQLException {
        String sql = "UPDATE egresos SET monto = ?, tipo = ?, id_lavadora_afectada = ?, descripcion = ? " +
                     "WHERE id_egreso = ?";
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDouble(1, e.getMonto());
            ps.setString(2, e.getTipo());
            
            if (e.getTipo().equals("reparacion_lavadora")) {
                ps.setInt(3, e.getIdLavadoraAfectada());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            
            ps.setString(4, e.getDescripcion());
            ps.setInt(5, e.getIdEgreso());
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un egreso por su ID
     */
    public boolean eliminarEgreso(int id) throws SQLException {
        String sql = "DELETE FROM egresos WHERE id_egreso = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Calcula el total de egresos de un mes específico
     */
    public double calcularTotalMes(int mes, int anio) throws SQLException {
        String sql = "SELECT COALESCE(SUM(monto), 0) as total " +
                     "FROM egresos " +
                     "WHERE MONTH(fecha) = ? AND YEAR(fecha) = ?";
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, anio);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }
}
