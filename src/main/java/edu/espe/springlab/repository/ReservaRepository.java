package edu.espe.springlab.repository;

import edu.espe.springlab.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsByHabitacionIdAndFechaEntradaLessThanEqualAndFechaSalidaGreaterThanEqual(
            Long habitacionId,
            LocalDate fechaSalida,
            LocalDate fechaEntrada
    );

    @Modifying
    @Query("DELETE FROM Reserva r WHERE r.huesped.id = :huespedId")
    void deleteByHuespedId(@Param("huespedId") Long huespedId);

    boolean existsByHuespedId(Long huespedId);
}