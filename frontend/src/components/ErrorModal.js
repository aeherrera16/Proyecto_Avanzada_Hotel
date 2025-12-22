import React from 'react';
import '../styles/ErrorModal.css';

/**
 * Modal de error para mostrar mensajes de error de forma prominente.
 * Útil para errores como habitaciones duplicadas, validaciones fallidas, etc.
 */
function ErrorModal({ isOpen, title, message, details, onClose }) {
  if (!isOpen) return null;

  return (
    <div className="error-modal-overlay" onClick={onClose}>
      <div className="error-modal" onClick={(e) => e.stopPropagation()}>
        {/* Header con icono de error */}
        <div className="error-modal-header">
          <div className="error-modal-icon">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
            </svg>
          </div>
          <h3 className="error-modal-title">{title || 'Error'}</h3>
        </div>

        {/* Cuerpo del modal */}
        <div className="error-modal-body">
          <p className="error-modal-message">{message}</p>
          {details && (
            <div className="error-modal-details">
              <span className="error-modal-details-label">Detalles:</span>
              <p className="error-modal-details-text">{details}</p>
            </div>
          )}
        </div>

        {/* Footer con botón de cerrar */}
        <div className="error-modal-footer">
          <button className="error-modal-btn" onClick={onClose}>
            Entendido
          </button>
        </div>
      </div>
    </div>
  );
}

export default ErrorModal;
