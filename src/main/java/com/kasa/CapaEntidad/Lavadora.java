package com.kasa.CapaEntidad;

public class Lavadora {
    private int id;
    private String nombre;
    private String estado; // disponible, malograda
    private String rutaFoto;
    private String descripcion;

    // Constructor vacío
    public Lavadora() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getRutaFoto() { return rutaFoto; }
    public void setRutaFoto(String rutaFoto) { this.rutaFoto = rutaFoto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    
    @Override
    public String toString() {
        // Esto es lo que el ComboBox dibujará en pantalla
        return this.nombre; 
    }
}