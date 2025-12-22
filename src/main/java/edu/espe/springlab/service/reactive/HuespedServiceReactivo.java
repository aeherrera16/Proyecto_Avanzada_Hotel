package edu.espe.springlab.service.reactive;

import edu.espe.springlab.domain.Huesped;
import edu.espe.springlab.repository.HuespedRepository;
import edu.espe.springlab.exception.reactive.ReactiveResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HuespedServiceReactivo {

    private static final Logger log = LoggerFactory.getLogger(HuespedServiceReactivo.class);

    private final HuespedRepository huespedRepository;

    public HuespedServiceReactivo(HuespedRepository huespedRepository) {
        this.huespedRepository = huespedRepository;
    }

    public Flux<Huesped> findAll() {
        log.info("================================================");
        log.info("INICIANDO FLUJO REACTIVO REAL DE HUÉSPEDES");
        log.info("================================================");

        return huespedRepository.findAll()
                .doOnSubscribe(subscription -> {
                    log.info("onSubscribe: suscripción iniciada");
                })
                .doOnNext(huesped -> {
                    log.info("onNext: procesando huésped ID: {}, Nombre: {} {}",
                            huesped.getId(), huesped.getNombre(), huesped.getApellido());
                })
                .onErrorResume(error -> {
                    log.warn("🔄 onErrorResume: Error en BD - Retornando lista vacía");
                    return Flux.empty();
                })
                .doOnComplete(() -> {
                    log.info("onComplete: flujo de huéspedes finalizado");
                });
    }

    /**
     * Buscar huésped por ID CON recuperación (onErrorResume)
     */
    public Mono<Huesped> findById(Long id) {
        log.info("================================================");
        log.info("BUSCANDO HUÉSPED CON ID: {}", id);
        log.info("================================================");

        return huespedRepository.findById(id)
                .doOnSubscribe(s -> log.info("onSubscribe: buscando huésped {}", id))
                .doOnNext(huesped -> log.info("onNext: ✅ Huésped encontrado: {} {}",
                        huesped.getNombre(), huesped.getApellido()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("⚠️ Huésped con ID {} NO EXISTE", id);
                    return Mono.error(new ReactiveResourceNotFoundException(
                            "Huésped no encontrado con ID: " + id));
                }))
                .onErrorResume(error -> {
                    log.warn("================================================");
                    log.warn("🔄 onErrorResume ACTIVADO!");
                    log.warn("   Error: {}", error.getMessage());
                    log.warn("   Acción: Retornando huésped por defecto");
                    log.warn("================================================");

                    Huesped huespedRecuperacion = new Huesped();
                    huespedRecuperacion.setId(-1L);
                    huespedRecuperacion.setNombre("NO ENCONTRADO");
                    huespedRecuperacion.setApellido("");
                    huespedRecuperacion.setCedula("N/A");
                    huespedRecuperacion.setEmail("error@recuperado.com");
                    huespedRecuperacion.setTelefono("Error: " + error.getMessage());

                    return Mono.just(huespedRecuperacion);
                });
    }

    /**
     * Buscar huésped SIN recuperación (para demo)
     */
    public Mono<Huesped> findByIdSinRecuperacion(Long id) {
        log.info("BUSCANDO HUÉSPED (SIN RECUPERACIÓN) ID: {}", id);

        return huespedRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("❌ ERROR: Huésped {} NO EXISTE - El flujo SE DETENDRÁ", id);
                    return Mono.error(new ReactiveResourceNotFoundException(
                            "Huésped no encontrado con ID: " + id));
                }));
    }

    public Mono<Huesped> save(Huesped huesped) {
        log.info("Guardando huésped: {} {}", huesped.getNombre(), huesped.getApellido());

        return huespedRepository.save(huesped)
                .doOnNext(saved -> log.info("✅ Huésped guardado con ID: {}", saved.getId()))
                .onErrorResume(error -> {
                    log.warn("🔄 onErrorResume al guardar: {}", error.getMessage());
                    huesped.setId(-1L);
                    huesped.setTelefono("ERROR: " + error.getMessage());
                    return Mono.just(huesped);
                });
    }

    public Mono<Huesped> update(Long id, Huesped huesped) {
        log.info("Actualizando huésped con ID: {}", id);

        return huespedRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Huésped no encontrado con ID: " + id)))
                .flatMap(existingHuesped -> {
                    existingHuesped.setNombre(huesped.getNombre());
                    existingHuesped.setApellido(huesped.getApellido());
                    existingHuesped.setCedula(huesped.getCedula());
                    existingHuesped.setTelefono(huesped.getTelefono());
                    existingHuesped.setEmail(huesped.getEmail());
                    existingHuesped.setNacionalidad(huesped.getNacionalidad());
                    return huespedRepository.save(existingHuesped);
                })
                .doOnNext(updated -> log.info("✅ Huésped actualizado: {}", updated.getNombre()))
                .onErrorResume(error -> {
                    log.warn("🔄 onErrorResume al actualizar: {}", error.getMessage());
                    huesped.setId(-1L);
                    huesped.setTelefono("ERROR: " + error.getMessage());
                    return Mono.just(huesped);
                });
    }

    public Mono<Void> deleteById(Long id) {
        log.info("Eliminando huésped con ID: {}", id);

        return huespedRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Huésped no encontrado con ID: " + id)))
                .flatMap(h -> huespedRepository.deleteById(id))
                .doOnSuccess(unused -> log.info("✅ Huésped eliminado con ID: {}", id))
                .onErrorResume(error -> {
                    log.warn("🔄 onErrorResume al eliminar: {}", error.getMessage());
                    return Mono.empty();
                });
    }
}
