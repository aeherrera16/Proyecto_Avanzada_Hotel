package edu.espe.springlab.component;

import edu.espe.springlab.domain.Habitacion;
import edu.espe.springlab.domain.Reserva;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Componente con filtros y procesadores de flujos reactivos
 * Proporciona operadores personalizados para procesamiento de datos del hotel
 */
public class ReactiveFilters {
    
    private static final Logger log = LoggerFactory.getLogger(ReactiveFilters.class);
    
    /**
     * Filtra habitaciones por precio máximo
     */
    public static Flux<Habitacion> filterByMaxPrice(Flux<Habitacion> habitaciones, Double maxPrice) {
        return habitaciones
                .filter(habitacion -> habitacion.getPrecio() <= maxPrice)
                .doOnNext(h -> log.debug("Habitación filtrada por precio: {} - ${}", h.getNumero(), h.getPrecio()));
    }
    
    /**
     * Filtra habitaciones por tipo
     */
    public static Flux<Habitacion> filterByType(Flux<Habitacion> habitaciones, String tipo) {
        return habitaciones
                .filter(habitacion -> tipo.equalsIgnoreCase(habitacion.getTipo()))
                .doOnNext(h -> log.debug("Habitación filtrada por tipo: {} - {}", h.getNumero(), h.getTipo()));
    }
    
    /**
     * Filtra habitaciones disponibles con throttle para evitar cambios rápidos
     */
    public static Flux<Habitacion> filterAvailableWithThrottle(Flux<Habitacion> habitaciones, Duration throttleTime) {
        return habitaciones
                .filter(h -> "Disponible".equalsIgnoreCase(h.getEstado()))
                .sample(throttleTime)
                .doOnNext(h -> log.debug("Habitación disponible estable: {}", h.getNumero()));
    }
    
    /**
     * Filtra reservas futuras
     */
    public static Flux<Reserva> filterFutureReservas(Flux<Reserva> reservas) {
        LocalDate today = LocalDate.now();
        return reservas
                .filter(reserva -> reserva.getFechaEntrada().isAfter(today))
                .doOnNext(r -> log.debug("Reserva futura encontrada: {} - {}", r.getId(), r.getFechaEntrada()));
    }
    
    /**
     * Filtra reservas por rango de fechas
     */
    public static Flux<Reserva> filterReservasByDateRange(Flux<Reserva> reservas, LocalDate startDate, LocalDate endDate) {
        return reservas
                .filter(reserva -> !reserva.getFechaSalida().isBefore(startDate) && 
                                 !reserva.getFechaEntrada().isAfter(endDate))
                .doOnNext(r -> log.debug("Reserva en rango de fechas: {} - {} a {}", 
                        r.getId(), r.getFechaEntrada(), r.getFechaSalida()));
    }
    
    /**
     * Transformador que calcula precio total con descuento
     */
    public static Function<Flux<Habitacion>, Flux<Habitacion>> applyDiscount(Double discountPercentage) {
        return habitaciones -> habitaciones
                .map(habitacion -> {
                    Double precioConDescuento = habitacion.getPrecio() * (1 - discountPercentage / 100);
                    habitacion.setPrecio(precioConDescuento);
                    log.debug("Aplicando descuento del {}% a habitación {}: ${} -> ${}", 
                            discountPercentage, habitacion.getNumero(), habitacion.getPrecio(), precioConDescuento);
                    return habitacion;
                });
    }
    
    /**
     * Operador de ventana temporal para procesamiento por lotes
     */
    public static Flux<Flux<Habitacion>> windowByTime(Flux<Habitacion> habitaciones, Duration windowDuration) {
        return habitaciones
                .window(windowDuration)
                .doOnNext(window -> log.debug("Nueva ventana temporal iniciada"));
    }
    
    /**
     * Operador de buffer con conteo para procesamiento por lotes
     */
    public static Flux<java.util.List<Habitacion>> bufferByCount(Flux<Habitacion> habitaciones, int bufferSize) {
        return habitaciones
                .buffer(bufferSize)
                .doOnNext(buffer -> log.debug("Buffer de {} habitaciones procesado", buffer.size()));
    }
    
