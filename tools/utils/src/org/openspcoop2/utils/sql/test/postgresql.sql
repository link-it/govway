CREATE SEQUENCE seq_tracce start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;
CREATE TABLE tracce (
     descrizione VARCHAR(255) NOT NULL,
     gdo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     gdo2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     tipo_mittente VARCHAR(255) NOT NULL,
     mittente VARCHAR(255) NOT NULL,
     tipo_destinatario VARCHAR(255) NOT NULL,
     destinatario VARCHAR(255) NOT NULL,
     campo_vuoto VARCHAR(255),
     campo_update VARCHAR(255),
     campo_int_update INT,
	 id BIGINT DEFAULT nextval('seq_tracce') NOT NULL
);
CREATE SEQUENCE seq_msgdiagnostici start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;
CREATE TABLE msgdiagnostici (
     descrizione VARCHAR(255) NOT NULL,
     gdo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     gdo2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     tipo_mittente VARCHAR(255) NOT NULL,
     mittente VARCHAR(255) NOT NULL,
     tipo_destinatario VARCHAR(255) NOT NULL,
     destinatario VARCHAR(255) NOT NULL,
     campo_vuoto VARCHAR(255),
     campo_update VARCHAR(255),
     campo_int_update INT,      
	 id BIGINT DEFAULT nextval('seq_msgdiagnostici') NOT NULL
);