package edu.espe.springlab.service.reactive;

import edu.espe.springlab.domain.Reserva;
import edu.espe.springlab.domain.Huesped;
import edu.espe.springlab.domain.Habitacion;
import edu.espe.springlab.dto.ReservaDetallada;
import edu.espe.springlab.dto.ReservaCompletaDTO;
import edu.espe.springlab.repository.ReservaRepository;
import edu.espe.springlab.repository.HuespedRepository;
import edu.espe.springlab.repository.HabitacionRepository;
import edu.espe.springlab.repository.PagoRepository;
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
    private final HuespedRepository huespedRepository;
    private final HabitacionRepository habitacionRepository;
    private final PagoRepository pagoRepository;
    
    public ReservaServiceReactivo(ReservaRepository reservaRepository, 
                                  HuespedRepository huespedRepository, 
                                  HabitacionRepository habitacionRepository,
                                  PagoRepository pagoRepository) {
        this.reservaRepository = reservaRepository;
        this.huespedRepository = huespedRepository;
        this.habitacionRepository = habitacionRepository;
        this.pagoRepository = pagoRepository;
    }
    
    /**
     * Obtener todas las reservas de forma reactiva
     */
    public Flux<Reserva> findAll() {
        log.info("================================================");
        log.info("INICIANDO FLUJO REACTIVO REAL DE RESERVAS");
        log.info("================================================");
        
        return reservaRepository.findAll()
                .doOnSubscribe(subscription -> {
                    log.info("onSubscribe: suscripción iniciada");
                    log.info("Backpressure -> solicitando reservas");
                    log.info("Backpressure -> solicitando reservas ilimitadas");
                })
                .doOnNext(reserva -> {
                    log.info("onNext: procesando reserva ID: {}, Habitación: {}, Estado: {}", 
                            reserva.getId(), 
                            reserva.getHabitacionId(), 
                            reserva.getEstado());
                    log.info("Filtro: reserva {} - Fecha Entrada: {}, Salida: {}", 
                            reserva.getId(),
                            reserva.getFechaEntrada(),
                            reserva.getFechaSalida());
                })
                .doOnComplete(() -> {
                    log.info("================================================");
                    log.info("FLUJO REACTIVO DE RESERVAS COMPLETADO");
                    log.info("onComplete: flujo de reservas finalizado");
                    log.info("================================================");
                })
                .doOnError(error -> {
                    log.error("================================================");
                    log.error("ERROR EN FLUJO REACTIVO DE RESERVAS");
                    log.error("onError: {}", error.getMessage());
                    log.error("================================================");
                });
    }
    
    /**
     * Buscar reserva por ID con datos completos de forma reactiva
     */
    public Mono<ReservaDetallada> findByIdConDetalles(Long id) {
        log.info("Buscando reserva con detalles completos para ID: {}", id);
        
        return reservaRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Reserva no encontrada con ID: " + id)))
                .flatMap(reserva -> {
                    log.info("Reserva encontrada: {}, buscando datos de huésped y habitación", reserva.getId());
                    
                    // Buscar huésped
                    Mono<Huesped> huespedMono = huespedRepository.findById(reserva.getHuespedId())
                            .doOnNext(huesped -> log.debug("Huésped encontrado: {}", huesped.getNombre()))
                            .switchIfEmpty(Mono.fromCallable(() -> {
                                Huesped h = new Huesped();
                                h.setNombre("No encontrado");
                                h.setApellido("");
                                h.setEmail("");
                                h.setTelefono("");
                                h.setCedula("");
                                return h;
                            }));
                    
                    // Buscar habitación
                    Mono<Habitacion> habitacionMono = habitacionRepository.findById(reserva.getHabitacionId())
                            .doOnNext(habitacion -> log.debug("Habitación encontrada: {}", habitacion.getNumero()))
                            .switchIfEmpty(Mono.fromCallable(() -> {
                                Habitacion h = new Habitacion();
                                h.setNumero("No encontrada");
                                h.setTipo("");
                                h.setPrecio(0.0);
                                return h;
                            }));
                    
                    return Mono.zip(huespedMono, habitacionMono)
                            .map(tuple -> {
                                ReservaDetallada detallada = new ReservaDetallada();
                                detallada.setId(reserva.getId());
                                detallada.setHuespedId(reserva.getHuespedId());
                                detallada.setHabitacionId(reserva.getHabitacionId());
                                detallada.setFechaEntrada(reserva.getFechaEntrada());
                                detallada.setFechaSalida(reserva.getFechaSalida());
                                detallada.setPrecioTotal(reserva.getPrecioTotal());
                                detallada.setEstado(reserva.getEstado());
                                
                                // Datos del huésped
                                Huesped huesped = tuple.getT1();
                                detallada.setNombreHuesped(huesped.getNombre() + " " + huesped.getApellido());
                                detallada.setEmailHuesped(huesped.getEmail());
                                detallada.setTelefonoHuesped(huesped.getTelefono());
                                detallada.setCedulaHuesped(huesped.getCedula());
                                
                                // Datos de la habitación
                                Habitacion habitacion = tuple.getT2();
                                detallada.setNumeroHabitacion(habitacion.getNumero());
                                detallada.setTipoHabitacion(habitacion.getTipo());
                                detallada.setPrecioHabitacion(habitacion.getPrecio());
                                
                                return detallada;
                            });
                })
                .doOnNext(detallada -> log.info("Reserva detallada completada para ID: {}", id))
                .doOnError(error -> log.error("Error al buscar reserva detallada con ID: {}", id, error));
    }
    
    /**
     * Buscar reserva por ID de forma reactiva
     */
    public Mono<Reserva> findById(Long id) {
        log.info("Buscando reserva con ID: {}", id);
        
        return reservaRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Reserva no encontrada con ID: " + id)))
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
     * Guardar reserva completa de forma transaccional (huésped + reserva + pago)
     */
    public Mono<ReservaDetallada> saveReservaCompleta(ReservaCompletaDTO reservaCompleta) {
        log.info("Iniciando guardado transaccional de reserva completa");
        
        // 1. Crear huésped
        Huesped huesped = new Huesped();
        huesped.setNombre(reservaCompleta.getNombre());
        huesped.setApellido(reservaCompleta.getApellido());
        huesped.setCedula(reservaCompleta.getCedula());
        huesped.setTelefono(reservaCompleta.getTelefono());
        huesped.setEmail(reservaCompleta.getEmail());
        huesped.setNacionalidad(reservaCompleta.getNacionalidad());
        
        return huespedRepository.save(huesped)
                .flatMap(huespedGuardado -> {
                    log.info("Huésped guardado: {}", huespedGuardado.getId());
                    
                    // 2. Crear reserva
                    Reserva reserva = new Reserva();
                    reserva.setHuespedId(huespedGuardado.getId());
                    reserva.setHabitacionId(reservaCompleta.getHabitacionId());
                    reserva.setFechaEntrada(reservaCompleta.getFechaEntrada());
                    reserva.setFechaSalida(reservaCompleta.getFechaSalida());
                    reserva.setPrecioTotal(reservaCompleta.getPrecioTotal().doubleValue());
                    reserva.setEstado("Confirmada");
                    
                    return reservaRepository.save(reserva)
                            .flatMap(reservaGuardada -> {
                                log.info("Reserva guardada: {}", reservaGuardada.getId());
                                
                                // 3. Crear pago
                                edu.espe.springlab.domain.Pago pago = new edu.espe.springlab.domain.Pago();
                                pago.setReservaId(reservaGuardada.getId());
                                pago.setMonto(reservaCompleta.getPrecioTotal().doubleValue());
                                pago.setFechaPago(java.time.LocalDateTime.now());
                                pago.setMetodoPago(reservaCompleta.getMetodoPago());
                                pago.setEstado("Completado");
                                
                                return pagoRepository.save(pago)
                                        .flatMap(pagoGuardado -> {
                                            log.info("Pago guardado: {}", pagoGuardado.getId());
                                            
                                            // 4. Actualizar habitación a ocupada
                                            return habitacionRepository.findById(reservaCompleta.getHabitacionId())
                                                    .flatMap(habitacion -> {
                                                        habitacion.setEstado("Ocupada");
                                                        return habitacionRepository.save(habitacion);
                                                    })
                                                    .map(habitacionActualizada -> {
                                                        log.info("Habitación actualizada a ocupada: {}", habitacionActualizada.getId());
                                                        
                                                        // 5. Retornar reserva detallada
                                                        ReservaDetallada detallada = new ReservaDetallada();
                                                        detallada.setId(reservaGuardada.getId());
                                                        detallada.setHuespedId(huespedGuardado.getId());
                                                        detallada.setHabitacionId(reservaGuardada.getHabitacionId());
                                                        detallada.setFechaEntrada(reservaGuardada.getFechaEntrada());
                                                        detallada.setFechaSalida(reservaGuardada.getFechaSalida());
                                                        detallada.setPrecioTotal(reservaGuardada.getPrecioTotal());
                                                        detallada.setEstado(reservaGuardada.getEstado());
                                                        
                                                        // Datos del huésped
                                                        detallada.setNombreHuesped(huespedGuardado.getNombre() + " " + huespedGuardado.getApellido());
                                                        detallada.setEmailHuesped(huespedGuardado.getEmail());
                                                        detallada.setTelefonoHuesped(huespedGuardado.getTelefono());
                                                        detallada.setCedulaHuesped(huespedGuardado.getCedula());
                                                        
                                                        // Datos de la habitación
                                                        detallada.setNumeroHabitacion(habitacionActualizada.getNumero());
                                                        detallada.setTipoHabitacion(habitacionActualizada.getTipo());
                                                        detallada.setPrecioHabitacion(habitacionActualizada.getPrecio());
                                                        
                                                        return detallada;
                                                    });
                                        });
                            });
                })
                .doOnSuccess(detallada -> log.info("Reserva completa guardada exitosamente: {}", detallada.getId()))
                .doOnError(error -> {
                    log.error("Error en guardado transaccional de reserva completa", error);
                    // En un sistema real, aquí se haría rollback de la transacción
                })
                .cast(ReservaDetallada.class);
    }
    
    /**
     * Guardar reserva pendiente de forma reactiva
     */
    public Mono<Reserva> savePendiente(Reserva reserva) {
        log.info("Guardando reserva pendiente: {}", reserva);
        
        // Asignar estado pendiente si no tiene estado
        if (reserva.getEstado() == null || reserva.getEstado().isEmpty()) {
            reserva.setEstado("Pendiente");
        }
        
        // Validar campos mínimos para reserva pendiente
        return validateReservaPendiente(reserva)
                .flatMap(reservaValidada -> reservaRepository.save(reservaValidada))
                .doOnSubscribe(subscription -> log.debug("Suscripción a savePendiente de reserva"))
                .doOnNext(reservaGuardada -> log.info("Reserva pendiente guardada exitosamente: {}", reservaGuardada))
                .doOnError(error -> log.error("Error al guardar reserva pendiente", error));
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
            // Validar ID de huésped (opcional para permitir creación)
            if (reserva.getHuespedId() == null) {
                reserva.setHuespedId(1L); // Valor por defecto para demostración
            }
            
            // Validar ID de habitación (opcional para permitir creación)
            if (reserva.getHabitacionId() == null) {
                reserva.setHabitacionId(1L); // Valor por defecto para demostración
            }
            
            // Validar fechas
            if (reserva.getFechaEntrada() == null) {
                throw new RuntimeException("La fecha de entrada es obligatoria");
            }
            
            if (reserva.getFechaSalida() == null) {
                throw new RuntimeException("La fecha de salida es obligatoria");
            }
            
            // Validar que la fecha de salida sea posterior a la entrada
            if (reserva.getFechaSalida().isBefore(reserva.getFechaEntrada())) {
                throw new RuntimeException("La fecha de salida debe ser posterior a la fecha de entrada");
            }
            
            // Validar que la fecha de entrada no sea en el pasado
            if (reserva.getFechaEntrada().isBefore(LocalDate.now())) {
                throw new RuntimeException("La fecha de entrada no puede ser en el pasado");
            }
            
            // Validar precio total
            if (reserva.getPrecioTotal() == null || reserva.getPrecioTotal() <= 0) {
                throw new RuntimeException("El precio total debe ser mayor a 0");
            }
            
            // Validar estado
            if (reserva.getEstado() == null || reserva.getEstado().trim().isEmpty()) {
                throw new RuntimeException("El estado de la reserva es obligatorio");
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
                throw new RuntimeException("Estado no válido. Estados permitidos: Confirmada, Pendiente, Cancelada");
            }
            
            return reserva;
        });
    }
    
    /**
     * Validar reserva pendiente de forma reactiva (menos estricto)
     */
    private Mono<Reserva> validateReservaPendiente(Reserva reserva) {
        return Mono.fromCallable(() -> {
            // Para reservas pendientes, solo validar campos mínimos
            
            // Validar ID de huésped (opcional para permitir creación)
            if (reserva.getHuespedId() == null) {
                reserva.setHuespedId(1L); // Valor por defecto para demostración
            }
            
            // Validar ID de habitación (opcional para permitir creación)
            if (reserva.getHabitacionId() == null) {
                reserva.setHabitacionId(1L); // Valor por defecto para demostración
            }
            
            // Para pendientes, las fechas pueden ser nulas inicialmente
            if (reserva.getFechaEntrada() == null) {
                reserva.setFechaEntrada(LocalDate.now().plusDays(1)); // Valor por defecto
            }
            
            if (reserva.getFechaSalida() == null) {
                reserva.setFechaSalida(LocalDate.now().plusDays(2)); // Valor por defecto
            }
            
            // Validar que la fecha de salida sea posterior a la entrada
            if (reserva.getFechaSalida().isBefore(reserva.getFechaEntrada())) {
                throw new RuntimeException("La fecha de salida debe ser posterior a la fecha de entrada");
            }
            
            // Para pendientes, el precio puede ser 0 inicialmente
            if (reserva.getPrecioTotal() == null) {
                reserva.setPrecioTotal(0.0); // Valor por defecto
            }
            
            // Asegurar estado pendiente
            reserva.setEstado("Pendiente");
            
            return reserva;
        });
    }
}
