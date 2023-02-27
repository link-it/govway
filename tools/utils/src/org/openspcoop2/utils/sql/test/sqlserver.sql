CREATE TABLE tracce (
     descrizione VARCHAR(255) NOT NULL,
     gdo DATETIME2 DEFAULT CURRENT_TIMESTAMP,
     gdo2 DATETIME2 DEFAULT CURRENT_TIMESTAMP,
     tipo_mittente VARCHAR(255) NOT NULL,
     mittente VARCHAR(255) NOT NULL,
     tipo_destinatario VARCHAR(255) NOT NULL,
     destinatario VARCHAR(255) NOT NULL,
     campo_vuoto VARCHAR(255),
     campo_update VARCHAR(255),
     campo_int_update INT,
	 id BIGINT IDENTITY
);
CREATE TABLE msgdiagnostici (
     descrizione VARCHAR(255) NOT NULL,
     gdo DATETIME2 DEFAULT CURRENT_TIMESTAMP,
     gdo2 DATETIME2 DEFAULT CURRENT_TIMESTAMP,
     tipo_mittente VARCHAR(255) NOT NULL,
     mittente VARCHAR(255) NOT NULL,
     tipo_destinatario VARCHAR(255) NOT NULL,
     destinatario VARCHAR(255) NOT NULL,
     campo_vuoto VARCHAR(255),
     campo_update VARCHAR(255),
     campo_int_update INT,
	 id BIGINT IDENTITY
);