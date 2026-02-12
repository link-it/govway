Miglioramenti alla funzionalità di generazione dei report statistici
----------------------------------------------------------------------

Introdotta nuova modalità di generazione statistiche tramite applicazione web che si affianca al batch esistente lanciabile via cron.
Il nuovo componente è un'applicazione web dispiegabile direttamente nell'application server, che elimina la necessità di schedulazioni esterne semplificando la gestione operativa in ambienti dove si preferisce mantenere tutti i servizi all'interno del medesimo application server.

Inoltre è stata ottimizzata la fase 0 del batch statistiche aggiungendo un limite inferiore sulla data, evitando la scansione di tutte le partizioni.
