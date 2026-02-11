package edu.espe.springlab;

import edu.espe.springlab.domain.Huesped;
import edu.espe.springlab.repository.HuespedRepository;
import edu.espe.springlab.service.reactive.HuespedServiceReactivo;
import edu.espe.springlab.exception.reactive.ReactiveResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas Unitarias para HuespedServiceReactivo
 */
@ExtendWith(MockitoExtension.class)
class HuespedServiceReactivoTest {

    @Mock
    private HuespedRepository huespedRepository;

    @InjectMocks
    private HuespedServiceReactivo huespedService;

    private Huesped huespedBase;

    @BeforeEach
    void setUp() {
        huespedBase = new Huesped();
        huespedBase.setId(1L);
        huespedBase.setNombre("Juan");
        huespedBase.setApellido("Perez");
        huespedBase.setCedula("0102030405");
        huespedBase.setEmail("juan@mail.com");
        huespedBase.setTelefono("0999999999");
        huespedBase.setNacionalidad("Ecuatoriano");
    }

    @Nested
    @DisplayName("Pruebas de Búsqueda")
    class BusquedaTests {

        @Test
        @DisplayName("Debe retornar todos los huéspedes")
        void testFindAll() {
            when(huespedRepository.findAll()).thenReturn(Flux.just(huespedBase));

            StepVerifier.create(huespedService.findAll())
                    .expectNext(huespedBase)
                    .verifyComplete();

            verify(huespedRepository, times(1)).findAll();
        }

    }
}
