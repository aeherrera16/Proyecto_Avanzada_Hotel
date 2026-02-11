package edu.espe.springlab;

import edu.espe.springlab.domain.Pago;
import edu.espe.springlab.repository.PagoRepository;
import edu.espe.springlab.service.reactive.PagoServiceReactivo;
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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;  // ← SOLO este import, NO ArgumentMatchers

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para PagoServiceReactivo")
class PagoServiceReactivoTest {

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoServiceReactivo pagoService;

    private Pago pago;

    @BeforeEach
    void setUp() {
        pago = new Pago();
        pago.setId(1L);
        pago.setReservaId(10L);
        pago.setMonto(100.0);
        pago.setMetodoPago("Tarjeta");
        pago.setEstado("COMPLETADO");
        pago.setFechaPago(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Pruebas de Búsqueda")
    class BusquedaTests {

        @Test
        @DisplayName("Debe retornar todos los pagos exitosamente")
        void findAll_debeRetornarListaPagos() {
            when(pagoRepository.findAll()).thenReturn(Flux.just(pago));

            StepVerifier.create(pagoService.findAll())
                    .expectNext(pago)
                    .verifyComplete();

            verify(pagoRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Debe retornar un pago cuando existe el ID")
        void findById_debeRetornarPagoSiExiste() {
            when(pagoRepository.findById(1L)).thenReturn(Mono.just(pago));

            StepVerifier.create(pagoService.findById(1L))
                    .expectNext(pago)
                    .verifyComplete();

            verify(pagoRepository, times(1)).findById(1L);  // ← SIN any(), SIN eq()
        }

        @Test
        @DisplayName("Debe retornar vacío cuando el pago no existe")
        void findById_debeRetornarVacioSiNoExiste() {
            when(pagoRepository.findById(1L)).thenReturn(Mono.empty());

            StepVerifier.create(pagoService.findById(1L))
                    .verifyComplete();

            verify(pagoRepository, times(1)).findById(1L);  // ← SIN any(), SIN eq()
        }
    }

    @Nested
    @DisplayName("Pruebas de Persistencia")
    class PersistenciaTests {

        @Test
        @DisplayName("Debe guardar un pago correctamente")
        void save_debeGuardarPagoCorrectamente() {
            when(pagoRepository.save(any(Pago.class))).thenReturn(Mono.just(pago));

            StepVerifier.create(pagoService.save(pago))
                    .expectNext(pago)
                    .verifyComplete();

            verify(pagoRepository, times(1)).save(any(Pago.class));
        }

        @Test
        @DisplayName("Debe capturar los datos correctos usando ArgumentCaptor")
        void save_debeCapturarDatosCorrectamente() {
            when(pagoRepository.save(any(Pago.class))).thenReturn(Mono.just(pago));
            ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);

            pagoService.save(pago).block();

            verify(pagoRepository).save(captor.capture());
            Pago capturado = captor.getValue();

            assertAll("Verificar datos del pago",
                    () -> assertEquals(1L, capturado.getId()),
                    () -> assertEquals(10L, capturado.getReservaId()),
                    () -> assertEquals(100.0, capturado.getMonto()),
                    () -> assertEquals("Tarjeta", capturado.getMetodoPago()),
                    () -> assertEquals("COMPLETADO", capturado.getEstado()),
                    () -> assertNotNull(capturado.getFechaPago())
            );
        }
    }

    @Nested
    @DisplayName("Pruebas de Validación")
    class ValidacionTests {

        @Test
        @DisplayName("Debe fallar al guardar con monto negativo")
        void save_debeFallarSiMontoNegativo() {
            pago.setMonto(-10.0);

            StepVerifier.create(pagoService.save(pago))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(pagoRepository, never()).save(any(Pago.class));
        }

        @Test
        @DisplayName("Debe fallar al guardar con monto cero")
        void save_debeFallarSiMontoCero() {
            pago.setMonto(0.0);

            StepVerifier.create(pagoService.save(pago))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(pagoRepository, never()).save(any(Pago.class));
        }

        @Test
        @DisplayName("Debe asignar Efectivo por defecto cuando método de pago está vacío")
        void save_debeAsignarEfectivoSiMetodoPagoVacio() {
            // ARRANGE
            pago.setMetodoPago("");
            when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

            // ACT
            Mono<Pago> resultado = pagoService.save(pago);

            // ASSERT
            StepVerifier.create(resultado)
                    .assertNext(p -> {
                        assertEquals("Efectivo", p.getMetodoPago());
                        assertEquals(100.0, p.getMonto());
                        assertEquals(10L, p.getReservaId());
                    })
                    .verifyComplete();

            verify(pagoRepository, times(1)).save(any(Pago.class));
        }
    }

    @Nested
    @DisplayName("Pruebas de Actualización")
    class ActualizacionTests {

        @Test
        @DisplayName("Debe actualizar un pago existente correctamente")
        void update_debeActualizarPagoSiExiste() {
            Pago nuevoPago = new Pago();
            nuevoPago.setReservaId(20L);
            nuevoPago.setMonto(200.0);
            nuevoPago.setMetodoPago("Efectivo");
            nuevoPago.setEstado("PENDIENTE");

            when(pagoRepository.findById(1L)).thenReturn(Mono.just(pago));
            when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation ->
                    Mono.just(invocation.getArgument(0)));

            StepVerifier.create(pagoService.update(1L, nuevoPago))
                    .assertNext(p -> {
                        assertEquals(1L, p.getId());
                        assertEquals(20L, p.getReservaId());
                        assertEquals(200.0, p.getMonto());
                        assertEquals("Efectivo", p.getMetodoPago());
                        assertEquals("PENDIENTE", p.getEstado());
                    })
                    .verifyComplete();

            verify(pagoRepository, times(1)).findById(1L);  // ← SIN any(), SIN eq()
            verify(pagoRepository, times(1)).save(any(Pago.class));
        }

        @Test
        @DisplayName("Debe lanzar error al actualizar un pago que no existe")
        void update_debeLanzarErrorSiNoExiste() {
            when(pagoRepository.findById(1L)).thenReturn(Mono.empty());

            StepVerifier.create(pagoService.update(1L, pago))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(pagoRepository, times(1)).findById(1L);  // ← SIN any(), SIN eq()
            verify(pagoRepository, never()).save(any(Pago.class));
        }

        @Test
        @DisplayName("Debe validar datos antes de actualizar")
        void update_debeValidarDatosAntesDeActualizar() {
            Pago pagoInvalido = new Pago();
            pagoInvalido.setMonto(-50.0);

            StepVerifier.create(pagoService.update(1L, pagoInvalido))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(pagoRepository, never()).findById(anyLong());
            verify(pagoRepository, never()).save(any(Pago.class));
        }
    }

    @Nested
    @DisplayName("Pruebas de Eliminación")
    class EliminacionTests {

        @Test
        @DisplayName("Debe eliminar un pago correctamente")
        void deleteById_debeEliminarPago() {
            when(pagoRepository.deleteById(1L)).thenReturn(Mono.empty());

            StepVerifier.create(pagoService.deleteById(1L))
                    .verifyComplete();

            verify(pagoRepository, times(1)).deleteById(1L);  // ← SIN any(), SIN eq()
        }
    }
}