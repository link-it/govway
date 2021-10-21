Miglioramenti alle funzionalità base dell'API Gateway
------------------------------------------------------------


- aggiunti i contesti 'api-soap', limitata alla gestione di API SOAP e 'api-rest' limitata alla gestione di API REST;

- i criteri dinamici, utilizzabili nelle trasformazioni, nei criteri di autorizzazione, nella definizione di endpoint dinamici dei connettori etc, consentono adesso di:

	- accedere alle proprietà definite negli applicativi e nei soggetti;

	- accedere alle proprietà indicate nella configurazione generale di GovWay;

	- leggere variabili di sistema e proprietà della jvm;

- la presenza dei file di configurazione esterna generati dall'installer, non è più necessaria per il corretto avvio di GovWay e possono essere quindi presenti solo nel caso di effettiva necessità di personalizzazione dei valori di default;

- in una installazione di tipo cluster dinamico, viene adesso utilizzato come id del nodo l'identificativo del gruppo;

- aggiunto il tipo di errore 'ConnectorNotFound' utilizzato per identificare la casistica in cui non sia stato possibile individuare il connettore che implementa l'API, in configurazioni con connettori multipli (es. con consegna condizionale);

- miglioramenti prestazionali introdotti utilizzando il semaforo java.util.concurrent.Semaphore al posto dei blocchi synchronized.
 
