Bug Fix
-------

Sono state risolte le seguenti vulnerabilità relative alle librerie di terze parti:

- CVE-2026-44417, CVE-2026-50623, CVE-2026-50629, CVE-2026-50630, CVE-2026-50631, CVE-2026-50632, CVE-2026-50633, CVE-2026-50634, CVE-2026-50645, CVE-2026-54225, CVE-2026-57817, CVE-2026-57818, CVE-2026-57819, CVE-2026-61466, CVE-2026-63687, CVE-2026-64958, CVE-2026-65432, CVE-2026-65583, CVE-2026-66909, CVE-2026-68079, CVE-2026-68481, CVE-2026-71575, CVE-2026-100227, CVE-2026-107937, CVE-2026-107938, CVE-2026-108039: aggiornata libreria 'org.apache.cxf:\*' alla versione 4.2.4

- CVE-2026-41838, CVE-2026-41840, CVE-2026-41841, CVE-2026-41842, CVE-2026-41843, CVE-2026-41844, CVE-2026-41845, CVE-2026-41846, CVE-2026-41848, CVE-2026-41850, CVE-2026-41851, CVE-2026-41852, CVE-2026-47890, CVE-2026-47891, CVE-2026-47892, CVE-2026-47893, CVE-2026-59280, CVE-2026-59281, CVE-2026-59282, CVE-2026-59283, CVE-2026-59313, CVE-2026-59314: aggiornata libreria 'org.springframework:\*' alla versione 7.0.9

- CVE-2026-40988, CVE-2026-41003, CVE-2026-41694, CVE-2026-47841, CVE-2026-47842, CVE-2026-47877, CVE-2026-59270, CVE-2026-59276, CVE-2026-59277: aggiornata libreria 'org.springframework.security:\*' alla versione 7.1.1

- CVE-2026-41720: aggiornata libreria 'org.springframework.ldap:\*' alla versione 4.1.0

- CVE-2026-54512, CVE-2026-54513, CVE-2026-54514, CVE-2026-54515, CVE-2026-54516, CVE-2026-54517, CVE-2026-54518, CVE-2026-68497, CVE-2026-83557, CVE-2026-19032: aggiornata libreria 'com.fasterxml.jackson.core:\*' alla versione 2.22.3

- CVE-2026-44891, CVE-2026-55831, CVE-2026-55833, CVE-2026-62380, CVE-2026-89044: aggiornata libreria 'io.netty:\*' alla versione 4.2.18.Final

- CVE-2026-66142, CVE-2026-66143, CVE-2026-66144, CVE-2026-91863, CVE-2026-91864, CVE-2026-91865, CVE-2026-91866, CVE-2026-91867: aggiornata libreria 'org.apache.neethi:neethi' alla versione 3.2.4

- CVE-2026-88920, CVE-2026-85532, CVE-2026-92899: aggiornata libreria 'org.apache.wss4j:\*' alla versione 4.0.2

- CVE-2026-102495, CVE-2026-102496, CVE-2026-102497: aggiornata libreria 'org.apache.ws.xmlschema:xmlschema-core' alla versione 2.3.3

- CVE-2026-107726, CVE-2026-107725: aggiornata libreria 'com.hazelcast:hazelcast' alla versione 5.7.0; i contatori del rate limiting, che utilizzavano il CP Subsystem non più disponibile nella Community Edition, sono stati reimplementati su una mappa distribuita

- CVE-2026-8763, CVE-2026-13506: aggiornata libreria 'org.bouncycastle:\*' alla versione 1.86

- CVE-2026-71497, CVE-2026-75140: aggiornata libreria 'org.jsoup:jsoup' alla versione 1.23.2

- CVE-2026-84939: aggiornata libreria 'org.freemarker:freemarker' alla versione 2.3.35

- CVE-2026-78254: aggiornata libreria 'org.apache.ant:\*' alla versione 1.10.18

- CVE-2026-64607: aggiornata libreria 'org.apache.httpcomponents.client5:httpclient5' alla versione 5.6.4

- CVE-2026-54399, CVE-2026-54428: aggiornata libreria 'org.apache.httpcomponents.core5:\*' alla versione 5.4.3

- CVE-2026-49844: aggiornata libreria 'org.apache.logging.log4j:\*' alla versione 2.26.1

Sono state inoltre corrette le segnalazioni di vulnerabilità rilevate da SonarQube v26.6.0.123539.

Sono stati risolti i seguenti bug per la componente runtime del gateway:

- Il provider 'BouncyCastle' impediva la lettura dei keystore PKCS12 cifrati con algoritmi moderni (PBES2/AES-256, default di OpenSSL 3 e di keytool dal JDK 16), segnalando l'errore fuorviante 'keystore password was incorrect'. Il provider viene inoltre registrato dopo quelli del JDK, più performanti sugli algoritmi di uso comune.

