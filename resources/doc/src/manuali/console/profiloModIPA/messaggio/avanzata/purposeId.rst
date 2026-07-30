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
		- *1-1 con l'applicativo fruitore*: registrazione come proprietà 'purposeId' di un applicativo fruitore e riferito nella token policy tramite il valore '${clientApplicationConfig:purposeId}' se l'applicativo viene identificato tramite l':ref:`apiGwAutenticazione`, o tramite il valore '${tokenClientApplicationConfig:purposeId}' se l'applicativo viene identificato tramite il clientId presente nel token ottenuto dall':ref:`apiGwGestioneToken`;
		- *N applicativi fruitore censiti sulla fruzione*:  registrazione di N proprietà '<clientApplicationName>.purposeId' sulla fruizione, una per ogni applicativo fruitore il cui nome va indicato come prefisso della proprietà (è possibile utilizzare la proprietà senza prefisso come finalità di default); nella token policy deve essere utilizzato il valore '${dynamicConfig:apiSearchByClientApplication(purposeId)}' se gli applicativi vengono identificati tramite l':ref:`apiGwAutenticazione`, o il valore '${dynamicConfig:apiSearchByTokenClientApplication(purposeId)}' se vengono identificati tramite il clientId presente nel token ottenuto dall':ref:`apiGwGestioneToken`;
		- *N fruizioni censite sull'applicativo fruitore*:  registrazione di N proprietà '<nomeApiImpl>.v<versioneApiImpl>.purposeId' sull'applicativo, una per ogni fruizione di API che l'applicativo fruisce indicando il nome come prefisso della proprietà (è possibile utilizzare la proprietà senza prefisso come finalità di default); nella token policy deve essere utilizzato il valore '${dynamicConfig:clientApplicationSearch(purposeId)}' se l'applicativo viene identificato tramite l':ref:`apiGwAutenticazione`, o il valore '${dynamicConfig:tokenClientApplicationSearch(purposeId)}' se viene identificato tramite il clientId presente nel token ottenuto dall':ref:`apiGwGestioneToken`.

.. note::
    Ogni applicativo fruitore viene censito con le credenziali relative ad una specifica modalità di autenticazione, ed è tramite tali credenziali che GovWay lo identifica sulla richiesta: verificando le credenziali del trasporto (https, basic, api-key, principal) tramite l':ref:`apiGwAutenticazione`, oppure il clientId presente nel token tramite l':ref:`apiGwGestioneToken`.

    Le keyword 'clientApplicationConfig', 'apiSearchByClientApplication' e 'clientApplicationSearch' riferiscono l'applicativo identificato nella prima modalità, mentre le corrispondenti 'tokenClientApplicationConfig', 'apiSearchByTokenClientApplication' e 'tokenClientApplicationSearch' riferiscono l'applicativo identificato nella seconda; non sono quindi interscambiabili e vanno scelte coerentemente con la modalità di identificazione attiva sulla fruizione.
