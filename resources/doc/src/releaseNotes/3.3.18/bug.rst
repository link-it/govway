Bug Fix
-------

Sono state risolte le seguenti vulnerabilità relative alle librerie di terza parte:

- CVE-2025-55163, CVE-2025-58057 e CVE-2025-58056: aggiornata libreria 'io.netty:\*' alla versione 4.1.127.Final

- CVE-2025-8916: aggiornata libreria 'org.bouncycastle:\*' alla versione 1.81

- CVE-2025-48913, CVE-2025-48795: aggiornata libreria 'org.apache.cxf:\*' alla versione 3.6.8

- CVE-2025-31672: aggiornata libreria 'org.apache.poi:poi' alla versione 5.4.1

- CVE-2025-53864: aggiornate libreria 'com.nimbusds:nimbus-jose-jwt' alla versione 10.3.1
	
- CVE-2025-48924: aggiornate libreria 'org.apache.commons:commons-lang3' alla versione 3.18.0

Sono state risolte le seguenti vulnerabilità relative alle console di gestione e monitoraggio:

- CWE-307 (Brute Force)
- CWE-384 (Session Fixation)

Sono state risolte le seguenti vulnerabilità relative alle API di gestione e monitoraggio:

- CWE-307 (Brute Force)


Sono stati risolti i seguenti bug:

- Corretta la gestione della codifica dei caratteri speciali [ e ] nelle query string delle URL. In precedenza, GovWay applicava una doppia codifica ai parametri contenenti questi caratteri (es. test%5B%5D → test%255B%255D), causando errori di interpretazione lato applicativo.

- Corretto errore di inizializzazione degli schemi XSD ("Cannot resolve the name '... to a(n) type definition component'") dovuto alla presenza di inclusioni circolari tra file XSD.

Per il profilo di interoperabilità 'Fatturazione Elettronica' è stata gestita la seguente issue:

- (https://github.com/link-it/govway/issues/214) In caso di errore di comunicazione con il backend (ad esempio per read timeout), il sistema restituisce comunque l’header HTTP 'GovWay-SDI-NomeFile', contenente il nome del file della fattura inviata. In questo modo è possibile identificare correttamente il nome del file associato alla fattura, anche in presenza di errori di comunicazione.

Per la console di gestione sono stati risolti i seguenti bug:

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
