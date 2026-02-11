package edu.espe.springlab.controller;

import edu.espe.springlab.domain.Huesped;
import edu.espe.springlab.service.reactive.HuespedServiceReactivo;
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
@RequestMapping("/api/reactive/huespedes")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.OPTIONS })
@Tag(name = "Huéspedes Reactivos", description = "API reactiva para gestión de huéspedes")
public class HuespedControllerReactivo {

    private static final Logger log = LoggerFactory.getLogger(HuespedControllerReactivo.class);

    private final HuespedServiceReactivo huespedService;

    public HuespedControllerReactivo(HuespedServiceReactivo huespedService) {
        this.huespedService = huespedService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los huéspedes", description = "Retorna todos los huéspedes de forma reactiva")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Huéspedes encontrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Flux<Huesped> findAll() {
        log.info("Solicitud GET a /api/reactive/huespedes");
        return huespedService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener huésped por ID", description = "Retorna un huésped específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Huésped encontrado"),
            @ApiResponse(responseCode = "404", description = "Huésped no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Huesped>> findById(
            @Parameter(description = "ID del huésped a buscar") @PathVariable Long id) {
        log.info("Solicitud GET a /api/reactive/huespedes/{}", id);
        return huespedService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear huésped", description = "Crea un nuevo huésped")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Huésped creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Huesped>> create(@RequestBody Huesped huesped) {
        log.info("Solicitud POST a /api/reactive/huespedes");
        return huespedService.save(huesped)
                .map(savedHuesped -> ResponseEntity.status(HttpStatus.CREATED).body(savedHuesped));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar huésped", description = "Actualiza un huésped existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Huésped actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Huésped no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Huesped>> update(
            @Parameter(description = "ID del huésped a actualizar") @PathVariable Long id,
            @RequestBody Huesped huesped) {
        log.info("Solicitud PUT a /api/reactive/huespedes/{}", id);
        return huespedService.update(id, huesped)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar huésped", description = "Elimina un huésped por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Huésped eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Huésped no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Void>> deleteById(
            @Parameter(description = "ID del huésped a eliminar") @PathVariable Long id) {
        log.info("Solicitud DELETE a /api/reactive/huespedes/{}", id);
        return huespedService.deleteById(id)
                .thenReturn(ResponseEntity.noContent().<Void>build())
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    // ============================================================
    // ENDPOINTS DE DEMOSTRACIÓN - Con y sin onErrorResume
    // ============================================================

    @GetMapping("/demo/con-recuperacion/{id}")
    @Operation(summary = "[DEMO] Buscar con recuperación", description = "Usa onErrorResume - si no existe retorna huésped default")
    public Mono<Huesped> findByIdConRecuperacion(@PathVariable Long id) {
        log.info("=== DEMO: Buscando huésped {} CON recuperación ===", id);
        return huespedService.findById(id);
    }

    @GetMapping("/demo/sin-recuperacion/{id}")
    @Operation(summary = "[DEMO] Buscar sin recuperación", description = "Sin onErrorResume - si no existe lanza error 500")
    public Mono<Huesped> findByIdSinRecuperacion(@PathVariable Long id) {
        log.info("=== DEMO: Buscando huésped {} SIN recuperación ===", id);
        return huespedService.findByIdSinRecuperacion(id);
    }
}
