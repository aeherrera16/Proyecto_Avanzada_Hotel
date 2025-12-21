package edu.espe.springlab.service.reactive;

import edu.espe.springlab.domain.Reserva;
import edu.espe.springlab.exception.reactive.ReactiveValidationException;
import edu.espe.springlab.repository.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class ReservaServiceReactivo {

    private static final Logger log = LoggerFactory.getLogger(ReservaServiceReactivo.class);

    private final ReservaRepository reservaRepository;

    public ReservaServiceReactivo(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    /**
     * Obtener todas las reservas de forma reactiva
     */
    public Flux<Reserva> findAll() {
        log.info("Iniciando búsqueda de todas las reservas");

        return reservaRepository.findAll()
                .doOnSubscribe(subscription -> log.debug("Suscripción a findAll de reservas"))
                .doOnNext(reserva -> log.debug("Reserva encontrada: {}", reserva.getId()))
                .doOnComplete(() -> log.info("Búsqueda de reservas completada"))
                .doOnError(error -> log.error("Error al buscar reservas", error));
    }

    /**
     * Buscar reserva por ID de forma reactiva
     */
    public Mono<Reserva> findById(Long id) {
        log.info("Buscando reserva con ID: {}", id);

        return reservaRepository.findById(id)
                .switchIfEmpty(Mono.error(new ReactiveResourceNotFoundException("Reserva no encontrada con ID: " + id)))
                .doOnSubscribe(subscription -> log.debug("Suscripción a findById de reserva: {}", id))
                .doOnNext(reserva -> log.info("Reserva encontrada: {}", reserva))
                .doOnError(error -> log.error("Error al buscar reserva con ID: {}", id, error));
    }

    /**
     * Guardar reserva de forma reactiva
     */
    public Mono<Reserva> save(Reserva reserva) {
        log.info("Guardando reserva: {}", reserva);

        return validateReserva(reserva)
                .flatMap(reservaValidada -> reservaRepository.save(reservaValidada))
                .doOnSubscribe(subscription -> log.debug("Suscripción a save de reserva"))
                .doOnNext(reservaGuardada -> log.info("Reserva guardada exitosamente: {}", reservaGuardada))
                .doOnError(error -> log.error("Error al guardar reserva", error));
    }

    /**
     * Eliminar reserva por ID de forma reactiva
     */
    public Mono<Void> deleteById(Long id) {
        log.info("Eliminando reserva con ID: {}", id);

        return findById(id)
                .flatMap(reserva -> reservaRepository.deleteById(id))
                .doOnSubscribe(subscription -> log.debug("Suscripción a deleteById de reserva: {}", id))
                .doOnSuccess(reserva -> log.info("Reserva eliminada exitosamente: {}", id))
                .doOnError(error -> log.error("Error al eliminar reserva con ID: {}", id, error));
    }

    /**
     * Buscar reservas por ID de huésped de forma reactiva
     */
    public Flux<Reserva> findByHuespedId(Long huespedId) {
        log.info("Buscando reservas del huésped con ID: {}", huespedId);

        return reservaRepository.findByHuespedId(huespedId)
                .doOnSubscribe(subscription -> log.debug("Suscripción a findByHuespedId: {}", huespedId))
                .doOnNext(reserva -> log.debug("Reserva encontrada para huésped {}: {}", huespedId, reserva.getId()))
                .doOnComplete(() -> log.info("Búsqueda de reservas por huésped completada"))
                .doOnError(error -> log.error("Error al buscar reservas por huésped", error));
    }

    /**
     * Buscar reservas por ID de habitación de forma reactiva
     */
    public Flux<Reserva> findByHabitacionId(Long habitacionId) {
        log.info("Buscando reservas de la habitación con ID: {}", habitacionId);

        return reservaRepository.findByHabitacionId(habitacionId)
                .doOnSubscribe(subscription -> log.debug("Suscripción a findByHabitacionId: {}", habitacionId))
                .doOnNext(reserva -> log.debug("Reserva encontrada para habitación {}: {}", habitacionId, reserva.getId()))
                .doOnComplete(() -> log.info("Búsqueda de reservas por habitación completada"))
                .doOnError(error -> log.error("Error al buscar reservas por habitación", error));
    }

    /**
     * Validar reserva de forma reactiva
     */
    private Mono<Reserva> validateReserva(Reserva reserva) {
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
