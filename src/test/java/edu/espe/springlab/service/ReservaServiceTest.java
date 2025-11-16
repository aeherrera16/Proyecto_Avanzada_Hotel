package edu.espe.springlab.service;
import edu.espe.springlab.service.reserva.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
public class ReservaServiceTest {
    @Autowired
    private ReservaService reservaService;
    @Test
    void testServiceLoads() {
        assertThat(reservaService).isNotNull();
    }
    @Test
    void testFindAll() {
        assertThat(reservaService.findAll()).isNotNull();
    }
}
