ALTER TABLE users ADD COLUMN data_password TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
UPDATE users SET data_password = CURRENT_TIMESTAMP;
ALTER TABLE users MODIFY COLUMN data_password TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE users ADD COLUMN check_data_password INT DEFAULT 1;
UPDATE users SET check_data_password = 1;
ALTER TABLE users MODIFY COLUMN check_data_password INT NOT NULL DEFAULT 1;

CREATE TABLE users_password
(
	password VARCHAR(255) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_password TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_utente BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_users_password_1 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_password PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

