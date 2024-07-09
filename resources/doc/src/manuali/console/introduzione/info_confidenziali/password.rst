.. _console_informazioni_confidenziali_password:

Credenziali di entità censite nel registro di GovWay
------------------------------------------------------

Si tratta di credenziali utilizzate:

- dai soggetti (:ref:`soggetto`) e dagli applicativi (:ref:`applicativo`), registrati con :ref:`modalitaAccessoApiKey` o :ref:`modalitaAccessoHttpBasic`, per invocare le API esposte da GovWay;

- dagli utenti per accedere alle console di gestione e monitoraggio descritte nella sezione :ref:`utenti`. 

Le credenziali sono disponibili solamente nell'avviso visualizzato in seguito alla creazione dell'entità (es. :numref:`authBasicFig2`) e successivamente non sono più consultabili; in caso di smarrimento è necessario procedere con un aggiornamento della credenziale. 

Le credenziali vengono cifrate per default con un algoritmo di cifratura 'SHA-512-based Unix crypt ($6$)'; maggiori dettagli vengono forniti nella sezione :ref:`configAvanzataPassword`. 
