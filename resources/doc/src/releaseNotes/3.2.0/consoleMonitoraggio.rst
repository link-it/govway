Miglioramenti alla Console di Monitoraggio
-----------------------------------------------------

Sono state introdotte le seguenti nuove funzionalità:

- *Ricerca delle Transazioni*: è stata riorganizzata, classificando in sezioni le diverse modalità di ricerca:

  - Ricerca generica: consente di effettuare ricerche tramite la selezione di valori in liste ('base') o campi liberi ('avanzata').

  - Ricerca per mittente: consente di selezionare il fruitore della richiesta in base a vari criteri:

	 - Valori dei claims di un Token
	 - Identità del Soggetto
	 - Identità dell'applicativo
	 - Principal del chiamante
	 - Indirizzo IP del client

  - Ricerca per identificativo: consente di individuare una transazione tramite l'identificativo applicativo, l'id del messaggio o l'id di transazione.

- *Tipologia delle Transazioni*: è stata reintrodotta la possibilità di ricerca senza dover indicare obbligatoriamente la tipologia della transazione (erogazione/fruizione). 

- *Indirizzo IP del Chiamante*: è stata aggiunta la possibilità di effettuare la ricerca di transazioni specificando l'indirizzo IP del chiamante. L'indirizzo IP può riferirsi all'indirizzo IP del client o al valore dell'header http 'X-Forwarded-For'.
  L'indirizzo IP può essere inoltre utilizzato per filtrare i risultati dei report statistici (Distribuzione per API, per Operazione, per Soggetto ...). Infine è stata introdotta un nuovo tipo di report basat sugli indirizzi IP dei chiamanti.

-  *Ricerca per Identificativo di Collaborazione*: aggiunta la possibilità di effettuare ricerche per individuare tutte le transazioni correlate attraverso il medesimo *identificativo di collaborazione*.
 
-  *Ricerca per Identificativo della Richiesta*: consente di invidivuare una transazione che è correlata ad una precedente richiesta, tramite l'*id di riferimento richiesta*

