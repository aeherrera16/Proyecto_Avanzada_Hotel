package edu.espe.springlab.web.controller;
import edu.espe.springlab.dto.habitacion.HabitacionRequest;
import edu.espe.springlab.dto.habitacion.HabitacionResponse;
import edu.espe.springlab.service.habitacion.HabitacionService;
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
@WebMvcTest(HabitacionController.class)
public class HabitacionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private HabitacionService habitacionService;
    @Test
    void testGetAllHabitaciones() throws Exception {
        HabitacionResponse habitacion1 = new HabitacionResponse(1L, "101", "Simple", 50.0, "Disponible", null, null);
        HabitacionResponse habitacion2 = new HabitacionResponse(2L, "102", "Doble", 80.0, "Disponible", null, null);
        List<HabitacionResponse> habitaciones = Arrays.asList(habitacion1, habitacion2);
        when(habitacionService.findAll()).thenReturn(habitaciones);
        mockMvc.perform(get("/api/habitaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].numero").value("101"));
    }
    @Test
    void testGetHabitacionById() throws Exception {
        HabitacionResponse habitacion = new HabitacionResponse(1L, "101", "Simple", 50.0, "Disponible", null, null);
        when(habitacionService.findById(1L)).thenReturn(habitacion);
        mockMvc.perform(get("/api/habitaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("101"))
                .andExpect(jsonPath("$.tipo").value("Simple"));
    }
    @Test
    void testCreateHabitacion() throws Exception {
        HabitacionResponse habitacion = new HabitacionResponse(1L, "101", "Simple", 50.0, "Disponible", null, null);
        when(habitacionService.create(any(HabitacionRequest.class))).thenReturn(habitacion);
        String requestBody = "{\"numero\":\"101\",\"tipo\":\"Simple\",\"precio\":50.0,\"estado\":\"Disponible\"}";
        mockMvc.perform(post("/api/habitaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value("101"));
    }
    @Test
    void testUpdateHabitacion() throws Exception {
        HabitacionResponse habitacion = new HabitacionResponse(1L, "101", "Simple", 50.0, "Ocupada", null, null);
        when(habitacionService.update(eq(1L), any(HabitacionRequest.class))).thenReturn(habitacion);
        String requestBody = "{\"numero\":\"101\",\"tipo\":\"Simple\",\"precio\":50.0,\"estado\":\"Ocupada\"}";
        mockMvc.perform(put("/api/habitaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("Ocupada"));
    }
    @Test
    void testDeleteHabitacion() throws Exception {
        mockMvc.perform(delete("/api/habitaciones/1"))
                .andExpect(status().isNoContent());
    }
}
