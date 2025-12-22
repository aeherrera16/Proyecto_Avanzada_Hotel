package edu.espe.springlab.dto.reserva;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ReservaRequest {
    @NotNull(message = "El ID del huésped no puede ser nulo")
    @Min(value = 1, message = "El ID del huésped debe ser mayor a 0")
    private Long huespedId;

    @NotNull(message = "El ID de la habitación no puede ser nulo")
    @Min(value = 1, message = "El ID de la habitación debe ser mayor a 0")
    private Long habitacionId;

    @NotNull(message = "La fecha de entrada no puede ser nula")
    @FutureOrPresent(message = "La fecha de entrada debe ser hoy o en el futuro")
    private LocalDate fechaEntrada;

    @NotNull(message = "La fecha de salida no puede ser nula")
    @FutureOrPresent(message = "La fecha de salida debe ser hoy o en el futuro")
    private LocalDate fechaSalida;

    @NotNull(message = "El precio total no puede ser nulo")
    @Min(value = 0, message = "El precio total debe ser mayor o igual a 0")
    private Double precioTotal;

    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;

    // Getters y Setters
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
}