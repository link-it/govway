ALTER TABLE transazioni ADD token_purpose_id VARCHAR2(4000);

CREATE INDEX INDEX_TR_PDND_STATS ON transazioni (data_ingresso_richiesta,pdd_codice,token_purpose_id,id_messaggio_richiesta,eventi_gestione,pdd_ruolo);
CREATE INDEX INDEX_TR_PURPOSE_ID ON transazioni (token_purpose_id);

