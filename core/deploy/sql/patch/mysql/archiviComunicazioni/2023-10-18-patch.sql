INSERT INTO transazioni_classe_esiti (govway_status, govway_status_detail) VALUES ( 11 , 'Errore Client Indisponibile' );

UPDATE transazioni_esiti set govway_status_class=(select id from transazioni_classe_esiti WHERE govway_status=11) WHERE govway_status=3 AND govway_status_key='ERRORE_CONNESSIONE_CLIENT_NON_DISPONIBILE';

INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 49 , 'ERRORE_RESPONSE_TIMEOUT' , 'Read Timeout' , 'Risposta non ricevuta entro il timeout specificato' , (select id from transazioni_classe_esiti WHERE govway_status=4) );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 50 , 'ERRORE_REQUEST_TIMEOUT' , 'Request Read Timeout' , 'Richiesta non ricevuta entro il timeout specificato' , (select id from transazioni_classe_esiti WHERE govway_status=11) );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 51 , 'ERRORE_CONNECTION_TIMEOUT' , 'Connection Timeout' , 'Connessione non stabilita entro il timeout specificato.' , (select id from transazioni_classe_esiti WHERE govway_status=4) );
INSERT INTO transazioni_esiti (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( 52 , 'ERRORE_NEGOZIAZIONE_TOKEN' , 'Negoziazione Token Fallita' , 'Sono emersi degli errori durante la negoziazione del token' , (select id from transazioni_classe_esiti WHERE govway_status=4) );

