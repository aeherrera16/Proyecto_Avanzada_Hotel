package edu.espe.springlab.repository;

import edu.espe.springlab.domain.Reserva;
import edu.espe.springlab.domain.Habitacion;
import edu.espe.springlab.domain.Huesped;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ReservaRepositoryTest {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private HuespedRepository huespedRepository;

    private Habitacion createTestHabitacion(String numero) {
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero(numero);
        habitacion.setTipo("Simple");
        habitacion.setPrecio(50.0);
        habitacion.setEstado("Disponible");
        return habitacionRepository.save(habitacion);
    }

    private Huesped createTestHuesped(String cedula) {
        Huesped huesped = new Huesped();
        huesped.setNombre("Test");
        huesped.setApellido("User");
        huesped.setCedula(cedula);
        huesped.setEmail("test" + cedula + "@example.com");
        huesped.setTelefono("0999999999");
        huesped.setNacionalidad("Ecuatoriana");
        return huespedRepository.save(huesped);
    }

    @Test
    void testCreate() {
        Habitacion habitacion = createTestHabitacion("201");
        Huesped huesped = createTestHuesped("1111111111");

        Reserva reserva = new Reserva();
        reserva.setHabitacion(habitacion);
        reserva.setHuesped(huesped);
        reserva.setFechaEntrada(LocalDate.now());
        reserva.setFechaSalida(LocalDate.now().plusDays(2));
        reserva.setPrecioTotal(100.0);
        reserva.setEstado("Confirmada");

        Reserva saved = reservaRepository.save(reserva);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEstado()).isEqualTo("Confirmada");
    }

    @Test
    void testFindAll() {
        assertThat(reservaRepository.findAll()).isNotNull();
    }

    @Test
    void testFindById() {
        Habitacion habitacion = createTestHabitacion("202");
        Huesped huesped = createTestHuesped("2222222222");

        Reserva reserva = new Reserva();
        reserva.setHabitacion(habitacion);
        reserva.setHuesped(huesped);
        reserva.setFechaEntrada(LocalDate.now());
        reserva.setFechaSalida(LocalDate.now().plusDays(3));
        reserva.setPrecioTotal(150.0);
        reserva.setEstado("Confirmada");

        Reserva saved = reservaRepository.save(reserva);
        Reserva found = reservaRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getPrecioTotal()).isEqualTo(150.0);
    }

    @Test
    void testUpdate() {
        Habitacion habitacion = createTestHabitacion("203");
        Huesped huesped = createTestHuesped("3333333333");

        Reserva reserva = new Reserva();
        reserva.setHabitacion(habitacion);
        reserva.setHuesped(huesped);
        reserva.setFechaEntrada(LocalDate.now());
        reserva.setFechaSalida(LocalDate.now().plusDays(4));
        reserva.setPrecioTotal(200.0);
        reserva.setEstado("Confirmada");

        Reserva saved = reservaRepository.save(reserva);
        saved.setEstado("Cancelada");

        Reserva updated = reservaRepository.save(saved);

        assertThat(updated.getEstado()).isEqualTo("Cancelada");
    }

    @Test
    void testDelete() {
        Habitacion habitacion = createTestHabitacion("204");
        Huesped huesped = createTestHuesped("4444444444");

        Reserva reserva = new Reserva();
        reserva.setHabitacion(habitacion);
        reserva.setHuesped(huesped);
        reserva.setFechaEntrada(LocalDate.now());
        reserva.setFechaSalida(LocalDate.now().plusDays(5));
        reserva.setPrecioTotal(250.0);
        reserva.setEstado("Confirmada");

        Reserva saved = reservaRepository.save(reserva);
        Long id = saved.getId();

        reservaRepository.deleteById(id);

        assertThat(reservaRepository.findById(id)).isEmpty();
    }
}
