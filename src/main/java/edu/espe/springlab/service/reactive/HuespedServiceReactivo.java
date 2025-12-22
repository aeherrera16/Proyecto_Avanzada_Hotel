package edu.espe.springlab.service.reactive;

import edu.espe.springlab.domain.Huesped;
import edu.espe.springlab.repository.HuespedRepository;
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
                    log.info("Backpressure -> solicitando huéspedes");
                    log.info("Backpressure -> solicitando huéspedes ilimitados");
                })
                .doOnNext(huesped -> {
                    log.info("onNext: procesando huésped ID: {}, Nombre: {} {}, Cédula: {}", 
                            huesped.getId(), 
                            huesped.getNombre(), 
                            huesped.getApellido(), 
                            huesped.getCedula());
                    log.info("Filtro: huésped {} {} - Email: {}", 
                            huesped.getNombre(), 
                            huesped.getApellido(), 
                            huesped.getEmail());
                })
                .doOnComplete(() -> {
                    log.info("================================================");
                    log.info("FLUJO REACTIVO DE HUÉSPEDES COMPLETADO");
                    log.info("onComplete: flujo de huéspedes finalizado");
                    log.info("================================================");
                })
                .doOnError(error -> {
                    log.error("================================================");
                    log.error("ERROR EN FLUJO REACTIVO DE HUÉSPEDES");
                    log.error("onError: {}", error.getMessage());
                    log.error("================================================");
                });
    }
    
    public Mono<Huesped> findById(Long id) {
        log.info("Buscando huésped por ID: {}", id);
        return huespedRepository.findById(id)
                .doOnNext(huesped -> log.info("Huésped encontrado: {}", huesped != null ? huesped.getNombre() : "null"))
                .doOnError(error -> log.error("Error buscando huésped por ID {}: {}", id, error.getMessage()));
    }
    
    public Mono<Huesped> save(Huesped huesped) {
        log.info("Guardando huésped: {}", huesped.getNombre());
        return huespedRepository.save(huesped)
                .doOnNext(saved -> log.info("Huésped guardado con ID: {}", saved.getId()))
                .doOnError(error -> log.error("Error guardando huésped: {}", error.getMessage()));
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
                .doOnNext(updated -> log.info("Huésped actualizado: {}", updated.getNombre()))
                .doOnError(error -> log.error("Error actualizando huésped: {}", error.getMessage()));
    }
    
    public Mono<Void> deleteById(Long id) {
        log.info("Eliminando huésped con ID: {}", id);
        return huespedRepository.deleteById(id)
                .doOnSuccess(unused -> log.info("Huésped eliminado con ID: {}", id))
                .doOnError(error -> log.error("Error eliminando huésped: {}", error.getMessage()));
    }
}
