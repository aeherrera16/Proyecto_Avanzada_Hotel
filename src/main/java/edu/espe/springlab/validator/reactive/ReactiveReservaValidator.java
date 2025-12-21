package edu.espe.springlab.validator.reactive;

import edu.espe.springlab.domain.Reserva;
import edu.espe.springlab.exception.reactive.ReactiveValidationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Component
public class ReactiveReservaValidator {

    /**
     * Valida una reserva de forma reactiva
     */
    public Mono<Reserva> validate(Reserva reserva) {
        return Mono.fromCallable(() -> {
            // Validar ID de huésped
            if (reserva.getHuespedId() == null) {
                throw new ReactiveValidationException("El ID del huésped es obligatorio");
            }

            // Validar ID de habitación
            if (reserva.getHabitacionId() == null) {
                throw new ReactiveValidationException("El ID de la habitación es obligatorio");
            }

            // Validar fechas
            if (reserva.getFechaEntrada() == null) {
                throw new ReactiveValidationException("La fecha de entrada es obligatoria");
            }

            if (reserva.getFechaSalida() == null) {
                throw new ReactiveValidationException("La fecha de salida es obligatoria");
            }

            // Validar que la fecha de salida sea posterior a la entrada
            if (reserva.getFechaSalida().isBefore(reserva.getFechaEntrada())) {
                throw new ReactiveValidationException("La fecha de salida debe ser posterior a la fecha de entrada");
            }

            // Validar que la fecha de entrada no sea en el pasado
            if (reserva.getFechaEntrada().isBefore(LocalDate.now())) {
                throw new ReactiveValidationException("La fecha de entrada no puede ser en el pasado");
            }

            // Validar precio total
            if (reserva.getPrecioTotal() == null || reserva.getPrecioTotal() <= 0) {
                throw new ReactiveValidationException("El precio total debe ser mayor a 0");
            }

            // Validar estado
            if (reserva.getEstado() == null || reserva.getEstado().trim().isEmpty()) {
                throw new ReactiveValidationException("El estado de la reserva es obligatorio");
            }

            // Validar que el estado sea válido
            String[] estadosValidos = {"Confirmada", "Pendiente", "Cancelada"};
            boolean estadoValido = false;
            for (String estado : estadosValidos) {
                if (estado.equals(reserva.getEstado())) {
                    estadoValido = true;
                    break;
                }
            }

            if (!estadoValido) {
                throw new ReactiveValidationException("Estado no válido. Debe ser: Confirmada, Pendiente o Cancelada");
            }

            return reserva;
        });
    }
}
