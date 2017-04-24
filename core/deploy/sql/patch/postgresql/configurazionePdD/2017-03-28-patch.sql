-- Ruoli

ALTER TABLE porte_applicative ADD COLUMN autenticazione VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN autenticazione_opzionale VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN autorizzazione VARCHAR(255);
UPDATE porte_applicative set autorizzazione='authenticated';

ALTER TABLE porte_applicative ADD COLUMN ruoli_match VARCHAR(255); 

CREATE SEQUENCE seq_pa_ruoli start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pa_ruoli
(
	id_porta BIGINT NOT NULL,
	ruolo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pa_ruoli') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_ruoli_1 UNIQUE (id_porta,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_ruoli_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_ruoli PRIMARY KEY (id)
);


ALTER TABLE porte_delegate ADD COLUMN autenticazione_opzionale VARCHAR(255);

UPDATE porte_delegate set autorizzazione='authenticated' WHERE autorizzazione='openspcoop';

ALTER TABLE porte_delegate ADD COLUMN ruoli_match VARCHAR(255);
 
CREATE SEQUENCE seq_pd_ruoli start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pd_ruoli
(
	id_porta BIGINT NOT NULL,
	ruolo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pd_ruoli') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_ruoli_1 UNIQUE (id_porta,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_ruoli_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_ruoli PRIMARY KEY (id)
);


ALTER TABLE servizi_applicativi DROP COLUMN subjectrisp;
ALTER TABLE servizi_applicativi DROP COLUMN subjectinv;

CREATE SEQUENCE seq_sa_ruoli start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE sa_ruoli
(
	id_servizio_applicativo BIGINT NOT NULL,
	ruolo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_sa_ruoli') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_sa_ruoli_1 UNIQUE (id_servizio_applicativo,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_ruoli_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_ruoli PRIMARY KEY (id)
);



-- config

ALTER TABLE configurazione ADD COLUMN authn_statocache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN authn_dimensionecache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN authn_algoritmocache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN authn_idlecache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN authn_lifecache VARCHAR(255);

UPDATE configurazione set authn_statocache='abilitato';
UPDATE configurazione set authn_dimensionecache=5000;
UPDATE configurazione set authn_algoritmocache='lru';
UPDATE configurazione set authn_lifecache='7200';

