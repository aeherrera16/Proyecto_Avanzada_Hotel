import React from 'react';
import { Link } from 'react-router-dom';

function Home() {
  return (
    <div className="home">
      {/* Hero Section */}
      <section className="hero">
        <video className="hero-video" autoPlay loop muted playsInline>
          <source src="/videos/hotel-video.mp4" type="video/mp4" />
          Tu navegador no soporta el elemento de video.
        </video>
        <div className="hero-overlay"></div>
        <div className="hero-content">
          <h2>Bienvenido a Hotel ESPE</h2>
          <p>Una experiencia de lujo inolvidable en el corazón de Quito</p>
          <Link to="/reservas/nueva" className="btn-primary">
            Reservar Ahora
          </Link>
        </div>
      </section>

      {/* Intro Section */}
      <section className="container">
        <div className="section-title">
          <h2>Experiencia de Lujo</h2>
          <div className="divider"></div>
          <p>
            Nuestro hotel boutique combina la elegancia clásica con el confort moderno.
            Cada detalle ha sido cuidadosamente seleccionado para ofrecerle una
            estancia memorable en un ambiente de sofisticación y calidez.
          </p>
        </div>

        <div className="cards-grid">
          <div className="card">
            <img
              src="https://images.unsplash.com/photo-1590490360182-c33d57733427?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80"
              alt="Habitaciones"
              className="card-image"
            />
            <div className="card-content">
              <h3>Habitaciones Elegantes</h3>
              <p>
                Disfrute de nuestras habitaciones finamente decoradas, equipadas
                con todas las comodidades modernas para garantizar su confort.
              </p>
              <Link to="/habitaciones" className="btn-primary">
                Ver Habitaciones
              </Link>
            </div>
          </div>

          <div className="card">
            <img
              src="https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80"
              alt="Servicios"
              className="card-image"
            />
            <div className="card-content">
              <h3>Reservas Fáciles</h3>
              <p>
                Sistema de reservas en línea con proceso de pago seguro.
                Reserve su habitación en pocos minutos.
              </p>
              <Link to="/reservas/nueva" className="btn-primary">
                Hacer Reserva
              </Link>
            </div>
          </div>

          <div className="card">
            <img
              src="https://images.unsplash.com/photo-1564501049412-61c2a3083791?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80"
              alt="Ubicación"
              className="card-image"
            />
            <div className="card-content">
              <h3>Gestión Completa</h3>
              <p>
                Administre sus reservas, consulte el estado de sus habitaciones
                y acceda a toda la información de sus huéspedes.
              </p>
              <Link to="/reservas" className="btn-primary">
                Ver Reservas
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* Testimonials Section */}
      <section className="container" style={{ background: '#fff', padding: '80px 50px' }}>
        <div className="section-title">
          <h2>Lo que dicen nuestros huéspedes</h2>
          <div className="divider"></div>
        </div>
        <div className="cards-grid">
          <div className="card">
            <div className="card-content">
              <p style={{ fontStyle: 'italic', marginBottom: '20px' }}>
                "Una experiencia absolutamente maravillosa. El servicio es
                impecable y las instalaciones son de primera clase. Sin duda
                volveré."
              </p>
              <h4>— María González</h4>
              <p style={{ color: '#d4af37' }}>⭐⭐⭐⭐⭐</p>
            </div>
          </div>
          <div className="card">
            <div className="card-content">
              <p style={{ fontStyle: 'italic', marginBottom: '20px' }}>
                "El mejor hotel en el que me he hospedado en Quito. La atención
                al detalle y el servicio personalizado superaron mis expectativas."
              </p>
              <h4>— Carlos Rodríguez</h4>
              <p style={{ color: '#d4af37' }}>⭐⭐⭐⭐⭐</p>
            </div>
          </div>
          <div className="card">
            <div className="card-content">
              <p style={{ fontStyle: 'italic', marginBottom: '20px' }}>
                "Perfecto para una escapada romántica. El ambiente es elegante
                y acogedor. Altamente recomendado."
              </p>
              <h4>— Ana Martínez</h4>
              <p style={{ color: '#d4af37' }}>⭐⭐⭐⭐⭐</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}

export default Home;