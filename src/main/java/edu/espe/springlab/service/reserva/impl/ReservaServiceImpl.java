package edu.espe.springlab.service.reserva.impl;

import edu.espe.springlab.domain.Habitacion;
import edu.espe.springlab.domain.Huesped;
import edu.espe.springlab.domain.Reserva;
import edu.espe.springlab.dto.reserva.ReservaRequest;
import edu.espe.springlab.dto.reserva.ReservaResponse;
import edu.espe.springlab.repository.HabitacionRepository;
import edu.espe.springlab.repository.HuespedRepository;
import edu.espe.springlab.repository.ReservaRepository;
import edu.espe.springlab.service.reserva.ReservaService;
import edu.espe.springlab.web.advice.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final HuespedRepository huespedRepository;
    private final HabitacionRepository habitacionRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository, HuespedRepository huespedRepository, HabitacionRepository habitacionRepository) {
        this.reservaRepository = reservaRepository;
        this.huespedRepository = huespedRepository;
        this.habitacionRepository = habitacionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> findAll() {
        return reservaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponse findById(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada con ID: " + id));
        return mapToResponse(reserva);
    }

    @Override
    @Transactional
    public ReservaResponse create(ReservaRequest reservaRequest) {
        Huesped huesped = huespedRepository.findById(reservaRequest.getHuespedId())
                .orElseThrow(() -> new NotFoundException("Huésped no encontrado con ID: " + reservaRequest.getHuespedId()));
        Habitacion habitacion = habitacionRepository.findById(reservaRequest.getHabitacionId())
                .orElseThrow(() -> new NotFoundException("Habitación no encontrada con ID: " + reservaRequest.getHabitacionId()));

        Reserva reserva = new Reserva();
        reserva.setHuesped(huesped);
        reserva.setHabitacion(habitacion);
        mapRequestToEntity(reservaRequest, reserva);
        Reserva savedReserva = reservaRepository.save(reserva);
        return mapToResponse(savedReserva);
    }

    @Override
    @Transactional
    public ReservaResponse update(Long id, ReservaRequest reservaRequest) {
        Reserva existingReserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada con ID: " + id));

        Huesped huesped = huespedRepository.findById(reservaRequest.getHuespedId())
                .orElseThrow(() -> new NotFoundException("Huésped no encontrado con ID: " + reservaRequest.getHuespedId()));
        Habitacion habitacion = habitacionRepository.findById(reservaRequest.getHabitacionId())
                .orElseThrow(() -> new NotFoundException("Habitación no encontrada con ID: " + reservaRequest.getHabitacionId()));

        existingReserva.setHuesped(huesped);
        existingReserva.setHabitacion(habitacion);
        mapRequestToEntity(reservaRequest, existingReserva);
        Reserva updatedReserva = reservaRepository.save(existingReserva);
        return mapToResponse(updatedReserva);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new NotFoundException("Reserva no encontrada con ID: " + id);
        }
        reservaRepository.deleteById(id);
    }

    private ReservaResponse mapToResponse(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getHuesped().getId(),
                reserva.getHuesped().getNombre() + " " + reserva.getHuesped().getApellido(),
                reserva.getHabitacion().getId(),
                reserva.getHabitacion().getNumero(),
                reserva.getFechaEntrada(),
                reserva.getFechaSalida(),
                reserva.getPrecioTotal(),
                reserva.getEstado(),
                reserva.getFechaCreacion(),
                reserva.getFechaActualizacion()
        );
    }

    private void mapRequestToEntity(ReservaRequest request, Reserva entity) {
        entity.setFechaEntrada(request.getFechaEntrada());
        entity.setFechaSalida(request.getFechaSalida());
        entity.setPrecioTotal(request.getPrecioTotal());
        entity.setEstado(request.getEstado());
    }
}