Miglioramenti alla funzionalità di RateLimiting
------------------------------------------------------------

Aggiunte nuove modalità di gestione delle policy di RateLimiting utilizzabili su un cluster di nodi:

- possibilità di suddividere le quote previste per il numero di nodi attivi; questa modalità permette, potendo sfruttare la collaborazione del load balancer, di ottenere il conteggio corretto delle quote previste anche senza bisogno di utilizzare uno storage distribuito tra i nodi del cluster;

- possibilità di gestire il conteggio tramite storage distribuito tra i nodi del cluster; per limitare il degrado prestazionale introdotto dalla gestione del conteggio distribuito, è possibile configurare opzioni di comunicazione asincrona tra i nodi del cluster, ottenendo performance migliori a discapito della precisione nei conteggi.

La modalità di configurazione del rate limiting può essere perfezionata per ogni erogazione o fruizione di API, permettendo quindi di adottare la soluzione più opportuna per ogni singola API gestita da GovWay.

È inoltre stata introdotta la possibilità di personalizzare gli header HTTP restituiti ai client, relativi alle informazioni sulle quote e sulle finestre temporali delle policy di rate limiting: è possibile disabilitarne del tutto la generazione o modificarne i valori indicati per quanto concerne la quota (indicazione o meno della finestra temporale) e gli intervalli di retry (utilizzo o meno di un tempo di backoff).

È infine stata aggiunta la possibilità di filtrare l'applicabilità di una policy rispetto ai valori presenti in un token OAuth2.
