package edu.espe.springlab.repository;

import edu.espe.springlab.domain.Pago;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends R2dbcRepository<Pago, Long> {
}