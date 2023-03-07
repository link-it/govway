CREATE TABLE prova_bytes (
     descrizione VARCHAR(255) NOT NULL,
     contenuto MEDIUMBLOB NOT NULL,
	 contenuto_vuoto MEDIUMBLOB
);
		 
CREATE TABLE prova (
     descrizione VARCHAR(255) NOT NULL,
     id BIGINT AUTO_INCREMENT,
     CONSTRAINT pk_prova PRIMARY KEY (id)
);