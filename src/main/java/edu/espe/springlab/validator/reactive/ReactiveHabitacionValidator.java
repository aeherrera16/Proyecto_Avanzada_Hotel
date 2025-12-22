package edu.espe.springlab.validator.reactive;

import edu.espe.springlab.domain.Habitacion;
import edu.espe.springlab.exception.reactive.ReactiveValidationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReactiveHabitacionValidator {
    
    /**
     * Valida una habitación de forma reactiva
     */
    public Mono<Habitacion> validate(Habitacion habitacion) {
        return Mono.fromCallable(() -> {
            // Validar número de habitación
            if (habitacion.getNumero() == null || habitacion.getNumero().trim().isEmpty()) {
                throw new ReactiveValidationException("El número de habitación es obligatorio");
            }
            
            // Validar tipo de habitación
            if (habitacion.getTipo() == null || habitacion.getTipo().trim().isEmpty()) {
                throw new ReactiveValidationException("El tipo de habitación es obligatorio");
            }
            
            // Validar que el tipo sea válido
            String[] tiposValidos = {"Simple", "Individual", "Doble", "Suite", "Presidencial", "Suite Presidencial"};
            boolean tipoValido = false;
            String tipoNormalizado = habitacion.getTipo().trim();
            for (String tipo : tiposValidos) {
                if (tipo.trim().equals(tipoNormalizado)) {
                    tipoValido = true;
                    break;
                }
            }
            
            if (!tipoValido) {
                throw new ReactiveValidationException("Tipo no válido. Debe ser: Simple, Individual, Doble, Suite, Presidencial o Suite Presidencial");
            }
            
            // Validar precio por noche
            if (habitacion.getPrecio() == null || habitacion.getPrecio() <= 0) {
                throw new ReactiveValidationException("El precio por noche debe ser mayor a 0");
            }
            
            // Validar estado
            if (habitacion.getEstado() == null || habitacion.getEstado().trim().isEmpty()) {
                throw new ReactiveValidationException("El estado de la habitación es obligatorio");
            }
            
            // Validar que el estado sea válido
            String[] estadosValidos = {"Disponible", "Ocupada", "Mantenimiento"};
            boolean estadoValido = false;
            for (String estado : estadosValidos) {
                if (estado.equals(habitacion.getEstado())) {
                    estadoValido = true;
                    break;
                }
            }
            
            if (!estadoValido) {
                throw new ReactiveValidationException("Estado no válido. Debe ser: Disponible, Ocupada o Mantenimiento");
            }
            
            return habitacion;
        });
    }
}
