CREATE TABLE prova_bytes (
     descrizione VARCHAR(255) NOT NULL,
		contenuto VARBINARY(MAX) NOT NULL,
		contenuto_vuoto VARBINARY(MAX) 
);
		 
CREATE TABLE prova (
     descrizione VARCHAR(255) NOT NULL,
		id BIGINT IDENTITY,
     CONSTRAINT pk_prova PRIMARY KEY (id)
);