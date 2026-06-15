CREATE TABLE transazioni_llm
(
	id_transazione VARCHAR(255) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_ingresso_richiesta TIMESTAMP(3) NOT NULL DEFAULT 0,
	llm_provider VARCHAR(64),
	llm_model VARCHAR(64),
	llm_provider_binding VARCHAR(64),
	token_input BIGINT,
	token_output BIGINT,
	cost_estimated DOUBLE,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_transazioni_llm_1 UNIQUE (id_transazione),
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_llm PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_TR_LLM ON transazioni_llm (data_ingresso_richiesta DESC,llm_provider,llm_model,llm_provider_binding,token_input,token_output,cost_estimated);
CREATE INDEX INDEX_TR_LLM_BY_MODEL ON transazioni_llm (llm_provider,llm_model,llm_provider_binding,data_ingresso_richiesta DESC);
