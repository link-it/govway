Bug Fix
-------

Sono state risolte le seguenti vulnerabilità relative ai jar di terza parte:

- CVE-2023-33201: aggiornata libreria 'org.bouncycastle:\*' alla versione 1.74;

- CVE-2023-34411: aggiornata libreria 'com.fasterxml.woodstox:woodstox-core' alla versione 6.5.1, 'org.apache.cxf:\*' alla versione 3.6.1 e 'org.ow2.asm:asm' alla versione 9.5;

- CVE-2020-8908: aggiornata libreria 'com.google.guava:guava' alla versione 32.0.0-jre;

- CVE-2023-33264: aggiornata libreria 'com.hazelcast:hazelcast' alla versione 5.3.0;

- CVE-2023-20862: aggiornata libreria 'org.springframework.security:spring-security-\*' alla versione 5.8;

- CVE-2017-9096, CVE-2022-24196 e CVE-2022-24197: sostituita libreria 'com.lowagie:itext' versione 2.1.7.js7 con le librerie 'org.apache.pdfbox:\*' versione 2.0.27 e 'com.github.dhorions:boxable' versione 1.7.0.


Sono stati risolti i seguenti bug:

- (https://github.com/link-it/govway/issues/133) tentando di avviare la piattaforma GovWay sotto Windows si otteneva un errore causato dal mancato supporto agli attributi posix: "java.lang.UnsupportedOperationException: 'posix:permissions' not supported as initial attribute";

- (https://github.com/link-it/govway/issues/128) l'accesso ad un keystore pkcs12 creato importando un archivio pkcs12 al suo interno falliva con il seguente errore: "keystore password was incorrect";

- su database SQLServer veniva segnalato il seguente errore dovuto ad una colonna definita in minuscolo e riferita nella query in maiuscolo: "ERROR .... [GestoreCorrelazioneApplicativa.getCorrelazioniStoriche] errore, queryString[SELECT TOP 50 id,SCADENZA FROM CORRELAZIONE_APPLICATIVA WHERE ( ORA_REGISTRAZIONE < ? )]: Invalid column name 'ORA_REGISTRAZIONE'.";

- nella configurazione di default, su API SOAP, il riconoscimento dell'operazione avviene comparando il path indicato dopo la base-url rispetto alle operazioni della API. Il riconoscimento dell'operazione basata sulla url non funzionava correttamente in presenza di una url formata da molteplici endpoint come ad esempio: "http://host/govway/ente/service/v1/azione1,http://host/govway/ente/service/v1/azione3". L'operazione che veniva erroneamente individuata era 'azione3'. La problematica risiedeva nell'espressione regolare generata per default dalla console di configurazione e associata alla funzionalità di identificazione dell'operazione, nell'esempio:

	- .\*/(?:gw\_)?ente/(?:gw\_)?service/v1/([^/\|^?]\*).\*
  
  L'espressione è stata corretta in:

	- /(?:gw\_)?ente/(?:gw\_)?service/v1/([^/?]\*).\*


Per la console di gestione sono stati risolti i seguenti bug:

- l'apertura di un nuovo tab tramite le breadcump rendeva la console inutilizzabile sul nuovo tab. Per provocare l'anomalia si doveva procedere come segue:

	- aprire una lista di api, erogazioni, fruizioni, soggetti o applicativi;
	- entrare nel dettaglio di un oggetto;
	- cliccare con il tasto destro sulla breadcump che indica l'elenco degli oggetti ed aprire un nuovo tab;
	- spostarsi sul nuovo tab;
	- entrare nel dettaglio di un oggetto qualsiasi: la console andava in errore.

- la maschera di creazione di un soggetto con profilo di interoperabiltà 'API Gateway' o 'ModI' consente adesso di crearlo con una tipologia 'Fruitore' senza dover obbligatoriamente fornire delle credenziali; lo scenario serve a definire il soggetto che verrà poi associato all'applicativo fruitore che possiede le credenziali.

