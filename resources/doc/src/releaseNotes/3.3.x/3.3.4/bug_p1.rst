.. _3.3.4.1_bug:

Bug Fix 3.3.4.p1
----------------

Sono stati risolti i seguenti bug:

- corretto problema introdotto nella versione 3.3.4 con l'ottimizzazione dei messaggi soap: le richieste che presentavano un carattere '\\n', '\\r' o '\\t' nel root-element causavano un fallimento del parser il quale riportava erroneamente un errore simile al seguente:
	
	Caused by: org.xml.sax.SAXParseException: The end-tag for element type "rootElementName" must end with a '>' delimiter

- nei casi suddetti, in cui il parser andava in errore, la funzionalità di registrazione dei messaggi salvava un contenuto incompleto;

- l'header HTTP SOAPAction non risultava modificabile tramite una trasformazione;

- aggiunto file 'govway_transazioni_slow.log' utilizzabile per registrare le operazioni effettuate sulla base dati del tracciamento (registrazione log, controllo duplicati) che impiegano un tempo maggiore di una soglia prefissata;

- resa possibile la personalizzazione del nome dell'header HTTP 'GovWay-Transaction-ErrorType' e del namespace associato al faultCode su SOAPFault 1.2. Aggiunta inoltre la possibilità di non generare il claim 'type' in un Problem Detail.

Per la console di gestione sono stati risolti i seguenti bug:

- il bottone 'i' di informazione presente negli elementi 'checkbox' veniva erroneamente visualizzato spostato sulla destra;

- su un'installazione Tomcat, la sezione 'Runtime' non visualizzava correttamente le informazioni relative alle connessioni attive del database di configurazione;

- se nel controllo degli accessi veniva configurata un'autenticazione 'plugin', non venivano visualizzati i link necessari a registrare puntualmente gli applicativi e i soggetti.

Per la console di monitoraggio sono stati risolti i seguenti bug:

- risolta problematica che si presentava saltuariamente sul server.log dell'application server, riportando un errore simile al seguente:

	SEVERE [facelets.viewhandler] (default task-5) Error Rendering View[/transazioni/pages/form/dettagliMessaggioTab.xhtml]: java.lang.IllegalArgumentException: could not find dataTable with id 'diagnosticiTable_tbl'

Sull'installer è stato corretto il seguente bug:

- utilizzando la modalità avanzata con ambiente dedicato per la gestione e il monitoraggio, se veniva indicato l'utilizzo di uno schema dedicato per le informazioni statistiche, tra i datasource del runtime non veniva generato quello richiesto dal Controllo del Traffico per accedere alle informazioni statistiche.

