package edu.espe.springlab.controller;

import edu.espe.springlab.domain.Pago;
import edu.espe.springlab.service.reactive.PagoServiceReactivo;
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
@RequestMapping("/api/reactive/pagos")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@Tag(name = "Pagos Reactivos", description = "API reactiva para gestión de pagos")
public class PagoControllerReactivo {
    
    private static final Logger log = LoggerFactory.getLogger(PagoControllerReactivo.class);
    
    private final PagoServiceReactivo pagoService;
    
    public PagoControllerReactivo(PagoServiceReactivo pagoService) {
        this.pagoService = pagoService;
    }
    
    @GetMapping
    @Operation(summary = "Obtener todos los pagos", description = "Retorna todos los pagos de forma reactiva")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pagos encontrados"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Flux<Pago> findAll() {
        log.info("Solicitud GET a /api/reactive/pagos");
        return pagoService.findAll();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener pago por ID", description = "Retorna un pago específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago encontrado"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Pago>> findById(
            @Parameter(description = "ID del pago a buscar") 
            @PathVariable Long id) {
        log.info("Solicitud GET a /api/reactive/pagos/{}", id);
        return pagoService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Crear pago", description = "Crea un nuevo pago")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pago creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Pago>> create(@RequestBody Pago pago) {
        log.info("Solicitud POST a /api/reactive/pagos");
        return pagoService.save(pago)
                .map(savedPago -> ResponseEntity.status(HttpStatus.CREATED).body(savedPago));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pago", description = "Actualiza un pago existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Pago>> update(
            @Parameter(description = "ID del pago a actualizar") 
            @PathVariable Long id, 
            @RequestBody Pago pago) {
        log.info("Solicitud PUT a /api/reactive/pagos/{}", id);
        return pagoService.update(id, pago)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pago", description = "Elimina un pago por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pago eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Void>> deleteById(
            @Parameter(description = "ID del pago a eliminar") 
            @PathVariable Long id) {
        log.info("Solicitud DELETE a /api/reactive/pagos/{}", id);
        return pagoService.deleteById(id)
                .thenReturn(ResponseEntity.noContent().<Void>build())
                .onErrorReturn(ResponseEntity.notFound().build());
    }
}
