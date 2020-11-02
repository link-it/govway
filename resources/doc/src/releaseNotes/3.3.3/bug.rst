Bug Fix
-------

Sono stati risolti i seguenti bug:

- Sono stati corretti i seguenti problemi presenti sulla validazione tramite OpenAPI 3:

	- Un oggetto di tipo 'string', definito con la restrizione basata su pattern (es. '^\d{6}$'), non veniva correttamente validato se il valore presente nel messaggio json possedeva dei caratteri speciali come '\r\n' o '\n' o '\t'. La validazione OpenAPI terminava con successo non rilevando i caratteri non ammessi dal pattern indicato.

	- Sulla validazione della risposta, per operazioni che non prevedevano una risposta applicativa, la validazione non rilevava un contenuto non ammesso se era compatibile con la risposta definita per il codice http 'default'.

	- La validazione trattava come 'required' i contenuti delle richieste per cui nell'interfaccia non veniva definito l'attributo 'required' nel request-body. Il comportamento è stato modificato per adeguarsi a quando descritto nella specifica 'https://swagger.io/docs/specification/describing-request-body/': "Request bodies are optional by default. To mark the body as required, use required: true."

	- Corretti problemi di validazione dei parametri definiti come 'date-time' che presentavano il carattere '+' nell'offset.

- Corretto problema di identificazione fallita di un risorsa di API REST quando l'OpenAPI caricato conteneva lo '/' in fondo ad una risorsa.

- Corretto http status presente nel ProblemDetail generato dalle erogazioni in caso di 'internalResponseError'. Veniva indicato erroneamente 503, invece del corretto codice 502 utilizzato per la connessione http.

- La libreria di valutazione delle espressioni JsonPath, se queste contenevano la funzione concat (es. concat($.richiedente.codice_fiscale,"###",$.richiedente.nome)), salvava in una cache interna il risultato; il salvataggio comportava che a fronte di messaggi differenti l'applicazione dell'espressione JsonPath forniva lo stesso risultato. Il comportamento errato della libreria comportava malfunzionamenti nelle funzionalità di GovWay dove era possibile utilizzare espressioni JsonPath (es. nella correlazione applicativa, nelle trasformazioni).

