create database BD_T2_Soca ;
Use  BD_T2_Soca;
select * from Clientes;
select * from Peliculas;
select * from Alquileres;
select * from Detalle_alquiler;
DROP DATABASE BD_T2_Soca;
CREATE TABLE IF NOT EXISTS clientes (
    id_cliente BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS peliculas (
    id_pelicula BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    genero VARCHAR(50) NOT NULL,
    stock INT NOT NULL,
    precio DOUBLE NOT NULL
);

CREATE TABLE IF NOT EXISTS alquileres (
    id_alquiler BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    id_cliente BIGINT NOT NULL,
    total DOUBLE NOT NULL,
    estado VARCHAR(10) NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
);

CREATE TABLE IF NOT EXISTS detalle_alquiler (
    id_alquiler BIGINT NOT NULL,
    id_pelicula BIGINT NOT NULL,
    cantidad INT NOT NULL,
    PRIMARY KEY (id_alquiler, id_pelicula),
    FOREIGN KEY (id_alquiler) REFERENCES alquileres(id_alquiler),
    FOREIGN KEY (id_pelicula) REFERENCES peliculas(id_pelicula)
);

-- Datos de prueba
INSERT INTO clientes (nombre, email) VALUES 
('Karen Soca', 'karen@email.com'),
('Maria Perez', 'maria@email.com'),
('Alejandro Gomez', 'alejandro@email.com');

INSERT INTO peliculas (titulo, genero, stock, precio) VALUES 
('Silencio', 'Ciencia Ficción', 4, 20.0),
('Titanic', 'Acción', 5, 9.0),
('Star Wars', 'Ficcion', 2, 30.0);