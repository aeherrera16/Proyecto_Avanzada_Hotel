package edu.espe.springlab.repository;

import edu.espe.springlab.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsByHabitacionIdAndFechaEntradaLessThanEqualAndFechaSalidaGreaterThanEqual(
            Long habitacionId,
            LocalDate fechaSalida,
            LocalDate fechaEntrada
    );
}