    /**
     * Transformador que agrega metadata a reservas
     */
    public static Function<Flux<Reserva>, Flux<Reserva>> enrichReservaMetadata() {
        return reservas -> reservas
                .map(reserva -> {
                    // Calcular duración en noches
                    long noches = java.time.temporal.ChronoUnit.DAYS.between(
                            reserva.getFechaEntrada(), reserva.getFechaSalida());
                    
                    // Calcular precio por noche
                    Double precioPorNoche = reserva.getPrecioTotal() / noches;
                    
                    log.debug("Reserva {} enriquecida: {} noches, ${}/noche", 
                            reserva.getId(), noches, precioPorNoche);
                    
                    return reserva;
                });
    }
    
    /**
     * Filtrado con retry para manejo de errores temporales
     */
    public static Mono<Habitacion> findHabitacionWithRetry(Mono<Habitacion> habitacionMono, int maxRetries) {
        return habitacionMono
                .retry(maxRetries)
                .doOnError(error -> log.error("Error al encontrar habitación después de {} reintentos", maxRetries, error))
                .doOnSuccess(h -> log.debug("Habitación encontrada exitosamente: {}", h != null ? h.getId() : null));
    }
    
    /**
     * Operador de timeout con fallback
     */
    public static Mono<Habitacion> findWithTimeout(Mono<Habitacion> habitacionMono, Duration timeout, Habitacion fallback) {
        return habitacionMono
                .timeout(timeout, Mono.just(fallback))
                .doOnNext(h -> log.debug("Habitación obtenida: {}", h.getId()))
                .doOnError(error -> log.error("Timeout al obtener habitación, usando fallback"));
    }
    
    /**
     * Filtrado combinado con múltiples criterios
     */
    public static Flux<Habitacion> filterByMultipleCriteria(Flux<Habitacion> habitaciones, 
                                                           String tipo, Double maxPrice, String estado) {
        Predicate<Habitacion> criteria = h -> 
            (tipo == null || tipo.equalsIgnoreCase(h.getTipo())) &&
            (maxPrice == null || h.getPrecio() <= maxPrice) &&
            (estado == null || estado.equalsIgnoreCase(h.getEstado()));
        
        return habitaciones
                .filter(criteria)
                .doOnNext(h -> log.debug("Habitación que cumple múltiples criterios: {}", h.getNumero()));
    }
    
    /**
     * Transformador para estadísticas en tiempo real
     */
    public static Function<Flux<Habitacion>, Flux<HabitacionStats>> calculateStats() {
        return habitaciones -> habitaciones
                .window(10) // Ventanas de 10 habitaciones
                .flatMap(window -> window
                        .collectList()
                        .map(lista -> {
                            Double avgPrice = lista.stream()
                                    .mapToDouble(Habitacion::getPrecio)
                                    .average()
                                    .orElse(0.0);
                            
                            long disponibles = lista.stream()
                                    .filter(h -> "Disponible".equalsIgnoreCase(h.getEstado()))
                                    .count();
                            
                            return new HabitacionStats(lista.size(), avgPrice, disponibles);
                        }))
                .doOnNext(stats -> log.debug("Estadísticas calculadas: {}", stats));
    }
    
    /**
     * Clase para estadísticas de habitaciones
     */
    public static class HabitacionStats {
        private final int total;
        private final Double avgPrice;
        private final long disponibles;
        
        public HabitacionStats(int total, Double avgPrice, long disponibles) {
            this.total = total;
            this.avgPrice = avgPrice;
            this.disponibles = disponibles;
        }
        
        // Getters
        public int getTotal() { return total; }
        public Double getAvgPrice() { return avgPrice; }
        public long getDisponibles() { return disponibles; }
        
        @Override
        public String toString() {
            return String.format("HabitacionStats{total=%d, avgPrice=%.2f, disponibles=%d}",
                    total, avgPrice, disponibles);
        }
    }
}
