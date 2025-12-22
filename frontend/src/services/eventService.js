/**
 * Servicio de eventos reactivos.
 * Permite que diferentes componentes publiquen y escuchen eventos.
 */
class EventService {
    constructor() {
        this.listeners = [];
        this.logs = [];
    }

    // Agregar un log
    addLog(message, type = 'info') {
        const timestamp = new Date().toLocaleTimeString();
        const log = { time: timestamp, message, type };
        this.logs.push(log);

        // Mantener solo los últimos 100 logs
        if (this.logs.length > 100) {
            this.logs = this.logs.slice(-100);
        }

        // Notificar a todos los listeners
        this.listeners.forEach(callback => callback(log));
    }

    // Suscribirse a nuevos logs
    subscribe(callback) {
        this.listeners.push(callback);
        return () => {
            this.listeners = this.listeners.filter(l => l !== callback);
        };
    }

    // Obtener todos los logs
    getLogs() {
        return [...this.logs];
    }

    // Limpiar logs
    clearLogs() {
        this.logs = [];
        this.listeners.forEach(callback => callback({ clear: true }));
    }
}

// Singleton
const eventService = new EventService();
export default eventService;
