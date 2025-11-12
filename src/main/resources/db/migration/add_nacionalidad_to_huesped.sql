-- Agregar columna nacionalidad a la tabla huesped
ALTER TABLE huesped ADD COLUMN nacionalidad VARCHAR(100);

-- Actualizar registros existentes con un valor por defecto temporal
UPDATE huesped SET nacionalidad = 'No especificado' WHERE nacionalidad IS NULL;

-- Hacer la columna NOT NULL después de actualizar los registros existentes
ALTER TABLE huesped MODIFY COLUMN nacionalidad VARCHAR(100) NOT NULL;
