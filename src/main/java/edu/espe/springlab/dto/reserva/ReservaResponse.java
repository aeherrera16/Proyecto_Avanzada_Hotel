package edu.espe.springlab.dto.reserva;

import edu.espe.springlab.dto.huesped.HuespedResponse;
import edu.espe.springlab.dto.habitacion.HabitacionResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservaResponse {
    private Long id;
    private Long huespedId;
    private String huespedNombreCompleto; // Para mostrar el nombre del huésped
    private HuespedResponse huesped; // Objeto completo del huésped
    private Long habitacionId;
    private String habitacionNumero; // Para mostrar el número de habitación
    private HabitacionResponse habitacion; // Objeto completo de la habitación
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private Double precioTotal;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Constructor por defecto
    public ReservaResponse() {
    }

    // Constructor con todos los campos
    public ReservaResponse(Long id, Long huespedId, String huespedNombreCompleto, HuespedResponse huesped, Long habitacionId, String habitacionNumero, HabitacionResponse habitacion, LocalDate fechaEntrada, LocalDate fechaSalida, Double precioTotal, String estado, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.huespedId = huespedId;
        this.huespedNombreCompleto = huespedNombreCompleto;
        this.huesped = huesped;
        this.habitacionId = habitacionId;
        this.habitacionNumero = habitacionNumero;
        this.habitacion = habitacion;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.precioTotal = precioTotal;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    // Getters y Setters
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

    public String getHuespedNombreCompleto() {
        return huespedNombreCompleto;
    }

    public void setHuespedNombreCompleto(String huespedNombreCompleto) {
        this.huespedNombreCompleto = huespedNombreCompleto;
    }

    public Long getHabitacionId() {
        return habitacionId;
    }

    public void setHabitacionId(Long habitacionId) {
        this.habitacionId = habitacionId;
    }

    public String getHabitacionNumero() {
        return habitacionNumero;
    }

    public void setHabitacionNumero(String habitacionNumero) {
        this.habitacionNumero = habitacionNumero;
    }

    public HuespedResponse getHuesped() {
        return huesped;
    }

    public void setHuesped(HuespedResponse huesped) {
        this.huesped = huesped;
    }

    public HabitacionResponse getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(HabitacionResponse habitacion) {
        this.habitacion = habitacion;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}