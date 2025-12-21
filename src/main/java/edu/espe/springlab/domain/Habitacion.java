package edu.espe.springlab.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "habitacion") // Explicitly define table name
public class Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable = false)
    private String tipo; // Ej: Simple, Doble, Suite

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private String estado; // Ej: Disponible, Ocupada, Mantenimiento

    // Constructor por defecto
    public Habitacion() {
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