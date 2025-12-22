package edu.espe.springlab.service.reactive;

import edu.espe.springlab.domain.Habitacion;
import edu.espe.springlab.repository.HabitacionRepository;
import edu.espe.springlab.exception.reactive.ReactiveResourceNotFoundException;
import edu.espe.springlab.exception.reactive.ReactiveValidationException;
import edu.espe.springlab.validator.reactive.ReactiveHabitacionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HabitacionServiceReactivo {
    
    private static final Logger log = LoggerFactory.getLogger(HabitacionServiceReactivo.class);
    
    private final HabitacionRepository habitacionRepository;
    private final ReactiveHabitacionValidator habitacionValidator;
    
    public HabitacionServiceReactivo(HabitacionRepository habitacionRepository, 
                                   ReactiveHabitacionValidator habitacionValidator) {
        this.habitacionRepository = habitacionRepository;
        this.habitacionValidator = habitacionValidator;
    }
    
    /**
     * Obtener todas las habitaciones de forma reactiva con logs detallados
     */
    public Flux<Habitacion> findAll() {
        log.info("================================================");
        log.info("INICIANDO FLUJO REACTIVO REAL DE HABITACIONES");
        log.info("================================================");
        
        return habitacionRepository.findAll()
                .doOnSubscribe(subscription -> {
                    log.info("onSubscribe: suscripción iniciada");
                    log.info("Backpressure -> solicitando habitaciones");
                })
                .doOnNext(habitacion -> {
                    log.info("onNext: procesando habitación ID: {}, Número: {}, Precio: ${}", 
                            habitacion.getId(), habitacion.getNumero(), habitacion.getPrecio());
                    log.info("Filtro: habitación ${} - {}", 
                            habitacion.getPrecio(), 
                            habitacion.getEstado() != null ? habitacion.getEstado() : "SIN ESTADO");
                })
                .doOnError(error -> {
                    log.error("onError: error en flujo de habitaciones - {}", error.getMessage());
                    log.error("================================================");
                })
                .doOnComplete(() -> {
                    log.info("================================================");
                    log.info("FLUJO REACTIVO DE HABITACIONES COMPLETADO");
                    log.info("onComplete: flujo de habitaciones finalizado");
                    log.info("================================================");
                })
                .doOnRequest(request -> {
                    log.info("Backpressure -> solicitando {} habitaciones", request);
                })
                .doOnCancel(() -> {
                    log.warn("onCancel: flujo de habitaciones cancelado");
                });
    }
    
    /**
     * Obtener habitaciones disponibles de forma reactiva
     */
    public Flux<Habitacion> findAvailable() {
        log.info("Obteniendo habitaciones disponibles");
        return habitacionRepository.findByEstado("Disponible")
                .doOnSubscribe(subscription -> log.debug("Suscripción a findAvailable"))
                .doOnNext(habitacion -> log.trace("Habitación disponible encontrada: {}", habitacion.getId()))
                .doOnError(error -> log.error("Error al obtener habitaciones disponibles", error))
                .doOnComplete(() -> log.info("Completada la obtención de habitaciones disponibles"));
    }
    
    /**
     * Obtener habitación por ID de forma reactiva
     */
    public Mono<Habitacion> findById(Long id) {
        log.info("Buscando habitación con ID: {}", id);
        return habitacionRepository.findById(id)
                .switchIfEmpty(Mono.error(new ReactiveResourceNotFoundException("Habitación no encontrada con ID: " + id)))
                .doOnSubscribe(subscription -> log.debug("Suscripción a findById de habitación: {}", id))
                .doOnNext(habitacion -> log.info("Habitación encontrada: {}", habitacion.getId()))
                .doOnError(error -> log.error("Error al buscar habitación con ID: {}", id, error));
    }
    
    /**
     * Guardar habitación de forma reactiva
     */
    public Mono<Habitacion> save(Habitacion habitacion) {
        log.info("Guardando habitación: {}", habitacion.getNumero());
        
        return habitacionValidator.validate(habitacion)
                .flatMap(validHabitacion -> habitacionRepository.save(validHabitacion))
                .doOnSubscribe(subscription -> log.debug("Suscripción a save de habitación"))
                .doOnNext(savedHabitacion -> log.info("Habitación guardada exitosamente: {}", savedHabitacion.getId()))
                .doOnError(error -> log.error("Error al guardar habitación", error));
    }
    
    /**
     * Eliminar habitación por ID de forma reactiva
     */
    public Mono<Void> deleteById(Long id) {
        log.info("Eliminando habitación con ID: {}", id);
        
        return findById(id)
                .flatMap(habitacion -> habitacionRepository.deleteById(id))
                .doOnSubscribe(subscription -> log.debug("Suscripción a deleteById de habitación: {}", id))
                .doOnSuccess(habitacion -> log.info("Habitación eliminada exitosamente: {}", id))
                .doOnError(error -> log.error("Error al eliminar habitación con ID: {}", id, error));
    }
}
