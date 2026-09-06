DROP INDEX INDEX_STAT_HOUR_ENTRY ON statistiche_orarie;
-- DROP INDEX INDEX_STAT_HOUR_FULL ON statistiche_orarie;
-- DROP INDEX INDEX_STAT_HOUR ON statistiche_orarie;

ALTER TABLE statistiche_orarie ALTER COLUMN mittente VARCHAR(80) NOT NULL;
ALTER TABLE statistiche_orarie ALTER COLUMN destinatario VARCHAR(80) NOT NULL;

CREATE INDEX INDEX_STAT_HOUR_ENTRY ON statistiche_orarie (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_HOUR_FULL ON statistiche_orarie (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id);
-- CREATE INDEX INDEX_STAT_HOUR ON statistiche_orarie (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);

DROP INDEX INDEX_STAT_DAY_ENTRY ON statistiche_giornaliere;
-- DROP INDEX INDEX_STAT_DAY_FULL ON statistiche_giornaliere;
-- DROP INDEX INDEX_STAT_DAY ON statistiche_giornaliere;

ALTER TABLE statistiche_giornaliere ALTER COLUMN mittente VARCHAR(80) NOT NULL;
ALTER TABLE statistiche_giornaliere ALTER COLUMN destinatario VARCHAR(80) NOT NULL;

CREATE INDEX INDEX_STAT_DAY_ENTRY ON statistiche_giornaliere (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_DAY_FULL ON statistiche_giornaliere (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id);
-- CREATE INDEX INDEX_STAT_DAY ON statistiche_giornaliere (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);

DROP INDEX INDEX_STAT_WEEK_ENTRY ON statistiche_settimanali;
-- DROP INDEX INDEX_STAT_WEEK_FULL ON statistiche_settimanali;
-- DROP INDEX INDEX_STAT_WEEK ON statistiche_settimanali;

ALTER TABLE statistiche_settimanali ALTER COLUMN mittente VARCHAR(80) NOT NULL;
ALTER TABLE statistiche_settimanali ALTER COLUMN destinatario VARCHAR(80) NOT NULL;

CREATE INDEX INDEX_STAT_WEEK_ENTRY ON statistiche_settimanali (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_WEEK_FULL ON statistiche_settimanali (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id);
-- CREATE INDEX INDEX_STAT_WEEK ON statistiche_settimanali (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);

DROP INDEX INDEX_STAT_MONTH_ENTRY ON statistiche_mensili;
-- DROP INDEX INDEX_STAT_MONTH_FULL ON statistiche_mensili;
-- DROP INDEX INDEX_STAT_MONTH ON statistiche_mensili;

ALTER TABLE statistiche_mensili ALTER COLUMN mittente VARCHAR(80) NOT NULL;
ALTER TABLE statistiche_mensili ALTER COLUMN destinatario VARCHAR(80) NOT NULL;

CREATE INDEX INDEX_STAT_MONTH_ENTRY ON statistiche_mensili (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_MONTH_FULL ON statistiche_mensili (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id);
-- CREATE INDEX INDEX_STAT_MONTH ON statistiche_mensili (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);
