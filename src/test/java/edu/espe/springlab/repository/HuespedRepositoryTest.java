package edu.espe.springlab.repository;

import edu.espe.springlab.domain.Huesped;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class HuespedRepositoryTest {

    @Autowired
    private HuespedRepository huespedRepository;

    @Test
    void testCreate() {
        Huesped huesped = new Huesped();
        huesped.setNombre("Juan");
        huesped.setApellido("Pérez");
        huesped.setCedula("1234567890");
        huesped.setEmail("juan@example.com");
        huesped.setTelefono("0999999999");
        huesped.setNacionalidad("Ecuatoriana");

        Huesped saved = huespedRepository.save(huesped);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNombre()).isEqualTo("Juan");
    }

    @Test
    void testFindAll() {
        assertThat(huespedRepository.findAll()).isNotNull();
    }

    @Test
    void testFindById() {
        Huesped huesped = new Huesped();
        huesped.setNombre("María");
        huesped.setApellido("González");
        huesped.setCedula("0987654321");
        huesped.setEmail("maria@example.com");
        huesped.setTelefono("0988888888");
        huesped.setNacionalidad("Ecuatoriana");

        Huesped saved = huespedRepository.save(huesped);
        Huesped found = huespedRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getNombre()).isEqualTo("María");
    }

    @Test
    void testUpdate() {
        Huesped huesped = new Huesped();
        huesped.setNombre("Carlos");
        huesped.setApellido("Rodríguez");
        huesped.setCedula("1122334455");
        huesped.setEmail("carlos@example.com");
        huesped.setTelefono("0977777777");
        huesped.setNacionalidad("Ecuatoriana");

        Huesped saved = huespedRepository.save(huesped);
        saved.setTelefono("0966666666");

        Huesped updated = huespedRepository.save(saved);

        assertThat(updated.getTelefono()).isEqualTo("0966666666");
    }

    @Test
    void testDelete() {
        Huesped huesped = new Huesped();
        huesped.setNombre("Ana");
        huesped.setApellido("Martínez");
        huesped.setCedula("5544332211");
        huesped.setEmail("ana@example.com");
        huesped.setTelefono("0955555555");
        huesped.setNacionalidad("Ecuatoriana");

        Huesped saved = huespedRepository.save(huesped);
        Long id = saved.getId();

        huespedRepository.deleteById(id);

        assertThat(huespedRepository.findById(id)).isEmpty();
    }
}
