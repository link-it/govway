Miglioramenti alla funzionalità di Tracciamento
------------------------------------------------

Sono stati introdotti i seguenti miglioramenti al processo di failover attivato in caso di fallimento del tracciamento su database:

- i file delle tracce salvati su filesystem includono ora nel nome un UUID, che ne garantisce l’univocità;

- una gestione tramite semaforo su database viene adesso utilizzata per coordinare i timer di recupero delle tracce da filesystem tra più nodi in ambienti cluster, consentendo l’utilizzo di un volume persistente condiviso da tutti i nodi.

È stata inoltre aggiornata la documentazione relativa alla configurazione del tracciamento, al processo di failover e ai requisiti di spazio disco, con particolare attenzione agli ambienti cloud orchestrati.
	
