import React, { useState, useEffect, useCallback } from 'react';
import { habitacionesService, reservasService, huespedesService } from '../services/api';
import eventService from '../services/eventService';
import '../styles/SearchBar.css';

/**
 * Panel de Actividad del Hotel.
 * Muestra en tiempo real las operaciones reactivas del sistema.
 * Se suscribe a eventos de otros componentes (como SearchBar).
 */
function ActividadReactiva() {
    const [stats, setStats] = useState({
        habitaciones: { total: 0, disponibles: 0, ocupadas: 0 },
        huespedes: 0,
        reservas: { total: 0, confirmadas: 0, pendientes: 0 }
    });
    const [loading, setLoading] = useState(false);
    const [logs, setLogs] = useState([]);

    const addLog = useCallback((message, type = 'info') => {
        const timestamp = new Date().toLocaleTimeString();
        setLogs(prev => [...prev.slice(-50), { time: timestamp, message, type }]);
        // También publicar al servicio global
        eventService.addLog(message, type);
    }, []);

    // Suscribirse a eventos de otros componentes
    useEffect(() => {
        const unsubscribe = eventService.subscribe((log) => {
            if (log.clear) {
                setLogs([]);
            } else {
                setLogs(prev => [...prev.slice(-50), log]);
            }
        });

        // Cargar logs existentes
        setLogs(eventService.getLogs().slice(-50));

        return unsubscribe;
    }, []);

    const fetchData = useCallback(async () => {
        setLoading(true);
        eventService.addLog('━━━ Iniciando flujo reactivo ━━━', 'header');
        eventService.addLog('Suscripción activa (onSubscribe)', 'info');

        try {
            // Habitaciones
            eventService.addLog('Flux<Habitacion>.findAll() → solicitando datos...', 'info');
            const habResponse = await habitacionesService.getAll();
            const habitaciones = habResponse.data.filter(h => h.id !== -1);
            habitaciones.forEach(h => {
                eventService.addLog(`  onNext: Habitación #${h.numero} (${h.tipo}) → ${h.estado}`, 'data');
            });
            eventService.addLog(`onComplete: ${habitaciones.length} habitaciones procesadas`, 'success');

            const disponibles = habitaciones.filter(h => h.estado === 'Disponible').length;
            const ocupadas = habitaciones.filter(h => h.estado === 'Ocupada').length;

            // Huéspedes
            eventService.addLog('Flux<Huesped>.findAll() → solicitando datos...', 'info');
            const huesResponse = await huespedesService.getAll();
            const huespedes = huesResponse.data.filter(h => h.id !== -1);
            huespedes.forEach(h => {
                eventService.addLog(`  onNext: ${h.nombre} ${h.apellido} (ID: ${h.id})`, 'data');
            });
            eventService.addLog(`onComplete: ${huespedes.length} huéspedes procesados`, 'success');

            // Reservas
            eventService.addLog('Flux<Reserva>.findAll() → solicitando datos...', 'info');
            const resResponse = await reservasService.getAll();
            const reservas = resResponse.data.filter(r => r.id !== -1);
            reservas.forEach(r => {
                eventService.addLog(`  onNext: Reserva #${r.id} → ${r.estado}`, 'data');
            });
            eventService.addLog(`onComplete: ${reservas.length} reservas procesadas`, 'success');

            const confirmadas = reservas.filter(r => r.estado === 'Confirmada').length;
            const pendientes = reservas.filter(r => r.estado === 'Pendiente').length;

            setStats({
                habitaciones: { total: habitaciones.length, disponibles, ocupadas },
                huespedes: huespedes.length,
                reservas: { total: reservas.length, confirmadas, pendientes }
            });

            eventService.addLog('━━━ Flujo reactivo completado ━━━', 'header');
        } catch (err) {
            eventService.addLog(`onError: ${err.message}`, 'error');
            eventService.addLog('El flujo se detuvo debido a un error', 'error');
        } finally {
            setLoading(false);
        }
    }, []);

    // STREAMING: Ver habitaciones llegando una por una
    const testStreaming = () => {
        eventService.addLog('━━━ STREAMING: Habitaciones en tiempo real ━━━', 'header');
        eventService.addLog('Conectando al stream (Server-Sent Events)...', 'info');
        eventService.addLog('onSubscribe: Cliente suscrito al flujo', 'info');

        const eventSource = new EventSource(habitacionesService.getStreamUrl());

        eventSource.onmessage = (event) => {
            try {
                const habitacion = JSON.parse(event.data);
                eventService.addLog(`  onNext: Habitación #${habitacion.numero} (${habitacion.tipo}) → ${habitacion.estado}`, 'data');
            } catch (e) {
                eventService.addLog(`  onNext: ${event.data}`, 'data');
            }
        };

        eventSource.onerror = () => {
            eventService.addLog('onComplete: Stream finalizado', 'success');
            eventSource.close();
        };

        // Auto-cerrar después de 30 segundos
        setTimeout(() => {
            if (eventSource.readyState !== EventSource.CLOSED) {
                eventService.addLog('Cerrando conexión de stream...', 'info');
                eventSource.close();
            }
        }, 30000);
    };

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    const clearLogs = () => {
        eventService.clearLogs();
    };

    const getLogColor = (type) => {
        switch (type) {
            case 'success': return '#28a745';
            case 'error': return '#dc3545';
            case 'data': return '#17a2b8';
            case 'header': return '#d4af37';
            case 'warn': return '#ffc107';
            case 'recovered': return '#ff9800';
            default: return '#888';
        }
    };

    return (
        <div className="container">
            <div className="section-title">
                <h2>Actividad del Hotel</h2>
                <div className="divider"></div>
                <p>Monitor de operaciones reactivas en tiempo real</p>
            </div>

            {/* Estadísticas */}
            <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
                gap: '20px',
                marginBottom: '30px'
            }}>
                <div style={{
                    background: '#fff',
                    padding: '25px',
                    textAlign: 'center',
                    border: '1px solid #ddd',
                    borderTop: '4px solid #d4af37'
                }}>
                    <div style={{ fontSize: '2.5rem', color: '#d4af37', fontWeight: '300' }}>
                        {stats.habitaciones.total}
                    </div>
                    <div style={{ color: '#666', textTransform: 'uppercase', letterSpacing: '1px', fontSize: '0.85rem' }}>
                        Habitaciones
                    </div>
                    <div style={{ marginTop: '10px', fontSize: '0.85rem', color: '#888' }}>
                        {stats.habitaciones.disponibles} disponibles • {stats.habitaciones.ocupadas} ocupadas
                    </div>
                </div>

                <div style={{
                    background: '#fff',
                    padding: '25px',
                    textAlign: 'center',
                    border: '1px solid #ddd',
                    borderTop: '4px solid #28a745'
                }}>
                    <div style={{ fontSize: '2.5rem', color: '#28a745', fontWeight: '300' }}>
                        {stats.huespedes}
                    </div>
                    <div style={{ color: '#666', textTransform: 'uppercase', letterSpacing: '1px', fontSize: '0.85rem' }}>
                        Huéspedes
                    </div>
                </div>

                <div style={{
                    background: '#fff',
                    padding: '25px',
                    textAlign: 'center',
                    border: '1px solid #ddd',
                    borderTop: '4px solid #17a2b8'
                }}>
                    <div style={{ fontSize: '2.5rem', color: '#17a2b8', fontWeight: '300' }}>
                        {stats.reservas.total}
                    </div>
                    <div style={{ color: '#666', textTransform: 'uppercase', letterSpacing: '1px', fontSize: '0.85rem' }}>
                        Reservas
                    </div>
                    <div style={{ marginTop: '10px', fontSize: '0.85rem', color: '#888' }}>
                        {stats.reservas.confirmadas} confirmadas • {stats.reservas.pendientes} pendientes
                    </div>
                </div>
            </div>

            {/* Controles */}
            <div style={{
                background: '#fff',
                padding: '15px 20px',
                border: '1px solid #ddd',
                marginBottom: '20px',
                display: 'flex',
                gap: '12px',
                alignItems: 'center',
                flexWrap: 'wrap'
            }}>
                <button
                    onClick={fetchData}
                    disabled={loading}
                    style={{
                        padding: '10px 20px',
                        background: loading ? '#ccc' : '#d4af37',
                        color: '#fff',
                        border: 'none',
                        cursor: loading ? 'not-allowed' : 'pointer',
                        fontSize: '0.8rem',
                        textTransform: 'uppercase',
                        letterSpacing: '1px'
                    }}
                >
                    {loading ? 'Cargando...' : 'Cargar Datos'}
                </button>

                <button
                    onClick={testStreaming}
                    style={{
                        padding: '10px 20px',
                        background: '#17a2b8',
                        color: '#fff',
                        border: 'none',
                        cursor: 'pointer',
                        fontSize: '0.8rem',
                        textTransform: 'uppercase',
                        letterSpacing: '1px'
                    }}
                >
                    Ver Streaming
                </button>

                <button
                    onClick={clearLogs}
                    style={{
                        padding: '10px 20px',
                        background: '#6c757d',
                        color: '#fff',
                        border: 'none',
                        cursor: 'pointer',
                        fontSize: '0.8rem',
                        textTransform: 'uppercase',
                        letterSpacing: '1px'
                    }}
                >
                    Limpiar
                </button>
            </div>

            {/* Log de operaciones reactivas */}
            <div style={{
                background: '#1a1a1a',
                padding: '20px',
                minHeight: '350px',
                maxHeight: '450px',
                overflowY: 'auto',
                fontFamily: 'Consolas, Monaco, monospace',
                fontSize: '0.85rem',
                lineHeight: '1.6'
            }}>
                <div style={{ color: '#555', marginBottom: '15px', borderBottom: '1px solid #333', paddingBottom: '10px' }}>
                    Operaciones Reactivas — Las búsquedas desde otras páginas también aparecen aquí
                </div>
                {logs.length === 0 ? (
                    <div style={{ color: '#555' }}>
                        Esperando operaciones...
                    </div>
                ) : (
                    logs.map((log, index) => (
                        <div key={index} style={{ color: getLogColor(log.type) }}>
                            <span style={{ color: '#444' }}>[{log.time}]</span> {log.message}
                        </div>
                    ))
                )}
            </div>

            <style>{`
        @keyframes pulse {
          0%, 100% { opacity: 1; }
          50% { opacity: 0.3; }
        }
      `}</style>
        </div>
    );
}

export default ActividadReactiva;
