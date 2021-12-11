.. _3.3.5.1_bug:

Bug Fix 3.3.5.p1
----------------

Sono stati effettuati i seguenti interventi migliorativi degli aspetti prestazionali:

- gli oggetti java.util.Hashtable e java.util.Vector sono stati sostituiti con strutture più efficienti;

- sostituiti i blocchi synchronized con l'uso di java.util.concurrent.Semaphore, relativamente alla negoziazione di connessioni e alla funzionalità di filtro dei duplicati;

- gli oggetti restituiti dalle factory dei profili di interoperabilità che contengono solamente configurazioni statiche, vengono adesso istanziate solamente una volta all'avvio del gateway;

- il recupero dell'identificativo della PrimaryKey di una nuova entry, avviene adesso, anche su postgresql, utilizzando la funzionalità 'getGeneratedKeys' fornita dai driver jdbc postgresql con versione superiore alla 9.4;

- il provider 'Bouncy Castle' viene adesso utilizzato per gestire i certificati (java.security.cert.CertificateFactory e java.security.cert.CertPathValidator) e per calcolare il digest di un messaggio (java.security.MessageDigest).

Sono stati risolti i seguenti bug:

- nel profilo di interoperabilità 'ModI' con API REST configurata con pattern 'Integrity', la validazione degli header firmati non rilevava, in presenza di molteplici header HTTP con lo stesso nome, l'esistenza di un valore ulteriore rispetto a tutti quelli definiti all'interno del claim 'signed_headers';

- se venivano ricevuti messaggi SOAP che iniziavano con il carattere '>' l'anomalia veniva correttamente segnalata al client, ma nel log veniva emessa un'eccezione relativa ad un caso non gestito (NullPointerException);

- aggiunta gestione del charset nella classe 'OpenSPCoop2MessageSoapStreamReader' utilizzata per leggere le informazioni SOAP in streaming;

- risolto bug di serializzazione di un messaggio SOAP With Attachments, che si presentava quando il messaggio veniva acceduto per funzionalità read-only (es. correlazione applicativa), che provocava la generazione di un content-type con un mimepart differente da quello effettivamente serializzato;

- la normalizzazione dell'input stream 'vuoto', viene adesso gestita tramite l'utilizzo di un buffer di lunghezza 2 compatibile con il charset 'UTF-16';

- corretti problemi presenti sulla funzionalità 'follow-redirect' che non consentivano di ottenere una risposta applicativa una volta seguito il flusso di redirect;

- risolti problemi presenti nella funzione di merge degli schemi OpenAPI:

	- l'import di file i cui nomi erano uno inclusivo dell'altro causavano una serializzazione di un path scorretto;

	- la serializzazione YAML dell'interfaccia generava alcune enumeration (es. in security schema) con i valori maiuscoli invece che minuscoli come atteso dalla specifica OpenAPI;

- in caso l'immagine del controllo del traffico, salvata durante uno shutdown dell'A.S., risultava corrotta, il gateway non ripartiva; è stata migliorata la gestione facendo in modo che il gateway riparta con stato iniziale vuoto e segnali l'anomalia su file di log.

- in presenza di richieste malformate che causavano la generazione di un diagnostico contenente il carattere '\u0000', su ambienti con database di tipo Postgresql si otteneva un errore simile al seguente durante il tracciamento: Caused by: org.postgresql.util.PSQLException: ERROR: invalid byte sequence for encoding "UTF8": 0x0.

È stato inoltre migliorato il sistema di log, relativamente alle seguenti casistiche:

- aggiunto diagnostico che evidenzia la ricezione di richieste o risposte con un charset differente da quello definito per default nel prodotto (qualsiasi charset per messaggi REST e solamente il charset UTF-8 per messaggi SOAP);

- aggiunta verifica che le connessioni prelevate dal datasource siano con autocommit disabilitato e con livello di serializzazione atteso (ReadCommitted);

- il file di log 'govway_transazioni_slow.log' è stato arricchito con le seguenti informazioni:

	- dettaglio sulla fase in cui viene speso il tempo nella costruzione delle informazioni da salvare in fase di scrittura della transazione;

	- informazioni relative alla gestione del rate limiting applicabile in seguito al completamento della richiesta;

- migliorati i log di eventuali errori emersi durante la gestione degli handler.

Per la console di gestione sono stati risolti i seguenti bug:

- una connessione al database veniva acceduta dalla console anche per gestire risorse statiche non protette (es. js/autocomplete.js o css/linkit-base.css);

- corretto bug presente nella funzionalità di export e import di un'API che, in alcune configurazioni particolari, poteva non preservare la configurazione del profilo di collaborazione, del filtro duplicati, del riferimento alla richiesta e dell'id di conversazione;

- la ricerca delle operazioni effettuate sulla console, tramite la funzionalità di 'Auditing', avviene adesso tramite criteri "contains case insensitive". Inoltre nella lista delle operazioni individuate è stata aggiunta la data di esecuzione dell'operazione.


Per la console di monitoraggio sono stati risolti i seguenti bug:

- le pagine xhtml presentavano erroneamente campi 'date' con un time zone 'forzato' a 'Europe/Rome';

- aggiunta la possibilità di selezionare in blocco gli elementi visualizzati nello storico delle transazioni;

- nell'export CSV delle transazioni sono stati aggiunte le seguenti informazioni mancanti:

	- richiedente;
	- dettaglio dell'errore o dell'anomalia;
	- informazioni principali estratte dal token: subject, issuer, clientId, username ed indirizzo eMail.


Sull'installer è stato corretto il seguente bug:

- nei binari prodotti dall'installer, il timer per la consegna dei messaggi presi in carico viene adesso disabilitato essendo la funzionalità considerata 'sperimentale'.