- Profilo ModI: le richieste con Content-Type 'application/problem+json', 'application/problem+xml' o contenenti un SOAPFault venivano trattate come messaggi di errore e la sicurezza messaggio non veniva applicata in fruizione né verificata in erogazione.

- Corrette le seguenti anomalie relative alle trasformazioni:

  - sulle API REST le operazioni sull'header 'Content-Type' non venivano applicate in assenza di una conversione del contenuto;
  - sulle API REST una regola che ridefiniva HTTP Method o Path e trasformava la risposta faceva fallire ogni invocazione con errore 502.

- Corrette le seguenti anomalie relative ai connettori HTTP:

  - il connettore 'httpcore5' BIO non consentiva di invocare endpoint con host non conforme a RFC 2396 (es. contenente '_'), fallendo con l'errore 'NullPointerException: Host name';
  - il connettore 'httpcore5' non registrava, per le richieste con body, la data di uscita della richiesta, visibile nei tempi di dettaglio della transazione;
  - una risposta Problem Details compressa dal backend (es. 'Content-Encoding: gzip') veniva interpretata senza decompressione e inoltrata al client corrotta;
  - l'override della configurazione HTTPS della JVM attivato su un connettore veniva applicato anche alle connessioni verso Token Endpoint e Attribute Authority, causandone il fallimento;
  - il connettore 'Status' con verifica statistica poteva contare due volte le transazioni.

- Correlazione Applicativa: un messaggio privo di contenuto (es. risposta con codice HTTP 204) faceva terminare la transazione con errore anche quando l'impostazione 'Identificazione fallita' della regola era 'accetta'. È adesso possibile trattare l'assenza del contenuto come una normale identificazione non riuscita: la transazione termina con errore solo se la regola prevede 'blocca', mentre con 'accetta' prosegue senza registrare l'identificativo di correlazione. Il nuovo comportamento è disabilitato per default, per retrocompatibilità, ed è attivabile separatamente per richiesta e risposta nella configurazione globale.

- Claim, parametri e header definiti con la forma opzionale '?{..}' e non risolvibili a runtime venivano inviati con un valore vuoto anziché essere omessi, causando ad esempio il fallimento della negoziazione di un token PDND.

- La validazione dei contenuti di una API REST rifiutava con errore 502 documenti XML validi, in funzione della sola formattazione (es. elemento radice su più righe o attributi con apici singoli).

- I file temporanei creati nella directory 'dumpBinario' per i messaggi di grandi dimensioni non venivano sempre eliminati al termine della transazione, accumulandosi fino a saturare il volume dei log (caso tipico: API REST con validazione dei contenuti e messaggi binari o multipart). L'eliminazione è adesso garantita ed è stata aggiunta una pulizia periodica della directory.

- WS-Security: con la nuova versione di 'wss4j' non venivano più accettati gli attachment cifrati in modalità 'Element' privi del Content-Type tra gli header cifrati. Il Content-Type viene adesso sempre cifrato; per accettare i messaggi prodotti dalle versioni precedenti è possibile abilitare in ricezione l'opzione 'decryptAttachmentMimeTypeFallback'.

- Con il 'Multipart Parsing' abilitato, un allegato SOAP 'text/xml' con contenuto non valido faceva fallire la registrazione del messaggio; viene adesso registrato come binario.

- Gli eventi di violazione del controllo del traffico potevano non essere registrati quando più raggruppamenti di una stessa policy venivano violati contemporaneamente.

- Con il forward proxy (govway-proxy) attivo, il claim 'htu' del DPoP proof generato verso il backend riportava l'URL del proxy anziché quello del backend.

- Nel passaggio all'ora solare, i messaggi presi in carico tra le 02:00 e le 02:05 venivano riconsegnati continuamente e alcuni identificativi Integration Manager risultavano non trovati.

- Su Apache Tomcat l'arresto tramite lo script 'shutdown.sh' non terminava e il processo restava bloccato.

Per la console di gestione sono stati risolti i seguenti bug:

- I campi che contengono identificativi esterni o elenchi di valori (es. Client ID, Audience, Scope, Kid, CRL) rifiutavano caratteri ammessi come '|', '[', ']', '"' e ','.

- I caratteri speciali (es. doppi apici) dei template di Correlazione Applicativa venivano visualizzati come entità HTML e alterati a un successivo salvataggio.

- Nella gestione CORS di una fruizione, l'abilitazione dell'opzione 'Max Age' impediva il salvataggio della configurazione.

- Nelle maschere con caricamento di file, il salvataggio poteva fallire con l'errore "Dati incompleti" (es. una regola di trasformazione REST con conversione tramite template perdeva HTTP Method e Path). Corretti inoltre la validazione della password dei keystore caricati come credenziali, i ritorni a capo del WSDL modificato e gli spazi iniziali e finali delle interfacce YAML caricate tramite le API di configurazione.

