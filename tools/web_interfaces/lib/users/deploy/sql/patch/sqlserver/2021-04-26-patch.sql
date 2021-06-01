ALTER TABLE users ADD data_password DATETIME2 DEFAULT CURRENT_TIMESTAMP;
UPDATE users SET data_password = CURRENT_TIMESTAMP;
ALTER TABLE users ALTER COLUMN data_password DATETIME2 NOT NULL;

ALTER TABLE users ADD check_data_password INT DEFAULT 1;
UPDATE users SET check_data_password = 1;
ALTER TABLE users ALTER COLUMN check_data_password INT NOT NULL;


CREATE TABLE users_password
(
	password VARCHAR(255) NOT NULL,
	data_password DATETIME2 NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	id_utente BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_users_password_1 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_password PRIMARY KEY (id)
);

