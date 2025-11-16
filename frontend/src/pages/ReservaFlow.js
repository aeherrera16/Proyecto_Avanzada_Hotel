import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { habitacionesService, huespedesService, reservasService, pagosService } from '../services/api';
import PagoSimulador from '../components/PagoSimulador';

function ReservaFlow() {
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [habitacionesDisponibles, setHabitacionesDisponibles] = useState([]);

  // Datos del formulario
  const [huespedData, setHuespedData] = useState({
    nombre: '',
    apellido: '',
    cedula: '',
    email: '',
    telefono: '',
    nacionalidad: ''
  });

  const [reservaData, setReservaData] = useState({
    habitacionId: null,
    fechaEntrada: '',
    fechaSalida: '',
    precioTotal: 0
  });

  const [habitacionSeleccionada, setHabitacionSeleccionada] = useState(null);
  const [huespedId, setHuespedId] = useState(null);
  const [reservaId, setReservaId] = useState(null);

  useEffect(() => {
    if (currentStep === 2) {
      cargarHabitaciones();
    }
  }, [currentStep]);

  const cargarHabitaciones = async () => {
    try {
      setLoading(true);
      const response = await habitacionesService.getAll();
      const disponibles = response.data.filter(h => h.estado === 'Disponible');
      setHabitacionesDisponibles(disponibles);
    } catch (err) {
      setError('Error al cargar habitaciones disponibles');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const calcularPrecioTotal = () => {
    if (!reservaData.fechaEntrada || !reservaData.fechaSalida || !habitacionSeleccionada) {
      return 0;
    }
    const entrada = new Date(reservaData.fechaEntrada);
    const salida = new Date(reservaData.fechaSalida);
    const noches = Math.ceil((salida - entrada) / (1000 * 60 * 60 * 24));
    return noches * habitacionSeleccionada.precio;
  };

  const handleHuespedSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await huespedesService.create(huespedData);
      setHuespedId(response.data.id);
      setCurrentStep(2);
    } catch (err) {
      // Manejo específico para cédula duplicada
      if (err.response?.data?.message?.includes('Duplicate entry') || 
          err.message?.includes('Duplicate entry')) {
        setError('⚠️ Ya existe un huésped registrado con esa cédula o email. Por favor, use datos diferentes.');
      } else {
        setError('Error al registrar huésped: ' + (err.response?.data?.message || err.message));
      }
    } finally {
      setLoading(false);
    }
  };

  const handleHabitacionSelect = (habitacion) => {
    setHabitacionSeleccionada(habitacion);
    setReservaData({ ...reservaData, habitacionId: habitacion.id });
  };

  const handleFechasSubmit = (e) => {
    e.preventDefault();
    const precioTotal = calcularPrecioTotal();
    setReservaData({ ...reservaData, precioTotal });
    setCurrentStep(4);
  };

  const handlePagoCompletado = async (datosPago) => {
    setLoading(true);
    setError(null);

    try {
      // 1. Crear la reserva
      const reservaPayload = {
        huespedId: huespedId,
        habitacionId: reservaData.habitacionId,
        fechaEntrada: reservaData.fechaEntrada,
        fechaSalida: reservaData.fechaSalida,
        precioTotal: reservaData.precioTotal,
        estado: 'Confirmada'
      };

      const reservaResponse = await reservasService.create(reservaPayload);
      setReservaId(reservaResponse.data.id);

      // 2. Registrar el pago
      const pagoPayload = {
        reservaId: reservaResponse.data.id,
        monto: reservaData.precioTotal,
        fechaPago: new Date().toISOString(),
        metodoPago: datosPago.metodoPago,
        estado: 'Completado'
      };

      await pagosService.create(pagoPayload);

      // 3. Actualizar estado de la habitación a Ocupada
      await habitacionesService.update(reservaData.habitacionId, {
        ...habitacionSeleccionada,
        estado: 'Ocupada'
      });

      setCurrentStep(5);
    } catch (err) {
      setError('Error al procesar la reserva: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const getImageByTipo = (tipo) => {
    const images = {
      'Simple': 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
      'Doble': 'https://images.unsplash.com/photo-1590490360182-c33d57733427?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
      'Suite': 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
      'Suite Presidencial': 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'
    };
    return images[tipo] || images['Simple'];
  };

  return (
    <div className="container">
      {/* Progress Indicator */}
      <div className="wizard-steps">
        <div className={`wizard-step ${currentStep >= 1 ? 'active' : ''} ${currentStep > 1 ? 'completed' : ''}`}>
          <div className="step-number">1</div>
          <div className="step-label">Datos del Huésped</div>
        </div>
        <div className={`wizard-step ${currentStep >= 2 ? 'active' : ''} ${currentStep > 2 ? 'completed' : ''}`}>
          <div className="step-number">2</div>
          <div className="step-label">Seleccionar Habitación</div>
        </div>
        <div className={`wizard-step ${currentStep >= 3 ? 'active' : ''} ${currentStep > 3 ? 'completed' : ''}`}>
          <div className="step-number">3</div>
          <div className="step-label">Fechas de Reserva</div>
        </div>
        <div className={`wizard-step ${currentStep >= 4 ? 'active' : ''} ${currentStep > 4 ? 'completed' : ''}`}>
          <div className="step-number">4</div>
          <div className="step-label">Pago</div>
        </div>
        <div className={`wizard-step ${currentStep >= 5 ? 'active' : ''}`}>
          <div className="step-number">5</div>
          <div className="step-label">Confirmación</div>
        </div>
      </div>

      {error && <div className="error-message">{error}</div>}

      {/* Step 1: Datos del Huésped */}
      {currentStep === 1 && (
        <div className="wizard-content">
          <div className="section-title">
            <h2>Registro de Huésped</h2>
            <div className="divider"></div>
            <p>Por favor, ingrese sus datos personales para continuar</p>
          </div>

          <div style={{ 
            background: '#fff3cd', 
            border: '1px solid #ffc107', 
            padding: '15px', 
            borderRadius: '8px', 
            marginBottom: '20px',
            maxWidth: '600px',
            margin: '0 auto 20px'
          }}>
            <strong>💡 Nota:</strong> Si ya tiene una cuenta, use datos diferentes (nueva cédula/email). 
            Los huéspedes de prueba ya registrados: 1750285577, 1234567890, 1765432109
          </div>

          <form onSubmit={handleHuespedSubmit} className="form-container">
            <div className="form-row">
              <div className="form-group">
                <label>Nombre *</label>
                <input
                  type="text"
                  value={huespedData.nombre}
                  onChange={(e) => setHuespedData({ ...huespedData, nombre: e.target.value })}
                  required
                  placeholder="Ingrese su nombre"
                />
              </div>
              <div className="form-group">
                <label>Apellido *</label>
                <input
                  type="text"
                  value={huespedData.apellido}
                  onChange={(e) => setHuespedData({ ...huespedData, apellido: e.target.value })}
                  required
                  placeholder="Ingrese su apellido"
                />
              </div>
            </div>

            <div className="form-group">
              <label>Identificación (Cédula/Pasaporte) *</label>
              <input
                type="text"
                value={huespedData.cedula}
                onChange={(e) => setHuespedData({ ...huespedData, cedula: e.target.value })}
                required
                placeholder="Número de identificación (10-13 caracteres)"
                minLength="10"
                maxLength="13"
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Email *</label>
                <input
                  type="email"
                  value={huespedData.email}
                  onChange={(e) => setHuespedData({ ...huespedData, email: e.target.value })}
                  required
                  placeholder="correo@ejemplo.com"
                />
              </div>
              <div className="form-group">
                <label>Teléfono *</label>
                <input
                  type="tel"
                  value={huespedData.telefono}
                  onChange={(e) => {
                    const value = e.target.value.replace(/\D/g, '');
                    if (value.length <= 10) {
                      setHuespedData({ ...huespedData, telefono: value });
                    }
                  }}
                  required
                  placeholder="0999999999 (10 dígitos)"
                  pattern="[0-9]{10}"
                  title="El teléfono debe tener exactamente 10 dígitos"
                />
              </div>
            </div>

            <div className="form-group">
              <label>Nacionalidad *</label>
              <input
                type="text"
                value={huespedData.nacionalidad}
                onChange={(e) => setHuespedData({ ...huespedData, nacionalidad: e.target.value })}
                required
                placeholder="Ej: Ecuatoriana"
              />
            </div>

            <button type="submit" className="btn-submit" disabled={loading}>
              {loading ? 'Registrando...' : 'Continuar a Selección de Habitación'}
            </button>
          </form>
        </div>
      )}

      {/* Step 2: Seleccionar Habitación */}
      {currentStep === 2 && (
        <div className="wizard-content">
          <div className="section-title">
            <h2>Seleccione su Habitación</h2>
            <div className="divider"></div>
            <p>Elija la habitación que mejor se adapte a sus necesidades</p>
          </div>

          {loading ? (
            <div className="loading">Cargando habitaciones disponibles...</div>
          ) : (
            <>
              <div className="cards-grid">
                {habitacionesDisponibles.map((habitacion) => (
                  <div
                    key={habitacion.id}
                    className={`card selectable ${habitacionSeleccionada?.id === habitacion.id ? 'selected' : ''}`}
                    onClick={() => handleHabitacionSelect(habitacion)}
                  >
                    <img src={getImageByTipo(habitacion.tipo)} alt={habitacion.tipo} className="card-image" />
                    <div className="card-content">
                      <h3>Habitación {habitacion.numero}</h3>
                      <p><strong>Tipo:</strong> {habitacion.tipo}</p>
                      <div className="card-price">${habitacion.precio} / noche</div>
                      {habitacionSeleccionada?.id === habitacion.id && (
                        <span className="card-badge" style={{ backgroundColor: '#28a745' }}>✓ Seleccionada</span>
                      )}
                    </div>
                  </div>
                ))}
              </div>

              {habitacionesDisponibles.length === 0 && (
                <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
                  <p>No hay habitaciones disponibles en este momento.</p>
                </div>
              )}

              <div style={{ textAlign: 'center', marginTop: '30px' }}>
                <button
                  onClick={() => setCurrentStep(1)}
                  className="btn-secondary"
                  style={{ marginRight: '10px' }}
                >
                  Atrás
                </button>
                <button
                  onClick={() => habitacionSeleccionada && setCurrentStep(3)}
                  className="btn-primary"
                  disabled={!habitacionSeleccionada}
                >
                  Continuar a Fechas
                </button>
              </div>
            </>
          )}
        </div>
      )}

      {/* Step 3: Fechas de Reserva */}
      {currentStep === 3 && (
        <div className="wizard-content">
          <div className="section-title">
            <h2>Fechas de su Estadía</h2>
            <div className="divider"></div>
            <p>Seleccione las fechas de entrada y salida</p>
          </div>

          <div className="reservation-summary">
            <h3>Resumen de Reserva</h3>
            <div className="summary-item">
              <span>Habitación:</span>
              <strong>{habitacionSeleccionada.tipo} - #{habitacionSeleccionada.numero}</strong>
            </div>
            <div className="summary-item">
              <span>Precio por noche:</span>
              <strong>${habitacionSeleccionada.precio}</strong>
            </div>
          </div>

          <form onSubmit={handleFechasSubmit} className="form-container" style={{ maxWidth: '600px' }}>
            <div className="form-row">
              <div className="form-group">
                <label>Fecha de Entrada *</label>
                <input
                  type="date"
                  value={reservaData.fechaEntrada}
                  onChange={(e) => setReservaData({ ...reservaData, fechaEntrada: e.target.value })}
                  min={new Date().toISOString().split('T')[0]}
                  required
                />
              </div>
              <div className="form-group">
                <label>Fecha de Salida *</label>
                <input
                  type="date"
                  value={reservaData.fechaSalida}
                  onChange={(e) => setReservaData({ ...reservaData, fechaSalida: e.target.value })}
                  min={reservaData.fechaEntrada || new Date().toISOString().split('T')[0]}
                  required
                />
              </div>
            </div>

            {reservaData.fechaEntrada && reservaData.fechaSalida && (
              <div className="price-calculation">
                <div className="calc-row">
                  <span>Número de noches:</span>
                  <strong>
                    {Math.ceil((new Date(reservaData.fechaSalida) - new Date(reservaData.fechaEntrada)) / (1000 * 60 * 60 * 24))}
                  </strong>
                </div>
                <div className="calc-row total">
                  <span>Total a pagar:</span>
                  <strong>${calcularPrecioTotal().toFixed(2)}</strong>
                </div>
              </div>
            )}

            <div style={{ textAlign: 'center', marginTop: '30px' }}>
              <button
                type="button"
                onClick={() => setCurrentStep(2)}
                className="btn-secondary"
                style={{ marginRight: '10px' }}
              >
                Atrás
              </button>
              <button type="submit" className="btn-primary">
                Continuar al Pago
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Step 4: Pago */}
      {currentStep === 4 && (
        <div className="wizard-content">
          <div className="section-title">
            <h2>Procesamiento de Pago</h2>
            <div className="divider"></div>
            <p>Complete los datos de su tarjeta para finalizar la reserva</p>
          </div>

          <div className="reservation-summary">
            <h3>Resumen Final</h3>
            <div className="summary-item">
              <span>Huésped:</span>
              <strong>{huespedData.nombre} {huespedData.apellido}</strong>
            </div>
            <div className="summary-item">
              <span>Habitación:</span>
              <strong>{habitacionSeleccionada.tipo} - #{habitacionSeleccionada.numero}</strong>
            </div>
            <div className="summary-item">
              <span>Check-in:</span>
              <strong>{new Date(reservaData.fechaEntrada).toLocaleDateString('es-ES')}</strong>
            </div>
            <div className="summary-item">
              <span>Check-out:</span>
              <strong>{new Date(reservaData.fechaSalida).toLocaleDateString('es-ES')}</strong>
            </div>
            <div className="summary-item total">
              <span>Total a pagar:</span>
              <strong>${reservaData.precioTotal.toFixed(2)}</strong>
            </div>
          </div>

          <PagoSimulador
            monto={reservaData.precioTotal}
            onPagoCompletado={handlePagoCompletado}
            onCancelar={() => setCurrentStep(3)}
            loading={loading}
          />
        </div>
      )}

      {/* Step 5: Confirmación */}
      {currentStep === 5 && (
        <div className="wizard-content">
          <div className="confirmation-container">
            <div className="success-icon">✓</div>
            <h2>¡Reserva Confirmada!</h2>
            <p className="confirmation-message">
              Su reserva ha sido procesada exitosamente. Recibirá un correo de confirmación en {huespedData.email}
            </p>

            <div className="confirmation-details">
              <h3>Detalles de su Reserva</h3>
              <div className="detail-item">
                <span>Número de Reserva:</span>
                <strong>#{reservaId}</strong>
              </div>
              <div className="detail-item">
                <span>Huésped:</span>
                <strong>{huespedData.nombre} {huespedData.apellido}</strong>
              </div>
              <div className="detail-item">
                <span>Habitación:</span>
                <strong>{habitacionSeleccionada.tipo} - #{habitacionSeleccionada.numero}</strong>
              </div>
              <div className="detail-item">
                <span>Fecha de Entrada:</span>
                <strong>{new Date(reservaData.fechaEntrada).toLocaleDateString('es-ES', { 
                  weekday: 'long', 
                  year: 'numeric', 
                  month: 'long', 
                  day: 'numeric' 
                })}</strong>
              </div>
              <div className="detail-item">
                <span>Fecha de Salida:</span>
                <strong>{new Date(reservaData.fechaSalida).toLocaleDateString('es-ES', { 
                  weekday: 'long', 
                  year: 'numeric', 
                  month: 'long', 
                  day: 'numeric' 
                })}</strong>
              </div>
              <div className="detail-item">
                <span>Total Pagado:</span>
                <strong>${reservaData.precioTotal.toFixed(2)}</strong>
              </div>
            </div>

            <div style={{ textAlign: 'center', marginTop: '40px' }}>
              <button onClick={() => navigate('/reservas')} className="btn-primary" style={{ marginRight: '10px' }}>
                Ver Mis Reservas
              </button>
              <button onClick={() => navigate('/')} className="btn-secondary">
                Volver al Inicio
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ReservaFlow;
