ALTER TABLE transazioni ADD COLUMN token_id VARCHAR(50);

CREATE INDEX INDEX_TR_TOKEN_ID ON transazioni (token_id);

DROP INDEX INDEX_TR_PDND_STATS;
CREATE INDEX INDEX_TR_PDND_STATS ON transazioni (data_ingresso_richiesta,pdd_codice,token_purpose_id,token_id,eventi_gestione,pdd_ruolo);

