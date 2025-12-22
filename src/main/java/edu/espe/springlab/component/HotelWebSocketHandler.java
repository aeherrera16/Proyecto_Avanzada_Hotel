package edu.espe.springlab.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * WebSocket Handler para notificaciones en tiempo real del hotel
 * Permite comunicación bidireccional para actualizaciones de habitaciones y reservas
 */
@Component
public class HotelWebSocketHandler implements WebSocketHandler {
    
    private static final Logger log = LoggerFactory.getLogger(HotelWebSocketHandler.class);
    
    private final ObjectMapper objectMapper;
    private final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Sinks.Many<NotificationMessage> notificationSink = Sinks.many().multicast().onBackpressureBuffer();
    
    public HotelWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        
        // Iniciar emisión de notificaciones periódicas
        startPeriodicNotifications();
    }
    
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String sessionId = session.getId();
        log.info("Nueva conexión WebSocket establecida: {}", sessionId);
        
        // Registrar sesión
        sessions.put(sessionId, session);
        
        // Enviar mensaje de bienvenida
        Mono<Void> welcomeMessage = session.send(Mono.just(session.textMessage(
                new NotificationMessage("CONNECTION", "Conectado exitosamente", sessionId, LocalDateTime.now())
                        .toJson(objectMapper))));
        
        // Manejar mensajes entrantes
        Mono<Void> inbound = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(message -> log.info("Mensaje recibido de {}: {}", sessionId, message))
                .doOnError(error -> log.error("Error recibiendo mensaje de {}: {}", sessionId, error.getMessage()))
                .then();
        
        // Manejar mensajes salientes (notificaciones)
        Mono<Void> outbound = session.send(
                notificationSink.asFlux()
                        .map(notification -> session.textMessage(notification.toJson(objectMapper)))
                        .doOnNext(message -> log.debug("Enviando notificación a {}", sessionId))
        );
        
        // Manejar desconexión
        Mono<Void> onClose = session.close()
                .doOnTerminate(() -> {
                    sessions.remove(sessionId);
                    log.info("Conexión WebSocket cerrada: {}", sessionId);
                });
        
        return welcomeMessage
                .then(Mono.zip(inbound, outbound).then())
                .then(onClose);
    }
    
    /**
     * Envía notificación a todos los clientes conectados
     */
    public void broadcastNotification(String type, String message, Object data) {
        NotificationMessage notification = new NotificationMessage(type, message, data, LocalDateTime.now());
        notificationSink.tryEmitNext(notification);
        log.info("Notificación broadcast enviada: {} - {}", type, message);
    }
    
    /**
     * Envía notificación a un cliente específico
     */
    public Mono<Void> sendNotificationToSession(String sessionId, String type, String message, Object data) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null) {
            NotificationMessage notification = new NotificationMessage(type, message, data, LocalDateTime.now());
            return session.send(Mono.just(session.textMessage(notification.toJson(objectMapper))))
                    .doOnSuccess(v -> log.info("Notificación enviada a {}: {}", sessionId, type))
                    .doOnError(error -> log.error("Error enviando notificación a {}: {}", sessionId, error.getMessage()));
        }
        return Mono.empty();
    }
    
    /**
     * Inicia notificaciones periódicas (ej. estado de habitaciones)
     */
    private void startPeriodicNotifications() {
        Flux.interval(Duration.ofSeconds(30))
                .map(tick -> new NotificationMessage(
                        "HEARTBEAT", 
                        "Sistema activo", 
                        new SystemStatus(sessions.size(), LocalDateTime.now()),
                        LocalDateTime.now()
                ))
                .doOnNext(notificationSink::tryEmitNext)
                .subscribe();
    }
    
    /**
     * Notificación de nueva reserva
     */
    public void notifyNewReservation(Object reserva) {
        broadcastNotification("NEW_RESERVATION", "Nueva reserva creada", reserva);
    }
    
    /**
     * Notificación de cambio de estado de habitación
     */
    public void notifyHabitacionStatusChange(Object habitacion) {
        broadcastNotification("HABITACION_STATUS_CHANGE", "Estado de habitación actualizado", habitacion);
    }
    
    /**
     * Notificación de reserva cancelada
     */
    public void notifyReservationCancelled(Object reserva) {
        broadcastNotification("RESERVATION_CANCELLED", "Reserva cancelada", reserva);
    }
    
    /**
     * Obtiene estadísticas de conexiones
     */
    public ConnectionStats getConnectionStats() {
        return new ConnectionStats(sessions.size(), sessions.keySet());
    }
    
    /**
     * Clase para mensajes de notificación
     */
    public static class NotificationMessage {
        private final String type;
        private final String message;
        private final Object data;
        private final LocalDateTime timestamp;
        
        public NotificationMessage(String type, String message, Object data, LocalDateTime timestamp) {
            this.type = type;
            this.message = message;
            this.data = data;
            this.timestamp = timestamp;
        }
        
        public String toJson(ObjectMapper mapper) {
            try {
                return mapper.writeValueAsString(this);
            } catch (Exception e) {
                return String.format("{\"type\":\"%s\",\"message\":\"%s\",\"timestamp\":\"%s\"}", 
                        type, message, timestamp);
            }
        }
        
        // Getters
        public String getType() { return type; }
        public String getMessage() { return message; }
        public Object getData() { return data; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    /**
     * Clase para estado del sistema
     */
    public static class SystemStatus {
        private final int activeConnections;
        private final LocalDateTime timestamp;
        
        public SystemStatus(int activeConnections, LocalDateTime timestamp) {
            this.activeConnections = activeConnections;
            this.timestamp = timestamp;
        }
        
        // Getters
        public int getActiveConnections() { return activeConnections; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    /**
     * Clase para estadísticas de conexión
     */
    public static class ConnectionStats {
        private final int totalConnections;
        private final java.util.Set<String> activeSessionIds;
        
        public ConnectionStats(int totalConnections, java.util.Set<String> activeSessionIds) {
            this.totalConnections = totalConnections;
            this.activeSessionIds = activeSessionIds;
        }
        
        // Getters
        public int getTotalConnections() { return totalConnections; }
        public java.util.Set<String> getActiveSessionIds() { return activeSessionIds; }
        
        @Override
        public String toString() {
            return String.format("ConnectionStats{total=%d, sessions=%s}", totalConnections, activeSessionIds);
        }
    }
}
