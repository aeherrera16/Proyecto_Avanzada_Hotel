import React from 'react';

function Footer() {
  return (
    <footer className="footer">
      <div className="footer-content">
        <div className="footer-section">
          <h3>Hotel ESPE</h3>
          <p>
            Ubicado en el corazón de Quito, nuestro hotel boutique ofrece
            una experiencia única de lujo y confort. Descubre la perfecta
            combinación entre elegancia colonial y modernidad.
          </p>
        </div>
        <div className="footer-section">
          <h3>Contacto</h3>
          <ul>
            <li>📍 Av. Universitaria, Quito, Ecuador</li>
            <li>📞 +593 2 400 8000</li>
            <li>✉️ reservas@hotelespe.com</li>
            <li>🕐 Lun - Dom: 24 horas</li>
          </ul>
        </div>
        <div className="footer-section">
          <h3>Enlaces Rápidos</h3>
          <ul>
            <li><a href="/habitaciones">Nuestras Habitaciones</a></li>
            <li><a href="/reservas">Hacer una Reserva</a></li>
            <li><a href="/servicios">Servicios</a></li>
            <li><a href="/galeria">Galería</a></li>
          </ul>
        </div>
        <div className="footer-section">
          <h3>Síguenos</h3>
          <ul>
            <li><a href="#facebook">Facebook</a></li>
            <li><a href="#instagram">Instagram</a></li>
            <li><a href="#twitter">Twitter</a></li>
            <li><a href="#tripadvisor">TripAdvisor</a></li>
          </ul>
        </div>
      </div>
      <div className="footer-bottom">
        <p>&copy; 2025 Hotel ESPE. Todos los derechos reservados. | Desarrollado con React & Spring Boot</p>
      </div>
    </footer>
  );
}

export default Footer;

