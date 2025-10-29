Bug Fix
-------

Per la console di gestione sono stati risolti i seguenti bug:

- Introdotta una nuova modalità di gestione delle operazioni di delete ed export attraverso l'utilizzo di una form con method POST al posto dell'invocazione di una GET. La nuova modalità consente di evitare il formarsi di una url eccessivamente lunga che poteva essere bloccata dai frontend http.

Per la API di configurazione sono stati risolti i seguenti bug:

- Corretto http status, da 204 a 201, restituito in caso di operazione effettuata con successo per le risorse:

	- POST /erogazioni/{nome}/{versione}/gruppi/{nome_gruppo}/azioni
	- POST /fruizioni/{erogatore}/{nome}/{versione}/gruppi/{nome_gruppo}/azioni

