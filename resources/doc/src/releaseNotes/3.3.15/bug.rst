Bug Fix
-------

Sono state risolte le seguenti vulnerabilità relative ai jar di terza parte:

- CVE-2024-34447, CVE-2024-30172, CVE-2024-30171, CVE-2024-29857:
                      
 	- aggiornata libreria 'org.bouncycastle:bcprov-ext-jdk18on' alla versione 1.78.1 (migrazione verso bcprov-jdk18on)
 	- aggiornata libreria 'org.bouncycastle:bcpkix-jdk18on' alla versione 1.78.1
 	- aggiornata libreria 'org.bouncycastle:bcutil-jdk18on' alla versione 1.78.1

- CVE-2024-31573: aggiornata libreria 'org.xmlunit:\*' alla versione 2.10.0

- CVE-2024-22262: aggiornata libreria 'org.springframework:\*' alla versione 5.3.34

- CVE-2024-28752: 

	- aggiornata libreria 'org.apache.cxf:\*' alla versione 3.6.3
	- aggiornata libreria 'org.ow2.asm:asm' alla versione 9.6
	- aggiornata libreria 'org.codehaus.woodstox:stax2-api' alla versione 4.2.2
	- aggiornata libreria 'com.fasterxml.woodstox:woodstox-core' alla versione 6.6.0
	- aggiornata libreria 'org.apache.ws.xmlschema:xmlschema-core' alla versione 2.3.1
	- aggiornata libreria 'org.springframework:\*' alla versione 5.3.33

- CVE-2024-22257: aggiornata libreria 'org.springframework.security:\*' alla versione 5.8.11

- CVE-2024-21742: aggiornata libreria 'org.apache.james:apache-mime4j-\*' alla versione 0.8.10

- CVE-2024-22243: aggiornata libreria 'org.springframework:\*' alla versione 5.3.32

- CVE-2024-25710: aggiornata libreria 'org.apache.commons:commons-compress' alla versione 1.26.0

- CVE-2023-52428: aggiornata libreria 'com.nimbusds:nimbus-jose-jwt' alla versione 9.37.3


Sono stati risolti i seguenti bug:

- in caso di violazione della policy di Rate Limiting con raggruppamento per Token Claim, l'evento emesso non conteneva l'informazione puntuale sul valore del claim;

- (https://github.com/link-it/govway/issues/160) utilizzando un'architettura con database distinti per configurazione e runtime si otteneva un errore non bloccante riportato nei log del database, ad esempio su postgresql: "ERROR:  relation "db_info_console" does not exist at character 15 STATEMENT:  select * from db_info_console order by id DESC";

- nel profilo di interoperabilità 'Fatturazione Elettronica', la disabilitazione della validazione del nome della fattura tramite la proprietà 'org.openspcoop2.protocol.sdi.validazione.nomeFile.enable' causava il seguente errore bloccante se il nome della fattura era conforme a una fatturazione europea che iniziava con il codice 'UB' o 'II': 'Elemento [File] decodifica non riuscita: formato non conosciuto';

- nella funzionalità di consegna asincrona, in alcuni casi limite con connettori con consegna in errore, lo stato della transazione non veniva aggiornato correttamente.


Per la console di gestione sono stati risolti i seguenti bug:

- corretta un'anomalia presente durante il salvataggio di una policy di rate limiting con criterio di raggruppamento per Token Claim 'subject': l'impostazione del criterio non consentiva di entrare nuovamente in modifica della policy e nei log si poteva riscontrare un errore simile al seguente: 'Enum with value [TOKEN_ISSUER] not found';

- è stata risolta una problematica nella gestione degli allegati di una erogazione o API, in cui il pulsante 'cestino' non funzionava correttamente, impedendo la rimozione di un file una volta caricato;

- la modifica del nome di un soggetto non veniva riflessa correttamente sul nome dell'erogazione: nella denominazione del componente 'porta_applicativa', veniva erroneamente aggiunto un carattere '/' finale, causando l'impossibilità di riconoscere l'erogazione al momento della sua invocazione e generando un errore '404 NotFound' restituito al chiamante;

- è stato corretto un problema nella funzionalità di export per il profilo di interoperabilità 'SPCoop' che si verificava quando veniva selezionato un soggetto multi-tenant tra quelli disponibili. Il problema si presentava durante l'export di un'API o di una fruizione che faceva riferimento a un'API il cui soggetto era differente da quello selezionato; all'interno dell'archivio zip, l'API non veniva inclusa;

- corretta anomalia presente durante l'export di una erogazione o fruizione: il plugin riferito per l'autorizzazione dei contenuti non veniva esportato;

- apportate alcune migliorie prestazionali.

Infine è stato rivisto l'algoritmo di generazione delle statistiche poiché le transazioni da inserire in un intervallo temporale potrebbero non essere ancora tutte presenti nella base dati nel caso in cui il generatore di statistiche si avvii nell'intervallo prossimo successivo (es. calcolo intervallo orario 16-17 e generatore che si avvia alle 17:00:06). Transazioni che non rientrano nel calcolo dell'intervallo potrebbero essere relative ad eventi di 'readTimeout' (scritte sulla base dati dopo 120 secondi e oltre) o di lettura dello storico delle transazioni su una base dati in replica (ritardo dovuto alla sincronizzazione). Per gestire correttamente queste casistiche, è stato introdotto un parametro di tradeoff per individuare anche le transazioni che vengono registrate sulla base dati in un tempo successivo alla data di avvio del batch. Il generatore continuerà ad aggiornare i dati aggregati fino a quando la data di esecuzione del generatore non supera l'intervallo temporale corrente aumentato del tradeoff. Per default viene utilizzato un tradeoff di 5 minuti. In questo scenario, ad esempio, il generatore continuerà ad aggiornare i dati dell'intervallo 16-17 fino a quando non verrà avviato dopo le 17:05, consentendo così alle transazioni scritte dopo le 17:00 ma facenti parte dell'intervallo 16-17 di essere incluse nel dato aggregato statistico.

