package edu.espe.springlab.service.huesped.impl;

import edu.espe.springlab.domain.Huesped;
import edu.espe.springlab.dto.huesped.HuespedRequest;
import edu.espe.springlab.dto.huesped.HuespedResponse;
import edu.espe.springlab.repository.HuespedRepository;
import edu.espe.springlab.service.huesped.HuespedService;
import edu.espe.springlab.web.advice.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HuespedServiceImpl implements HuespedService {

    private final HuespedRepository huespedRepository;

    public HuespedServiceImpl(HuespedRepository huespedRepository) {
        this.huespedRepository = huespedRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HuespedResponse> findAll() {
        return huespedRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HuespedResponse findById(Long id) {
        Huesped huesped = huespedRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Huésped no encontrado con ID: " + id));
        return mapToResponse(huesped);
    }

    @Override
    @Transactional
    public HuespedResponse create(HuespedRequest huespedRequest) {
        Huesped huesped = new Huesped();
        mapRequestToEntity(huespedRequest, huesped);
        Huesped savedHuesped = huespedRepository.save(huesped);
        return mapToResponse(savedHuesped);
    }

    @Override
    @Transactional
    public HuespedResponse update(Long id, HuespedRequest huespedRequest) {
        Huesped existingHuesped = huespedRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Huésped no encontrado con ID: " + id));
        mapRequestToEntity(huespedRequest, existingHuesped);
        Huesped updatedHuesped = huespedRepository.save(existingHuesped);
        return mapToResponse(updatedHuesped);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!huespedRepository.existsById(id)) {
            throw new NotFoundException("Huésped no encontrado con ID: " + id);
        }
        huespedRepository.deleteById(id);
    }

    private HuespedResponse mapToResponse(Huesped huesped) {
        return new HuespedResponse(
                huesped.getId(),
                huesped.getNombre(),
                huesped.getApellido(),
                huesped.getCedula(), // CAMBIO: Usar getCedula()
                huesped.getEmail(),
                huesped.getTelefono()
        );
    }

    private void mapRequestToEntity(HuespedRequest request, Huesped entity) {
        entity.setNombre(request.getNombre());
        entity.setApellido(request.getApellido());
        entity.setCedula(request.getCedula()); // CAMBIO: Usar request.getCedula()
        entity.setEmail(request.getEmail());
        entity.setTelefono(request.getTelefono());
    }
}