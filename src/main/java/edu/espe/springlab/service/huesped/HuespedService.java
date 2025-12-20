package edu.espe.springlab.service.huesped;

import edu.espe.springlab.dto.huesped.HuespedRequest;
import edu.espe.springlab.dto.huesped.HuespedResponse;

import java.util.List;

public interface HuespedService {
    List<HuespedResponse> findAll();
    HuespedResponse findById(Long id);
    HuespedResponse create(HuespedRequest huespedRequest);
    HuespedResponse update(Long id, HuespedRequest huespedRequest);
    void delete(Long id);
}