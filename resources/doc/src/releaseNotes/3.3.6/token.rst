Miglioramenti alla funzionalità di Gestione dei Token
-----------------------------------------------------

.. note::

   La funzionalità è stata introdotta nella versione '3.3.6.p1'

Nella negoziazione di un Token tramite la modalità JWT Signed è adesso possibile:

	- configurare i parametri da inserire nell'header non firmato (kid, x5c, x5t ...);
	- personalizzare i valori dei claim inseriti nel payload firmato tramite parti dinamiche che vengono risolte a runtime dal Gateway;
	- aggiungere claim ulteriori oltre a quelli previsti dallo standard;
	- possibilità di abilitare una modalità di compatibilità con la Piattaforma Digitale Nazionale Dati (PDND), che permette di configurare gli ulteriori campi richiesti dalla PDND (purposeId, sessionInfo).

Nella validazione dei token è stata introdotta la possibilità di configurare un formato di token definito tramite mapping puntuale tra il nome di un claim e l'informazione che GovWay cerca di estrarre dal token.
Inoltre nel controllo degli accessi, in una autorizzazione per token claims (o per contenuti), è adesso possibile utilizzare la costante '${undefined}' per indicare che un claim non è atteso all'interno del token.

Infine la funzionalità di proxy applicativo è stata resa attivabile anche nelle comunicazioni verso gli authorization server indicati nelle token policy di negoziazione e validazione e nelle attribute authorities.
