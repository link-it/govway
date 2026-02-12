Bug Fix
-------

Sono state risolte le seguenti vulnerabilità relative alle librerie di terza parte:

- CVE-2025-67735: aggiornata libreria 'io.netty:\*' alla versione 4.2.9.Final;

- CVE-2025-68161: aggiornata libreria 'org.apache.logging.log4j:\*' alla versione 2.25.3;

- CVE-2025-66453: aggiornata libreria 'org.mozilla:rhino' alla versione 1.8.1.

- Corrette nuove segnalazioni di vulnerabilità emerse in seguito all'aggiornamento dei seguenti tools di analisi statica:

        - SpotBugs alla versione 4.9.8;
        - SonarQube alla versione '25.11.0.114957'.

Sono stati risolti i seguenti bug per la componente runtime del gateway:

- Riviste funzionalità di sicurezza messaggio e gestione token per supportare chiavi a curva ellittica.

- Risolta eccezione "UniqueIdentifierManager.newID() non riuscita" causata da InterruptedException dopo timeout di acquisizione semaforo. Introdotta eccezione dedicata 'SemaphoreTimeoutException' che preserva lo stato del thread, permettendo la corretta generazione di ID univoci in scenari di alta contesa delle risorse.

- Risolto errore di persistenza delle transazioni su PostgreSQL/MySQL causato da null byte (0x00) presenti in risposte binarie che finivano nelle colonne TEXT del database. Introdotta sanitizzazione dei null byte sulle colonne TEXT che possono contenere dati provenienti da input esterni.

- Aggiunta troncatura configurabile dei messaggi di errore dei validatori REST/SOAP per limitare la dimensione dell'errore ritornato quando il messaggio del validatore include il contenuto della richiesta/risposta.

- Invocando un'erogazione o una fruizione con azione non riconosciuta, a fronte di una configurazione che prevede per i diagnostici un livello di severità 'all', non si riscontrava l'effettiva emissione dei diagnostici con severità debug.

- Aggiornata la gestione delle govlet SUAP: risolto un problema di validazione delle richieste nel servizio 'ET-to-BO' che impediva il corretto riconoscimento dell'evento 'cdss_convened', dovuto a una definizione incoerente del campo event nella specifica OpenAPI.

Sono state risolte le seguenti problematiche relative alle console e alle API:

- Rivisti descrittori web.xml per garantire la compatibilità con l'opzione Tomcat '-Dorg.apache.catalina.STRICT_SERVLET_COMPLIANCE=true'.

Per la console di gestione sono stati risolti i seguenti bug:

- (https://github.com/link-it/govway/issues/272) Corretta anomalia che si presentava durante la modifica del nome di un soggetto quando il codice IPA generato dall'installer (senza /) differiva da quello calcolato di default (con /).

- Nella gestione delle policy di rate limiting sono state corrette le seguenti anomalie:

	- corretto il filtro nella lista delle policy attivate per gestire anche la tipologia 'richieste simultanee' che non veniva correttamente visualizzata una volta creata;
        - corretto reset automatico dei valori di 'Misurazione' e 'Algoritmo' nelle opzioni avanzate di Rate Limiting quando non sono più compatibili con l'implementazione selezionata.

- Corretta anomalia che impediva l'abilitazione/disabilitazione dei timer di generazione e pubblicazione del tracciamento PDND dalla console.

- Il testo dei tooltip che contenevano un ritorno a capo veniva visualizzato in modo errato, mostrando la sequenza '&#10;'.

- Corretto path hard-coded nella pagina index.html per consentire il deploy con context path personalizzati.

- Corretta un'anomalia in contesto multi-tenant nella gestione delle fruizioni verso la stessa API: in fase di creazione di una nuova fruizione da parte di un secondo utente gestore, venivano erroneamente riportate le utenze della fruizione precedentemente creata da un altro utente. Inoltre, la modifica della nuova fruizione poteva propagare in modo errato l'utenza dell'ultima modifica anche alla fruizione originale.

- Risolta anomalia che codificava come carattere speciale la dicitura '&not' nelle textarea. Nell'intervento è stato riorganizzato il meccanismo di escape HTML.

- Le sezioni relative al controllo degli accessi per le erogazioni e le fruizioni non risultavano più richiudibili.

- Nella funzionalità di import/export sono state corrette le seguenti anomalie:

	- Corretta problematica presente nella funzionalità di export che generava una risposta con codice http 302 con URL eccessivamente lungo; gli oggetti da esportare vengono ora salvati in sessione invece che nella query string.

	- L'esportazione di un'erogazione/fruizione che includeva una trasformazione REST, nella quale nella configurazione della richiesta non veniva specificato il metodo HTTP, generava un pacchetto che non risultava reimportabile tramite console. In fase di importazione del pacchetto esportato, la console segnala il seguente errore di validazione/sintassi: "cvc-complex-type.4: Attribute 'metodo' must appear on element 'ns2:trasformazione-rest'."

	- Corretta un'anomalia che, durante un'operazione di importazione, riportava erroneamente nella nuova base dati le utenze e le date presenti negli archivi.

- Eliminato supporto IE11.

Per la console di monitoraggio sono stati risolti i seguenti bug:

- (https://github.com/link-it/govway/issues/269) Corretto il flusso di autenticazione OIDC nella console, evitando la validazione dei campi del login locale quando entrambe le modalità di autenticazione (locale e OIDC) sono abilitate.

- Risolto blocco della console di monitoraggio durante l'esecuzione del flusso 'Login → Navigazione → Logout → Login → Navigazione', causato da un errore di Content Security Policy (CSP).

- Rimosso FusionCharts (Flash): eliminata completamente la libreria FusionCharts e tutti i file .swf, già di fatto inutilizzati poiché abilitati solo su browser obsoleti, migrando i grafici delle statistiche esclusivamente su SVG (D3/C3) e rimuovendo ogni logica condizionale Flash/SVG.

- Eliminato supporto IE11.

Per le API di configurazione sono stati risolti i seguenti bug:

- (https://github.com/link-it/govway/issues/277) Accedendo a erogazioni o fruizioni di API con sorgente token 'PDND' o 'OAuth' si otteneva il seguente errore: "Property modipaSecurityMessageRestRequestX509Cert non trovata"

- Aggiunta gestione dei campi 'Key Id (kid) del Certificato' e 'Identificativo' nella configurazione della sicurezza messaggio della risposta per le erogazioni REST ModI.

- Effettuando la registrazione di un applicativo ModI con coppia di chiavi nel keystore e utilizzandolo per la fruizione di una API, la cui token policy è configurata per utilizzare le chiavi dell'applicativo, si otteneva l'errore: "Il tipo di keystore indicato nella token policy 'api-pdnd-attestazione' non è utilizzabile: Property [modipaKeystoreKeyPairAlgo] not found". L'errore non si verificava se la configurazione veniva effettuata tramite console.
