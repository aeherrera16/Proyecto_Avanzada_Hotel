package edu.espe.springlab.web.controller;


import edu.espe.springlab.domain.Reserva;
import edu.espe.springlab.service.reactive.ReservaServiceReactivo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reactive/reservas")
@Tag(name = "Controlador Reactivo de Reservas", description = "Endpoints reactivos para gestión de reservas")
public class ReservaControllerReactivo {

    private static final Logger log = LoggerFactory.getLogger(ReservaControllerReactivo.class);

    private final ReservaServiceReactivo reservaService;

    public ReservaControllerReactivo(ReservaServiceReactivo reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las reservas", description = "Retorna todas las reservas existentes de forma reactiva")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Flux<Reserva> findAll() {
        log.info("Solicitud GET a /api/reactive/reservas");
        return reservaService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID", description = "Retorna una reserva específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Reserva>> findById(
            @Parameter(description = "ID de la reserva a buscar")
            @PathVariable Long id) {
        log.info("Solicitud GET a /api/reactive/reservas/{}", id);
        return reservaService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear reserva", description = "Crea una nueva reserva")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Reserva>> create(@RequestBody Reserva reserva) {
        log.info("Solicitud POST a /api/reactive/reservas");
        return reservaService.save(reserva)
                .map(savedReserva -> ResponseEntity.status(HttpStatus.CREATED).body(savedReserva));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar reserva", description = "Actualiza una reserva existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Reserva>> update(
            @Parameter(description = "ID de la reserva a actualizar")
            @PathVariable Long id,
            @RequestBody Reserva reserva) {
        log.info("Solicitud PUT a /api/reactive/reservas/{}", id);
        return reservaService.findById(id)
                .flatMap(existingReserva -> {
                    existingReserva.setHuespedId(reserva.getHuespedId());
                    existingReserva.setHabitacionId(reserva.getHabitacionId());
                    existingReserva.setFechaEntrada(reserva.getFechaEntrada());
                    existingReserva.setFechaSalida(reserva.getFechaSalida());
                    existingReserva.setPrecioTotal(reserva.getPrecioTotal());
                    existingReserva.setEstado(reserva.getEstado());
                    return reservaService.save(existingReserva);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reserva", description = "Elimina una reserva por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reserva eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Void>> deleteById(
            @Parameter(description = "ID de la reserva a eliminar")
            @PathVariable Long id) {
        log.info("Solicitud DELETE a /api/reactive/reservas/{}", id);
        return reservaService.deleteById(id)
                .thenReturn(ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/huesped/{huespedId}")
    @Operation(summary = "Obtener reservas por huésped", description = "Retorna todas las reservas de un huésped específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Flux<Reserva> findByHuespedId(
            @Parameter(description = "ID del huésped")
            @PathVariable Long huespedId) {
        log.info("Solicitud GET a /api/reactive/reservas/huesped/{}", huespedId);
        return reservaService.findByHuespedId(huespedId);
    }

    @GetMapping("/habitacion/{habitacionId}")
    @Operation(summary = "Obtener reservas por habitación", description = "Retorna todas las reservas de una habitación específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Flux<Reserva> findByHabitacionId(
            @Parameter(description = "ID de la habitación")
            @PathVariable Long habitacionId) {
        log.info("Solicitud GET a /api/reactive/reservas/habitacion/{}", habitacionId);
        return reservaService.findByHabitacionId(habitacionId);
    }
}
