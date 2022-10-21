CREATE SEQUENCE seq_transazioni_esiti start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 CYCLE;

CREATE TABLE transazioni_esiti
(
	-- Codice numerico dell'esito della transazione
	govway_status INT NOT NULL,
	-- Identificativo dell'esito della transazione
	govway_status_key VARCHAR(255) NOT NULL,
	-- Frase dell'errore che identifica l'esito della transazione
	govway_status_detail VARCHAR(255) NOT NULL,
	-- Descrizione dell'esito della transazione
	govway_status_description VARCHAR(255) NOT NULL,
	-- Codice numerico della classe di esiti a cui appartiene la transazione
	govway_status_class INT NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_transazioni_esiti') NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_tr_esiti_1 UNIQUE (govway_status),
	CONSTRAINT uniq_tr_esiti_2 UNIQUE (govway_status_key),
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_esiti PRIMARY KEY (id)
);




CREATE SEQUENCE seq_transazioni_classe_esiti start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 CYCLE;

CREATE TABLE transazioni_classe_esiti
(
	-- Codice numerico della classe di appartenenza di un esito della transazione
	govway_status INT NOT NULL,
	-- Descrizione della classe di un esito
	govway_status_detail VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_transazioni_classe_esiti') NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_tr_classe_esiti_1 UNIQUE (govway_status),
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_classe_esiti PRIMARY KEY (id)
);


-- classe esiti
INSERT INTO transazioni_classe_esiti (id, govway_status, govway_status_detail) VALUES ( 1 , 1 , 'Completata con Successo' );
INSERT INTO transazioni_classe_esiti (id, govway_status, govway_status_detail) VALUES ( 2 , 2 , 'Fault Applicativo' );
INSERT INTO transazioni_classe_esiti (id, govway_status, govway_status_detail) VALUES ( 3 , 3 , 'Richiesta Scartata' );
INSERT INTO transazioni_classe_esiti (id, govway_status, govway_status_detail) VALUES ( 4 , 4 , 'Errore di Consegna' );
INSERT INTO transazioni_classe_esiti (id, govway_status, govway_status_detail) VALUES ( 5 , 5 , 'Autorizzazione Negata' );
INSERT INTO transazioni_classe_esiti (id, govway_status, govway_status_detail) VALUES ( 6 , 6 , 'Policy Controllo Traffico Violate' );
INSERT INTO transazioni_classe_esiti (id, govway_status, govway_status_detail) VALUES ( 7 , 7 , 'Errori Servizio IntegrationManager/MessageBox' );
INSERT INTO transazioni_classe_esiti (id, govway_status, govway_status_detail) VALUES ( 8 , 8 , 'Errori Processamento Richiesta' );
INSERT INTO transazioni_classe_esiti (id, govway_status, govway_status_detail) VALUES ( 9 , 9 , 'Errori Processamento Risposta' );
INSERT INTO transazioni_classe_esiti (id, govway_status, govway_status_detail) VALUES ( 10 , 10 , 'Errore Generico' );

