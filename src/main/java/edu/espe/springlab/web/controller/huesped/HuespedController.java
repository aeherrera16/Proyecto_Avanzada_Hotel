package edu.espe.springlab.web.controller.huesped;

import edu.espe.springlab.dto.huesped.HuespedRequest;
import edu.espe.springlab.dto.huesped.HuespedResponse;
import edu.espe.springlab.service.huesped.HuespedService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/huespedes")
@CrossOrigin(origins = "*") // Permite solicitudes desde cualquier origen. Considera restringirlo en producción.
public class HuespedController {

    private final HuespedService huespedService;

    public HuespedController(HuespedService huespedService) {
        this.huespedService = huespedService;
    }

    @GetMapping
    public ResponseEntity<List<HuespedResponse>> getAllHuespedes() {
        List<HuespedResponse> huespedes = huespedService.findAll();
        return ResponseEntity.ok(huespedes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HuespedResponse> getHuespedById(@PathVariable Long id) {
        HuespedResponse huesped = huespedService.findById(id);
        return ResponseEntity.ok(huesped);
    }

    @PostMapping
    public ResponseEntity<HuespedResponse> createHuesped(@Valid @RequestBody HuespedRequest huespedRequest) {
        HuespedResponse newHuesped = huespedService.create(huespedRequest);
        return new ResponseEntity<>(newHuesped, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HuespedResponse> updateHuesped(@PathVariable Long id, @Valid @RequestBody HuespedRequest huespedRequest) {
        HuespedResponse updatedHuesped = huespedService.update(id, huespedRequest);
        return ResponseEntity.ok(updatedHuesped);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHuesped(@PathVariable Long id) {
        huespedService.delete(id);
        return ResponseEntity.noContent().build();
    }
}