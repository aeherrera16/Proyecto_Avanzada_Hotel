package edu.espe.springlab.repository;

import edu.espe.springlab.domain.Habitacion;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface HabitacionRepository extends ReactiveCrudRepository<Habitacion, Long> {
    Flux<Habitacion> findByEstado(String estado);
    Flux<Habitacion> findByTipo(String tipo);
}