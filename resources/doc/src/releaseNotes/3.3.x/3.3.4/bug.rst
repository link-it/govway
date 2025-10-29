Bug Fix
-------

Sono stati risolti i seguenti bug:

- In presenza di parametri della query con lo stesso nome, veniva inoltrato all'API di backend solamente il primo. Analogo problema si verificava con gli header http (es. Set-Cookie).

- Le richieste contenenti Content-Type strutturalmente non corretti non venivano registrate nello storico delle transazioni e ai client veniva restituito una pagina html su codice di risposta 404.
  La problematica è stata risolta: tutte le richieste vengono adesso registrate e ai client viene ritornato un errore 'Bad Request' conforme alla specifica REST o SOAP dell'API invocata.
	
- Risolte le seguenti anomalie presenti sulla validazione tramite OpenAPI 3:

	- se il Content-Type di una risposta veniva definito tramite placeholders \*/\*, la validazione terminava con un errore "Content-Type non supportato";

	- vengono adesso supportate le varie modalità di serializzazione dei parametri della query e degli header HTTP descritte in 'https://swagger.io/docs/specification/serialization/';

	- se gli oggetti erano definito nell'OpenAPI con 'nullable: true' e venivano quindi serializzati con il valore null la validazione sollevava erroneamente un problema di mancanza degli elementi obbligatori definiti per l'oggetto;

	- corretti i problemi che causavano una errata validazione delle interfacce OpenAPI 3.0.x contenenti il claim 'allowEmptyValue' o security schema vuoti ("security": [ {} ]);

	- se uno degli oggetti indicati nell'enumerazione oneOf di un discriminator veniva dichiarato tramite costrutto 'allOf' la validazione falliva erroneamente indicando un errore simile al seguente: "components.schemas.xxx.properties.xxx.discriminator: The discriminator 'nomeDiscriminator' is not required or not a property of the allOf schemas (code: 133)". 

- Risolte le seguenti anomalie presenti sul Rate Limiting:

	- durante la valutazione di una policy configurata per essere applicata solamente in presenza di degrado prestazionale, si verificava un errore inatteso dovuto ad un accesso fallito alla base dati statistica a causa di una inizializzazione errata;

	- in presenza di una policy con filtro basato su chiave estratta dal contenuto, la valutazione della policy terminava con errore, invece di essere semplicemente filtrata, nel caso la richiesta non contenesse un payload http (es. GET);

	- le politiche basate sulle richieste simultanee potevano bloccare un numero maggiore di richieste rispetto a quelle permesse;

	- risolto problema sull'aggiornamento dell'intervallo temporale corrente che in alcuni casi provocava una mancata generazione dell'header X-\*-Reset;

	- le politiche con metrica 'Numero Richieste Completate con Successo', 'Numero Richieste Fallite' e 'Numero Fault Applicativi' consentivano erroneamente il transito della N+1 richiesta, quando il limite era N;

	- utilizzando la metrica 'Numero Richieste Fallite' o 'Fault Applicativi' non veniva generato l'header HTTP '\*-Limit' che segnala il numero massimo di richieste effettuabili;

	- con la metrica 'Numero Richieste Completate con successo' venivano erroneamente conteggiate anche le richieste terminate con esito 'Fault Applicativo';

	- sono stati riviste le pagine html generate insieme al codice HTTP 429 su API SOAP, in seguito a errori 'Too Many Requests' e 'Limit Exceeded'; è stato aggiunto l'header http 'GovWay-Transaction-ErrorType' mancante;

	- risolta anomalia che poteva causare un deadlock nell'utilizzo delle connessioni se in una API veniva definita una policy applicabile solamente in presenza di degrado prestazionale.

- Risolto problema che si presentava durante la negoziazione di un token, se l'url dell'AuthorizationServer ritornava un codice http 3xx con header Location. L'errore segnalato nei diagnostici era il seguente: "Errore durante l'aggiornamento dell'header 'Location' attraverso la funzione di proxy pass reverse: [getAccordoServizioParteComune]: Parametro non definito"

- La chiave utilizzata per l'aggiunta in cache di un'autorizzazione non teneva conto del token OAuth2.

- Su API SOAP, in alcuni casi di errore, non veniva individuato correttamente l'esito della transazione associando un errato esito 'SOAPFault'.

