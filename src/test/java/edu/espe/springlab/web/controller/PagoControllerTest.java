package edu.espe.springlab.web.controller.pago;
import edu.espe.springlab.dto.pago.PagoRequest;
import edu.espe.springlab.dto.pago.PagoResponse;
import edu.espe.springlab.service.pago.PagoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(PagoController.class)
public class PagoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PagoService pagoService;
    @Test
    void testGetAllPagos() throws Exception {
        PagoResponse pago1 = new PagoResponse(1L, 1L, 100.0, LocalDateTime.now(), "Tarjeta de Crédito", "Completado", null, null);
        PagoResponse pago2 = new PagoResponse(2L, 2L, 150.0, LocalDateTime.now(), "Efectivo", "Completado", null, null);
        List<PagoResponse> pagos = Arrays.asList(pago1, pago2);
        when(pagoService.findAll()).thenReturn(pagos);
        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].metodoPago").value("Tarjeta de Crédito"));
    }
    @Test
    void testGetPagoById() throws Exception {
        PagoResponse pago = new PagoResponse(1L, 1L, 100.0, LocalDateTime.now(), "Tarjeta de Crédito", "Completado", null, null);
        when(pagoService.findById(1L)).thenReturn(pago);
        mockMvc.perform(get("/api/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metodoPago").value("Tarjeta de Crédito"))
                .andExpect(jsonPath("$.monto").value(100.0));
    }
    @Test
    void testCreatePago() throws Exception {
        PagoResponse pago = new PagoResponse(1L, 1L, 100.0, LocalDateTime.now(), "Tarjeta de Crédito", "Completado", null, null);
        when(pagoService.create(any(PagoRequest.class))).thenReturn(pago);
        String requestBody = "{\"reservaId\":1,\"monto\":100.0,\"fechaPago\":\"2025-11-16T10:00:00\",\"metodoPago\":\"Tarjeta de Crédito\",\"estado\":\"Completado\"}";
        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metodoPago").value("Tarjeta de Crédito"));
    }
    @Test
    void testUpdatePago() throws Exception {
        PagoResponse pago = new PagoResponse(1L, 1L, 100.0, LocalDateTime.now(), "Tarjeta de Crédito", "Completado", null, null);
        when(pagoService.update(eq(1L), any(PagoRequest.class))).thenReturn(pago);
        String requestBody = "{\"reservaId\":1,\"monto\":100.0,\"fechaPago\":\"2025-11-16T10:00:00\",\"metodoPago\":\"Tarjeta de Crédito\",\"estado\":\"Completado\"}";
        mockMvc.perform(put("/api/pagos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("Completado"));
    }
    @Test
    void testDeletePago() throws Exception {
        mockMvc.perform(delete("/api/pagos/1"))
                .andExpect(status().isNoContent());
    }
}
