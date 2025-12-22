import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { habitacionesService } from '../services/api';

function HabitacionForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEdit = Boolean(id);

  const [formData, setFormData] = useState({
    numero: '',
    tipo: 'Simple',
    precio: '',
    estado: 'Disponible'
  });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isEdit) {
      fetchHabitacion();
    }
  }, [id]);

  const fetchHabitacion = async () => {
    try {
      const response = await habitacionesService.getById(id);
      setFormData(response.data);
    } catch (err) {
      setError('Error al cargar la habitación');
      console.error('Error:', err);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      if (isEdit) {
        await habitacionesService.update(id, formData);
      } else {
        await habitacionesService.create(formData);
      }
      navigate('/habitaciones');
    } catch (err) {
      setError(err.response?.data?.message || 'Error al guardar la habitación');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="section-title">
        <h2>{isEdit ? 'Editar Habitación' : 'Nueva Habitación'}</h2>
        <div className="divider"></div>
      </div>

      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit} className="form-container">
        <div className="form-group">
          <label htmlFor="numero">Número de Habitación *</label>
          <input
            type="text"
            id="numero"
            name="numero"
            value={formData.numero}
            onChange={handleChange}
            required
            placeholder="Ej: 101"
          />
        </div>

        <div className="form-group">
          <label htmlFor="tipo">Tipo de Habitación *</label>
          <select
            id="tipo"
            name="tipo"
            value={formData.tipo}
            onChange={handleChange}
            required
          >
            <option value="Simple">Simple</option>
            <option value="Doble">Doble</option>
            <option value="Suite">Suite</option>
            <option value="Suite Presidencial">Suite Presidencial</option>
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="precio">Precio por Noche (USD) *</label>
          <input
            type="number"
            id="precio"
            name="precio"
            value={formData.precio}
            onChange={handleChange}
            required
            step="0.01"
            min="0"
            placeholder="Ej: 150.00"
          />
        </div>

        <div className="form-group">
          <label htmlFor="estado">Estado *</label>
          <select
            id="estado"
            name="estado"
            value={formData.estado}
            onChange={handleChange}
            required
          >
            <option value="Disponible">Disponible</option>
            <option value="Ocupada">Ocupada</option>
            <option value="Mantenimiento">Mantenimiento</option>
          </select>
        </div>

        <button type="submit" className="btn-submit" disabled={loading}>
          {loading ? 'Guardando...' : isEdit ? 'Actualizar Habitación' : 'Crear Habitación'}
        </button>

        <button
          type="button"
          onClick={() => navigate('/habitaciones')}
          className="btn-secondary"
          style={{ width: '100%', marginTop: '10px' }}
        >
          Cancelar
        </button>
      </form>
    </div>
  );
}

export default HabitacionForm;

