Bug Fix
-------

Sono stati risolti i seguenti bug:

- Nella funzionalità di tracciamento con correlazione applicativa è adesso possibile definire regole multiple di correlazione anche per i messaggi JSON e non più solamente per messaggi XML. L'applicabilità di una regola è verificabile tramite l'utilizzo di un'espressione JSONPath.

- Le richieste 'OPTIONS', identificate tramite una risorsa definita nell'API con HttpMethod 'Qualsiasi', vengono adesso gestite come 'Request Preflight' se il CORS è abilitato nell'API.
        
- Gli header HTTP di integrazione specifici di un Profilo di Interoperabilità (es. Fatturazione Elettronica) non venivano gestiti correttamente in caso di attivazione dei 'metadati' di integrazione 'backward compatibility' il cui scopo è quello di produrre header di integrazione uguali a quelli generati da 'OpenSPCoop2' (es. x-openspcoop2-trasporto). Il problema è stato risolto e adesso tutti gli header generati sono stati allineati per essere retrocompatibili (es. X-SDI-\*).

- L'accesso al database è stato ottimizzato per i seguenti casi:

        - Registrazione delle credenziali (trasporto, token) e dei tags/eventi nella transazione.

	- Funzionalità di arricchimento delle tracce, nel Profilo di Interoperabilità 'Fatturazione Elettronica'

- Sono stati disattivati i log generati dai template engine 'Freemarker' e 'Velocity' sul server log dell'Application Server.

- Aggiunta la possibilità di gestire gli header HTTP e i parametri delle URL all'interno di una trasformazione.

- L'identificativo del cluster numerico assegnabile alla proprietà 'org.openspcoop2.pdd.cluster_id.numeric' nel file 'govway_local.properties', veniva erroneamente gestito come intero invece che come stringa; questo comportava nel caso di numeri a due cifre che non si potessero utilizzare le prime 9 cifre definite come '0X' poichè si perdeva lo 0 iniziale. Il problema è stato corretto.

- Modificato il livello di severità da 'error' a 'info' del diagnostico che riporta l'esito di una comunicazione terminata con codice http 3xx di una API REST.


Per la console di gestione sono stati risolti i seguenti bug:

- Risolto problema, per quanto concerne il profilo 'ModI PA', che non consentiva di registrare gli applicativi autorizzati in presenza di Multi-Tenant abilitato.

- Corretto messaggio di warning presente durante la modifica di un referente di API (Profilo SPCoop); veniva erronamente visualizzato un id numerico al posto del nome del soggetto.

- La configurazione sui connettori multipli veniva erroneamente persa se venivano effettuate modifica nella sezione 'URL di Invocazione'.

- Corretto colore dei link visitati; veniva erroneamente utilizzato il colore associato all'hover.

- Se durante un aggiornamento veniva aggiunto un nuovo profilo di interoperabilità, senza che fosse stato creato sul database un soggetto di default associato, la console sollevava un errore generico. L'anomalia viene adesso segnalata correttamente.



In particolar modo sono stati risolti i seguenti bug relativi alle funzionalità 'importa' e 'esporta'

- Aggiunti controlli di consistenza durante l'import di un archivio: vengono verificate la presenta di tutti gli elementi riferiti dalle configurazioni, compreso le Token Policy.

- Mediante il processo di importazione di una govlet era possibile creare un nuovo applicativo o soggetto con le stesse credenziali di uno già esistente.

- Revisione delle govlet per gestire correttamente il soggetto referente di 'default' per il Profilo API Gateway.

- Risolto problema presente durante l'esportazione di una API con Multi-Tenant abilitato e un soggetto selezionato in testata; l'archivio esportato era vuoto.

- L'esportazione di una erogazione, sulla quale era associato nel connettore un applicativo 'server', non veniva effettuata correttamente poichè tra gli elementi esportati non veniva inserito l'applicativo interno associato all'erogazione stessa.

- Importando un'archivio contenente una erogazione in cui era stato registrato nel controllo degli accessi un applicativo non esistente, l'operazione terminava con un errore inatteso. Aggiungendo l'applicativo mancante l'operazione terminava correttamente ma veniva generata una erogazione con stato disabilitato contenente erroneamente due gruppi (nell'erogazione originale esisteva solamente il gruppo predefinito). Entrambe le problematiche sono stati risolte.

- L'import di un archivio, contenente una una erogazione con connettore definito tramite Integration Manager, comportava una errata registrazione di un applicativo 'client'.

- Se su un connettore veniva prima abilitata la consegna su Integration Manager, definendo delle credenziali basic, e poi successivamente disabilitata, le credenziali rimanevano erroneamente assegnato all'applicativo. Il problema si evidenziava effettuando una esportazione ed una successiva importazione dell'erogazione. Terminato il processo di import veniva creato erroneamente un applicativo che possedeva il nome interno dell'erogazione e le credenziali che inizialmente erano state assegnate alla funzione I.M.


Sulla console di monitoraggio sono stati risolti i seguenti bug:

- Ritornando alla lista delle transazioni, dopo aver visionato il dettaglio di una transazione, viene adesso mantenuto chiuso il filtro di ricerca in modo da permettere una immediata consultazione di una transazione successiva presente nell'elenco.

- Risolto problema di visualizzazione dei contenuti di messaggi formato da solamente 2 caratteri (es. un semplice text/plain 'ok') che visualizzava erroneamente un messaggio vuoto.

- Il download dei contenuti applicativi utilizza adesso la corretta estensione del file, in relazione al Content-Type, anche per una registrazione avvenuto con la funzionalità di 'Dump Binario'. Prima del fix veniva utilizzata sempre l'estensione '.bin'.

- L'accesso al database è stato ottimizzato per alcune voci della console di monitoraggio che portano a pagine statiche e quindi non necessitano di query:

	- le voci del menù principale 'Transazioni' e 'Analisi Statistica' 
	
	- le modalità di ricerca 'ID Transazione' e 'ID Messaggio'.
      
- Corretto colore dei link visitati; veniva erroneamente utilizzato il colore associato all'hover.

- L'export, di qualsiasi tipo, non conteneva le informazioni riguardanti il tipo di api (REST/SOAP) e i tags.



Per le API di configurazione e monitoraggio sono stati risolti i seguenti bug:

- Adeguate interfacce OpenAPI 3 per utilizzare un discriminator interno agli oggetti elencati nelle strutture oneOf, come richiesto dalla specifica OpenAPI "https://swagger.io/docs/specification/data-models/inheritance-and-polymorphism/". Nelle precedenti versioni il discriminator utilizzato era un claim esterno agli oggetti riferiti nella struttura oneOf.

- Aggiunta la possibilità, durante la creazione di una erogazione o fruizione, di ridefinire un nome o una versione differente da quella dell'API implementata.

- Ripristino log relativo all'http status code ritornato nel log govway_api[Config,Monitor]_transaction.log

- Aggiunti controlli di robustezza durante la creazione di una erogazione o fruizione di API SOAP, al fine di verificare la presenza obbligatoria del claim 'api_soap_servizio'.

- Corretta descrizione 'TAg' in govway_core.yaml.

