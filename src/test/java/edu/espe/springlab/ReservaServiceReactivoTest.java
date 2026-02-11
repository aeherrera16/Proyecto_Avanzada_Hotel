package edu.espe.springlab;

import edu.espe.springlab.domain.Reserva;
import edu.espe.springlab.domain.Huesped;
import edu.espe.springlab.domain.Habitacion;
import edu.espe.springlab.domain.Pago;
import edu.espe.springlab.dto.ReservaCompletaDTO;
import edu.espe.springlab.repository.ReservaRepository;
import edu.espe.springlab.repository.HuespedRepository;
import edu.espe.springlab.repository.HabitacionRepository;
import edu.espe.springlab.repository.PagoRepository;
import edu.espe.springlab.service.reactive.ReservaServiceReactivo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas Unitarias para el servicio de Reservas
 * Siguiendo el patrón AAA (Arrange, Act, Assert)
 * Autor: Herrera (Estudiante)
 */
@ExtendWith(MockitoExtension.class)
public class ReservaServiceReactivoTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private HuespedRepository huespedRepository;

    @Mock
    private HabitacionRepository habitacionRepository;

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private ReservaServiceReactivo reservaService;

    private Reserva reservaBase;

    @BeforeEach
    void setUp() {
        // ARRANGE: Preparar el objeto base que usaremos en las pruebas
        reservaBase = new Reserva();
        reservaBase.setId(1L);
        reservaBase.setHuespedId(1L);
        reservaBase.setHabitacionId(1L);
        reservaBase.setFechaEntrada(LocalDate.now().plusDays(1));
        reservaBase.setFechaSalida(LocalDate.now().plusDays(5));
        reservaBase.setPrecioTotal(150.0);
        reservaBase.setEstado("Confirmada");
    }

    @Nested
    @DisplayName("Pruebas de Búsqueda")
    class BusquedaTests {

        @Test
        @DisplayName("Debe retornar todas las reservas exitosamente")
        void testFindAll() {
            // Arrange
            when(reservaRepository.findAll()).thenReturn(Flux.just(reservaBase));

            // Act & Assert
            StepVerifier.create(reservaService.findAll())
                    .expectNext(reservaBase)
                    .verifyComplete();

            verify(reservaRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Debe encontrar una reserva por ID con recuperación")
        void testFindById_Success() {
            // Arrange
            when(reservaRepository.findById(1L)).thenReturn(Mono.just(reservaBase));

            // Act & Assert
            StepVerifier.create(reservaService.findById(1L))
                    .expectNext(reservaBase)
                    .verifyComplete();

            verify(reservaRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Debe retornar reserva por defecto cuando no existe (Recuperación)")
        void testFindById_NotFound_ReturnsDefault() {
            // Arrange
            when(reservaRepository.findById(99L)).thenReturn(Mono.empty());

            // Act & Assert
            StepVerifier.create(reservaService.findById(99L))
                    .expectNextMatches(reserva -> reserva.getId().equals(-1L))
                    .verifyComplete();

            verify(reservaRepository, times(1)).findById(99L);
        }
    }

    @Nested
    @DisplayName("Pruebas de Persistencia y Validación")
    class PersistenciaTests {

        @Test
        @DisplayName("Debe guardar una reserva válida exitosamente")
        void testSave_Success() {
            // Arrange
            when(reservaRepository.save(any(Reserva.class))).thenReturn(Mono.just(reservaBase));

            // Act & Assert
            StepVerifier.create(reservaService.save(reservaBase))
                    .expectNext(reservaBase)
                    .verifyComplete();

            verify(reservaRepository).save(any(Reserva.class));
        }

        @Test
        @DisplayName("Debe fallar al guardar con precio cero o negativo")
        void testSave_NegativeAmount() {
            // Arrange
            reservaBase.setPrecioTotal(-10.0);

            // Act & Assert
            StepVerifier.create(reservaService.save(reservaBase))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(reservaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe capturar los datos correctos usando ArgumentCaptor")
        void testSave_WithArgumentCaptor() {
            // Arrange
            when(reservaRepository.save(any(Reserva.class))).thenReturn(Mono.just(reservaBase));
            ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);

            // Act
            reservaService.save(reservaBase).block();

            // Assert
            verify(reservaRepository).save(captor.capture());
            Reserva capturada = captor.getValue();
            assertEquals(150.0, capturada.getPrecioTotal());
            assertEquals("Confirmada", capturada.getEstado());
        }
    }

    @Nested
    @DisplayName("Pruebas de Integración (InOrder)")
    class IntegracionTests {

        @Test
        @DisplayName("Debe verificar el orden de ejecución en el guardado completo")
        void testSaveReservaCompleta_Order() {
            // Arrange
            ReservaCompletaDTO dto = new ReservaCompletaDTO();
            dto.setHabitacionId(1L);
            dto.setFechaEntrada(LocalDate.now().plusDays(1));
            dto.setFechaSalida(LocalDate.now().plusDays(2));
            dto.setPrecioTotal(new BigDecimal("100"));
            dto.setMetodoPago("Efectivo");

            Huesped huesped = new Huesped();
            huesped.setId(1L);
            Habitacion habitacion = new Habitacion();
            habitacion.setId(1L);
            Pago pago = new Pago();
            pago.setId(1L);

            when(huespedRepository.save(any())).thenReturn(Mono.just(huesped));
            when(reservaRepository.save(any())).thenReturn(Mono.just(reservaBase));
            when(pagoRepository.save(any())).thenReturn(Mono.just(pago));
            when(habitacionRepository.findById(anyLong())).thenReturn(Mono.just(habitacion));
            when(habitacionRepository.save(any())).thenReturn(Mono.just(habitacion));

            // Act
            StepVerifier.create(reservaService.saveReservaCompleta(dto))
                    .expectNextCount(1)
                    .verifyComplete();

            // Assert: Verificar orden de interacciones
            InOrder inOrder = inOrder(huespedRepository, reservaRepository, pagoRepository, habitacionRepository);
            inOrder.verify(huespedRepository).save(any());
            inOrder.verify(reservaRepository).save(any());
            inOrder.verify(pagoRepository).save(any());
            inOrder.verify(habitacionRepository).findById(anyLong());
            inOrder.verify(habitacionRepository).save(any());
        }
    }
}
