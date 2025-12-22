package edu.espe.springlab.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Table(name = "habitacion") // Explicitly define table name
public class Habitacion {
    @Id
    private Long id;

    @Column("numero")
    private String numero;

    @Column("tipo")
    private String tipo; // Ej: Simple, Doble, Suite

    @Column("precio")
    private Double precio;

    @Column("estado")
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