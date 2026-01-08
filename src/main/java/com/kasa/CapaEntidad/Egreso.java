package com.kasa.CapaEntidad;

import java.sql.Timestamp;

public class Egreso {
    private int idEgreso;
    private Timestamp fecha;
    private double monto;
    private String tipo; // compra, reparacion_moto, reparacion_lavadora
    private int idLavadoraAfectada; // Se usa si el tipo es reparacion_lavadora
    private String descripcion;

    // Constructor vacío
    public Egreso() {
    }

    // Constructor con parámetros (opcional para facilitar creación rápida)
    public Egreso(double monto, String tipo, int idLavadoraAfectada, String descripcion) {
        this.monto = monto;
        this.tipo = tipo;
        this.idLavadoraAfectada = idLavadoraAfectada;
        this.descripcion = descripcion;
    }

    // --- GETTERS Y SETTERS ---

    public int getIdEgreso() {
        return idEgreso;
    }

    public void setIdEgreso(int idEgreso) {
        this.idEgreso = idEgreso;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdLavadoraAfectada() {
        return idLavadoraAfectada;
    }

    public void setIdLavadoraAfectada(int idLavadoraAfectada) {
        this.idLavadoraAfectada = idLavadoraAfectada;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}