import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { huespedesService } from '../services/api';

function HuespedForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEdit = Boolean(id);

  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    cedula: '',
    email: '',
    telefono: '',
    nacionalidad: ''
  });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isEdit) {
      fetchHuesped();
    }
  }, [id]);

  const fetchHuesped = async () => {
    try {
      const response = await huespedesService.getById(id);
      setFormData(response.data);
    } catch (err) {
      setError('Error al cargar el huésped');
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
        await huespedesService.update(id, formData);
      } else {
        await huespedesService.create(formData);
      }
      navigate('/huespedes');
    } catch (err) {
      setError(err.response?.data?.message || 'Error al guardar el huésped');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="section-title">
        <h2>{isEdit ? 'Editar Huésped' : 'Nuevo Huésped'}</h2>
        <div className="divider"></div>
      </div>

      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit} className="form-container">
        <div className="form-group">
          <label htmlFor="nombre">Nombre *</label>
          <input
            type="text"
            id="nombre"
            name="nombre"
            value={formData.nombre}
            onChange={handleChange}
            required
            placeholder="Ingrese el nombre"
          />
        </div>

        <div className="form-group">
          <label htmlFor="apellido">Apellido *</label>
          <input
            type="text"
            id="apellido"
            name="apellido"
            value={formData.apellido}
            onChange={handleChange}
            required
            placeholder="Ingrese el apellido"
          />
        </div>

        <div className="form-group">
          <label htmlFor="cedula">Identificación *</label>
          <input
            type="text"
            id="cedula"
            name="cedula"
            value={formData.cedula}
            onChange={handleChange}
            required
            placeholder="Cédula o pasaporte (10-13 caracteres)"
            minLength="10"
            maxLength="13"
          />
        </div>

        <div className="form-group">
          <label htmlFor="email">Email *</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            placeholder="correo@ejemplo.com"
          />
        </div>

        <div className="form-group">
          <label htmlFor="telefono">Teléfono *</label>
          <input
            type="tel"
            id="telefono"
            name="telefono"
            value={formData.telefono}
            onChange={(e) => {
              const value = e.target.value.replace(/\D/g, '');
              if (value.length <= 10) {
                setFormData({ ...formData, telefono: value });
              }
            }}
            required
            placeholder="0999999999 (10 dígitos)"
            pattern="[0-9]{10}"
            title="El teléfono debe tener exactamente 10 dígitos"
          />
        </div>

        <div className="form-group">
          <label htmlFor="nacionalidad">Nacionalidad *</label>
          <input
            type="text"
            id="nacionalidad"
            name="nacionalidad"
            value={formData.nacionalidad}
            onChange={handleChange}
            required
            placeholder="Ej: Ecuatoriana"
          />
        </div>

        <button type="submit" className="btn-submit" disabled={loading}>
          {loading ? 'Guardando...' : isEdit ? 'Actualizar Huésped' : 'Registrar Huésped'}
        </button>

        <button
          type="button"
          onClick={() => navigate('/huespedes')}
          className="btn-secondary"
          style={{ width: '100%', marginTop: '10px' }}
        >
          Cancelar
        </button>
      </form>
    </div>
  );
}

export default HuespedForm;

