import React, { useState, useEffect } from 'react';
import { habitacionesService } from '../services/api';
import { Link } from 'react-router-dom';
import Notification from '../components/Notification';
import ConfirmDialog from '../components/ConfirmDialog';
import ErrorModal from '../components/ErrorModal';
import SearchBar from '../components/SearchBar';
import '../styles/SearchBar.css';

function Habitaciones() {
  const [habitaciones, setHabitaciones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [notification, setNotification] = useState({ message: '', type: '' });
  const [confirmDialog, setConfirmDialog] = useState({ isOpen: false, habitacionId: null });
  const [errorModal, setErrorModal] = useState({ isOpen: false, title: '', message: '', details: '' });
  const [searchResult, setSearchResult] = useState(null);

  useEffect(() => {
    fetchHabitaciones();
  }, []);

  const fetchHabitaciones = async () => {
    try {
      setLoading(true);
      const response = await habitacionesService.getAll();
      const habitacionesValidas = response.data.filter(h => h.id !== -1);
      setHabitaciones(habitacionesValidas);
      setError(null);
    } catch (err) {
      setError('Error al cargar las habitaciones.');
    } finally {
      setLoading(false);
    }
  };

  const showNotification = (message, type) => {
    setNotification({ message, type });
    setTimeout(() => setNotification({ message: '', type: '' }), 5000);
  };

  const showErrorModal = (title, message, details = '') => {
    setErrorModal({ isOpen: true, title, message, details });
  };

  const closeErrorModal = () => {
    setErrorModal({ isOpen: false, title: '', message: '', details: '' });
  };

  const handleSearchResult = (result) => {
    setSearchResult(result);
  };

  const handleDeleteClick = (id) => {
    setConfirmDialog({ isOpen: true, habitacionId: id });
  };

  const handleDeleteConfirm = async () => {
    const id = confirmDialog.habitacionId;
    setConfirmDialog({ isOpen: false, habitacionId: null });

    try {
      await habitacionesService.delete(id);
      showNotification('Habitación eliminada exitosamente', 'success');
      fetchHabitaciones();
    } catch (err) {
      const errorMessage = err.response?.data?.message || err.message;
      if (errorMessage.toLowerCase().includes('reserva')) {
        showErrorModal('No se puede eliminar', 'Esta habitación tiene reservas asociadas.',
          'Elimine primero las reservas asociadas.');
      } else {
        showErrorModal('Error', 'No se pudo eliminar la habitación.', errorMessage);
      }
    }
  };

  const handleDeleteCancel = () => {
    setConfirmDialog({ isOpen: false, habitacionId: null });
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

  const getResultClass = () => {
    if (!searchResult) return '';
    if (searchResult.error) return 'error';
    if (searchResult.recovered) return 'recovered';
    return 'found';
  };

  if (loading) {
    return (
      <div className="container">
        <div className="loading">Cargando habitaciones...</div>
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
        title="Confirmar eliminación"
        message="¿Está seguro de que desea eliminar esta habitación?"
        onConfirm={handleDeleteConfirm}
        onCancel={handleDeleteCancel}
      />

      <ErrorModal
        isOpen={errorModal.isOpen}
        title={errorModal.title}
        message={errorModal.message}
        details={errorModal.details}
        onClose={closeErrorModal}
      />

      <div className="section-title">
        <h2>Nuestras Habitaciones</h2>
        <div className="divider"></div>
        <p>Descubra nuestras elegantes habitaciones diseñadas para su máximo confort</p>
      </div>

      {/* Barra de búsqueda */}
      <SearchBar
        entityName="Habitación"
        searchFunctionConRecuperacion={habitacionesService.getByIdConRecuperacion}
        searchFunctionSinRecuperacion={habitacionesService.getByIdSinRecuperacion}
        onResult={handleSearchResult}
        placeholder="Buscar habitación por ID..."
      />

      {/* Resultado de búsqueda */}
      {searchResult && (
        <div className={`search-result ${getResultClass()}`}>
          <div className="search-result-header">
            <span className="search-result-badge">
              {searchResult.error ? 'Error' : searchResult.recovered ? 'Recuperado' : 'Encontrado'}
            </span>
            <span style={{ color: '#888', fontSize: '0.85rem' }}>
              Modo: {searchResult.mode === 'con' ? 'Con Recuperación' : 'Sin Recuperación'}
            </span>
          </div>

          {searchResult.found && (
            <>
              <h4>Habitación #{searchResult.data.numero}</h4>
              <p>{searchResult.data.tipo} — ${searchResult.data.precio}/noche — {searchResult.data.estado}</p>
            </>
          )}

          {searchResult.recovered && (
            <>
              <h4>Habitación no encontrada</h4>
              <p>No existe una habitación con ese ID en la base de datos.</p>
              <div className="recovered-message">
                <strong>onErrorResume activo:</strong> El flujo continuó y retornó un valor por defecto en lugar de fallar.
              </div>
            </>
          )}

          {searchResult.error && (
            <>
              <h4>Error en la búsqueda</h4>
              <div className="error-message-box">
                <strong>Sin recuperación:</strong> {searchResult.error}
              </div>
            </>
          )}

          <button className="close-btn" onClick={() => setSearchResult(null)}>Cerrar</button>
        </div>
      )}

      {error && <div className="error-message">{error}</div>}

      <div style={{ marginBottom: '30px', textAlign: 'center' }}>
        <Link to="/habitaciones/nueva" className="btn-primary">
          + Agregar Nueva Habitación
        </Link>
      </div>

      <div className="cards-grid">
        {habitaciones.map((habitacion) => (
          <div key={habitacion.id} className="card">
            <img
              src={getImageByTipo(habitacion.tipo)}
              alt={habitacion.tipo}
              className="card-image"
            />
            <div className="card-content">
              <h3>Habitación {habitacion.numero}</h3>
              <p><strong>ID:</strong> {habitacion.id}</p>
              <p><strong>Tipo:</strong> {habitacion.tipo}</p>
              <div className="card-price">${habitacion.precio} / noche</div>
              <span className={`card-badge ${habitacion.estado ? habitacion.estado.toLowerCase() : 'disponible'}`}>
                {habitacion.estado || 'Disponible'}
              </span>
              <div className="action-buttons">
                <Link to={`/habitaciones/editar/${habitacion.id}`} className="btn-secondary">
                  Editar
                </Link>
                <button onClick={() => handleDeleteClick(habitacion.id)} className="btn-danger">
                  Eliminar
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {habitaciones.length === 0 && !loading && (
        <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
          <p>No hay habitaciones registradas. ¡Agregue la primera!</p>
        </div>
      )}
    </div>
  );
}

export default Habitaciones;
