package edu.espe.springlab.domain.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("system", "Sistema de Gestión Hotelera");
        response.put("status", "UP");
        response.put("version", "1.0.0-reactivo");
        response.put("api_base", "/api/reactive");
        response.put("endpoints", Map.of(
                "habitaciones", "/api/reactive/habitaciones",
                "huespedes", "/api/reactive/huespedes",
                "reservas", "/api/reactive/reservas",
                "pagos", "/api/reactive/pagos",
                "docs", "/swagger-ui.html"));
        return response;
    }
}
