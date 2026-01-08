package com.kasa.CapaEntidad;

import java.sql.Timestamp;

public class Alquiler {
    private int idAlquiler;
    private int idCliente;
    private int idLavadora;
    private Timestamp fecha;
    private String tipoCobro; // "horas" o "promocion"
    private int cantidadHoras; // 0 si es promoción
    private String nombrePromocion; // "diurna", "nocturna" o null
    private double total;

    public Alquiler() {}

    // Getters y Setters
    public int getIdAlquiler() { return idAlquiler; }
    public void setIdAlquiler(int idAlquiler) { this.idAlquiler = idAlquiler; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdLavadora() { return idLavadora; }
    public void setIdLavadora(int idLavadora) { this.idLavadora = idLavadora; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }

    public String getTipoCobro() { return tipoCobro; }
    public void setTipoCobro(String tipoCobro) { this.tipoCobro = tipoCobro; }

    public int getCantidadHoras() { return cantidadHoras; }
    public void setCantidadHoras(int cantidadHoras) { this.cantidadHoras = cantidadHoras; }

    public String getNombrePromocion() { return nombrePromocion; }
    public void setNombrePromocion(String nombrePromocion) { this.nombrePromocion = nombrePromocion; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}