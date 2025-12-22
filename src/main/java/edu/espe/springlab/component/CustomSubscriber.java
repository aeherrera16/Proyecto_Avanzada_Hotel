package edu.espe.springlab.component;

// Importaciones del estándar Reactive Streams y logging
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Subscriber personalizado para control de backpressure
 * Implementa control manual de la tasa de consumo de elementos
 */
// Implementación de Subscriber que gestiona explícitamente la demanda (backpressure)
public class CustomSubscriber<T> implements Subscriber<T> {

    // Logger para registrar eventos del subscriber
    private static final Logger log = LoggerFactory.getLogger(CustomSubscriber.class);

    // Nombre identificador del subscriber (para logs)
    private final String subscriberName;
    // Tamaño del lote de elementos a solicitar en cada petición
    private final int batchSize;
    // Contador de elementos solicitados al publisher
    private final AtomicLong requested = new AtomicLong(0);
    // Contador de elementos procesados
    private final AtomicLong processed = new AtomicLong(0);

    // Suscripción activa con el publisher
    private Subscription subscription;
    // Indica si el flujo ha terminado (por error o completación)
    private boolean completed = false;

    // Constructor con nombre y tamaño de lote
    public CustomSubscriber(String subscriberName, int batchSize) {
        this.subscriberName = subscriberName;
        this.batchSize = batchSize;
    }

    // Se llama al suscribirse: guarda la suscripción y solicita el primer lote
    @Override
    public void onSubscribe(Subscription subscription) {
        log.info("[{}] Subscriber suscrito", subscriberName);
        this.subscription = subscription;
        
        // Solicitar primer lote de elementos
        requestBatch();
    }

    // Procesa cada elemento emitido por el publisher
    @Override
    public void onNext(T item) {
        log.info("[{}] Procesando elemento: {}", subscriberName, item);
        
        // Simular procesamiento
        try {
            Thread.sleep(100); // Simular trabajo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[{}] Procesamiento interrumpido", subscriberName);
        }
        
        long processedCount = processed.incrementAndGet();
        log.info("[{}] Elementos procesados: {}", subscriberName, processedCount);
        
        // Solicitar más elementos si completamos el lote
        if (processedCount % batchSize == 0) {
            requestBatch();
        }
    }

    // Maneja errores en el flujo reactivo
    @Override
    public void onError(Throwable throwable) {
        log.error("[{}] Error en subscriber: {}", subscriberName, throwable.getMessage(), throwable);
        completed = true;
    }

    // Se llama al finalizar exitosamente el flujo
    @Override
    public void onComplete() {
        log.info("[{}] Subscriber completado. Total procesados: {}", subscriberName, processed.get());
        completed = true;
    }
    
    /**
     * Solicita un lote de elementos al publisher
     */
    private void requestBatch() {
        if (subscription != null && !completed) {
            long currentRequested = requested.addAndGet(batchSize);
            subscription.request(batchSize);
            log.info("[{}] Solicitando {} elementos. Total solicitados: {}", 
                    subscriberName, batchSize, currentRequested);
        }
    }
    
    /**
     * Cancela la suscripción
     */
    public void cancel() {
        if (subscription != null && !completed) {
            log.info("[{}] Cancelando suscripción", subscriberName);
            subscription.cancel();
            completed = true;
        }
    }
    
    /**
     * Obtiene estadísticas del subscriber
     */
    public SubscriberStats getStats() {
        return new SubscriberStats(
                subscriberName,
                processed.get(),
                requested.get(),
                completed
        );
    }
    
    /**
     * Clase para estadísticas del subscriber
     */
    public static class SubscriberStats {
        private final String name;
        private final long processed;
        private final long requested;
        private final boolean completed;
        
        public SubscriberStats(String name, long processed, long requested, boolean completed) {
            this.name = name;
            this.processed = processed;
            this.requested = requested;
            this.completed = completed;
        }
        
        // Getters
        public String getName() { return name; }
        public long getProcessed() { return processed; }
        public long getRequested() { return requested; }
        public boolean isCompleted() { return completed; }
        
        @Override
        public String toString() {
            return String.format("SubscriberStats{name='%s', processed=%d, requested=%d, completed=%s}",
                    name, processed, requested, completed);
        }
    }
}
