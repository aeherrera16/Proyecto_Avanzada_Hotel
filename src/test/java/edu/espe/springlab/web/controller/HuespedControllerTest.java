package edu.espe.springlab.web.controller;
import edu.espe.springlab.dto.huesped.HuespedRequest;
import edu.espe.springlab.dto.huesped.HuespedResponse;
import edu.espe.springlab.service.huesped.HuespedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(HuespedController.class)
public class HuespedControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private HuespedService huespedService;
    @Test
    void testGetAllHuespedes() throws Exception {
        HuespedResponse huesped1 = new HuespedResponse(1L, "Juan", "Pérez", "1234567890", "juan@example.com", "0999999999", "Ecuatoriana", null, null);
        HuespedResponse huesped2 = new HuespedResponse(2L, "María", "González", "0987654321", "maria@example.com", "0988888888", "Ecuatoriana", null, null);
        List<HuespedResponse> huespedes = Arrays.asList(huesped1, huesped2);
        when(huespedService.findAll()).thenReturn(huespedes);
        mockMvc.perform(get("/api/huespedes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }
    @Test
    void testGetHuespedById() throws Exception {
        HuespedResponse huesped = new HuespedResponse(1L, "Juan", "Pérez", "1234567890", "juan@example.com", "0999999999", "Ecuatoriana", null, null);
        when(huespedService.findById(1L)).thenReturn(huesped);
        mockMvc.perform(get("/api/huespedes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Pérez"));
    }
    @Test
    void testCreateHuesped() throws Exception {
        HuespedResponse huesped = new HuespedResponse(1L, "Juan", "Pérez", "1234567890", "juan@example.com", "0999999999", "Ecuatoriana", null, null);
        when(huespedService.create(any(HuespedRequest.class))).thenReturn(huesped);
        String requestBody = "{\"nombre\":\"Juan\",\"apellido\":\"Pérez\",\"cedula\":\"1234567890\",\"email\":\"juan@example.com\",\"telefono\":\"0999999999\",\"nacionalidad\":\"Ecuatoriana\"}";
        mockMvc.perform(post("/api/huespedes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }
    @Test
    void testUpdateHuesped() throws Exception {
        HuespedResponse huesped = new HuespedResponse(1L, "Juan", "Pérez", "1234567890", "juan@example.com", "0988888888", "Ecuatoriana", null, null);
        when(huespedService.update(eq(1L), any(HuespedRequest.class))).thenReturn(huesped);
        String requestBody = "{\"nombre\":\"Juan\",\"apellido\":\"Pérez\",\"cedula\":\"1234567890\",\"email\":\"juan@example.com\",\"telefono\":\"0988888888\",\"nacionalidad\":\"Ecuatoriana\"}";
        mockMvc.perform(put("/api/huespedes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telefono").value("0988888888"));
    }
    @Test
    void testDeleteHuesped() throws Exception {
        mockMvc.perform(delete("/api/huespedes/1"))
                .andExpect(status().isNoContent());
    }
}
