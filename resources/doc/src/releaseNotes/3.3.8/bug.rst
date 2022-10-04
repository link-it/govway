Bug Fix
-------

Sono stati effettuati i seguenti interventi migliorativi degli aspetti prestazionali:

- in presenza di stress test alcune comunicazioni terminavano con l'errore "Cannot assign requested address" dovuto ad un numero troppo basso (5) di connessioni http 'keep-alive' mantenute aperte per una stessa destinazione (numero aumentato per default a 200);

- risolto degrado delle performance, che avveniva in presenza di trasformazione Freemarker o Velocity, attraverso il salvataggio dell'oggetto template istanziato in cache;

- è adesso possibile configurare una 'DenyList' o una 'WhiteList' che consente di personalizzare gli header HTTP che devono essere registrati su database tramite la funzionalità di registrazione messaggi;

- migliorata la latenza introdotta da GovWay (attualmente pochi millisecondi) durante la gestione di una prima richiesta non ancora in cache, su API Rest contenenti molte risorse (es. 600), in cui la latenza introdotta da GovWay era nell'ordine dei secondi (5,7 secondi);

- le richieste che richiedevano la validazione/negoziazione di un token, il recupero di attributi da un AttributeAuthority o l'invocazione di meccanismi di autenticazione/autorizzazione esterni acceduti via http (implementati tramite plugin) potevano far scaturire l'errore "Could not acquire semaphore after 30000ms", quando il servizio http esterno contattato (es. Authorization Server) non rispondeva mandando la richiesta in read timeout e nel frattempo continuavano ad accumularsi richieste che necessitavano dell'invocazione del servizio esterno poichè l'informazione richiesta non era in cache. L'errore si amplificava poichè per stessa funzionalità (es. Token Policy di Negoziazione) non vi era un lock dedicato alla singola policy ma un lock condiviso tra tutte le policy. La problematica è stata risolta:

	- dedicando un lock ad ogni Token Policy, AttributeAuthority o tipo di autenticazione/autorizzazione;

	- abbassando i tempi di read-timeout di default a 10 secondi;

	- consentendo un numero di richieste parallele verso il servizio esterno quando le informazioni non sono in cache (default 10), in modo da non rendere seriale l'inizializzazione della cache.

Sono stati risolti i seguenti bug:

- risolti i seguenti problemi relativi alla validazione di un'interfaccia OpenAPI 3.0:

	- non erano supportati parametri (path/header/query/cookie) definiti tramite complex type (anyOf/allOf/oneOf);

	- la validazione di un 'path parameter', contenente caratteri che erano stati codificati per poter essere trasmessi nella url, falliva erroneamente poichè non veniva attuata una decodifica prima della validazione del parametro;

- l'invocazione di API REST con contenuti XML permettevano di sfruttare la vulnerabilità XXE descritta in:

	- https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html

	- https://owasp.org/www-community/vulnerabilities/XML_External_Entity_(XXE)_Processing

- la keyword 'securityToken.accessToken' era utilizzabile per accedere al certificato utilizzato nell'access token solamente alla prima invocazione;

- il certificato ottenuto tramite il 'gestore delle credenziali' via header HTTP è adesso disponibile nel contesto per essere acceduto tramite la keyword 'securityToken.channel';

- l'utilizzo dei metodi getJSONObject(...) e getJSONArray(...) dell'utility JsonPathExpressionEngine, creava in presenza di richieste parallele, dei messaggi inconsistenti per via di un utilizzo statico del parser JSONParser che non era 'thread safe';

- se in una API SOAP veniva configurata una trasformazione della richiesta (o della risposta) che rispettava la casistica seguente, l'applicazione della trasformazione a runtime produceva l'errore "Trasformazione richiesta fallita: Cannot add fragments which contain elements which are in the SOAP namespace":

	- il contenuto veniva ridefinito tramite una envelope soap di una versione differente da quello della richiesta originale (es. soap1.1 -> soap 1.2) 

	- veniva definito il nuovo Content Type conforme alla nuova versione dell'envelope (es. application/soap+xml).


Per la console di gestione sono stati risolti i seguenti bug:

- la creazione di una API REST, tramite l'upload di un openapi, presentava le seguenti problematiche:

	- non veniva create eventuali risorse definite con i metodi 'HEAD' o 'TRACE';

	- se venivano definiti erroneamente degli header senza schema, la console riportava un errore non corretto: "[Interfaccia OpenAPI 3] Documento non valido: java.lang.NullPointerException"; 

- la validazione di un'interfaccia OpenAPI era troppo stringente per quanto concerne i valori definiti nella sezione 'info' riguardanti:

	- indirizzo email dei contatti (info.contact.email)

	- url dei contatti e della licenza (info.contact.url e info.license.url)

- sono stati risolti le seguenti problematiche relative al caricamento di un allegato in una API:

	- utilizzando Internet Explorer avveniva una errata registrazione dove il nome del documento conteneva il path assoluto;

	- il tipo di specifica semiformale selezionato non veniva preservato quando si caricava un file e veniva riproposto il tipo UML (stesso problema era presente nel caricamento degli allegati di una erogazione o fruizione);

- nei campi 'textarea' utilizzabili per indicare path su file system, url, audience e altri valori che rappresentano un identificativo, non veniva segnalata l'eventuale presenza errata di new-line o tab nel valore fornito;

- aggiornate le librerie che consentono di effettuare il parsing di un documento yaml (OpenAPI 3) al fine di non essere più vulnerabile a Denial of Service (DoS) per mancanza di limitazione sulla profondita dei nodi analizzati e collezionati durante il parsing;

- l'utilizzo di molteplici schede all'interno del browser provocava errori durante l'utilizzo della console;

- utilizzando la funzionalità 'Elimina' per caricare archivi contenenti Policy di Rate Limiting, l'operazione completavano con successo ma l'eliminazione lasciava policy 'zombie' sulla base dati.



Per la console di monitoraggio sono stati risolti i seguenti bug:

- l'esito 'API Sospesa' è stato incluso nel gruppo 'Richiesta Scartata';

- migliorata la documentazione della console di monitoraggio relativa alle informazioni Esito, Richiedente e Dettaglio Errore riportate nel dettaglio di una transazione;

- risolta anomalia presente in tutte le distribuzioni statistiche eccetto quella temporale e per esiti. Quando si selezionava un periodo 'personalizzato', se la scelta precedente del periodo era 'ultime 12 ore' l'impostazione personalizzata delle ore funzionava, altrimenti le date venivano resettate automaticamente dopo l'impostazione dell'ora all'intervallo inferiore o superiore a seconda della data iniziale o finale.
