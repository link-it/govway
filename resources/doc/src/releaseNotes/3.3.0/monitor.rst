Miglioramenti alle Console di Monitoraggio
-----------------------------------------------------

Sono state apportate le seguenti migliorie:

-  *Restyling grafico dello storico delle Transazioni*: le informazioni sulle transazioni vengono riportate tramite una nuova grafica, con un approccio non più tabellare ma orientato all'ottimizzazione degli spazi e del posizionamento delle informazioni di interesse per il monitoraggio. È stata inoltre rivista anche la maschera di dettaglio di una transazione al fine di suddividere le molteplici informazioni riportate in più tab categorizzati.

-  *Nuova esiti associati alle Transazioni*:

	- Token non Presente: la richiesta non presente un token;
	- Autenticazione Token Fallita: nel token ricevuto non sono presenti dei claim obbligatori richiesti per l'accesso all'API invocata;
	- API non Individuata: la richiesta non permette di individuare una API registrata sul Gateway;
	- Operazione non Individuata: la richiesta non indirizza un'operazione esistente sull'API invocata.

- *Nuovi criteri di ricerca delle Transazioni basate sugli esiti*: sia le ricerche nello storico che le informazioni statistiche possono adesso essere ricercate per due nuovi raggruppamenti degli esiti:

	- Errori di Consegna: in questo gruppo sono collezionati tutti gli esiti che individuano un errore generato dal backend applicativo (Fault Applicativi e/o codici di ritorno 4xx e 5xx) o un errore di connettività verso il backend.
	- Richiesta Scartate: in questo gruppo sono presenti gli esiti che riguardano richieste di erogazione o fruizione malformate (es. api non individuate, operazioni non individuate, errori di autenticazione ...)

  Inoltre negli altri criteri di ricerca con qualsiasi esito è possibile comunque indicare se le richieste scartate devono essere prese in considerazione o meno; per default le richieste scartate non vengono considerati ai fini del risultato della ricerca.

-  *Numero di Risultati*: nello storico delle transazioni è adesso possibile indicare il numero di risultati della ricerca che si desidera ottenere (default: 25).
