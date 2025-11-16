package edu.espe.springlab.web.controller;
import edu.espe.springlab.dto.reserva.ReservaRequest;
import edu.espe.springlab.dto.reserva.ReservaResponse;
import edu.espe.springlab.service.reserva.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(ReservaController.class)
public class ReservaControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReservaService reservaService;
    @Test
    void testGetAllReservas() throws Exception {
        ReservaResponse reserva1 = new ReservaResponse(1L, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(2), 100.0, "Confirmada", null, null);
        ReservaResponse reserva2 = new ReservaResponse(2L, 2L, 2L, LocalDate.now(), LocalDate.now().plusDays(3), 150.0, "Confirmada", null, null);
        List<ReservaResponse> reservas = Arrays.asList(reserva1, reserva2);
        when(reservaService.findAll()).thenReturn(reservas);
        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].estado").value("Confirmada"));
    }
    @Test
    void testGetReservaById() throws Exception {
        ReservaResponse reserva = new ReservaResponse(1L, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(2), 100.0, "Confirmada", null, null);
        when(reservaService.findById(1L)).thenReturn(reserva);
        mockMvc.perform(get("/api/reservas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("Confirmada"))
                .andExpect(jsonPath("$.precioTotal").value(100.0));
    }
    @Test
    void testCreateReserva() throws Exception {
        ReservaResponse reserva = new ReservaResponse(1L, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(2), 100.0, "Confirmada", null, null);
        when(reservaService.create(any(ReservaRequest.class))).thenReturn(reserva);
        String requestBody = "{\"habitacionId\":1,\"huespedId\":1,\"fechaEntrada\":\"2025-11-16\",\"fechaSalida\":\"2025-11-18\",\"precioTotal\":100.0,\"estado\":\"Confirmada\"}";
        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("Confirmada"));
    }
    @Test
    void testUpdateReserva() throws Exception {
        ReservaResponse reserva = new ReservaResponse(1L, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(2), 100.0, "Cancelada", null, null);
        when(reservaService.update(eq(1L), any(ReservaRequest.class))).thenReturn(reserva);
        String requestBody = "{\"habitacionId\":1,\"huespedId\":1,\"fechaEntrada\":\"2025-11-16\",\"fechaSalida\":\"2025-11-18\",\"precioTotal\":100.0,\"estado\":\"Cancelada\"}";
        mockMvc.perform(put("/api/reservas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("Cancelada"));
    }
    @Test
    void testDeleteReserva() throws Exception {
        mockMvc.perform(delete("/api/reservas/1"))
                .andExpect(status().isNoContent());
    }
}
