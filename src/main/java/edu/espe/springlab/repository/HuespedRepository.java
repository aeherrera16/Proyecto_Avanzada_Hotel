package edu.espe.springlab.repository;

import edu.espe.springlab.domain.Huesped;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HuespedRepository extends R2dbcRepository<Huesped, Long> {
}