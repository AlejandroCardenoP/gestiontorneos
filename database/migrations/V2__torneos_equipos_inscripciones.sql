-- Ejecutar en gestiontorneos. No borra los datos existentes. Motor requerido: InnoDB.

CREATE TABLE IF NOT EXISTS deporte (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    min_jugadores INT NOT NULL,
    max_jugadores INT NOT NULL,
    CONSTRAINT chk_jugadores CHECK (min_jugadores > 0 AND max_jugadores >= min_jugadores)
);
CREATE TABLE IF NOT EXISTS equipo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    deporte_id BIGINT NOT NULL,
    cantidad_jugadores INT NOT NULL CHECK (cantidad_jugadores > 0),
    CONSTRAINT uq_equipo_nombre_deporte UNIQUE (nombre, deporte_id),
    CONSTRAINT fk_equipo_deporte FOREIGN KEY (deporte_id) REFERENCES deporte(id)
);
CREATE TABLE IF NOT EXISTS torneo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    deporte_id BIGINT NOT NULL,
    cupo_equipos INT NOT NULL CHECK (cupo_equipos BETWEEN 2 AND 128),
    fecha_inicio DATE NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('INSCRIPCION', 'EN_CURSO', 'FINALIZADO')),
    CONSTRAINT fk_torneo_deporte FOREIGN KEY (deporte_id) REFERENCES deporte(id)
);
CREATE TABLE IF NOT EXISTS inscripcion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    torneo_id BIGINT NOT NULL,
    equipo_id BIGINT NOT NULL,
    CONSTRAINT uq_inscripcion UNIQUE (torneo_id, equipo_id),
    CONSTRAINT fk_inscripcion_torneo FOREIGN KEY (torneo_id) REFERENCES torneo(id),
    CONSTRAINT fk_inscripcion_equipo FOREIGN KEY (equipo_id) REFERENCES equipo(id)
);
