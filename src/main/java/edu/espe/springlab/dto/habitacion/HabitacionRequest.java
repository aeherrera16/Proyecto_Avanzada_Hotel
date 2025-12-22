package edu.espe.springlab.dto.habitacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class HabitacionRequest {
    @NotBlank(message = "El número de habitación no puede estar vacío")
    private String numero;

    @NotBlank(message = "El tipo de habitación no puede estar vacío")
    private String tipo;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser un valor positivo")
    private Double precio;

    @NotBlank(message = "El estado de la habitación no puede estar vacío")
    private String estado;

    // Constructor por defecto
    public HabitacionRequest() {
    }

    // Getters y Setters
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}