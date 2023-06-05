.. _modipa_infoUtente_audit:

AUDIT_REST_01 / AUDIT_REST_02
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il pattern AUDIT_REST nelle sue varie declinazioni consente all'erogatore di identificare la specifica provenienza di ogni singola richiesta di accesso ai dati effettuta dal fruitore. 

Le Linee Guida indicano che l'erogatore e il fruitore devono individuare i claim da includere nel JWT di audit e suggeriscono i seguenti dati che dovranno essere presenti nel token generato dal fruitore, per ogni richiesta effettuata:

- userID, un identificativo univoco dell'utente interno al dominio del fruitore che ha determinato l'esigenza della request di accesso all'e-service dell'erogatore;

- userLocation, un identificativo univoco della postazione interna al dominio del fruitore da cui è avviata l'esigenza della request di accesso all'e-service dell'erogatore;

- LoA, livello di sicurezza o di garanzia adottato nel processo di autenticazione informatica nel dominio del fruitore.

Le Linee Guida definiscono 2 pattern utilizzabili sia per API REST che per API SOAP, che si aggiungono al precedente pattern legacy di GovWay:

- :ref:`modipa_infoUtente_audit01`: definisce la struttura del token di audit utilizzabile tramite due modalità:
	
	- un criterio di trust realizzato tramite il materiale crittografico depositato sulla PDND;
	- un trust diretto fruitore-erogatore attraverso l'utilizzo di certificati X509. 

- :ref:`modipa_infoUtente_audit02`: pattern che estende il precedente aggiungendo la correlazione tra il token di autenticazione e il token di audit. Il pattern richiede un trust realizzato tramite il materiale crittografico depositato sulla PDND.

- :ref:`modipa_infoUtente_legacy`: soluzione legacy di GovWay già presente nelle precedenti versioni.



.. toctree::
        :maxdepth: 2

	audit01/index
        audit02
	auditLegacy