- Risolti problemi di serializzazione relativo all'elemento 'details', per le fruizioni di API SOAP, in caso di errore generato tramite handler di ingresso.

- Su una installazione con più nodi in load balancer sono stati risolti i seguenti errori:

	- se arrivavano nel medesimo istante, ma su due nodi differenti, due richieste che presentano la stessa credenziale fino ad ora mai ricevuta sul gateway: "ERROR org.openspcoop2.generic_project.exception.ServiceException: Create not completed: insertAndReturnGeneratedKey failed: ORA-00001: violata restrizione di unicità (GOVWAY_TRAC.UNIQUE_CREDENZIALE_MITTENTE_1)"

	- al primo avvio del gateway, dopo l'installazione, se nell'avvio venivano fatti partire simultaneamente più nodi (installazione in cluster) avveniva un errore simile al seguente "org.openspcoop2.core.registry.driver.DriverRegistroServiziException: [DriverRegistroServiziDB::createAccordoServizioParteComune] SQLException [ERROR: duplicate key value violates unique constraint "unique_accordi_1"

- La funzione di filtro delle richieste duplicate poteva far scaturire un deadlock tra le richieste su architetture dove il tracciamento fosse dispiegato su un database differente da quello di runtime.

- È stata risolta una problematica relativa alla funzionalità 'Riferimento della Richiesta' dove non veniva associata alla traccia l'identificativo correlato.

- Risolto Bug sul Timer 'FileSystem Recovery' che tentava di ripristinare le transazioni aggiungendo un evento che causava l'errore: "il valore è troppo lungo per il tipo character varying(20)"
 
- Invocazioni GET che richiedevano un wsdl tramite il parametro '?wsdl' potevano causare un OutOfMemory sul Gateway. L'anomalia risiedeva nel fatto che tutte le richieste venivano registrate in memoria ma mai rilasciate e continuavano a crescere nel tempo. Si poteva individuare l'anomalia tramite la console di gestione, accedendo alla sezione 'Runtime - Transazioni Attive', dove veniva riportata una crescita costante del numero di transazioni.

- Corretta anomalia che si verificava durante l'identificazione dell'esito in presenza di richieste parallele ricevute non appena veniva avviato il gateway: "Identificazione stato non riuscita: null ... Caused by: java.util.ConcurrentModificationException"

- Modificato livello di serverità da ERROR a WARN per l'evento registrato nel file di log govway_core.log relativo all'invocazione di una API per la quale non esiste una specifica di interfaccia. 

- Corretto problema presente su application server wildfly che non consentiva di accedere ai parametri di una richiesta 'application/x-www-form-urlencoded' se i parametri erano un numero maggiore di uno.

- Risolto un problema di rilascio dell'input stream della risposta che si verificava se la richiesta presentava il medesimo identificativo; l'errore generato riportava il messaggio 'stream is closed'.

- La consegna con notifiche non propagava l'identità del servizio applicativo fruitore.

- Nella funzionalità di presa in carico dei messaggi (attivabile con una installazione in modalità avanzata):

	- i comandi che si occupavano di aggiornare lo stato della transazione sono stati ricondotti ad un unico comando di UPDATE con CASE e condizione di BEETWEEN per ottimizzare le query in presenza di partizioni,
	- i comandi che si occupano di selezionare i messaggi da consegnare sono stati rivisti al fine di smistare normalmente solamente i nuovi messaggi e ogni X secondi di provare a rispedire eventuali messaggio andati precedentemente in errore. È stata inoltre aggiunta una query che calcola, in caso di rispedizione dei messaggi in errore, la data del più vecchio messaggio che può essere rispedito.

- Rivisto il servizio di IntegrationManager (attivabile con una installazione in modalità avanzata): per:

	- ritornare identificativi, tramite il metodo 'getAllIdMessages', che contengano anche la data (formato: YYYYMMDDHHMMSS.sss@UUID);

	- sono state ricondotte ad un'unica query il recupero di un messaggio tramite il metodo 'getMessage';

	- aggiunto esito 'Disponibile in MessageBox' ricercabile tramite la console di monitoraggio;

	- nel dettaglio di ogni transazione sono adesso disponibili le informazioni relative allo scaricamento e all'eliminazione;

	- sono infine state migliorate le query in generale di accesso al messaggio e di eliminazione e in ogni comando è stato aggiunta la condizione BEETWEEN per ottimizzare le query in presenza di partizioni.

