package edu.espe.springlab.domain;

// Anotaciones de Spring Data R2DBC para mapeo objeto-relacional
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import java.time.LocalDateTime;

// Mapea esta clase a la tabla "pagos" en la base de datos
@Table(name = "pagos")
public class Pago {
    // Identificador único del pago (clave primaria)
    @Id
    @Column("id")
    private Long id;

    // Relación con la reserva (almacenado en la columna "reserva_id")
    @Column("reserva_id")
    private Long reservaId;

    // Monto del pago
    @Column("monto")
    private Double monto;

    // Fecha y hora en que se realizó el pago
    @Column("fecha_pago")
    private LocalDateTime fechaPago;

    // Método de pago (efectivo, tarjeta, etc.)
    @Column("metodo_pago")
    private String metodoPago;

    // Estado actual del pago (ej. "completado", "pendiente", "fallido")
    @Column("estado")
    private String estado;

    // Fecha y hora de creación del registro
    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Fecha y hora de la última actualización del registro
    @Column("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Método llamado automáticamente al crear un nuevo pago (puede usarse con
    // listeners)
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    // Método llamado automáticamente al actualizar el pago (puede usarse con
    // listeners)
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
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