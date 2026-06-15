CREATE SEQUENCE seq_stat_orarie_llm MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE stat_orarie_llm
(
	data TIMESTAMP NOT NULL,
	llm_provider VARCHAR2(64) NOT NULL,
	llm_model VARCHAR2(64) NOT NULL,
	llm_provider_binding VARCHAR2(64),
	token_input NUMBER NOT NULL,
	token_output NUMBER NOT NULL,
	cost_estimated BINARY_DOUBLE,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_stat NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_orarie_llm_1 FOREIGN KEY (id_stat) REFERENCES statistiche_orarie(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_orarie_llm PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_STAT_HOUR_LLM_COVER ON stat_orarie_llm (id_stat,llm_provider,llm_model,llm_provider_binding);
CREATE INDEX INDEX_STAT_HOUR_LLM ON stat_orarie_llm (data DESC,llm_provider,llm_model,llm_provider_binding,token_input,token_output,cost_estimated);
CREATE TRIGGER trg_stat_orarie_llm
BEFORE
insert on stat_orarie_llm
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_stat_orarie_llm.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_stat_giorni_llm MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE stat_giorni_llm
(
	data TIMESTAMP NOT NULL,
	llm_provider VARCHAR2(64) NOT NULL,
	llm_model VARCHAR2(64) NOT NULL,
	llm_provider_binding VARCHAR2(64),
	token_input NUMBER NOT NULL,
	token_output NUMBER NOT NULL,
	cost_estimated BINARY_DOUBLE,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_stat NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_giorni_llm_1 FOREIGN KEY (id_stat) REFERENCES statistiche_giornaliere(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_giorni_llm PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_STAT_DAY_LLM_COVER ON stat_giorni_llm (id_stat,llm_provider,llm_model,llm_provider_binding);
CREATE INDEX INDEX_STAT_DAY_LLM ON stat_giorni_llm (data DESC,llm_provider,llm_model,llm_provider_binding,token_input,token_output,cost_estimated);
CREATE TRIGGER trg_stat_giorni_llm
BEFORE
insert on stat_giorni_llm
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_stat_giorni_llm.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/