- Sia nella funzionalità di negoziazione dei token che durante la verifica tramite servizio di introspection e userInfo, viene adesso utilizzata la modalità http 'content-length' al posto della precedente modalità 'transfer-encoding-chunked'. Nella negoziazione del token è stato inoltre corretto il body della richiesta 'application/x-www-form-urlencoded' eliminando il primo carattere '&' aggiunto erroneamente, es. &grant_type=client_credentials (https://github.com/link-it/govway/issues/69).

- Nella funzionalità di negoziazione dei token, in presenza di un token con expires_in = 9223372036854775 (il Long.MAX_VALUE portato ai secondi), il sistema segnalava erroneamente il token come scaduto. Invece da specifiche tale valore dovrebbe indicare un tempo "infinito" (https://github.com/link-it/govway/issues/70).

- Aggiunta generazione dell'header WWW-Authenticate in ogni tipo di autenticazione gestita su GovWay se l'autenticazione fallisce (codice http 401). Anche il gestore delle credenziali, da utilizzare in caso di frontend web, gestisce adesso la possibilità di generare tale header. È stato inoltre associato uno stato 'autenticazione fallita' alle transazioni che non presentano le credenziali attese dal gestore.

- La funzione di ottenimento di un wsdl tramite l'invocazione dell'url di invocazione arricchita del prefisso '?wsdl', poteva generare un wsdl contenente schemi xsd in cui alcuni elementi riferivano prefissi non associati a dichiarazioni di namespace. Il problema è stato risolto.

- Il campionamento statistico mensile e settimanale, in ambienti con un elevato traffico, non era performante e quindi ne è stata disabilitata la generazione. I report di distribuzione statistica che riguardano un periodo superiore al giorno vengono adesso calcolati con il campionamento giornaliero. Durante la generazione delle statistiche è stato inoltre corretto il valore attribuito ai tempi di latenza per le transazioni che non possedevano le date dei quattro corner, utilizzando un valore null al posto di un fuorviante valore 0.

- Ottimizzato il diagnostico relativo ad una utenza errata, eliminando dalle informazioni tracciate la password utilizzata dal chiamante.

- Corretto timer che consente di verificare lo spazio disco occupato da un database.

- I timer e il batch di generazione delle statistiche registrano adesso gli eventi in log dedicati al tipo di intervallo gestito: orario,giornaliero,settimanale,mensile.
  Questo consente di evitare la sovrascrittura da parte degli intervalli più frequenti delle informazioni inerenti a intervalli maggiori durante la ruotazione dei log.

- Nelle installazioni in cluster, sono stati ridotti i tempi di verifica per l'ottenimento di un lock da parte del timer che consegna le notifiche ed è stato corretto un problema che sbilanciava l'acquisizione del lock sempre su un nodo. Inoltre è stata corretta la procedura di rilascio dei lock in fase di shutdown dell'A.S., ed è stata inserita anche una pulizia iniziale all'avvio. Sono inoltre stati rivisti l'utilizzo dei lock per quanto concerne i timer adibiti alla pulizia del repository messaggi.

- Aggiunta impostazione del Locale per il fault string del SOAPFault. Il locale utilizzato è adesso indicabile in govway.properties tramite le proprietà 'org.openspcoop2.pdd.erroreApplicativo.faultString.language' e 'org.openspcoop2.pdd.erroreApplicativo.faultString.country'.

- Corretti i seguenti problemi presenti in uno scenario con profilo di interoperabilità ModI:

	- aggiunti gli header contenente gli identificatori anche sulle erogazioni (es. GovWay-Conversation-ID);
	- corretto digest salvato nelle tracce per i profili IDAS03: veniva erroneamente salvato in codifica hex invece che in codifica base64;
	- corrette utility JWT al fine di validare un certificato in un truststore verificandone anche l'uguaglianza con il certificato stesso oltre che tramite la CA;
	- aggiunto controllo che l'applicativo venga identificato, tramite uno dei meccanismi di autenticazione, per poter usare funzionalità di sicurezza;
	- corretto problema che non consentiva, in presenza di validazione dei contenuti attivi, la generazione dell'header SOAP 'X-Correlation-ID', nel profilo di interazione PUSH, quando il backend server non generava di proprio conto tale header;
	- risolto problema che avveniva durante la creazione dell'header SOAP 'X-Correlation-ID' se il messaggio di risposta di una Richiesta PULL non conteneva il SOAPHeader;
	- gestito aggiornamento del valore dell'header 'ReplyTo', sia in REST che SOAP, sull'erogazione prima di contattare il backend in modo da tradurre il valore con la relativa url di invocazione del servizio di callback correlato;
	- corretta descrizione dell'errore ritornata al client in caso di header SOAP Correlation-ID non trovato;
	- risolto problema presente nella gestione della fase di Richiesta per il profilo PUSH e PULL SOAP: se il server non generava un header 'X-Correlation-ID', GovWay creava un header valorizzandolo con l'id del messaggio invece che con l'id di transazione come descritto da documentazione;
	- corretti esiti riportati per la fase di imbustamento SOAP quando mancavano elementi obbligatori nel profilo PULL;
	- corretto esito della transazione, quando avveniva un errore di 'protocollo' anche durante la fase di imbustamento;
	- aggiunto controllo sul codice http nella fase di imbustamento per il protocollo rest, profilo non bloccante;
	- aggiunto 'IDCollaborazione' letto dalla risposta per generare l'header GovWay-Conversation-ID.
 

Per la console di gestione sono stati risolti i seguenti bug:

- Durante l'aggiunta di un applicativo di tipo server veniva visualizzata erroneamente la finestra di dialogo per la copia delle credenziali con delle credenziali vuote.

- Risolto malfunzionamento presente nella sezione del connettore che consente il download dei certificati server; il problema causava un errore inatteso sulla console quando il server non era raggiungibile (es. errore di connection refused o timed out).

- Durante la creazione di una API, se si caricava un wsdl e solo successivamente si impostava il tipo di API a SOAP,   la console non consentiva di completare la creazione segnalando erroneamente un problema di sintassi relativo all'interfaccia caricata.

- La selezione di un 'Servizio' in una erogazione o fruizione di API SOAP è adesso obbligatoria anche utilizzando la console in modalità avanzata. 

- Corretta una errata visualizzazione delle informazioni presenti nella maschera di creazione di una fruizione, utilizzando la console in modalità avanzata: la scelta dell'erogatore risiedeva tra i dati dell'API e venivano quindi visualizzati due campi consecutivi con la medesima denominazione 'Nome'.

- Risolto problema che provocava uno stallo sulla console quando si selezionava ripetutamente da una select list il valore già scelto in precedenza.

- La creazione di una nuova erogazione/fruizione o l'aggiornamento del nome e/o dei parametri del profilo, richiedevano un tempo considerevole per presentare la maschera dei dati quando il numero delle API era elevato. 

- L'aggiornamento del nome di una erogazione, che presentava connettori multipli, non veniva riportato sugli applicativi aggiuntivi a quello di default.

- Corretti i seguenti problemi presenti in uno scenario con profilo di interoperabilità ModI:

	- se si modificava la configurazione del profilo in una fruizione, alcuni parametri del connettore quale l'eventuale abilitazione del debug e l'autenticazione http o token venivano erroneamente disabilitate;

	- il cambio di nome di una API di richiesta correlata tramite profilo PUSH, provocava un errore inatteso della console quando si accedeva all'API con ruolo 'Risposta';

	- risolta anomalia che si presentava quando veniva creata una erogazione a partire da una API con sicurezza canale IDAC01, e successivamente veniva modificata la sicurezza del canale dell'API implementata a IDAC02, o veniva associata all'erogazione una API differente con sicurezza canale IDAC02. La configurazione riportava correttamente un warning nella sezione 'Controllo degli Accessi' poichè l'autenticazione era opzionale (configurazione derivante dall'API indicata al momento della creazione dell'erogazione). Accedendo in modifica al controllo degli accessi non veniva però visualizzato e non era consentito modificare il criterio di opzionalità dell'autenticazione.

- Corretta funzionalità di import affinchè il controllo che verifica l'aderenza delle informazioni fornite con il WSDL sia effettuato solamente su API SOAP.

- Corretti i seguenti problemi relativi alla gestione delle proprietà binarie di un profilo di interoperabilità:

	- l'import di un servizio applicativo andava in errore se una proprietà binaria era valorizzata (es. keystore pkcs12 di un applicativo in ModI PA);
	- l'accesso al dettaglio di una proprietà binaria di una fruizione andava in errore;
	- la registrazione o l'aggiornamento di una proprietà binaria comportava un salvataggio corrotto di tale documento sul registro; il problema era presente anche per gli allegati di una API, di una erogazione e di una fruizione;
	- corrette breadcump errate presenti nelle sezioni di dettaglio di una proprietà binaria di una erogazione o di una fruizione;
	- corretta breadcump errata presente nella sezione 'Modifica Profilo Interoperabiltà' di una fruizione.

- Nella sezione Runtime è adesso disponibile il dettaglio dei timer attivi e  la possibilità di attivarli/disattivarli.

- Rivista la sezione 'Coda Messaggi' nella govwayConsole al fine di visualizzare tutte le informazioni di interesse: nome dell'erogazione, nome dei connettori, data di rispedizione per ogni connettore, nome degli applicativi server. È stato inoltre aggiunta la funzionalità 'Riconsegna Immediata' utilizzabile per forzare la consegna di un messaggio senza aspettarne la data di rispedizione (la presa in carico avviene tramite funzionalità ancora in versione alfa e quindi non attive per default).

- Il cookie generato dalla console di gestione è stato ridenominato in 'JSESSIONID_GW_CONSOLE'.

Per la console di monitoraggio sono stati risolti i seguenti bug:

- I dati delle configurazioni accedute tramite la console di monitoraggio vengono adesso salvati in una cache interna al fine di rendere più veloce la navigazione tra le maschere di ricerca.

- Nella Ricerca Avanzata delle transazioni è adesso consentito effettuare una ricerca che includa qualsiasi profilo di interoperabiltà; con questa scelta anche l'indicazione del soggetto locale diventa a campo libero.

- Nei report statistici che non riguardano distribuzioni temporali è stata eliminata la voce 'Unità temporale' tra i criteri di ricerca; la scelta compare solamente, limitata ai valori 'oraria' e 'giornaliera', nel caso di impostazione di un periodo personalizzato poichè influenza il widget nel quale viene consentito o meno la possibilità di indicare l'orario desiderato.

- Corretto nullPointer che capitava dopo un utilizzo prolungato della console:  "Caused by: javax.el.ELException: /commons/includes/menu.xhtml @33,58 rendered="#{applicationBean.showTransazioniBase}": java.lang.NullPointerException".

- Il cookie generato dalla console di monitoraggio è stato ridenominato in 'JSESSIONID_GW_MONITOR'.


Solo stati risolti i seguenti bug presenti nell'Installer:

- Le patch SQL che modificano il tipo di una colonna da VARCHAR a CLOB, tramite il comando ALTER, sono state riviste al fine di utilizzare una versione più efficente per i tipi di database che lo consentono (https://github.com/link-it/govway/issues/58).

- Modificato tipo della colonna 'value' della tabella 'tracce_ext_protocol_info' al fine di poter creare un indice su tale colonna. L'indice consente di migliorare le performance come descritto nell'issue https://github.com/link-it/govway/issues/60.

- Lo script SQL generato per MySQL, possedeva un vincolo 'unique' non instanziabile su mysql: "ERROR 1071 (42000): Specified key was too long; max key length is 3072" (https://github.com/link-it/govway/issues/66).

- L'indice 'full INDEX_TR_SEARCH', generato dall'installer in modalità avanzata per i database di tipo postgresql e oracle, non venivano utilizzati dal db per soddisfare le query prodotte tramite la console di monitoraggio durante la ricerca nello storico a causa dei seguenti problemi:

	- la colonna 'versione_servizio' non era presente nell'indice ma veniva utilizzata sia per la ricerca di una API Implementata che per la visualizzazione delle informazioni relative ad una singola transazione
	- nel comando SELECT venivano aggiunte ulteriori colonne non presenti nell'indice che non avevano però alcuna utilità durante la presentazione dei risultati in lista. 

- L'installer genera adesso un archivio govway.ear contenente nel file application.xml i 'resource-ref' necessari all'A.S. per effettuare un shutdown corretto dei datasource.

I seguenti bug sono stati invece risolti utilizzando l'Installer in modalità avanzata:

- Il file 'consolePassword.properties' non veniva generato per l'ambiente runtime in caso di disaccoppiamento tra runtime e manager.

- È adesso possibile generare un batch che gestisce la pulizia dei messaggi presi in carico sul gateway (gestoreRuntimeRepository). La presa in carico avviene tramite due funzionalità ancora in versione alfa e quindi non attive per default (IntegrationManager e ConsegnaAsincrona) per le quali è stata introdotta la gestione delle priorità e la possibilità di assegnare una coda di pool di thread differente ad un connettore. Sono inoltre stati risolti i problemi che riguardavano la consegna (veniva incrementato erroneamente per 2 volte il numero di rispedizione del messaggio ad ogni consegna) e l'invocazione del servizio IntegrationManager (segnalazione non bloccante presente nel log: 'Non abilitata la gestione delle transazioni stateful'). Nella consegna con notifiche, anche le url verso i server di notifica venivano erroneamente arricchiti con il contesto 'rest' dell'invocazione: il problema è stato risolto. Infine sul database di runtime è stata aggiunta una colonna contenente l'informazione temporale di creazione dell'entry, nelle tabelle del runtime che non la possedevano, per poter effettuare il partizionamento del database 'runtime'.


