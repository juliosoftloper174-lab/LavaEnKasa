package com.kasa.CapaDatos;
import com.kasa.CapaEntidad.Egreso;
import java.sql.*;

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
}