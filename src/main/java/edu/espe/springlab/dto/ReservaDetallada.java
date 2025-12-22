package edu.espe.springlab.dto;

import java.time.LocalDate;

public class ReservaDetallada {
    private Long id;
    private Long huespedId;
    private Long habitacionId;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private Double precioTotal;
    private String estado;
    
    // Datos del huésped
    private String nombreHuesped;
    private String emailHuesped;
    private String telefonoHuesped;
    private String cedulaHuesped;
    
    // Datos de la habitación
    private String numeroHabitacion;
    private String tipoHabitacion;
    private Double precioHabitacion;
    
    public ReservaDetallada() {}
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getHuespedId() {
        return huespedId;
    }
    
    public void setHuespedId(Long huespedId) {
        this.huespedId = huespedId;
    }
    
    public Long getHabitacionId() {
        return habitacionId;
    }
    
    public void setHabitacionId(Long habitacionId) {
        this.habitacionId = habitacionId;
    }
    
    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }
    
    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }
    
    public LocalDate getFechaSalida() {
        return fechaSalida;
    }
    
    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }
    
    public Double getPrecioTotal() {
        return precioTotal;
    }
    
    public void setPrecioTotal(Double precioTotal) {
        this.precioTotal = precioTotal;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getNombreHuesped() {
        return nombreHuesped;
    }
    
    public void setNombreHuesped(String nombreHuesped) {
        this.nombreHuesped = nombreHuesped;
    }
    
    public String getEmailHuesped() {
        return emailHuesped;
    }
    
    public void setEmailHuesped(String emailHuesped) {
        this.emailHuesped = emailHuesped;
    }
    
    public String getTelefonoHuesped() {
        return telefonoHuesped;
    }
    
    public void setTelefonoHuesped(String telefonoHuesped) {
        this.telefonoHuesped = telefonoHuesped;
    }
    
    public String getCedulaHuesped() {
        return cedulaHuesped;
    }
    
    public void setCedulaHuesped(String cedulaHuesped) {
        this.cedulaHuesped = cedulaHuesped;
    }
    
    public String getNumeroHabitacion() {
        return numeroHabitacion;
    }
    
    public void setNumeroHabitacion(String numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }
    
    public String getTipoHabitacion() {
        return tipoHabitacion;
    }
    
    public void setTipoHabitacion(String tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }
    
    public Double getPrecioHabitacion() {
        return precioHabitacion;
    }
    
    public void setPrecioHabitacion(Double precioHabitacion) {
        this.precioHabitacion = precioHabitacion;
    }
}
