package edu.espe.springlab.service.reserva;

import edu.espe.springlab.dto.reserva.ReservaRequest;
import edu.espe.springlab.dto.reserva.ReservaResponse;

import java.util.List;

public interface ReservaService {
    List<ReservaResponse> findAll();
    ReservaResponse findById(Long id);
    ReservaResponse create(ReservaRequest reservaRequest);
    ReservaResponse update(Long id, ReservaRequest reservaRequest);
    void delete(Long id);
}