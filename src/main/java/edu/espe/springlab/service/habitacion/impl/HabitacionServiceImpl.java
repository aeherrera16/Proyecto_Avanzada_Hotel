package edu.espe.springlab.service.habitacion.impl;

import edu.espe.springlab.domain.Habitacion;
import edu.espe.springlab.dto.habitacion.HabitacionRequest;
import edu.espe.springlab.dto.habitacion.HabitacionResponse;
import edu.espe.springlab.repository.HabitacionRepository;
import edu.espe.springlab.service.habitacion.HabitacionService;
import edu.espe.springlab.web.advice.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionRepository habitacionRepository;

    public HabitacionServiceImpl(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionResponse> findAll() {
        return habitacionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HabitacionResponse findById(Long id) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Habitación no encontrada con ID: " + id));
        return mapToResponse(habitacion);
    }

    @Override
    @Transactional
    public HabitacionResponse create(HabitacionRequest habitacionRequest) {
        Habitacion habitacion = new Habitacion();
        mapRequestToEntity(habitacionRequest, habitacion);
        Habitacion savedHabitacion = habitacionRepository.save(habitacion);
        return mapToResponse(savedHabitacion);
    }

    @Override
    @Transactional
    public HabitacionResponse update(Long id, HabitacionRequest habitacionRequest) {
        Habitacion existingHabitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Habitación no encontrada con ID: " + id));
        mapRequestToEntity(habitacionRequest, existingHabitacion);
        Habitacion updatedHabitacion = habitacionRepository.save(existingHabitacion);
        return mapToResponse(updatedHabitacion);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!habitacionRepository.existsById(id)) {
            throw new NotFoundException("Habitación no encontrada con ID: " + id);
        }
        habitacionRepository.deleteById(id);
    }

    private HabitacionResponse mapToResponse(Habitacion habitacion) {
        return new HabitacionResponse(
                habitacion.getId(),
                habitacion.getNumero(),
                habitacion.getTipo(),
                habitacion.getPrecio(),
                habitacion.getEstado()
        );
    }

    private void mapRequestToEntity(HabitacionRequest request, Habitacion entity) {
        entity.setNumero(request.getNumero());
        entity.setTipo(request.getTipo());
        entity.setPrecio(request.getPrecio());
        entity.setEstado(request.getEstado());
    }
}