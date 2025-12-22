package edu.espe.springlab.service.reactive;

// Importaciones del dominio, repositorio y librerías de Spring/Reactor
import edu.espe.springlab.domain.Pago;
import edu.espe.springlab.repository.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Indica que esta clase es un servicio gestionado por Spring
@Service
public class PagoServiceReactivo {

    // Logger para registrar eventos del servicio
    private static final Logger log = LoggerFactory.getLogger(PagoServiceReactivo.class);

    // Repositorio inyectado para acceder a la base de datos de forma reactiva
    private final PagoRepository pagoRepository;

    // Constructor para inyección de dependencias
    public PagoServiceReactivo(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    // Devuelve todos los pagos como un flujo reactivo (Flux) con logging detallado
    public Flux<Pago> findAll() {
        log.info("================================================");
        log.info("INICIANDO FLUJO REACTIVO REAL DE PAGOS");
        log.info("================================================");

        return pagoRepository.findAll()
                .doOnSubscribe(subscription -> {
                    log.info("onSubscribe: suscripción iniciada");
                    log.info("Backpressure -> solicitando pagos");
                    log.info("Backpressure -> solicitando pagos ilimitados");
                })
                .doOnNext(pago -> {
                    log.info("onNext: procesando pago ID: {}, Reserva: {}, Monto: ${}",
                            pago.getId(),
                            pago.getReservaId(),
                            pago.getMonto());
                    log.info("Filtro: pago {} - Método: {}, Estado: {}",
                            pago.getId(),
                            pago.getMetodoPago(),
                            pago.getEstado());
                })
                .doOnComplete(() -> {
                    log.info("================================================");
                    log.info("FLUJO REACTIVO DE PAGOS COMPLETADO");
                    log.info("onComplete: flujo de pagos finalizado");
                    log.info("================================================");
                })
                .doOnError(error -> {
                    log.error("================================================");
                    log.error("ERROR EN FLUJO REACTIVO DE PAGOS");
                    log.error("onError: {}", error.getMessage());
                    log.error("================================================");
                });
    }

    // Busca un pago por ID y registra su resultado o error
    public Mono<Pago> findById(Long id) {
        log.info("Buscando pago por ID: {}", id);
        return pagoRepository.findById(id)
                .doOnNext(pago -> log.info("Pago encontrado: {}", pago != null ? pago.getMonto() : "null"))
                .doOnError(error -> log.error("Error buscando pago por ID {}: {}", id, error.getMessage()));
    }

    // Guarda un nuevo pago, aplicando valores por defecto si faltan datos
    public Mono<Pago> save(Pago pago) {
        log.info("Guardando pago: {}", pago.getMonto());

        // Asigna valores por defecto si no están presentes
        if (pago.getReservaId() == null) {
            pago.setReservaId(1L); // Valor por defecto para demostración
        }

        if (pago.getMetodoPago() == null || pago.getMetodoPago().trim().isEmpty()) {
            pago.setMetodoPago("Efectivo"); // Valor por defecto
        }

        if (pago.getEstado() == null || pago.getEstado().trim().isEmpty()) {
            pago.setEstado("PENDIENTE"); // Valor por defecto
        }

        return pagoRepository.save(pago)
                .doOnNext(saved -> log.info("Pago guardado con ID: {}", saved.getId()))
                .doOnError(error -> log.error("Error guardando pago: {}", error.getMessage()));
    }

    // Actualiza un pago existente; falla si no existe
    public Mono<Pago> update(Long id, Pago pago) {
        log.info("Actualizando pago con ID: {}", id);
        return pagoRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Pago no encontrado con ID: " + id))) // Manejo explícito de "no encontrado"
                .flatMap(existingPago -> {
                    // Actualiza los campos con los nuevos valores
                    existingPago.setReservaId(pago.getReservaId());
                    existingPago.setMonto(pago.getMonto());
                    existingPago.setMetodoPago(pago.getMetodoPago());
                    existingPago.setEstado(pago.getEstado());
                    return pagoRepository.save(existingPago);
                })
                .doOnNext(updated -> log.info("Pago actualizado: {}", updated.getMonto()))
                .doOnError(error -> log.error("Error actualizando pago: {}", error.getMessage()));
    }

    // Elimina un pago por su ID y registra el resultado
    public Mono<Void> deleteById(Long id) {
        log.info("Eliminando pago con ID: {}", id);
        return pagoRepository.deleteById(id)
                .doOnSuccess(unused -> log.info("Pago eliminado con ID: {}", id))
                .doOnError(error -> log.error("Error eliminando pago: {}", error.getMessage()));
    }
}