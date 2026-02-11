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
     * Obtener todas las habitaciones de forma reactiva con logs detallados.
     * Si hay error en la base de datos, retorna lista vacía (onErrorResume).
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
                // ⭐ onErrorResume: Si hay error de BD, retorna flujo vacío en lugar de fallar
                .onErrorResume(error -> {
                    log.warn("🔄 onErrorResume ACTIVADO: Error en BD - Retornando lista vacía");
                    log.warn("   Error original: {}", error.getMessage());
                    return Flux.empty();
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
                // ⭐ onErrorResume: Si hay error, retorna lista vacía
                .onErrorResume(error -> {
                    log.warn("🔄 onErrorResume: Error obteniendo disponibles - Retornando vacío");
                    return Flux.empty();
                })
                .doOnComplete(() -> log.info("Completada la obtención de habitaciones disponibles"));
    }

    /**
     * Obtener habitación por ID de forma reactiva.
     * ⭐ DEMO onErrorResume: Si la habitación NO existe, retorna una habitación
     * por defecto en lugar de lanzar error. El flujo CONTINÚA.
     */
    public Mono<Habitacion> findById(Long id) {
        log.info("================================================");
        log.info("BUSCANDO HABITACIÓN CON ID: {}", id);
        log.info("================================================");

        return habitacionRepository.findById(id)
                .doOnSubscribe(subscription -> log.info("onSubscribe: buscando habitación {}", id))
                .doOnNext(habitacion -> {
                    log.info("onNext: ✅ Habitación ENCONTRADA - ID: {}, Número: {}",
                            habitacion.getId(), habitacion.getNumero());
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("⚠️ Habitación con ID {} NO EXISTE en la base de datos", id);
                    return Mono.error(new ReactiveResourceNotFoundException(
                            "Habitación no encontrada con ID: " + id));
                }))
                // ⭐ onErrorResume: Si no existe, retorna habitación por defecto
                // EL FLUJO CONTINÚA en lugar de detenerse!
                .onErrorResume(error -> {
                    log.warn("================================================");
                    log.warn("🔄 onErrorResume ACTIVADO!");
                    log.warn("   Error: {}", error.getMessage());
                    log.warn("   Acción: Retornando habitación por defecto");
                    log.warn("   El flujo CONTINÚA en lugar de detenerse");
                    log.warn("================================================");

                    // Crear habitación de recuperación
                    Habitacion habitacionRecuperacion = new Habitacion();
                    habitacionRecuperacion.setId(-1L);
                    habitacionRecuperacion.setNumero("NO-ENCONTRADA");
                    habitacionRecuperacion.setTipo("N/A");
                    habitacionRecuperacion.setPrecio(0.0);
                    habitacionRecuperacion.setEstado("Error: " + error.getMessage());

                    return Mono.just(habitacionRecuperacion);
                })
                .doOnSuccess(habitacion -> {
                    if (habitacion.getId() == -1L) {
                        log.info("onSuccess: Retornada habitación de RECUPERACIÓN");
                    } else {
                        log.info("onSuccess: Retornada habitación real ID: {}", habitacion.getId());
                    }
                });
    }

    /**
     * Buscar habitación por ID SIN recuperación (para comparar comportamiento).
     * Este método LANZA ERROR si no encuentra la habitación.
     */
    public Mono<Habitacion> findByIdSinRecuperacion(Long id) {
        log.info("================================================");
        log.info("BUSCANDO HABITACIÓN (SIN RECUPERACIÓN) ID: {}", id);
        log.info("================================================");

        return habitacionRepository.findById(id)
                .doOnSubscribe(subscription -> log.info("onSubscribe: buscando habitación {}", id))
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("❌ ERROR: Habitación {} NO EXISTE - El flujo SE DETENDRÁ", id);
                    return Mono.error(new ReactiveResourceNotFoundException(
                            "Habitación no encontrada con ID: " + id));
                }))
                // ⚠️ SIN onErrorResume - el error se propaga y detiene el flujo
                .doOnError(error -> {
                    log.error("onError: {} - FLUJO DETENIDO", error.getMessage());
                });
    }

    /**
     * Guardar habitación de forma reactiva.
     * ⭐ DEMO onErrorResume: Si hay error de validación, retorna la habitación
     * con un estado de error en lugar de fallar completamente.
     */
    public Mono<Habitacion> save(Habitacion habitacion) {
        log.info("================================================");
        log.info("GUARDANDO HABITACIÓN: {}", habitacion.getNumero());
        log.info("================================================");

        return habitacionValidator.validate(habitacion)
                .doOnNext(h -> log.info("✅ Validación exitosa para habitación: {}", h.getNumero()))
                .flatMap(validHabitacion -> habitacionRepository.save(validHabitacion))
                .doOnSubscribe(subscription -> log.info("onSubscribe: guardando habitación"))
                .doOnNext(savedHabitacion -> log.info("onNext: ✅ Habitación guardada ID: {}", savedHabitacion.getId()))
                // ⭐ onErrorResume: Si hay error de validación, no falla completamente
                .onErrorResume(error -> {
                    log.warn("================================================");
                    log.warn("🔄 onErrorResume ACTIVADO al guardar!");
                    log.warn("   Error: {}", error.getMessage());
                    log.warn("   Acción: Retornando habitación sin guardar con error");
                    log.warn("================================================");

                    // Retornar habitación con estado de error para que el frontend sepa qué pasó
                    habitacion.setId(-1L);
                    habitacion.setEstado("ERROR: " + error.getMessage());
                    return Mono.just(habitacion);
                })
                .doOnSuccess(h -> {
                    if (h.getId() == -1L) {
                        log.warn("onSuccess: Habitación NO guardada (error recuperado)");
                    } else {
                        log.info("onSuccess: Habitación guardada exitosamente");
                    }
                });
    }

    /**
     * Eliminar habitación por ID de forma reactiva.
     * ⭐ DEMO onErrorResume: Si hay error al eliminar, no falla.
     */
    public Mono<Void> deleteById(Long id) {
        log.info("================================================");
        log.info("ELIMINANDO HABITACIÓN CON ID: {}", id);
        log.info("================================================");

        return habitacionRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("⚠️ Habitación {} no existe, no se puede eliminar", id);
                    return Mono.error(new ReactiveResourceNotFoundException(
                            "No se puede eliminar: habitación no encontrada con ID: " + id));
                }))
                .flatMap(habitacion -> {
                    log.info("Habitación {} encontrada, procediendo a eliminar", id);
                    return habitacionRepository.deleteById(id);
                })
                .doOnSubscribe(subscription -> log.info("onSubscribe: eliminando habitación {}", id))
                // ⭐ onErrorResume: Si hay error, el flujo continúa sin fallar
                .onErrorResume(error -> {
                    log.warn("================================================");
                    log.warn("🔄 onErrorResume ACTIVADO al eliminar!");
                    log.warn("   Error: {}", error.getMessage());
                    log.warn("   Acción: Ignorando error, flujo continúa");
                    log.warn("================================================");
                    return Mono.empty(); // Continúa sin error
                })
                .doOnSuccess(v -> log.info("onSuccess: Operación de eliminación completada"));
    }
}
