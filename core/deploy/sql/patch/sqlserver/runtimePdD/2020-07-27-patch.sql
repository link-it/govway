ALTER TABLE MSG_SERVIZI_APPLICATIVI ADD ORA_REGISTRAZIONE DATETIME2;
UPDATE MSG_SERVIZI_APPLICATIVI SET ORA_REGISTRAZIONE=(select MESSAGGI.ORA_REGISTRAZIONE FROM MESSAGGI WHERE MESSAGGI.ID_MESSAGGIO=MSG_SERVIZI_APPLICATIVI.ID_MESSAGGIO AND MESSAGGI.TIPO=MSG_SERVIZI_APPLICATIVI.TIPO);
ALTER TABLE MSG_SERVIZI_APPLICATIVI ALTER COLUMN ORA_REGISTRAZIONE DATETIME2 NOT NULL;

ALTER TABLE DEFINIZIONE_MESSAGGI ADD ORA_REGISTRAZIONE DATETIME2;
UPDATE DEFINIZIONE_MESSAGGI SET ORA_REGISTRAZIONE=(select MESSAGGI.ORA_REGISTRAZIONE FROM MESSAGGI WHERE MESSAGGI.ID_MESSAGGIO=DEFINIZIONE_MESSAGGI.ID_MESSAGGIO AND MESSAGGI.TIPO=DEFINIZIONE_MESSAGGI.TIPO);
ALTER TABLE DEFINIZIONE_MESSAGGI ALTER COLUMN ORA_REGISTRAZIONE DATETIME2 NOT NULL;

ALTER TABLE REPOSITORY_BUSTE ADD DATA_REGISTRAZIONE DATETIME2;
UPDATE REPOSITORY_BUSTE SET DATA_REGISTRAZIONE=(select MESSAGGI.ORA_REGISTRAZIONE FROM MESSAGGI WHERE MESSAGGI.ID_MESSAGGIO=REPOSITORY_BUSTE.ID_MESSAGGIO AND MESSAGGI.TIPO=REPOSITORY_BUSTE.TIPO);
ALTER TABLE REPOSITORY_BUSTE ALTER COLUMN DATA_REGISTRAZIONE DATETIME2 NOT NULL;

ALTER TABLE LISTA_RISCONTRI ADD DATA_REGISTRAZIONE DATETIME2;
UPDATE LISTA_RISCONTRI SET DATA_REGISTRAZIONE=(select MESSAGGI.ORA_REGISTRAZIONE FROM MESSAGGI WHERE MESSAGGI.ID_MESSAGGIO=LISTA_RISCONTRI.ID_MESSAGGIO AND MESSAGGI.TIPO=LISTA_RISCONTRI.TIPO);
ALTER TABLE LISTA_RISCONTRI ALTER COLUMN DATA_REGISTRAZIONE DATETIME2 NOT NULL;

ALTER TABLE LISTA_TRASMISSIONI ADD DATA_REGISTRAZIONE DATETIME2;
UPDATE LISTA_TRASMISSIONI SET DATA_REGISTRAZIONE=(select MESSAGGI.ORA_REGISTRAZIONE FROM MESSAGGI WHERE MESSAGGI.ID_MESSAGGIO=LISTA_TRASMISSIONI.ID_MESSAGGIO AND MESSAGGI.TIPO=LISTA_TRASMISSIONI.TIPO);
ALTER TABLE LISTA_TRASMISSIONI ALTER COLUMN DATA_REGISTRAZIONE DATETIME2 NOT NULL;

ALTER TABLE LISTA_ECCEZIONI ADD DATA_REGISTRAZIONE DATETIME2;
UPDATE LISTA_ECCEZIONI SET DATA_REGISTRAZIONE=(select MESSAGGI.ORA_REGISTRAZIONE FROM MESSAGGI WHERE MESSAGGI.ID_MESSAGGIO=LISTA_ECCEZIONI.ID_MESSAGGIO AND MESSAGGI.TIPO=LISTA_ECCEZIONI.TIPO);
ALTER TABLE LISTA_ECCEZIONI ALTER COLUMN DATA_REGISTRAZIONE DATETIME2 NOT NULL;

ALTER TABLE LISTA_EXT_PROTOCOL_INFO ADD DATA_REGISTRAZIONE DATETIME2;
UPDATE LISTA_EXT_PROTOCOL_INFO SET DATA_REGISTRAZIONE=(select MESSAGGI.ORA_REGISTRAZIONE FROM MESSAGGI WHERE MESSAGGI.ID_MESSAGGIO=LISTA_EXT_PROTOCOL_INFO.ID_MESSAGGIO AND MESSAGGI.TIPO=LISTA_EXT_PROTOCOL_INFO.TIPO);
ALTER TABLE LISTA_EXT_PROTOCOL_INFO ALTER COLUMN DATA_REGISTRAZIONE DATETIME2 NOT NULL;

ALTER TABLE SEQUENZA_DA_INVIARE ADD DATA_REGISTRAZIONE DATETIME2;
UPDATE SEQUENZA_DA_INVIARE SET DATA_REGISTRAZIONE=CURRENT_DATETIME2;
ALTER TABLE SEQUENZA_DA_INVIARE ALTER COLUMN DATA_REGISTRAZIONE DATETIME2 NOT NULL;

ALTER TABLE SEQUENZA_DA_RICEVERE ADD DATA_REGISTRAZIONE DATETIME2;
UPDATE SEQUENZA_DA_RICEVERE SET DATA_REGISTRAZIONE=CURRENT_DATETIME2;
ALTER TABLE SEQUENZA_DA_RICEVERE ALTER COLUMN DATA_REGISTRAZIONE DATETIME2 NOT NULL;


-- CREATE INDEX REP_BUSTE_DATAREG ON REPOSITORY_BUSTE (DATA_REGISTRAZIONE,TIPO,HISTORY,PROFILO,PDD);
CREATE INDEX REP_BUSTE_DATAREG_RA ON REPOSITORY_BUSTE (DATA_REGISTRAZIONE,TIPO,REPOSITORY_ACCESS);
DROP INDEX REP_BUSTE_SEARCH ON REPOSITORY_BUSTE;
DROP INDEX REP_BUSTE_SEARCH_TIPO ON REPOSITORY_BUSTE;
