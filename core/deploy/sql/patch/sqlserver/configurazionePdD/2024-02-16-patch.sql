ALTER TABLE configurazione ADD tracciamento_esiti_pd VARCHAR(255);
UPDATE configurazione set tracciamento_esiti_pd=tracciamento_esiti;
ALTER TABLE configurazione ADD transazioni_tempi_pd VARCHAR(255);
UPDATE configurazione set transazioni_tempi_pd=transazioni_tempi;
ALTER TABLE configurazione ADD transazioni_token_pd VARCHAR(255);
UPDATE configurazione set transazioni_token_pd=transazioni_token;

ALTER TABLE porte_delegate ADD tracciamento_stato VARCHAR(255);
UPDATE porte_delegate set tracciamento_stato='abilitato' WHERE tracciamento_esiti IS NOT NULL AND tracciamento_esiti <> '';
UPDATE porte_delegate set tracciamento_stato='disabilitato' WHERE tracciamento_esiti IS NULL OR tracciamento_esiti = '';
ALTER TABLE porte_delegate ADD transazioni_tempi VARCHAR(255);
ALTER TABLE porte_delegate ADD transazioni_token VARCHAR(255);

ALTER TABLE porte_applicative ADD tracciamento_stato VARCHAR(255);
UPDATE porte_applicative set tracciamento_stato='abilitato' WHERE tracciamento_esiti IS NOT NULL AND tracciamento_esiti <> '';
UPDATE porte_applicative set tracciamento_stato='disabilitato' WHERE tracciamento_esiti IS NULL OR tracciamento_esiti = '';
ALTER TABLE porte_applicative ADD transazioni_tempi VARCHAR(255);
ALTER TABLE porte_applicative ADD transazioni_token VARCHAR(255);



CREATE TABLE tracce_config
(
	proprietario VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	id_proprietario BIGINT NOT NULL,
	stato VARCHAR(255),
	filtro_esiti VARCHAR(255),
	request_in VARCHAR(255),
	request_out VARCHAR(255),
	response_out VARCHAR(255),
	response_out_complete VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT pk_tracce_config PRIMARY KEY (id)
);

-- index
CREATE INDEX index_tracce_config_1 ON tracce_config (proprietario,tipo);



CREATE TABLE filetrace_config
(
	proprietario VARCHAR(255) NOT NULL,
	id_proprietario BIGINT NOT NULL,
	config VARCHAR(255),
	dump_in_stato VARCHAR(255),
	dump_in_stato_hdr VARCHAR(255),
	dump_in_stato_body VARCHAR(255),
	dump_out_stato VARCHAR(255),
	dump_out_stato_hdr VARCHAR(255),
	dump_out_stato_body VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT pk_filetrace_config PRIMARY KEY (id)
);

-- index
CREATE INDEX index_filetrace_config_1 ON filetrace_config (proprietario);



INSERT INTO tracce_config (proprietario,tipo,id_proprietario,stato,filtro_esiti,request_in,request_out,response_out,response_out_complete) VALUES ('configpa','filetrace',-1,'configurazioneEsterna','disabilitato','disabilitato','disabilitato','disabilitato','abilitato');
INSERT INTO tracce_config (proprietario,tipo,id_proprietario,stato,filtro_esiti,request_in,request_out,response_out,response_out_complete) VALUES ('configpd','filetrace',-1,'configurazioneEsterna','disabilitato','disabilitato','disabilitato','disabilitato','abilitato');


