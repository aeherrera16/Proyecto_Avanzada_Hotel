import React, { useState, useEffect } from 'react';
import { habitacionesService } from '../services/api';
import { Link } from 'react-router-dom';

function Habitaciones() {
  const [habitaciones, setHabitaciones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchHabitaciones();
  }, []);

  const fetchHabitaciones = async () => {
    try {
      setLoading(true);
      const response = await habitacionesService.getAll();
      setHabitaciones(response.data);
      setError(null);
    } catch (err) {
      setError('Error al cargar las habitaciones. Por favor, intente nuevamente.');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de que desea eliminar esta habitación?')) {
      try {
        await habitacionesService.delete(id);
        fetchHabitaciones();
      } catch (err) {
        alert('Error al eliminar la habitación');
        console.error('Error:', err);
      }
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

  if (loading) {
    return (
      <div className="container">
        <div className="loading">Cargando habitaciones...</div>
      </div>
    );
  }

  return (
    <div className="container">
      <div className="section-title">
        <h2>Nuestras Habitaciones</h2>
        <div className="divider"></div>
        <p>Descubra nuestras elegantes habitaciones diseñadas para su máximo confort</p>
      </div>

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
              <p><strong>Tipo:</strong> {habitacion.tipo}</p>
              <div className="card-price">${habitacion.precio} / noche</div>
              <span className={`card-badge ${habitacion.estado.toLowerCase()}`}>
                {habitacion.estado}
              </span>
              <div className="action-buttons">
                <Link to={`/habitaciones/editar/${habitacion.id}`} className="btn-secondary">
                  Editar
                </Link>
                <button
                  onClick={() => handleDelete(habitacion.id)}
                  className="btn-danger"
                >
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

