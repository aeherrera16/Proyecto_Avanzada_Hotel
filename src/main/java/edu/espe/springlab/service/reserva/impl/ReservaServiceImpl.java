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
        if (reservaRequest.getFechaSalida().isBefore(reservaRequest.getFechaEntrada())) {
            throw new IllegalArgumentException("La fecha de salida no puede ser antes que la fecha de entrada");
        }
        boolean ocupada = reservaRepository
                .existsByHabitacionIdAndFechaEntradaLessThanEqualAndFechaSalidaGreaterThanEqual(
                        reservaRequest.getHabitacionId(),
                        reservaRequest.getFechaSalida(),
                        reservaRequest.getFechaEntrada()
                );

        if (ocupada) {
            throw new IllegalArgumentException("La habitación está ocupada en esas fechas");
        }

        Reserva reserva = new Reserva();
        setRelations(reserva, reservaRequest);
        mapRequestToEntity(reservaRequest, reserva);
        return mapToResponse(reservaRepository.save(reserva));

    }

    @Override
    @Transactional
    public ReservaResponse update(Long id, ReservaRequest reservaRequest) {
        Reserva existingReserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada con ID: " + id));

        setRelations(existingReserva, reservaRequest);
        mapRequestToEntity(reservaRequest, existingReserva);
        return mapToResponse(reservaRepository.save(existingReserva));

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
    private void setRelations(Reserva reserva, ReservaRequest request) {
        Huesped huesped = huespedRepository.findById(request.getHuespedId())
                .orElseThrow(() -> new NotFoundException("Huésped no encontrado con ID: " + request.getHuespedId()));

        Habitacion habitacion = habitacionRepository.findById(request.getHabitacionId())
                .orElseThrow(() -> new NotFoundException("Habitación no encontrada con ID: " + request.getHabitacionId()));

        reserva.setHuesped(huesped);
        reserva.setHabitacion(habitacion);
    }

}