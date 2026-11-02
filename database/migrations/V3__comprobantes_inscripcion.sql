-- Aplicar despues de V2. No elimina datos. Respalde antes de ejecutar.
CREATE TABLE IF NOT EXISTS comprobante_inscripcion (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 inscripcion_id BIGINT NOT NULL UNIQUE,
 codigo VARCHAR(36) NOT NULL UNIQUE,
 CONSTRAINT fk_comprobante_inscripcion FOREIGN KEY (inscripcion_id) REFERENCES inscripcion(id)
);
INSERT INTO comprobante_inscripcion (inscripcion_id, codigo)
SELECT i.id, UUID() FROM inscripcion i
WHERE NOT EXISTS (SELECT c.id FROM comprobante_inscripcion c WHERE c.inscripcion_id=i.id);
