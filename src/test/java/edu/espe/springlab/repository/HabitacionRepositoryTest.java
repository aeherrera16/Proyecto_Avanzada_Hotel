package edu.espe.springlab.repository;

import edu.espe.springlab.domain.Habitacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class HabitacionRepositoryTest {

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Test
    void testCreate() {
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero("101");
        habitacion.setTipo("Simple");
        habitacion.setPrecio(50.0);
        habitacion.setEstado("Disponible");

        Habitacion saved = habitacionRepository.save(habitacion);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNumero()).isEqualTo("101");
    }

    @Test
    void testFindAll() {
        assertThat(habitacionRepository.findAll()).isNotNull();
    }

    @Test
    void testFindById() {
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero("102");
        habitacion.setTipo("Doble");
        habitacion.setPrecio(80.0);
        habitacion.setEstado("Disponible");

        Habitacion saved = habitacionRepository.save(habitacion);
        Habitacion found = habitacionRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getNumero()).isEqualTo("102");
    }

    @Test
    void testUpdate() {
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero("103");
        habitacion.setTipo("Suite");
        habitacion.setPrecio(150.0);
        habitacion.setEstado("Disponible");

        Habitacion saved = habitacionRepository.save(habitacion);
        saved.setEstado("Ocupada");

        Habitacion updated = habitacionRepository.save(saved);

        assertThat(updated.getEstado()).isEqualTo("Ocupada");
    }

    @Test
    void testDelete() {
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero("104");
        habitacion.setTipo("Simple");
        habitacion.setPrecio(50.0);
        habitacion.setEstado("Disponible");

        Habitacion saved = habitacionRepository.save(habitacion);
        Long id = saved.getId();

        habitacionRepository.deleteById(id);

        assertThat(habitacionRepository.findById(id)).isEmpty();
    }
}
