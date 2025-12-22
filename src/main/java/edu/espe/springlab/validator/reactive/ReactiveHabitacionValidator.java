package edu.espe.springlab.validator.reactive;

import edu.espe.springlab.domain.Habitacion;
import edu.espe.springlab.exception.reactive.ReactiveValidationException;
import edu.espe.springlab.repository.HabitacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReactiveHabitacionValidator {

    private static final Logger log = LoggerFactory.getLogger(ReactiveHabitacionValidator.class);

    private final HabitacionRepository habitacionRepository;

    public ReactiveHabitacionValidator(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    /**
     * Valida una habitación de forma reactiva.
     * Incluye validación de duplicados para el número de habitación.
     */
    public Mono<Habitacion> validate(Habitacion habitacion) {
        return validateBasicFields(habitacion)
                .flatMap(this::validateNoDuplicate);
    }

    /**
     * Validación básica de campos requeridos
     */
    private Mono<Habitacion> validateBasicFields(Habitacion habitacion) {
        return Mono.fromCallable(() -> {
            // Validar número de habitación
            if (habitacion.getNumero() == null || habitacion.getNumero().trim().isEmpty()) {
                log.warn("Validación fallida: número de habitación vacío");
                throw new ReactiveValidationException("El número de habitación es obligatorio");
            }

            // Validar tipo de habitación
            if (habitacion.getTipo() == null || habitacion.getTipo().trim().isEmpty()) {
                log.warn("Validación fallida: tipo de habitación vacío");
                throw new ReactiveValidationException("El tipo de habitación es obligatorio");
            }

            // Validar que el tipo sea válido
            String[] tiposValidos = { "Simple", "Individual", "Doble", "Suite", "Presidencial", "Suite Presidencial" };
            boolean tipoValido = false;
            String tipoNormalizado = habitacion.getTipo().trim();
            for (String tipo : tiposValidos) {
                if (tipo.trim().equals(tipoNormalizado)) {
                    tipoValido = true;
                    break;
                }
            }

            if (!tipoValido) {
                log.warn("Validación fallida: tipo inválido '{}'", habitacion.getTipo());
                throw new ReactiveValidationException(
                        "Tipo no válido. Debe ser: Simple, Individual, Doble, Suite, Presidencial o Suite Presidencial");
            }

            // Validar precio por noche
            if (habitacion.getPrecio() == null || habitacion.getPrecio() <= 0) {
                log.warn("Validación fallida: precio inválido {}", habitacion.getPrecio());
                throw new ReactiveValidationException("El precio por noche debe ser mayor a 0");
            }

            // Validar estado
            if (habitacion.getEstado() == null || habitacion.getEstado().trim().isEmpty()) {
                log.warn("Validación fallida: estado vacío");
                throw new ReactiveValidationException("El estado de la habitación es obligatorio");
            }

            // Validar que el estado sea válido
            String[] estadosValidos = { "Disponible", "Ocupada", "Mantenimiento" };
            boolean estadoValido = false;
            for (String estado : estadosValidos) {
                if (estado.equals(habitacion.getEstado())) {
                    estadoValido = true;
                    break;
                }
            }

            if (!estadoValido) {
                log.warn("Validación fallida: estado inválido '{}'", habitacion.getEstado());
                throw new ReactiveValidationException(
                        "Estado no válido. Debe ser: Disponible, Ocupada o Mantenimiento");
            }

            log.info("✅ Validación básica exitosa para habitación: {}", habitacion.getNumero());
            return habitacion;
        });
    }

    /**
     * Valida que no exista otra habitación con el mismo número.
     * ⭐ DEMO: Si ya existe, lanza error que será manejado por onErrorResume en el
     * servicio.
     */
    private Mono<Habitacion> validateNoDuplicate(Habitacion habitacion) {
        log.info("Verificando si existe habitación duplicada con número: {}", habitacion.getNumero());

        return habitacionRepository.findByNumero(habitacion.getNumero())
                .flatMap(existente -> {
                    // Si encontramos una habitación con el mismo número
                    // Verificar si es la misma (para edición) o es diferente (duplicado)
                    if (habitacion.getId() == null || !habitacion.getId().equals(existente.getId())) {
                        log.error("❌ Habitación duplicada detectada! Número '{}' ya existe con ID: {}",
                                existente.getNumero(), existente.getId());
                        return Mono.<Habitacion>error(new ReactiveValidationException(
                                "Ya existe una habitación con el número '" + habitacion.getNumero() +
                                        "'. Por favor, use un número diferente."));
                    }
                    // Es la misma habitación (edición), permitir
                    log.info("✅ Habitación {} es la misma (edición permitida)", habitacion.getNumero());
                    return Mono.just(habitacion);
                })
                // Si no existe ninguna con ese número, está bien
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("✅ Número de habitación '{}' está disponible", habitacion.getNumero());
                    return Mono.just(habitacion);
                }));
    }
}
