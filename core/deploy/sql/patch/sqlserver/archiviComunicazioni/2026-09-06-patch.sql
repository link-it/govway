-- DROP INDEX INDEX_TRSA_IN_QUEUE ON transazioni_sa;
-- DROP INDEX INDEX_TRSA_SEND ON transazioni_sa;
ALTER TABLE transazioni_sa DROP CONSTRAINT unique_transazioni_sa_1;
ALTER TABLE transazioni_sa ALTER COLUMN servizio_applicativo_erogatore VARCHAR(1024) NOT NULL;
ALTER TABLE transazioni_sa ADD CONSTRAINT unique_transazioni_sa_1 UNIQUE (id_transazione,servizio_applicativo_erogatore);
-- CREATE INDEX INDEX_TRSA_IN_QUEUE ON transazioni_sa (data_registrazione DESC,servizio_applicativo_erogatore,connettore_nome,consegna_terminata,dettaglio_esito,consegna_trasparente,consegna_im,numero_tentativi,codice_risposta,data_uscita_richiesta,data_ingresso_risposta,numero_prelievi_im,data_eliminazione_im);
-- CREATE INDEX INDEX_TRSA_SEND ON transazioni_sa (data_uscita_richiesta DESC,servizio_applicativo_erogatore,connettore_nome,data_ingresso_risposta,consegna_terminata,dettaglio_esito,consegna_trasparente,consegna_im,numero_tentativi,codice_risposta,data_registrazione);

-- DROP INDEX INDEX_TR_SEARCH ON transazioni;
-- CREATE INDEX INDEX_TR_SEARCH ON transazioni (data_ingresso_richiesta DESC,esito,esito_contesto,pdd_ruolo,pdd_codice,tipo_soggetto_erogatore,nome_soggetto_erogatore,tipo_servizio,nome_servizio,versione_servizio,azione,tipo_soggetto_fruitore,nome_soggetto_fruitore,servizio_applicativo_fruitore,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,protocollo,client_address,gruppi,uri_api,eventi_gestione,cluster_id,id,data_uscita_richiesta,data_ingresso_risposta,data_uscita_risposta) INCLUDE (id_correlazione_applicativa,id_correlazione_risposta);

-- DROP INDEX INDEX_TR_FULL ON transazioni;
-- CREATE INDEX INDEX_TR_FULL ON transazioni (data_ingresso_richiesta DESC,esito,esito_contesto,pdd_ruolo,pdd_codice,tipo_soggetto_erogatore,nome_soggetto_erogatore,tipo_servizio,nome_servizio,versione_servizio,azione,tipo_soggetto_fruitore,nome_soggetto_fruitore,servizio_applicativo_fruitore,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,protocollo,client_address,gruppi,uri_api,eventi_gestione,cluster_id) INCLUDE (id_correlazione_applicativa,id_correlazione_risposta);

DROP INDEX TRACCE_EXT_SEARCH ON tracce_ext_protocol_info;
UPDATE tracce_ext_protocol_info SET value = LEFT(value, 1445) WHERE DATALENGTH(value) > 1445;
ALTER TABLE tracce_ext_protocol_info ALTER COLUMN value VARCHAR(1445);
CREATE INDEX TRACCE_EXT_SEARCH ON tracce_ext_protocol_info (name,value);
