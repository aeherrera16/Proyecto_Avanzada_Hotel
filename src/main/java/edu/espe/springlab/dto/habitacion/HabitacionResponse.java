package edu.espe.springlab.dto.habitacion;

public class HabitacionResponse {
    private Long id;
    private String numero;
    private String tipo;
    private Double precio;
    private String estado;

    // Constructor por defecto
    public HabitacionResponse() {
    }

    // Constructor con todos los campos
    public HabitacionResponse(Long id, String numero, String tipo, Double precio, String estado) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
        this.precio = precio;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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