import React, { useState, useEffect } from 'react';
import { reservasService, habitacionesService } from '../services/api';
import { useNavigate } from 'react-router-dom';
import Notification from '../components/Notification';
import ConfirmDialog from '../components/ConfirmDialog';

function Reservas() {
  const navigate = useNavigate();
  const [reservas, setReservas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filtroEstado, setFiltroEstado] = useState('Todas');
  const [reservaSeleccionada, setReservaSeleccionada] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [notification, setNotification] = useState({ message: '', type: '' });
  const [confirmDialog, setConfirmDialog] = useState({ isOpen: false, reservaId: null, habitacionId: null });
  const [deleteDialog, setDeleteDialog] = useState({ isOpen: false, reservaId: null, habitacionId: null });

  useEffect(() => {
    fetchReservas();
  }, []);

  const fetchReservas = async () => {
    try {
      setLoading(true);
      const response = await reservasService.getAll();
      // Ordenar por fecha de creación, más recientes primero
      const ordenadas = response.data.sort((a, b) => 
        new Date(b.fechaCreacion) - new Date(a.fechaCreacion)
      );
      setReservas(ordenadas);
      setError(null);
    } catch (err) {
      setError('Error al cargar las reservas. Por favor, intente nuevamente.');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  const showNotification = (message, type) => {
    setNotification({ message, type });
    setTimeout(() => {
      setNotification({ message: '', type: '' });
    }, 5000);
  };

  const handleCancelarReservaClick = (reservaId, habitacionId) => {
    setConfirmDialog({ isOpen: true, reservaId, habitacionId });
  };

  const handleCancelarReservaConfirm = async () => {
    const { reservaId, habitacionId } = confirmDialog;
    setConfirmDialog({ isOpen: false, reservaId: null, habitacionId: null });

    try {
      // Actualizar estado de la reserva
      const reserva = reservas.find(r => r.id === reservaId);
      await reservasService.update(reservaId, {
        ...reserva,
        estado: 'Cancelada'
      });

      // Liberar la habitación
      const habitacionResponse = await habitacionesService.getById(habitacionId);
      await habitacionesService.update(habitacionId, {
        ...habitacionResponse.data,
        estado: 'Disponible'
      });

      showNotification('Reserva cancelada exitosamente', 'success');
      fetchReservas();
    } catch (err) {
      console.error('Error:', err);
      const errorMessage = err.response?.data?.message || 'Error al cancelar la reserva';
      showNotification(errorMessage, 'error');
    }
  };

  const handleCancelarReservaCancel = () => {
    setConfirmDialog({ isOpen: false, reservaId: null, habitacionId: null });
  };

  const handleConfirmarReserva = async (reservaId) => {
    try {
      const reserva = reservas.find(r => r.id === reservaId);
      await reservasService.update(reservaId, {
        ...reserva,
        estado: 'Confirmada'
      });
      showNotification('Reserva confirmada exitosamente', 'success');
      fetchReservas();
    } catch (err) {
      console.error('Error:', err);
      const errorMessage = err.response?.data?.message || 'Error al confirmar la reserva';
      showNotification(errorMessage, 'error');
    }
  };

  const handleEliminarReservaClick = (reservaId, habitacionId) => {
    setDeleteDialog({ isOpen: true, reservaId, habitacionId });
  };

  const handleEliminarReservaConfirm = async () => {
    const { reservaId, habitacionId } = deleteDialog;
    setDeleteDialog({ isOpen: false, reservaId: null, habitacionId: null });

    try {
      // Liberar la habitación antes de eliminar la reserva
      if (habitacionId) {
        try {
          const habitacionResponse = await habitacionesService.getById(habitacionId);
          await habitacionesService.update(habitacionId, {
            ...habitacionResponse.data,
            estado: 'Disponible'
          });
        } catch (err) {
          console.error('Error al liberar habitación:', err);
        }
      }

      // Eliminar la reserva
      await reservasService.delete(reservaId);
      showNotification('Reserva eliminada exitosamente', 'success');
      fetchReservas();
    } catch (err) {
      console.error('Error:', err);
      const errorMessage = err.response?.data?.message || 'Error al eliminar la reserva';
      showNotification(errorMessage, 'error');
    }
  };

  const handleEliminarReservaCancel = () => {
    setDeleteDialog({ isOpen: false, reservaId: null, habitacionId: null });
  };

  const verDetalles = (reserva) => {
    setReservaSeleccionada(reserva);
    setShowModal(true);
  };

  const getEstadoBadgeClass = (estado) => {
    const classes = {
      'Confirmada': 'badge-confirmada',
      'Pendiente': 'badge-pendiente',
      'Cancelada': 'badge-cancelada',
      'Completada': 'badge-completada'
    };
    return classes[estado] || 'badge-default';
  };

  const getEstadoIcon = (estado) => {
    const icons = {
      'Confirmada': '✓',
      'Pendiente': '⏱',
      'Cancelada': '✗',
      'Completada': '★'
    };
    return icons[estado] || '•';
  };

  const calcularNoches = (fechaEntrada, fechaSalida) => {
    const entrada = new Date(fechaEntrada);
    const salida = new Date(fechaSalida);
    return Math.ceil((salida - entrada) / (1000 * 60 * 60 * 24));
  };

  const reservasFiltradas = filtroEstado === 'Todas' 
    ? reservas 
    : reservas.filter(r => r.estado === filtroEstado);

  if (loading) {
    return (
      <div className="container">
        <div className="loading">Cargando reservas...</div>
      </div>
    );
  }

  return (
    <div className="container">
      <Notification
        message={notification.message}
        type={notification.type}
        onClose={() => setNotification({ message: '', type: '' })}
      />

      <ConfirmDialog
        isOpen={confirmDialog.isOpen}
        title="Confirmar cancelación"
        message="¿Está seguro de que desea cancelar esta reserva? La habitación será liberada pero la reserva permanecerá en el sistema con estado 'Cancelada'."
        onConfirm={handleCancelarReservaConfirm}
        onCancel={handleCancelarReservaCancel}
      />

      <ConfirmDialog
        isOpen={deleteDialog.isOpen}
        title="Confirmar eliminación"
        message="¿Está seguro de que desea ELIMINAR esta reserva? Esta acción es PERMANENTE y no se puede deshacer. La reserva será eliminada completamente del sistema."
        onConfirm={handleEliminarReservaConfirm}
        onCancel={handleEliminarReservaCancel}
      />

      <div className="section-title">
        <h2>Gestión de Reservas</h2>
        <div className="divider"></div>
        <p>Administre y supervise todas las reservas del hotel</p>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="reservas-header">
        <button onClick={() => navigate('/reservas/nueva')} className="btn-primary">
          + Nueva Reserva
        </button>

        <div className="filter-buttons">
          {['Todas', 'Confirmada', 'Pendiente', 'Cancelada', 'Completada'].map(estado => (
            <button
              key={estado}
              onClick={() => setFiltroEstado(estado)}
              className={`filter-btn ${filtroEstado === estado ? 'active' : ''}`}
            >
              {estado}
              <span className="filter-count">
                {estado === 'Todas' ? reservas.length : reservas.filter(r => r.estado === estado).length}
              </span>
            </button>
          ))}
        </div>
      </div>

      <div className="reservas-stats">
        <div className="stat-card">
          <div className="stat-number">{reservas.filter(r => r.estado === 'Confirmada').length}</div>
          <div className="stat-label">Confirmadas</div>
        </div>
        <div className="stat-card">
          <div className="stat-number">{reservas.filter(r => r.estado === 'Pendiente').length}</div>
          <div className="stat-label">Pendientes</div>
        </div>
        <div className="stat-card">
          <div className="stat-number">
            ${reservas.reduce((sum, r) => sum + (r.precioTotal || 0), 0).toFixed(2)}
          </div>
          <div className="stat-label">Total Ingresos</div>
        </div>
      </div>

      <div className="reservas-timeline">
        {reservasFiltradas.length === 0 ? (
          <div className="no-reservas">
            <p>No hay reservas {filtroEstado !== 'Todas' ? `con estado "${filtroEstado}"` : 'registradas'}.</p>
          </div>
        ) : (
          reservasFiltradas.map((reserva) => (
            <div key={reserva.id} className={`reserva-card ${reserva.estado.toLowerCase()}`}>
              <div className="reserva-header">
                <div className="reserva-id">Reserva #{reserva.id}</div>
                <span className={`reserva-badge ${getEstadoBadgeClass(reserva.estado)}`}>
                  {getEstadoIcon(reserva.estado)} {reserva.estado}
                </span>
              </div>

              <div className="reserva-body">
                <div className="reserva-info">
                  <div className="info-row">
                    <span className="info-icon">👤</span>
                    <div className="info-content">
                      <div className="info-label">Huésped</div>
                      <div className="info-value">
                        {reserva.huesped?.nombre} {reserva.huesped?.apellido}
                      </div>
                    </div>
                  </div>

                  <div className="info-row">
                    <span className="info-icon">🏨</span>
                    <div className="info-content">
                      <div className="info-label">Habitación</div>
                      <div className="info-value">
                        {reserva.habitacion?.tipo} - #{reserva.habitacion?.numero}
                      </div>
                    </div>
                  </div>

                  <div className="info-row">
                    <span className="info-icon">📅</span>
                    <div className="info-content">
                      <div className="info-label">Estadía</div>
                      <div className="info-value">
                        {new Date(reserva.fechaEntrada).toLocaleDateString('es-ES')} - 
                        {new Date(reserva.fechaSalida).toLocaleDateString('es-ES')}
                        <span className="noches-badge">
                          {calcularNoches(reserva.fechaEntrada, reserva.fechaSalida)} noches
                        </span>
                      </div>
                    </div>
                  </div>

                  <div className="info-row">
                    <span className="info-icon">💰</span>
                    <div className="info-content">
                      <div className="info-label">Total</div>
                      <div className="info-value price">${reserva.precioTotal?.toFixed(2)}</div>
                    </div>
                  </div>
                </div>

                <div className="reserva-actions">
                  <button onClick={() => verDetalles(reserva)} className="btn-details">
                    Ver Detalles
                  </button>
                  
                  {reserva.estado === 'Pendiente' && (
                    <button 
                      onClick={() => handleConfirmarReserva(reserva.id)} 
                      className="btn-confirm"
                    >
                      Confirmar
                    </button>
                  )}
                  
                  {(reserva.estado === 'Confirmada' || reserva.estado === 'Pendiente') && (
                    <button 
                      onClick={() => handleCancelarReservaClick(reserva.id, reserva.habitacion?.id)}
                      className="btn-cancel"
                    >
                      Cancelar
                    </button>
                  )}

                  {(reserva.estado === 'Cancelada' || reserva.estado === 'Completada') && (
                    <button
                      onClick={() => handleEliminarReservaClick(reserva.id, reserva.habitacion?.id)}
                      className="btn-danger"
                    >
                      Eliminar
                    </button>
                  )}
                </div>
              </div>

              <div className="reserva-footer">
                <small>Creada: {new Date(reserva.fechaCreacion).toLocaleString('es-ES')}</small>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Modal de Detalles */}
      {showModal && reservaSeleccionada && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Detalles de Reserva #{reservaSeleccionada.id}</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>✕</button>
            </div>
            
            <div className="modal-body">
              <div className="detail-section">
                <h4>Información del Huésped</h4>
                <p><strong>Nombre:</strong> {reservaSeleccionada.huesped?.nombre} {reservaSeleccionada.huesped?.apellido}</p>
                <p><strong>Email:</strong> {reservaSeleccionada.huesped?.email}</p>
                <p><strong>Teléfono:</strong> {reservaSeleccionada.huesped?.telefono}</p>
                <p><strong>Identificación:</strong> {reservaSeleccionada.huesped?.cedula}</p>
                {reservaSeleccionada.huesped?.nacionalidad && (
                  <p><strong>Nacionalidad:</strong> {reservaSeleccionada.huesped?.nacionalidad}</p>
                )}
              </div>

              <div className="detail-section">
                <h4>Información de la Habitación</h4>
                <p><strong>Tipo:</strong> {reservaSeleccionada.habitacion?.tipo}</p>
                <p><strong>Número:</strong> {reservaSeleccionada.habitacion?.numero}</p>
                <p><strong>Precio por noche:</strong> ${reservaSeleccionada.habitacion?.precio}</p>
              </div>

              <div className="detail-section">
                <h4>Detalles de la Reserva</h4>
                <p><strong>Check-in:</strong> {new Date(reservaSeleccionada.fechaEntrada).toLocaleDateString('es-ES', { 
                  weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' 
                })}</p>
                <p><strong>Check-out:</strong> {new Date(reservaSeleccionada.fechaSalida).toLocaleDateString('es-ES', { 
                  weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' 
                })}</p>
                <p><strong>Número de noches:</strong> {calcularNoches(reservaSeleccionada.fechaEntrada, reservaSeleccionada.fechaSalida)}</p>
                <p><strong>Total:</strong> <span style={{ fontSize: '1.2rem', color: '#d4af37' }}>${reservaSeleccionada.precioTotal?.toFixed(2)}</span></p>
                <p><strong>Estado:</strong> <span className={`reserva-badge ${getEstadoBadgeClass(reservaSeleccionada.estado)}`}>
                  {reservaSeleccionada.estado}
                </span></p>
              </div>

              <div className="detail-section">
                <h4>Información del Sistema</h4>
                <p><strong>Fecha de creación:</strong> {new Date(reservaSeleccionada.fechaCreacion).toLocaleString('es-ES')}</p>
                {reservaSeleccionada.fechaActualizacion && (
                  <p><strong>Última actualización:</strong> {new Date(reservaSeleccionada.fechaActualizacion).toLocaleString('es-ES')}</p>
                )}
              </div>
            </div>
            
            <div className="modal-footer">
              <button onClick={() => setShowModal(false)} className="btn-primary">
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Reservas;
