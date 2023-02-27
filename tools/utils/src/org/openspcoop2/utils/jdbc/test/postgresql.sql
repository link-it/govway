CREATE TABLE prova_bytes (
     descrizione VARCHAR(255) NOT NULL,
     contenuto BYTEA NOT NULL,
     contenuto_vuoto BYTEA
);
		 
CREATE SEQUENCE seq_prova start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;
CREATE TABLE prova (
     descrizione VARCHAR(255) NOT NULL,
	 id BIGINT DEFAULT nextval('seq_prova') NOT NULL,
     CONSTRAINT pk_prova PRIMARY KEY (id)
);