CREATE TABLE transazioni_llm
(
	id_transazione VARCHAR(255) NOT NULL,
	data_ingresso_richiesta TIMESTAMP NOT NULL,
	llm_provider VARCHAR(64),
	llm_model VARCHAR(64),
	llm_provider_binding VARCHAR(64),
	token_input BIGINT,
	token_output BIGINT,
	cost_estimated REAL,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT unique_transazioni_llm_1 UNIQUE (id_transazione),
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_llm PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_TR_LLM ON transazioni_llm (data_ingresso_richiesta DESC,llm_provider,llm_model,llm_provider_binding,token_input,token_output,cost_estimated);
CREATE INDEX INDEX_TR_LLM_BY_MODEL ON transazioni_llm (llm_provider,llm_model,llm_provider_binding,data_ingresso_richiesta DESC);
