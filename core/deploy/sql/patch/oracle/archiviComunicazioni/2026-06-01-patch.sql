CREATE SEQUENCE seq_transazioni_llm MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE transazioni_llm
(
	id_transazione VARCHAR2(255) NOT NULL,
	data_ingresso_richiesta TIMESTAMP NOT NULL,
	llm_provider VARCHAR2(64),
	llm_model VARCHAR2(64),
	llm_provider_binding VARCHAR2(64),
	token_input NUMBER,
	token_output NUMBER,
	cost_estimated BINARY_DOUBLE,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_transazioni_llm_1 UNIQUE (id_transazione),
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_llm PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_TR_LLM ON transazioni_llm (data_ingresso_richiesta DESC,llm_provider,llm_model,llm_provider_binding,token_input,token_output,cost_estimated);
CREATE INDEX INDEX_TR_LLM_BY_MODEL ON transazioni_llm (llm_provider,llm_model,llm_provider_binding,data_ingresso_richiesta DESC);
CREATE TRIGGER trg_transazioni_llm
BEFORE
insert on transazioni_llm
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_transazioni_llm.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/
