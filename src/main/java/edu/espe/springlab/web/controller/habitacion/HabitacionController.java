package edu.espe.springlab.web.controller.habitacion;

import edu.espe.springlab.dto.habitacion.HabitacionRequest;
import edu.espe.springlab.dto.habitacion.HabitacionResponse;
import edu.espe.springlab.service.habitacion.HabitacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
@CrossOrigin(origins = "*") // Permite solicitudes desde cualquier origen. Considera restringirlo en producción.
public class HabitacionController {

    private final HabitacionService habitacionService;

    public HabitacionController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    @GetMapping
    public ResponseEntity<List<HabitacionResponse>> getAllHabitaciones() {
        List<HabitacionResponse> habitaciones = habitacionService.findAll();
        return ResponseEntity.ok(habitaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitacionResponse> getHabitacionById(@PathVariable Long id) {
        HabitacionResponse habitacion = habitacionService.findById(id);
        return ResponseEntity.ok(habitacion);
    }

    @PostMapping
    public ResponseEntity<HabitacionResponse> createHabitacion(@Valid @RequestBody HabitacionRequest habitacionRequest) {
        HabitacionResponse newHabitacion = habitacionService.create(habitacionRequest);
        return new ResponseEntity<>(newHabitacion, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitacionResponse> updateHabitacion(@PathVariable Long id, @Valid @RequestBody HabitacionRequest habitacionRequest) {
        HabitacionResponse updatedHabitacion = habitacionService.update(id, habitacionRequest);
        return ResponseEntity.ok(updatedHabitacion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabitacion(@PathVariable Long id) {
        habitacionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}