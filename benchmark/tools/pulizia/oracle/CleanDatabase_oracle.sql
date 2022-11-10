truncate table credenziale_mittente;

truncate table transazioni ;

truncate table msgdiagnostici ;

ALTER TABLE tracce_ext_protocol_info DISABLE CONSTRAINT fk_tracce_ext_protocol_info_1;
ALTER TABLE tracce_allegati DISABLE CONSTRAINT fk_tracce_allegati_1;
ALTER TABLE tracce_trasmissioni DISABLE CONSTRAINT fk_tracce_trasmissioni_1;
ALTER TABLE tracce_riscontri DISABLE CONSTRAINT fk_tracce_riscontri_1;
ALTER TABLE tracce_eccezioni DISABLE CONSTRAINT fk_tracce_eccezioni_1;
truncate table tracce_ext_protocol_info ;
truncate table tracce_allegati ;
truncate table tracce_trasmissioni ;
truncate table tracce_riscontri ;
truncate table tracce_eccezioni ;
truncate table tracce;
ALTER TABLE tracce_ext_protocol_info ENABLE CONSTRAINT fk_tracce_ext_protocol_info_1;
ALTER TABLE tracce_allegati ENABLE CONSTRAINT fk_tracce_allegati_1;
ALTER TABLE tracce_trasmissioni ENABLE CONSTRAINT fk_tracce_trasmissioni_1;
ALTER TABLE tracce_riscontri ENABLE CONSTRAINT fk_tracce_riscontri_1;
ALTER TABLE tracce_eccezioni ENABLE CONSTRAINT fk_tracce_eccezioni_1;


ALTER TABLE msg_servizi_applicativi DISABLE CONSTRAINT fk_MSG_SERVIZI_APPLICATIVI_1;
ALTER TABLE definizione_messaggi DISABLE CONSTRAINT fk_DEFINIZIONE_MESSAGGI_1;
truncate table msg_servizi_applicativi;
truncate table definizione_messaggi ;
truncate table messaggi ;
ALTER TABLE msg_servizi_applicativi ENABLE CONSTRAINT fk_MSG_SERVIZI_APPLICATIVI_1;
ALTER TABLE definizione_messaggi ENABLE CONSTRAINT fk_DEFINIZIONE_MESSAGGI_1;


ALTER TABLE DUMP_MULTIPART_HEADER DISABLE CONSTRAINT fk_dump_multipart_header_1;
ALTER TABLE dump_header_trasporto DISABLE CONSTRAINT fk_dump_header_trasporto_1;
ALTER TABLE dump_allegati DISABLE CONSTRAINT fk_dump_allegati_1;
ALTER TABLE dump_header_allegato DISABLE CONSTRAINT fk_dump_header_allegato_1;
ALTER TABLE dump_contenuti DISABLE CONSTRAINT fk_dump_contenuti_1;
truncate table dump_header_allegato ;
truncate table dump_allegati ;
truncate table dump_contenuti ;
truncate table dump_header_trasporto ;
truncate table DUMP_MULTIPART_HEADER;
truncate table dump_messaggi ;
ALTER TABLE DUMP_MULTIPART_HEADER ENABLE CONSTRAINT fk_dump_multipart_header_1;
ALTER TABLE dump_header_trasporto ENABLE CONSTRAINT fk_dump_header_trasporto_1;
ALTER TABLE dump_allegati ENABLE CONSTRAINT fk_dump_allegati_1;
ALTER TABLE dump_header_allegato ENABLE CONSTRAINT fk_dump_header_allegato_1;
ALTER TABLE dump_contenuti ENABLE CONSTRAINT fk_dump_contenuti_1;



ALTER TABLE lista_riscontri DISABLE CONSTRAINT fk_LISTA_RISCONTRI_1;
ALTER TABLE lista_trasmissioni DISABLE CONSTRAINT fk_LISTA_TRASMISSIONI_1;
ALTER TABLE lista_eccezioni DISABLE CONSTRAINT fk_LISTA_ECCEZIONI_1;
ALTER TABLE LISTA_EXT_PROTOCOL_INFO DISABLE CONSTRAINT fk_LISTA_EXT_PROTOCOL_INFO_1; 
truncate table lista_trasmissioni ;
truncate table lista_eccezioni ;
truncate table lista_riscontri ;
truncate table LISTA_EXT_PROTOCOL_INFO;
truncate table repository_buste ;
ALTER TABLE lista_riscontri ENABLE CONSTRAINT fk_LISTA_RISCONTRI_1;
ALTER TABLE lista_trasmissioni ENABLE CONSTRAINT fk_LISTA_TRASMISSIONI_1;
ALTER TABLE lista_eccezioni ENABLE CONSTRAINT fk_LISTA_ECCEZIONI_1;
ALTER TABLE LISTA_EXT_PROTOCOL_INFO ENABLE CONSTRAINT fk_LISTA_EXT_PROTOCOL_INFO_1;


-- truncate notifiche_eventi cascade;
truncate table asincrono;
truncate table riscontri_da_ricevere;

ALTER TABLE audit_binaries DISABLE CONSTRAINT fk_audit_binaries_1;
truncate table audit_binaries;
truncate table AUDIT_OPERATIONS;
