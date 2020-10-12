ALTER TABLE statistiche_orarie ADD COLUMN uri_api VARCHAR2(20) ;
UPDATE statistiche_orarie set uri_api='-';
ALTER TABLE statistiche_orarie MODIFY COLUMN uri_api VARCHAR2(20) NOT NULL;

-- DROP INDEX INDEX_STAT_HOUR;
-- CREATE INDEX INDEX_STAT_HOUR ON statistiche_orarie (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);

ALTER TABLE statistiche_giornaliere ADD COLUMN uri_api VARCHAR2(20) ;
UPDATE statistiche_giornaliere set uri_api='-';
ALTER TABLE statistiche_giornaliere MODIFY COLUMN uri_api VARCHAR2(20) NOT NULL;

-- DROP INDEX INDEX_STAT_DAY;
-- CREATE INDEX INDEX_STAT_DAY ON statistiche_giornaliere (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);

ALTER TABLE statistiche_settimanali ADD COLUMN uri_api VARCHAR2(20) ;
UPDATE statistiche_settimanali set uri_api='-';
ALTER TABLE statistiche_settimanali MODIFY COLUMN uri_api VARCHAR2(20) NOT NULL;

-- DROP INDEX INDEX_STAT_WEEK;
-- CREATE INDEX INDEX_STAT_WEEK ON statistiche_settimanali (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);

ALTER TABLE statistiche_mensili ADD COLUMN uri_api VARCHAR2(20) ;
UPDATE statistiche_mensili set uri_api='-';
ALTER TABLE statistiche_mensili MODIFY COLUMN uri_api VARCHAR2(20) NOT NULL;

-- DROP INDEX INDEX_STAT_MONTH;
-- CREATE INDEX INDEX_STAT_MONTH ON statistiche_mensili (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);

