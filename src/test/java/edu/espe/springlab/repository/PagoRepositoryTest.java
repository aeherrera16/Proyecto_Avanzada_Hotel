package edu.espe.springlab.repository;

import edu.espe.springlab.domain.Pago;
import edu.espe.springlab.domain.Reserva;
import edu.espe.springlab.domain.Habitacion;
import edu.espe.springlab.domain.Huesped;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PagoRepositoryTest {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private HuespedRepository huespedRepository;

    private Reserva createTestReserva() {
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero("101");
        habitacion.setTipo("Simple");
        habitacion.setPrecio(50.0);
        habitacion.setEstado("Disponible");
        habitacion = habitacionRepository.save(habitacion);

        Huesped huesped = new Huesped();
        huesped.setNombre("Test");
        huesped.setApellido("User");
        huesped.setCedula("9999999999");
        huesped.setEmail("test@example.com");
        huesped.setTelefono("0999999999");
        huesped.setNacionalidad("Ecuatoriana");
        huesped = huespedRepository.save(huesped);

        Reserva reserva = new Reserva();
        reserva.setHabitacion(habitacion);
        reserva.setHuesped(huesped);
        reserva.setFechaEntrada(LocalDate.now());
        reserva.setFechaSalida(LocalDate.now().plusDays(2));
        reserva.setPrecioTotal(100.0);
        reserva.setEstado("Confirmada");

        return reservaRepository.save(reserva);
    }

    @Test
    void testCreate() {
        Reserva reserva = createTestReserva();

        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMonto(100.0);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMetodoPago("Tarjeta de Crédito");
        pago.setEstado("Completado");

        Pago saved = pagoRepository.save(pago);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMonto()).isEqualTo(100.0);
    }

    @Test
    void testFindAll() {
        assertThat(pagoRepository.findAll()).isNotNull();
    }

    @Test
    void testFindById() {
        Reserva reserva = createTestReserva();

        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMonto(150.0);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMetodoPago("Efectivo");
        pago.setEstado("Completado");

        Pago saved = pagoRepository.save(pago);
        Pago found = pagoRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getMonto()).isEqualTo(150.0);
    }

    @Test
    void testUpdate() {
        Reserva reserva = createTestReserva();

        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMonto(200.0);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMetodoPago("Tarjeta de Débito");
        pago.setEstado("Pendiente");

        Pago saved = pagoRepository.save(pago);
        saved.setEstado("Completado");

        Pago updated = pagoRepository.save(saved);

        assertThat(updated.getEstado()).isEqualTo("Completado");
    }

    @Test
    void testDelete() {
        Reserva reserva = createTestReserva();

        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMonto(250.0);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMetodoPago("Transferencia");
        pago.setEstado("Completado");

        Pago saved = pagoRepository.save(pago);
        Long id = saved.getId();

        pagoRepository.deleteById(id);

        assertThat(pagoRepository.findById(id)).isEmpty();
    }
}
