package com.kasa.CapaDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static Connection cn = null;
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_lavadoras";
    private static final String USER = "root";
    private static final String PASS = ""; // Por defecto en XAMPP está vacío

    public static Connection getConexion() throws SQLException {
        if (cn == null || cn.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                cn = DriverManager.getConnection(URL, USER, PASS);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Error con el Driver: " + e.getMessage());
            }
        }
        return cn;
    }
}