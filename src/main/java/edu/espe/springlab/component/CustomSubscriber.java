package edu.espe.springlab.component;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Subscriber personalizado para control de backpressure
 * Implementa control manual de la tasa de consumo de elementos
 */
public class CustomSubscriber<T> implements Subscriber<T> {
    
    private static final Logger log = LoggerFactory.getLogger(CustomSubscriber.class);
    
    private final String subscriberName;
    private final int batchSize;
    private final AtomicLong requested = new AtomicLong(0);
    private final AtomicLong processed = new AtomicLong(0);
    
    private Subscription subscription;
    private boolean completed = false;
    
    public CustomSubscriber(String subscriberName, int batchSize) {
        this.subscriberName = subscriberName;
        this.batchSize = batchSize;
    }
    
    @Override
    public void onSubscribe(Subscription subscription) {
        log.info("[{}] Subscriber suscrito", subscriberName);
        this.subscription = subscription;
        
        // Solicitar primer lote de elementos
        requestBatch();
    }
    
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
    
    @Override
    public void onError(Throwable throwable) {
        log.error("[{}] Error en subscriber: {}", subscriberName, throwable.getMessage(), throwable);
        completed = true;
    }
    
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
