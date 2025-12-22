package edu.espe.springlab.controller;

import edu.espe.springlab.domain.Habitacion;
import edu.espe.springlab.service.reactive.HabitacionServiceReactivo;
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
@RequestMapping("/api/reactive/habitaciones")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
                RequestMethod.DELETE, RequestMethod.OPTIONS })
@Tag(name = "Habitaciones Reactivas", description = "API reactiva para gestión de habitaciones")
public class HabitacionControllerReactivo {

        private static final Logger log = LoggerFactory.getLogger(HabitacionControllerReactivo.class);

        private final HabitacionServiceReactivo habitacionService;

        public HabitacionControllerReactivo(HabitacionServiceReactivo habitacionService) {
                this.habitacionService = habitacionService;
        }

        @GetMapping
        @Operation(summary = "Obtener todas las habitaciones", description = "Retorna todas las habitaciones disponibles de forma reactiva")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Habitaciones encontradas"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        public Flux<Habitacion> findAll() {
                log.info("Solicitud GET a /api/reactive/habitaciones");
                return habitacionService.findAll();
        }

        @GetMapping("/disponibles")
        @Operation(summary = "Obtener habitaciones disponibles", description = "Retorna solo las habitaciones con estado 'Disponible'")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Habitaciones disponibles encontradas"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        public Flux<Habitacion> findAvailable() {
                log.info("Solicitud GET a /api/reactive/habitaciones/disponibles");
                return habitacionService.findAvailable();
        }

        @GetMapping("/{id}")
        @Operation(summary = "Obtener habitación por ID", description = "Retorna una habitación específica por su ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Habitación encontrada"),
                        @ApiResponse(responseCode = "404", description = "Habitación no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        public Mono<ResponseEntity<Habitacion>> findById(
                        @Parameter(description = "ID de la habitación a buscar") @PathVariable Long id) {
                log.info("Solicitud GET a /api/reactive/habitaciones/{}", id);
                return habitacionService.findById(id)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @PostMapping
        @Operation(summary = "Crear habitación", description = "Crea una nueva habitación")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Habitación creada exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        public Mono<ResponseEntity<Habitacion>> create(@RequestBody Habitacion habitacion) {
                log.info("Solicitud POST a /api/reactive/habitaciones");
                return habitacionService.save(habitacion)
                                .map(savedHabitacion -> ResponseEntity.status(HttpStatus.CREATED)
                                                .body(savedHabitacion));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Actualizar habitación", description = "Actualiza una habitación existente")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Habitación actualizada exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Habitación no encontrada"),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        public Mono<ResponseEntity<Habitacion>> update(
                        @Parameter(description = "ID de la habitación a actualizar") @PathVariable Long id,
                        @RequestBody Habitacion habitacion) {
                log.info("Solicitud PUT a /api/reactive/habitaciones/{}", id);
                return habitacionService.findById(id)
                                .flatMap(existingHabitacion -> {
                                        existingHabitacion.setNumero(habitacion.getNumero());
                                        existingHabitacion.setTipo(habitacion.getTipo());
                                        existingHabitacion.setPrecio(habitacion.getPrecio());
                                        existingHabitacion.setEstado(habitacion.getEstado());
                                        return habitacionService.save(existingHabitacion);
                                })
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Eliminar habitación", description = "Elimina una habitación por su ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Habitación eliminada exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Habitación no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        public Mono<ResponseEntity<Void>> deleteById(
                        @Parameter(description = "ID de la habitación a eliminar") @PathVariable Long id) {
                log.info("Solicitud DELETE a /api/reactive/habitaciones/{}", id);
                return habitacionService.deleteById(id)
                                .thenReturn(ResponseEntity.noContent().<Void>build())
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        // ============================================================
        // ENDPOINTS DE DEMOSTRACIÓN - Comparar con y sin onErrorResume
        // ============================================================

        /**
         * DEMO: Buscar habitación CON recuperación (onErrorResume).
         * Si no existe, retorna habitación por defecto. EL FLUJO CONTINÚA.
         * 
         * Probar: GET /api/reactive/habitaciones/999
         * (ID que no existe - verás la respuesta de recuperación en lugar de error)
         */
        @GetMapping("/demo/con-recuperacion/{id}")
        @Operation(summary = "[DEMO] Buscar con recuperación", description = "Usa onErrorResume - si no existe retorna habitación default")
        public Mono<Habitacion> findByIdConRecuperacion(@PathVariable Long id) {
                log.info("=== DEMO: Buscando habitación {} CON recuperación ===", id);
                return habitacionService.findById(id); // Ya tiene onErrorResume
        }

        /**
         * DEMO: Buscar habitación SIN recuperación.
         * Si no existe, LANZA ERROR. El flujo SE DETIENE.
         * 
         * Probar: GET /api/reactive/habitaciones/demo/sin-recuperacion/999
         * (ID que no existe - verás error 500)
         */
        @GetMapping("/demo/sin-recuperacion/{id}")
        @Operation(summary = "[DEMO] Buscar sin recuperación", description = "Sin onErrorResume - si no existe lanza error 500")
        public Mono<Habitacion> findByIdSinRecuperacion(@PathVariable Long id) {
                log.info("=== DEMO: Buscando habitación {} SIN recuperación ===", id);
                return habitacionService.findByIdSinRecuperacion(id); // Sin onErrorResume - lanza error
        }

        /**
         * STREAMING: Envía habitaciones una por una con delay.
         * Demuestra el flujo de datos reactivo en tiempo real.
         * 
         * Probar en navegador: GET /api/reactive/habitaciones/stream
         */
        @GetMapping(value = "/stream", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
        @Operation(summary = "Stream de habitaciones", description = "Envía habitaciones una por una en tiempo real")
        public Flux<Habitacion> streamHabitaciones() {
                log.info("=== STREAMING: Iniciando flujo de habitaciones ===");
                return habitacionService.findAll()
                                .delayElements(java.time.Duration.ofMillis(800))
                                .doOnSubscribe(s -> log.info("onSubscribe: Cliente conectado al stream"))
                                .doOnNext(h -> log.info("onNext: Enviando habitación #{} al cliente", h.getNumero()))
                                .doOnComplete(() -> log.info("onComplete: Stream de habitaciones finalizado"))
                                .doOnCancel(() -> log.info("onCancel: Cliente desconectado del stream"));
        }
}
