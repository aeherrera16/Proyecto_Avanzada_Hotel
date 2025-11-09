package edu.espe.springlab.service.pago;

import edu.espe.springlab.dto.pago.PagoRequest;
import edu.espe.springlab.dto.pago.PagoResponse;

import java.util.List;

public interface PagoService {
    List<PagoResponse> findAll();
    PagoResponse findById(Long id);
    PagoResponse create(PagoRequest pagoRequest);
    PagoResponse update(Long id, PagoRequest pagoRequest);
    void delete(Long id);
}