- Negli elenchi il numero di elementi per pagina veniva perso tornando all'elenco; l'export di molti oggetti poteva fallire con l'errore "414 URI Too Long". Aggiunto inoltre un indicatore di operazione in corso durante la preparazione dell'export.

- Lo spostamento tramite frecce delle regole di proxy pass e degli archivi dei plugin falliva dopo la cancellazione di elementi. In tutte le liste ordinabili (regole di proxy pass, policy di rate limiting, regole di trasformazione e relative risposte, archivi dei plugin, handler) lo spostamento è adesso possibile anche oltre il confine di pagina, mentre con un filtro di ricerca attivo la colonna 'Ordine' non viene visualizzata. Corretti inoltre i tooltip con lettere accentate, mostrati come entità HTML, e il titolo delle liste annidate dopo una ricerca.

- Aggiunto un limite alla dimensione dei file caricati, configurabile tramite la property 'console.security.request.multipart.maxSize' (default 250M).

- Le API REST con interfaccia WADL, provenienti da un aggiornamento dalla 3.3, rendevano inaccessibile l'elenco delle API; sono adesso presentate come 'WADL (deprecato, non supportato)'.

- Le proprietà di un connettore di tipo plugin di una fruizione, ridefinito in un gruppo, venivano lette e modificate sul connettore di default.

- Nella sezione 'Runtime', in modalità cluster dinamica, vengono adesso nascoste anche le informazioni riferite al singolo nodo ('HTTP Client - BIO', 'HTTP Client - NIO', 'Threads Pool' della 'Consegna Notifiche').

- Documentate, nella token policy di negoziazione PDND e nel manuale, le keyword per la configurazione del purposeId riferite all'applicativo identificato tramite token.

- Il controllo anti SQL injection rifiutava i valori contenenti una parola chiave SQL (es. un'azione 'insert' o 'delete-user'), segnalando l'errore fuorviante 'Dati incompleti'; la correzione riguarda anche la console di monitoraggio e le API di configurazione e monitoraggio.

- La sanificazione degli attributi 'style' nei parametri ricevuti non filtrava alcuna proprietà CSS.

- Adeguamento alle WCAG 2.1 AA: rimosse le barriere di accessibilità rilevate, con particolare riferimento all'operabilità da tastiera, ai nomi accessibili dei controlli e al contrasto.

Per la console di monitoraggio sono stati risolti i seguenti bug:

- La lettura dei CSV del Tracciamento PDND non rilasciava la connessione al database, esaurendo le connessioni del datasource; il problema riguardava l'export e il download dei tracciati e la pubblicazione verso la PDND del timer statistiche in modalità applicazione web.

- Nel caricamento di file sono stati aggiunti il limite di dimensione e la verifica del token CSRF; corretti inoltre l'indicatore di caricamento, il messaggio di errore, la cancellazione dei file in 'Esamina archivio' e il conteggio dell'elenco delle ricerche salvate.

- Il diagnostico 'DPoP backend proof generato (in cache)' veniva classificato erroneamente come warning.

- Su Tomcat, in presenza di molti soggetti operativi, il cambio del profilo di interoperabilità e la navigazione tra le sezioni visualizzavano l'errore "Illegal Syntax for Set Operation".

- Il titolo della console si configura adesso con la proprietà 'console.nome.esteso', come per la console di gestione.

- Adeguamento alle WCAG 2.1 AA: rimosse le barriere di accessibilità rilevate, con particolare riferimento all'operabilità da tastiera, ai nomi accessibili dei controlli e al contrasto.

Per le API di configurazione sono stati risolti i seguenti bug:

- La creazione o modifica di risorse, azioni o servizi, e l'aggiornamento della gestione token o del caching della risposta, terminavano con errore 500 in assenza di alcuni campi opzionali; i campi assumono adesso il valore di default.

- La configurazione ModI di una fruizione non veniva validata secondo le regole del profilo, consentendo configurazioni non ammesse dalla console.

- Una fruizione ModI 'oauth' creata senza identificativo, kid e keystore non era più leggibile (errore 500).

- Nella creazione di un'API SOAP, la validazione del profilo ModI la trattava come REST.

Per le API di monitoraggio sono stati risolti i seguenti bug:

- I report di Analisi Statistica non coincidevano con quelli della console: senza filtro sull'esito venivano riportate le sole transazioni con successo, gli estremi dell'intervallo non venivano allineati all'unità di campionamento e l'unità temporale, se non indicata, veniva assunta giornaliera.

- Le richieste scartate venivano escluse per default da tutte le ricerche, in contrasto con la console e con l'interfaccia OpenAPI.

- L'API che restituisce il CSV di un tracciato PDND risponde adesso con codice HTTP 404, anziché 500, quando il CSV non è disponibile.
