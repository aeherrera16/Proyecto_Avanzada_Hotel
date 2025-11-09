package edu.espe.springlab.web.controller.reserva;

import edu.espe.springlab.dto.reserva.ReservaRequest;
import edu.espe.springlab.dto.reserva.ReservaResponse;
import edu.espe.springlab.service.reserva.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*") // Permite solicitudes desde cualquier origen. Considera restringirlo en producción.
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> getAllReservas() {
        List<ReservaResponse> reservas = reservaService.findAll();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> getReservaById(@PathVariable Long id) {
        ReservaResponse reserva = reservaService.findById(id);
        return ResponseEntity.ok(reserva);
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> createReserva(@Valid @RequestBody ReservaRequest reservaRequest) {
        ReservaResponse newReserva = reservaService.create(reservaRequest);
        return new ResponseEntity<>(newReserva, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponse> updateReserva(@PathVariable Long id, @Valid @RequestBody ReservaRequest reservaRequest) {
        ReservaResponse updatedReserva = reservaService.update(id, reservaRequest);
        return ResponseEntity.ok(updatedReserva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long id) {
        reservaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}