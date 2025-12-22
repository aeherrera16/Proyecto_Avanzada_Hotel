import React, { useState } from 'react';

function PagoSimulador({ monto, onPagoCompletado, onCancelar, loading }) {
  const [metodoPago, setMetodoPago] = useState('Tarjeta de Crédito');
  const [processing, setProcessing] = useState(false);
  const [cardData, setCardData] = useState({
    numeroTarjeta: '',
    nombreTitular: '',
    fechaVencimiento: '',
    cvv: ''
  });
  const [errors, setErrors] = useState({});

  const formatCardNumber = (value) => {
    const cleaned = value.replace(/\s/g, '');
    const match = cleaned.match(/.{1,4}/g);
    return match ? match.join(' ') : cleaned;
  };

  const handleCardNumberChange = (e) => {
    const value = e.target.value.replace(/\s/g, '');
    if (value.length <= 16 && /^\d*$/.test(value)) {
      setCardData({ ...cardData, numeroTarjeta: value });
    }
  };

  const handleFechaChange = (e) => {
    let value = e.target.value.replace(/\D/g, '');
    if (value.length >= 2) {
      value = value.slice(0, 2) + '/' + value.slice(2, 4);
    }
    if (value.length <= 5) {
      setCardData({ ...cardData, fechaVencimiento: value });
    }
  };

  const handleCvvChange = (e) => {
    const value = e.target.value;
    if (value.length <= 3 && /^\d*$/.test(value)) {
      setCardData({ ...cardData, cvv: value });
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (cardData.numeroTarjeta.length !== 16) {
      newErrors.numeroTarjeta = 'Número de tarjeta inválido';
    }
    
    if (!cardData.nombreTitular.trim()) {
      newErrors.nombreTitular = 'Nombre del titular es requerido';
    }
    
    const [mes, ano] = cardData.fechaVencimiento.split('/');
    const currentYear = new Date().getFullYear() % 100;
    const currentMonth = new Date().getMonth() + 1;
    
    if (!mes || !ano || parseInt(mes) < 1 || parseInt(mes) > 12) {
      newErrors.fechaVencimiento = 'Fecha inválida';
    } else if (parseInt(ano) < currentYear || (parseInt(ano) === currentYear && parseInt(mes) < currentMonth)) {
      newErrors.fechaVencimiento = 'Tarjeta vencida';
    }
    
    if (cardData.cvv.length !== 3) {
      newErrors.cvv = 'CVV inválido';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setProcessing(true);
    
    // Simular procesamiento de pago (2-3 segundos)
    setTimeout(() => {
      setProcessing(false);
      onPagoCompletado({
        metodoPago: metodoPago,
        numeroTarjeta: `**** **** **** ${cardData.numeroTarjeta.slice(-4)}`,
        nombreTitular: cardData.nombreTitular
      });
    }, 2500);
  };

  const getCardType = () => {
    const firstDigit = cardData.numeroTarjeta[0];
    if (firstDigit === '4') return 'Visa';
    if (firstDigit === '5') return 'Mastercard';
    if (firstDigit === '3') return 'American Express';
    return 'Tarjeta';
  };

  return (
    <div className="payment-simulator">
      <div className="payment-methods">
        <h3>Método de Pago</h3>
        <div className="payment-options">
          <label className={`payment-option ${metodoPago === 'Tarjeta de Crédito' ? 'selected' : ''}`}>
            <input
              type="radio"
              value="Tarjeta de Crédito"
              checked={metodoPago === 'Tarjeta de Crédito'}
              onChange={(e) => setMetodoPago(e.target.value)}
            />
            <span>💳 Tarjeta de Crédito</span>
          </label>
          <label className={`payment-option ${metodoPago === 'Tarjeta de Débito' ? 'selected' : ''}`}>
            <input
              type="radio"
              value="Tarjeta de Débito"
              checked={metodoPago === 'Tarjeta de Débito'}
              onChange={(e) => setMetodoPago(e.target.value)}
            />
            <span>💳 Tarjeta de Débito</span>
          </label>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="payment-form">
        <div className="credit-card-display">
          <div className="card-chip"></div>
          <div className="card-number">
            {cardData.numeroTarjeta ? formatCardNumber(cardData.numeroTarjeta) : '**** **** **** ****'}
          </div>
          <div className="card-info">
            <div className="card-holder">
              <div className="card-label">TITULAR</div>
              <div className="card-value">{cardData.nombreTitular || 'NOMBRE APELLIDO'}</div>
            </div>
            <div className="card-expiry">
              <div className="card-label">VÁLIDA HASTA</div>
              <div className="card-value">{cardData.fechaVencimiento || 'MM/AA'}</div>
            </div>
          </div>
          <div className="card-brand">{getCardType()}</div>
        </div>

        <div className="form-group">
          <label>Número de Tarjeta *</label>
          <input
            type="text"
            value={formatCardNumber(cardData.numeroTarjeta)}
            onChange={handleCardNumberChange}
            placeholder="1234 5678 9012 3456"
            className={errors.numeroTarjeta ? 'error' : ''}
            disabled={processing || loading}
          />
          {errors.numeroTarjeta && <span className="error-text">{errors.numeroTarjeta}</span>}
        </div>

        <div className="form-group">
          <label>Nombre del Titular *</label>
          <input
            type="text"
            value={cardData.nombreTitular}
            onChange={(e) => setCardData({ ...cardData, nombreTitular: e.target.value.toUpperCase() })}
            placeholder="NOMBRE COMO APARECE EN LA TARJETA"
            className={errors.nombreTitular ? 'error' : ''}
            disabled={processing || loading}
          />
          {errors.nombreTitular && <span className="error-text">{errors.nombreTitular}</span>}
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Fecha de Vencimiento *</label>
            <input
              type="text"
              value={cardData.fechaVencimiento}
              onChange={handleFechaChange}
              placeholder="MM/AA"
              className={errors.fechaVencimiento ? 'error' : ''}
              disabled={processing || loading}
            />
            {errors.fechaVencimiento && <span className="error-text">{errors.fechaVencimiento}</span>}
          </div>
          <div className="form-group">
            <label>CVV *</label>
            <input
              type="password"
              value={cardData.cvv}
              onChange={handleCvvChange}
              placeholder="123"
              maxLength="3"
              className={errors.cvv ? 'error' : ''}
              disabled={processing || loading}
            />
            {errors.cvv && <span className="error-text">{errors.cvv}</span>}
          </div>
        </div>

        <div className="payment-total">
          <span>Total a Pagar:</span>
          <strong>${monto.toFixed(2)}</strong>
        </div>

        {processing && (
          <div className="processing-overlay">
            <div className="processing-spinner"></div>
            <p>Procesando pago seguro...</p>
            <p className="processing-detail">Verificando datos con el banco</p>
          </div>
        )}

        <div className="payment-actions">
          <button
            type="button"
            onClick={onCancelar}
            className="btn-secondary"
            disabled={processing || loading}
          >
            Cancelar
          </button>
          <button
            type="submit"
            className="btn-submit"
            disabled={processing || loading}
          >
            {processing ? 'Procesando...' : `Pagar $${monto.toFixed(2)}`}
          </button>
        </div>

        <div className="payment-security">
          <p>🔒 Pago 100% seguro y encriptado</p>
        </div>
      </form>
    </div>
  );
}

export default PagoSimulador;
