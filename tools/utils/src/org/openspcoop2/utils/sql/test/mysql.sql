CREATE TABLE tracce (
     descrizione VARCHAR(255) NOT NULL,
     gdo TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
     gdo2 TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
     tipo_mittente VARCHAR(255) NOT NULL,
     mittente VARCHAR(255) NOT NULL,
     tipo_destinatario VARCHAR(255) NOT NULL,
     destinatario VARCHAR(255) NOT NULL,
     campo_vuoto VARCHAR(255),
     campo_update VARCHAR(255),
     campo_int_update INT,
	 id BIGINT AUTO_INCREMENT,
	 CONSTRAINT pk_tracce PRIMARY KEY (id)
);
CREATE TABLE msgdiagnostici (
     descrizione VARCHAR(255) NOT NULL,
     gdo TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
     gdo2 TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
     tipo_mittente VARCHAR(255) NOT NULL,
     mittente VARCHAR(255) NOT NULL,
     tipo_destinatario VARCHAR(255) NOT NULL,
     destinatario VARCHAR(255) NOT NULL,
     campo_vuoto VARCHAR(255),
     campo_update VARCHAR(255),
     campo_int_update INT,
	 id BIGINT AUTO_INCREMENT,
	 CONSTRAINT pk_msgdiagnostici PRIMARY KEY (id)
);