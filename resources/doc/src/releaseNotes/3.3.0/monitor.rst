Miglioramenti alla Console di Monitoraggio
-----------------------------------------------------

Sono stati apportati i seguenti miglioramenti:

-  *Restyling grafico dello storico delle Transazioni*: le informazioni sulle transazioni vengono riportate tramite una nuova veste grafica, con un approccio non più tabellare ma orientato all'ottimizzazione degli spazi e del posizionamento delle informazioni di interesse per l'operatore. È stata inoltre rivista anche la maschera di dettaglio di una transazione al fine di suddividere in maniera più razionale le numerose informazioni presenti.

-  *Nuovi esiti associati alle Transazioni*:

	- Token non Presente: la richiesta non presenta un token;
	- Autenticazione Token Fallita: nel token ricevuto non sono presenti dei claim configurati come obbligatori per l'accesso alla API;
	- API non Individuata: la richiesta non indirizza una API registrata sul Gateway;
	- Operazione non Individuata: la richiesta non indirizza un'operazione prevista sulla API invocata.

- *Nuovi criteri di ricerca delle Transazioni basate sugli esiti*: sia le ricerche nello storico che le informazioni statistiche possono adesso essere ricercate per due nuovi raggruppamenti degli esiti:

	- Errori di Consegna: in questo gruppo sono collezionate tutte le transazioni con esiti che individuano un errore generato dal backend applicativo (Fault Applicativi e/o codici di ritorno 4xx e 5xx) o un errore di connettività verso il backend.
	- Richieste Scartate: in questo gruppo sono collezionate tutte le transazioni con esiti che riguardano richieste di erogazione o fruizione malformate (es. api non individuate, operazioni non individuate, errori di autenticazione ...)

  Inoltre per gli altri criteri di ricerca è sempre possibile indicare se le richieste scartate devono essere prese in considerazione o meno; per default le richieste scartate non vengono considerate ai fini del risultato della ricerca.

-  *Numero di Risultati*: nello storico delle transazioni è adesso possibile indicare il numero di risultati della ricerca che si desidera ottenere (default: 25).
