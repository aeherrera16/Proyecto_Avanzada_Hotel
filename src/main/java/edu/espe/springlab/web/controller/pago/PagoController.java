package edu.espe.springlab.web.controller.pago;

import edu.espe.springlab.dto.pago.PagoRequest;
import edu.espe.springlab.dto.pago.PagoResponse;
import edu.espe.springlab.service.pago.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*") // Permite solicitudes desde cualquier origen. Considera restringirlo en producción.
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public ResponseEntity<List<PagoResponse>> getAllPagos() {
        List<PagoResponse> pagos = pagoService.findAll();
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> getPagoById(@PathVariable Long id) {
        PagoResponse pago = pagoService.findById(id);
        return ResponseEntity.ok(pago);
    }

    @PostMapping
    public ResponseEntity<PagoResponse> createPago(@Valid @RequestBody PagoRequest pagoRequest) {
        PagoResponse newPago = pagoService.create(pagoRequest);
        return new ResponseEntity<>(newPago, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoResponse> updatePago(@PathVariable Long id, @Valid @RequestBody PagoRequest pagoRequest) {
        PagoResponse updatedPago = pagoService.update(id, pagoRequest);
        return ResponseEntity.ok(updatedPago);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePago(@PathVariable Long id) {
        pagoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}