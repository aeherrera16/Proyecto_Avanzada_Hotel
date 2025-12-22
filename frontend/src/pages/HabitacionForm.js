import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { habitacionesService } from '../services/api';
import ErrorModal from '../components/ErrorModal';

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
  const [loading, setLoading] = useState(false);

  // Estado para el modal de error
  const [errorModal, setErrorModal] = useState({
    isOpen: false,
    title: '',
    message: '',
    details: ''
  });

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
      showErrorModal(
        'Error al cargar',
        'No se pudo cargar la información de la habitación.',
        err.response?.data?.message || err.message
      );
      console.error('Error:', err);
    }
  };

  // Función para mostrar el modal de error
  const showErrorModal = (title, message, details = '') => {
    setErrorModal({
      isOpen: true,
      title,
      message,
      details
    });
  };

  // Función para cerrar el modal
  const closeErrorModal = () => {
    setErrorModal({
      isOpen: false,
      title: '',
      message: '',
      details: ''
    });
  };

  // Función para determinar el tipo de error y mensaje apropiado
  const handleError = (err) => {
    console.error('Error completo:', err);

    const errorMessage = err.response?.data?.message ||
      err.response?.data?.estado ||
      err.message ||
      'Error desconocido';

    // Detectar errores específicos
    if (errorMessage.toLowerCase().includes('duplicate') ||
      errorMessage.toLowerCase().includes('duplicad') ||
      errorMessage.toLowerCase().includes('unique') ||
      errorMessage.toLowerCase().includes('ya existe') ||
      errorMessage.toLowerCase().includes('already exists')) {
      showErrorModal(
        '¡Habitación Duplicada!',
        `El número de habitación "${formData.numero}" ya existe en el sistema.`,
        'Por favor, ingrese un número de habitación diferente.'
      );
    } else if (errorMessage.toLowerCase().includes('validación') ||
      errorMessage.toLowerCase().includes('validation') ||
      errorMessage.toLowerCase().includes('obligatorio') ||
      errorMessage.toLowerCase().includes('required')) {
      showErrorModal(
        'Error de Validación',
        'Los datos ingresados no son válidos.',
        errorMessage
      );
    } else if (err.response?.status === 500 || errorMessage.includes('ERROR:')) {
      // Error recuperado del backend con onErrorResume
      showErrorModal(
        'Error al Guardar',
        'No se pudo guardar la habitación.',
        errorMessage
      );
    } else {
      showErrorModal(
        'Error',
        'Ocurrió un error al procesar la solicitud.',
        errorMessage
      );
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

    try {
      let response;
      if (isEdit) {
        response = await habitacionesService.update(id, formData);
      } else {
        response = await habitacionesService.create(formData);
      }

      // Verificar si la respuesta indica un error recuperado del backend
      // (cuando onErrorResume retorna una habitación con id=-1)
      if (response.data && response.data.id === -1) {
        handleError({
          response: {
            data: { message: response.data.estado || 'Error al guardar' },
            status: 500
          }
        });
        return;
      }

      navigate('/habitaciones');
    } catch (err) {
      handleError(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      {/* Modal de Error */}
      <ErrorModal
        isOpen={errorModal.isOpen}
        title={errorModal.title}
        message={errorModal.message}
        details={errorModal.details}
        onClose={closeErrorModal}
      />

      <div className="section-title">
        <h2>{isEdit ? 'Editar Habitación' : 'Nueva Habitación'}</h2>
        <div className="divider"></div>
      </div>

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