-- esiti
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 0 , 'OK' , 'Ok' , 'Transazione gestita con successo' , 1 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 1 , 'ERRORE_PROTOCOLLO' , 'Errore di Protocollo' , 'Transazione fallita a causa di un errore avvenuto durante la gestione del Profilo di Interoperabilità' , 10 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 2 , 'ERRORE_APPLICATIVO' , 'Fault Applicativo' , 'La risposta applicativa contiene un Fault Applicativo' , 2 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 3 , 'ERRORE_CONNESSIONE_CLIENT_NON_DISPONIBILE' , 'Connessione Client Interrotta' , 'La connessione del Client che ha scaturito la richiesta non è più disponibile' , 10 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 4 , 'ERRORE_PROCESSAMENTO_PDD_4XX' , 'Richiesta Malformata' , 'Errore causato da informazioni errate fornite dal client' , 3 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 5 , 'ERRORE_PROCESSAMENTO_PDD_5XX' , 'Errore Generico' , 'Errore causato da risorse non disponibili o problemi interni del gateway' , 10 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 6 , 'AUTENTICAZIONE_FALLITA' , 'Autenticazione Fallita [I.M.]' , 'Autenticazione fallita (servizio di MessageBox dell''Integration Manager)' , 7 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 7 , 'AUTORIZZAZIONE_FALLITA' , 'Autorizzazione Fallita [I.M.]' , 'Autorizzazione fallita (servizio di MessageBox dell''Integration Manager)' , 7 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 8 , 'MESSAGGI_NON_PRESENTI' , 'Messaggi non presenti [I.M.]' , 'Messaggi non presenti (servizio di MessageBox dell''Integration Manager)' , 1 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 9 , 'MESSAGGIO_NON_TROVATO' , 'Messaggio non trovato [I.M.]' , 'Messaggio non trovato (servizio di MessageBox dell''Integration Manager)' , 7 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 10 , 'ERRORE_INVOCAZIONE' , 'Errore di Connessione' , 'Messaggio non inoltrabile al destinatario a causa di problemi di connessione ' , 4 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 11 , 'ERRORE_SERVER' , 'Fault senza Informazioni Protocollo' , 'SOAP Fault ritornato dall''Erogatore senza essere contenuto in un messaggio previsto dal Profilo di Interoperabilità' , 4 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 12 , 'OK_PRESENZA_ANOMALIE' , 'Ok (Presenza Anomalie)' , 'Transazione gestito con successo (sono stati emessi dei diagnostici che hanno rilevato delle anomalie)' , 1 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 13 , 'CONTENUTO_RICHIESTA_NON_RICONOSCIUTO' , 'Contenuto Richiesta Malformato' , 'Il contenuto della richiesta non è processabile dal Gateway' , 3 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 14 , 'CONTENUTO_RISPOSTA_NON_RICONOSCIUTO' , 'Contenuto Risposta Malformato' , 'Il contenuto della risposta non è processabile dal Gateway' , 9 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 15 , 'ERRORE_TOKEN' , 'Gestione Token Fallita' , 'Sono emersi degli errori durante la gestione/validazione del token' , 3 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 16 , 'ERRORE_AUTENTICAZIONE' , 'Autenticazione Fallita' , 'L''autenticazione del richiedente non è stata effettuata con successo' , 3 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 17 , 'ERRORE_AUTORIZZAZIONE' , 'Autorizzazione Negata' , 'La richiesta non è stata autorizzata' , 5 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 18 , 'CONTROLLO_TRAFFICO_POLICY_VIOLATA' , 'Violazione Rate Limiting' , 'Rilevata una violazione di una Policy di Rate Limiting' , 6 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 19 , 'CONTROLLO_TRAFFICO_POLICY_VIOLATA_WARNING_ONLY' , 'Violazione Rate Limiting WarningOnly' , 'Rilevata una violazione di una Policy di Rate Limiting (configurata in WarningOnly)' , 1 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 20 , 'CONTROLLO_TRAFFICO_MAX_THREADS' , 'Superamento Limite Richieste' , 'Rilevato il superamento del numero massimo di richieste simultanee consentite sul gateway' , 6 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 21 , 'CONTROLLO_TRAFFICO_MAX_THREADS_WARNING_ONLY' , 'Superamento Limite Richieste WarningOnly' , 'Superamento numero massimo di richieste simultanee consentite sul gateway (controllo configurato in WarningOnly)' , 1 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 22 , 'ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA' , 'Sicurezza Messaggio Richiesta Fallita' , 'La gestione della sicurezza messaggio sulla richiesta non è stata completata con successo' , 8 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 23 , 'ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA' , 'Sicurezza Messaggio Risposta Fallita' , 'La gestione della sicurezza messaggio sulla risposta non è stata completata con successo' , 9 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 24 , 'ERRORE_ALLEGATI_MESSAGGIO_RICHIESTA' , 'Gestione Allegati Richiesta Fallita' , 'La gestione degli allegati sulla richiesta non è stata completata con successo' , 8 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 25 , 'ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA' , 'Gestione Allegati Risposta Fallita' , 'La gestione degli allegati sulla risposta non è stata completata con successo' , 9 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 26 , 'ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA' , 'Correlazione Applicativa Richiesta Fallita' , 'La gestione della correlazione applicativa sulla richiesta non è stata completata con successo' , 8 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 27 , 'ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA' , 'Correlazione Applicativa Risposta Fallita' , 'La gestione della correlazione applicativa sulla risposta non è stata completata con successo' , 9 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 28 , 'HTTP_3xx' , 'Risposta HTTP 3xx' , 'La risposta applicativa contiene un codice di trasporto 3xx' , 1 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 29 , 'HTTP_4xx' , 'Risposta HTTP 4xx' , 'La risposta applicativa contiene un codice di trasporto 4xx' , 4 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 30 , 'HTTP_5xx' , 'Risposta HTTP 5xx' , 'La risposta applicativa contiene un codice di trasporto 5xx' , 4 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 31 , 'ERRORE_VALIDAZIONE_RICHIESTA' , 'Validazione Richiesta Fallita' , 'La validazione della richiesta non è stata completata con successo' , 8 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 32 , 'ERRORE_VALIDAZIONE_RISPOSTA' , 'Validazione Risposta Fallita' , 'La validazione della risposta non è stata completata con successo' , 9 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 33 , 'ERRORE_SOSPENSIONE' , 'API Sospesa' , 'L''API invocata risulta sospesa' , 3 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 34 , 'CORS_PREFLIGHT_REQUEST_VIA_GATEWAY' , 'CORS Preflight Gestione Gateway' , 'Richiesta OPTIONS CORS Preflight Request gestita dal Gateway' , 1 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 35 , 'CORS_PREFLIGHT_REQUEST_TRASPARENTE' , 'CORS Preflight Gestione Applicativa' , 'Richiesta OPTIONS CORS Preflight Request gestita dall''applicativo' , 1 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 36 , 'ERRORE_TRASFORMAZIONE_RICHIESTA' , 'Trasformazione Richiesta Fallita' , 'La trasformazione della richiesta non è stata completata con successo' , 8 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 37 , 'ERRORE_TRASFORMAZIONE_RISPOSTA' , 'Trasformazione Risposta Fallita' , 'La trasformazione della risposta non è stata completata con successo' , 9 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 38 , 'CONSEGNA_MULTIPLA' , 'Consegna Asincrona in Coda' , 'Tutte le consegne verso i connettori associati all''API sono ancora in attesa di essere consegnate' , 1 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 39 , 'CONSEGNA_MULTIPLA_COMPLETATA' , 'Consegna Asincrona Completata' , 'La richiesta pervenuta è stata inoltrata correttamente a tutti i connettori associati all''API' , 1 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 40 , 'CONSEGNA_MULTIPLA_FALLITA' , 'Consegna Asincrona Fallita' , 'La richiesta pervenuta è stata inoltrata a tutti i connettori associati all''API; in alcuni casi la consegna non è andata a buon fine' , 4 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 41 , 'TOKEN_NON_PRESENTE' , 'Token non Presente' , 'La richiesta non presenta un token' , 3 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 42 , 'ERRORE_AUTENTICAZIONE_TOKEN' , 'Autenticazione Token Fallita' , 'Nel token ricevuto non sono presenti dei claim richiesti per l''accesso all''API invocata' , 3 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 43 , 'API_NON_INDIVIDUATA' , 'API non Individuata' , 'La richiesta non permette di individuare una API registrata sul Gateway' , 3 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 44 , 'OPERAZIONE_NON_INDIVIDUATA' , 'Operazione non Individuata' , 'La richiesta non indirizza un''operazione esistente sull''API invocata' , 3 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 45 , 'RICHIESTA_DUPLICATA' , 'Richiesta già elaborata' , 'La richiesta risulta essere già stata elaborata' , 8 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 46 , 'RISPOSTA_DUPLICATA' , 'Risposta già elaborata' , 'La risposta risulta essere già stata elaborata' , 9 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 47 , 'MESSAGE_BOX' , 'Disponibile in MessageBox' , 'Messaggio gestibile tramite servizio IntegrationManager' , 1 );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 48 , 'CONSEGNA_MULTIPLA_IN_CORSO' , 'Consegna Asincrona in Corso' , 'Alcune consegne verso i connettori associati all''API risultano ancora non completate' , 1 );
