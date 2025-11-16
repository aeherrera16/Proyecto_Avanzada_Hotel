import React from 'react';
import { Link } from 'react-router-dom';

function Header() {
  return (
    <header className="header">
      <div className="header-top">
        Hotel Boutique de Lujo • Reservas: +593 2 400 8000
      </div>
      <div className="header-main">
        <div className="logo">
          <h1>HOTEL ESPE</h1>
          <p>Experiencia de Lujo</p>
        </div>
        <nav className="nav">
          <ul>
            <li><Link to="/">Inicio</Link></li>
            <li><Link to="/habitaciones">Habitaciones</Link></li>
            <li><Link to="/reservas">Reservas</Link></li>
            <li><Link to="/huespedes">Huéspedes</Link></li>
          </ul>
        </nav>
      </div>
    </header>
  );
}

export default Header;

