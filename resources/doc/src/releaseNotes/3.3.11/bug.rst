Bug Fix
-------

Sono state risolte le seguenti vulnerabilità relative ai jar di terza parte:

- CVE-2022-45688: aggiornata libreria 'org.json:json' alla versione 20230227;

- CVE-2023-24998: aggiornata libreria 'commons-fileupload' alla versione 1.5 .

Sono stati risolti i seguenti bug:

- l'autorizzazione puntuale in una erogazione con profilo di interoperabilità 'ModI' non veniva effettuata se la lista degli applicativi autorizzati veniva lasciata vuota;

- le regole di autorizzazione definite nell'autorizzazione dei contenuti o nell'autorizzazione per token claims non venivano controllate nell'ordine in cui erano state configurate;

- la validazione di un token che presentava date (exp,iat,nbf) serializzate in un formato numerico esponenziale falliva generando un errore simile al seguente: 'Token non valido: For input string: "1.67"'


Per la console di gestione sono stati risolti i seguenti bug:

- il controllo dei certificati di una token policy di negoziazione andava in errore quando la modalità scelta era 'Definito nell'applicativo ModI';

- non era più possibile creare una token policy in caso di servizi OCSP disabilitati (file ocsp.properties non presente o nessuna policy definita al suo interno);

- sono stati corretti i seguenti problemi relativi alla configurazione dell'autorizzazione per contenuti:

	- se nelle regole erano presenti commenti (#) potevano presentarsi segnalazioni errate dovuti a 'commenti duplicati' quando la linea inserita era la stessa in due o più righe;

	- in alcuni casi l'ordine di inserimento delle regole non veniva preservato in fase di salvataggio;

	- non venivano gestite correttamente più entry con la stessa chiave (la stessa problematica è stata risolta anche nella configurazione dell'autorizzazione per token claims).


Per la console di monitoraggio sono stati risolti i seguenti bug:

- risolta problematica che non consentiva di visualizzare la scheda dei messaggi duplicati nel dettaglio di una transazione.
