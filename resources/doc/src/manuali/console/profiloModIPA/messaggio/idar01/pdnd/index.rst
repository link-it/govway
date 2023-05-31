.. _modipa_pdnd:

ID_AUTH_REST_01 tramite la Piattaforma Digitale Nazionale Dati (PDND)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il token di autenticazione ID_AUTH_REST_01 descritto nella sezione ':ref:`modipa_idar01`' viene generato e firmato dall'applicativo mittente. Un token con analoga struttura può essere richiesto sulla Piattaforma Digitale Nazionale Dati (PDND) al fine di ottere un voucher di autenticazione spendibile verso l'erogatore. 

Il token può essere richiesto per tutti i servizi registrati sulla PDND per cui un erogatore ha completato il processo di on-boarding definendo:

	- gli attributi che un fruitore deve possedere per poter fare richiesta di fruizione;
	
	- l'identificativo del servizio che il fruitore dovrà riferire per ottenere un token ('*audience*');

	- altri parametri quali la durata del token e il carico di chiamate giornaliere supportate in termini di chiamate complessive e per fruitore.

Per ottenere un token, un applicativo mittente deve:

	- essersi registrato sulla PDND caricando il certificato di firma che utilizzerà per richiedere il token. Al termina della registrazione otterrà un identificativo univoco della propria identità ('*client_id*' o '*sub*') e un identificativo associato al certificato caricato ('*kid*').
	
	- aver registrato una finalità che descrive la motivazione per cui vuole richiedere la fruizione del servizio e il numero di richieste giornaliere che intende effettuare. La creazione di una finalità si completa con l'ottenimento di un suo identificativo univoco denominato '*purposeId*'. 

L'adozione del pattern ID_AUTH_REST_01 rilasciato dalla PDND consente alla ricezione di un messaggio di effettuare i medesimi controlli attuati su un token conforme al pattern descritto nella sezione ':ref:`modipa_idar01`' con la differenza che il token non è più firmato dall'applicativo mittente ma bensì dall'authorization server della PDND e l'identificazione dell'applicativo chiamante non è più attuabile tramite il certificato fornito nell'header del JWT tramite claim 'x5c/x5t/x5u' ma bensì tramite l'identificativo presente nel claim 'client_id'.

Di seguito vengono forniti i dettagli di configurazione necessari ad utilizzare la PDND negli scenari di fruizione o erogazione di un servizio.

.. toctree::
   :maxdepth: 2

    Passi per la configurazione di una fruizione <pdnd_fruizione>
    Passi per la configurazione di una erogazione <pdnd_erogazione>



























