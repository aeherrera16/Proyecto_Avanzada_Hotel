package edu.espe.springlab.dto;

import java.time.LocalDate;
import java.math.BigDecimal;

public class ReservaCompletaDTO {
    // Datos del huésped
    private String nombre;
    private String apellido;
    private String cedula;
    private String telefono;
    private String email;
    private String nacionalidad;
    
    // Datos de la reserva
    private Long habitacionId;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private BigDecimal precioTotal;
    
    // Datos del pago
    private String metodoPago;
    
    // Constructor
    public ReservaCompletaDTO() {}
    
    public ReservaCompletaDTO(String nombre, String apellido, String cedula, String telefono, 
                             String email, String nacionalidad, Long habitacionId, 
                             LocalDate fechaEntrada, LocalDate fechaSalida, 
                             BigDecimal precioTotal, String metodoPago) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.telefono = telefono;
        this.email = email;
        this.nacionalidad = nacionalidad;
        this.habitacionId = habitacionId;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.precioTotal = precioTotal;
        this.metodoPago = metodoPago;
    }
    
    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }
    
    public Long getHabitacionId() { return habitacionId; }
    public void setHabitacionId(Long habitacionId) { this.habitacionId = habitacionId; }
    
    public LocalDate getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(LocalDate fechaEntrada) { this.fechaEntrada = fechaEntrada; }
    
    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
    
    public BigDecimal getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(BigDecimal precioTotal) { this.precioTotal = precioTotal; }
    
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
}