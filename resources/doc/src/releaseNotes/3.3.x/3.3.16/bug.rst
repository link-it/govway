Bug Fix
-------

Sono state risolte le seguenti vulnerabilità relative alle librerie di terza parte:

- CVE-2025-23184: 

	- aggiornata libreria 'org.apache.cxf:\*' alla versione 3.6.5
	- aggiornata libreria 'org.ow2.asm:asm' alla versione 9.7.1	

- CVE-2024-38827: aggiornata libreria 'org.springframework.security:\*' alla versione 5.8.16

- CVE-2024-38829: aggiornata libreria 'org.springframework.ldap:\*' alla versione 2.4.4

- CVE-2024-47535: aggiornata libreria 'io.netty:\*' alla versione 4.1.115

- Corrette nuove segnalazioni di vulnerabilità emerse in seguito all'aggiornamento dei seguenti tools di analisi statica:

	- SpotBugs alla versione 4.8.6;
	- SonarQube alla versione '10.7'.


Sono stati risolti i seguenti bug:

- risolta anomalia presente nella funzionalità 'FileTrace' per la registrazione delle transazioni, dove l'informazione 'requester', se registrata con l'opzione 'logBase64', veniva codificata in Base64 due volte;

- quando la funzionalità di proxy pass reverse per gli header HTTP è attiva, se un header Location contiene un'URL con query parameter che non corrisponde a quella del connettore (e quindi non viene tradotta), l'header veniva inoltrato al client senza i query parameters.

Per la console di gestione sono stati risolti i seguenti bug:

- utilizzando il database 'SQLServer', si verificava un errore inatteso accedendo alla sezione 'Handler' nelle opzioni avanzate di un'erogazione/fruizione e cliccando su una qualsiasi delle liste relative alla richiesta o alla risposta.

Per la console di monitoraggio sono stati risolti i seguenti bug:

- il filtro utilizzato per la ricerca delle transazioni o per la generazione di report statistici non permetteva di selezionare una risorsa o un'azione semplicemente scegliendo l'API. Era necessario selezionare la voce Implementazione API. Ora, il filtro per risorse/azioni consente la selezione sia tramite l'API che tramite la sua implementazione, offrendo maggiore flessibilità.

- corretta anomalia che causava la registrazione di comandi SQL nel file di log 'govway_monitor_core.log' invece che nel file 'govway_monitor_sql.log'.


Infine è stato aggiunto su tutti i tools un controllo dello stato della connessione al rilascio al datasource che consente di:

- verificare la presenza di transazioni aperte (autoCommit disabilitato);
- effettuare il log dello stack trace per identificare la classe responsabile;
- richiamare `setAutoCommit(true)` per ripristinare lo stato corretto.

Grazie all'introduzione del controllo sulla connessione è stato individuata e corretta un'anomalia presente in alcuni casi limite, in cui i driver per l'accesso al database delle configurazioni restituivano al pool una connessione con l'opzione autoCommit disabilitata.
