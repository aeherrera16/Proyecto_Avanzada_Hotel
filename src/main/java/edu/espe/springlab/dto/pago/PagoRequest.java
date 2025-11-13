package edu.espe.springlab.dto.pago;

import edu.espe.springlab.validator.PagoValido;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@PagoValido  //
public class PagoRequest {
    @NotNull(message = "El ID de la reserva no puede ser nulo")
    @Min(value = 1, message = "El ID de la reserva debe ser mayor a 0")
    private Long reservaId;

    @NotNull(message = "El monto no puede ser nulo")
    @Min(value = 0, message = "El monto debe ser mayor o igual a 0")
    private Double monto;

    @NotNull(message = "La fecha de pago no puede ser nula")
    private LocalDateTime fechaPago;

    @NotBlank(message = "El método de pago no puede estar vacío")
    private String metodoPago;

    @NotBlank(message = "El estado del pago no puede estar vacío")
    private String estado;

    // Getters y Setters (igual)
    public Long getReservaId() { return reservaId; }
    public void setReservaId(Long reservaId) { this.reservaId = reservaId; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}