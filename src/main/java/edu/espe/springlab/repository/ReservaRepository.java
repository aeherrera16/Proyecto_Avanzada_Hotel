package edu.espe.springlab.repository;

import edu.espe.springlab.domain.Reserva;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ReservaRepository extends ReactiveCrudRepository<Reserva, Long> {

    Flux<Reserva> findByHuespedId(Long huespedId);
    Flux<Reserva> findByHabitacionId(Long habitacionId);
    Flux<Reserva> findByEstado(String estado);
    Mono<Boolean> existsByHuespedId(Long huespedId);
    Mono<Void> deleteByHuespedId(Long huespedId);
}