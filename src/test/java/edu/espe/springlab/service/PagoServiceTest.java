package edu.espe.springlab.service;
import edu.espe.springlab.service.pago.PagoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
public class PagoServiceTest {
    @Autowired
    private PagoService pagoService;
    @Test
    void testServiceLoads() {
        assertThat(pagoService).isNotNull();
    }
    @Test
    void testFindAll() {
        assertThat(pagoService.findAll()).isNotNull();
    }
}
