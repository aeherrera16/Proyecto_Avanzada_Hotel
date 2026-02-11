package edu.espe.springlab.dto.pago;

// Importaciones de validaciones de Bean Validation (Jakarta EE)
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

// DTO (Data Transfer Object) para recibir datos de un pago desde el cliente
public class PagoRequest {
    // ID de la reserva asociada: obligatorio y > 0
    @NotNull(message = "El ID de la reserva no puede ser nulo")
    @Min(value = 1, message = "El ID de la reserva debe ser mayor a 0")
    private Long reservaId;

    // Monto del pago: obligatorio y >= 0
    @NotNull(message = "El monto no puede ser nulo")
    @Min(value = 0, message = "El monto debe ser mayor o igual a 0")
    private Double monto;

    // Fecha y hora en que se realizó el pago: obligatoria
    @NotNull(message = "La fecha de pago no puede ser nula")
    private LocalDateTime fechaPago;

    // Método de pago (efectivo, tarjeta, etc.): no puede estar vacío
    @NotBlank(message = "El método de pago no puede estar vacío")
    private String metodoPago;

    // Estado del pago (pendiente, completado, fallido, etc.): no puede estar vacío
    @NotBlank(message = "El estado del pago no puede estar vacío")
    private String estado;

    // Getters y Setters
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
}