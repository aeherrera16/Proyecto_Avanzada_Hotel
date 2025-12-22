-- Tablas para R2DBC H2 - Creación Automática
CREATE TABLE IF NOT EXISTS habitacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(10) NOT NULL UNIQUE,
    tipo VARCHAR(50) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    disponible BOOLEAN DEFAULT TRUE,
    estado VARCHAR(20) DEFAULT 'Disponible'
);

CREATE TABLE IF NOT EXISTS huesped (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    cedula VARCHAR(20) NOT NULL UNIQUE,
    telefono VARCHAR(15),
    email VARCHAR(100),
    nacionalidad VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    huesped_id BIGINT NOT NULL,
    habitacion_id BIGINT NOT NULL,
    fecha_entrada DATE NOT NULL,
    fecha_salida DATE NOT NULL,
    precio_total DECIMAL(10,2),
    estado VARCHAR(20) DEFAULT 'ACTIVA',
    FOREIGN KEY (huesped_id) REFERENCES huesped(id),
    FOREIGN KEY (habitacion_id) REFERENCES habitacion(id)
);

CREATE TABLE IF NOT EXISTS pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reserva_id BIGINT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metodo_pago VARCHAR(50) NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    FOREIGN KEY (reserva_id) REFERENCES reservas(id)
);

-- Datos de prueba para demostración
INSERT INTO habitacion (numero, tipo, precio, disponible) VALUES 
('101', 'Simple', 50.00, TRUE),
('102', 'Doble', 80.00, TRUE),
('103', 'Suite', 120.00, TRUE);

INSERT INTO huesped (nombre, apellido, cedula, telefono, email, nacionalidad) VALUES 
('Juan', 'Perez', '12345678', '0987654321', 'juan@email.com', 'Ecuatoriana'),
('Maria', 'Gomez', '87654321', '0123456789', 'maria@email.com', 'Ecuatoriana');
