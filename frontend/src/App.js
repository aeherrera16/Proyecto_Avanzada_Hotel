import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';

// Components
import Header from './components/Header';
import Footer from './components/Footer';

// Pages
import Home from './pages/Home';
import Habitaciones from './pages/Habitaciones';
import HabitacionForm from './pages/HabitacionForm';
import Huespedes from './pages/Huespedes';
import HuespedForm from './pages/HuespedForm';
import Reservas from './pages/Reservas';
import ReservaFlow from './pages/ReservaFlow';
import ActividadReactiva from './pages/ActividadReactiva';

function App() {
  return (
    <Router>
      <div className="App">
        <Header />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/habitaciones" element={<Habitaciones />} />
          <Route path="/habitaciones/nueva" element={<HabitacionForm />} />
          <Route path="/habitaciones/editar/:id" element={<HabitacionForm />} />
          <Route path="/huespedes" element={<Huespedes />} />
          <Route path="/huespedes/nuevo" element={<HuespedForm />} />
          <Route path="/huespedes/editar/:id" element={<HuespedForm />} />
          <Route path="/reservas" element={<Reservas />} />
          <Route path="/reservas/nueva" element={<ReservaFlow />} />
          <Route path="/actividad" element={<ActividadReactiva />} />
        </Routes>
        <Footer />
      </div>
    </Router>
  );
}

export default App;

