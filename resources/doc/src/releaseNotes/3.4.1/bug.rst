Bug Fix
-------

Sono state risolte le seguenti vulnerabilità relative alle librerie di terza parte:

- CVE-2025-41249 e CVE-2025-41242: aggiornata libreria 'org.springframework:\*' alla versione 6.2.11

- CVE-2025-41248: aggiornata libreria 'org.springframework.security:\* alla versione 6.5.5

- CVE-2025-55163, CVE-2025-58057 e CVE-2025-58056: aggiornata libreria 'io.netty:\*' alla versione 4.1.127.Final

- CVE-2025-8916: aggiornata libreria 'org.bouncycastle:\*' alla versione 1.81

- CVE-2025-4949: aggiornata libreria 'org.eclipse.jgit:org.eclipse.jgit' alla versione 7.3.0.202506031305-r

- CVE-2025-48913: aggiornata libreria 'org.apache.cxf:\*' alla versione 4.1.3 (3.4.x)

- CVE-2025-7962: aggiornata libreria 'org.eclipse.angus:angus-mail' alla versione 2.0.4

- CVE-2025-48924: sostituita la dipendenza 'commons-lang:commons-lang' con 'org.apache.commons:commons-lang3'

- CVE-2025-31672: aggiornata libreria 'org.apache.poi:poi' alla versione 5.4.1

- CVE-2025-53864: aggiornate libreria 'com.nimbusds:nimbus-jose-jwt' alla versione 10.3.1
	
- CVE-2025-48924: aggiornate libreria 'org.apache.commons:commons-lang3' alla versione 3.18.0

Sono state risolte le seguenti vulnerabilità relative alle console di gestione e monitoraggio:

- CWE-307 (Brute Force)
- CWE-384 (Session Fixation)
- CWE-200 (Information Exposure): esposizione delle Versioni delle Librerie Frontend

Sono state risolte le seguenti vulnerabilità relative alle API di gestione e monitoraggio:

- CWE-307 (Brute Force)


Sono stati risolti i seguenti bug per la componente runtime del gateway:

- Risolta una problematica che impediva l’utilizzo delle proprietà Java http.proxy* e https.proxy* nella nuova libreria HttpCore, impiegata come client HTTP dal gateway. 

- Tutti gli accessi HTTP verso risorse esterne (ad esempio per la negoziazione dei token, l’introspezione dei token, ecc.) vengono adesso gestiti tramite la libreria Apache HttpClient 5 (org.apache.hc.client5).

- Corretta la gestione della codifica dei caratteri speciali [ e ] nelle query string delle URL. In precedenza, GovWay applicava una doppia codifica ai parametri contenenti questi caratteri (es. test%5B%5D → test%255B%255D), causando errori di interpretazione lato applicativo.

- Corretto errore di inizializzazione degli schemi XSD ("Cannot resolve the name '... to a(n) type definition component'") dovuto alla presenza di inclusioni circolari tra file XSD.

- Risolta anomalia che rendeva inutilizzabile l'utilizzo della validazione dei contenuti con la libreria 'swagger_request_validator' su wildfly.
  Nei log veniva riportato il seguente errore:
  
    Caused by: java.lang.NoClassDefFoundError: Could not initialize class com.github.fge.jsonschema.core.util.RegexECMA
    ...
    Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.NoClassDefFoundError: jdk/dynalink/Namespace 

Per il profilo di interoperabilità 'Fatturazione Elettronica' è stata gestita la seguente issue:

- (https://github.com/link-it/govway/issues/214) In caso di errore di comunicazione con il backend (ad esempio per read timeout), il sistema restituisce comunque l’header HTTP 'GovWay-SDI-NomeFile', contenente il nome del file della fattura inviata. In questo modo è possibile identificare correttamente il nome del file associato alla fattura, anche in presenza di errori di comunicazione.

Per entrambe le console è stato risolto un problema che impediva:

- (https://github.com/link-it/govway/issues/250) l’autenticazione alle console per le utenze con password contenenti i caratteri & # % ^ < >;
- (https://github.com/link-it/govway/issues/249) la registrazione di nuovi utenti con password che includessero uno di questi caratteri.

Per la console di gestione sono stati risolti i seguenti bug:

- Durante il tentativo di modifica della descrizione o del tag di un’API, l’operazione falliva restituendo l’errore: 'Dati incompleti. È necessario indicare un nome'.

- (https://github.com/link-it/govway/issues/244) Nel controllo degli accessi, le configurazioni di autorizzazione per token claims o contenuti con più controlli su più righe venivano salvate su una sola riga. Il comportamento anomalo è stato corretto.

- Risolta anomalia che non consentiva di aggiornare una Token Policy inserendo un valore dinamico contenente caratteri '{' o '}' (es. nel campo purposeId). L'applicazione restituiva un errore che segnalava la mancata valorizzazione obbligatoria del campo, come se il valore non fosse stato inserito.

- Migliorata la gestione delle informazioni sensibili nel report di auditing quando la funzionalità BYOK non è attiva.

- Risolto un problema che impediva l’accesso alla console dopo la disabilitazione e successiva riabilitazione dell’opzione Log4j Auditing. In tali condizioni, il log riportava l’errore: "Inizializzazione appender[log4jAppender] non riuscita: InputStream [audit.log4j2.properties] non trovato".

Per la console di monitoraggio sono stati risolti i seguenti bug:

- Nei report CSV/XLS generati tramite la funzionalità 'Reportistica - Configurazione API', per le erogazioni con profilo di interoperabilità ModI tramite PDND, la colonna 'Autorizzazione Token (Applicativi Autorizzati)' non risultava valorizzata.

Per le API di configurazione sono stati risolti i seguenti bug:

- La registrazione di un servizio applicativo nel controllo accessi di un’erogazione, tramite l’endpoint '/erogazioni/{nome_erogazione}/{versione_erogazione}/configurazioni/controllo-accessi/autorizzazione/token/applicativi' risultava troppo restrittiva rispetto alla console di gestione 'govwayConsole'. In particolare, con la seguente configurazione:

	- API di tipo ModI con generazione token di tipo PDND
	- Servizio applicativo client esterno di tipo ModI con sicurezza messaggio Authorization PDND
  
  la registrazione falliva via API, mentre tramite console avveniva correttamente. È stata quindi allineata la logica di validazione dell’API di configurazione a quella della console di amministrazione, consentendo la corretta registrazione dei servizi applicativi esterni di tipo ModI con sicurezza PDND.
  
Per le API di monitoraggio sono stati risolti i seguenti bug:  
  
- negli scenari di configurazione in cui nella registrazione di una transazione viene incluso esclusivamente gli header HTTP della chiamata senza il relativo payload, la risorsa /monitoraggio/transazioni/{id} non restituiva gli header.
