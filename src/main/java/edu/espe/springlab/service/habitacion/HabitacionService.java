package edu.espe.springlab.service.habitacion;

import edu.espe.springlab.dto.habitacion.HabitacionRequest;
import edu.espe.springlab.dto.habitacion.HabitacionResponse;

import java.util.List;

public interface HabitacionService {
    List<HabitacionResponse> findAll();
    HabitacionResponse findById(Long id);
    HabitacionResponse create(HabitacionRequest habitacionRequest);
    HabitacionResponse update(Long id, HabitacionRequest habitacionRequest);
    void delete(Long id);
}