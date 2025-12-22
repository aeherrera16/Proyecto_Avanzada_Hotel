import React, { useState, useEffect } from 'react';
import { huespedesService } from '../services/api';
import { Link } from 'react-router-dom';
import Notification from '../components/Notification';
import ConfirmDialog from '../components/ConfirmDialog';

function Huespedes() {
  const [huespedes, setHuespedes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [notification, setNotification] = useState({ message: '', type: '' });
  const [confirmDialog, setConfirmDialog] = useState({ isOpen: false, huespedId: null });

  useEffect(() => {
    fetchHuespedes();
  }, []);

  const fetchHuespedes = async () => {
    try {
      setLoading(true);
      const response = await huespedesService.getAll();
      setHuespedes(response.data);
      setError(null);
    } catch (err) {
      setError('Error al cargar los huéspedes. Por favor, intente nuevamente.');
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
      console.error('Error:', err);
      const errorMessage = err.response?.data?.message ||
        'No se puede eliminar el huésped porque tiene reservas asociadas. Por favor, elimine primero las reservas.';
      showNotification(errorMessage, 'error');
    }
  };

  const handleDeleteCancel = () => {
    setConfirmDialog({ isOpen: false, huespedId: null });
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
        message="¿Está seguro de que desea eliminar este huésped? Esta acción no se puede deshacer."
        onConfirm={handleDeleteConfirm}
        onCancel={handleDeleteCancel}
      />

      <div className="section-title">
        <h2>Gestión de Huéspedes</h2>
        <div className="divider"></div>
        <p>Administre la información de nuestros distinguidos huéspedes</p>
      </div>

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
              <p><strong>Identificación:</strong> {huesped.cedula}</p>
              <p><strong>Email:</strong> {huesped.email}</p>
              <p><strong>Teléfono:</strong> {huesped.telefono}</p>
              {huesped.nacionalidad && <p><strong>Nacionalidad:</strong> {huesped.nacionalidad}</p>}
              <div className="action-buttons">
                <Link to={`/huespedes/editar/${huesped.id}`} className="btn-secondary">
                  Editar
                </Link>
                <button
                  onClick={() => handleDeleteClick(huesped.id)}
                  className="btn-danger"
                >
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

