CREATE TABLE stat_orarie_llm
(
	data TIMESTAMP NOT NULL,
	llm_provider VARCHAR(64) NOT NULL,
	llm_model VARCHAR(64) NOT NULL,
	llm_provider_binding VARCHAR(64),
	token_input BIGINT NOT NULL,
	token_output BIGINT NOT NULL,
	cost_estimated REAL,
	richieste INT NOT NULL,
	bytes_banda_complessiva BIGINT,
	bytes_banda_interna BIGINT,
	bytes_banda_esterna BIGINT,
	latenza_totale BIGINT,
	latenza_porta BIGINT,
	latenza_servizio BIGINT,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	id_stat BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_orarie_llm_1 FOREIGN KEY (id_stat) REFERENCES statistiche_orarie(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_orarie_llm PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_STAT_HOUR_LLM_COVER ON stat_orarie_llm (id_stat,llm_provider,llm_model,llm_provider_binding);
CREATE INDEX INDEX_STAT_HOUR_LLM_FULL ON stat_orarie_llm (data DESC,llm_provider,llm_model,llm_provider_binding,token_input,token_output,cost_estimated,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);



CREATE TABLE stat_giorni_llm
(
	data TIMESTAMP NOT NULL,
	llm_provider VARCHAR(64) NOT NULL,
	llm_model VARCHAR(64) NOT NULL,
	llm_provider_binding VARCHAR(64),
	token_input BIGINT NOT NULL,
	token_output BIGINT NOT NULL,
	cost_estimated REAL,
	richieste INT NOT NULL,
	bytes_banda_complessiva BIGINT,
	bytes_banda_interna BIGINT,
	bytes_banda_esterna BIGINT,
	latenza_totale BIGINT,
	latenza_porta BIGINT,
	latenza_servizio BIGINT,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	id_stat BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_giorni_llm_1 FOREIGN KEY (id_stat) REFERENCES statistiche_giornaliere(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_giorni_llm PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_STAT_DAY_LLM_COVER ON stat_giorni_llm (id_stat,llm_provider,llm_model,llm_provider_binding);
CREATE INDEX INDEX_STAT_DAY_LLM_FULL ON stat_giorni_llm (data DESC,llm_provider,llm_model,llm_provider_binding,token_input,token_output,cost_estimated,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);
