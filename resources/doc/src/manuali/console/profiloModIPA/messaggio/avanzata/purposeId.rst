.. _modipa_sicurezza_avanzate_fruizione_purposeId_scenari:

Finalità (purposeId) utilizzata per una fruizione di API
------------------------------------------------------------

Per ottenere un token dalla PDND un applicativo mittente deve aver registrato una finalità che descrive la motivazione per cui vuole richiedere la fruizione del servizio e il numero di richieste giornaliere che intende effettuare. La creazione di una finalità si completa con l’ottenimento di un suo identificativo univoco denominato "*purposeId*".

Negli scenari di configurazione attuabili su GovWay il purposeId può essere indicato in differenti modi, descritti in questa sezione, a seconda della modalità con cui viene definito il campo 'Purpose ID' all'interno della Token Policy di negoziazione con la PDND descritta nei passi di configurazione della sezione :ref:`modipa_pdnd_fruizione`.

Le modalità supportate sono le seguenti:

- **statica**: il valore del purposeId può essere fornito staticamente nel campo 'Purpose ID' della token policy richiedendo quindi la registrazione di una token policy per ogni finalità;

- **fornita dal client**: il valore può contenere una keyword risolta a runtime in modo da valorizzare il claim 'purposeId' con un valore prelevato dai dati della richiesta. Ad esempio se il censimento dei purposeId viene mantenuto a livello applicativo può essere indicato un header HTTP con cui il richiedente può fornire a GovWay il valore da utilizzare (es. ${header:NOME_HEADER_HTTP}). Si rimanda alla sezione ':ref:`valoriDinamici`' per le varie modalità dinamiche utilizzabili; 

- **proprietà degli oggetti**: di seguito vengono invece fornite alcune indicazioni per mantenere la registrazione del purposeId sul registro di GovWay supportando differenti scenari (per maggiori dettagli si rimanda alla sezione :ref:`avanzate_dynamic_config`): 

		- *1-1 con la fruizione*: registrazione come proprietà 'purposeId' della fruizione e riferito nella token policy tramite il valore '${config:purposeId}';
		- *1-1 con l'applicativo fruitore*: registrazione come proprietà 'purposeId' di un applicativo fruitore e riferito nella token policy tramite il valore '${clientApplicationConfig:purposeId}';
		- *N applicativi fruitore censiti sulla fruzione*:  registrazione di N proprietà '<clientApplicationName>.purposeId' sulla fruizione, una per ogni applicativo fruitore il cui nome va indicato come prefisso della proprietà; nella token policy deve essere utilizzato il valore '${dynamicConfig:apiSearchByClientApplication(purposeId)}';
		- *N fruizioni censite sull'applicativo fruitore*:  registrazione di N proprietà '<nomeApiImpl>.v<nomeApiImpl>.purposeId' sull'applicativo, una per ogni fruizione di API che l'applicativo deve fruitore il cui nome va indicato come prefisso della proprietà; nella token policy deve essere utilizzato il valore '${dynamicConfig:clientApplicationSearch(purposeId)}'.