Per la console di gestione sono stati risolti i seguenti bug:

- La creazione o l'aggiornamento di una API tramite il caricamento dell'interfaccia OpenAPI 3.x non rilevava alcuni tipi di errore presenti nell'interfaccia (es. negli schema) e terminava con la creazione dell'API correttamente senza segnalarli. Il problema è stato risolto, e adesso vengono segnalati anche eventuali anomalie non bloccanti (es. url scorrette definite nella sezione info).

- L'aggiornamento dell'interfaccia di una API sovrascriveva eventuali impostazioni 'ModI' definite a livello della singola operazione aggiornata.

- Il semaforo che visualizza lo stato di una erogazione o fruizione considerava lo stato del gruppo 'Predefinito' anche se tutte le azioni o risorse erano state riassegnate in altri gruppi. Il problema è stato risolto.

- Le policy di RateLimiting associate ad un'erogazione si perdevano se si effettuava la modifica del nome del soggetto erogatore.
  
- In presenza di una policy di RateLimiting con raggruppamento per risorsa/azione, se veniva abilitato un filtro sulla policy, l'impostazione del raggruppamento spariva.

- L'aggiornamento del nome di un soggetto generava un errore inatteso se esistevano erogazioni o fruizioni interessate dal soggetto modificato contenenti trasformazioni configurate con applicativi o soggetti nei criteri di applicabilità.

- L'aggiornamento del nome di un soggetto non veniva correttamente propagato nelle liste dei soggetti presenti tra i criteri di applicabilità delle trasformazioni attivate su erogazioni o fruizioni e nelle regole di proxy pass che lo contenevano come criterio di applicabilità.

- L'aggiornamento del nome di un soggetto, di un ruolo, o di una erogazione/fruizione (compresa la versione) non veniva propagata sulle policy di RateLimiting, sia attivate globalmente che puntualmente su una erogazione o fruizione (i filtri che contenevano l'oggetto di modifica risultavano erroneamente disabiliti).

- La modifica del nome del soggetto, se riferito da più di 1000 erogazioni, non veniva propagata sulle erogazioni successive alla 1000-esima.

- La modifica del tipo di credenziali di un soggetto da nessuna a basic o api-key comportava una mancata visualizzazione del dialog informativo che indica di copiare e custodire attentamente le credenziali generate.

- In una configurazione con Multitenant abilitato, accendendo in modifica ai dati di un soggetto definito con credenziali https, se si modificava il dominio, si avviava erroneamente il wizard di caricamento dei certificati.

- In una erogazione o fruizione, durante la creazione di un nuovo gruppo, se si sceglieva di ereditare la configurazione da un precedente gruppo, non venivano riportate le politiche di rate limiting esistenti sul vecchio.

- Se una erogazione conteneva nel gruppo 'predefinito' un connettore multiplo non era possibile ridefinire il connettore su eventuali altri gruppi.

- Nel cambio di versione di una API venivano erroneamente proposte anche le versioni di API incomplete o che non contenevano lo stesso port-type nel caso di API SOAP. Inoltre non veniva verificato che la nuova versione possedesse tutte le operazioni riferite puntualmente nei gruppi, nei criteri di applicabilità delle trasformazioni o nei filtri di policy di RateLimiting.

- Durante la creazione di un nuovo gruppo, non veniva verificato se le azioni associate fossero già riferite puntualmente nei criteri di applicabilità delle trasformazioni del gruppo Predefinito.

- L'aggiornamento dell'interfaccia WSDL di una API SOAP provocava un errore inatteso della console se il WSDL possedeva i caratteri \\r\\n all'inizio del file.

- In seguito alla creazione di una API REST creata attraverso il caricamento di un'interfaccia OpenAPI contenente una descrizione maggiore di 255 caratteri, una qualsiasi modifica dell'API (del nome, tag, descrizione stessa, ...) terminava con un errore: 'La descrizione supera i 255 caratteri ammessi'. Il problema derivava dall'aggiunta dei caratteri '\\r' dove erano presenti i caratteri '\\n' nella descrizione.  La correzione è stata effettuata per tutti gli elementi della console che vengono gestiti con lo stesso tipo di elemento html: 'text-area'.

