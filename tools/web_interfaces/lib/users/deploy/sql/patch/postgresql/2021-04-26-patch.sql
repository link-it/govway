ALTER TABLE users ADD COLUMN data_password TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
UPDATE users SET data_password = CURRENT_TIMESTAMP;
ALTER TABLE users ALTER COLUMN data_password SET NOT NULL;

ALTER TABLE users ADD COLUMN check_data_password INT DEFAULT 1;
UPDATE users SET check_data_password = 1;
ALTER TABLE users ALTER COLUMN check_data_password SET NOT NULL;


CREATE SEQUENCE seq_users_password start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE users_password
(
	password VARCHAR(255) NOT NULL,
	data_password TIMESTAMP NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_users_password') NOT NULL,
	id_utente BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_users_password_1 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_password PRIMARY KEY (id)
);

