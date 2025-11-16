package edu.espe.springlab.service;
import edu.espe.springlab.service.huesped.HuespedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
public class HuespedServiceTest {
    @Autowired
    private HuespedService huespedService;
    @Test
    void testServiceLoads() {
        assertThat(huespedService).isNotNull();
    }
    @Test
    void testFindAll() {
        assertThat(huespedService.findAll()).isNotNull();
    }
}
