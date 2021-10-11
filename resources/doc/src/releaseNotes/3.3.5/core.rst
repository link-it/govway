Miglioramenti alle funzionalità base dell'API Gateway
------------------------------------------------------------


- aggiunti contesti 'api-soap' e 'api-rest' dove è abilitata solamente la tecnologia indicata nel nome;

- i criteri dinamici, utilizzabili nelle trasformazioni, nei criteri di autorizzazione, nella definizione di endpoint dinamici dei connettori etc, consentono adesso di:

	- accedere alle proprietà definite negli applicativi e nei soggetti;

	- accedere alle proprietà indicate nella configurazione generale di GovWay;

	- leggere variabili di sistema e proprietà della jvm;

- la presenza dei file di configurazione esterna, generati dall'installer, non sono più obbligatori per il corretto avvio di GovWay;

- in una installazione con cluster dinamico, viene adesso utilizzato come id del nodo l'identificativo del gruppo;

- aggiunto il tipo di errore 'ConnectorNotFound' utilizzato per identificare la casistica in cui non è stato possibile individuare il connettore che implementa l'API, in configurazioni con connettori multipli (es. con consegna condizionale).

