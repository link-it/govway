.. _3.3.16.1_bug:

Bug Fix 3.3.16.p1
------------------

Sono state risolte le seguenti vulnerabilità relative alle librerie di terza parte:

- CVE-2025-22228: aggiornata libreria 'org.springframework.security:spring-security-crypto' alla versione 5.8.16-gov4j-1;

- CVE-2025-25193: aggiornata libreria 'io.netty:\*' alla versione 4.1.118.Final.

Sono stati risolti i seguenti bug:

- Il token presente nell’header 'Authorization' non veniva inoltrato al backend in caso di API SOAP, nonostante la configurazione ne prevedesse l’inoltro.

- I contatori delle policy di rate limiting non venivano aggiornati correttamente se si disabilitava il tracciamento su database.

- Migliorata la tracciabilità nei casi in cui una policy non veniva elaborata a causa di malfunzionamenti interni, configurazioni errate o altre anomalie. In queste circostanze viene ora assegnato un esito di errore generico, distinto da "Violazione Rate Limiting", consentendo il tracciamento e la diagnosi anche quando la registrazione delle policy violate è disabilitata.

- Nel profilo di interoperabilità 'ModI' una configurazione della risposta che prevedeva la personalizzazione del claim 'aud' veniva ignorata, impedendo l'assegnazione del valore configurato nei token di risposta generati.

- Nella funzionalità di validazione dei contenuti applicativi per API SOAP, sono state risolte le seguenti anomalie:

	- In caso di fallimento nella costruzione dello schema XSD, la collezione degli schemi veniva serializzata nella directory `/tmp`, causando potenziali problemi di esaurimento dello spazio nella partizione temporanea. Ora la collezione degli schemi viene registrata in una directory interna alla directory di log associata a GovWay, migliorando la gestione dello spazio e facilitando il debug.

	- In presenza di schemi XSD con un grafo di import ciclico, l'applicazione andava in out of memory a causa della mancata gestione corretta di tale situazione.

	- In presenza di allegati contenenti uno spazio nel nome del file la validazione dei contenuti falliva.
	
	- Risolta l'anomalia "The prefix 'xml' cannot be bound to any namespace other than its usual namespace; neither can the namespace for 'xm'" be bound to any prefix other than 'xml'."   che poteva accadere se l'API contenente lo schema 'http://www.w3.org/XML/1998/namespace' tra gli allegati caricati.

- La diagnostica di Hazelcast veniva mantenuta attiva per default, generando file di log nel percorso LOG_DIR/hazelcast con il formato diagnostics-#-.log. Sebbene fossero presenti meccanismi di rotazione basati sulla dimensione del file, il nome del log cambiava a ogni riavvio dell'application server, portando nel tempo all'accumulo di file e al rischio di esaurimento dello spazio su disco. Per prevenire questo problema, la diagnostica di Hazelcast viene adesso disabilitata di default.

- (https://github.com/link-it/govway/issues/191) Arricchite tabelle 'statistiche' e 'transazioni_info' con una colonna 'id' definita come primary key.

- Nelle configurazioni dei Key Management Service utilizzati per le funzionalità 'BYOK', veniva erroneamente utilizzata la keyword 'ksm' al posto di 'kms'. È stata apportata la correzione, mantenendo tuttavia il supporto della vecchia keyword per motivi di retrocompatibilità, deprecandone l’utilizzo.

- Le funzionalità di lock basate su semafori sono state estese con uno scheduler che permette il rilascio automatico del lock dopo un timeout configurabile.

- Nel log applicativo 'govway_core', in condizioni limite, avveniva il seguente errore: "Errore durante il dump del soap fault".

- Introdotta una gestione delle risposte Problem Details non conformi; ora il connettore intercetta e registra correttamente le risposte contenenti Problem Details con struttura non valida (es. {"code":500,"status":"internal error"}), evitando la terminazione anomala della transazione. In questi casi, tuttavia, il codice di stato non può essere interpretato, quindi eventuali rispedizioni condizionate su tale informazione non saranno eseguite.


Per la console di gestione sono stati risolti i seguenti bug:

- Se il numero di regole di proxy pass definite superava le 9, l'ordinamento previsto non veniva mantenuto, causando un riordino errato.  In particolare, le regole venivano ordinate in modo lessicografico anziché numerico.

Per la console di monitoraggio sono stati risolti i seguenti bug:

- Corretto un problema nella risoluzione delle informazioni PDND nell’elenco delle transazioni e nei report statistici, che si verificava in presenza di più clientId associati alla stessa organizzazione. In tali casi, solo uno dei clientId veniva risolto correttamente, mostrando i dati dell’organizzazione, mentre per gli altri la risoluzione non avveniva come previsto.

- È stato corretto un comportamento anomalo nel filtro di ricerca delle azioni per le API SOAP nelle sezioni "Transazioni" e "Statistiche", che impediva la selezione delle azioni. Il filtro funzionava correttamente per le API di tipo REST.

- Nella ricerca per token, per principal o per indirizzo ip non veniva effettuato il trim del valore inserito in input.

- (https://gitlab.link.it/gitlab/linkit/dev/govway/govway/-/issues/1583) Aggiunto il suffisso '.rollingFile' ai RollingFile appender in monitor.log4j2.properties per uniformarli agli altri file di configurazione log4j del progetto.