- Se per un applicativo o soggetto veniva caricato un certificato con serial number più grande della dimensione massima di un long, la console visualizzava un numero negativo.
	
- Nella sezione 'Configurazione - Cache' (disponibile in modalità avanzata) sono adesso configurabili tutte le cache del prodotto (anche registry e controllo del traffico).
	
- Corretta anomalia nella ridefinizione del connettore su un nuovo gruppo, che provocava errore in caso di annullamento/ripetizione della stessa operazione.

- L'eliminazione di un'entità del registro rimaneva nella cache interna dell'applicazione e in alcune circostanze veniva ripresentata erroneamente.

- Una volta impostato un filtro su un connettore multiplo, era possibile solamente modificarne il valore ma non eliminarlo.

- Durante l'aggiornamento del nome di un Tag veniva erroneamente generato un messaggio di livello ERROR nel file di log.

- Durante il salvataggio delle API (e dei Soggetti in Multitenant) associate ad un utente, quando si selezionava la checkbox 'Tutti' nella pagina di configurazione dell'utente la precedente associazione continuava ad esistere a livello di base dati anche se non più visualizzate da console comportando anomalie durante l'utilizzo della console.

- Migliorato il messaggio di errore riportato dalla console se viene utilizzata la funzionalità 'Importa' o 'Esporta' senza fornire un archivio.

- Nei Connettori Multipli erano presenti i seguenti problemi relativi alle credenziali basic associate in una consegna tramite servizio IntegrationManager (funzionalità attivabile con una installazione 'avanzata'):

	- le credenziali non venivano verificate ed era quindi possibile creare una configurazione di consegna senza credenziali

	- per le credenziali definite non veniva controllato se fossero già utilizzate in altri applicativi o erogazioni

	- se veniva prima abilitata la consegna tramite I.M. assegnando delle credenziali basic e poi successivamente disabilitata, le credenziali restavano erroneamente assegnate all'applicativo.

Per la console di monitoraggio sono stati risolti i seguenti bug:

- La console non visualizzava il contenuto del messaggio, anche se salvato dal gateway, in presenza di header http con valore una stringa vuota.

- Risolto problema presente sui criteri di generazione di report statistici: quando veniva selezionata un'implementazione di API non era più possibile modificare il tipo delle informazioni visualizzate nel report (numero transazioni, dimensione e latenza).

- In presenza di una configurazione in Load Balancing, se la connessione verso un nodo andava in "read timed out", la console era accessibile allo scadere del timeout impostato a 120 secondi. Il valore di default del timeout è stato ridotto a 5 secondi.

- Risolto problema presente nella gestione dei permessi riguardanti gli utenti della console di monitoraggio con visibilità limitata per soggetti e/o API:

	- le ricerche puntuali tramite identificativo di transazione o di messaggio non verificavano che l'utente avesse i diritti per visualizzare i dati della transazione;

	- le liste contenenti le erogazioni o le fruizioni di API, impostabili nei criteri di ricerca, visualizzano adesso solamente le API associate all'utente.

- Il controllo dello stato dei nodi del cluster non avveniva ogni 60 secondi a causa di una problematica che resettava il counter ad ogni navigazione sulla console di monitoraggio e quindi una continua navigazione faceva si che l'aggiornamento dello stato non avvenisse mai.

Per le API di monitoraggio sono stati risolti i seguenti bug:

- In una configurazione multitenant, con opzione 'multitenant.forzaSoggettoDefault' disabilitata (comportamento di default), la generazione di report statistici con filtro contenente il field 'api_implementata', senza la definizione del soggetto referente (richiesta comune per il Profilo 'API Gateway'), produceva il seguente errore: Parametro 'api' fornito possiede un valore 'NomeAPI:1' che non rispetta il formato atteso '^[a-z]{2,20}/[0-9A-Za-z]+:[_A-Za-z][\\-\\._A-Za-z0-9]*:\\d$'

- Le interfacce generate tramite 'CXF OpenApiFeature' presentavano erroneamente come risposta 2xx un problem detail.
