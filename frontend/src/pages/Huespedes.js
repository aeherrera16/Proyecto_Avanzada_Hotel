import React, { useState, useEffect } from 'react';
import { huespedesService } from '../services/api';
import { Link } from 'react-router-dom';
import Notification from '../components/Notification';
import ConfirmDialog from '../components/ConfirmDialog';
import ErrorModal from '../components/ErrorModal';
import SearchBar from '../components/SearchBar';
import '../styles/SearchBar.css';

function Huespedes() {
  const [huespedes, setHuespedes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [notification, setNotification] = useState({ message: '', type: '' });
  const [confirmDialog, setConfirmDialog] = useState({ isOpen: false, huespedId: null });
  const [errorModal, setErrorModal] = useState({ isOpen: false, title: '', message: '', details: '' });
  const [searchResult, setSearchResult] = useState(null);

  useEffect(() => {
    fetchHuespedes();
  }, []);

  const fetchHuespedes = async () => {
    try {
      setLoading(true);
      const response = await huespedesService.getAll();
      const huespedesValidos = response.data.filter(h => h.id !== -1);
      setHuespedes(huespedesValidos);
      setError(null);
    } catch (err) {
      setError('Error al cargar los huéspedes.');
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
    setConfirmDialog({ isOpen: true, huespedId: id });
  };

  const handleDeleteConfirm = async () => {
    const id = confirmDialog.huespedId;
    setConfirmDialog({ isOpen: false, huespedId: null });

    try {
      await huespedesService.delete(id);
      showNotification('Huésped eliminado exitosamente', 'success');
      fetchHuespedes();
    } catch (err) {
      const errorMessage = err.response?.data?.message || err.message;
      if (errorMessage.toLowerCase().includes('reserva')) {
        showErrorModal('No se puede eliminar', 'Este huésped tiene reservas asociadas.',
          'Elimine primero las reservas asociadas.');
      } else {
        showErrorModal('Error', 'No se pudo eliminar el huésped.', errorMessage);
      }
    }
  };

  const handleDeleteCancel = () => {
    setConfirmDialog({ isOpen: false, huespedId: null });
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
        <div className="loading">Cargando huéspedes...</div>
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
        message="¿Está seguro de que desea eliminar este huésped?"
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
        <h2>Gestión de Huéspedes</h2>
        <div className="divider"></div>
        <p>Administre la información de nuestros distinguidos huéspedes</p>
      </div>

      {/* Barra de búsqueda */}
      <SearchBar
        entityName="Huésped"
        searchFunctionConRecuperacion={huespedesService.getByIdConRecuperacion}
        searchFunctionSinRecuperacion={huespedesService.getByIdSinRecuperacion}
        onResult={handleSearchResult}
        placeholder="Buscar huésped por ID..."
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
              <h4>{searchResult.data.nombre} {searchResult.data.apellido}</h4>
              <p>Cédula: {searchResult.data.cedula} — Email: {searchResult.data.email}</p>
            </>
          )}

          {searchResult.recovered && (
            <>
              <h4>Huésped no encontrado</h4>
              <p>No existe un huésped con ese ID en la base de datos.</p>
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
        <Link to="/huespedes/nuevo" className="btn-primary">
          + Registrar Nuevo Huésped
        </Link>
      </div>

      <div className="cards-grid">
        {huespedes.map((huesped) => (
          <div key={huesped.id} className="card">
            <div className="card-content">
              <h3>{huesped.nombre} {huesped.apellido}</h3>
              <p><strong>ID:</strong> {huesped.id}</p>
              <p><strong>Identificación:</strong> {huesped.cedula}</p>
              <p><strong>Email:</strong> {huesped.email}</p>
              <p><strong>Teléfono:</strong> {huesped.telefono}</p>
              {huesped.nacionalidad && <p><strong>Nacionalidad:</strong> {huesped.nacionalidad}</p>}
              <div className="action-buttons">
                <Link to={`/huespedes/editar/${huesped.id}`} className="btn-secondary">
                  Editar
                </Link>
                <button onClick={() => handleDeleteClick(huesped.id)} className="btn-danger">
                  Eliminar
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {huespedes.length === 0 && !loading && (
        <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
          <p>No hay huéspedes registrados. ¡Agregue el primero!</p>
        </div>
      )}
    </div>
  );
}

export default Huespedes;
