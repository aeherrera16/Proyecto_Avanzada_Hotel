package edu.espe.springlab.service.pago.impl;

import edu.espe.springlab.domain.Pago;
import edu.espe.springlab.domain.Reserva;
import edu.espe.springlab.dto.pago.PagoRequest;
import edu.espe.springlab.dto.pago.PagoResponse;
import edu.espe.springlab.repository.PagoRepository;
import edu.espe.springlab.repository.ReservaRepository;
import edu.espe.springlab.service.pago.PagoService;
import edu.espe.springlab.web.advice.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;

    public PagoServiceImpl(PagoRepository pagoRepository, ReservaRepository reservaRepository) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> findAll() {
        return pagoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponse findById(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + id));
        return mapToResponse(pago);
    }

    @Override
    @Transactional
    public PagoResponse create(PagoRequest pagoRequest) {
        Reserva reserva = reservaRepository.findById(pagoRequest.getReservaId())
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada con ID: " + pagoRequest.getReservaId()));

        Pago pago = new Pago();
        pago.setReserva(reserva);
        mapRequestToEntity(pagoRequest, pago);
        Pago savedPago = pagoRepository.save(pago);
        return mapToResponse(savedPago);
    }

    @Override
    @Transactional
    public PagoResponse update(Long id, PagoRequest pagoRequest) {
        Pago existingPago = pagoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + id));

        Reserva reserva = reservaRepository.findById(pagoRequest.getReservaId())
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada con ID: " + pagoRequest.getReservaId()));

        existingPago.setReserva(reserva);
        mapRequestToEntity(pagoRequest, existingPago);
        Pago updatedPago = pagoRepository.save(existingPago);
        return mapToResponse(updatedPago);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!pagoRepository.existsById(id)) {
            throw new NotFoundException("Pago no encontrado con ID: " + id);
        }
        pagoRepository.deleteById(id);
    }

    private PagoResponse mapToResponse(Pago pago) {
        return new PagoResponse(
                pago.getId(),
                pago.getReserva().getId(),
                pago.getMonto(),
                pago.getFechaPago(),
                pago.getMetodoPago(),
                pago.getEstado(),
                pago.getFechaCreacion(),
                pago.getFechaActualizacion()
        );
    }

    private void mapRequestToEntity(PagoRequest request, Pago entity) {
        entity.setMonto(request.getMonto());
        entity.setFechaPago(request.getFechaPago());
        entity.setMetodoPago(request.getMetodoPago());
        entity.setEstado(request.getEstado());
    }
}