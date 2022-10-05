Miglioramenti alla funzionalità di RateLimiting
------------------------------------------------------------

Sono state ottimizzate le prestazioni in caso di utilizzo degli scenari di conteggio tramite storage distribuito tra i nodi del cluster. Vengono adesso utilizzati 'atomic-long', sia sull'implementazione 'Hazelcast' che sull'implementazione 'Redis'.

Per l'implementazione 'Hazelcast' sono inoltre stati effettuate le seguenti ulteriori ottimizzazioni:

- aggiunta una tecnica di sincronizzazione basata sui 'PN Counters';

- aggiunta la possibilità di definire un'unica configurazione di rete condivisa tra tutte le istanze attivate per ogni tecnica.
