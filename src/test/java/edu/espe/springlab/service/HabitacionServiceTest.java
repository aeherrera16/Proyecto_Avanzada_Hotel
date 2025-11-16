package edu.espe.springlab.service;
import edu.espe.springlab.service.habitacion.HabitacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
public class HabitacionServiceTest {
    @Autowired
    private HabitacionService habitacionService;
    @Test
    void testServiceLoads() {
        assertThat(habitacionService).isNotNull();
    }
    @Test
    void testFindAll() {
        assertThat(habitacionService.findAll()).isNotNull();
    }
}
