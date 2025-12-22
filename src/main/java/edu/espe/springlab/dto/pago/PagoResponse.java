package edu.espe.springlab.dto.pago;

// Importación de LocalDateTime para manejar fechas y horas
import java.time.LocalDateTime;

// DTO (Data Transfer Object) para enviar datos de un pago al cliente
public class PagoResponse {
    // Identificador único del pago
    private Long id;
    // ID de la reserva asociada al pago
    private Long reservaId;
    // Monto del pago
    private Double monto;
    // Fecha y hora en que se realizó el pago
    private LocalDateTime fechaPago;
    // Método de pago utilizado (ej. "Tarjeta", "Efectivo")
    private String metodoPago;
    // Estado actual del pago (ej. "Completado", "Pendiente")
    private String estado;
    // Fecha y hora de creación del registro
    private LocalDateTime fechaCreacion;
    // Fecha y hora de la última actualización del registro
    private LocalDateTime fechaActualizacion;

    // Constructor por defecto (requerido para frameworks como Jackson)
    public PagoResponse() {
    }

    // Constructor con todos los campos para facilitar la creación de instancias completas
    public PagoResponse(Long id, Long reservaId, Double monto, LocalDateTime fechaPago, String metodoPago, String estado, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.reservaId = reservaId;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
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

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
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