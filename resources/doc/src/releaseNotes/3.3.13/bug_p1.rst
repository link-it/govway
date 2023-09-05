.. _3.3.13.1_bug:

Bug Fix 3.3.13.p1
------------------

Sono state risolte le seguenti vulnerabilità relative ai jar di terza parte:

- CVE-2023-2976: aggiornata libreria 'com.google.guava:guava' alla versione 32.1.1-jre;

- CVE-2023-34034: aggiornata libreria 'org.springframework.security:\*' alla versione 5.8.5;

- CVE-2023-34462: aggiornata libreria 'io.netty:\*' alla versione 4.1.94.Final.

Sono stati risolti i seguenti bug:

- corrette le seguenti anomalie relative al profilo di interoperabilità 'ModI': 
	
	- risultavano le stesse informazioni sulle organizzazioni prelevate dalla PDND relativamente a chiamate (clientId) differenti;

	- dopo un upgrade all'ultima versione 3.3.13 si otteneva l'errore "[GOVWAY-6] - EccezioneProcessamento: per abilitare la proprietà è richiesto che sia abilitata la gestione delle chiavi PDND" se il file di proprietà esterno 'govway_local.properties' non veniva aggiornato con le differenze introdotte nell'ultima versione riguardanti la gestione delle chiavi 'PDND'. La problematica è stata risolta in modo che l'errore non avvenga anche se non vengono aggiornati i file locali;

- nel profilo di interoperabilità 'SPCoop' è stato rivisto l'utilizzo dell'identificativo numerico del nodo in modo da utilizzare il "padding" corretto in presenza di 2 cifre;

- utilizzando la configurazione di Apache suggerita per inoltrare il certificato TLS ('RequestHeader set SSL_CLIENT_CERT "%{SSL_CLIENT_CERT}s" "expr=-n %{SSL_CLIENT_CERT}"') avviene un inoltro dell'header in una formato che non veniva supportato da GovWay: PEM su una unica linea dove i ritorni a capo venivano sostituiti da spazi. È stato aggiunto il supporto.

Sono state corrette alcune anomalie riguardanti la consegna asincrona:

	- i messaggi serializzati su database contenevano informazioni "inconsistenti" se utilizzati dopo un upgrade di versione di GovWay. In particolar modo l'identificativo di protocollo (trasparente, modi, spcoop...) non veniva risolto correttamente causando una mancata registrazione della diagnostica, dei messaggi e dei dati relativi alla consegna asincrona (nella transazione) per le nuove consegne effettuate con la versione del software aggiornata;

	- l'informazione di contesto sul nome della porta invocata non era presente in un messaggio trasformato e causava un errore simile al seguente: "Errore avvenuto durante la consegna HTTP: Errore durante la raccolta delle informazioni necessarie alla funzione di proxy pass reverse: [getPortaApplicativa]: Parametro non definito (idPA.getNome() is null)".


Per la console di gestione sono stati risolti i seguenti bug:

- in un ambiente multi-tenant la creazione di un applicativo con credenziali api-key, dove il soggetto di dominio interno non veniva selezionato attraverso le voci in alto a destra nella console ma utilizzando la select list presente nella form di creazione, non funzionava correttamente poichè le credenziali generate venivano assegnate al soggetto presente inizialmente nella maschera di creazione e non al soggetto successivamente selezionato;

- se su una API REST venivano caricati schemi XSD, il download della 'XSD Schema Collection' produceva l'errore: "Content is not allowed in prolog.";

- nella maschera di resoconto dei dati di una trasformazione della richiesta, dopo aver effettuato una operazione di salvataggio, i link sugli header http e/o sui parametri della url non riportavano il numero corretto di header/parametri precedentemente configurati e veniva sempre indicato il valore '0';

- corretti alcuni errori che procuravano il fallimento dell'importazione di configurazione tramite 'wizard'. 

Per le API di monitoraggio sono stati risolti i seguenti bug:

- nel dettaglio di una transazione veniva restituito un elemento vuoto 'informazioni_token:{}' nel caso in cui la gestione della richiesta non prevedesse un token; se non valorizzato adesso l'elemento non viene prodotto.
