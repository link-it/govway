.. _mon_dettaglioErrore:

Dettaglio dell'errore
~~~~~~~~~~~~~~~~~~~~~~

Nel dettaglio di una transazione (:ref:`mon_dettaglio_transazione`) viene riportato l'errore principale che ha causato il fallimento della gestione della transazione.

L'informazione viene calcolata seguendo la logica riportata di seguito.

- se l'esito della transazione è 'Fault Applicativo' (codice 2, vedi sezione :ref:`mon_esito_transazione`) il dettaglio dell'errore viene valorizzato rispetto al ruolo della transazione:

	- in caso di erogazione di API viene utilizzato il fault ricevuto dal backend;

	- in caso di fruizione di API viene utilizzato il fault ricevuto dal dominio esterno.

- altrimenti vengono analizzati i diagnostici di livello 'errore' emessi, ordinati per data di emissione crescente:

	- se l'esito della transazione non rientra nei gruppi 'Completata con Successo' e 'Fault Applicativo' (vedi sezione :ref:`mon_esito_transazione`) e il diagnostico emesso riguarda funzionalità configurate in modalità 'warning' (maggiori informazioni in fondo alla pagina), il diagnostico viene scartato e l'analisi prosegue con il prossimo diagnostico di livello 'error'; 

	- se l'esito della transazione rientra nel gruppo 'Errori Processamento Risposta' (vedi sezione :ref:`mon_esito_transazione`) e il diagnostico emesso riguarda errori generati sul connettore verso il backend (indicati in fondo alla pagina), il diagnostico viene temporaneamente marcato come 'MESSAGGIO_ERRORE_CONNESSIONE' e l'analisi prosegue sul successivo diagnostico;

	- se il diagnostico emesso riguarda la segnalazione di una risposta errore ritornata al client (codici riportati in fondo alla pagina) il diagnostico viene temporaneamente marcato come 'MESSAGGIO_RISPOSTA_ERRORE' e l'analisi prosegue sul successivo diagnostico;

	- altrimenti l'analisi termina e il messaggio di errore riportato nel diagnostico viene utilizzato come errore di dettaglio.

- se il processamento dei diagnostici di livello 'errore' non ha individuato un errore di dettaglio, vengono analizzati eventuali diagnostici marcati durante l'analisi e se presenti vengono utilizzati come errore di dettaglio. L'ordine di utilizzo è il seguente: 

	- se presente viene utilizzato il diagnostico marcato come 'MESSAGGIO_ERRORE_CONNESSIONE';

	- altrimenti il diagnostico 'MESSAGGIO_RISPOSTA_ERRORE'.

- infine se non è stato individuato un errore di dettaglio viene verificata l'eventuale presenza di un fault applicativo registrato:

	- in caso di erogazione di API viene utilizzato il fault ricevuto dal backend, se presente, altrimenti l'eventuale fault ritornato al client;

	- in caso di fruizione di API viene utilizzato il fault ricevuto dal dominio esterno, se presente, altrimenti l'eventuale fault ritornato al client.


I diagnostici emessi, rispettivamente su una fruizione ed una erogazione, per funzionalità configurata in modalità 'warning' possiedono i seguenti codici:

- 001069, 004092: policy rate limiting violata;
- 001072, 004095: numero massimo di richieste complessive raggiunto;
- 001084, 004107: validazione jwt di un token fallita;
- 001091, 004114: validazione di un token, tramite servizio di introspection, fallita;
- 001098, 004121: validazione di un token, tramite servizio 'userInfo', fallita;
- 001108, 004131: validazione dei contenuti della richiesta;
- 003060, 007059: validazione dei contenuti della risposta;
- 001122, 004145: charset della richiesta non atteso;
- 001123, 004146: charset della risposta non atteso.

I diagnostici emessi, rispettivamente su una fruizione ed una erogazione, che riguardano errori generati sul connettore verso il backend possiedono i seguenti codici:

- 003008, 007013: la comunicazione verso il backend è fallita e nel diagnostico viene riportato il codice http di risposta e/o la motivazione dell'errore;
- 003013, 007014: il backend ha restituito un SOAPFault (API SOAP)
- 003059, 007058: il backend ha restituito un Problem Detail RFC 7807 (API REST)

I diagnostici emessi che riguardano la segnalazione di una risposta errore ritornata al client possiedono i seguenti codici:

- 001008, 004007: segnala una transazione non terminata con codice http 200 verso il client, rispettivamente su una fruizione ed una erogazione;
- 001033: segnalazione identica alla precedente, effettuata via servizio Integration Manager;
- 004008: simile al diagnostico '004007', segnala inoltre che il soggetto destinatario della richiesta non è stato identificato;
- 004080: simile al diagnostico '004007', dove il mittente non è stato però identificato e quindi non viene riportato nel diagnostico.
