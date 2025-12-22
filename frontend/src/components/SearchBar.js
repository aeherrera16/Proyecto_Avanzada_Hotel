// Importaciones estándar de React y del servicio de eventos
import React, { useState } from 'react';
import eventService from '../services/eventService';

/**
 * Barra de búsqueda con selector de modo (con/sin recuperación).
 * Publica eventos al servicio para que se vean en Actividad.
 */
// Componente reutilizable de búsqueda con soporte para dos estrategias reactivas
function SearchBar({
    entityName,
    searchFunctionConRecuperacion,
    searchFunctionSinRecuperacion,
    onResult,
    placeholder = "Buscar por ID..."
}) {
    const [searchId, setSearchId] = useState('');
    const [searching, setSearching] = useState(false);
    const [mode, setMode] = useState('con'); // 'con' o 'sin'

    // Ejecuta la búsqueda según el modo seleccionado
    const handleSearch = async () => {
        if (!searchId.trim()) return;

        setSearching(true);

        // Publicar inicio de búsqueda
        eventService.addLog(`━━━ Búsqueda de ${entityName} ID: ${searchId} ━━━`, 'header');
        eventService.addLog(`Mono<${entityName}>.findById(${searchId}) → buscando...`, 'info');
        eventService.addLog(`Modo: ${mode === 'con' ? 'CON recuperación' : 'SIN recuperación'}`, 'info');

        try {
            // Selecciona dinámicamente la función de búsqueda según el modo
            const searchFn = mode === 'con'
                ? searchFunctionConRecuperacion
                : searchFunctionSinRecuperacion;

            const response = await searchFn(searchId);

            // Verificar si es una respuesta recuperada (id === -1)
            const isRecovered = response.data.id === -1;

            if (isRecovered) {
                // Log detallado cuando se usa recuperación (onErrorResume)
                eventService.addLog(`  switchIfEmpty → ID ${searchId} no existe en BD`, 'warn');
                eventService.addLog(`  onErrorResume ACTIVADO!`, 'recovered');
                eventService.addLog(`  → Retornando valor por defecto`, 'recovered');
                eventService.addLog(`  onNext: ${entityName} recuperado (ID: -1)`, 'data');
                eventService.addLog(`onComplete: Flujo continuó exitosamente`, 'success');
            } else {
                // Log cuando se encuentra el recurso normalmente
                eventService.addLog(`  onNext: ${entityName} encontrado (ID: ${response.data.id})`, 'data');
                eventService.addLog(`onComplete: Búsqueda exitosa`, 'success');
            }

            // Notifica al componente padre del resultado
            onResult && onResult({
                data: response.data,
                found: !isRecovered,
                recovered: isRecovered,
                mode: mode,
                error: null
            });
        } catch (err) {
            // Log detallado del fallo cuando NO hay recuperación
            eventService.addLog(`━━━ ❌ ERROR SIN RECUPERACIÓN ━━━`, 'error');
            eventService.addLog(`  switchIfEmpty → ID ${searchId} no existe en BD`, 'warn');
            eventService.addLog(`  ⚠️ Sin onErrorResume configurado`, 'error');
            eventService.addLog(`  onError: ${err.response?.data?.message || err.message || 'Error del servidor'}`, 'error');
            eventService.addLog(`  → El flujo SE DETUVO COMPLETAMENTE`, 'error');
            eventService.addLog(`━━━ Esto demuestra la importancia de onErrorResume ━━━`, 'error');

            // Notifica al padre del error
            onResult && onResult({
                data: null,
                found: false,
                recovered: false,
                mode: mode,
                error: err.response?.data?.message || err.message || 'Error en la búsqueda'
            });
        } finally {
            setSearching(false);
        }
    };

    // Permite ejecutar la búsqueda al presionar Enter
    const handleKeyPress = (e) => {
        if (e.key === 'Enter') handleSearch();
    };

    // Renderizado del componente de búsqueda
    return (
        <div className="search-bar-container">
            <div className="search-bar">
                <input
                    type="text"
                    value={searchId}
                    onChange={(e) => setSearchId(e.target.value)}
                    onKeyPress={handleKeyPress}
                    placeholder={placeholder}
                    className="search-input"
                />
                {/* Selector para cambiar entre modos de recuperación */}
                <select
                    value={mode}
                    onChange={(e) => setMode(e.target.value)}
                    className="search-mode-select"
                >
                    <option value="con">Con Recuperación</option>
                    <option value="sin">Sin Recuperación</option>
                </select>
                <button
                    onClick={handleSearch}
                    disabled={searching || !searchId.trim()}
                    className="search-btn"
                >
                    {searching ? '...' : '🔍'}
                </button>
            </div>
        </div>
    );
}

// Exportación por defecto del componente
export default SearchBar;
