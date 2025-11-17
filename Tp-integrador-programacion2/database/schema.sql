CREATE SCHEMA IF NOT EXISTS bdd_tpi;
USE bdd_tpi;

DROP TABLE IF EXISTS paciente;
DROP TABLE IF EXISTS historiaclinica;
DROP TABLE IF EXISTS _digits;

-- 1) DDL - Tablas y constraints
CREATE TABLE bdd_tpi.historiaclinica (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nroHistoria BIGINT NOT NULL,
  grupoSanguineo VARCHAR(3) NOT NULL,
  antecedentes VARCHAR(100) NULL,
  medicacionActual VARCHAR(200) NULL,
  observaciones VARCHAR(200) NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX nroHistoria_UNIQUE (nroHistoria),
  CHECK (grupoSanguineo IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'))
);

CREATE TABLE bdd_tpi.paciente (
  idPaciente BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(60) NOT NULL,
  apellido VARCHAR(60) NOT NULL,
  dni INT NOT NULL,
  fechaNacimiento DATE NOT NULL,
  historiaClinica BIGINT NOT NULL,
  PRIMARY KEY (idPaciente),
  UNIQUE INDEX dni_UNIQUE (dni),
  CHECK (dni >= 1000000 AND dni <= 99999999),
  CONSTRAINT fk_historiaClinicahistoriaclinica
    FOREIGN KEY (historiaClinica)
    REFERENCES bdd_tpi.historiaclinica (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
);

DELIMITER //
CREATE TRIGGER tr_before_insert_paciente
BEFORE INSERT ON bdd_tpi.paciente
FOR EACH ROW
BEGIN
  IF NEW.fechaNacimiento > CURDATE() THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Fecha de nacimiento futura no permitida.';
  END IF;
END//
CREATE TRIGGER tr_before_update_paciente
BEFORE UPDATE ON bdd_tpi.paciente
FOR EACH ROW
BEGIN
  IF NEW.fechaNacimiento > CURDATE() THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Fecha de nacimiento futura no permitida.';
  END IF;
END//
DELIMITER ;
