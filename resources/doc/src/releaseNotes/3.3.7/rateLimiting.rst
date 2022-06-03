Miglioramenti alla funzionalità di RateLimiting
------------------------------------------------------------

Aggiunti differenti gestori delle policy di RateLimiting utilizzabili su un cluster di nodi:

- gestore che consente di suddividere la quota per il numero di nodi attivi e si basa su un bilanciamento delle richieste "round robin" (soluzione applicabile solamente in specifici contesti);

- gestore in cui il conteggio viene attuato tramite un archivio dati centralizzato: Hazelcast. Con questo gestore si ha un degrato delle performance dovuto all’aumento della latenza che impatta su ogni richiesta per via dell’archiviazione dei dati in remoto e per la concorrenza di tali operazioni. Per ovviare al degrado vengono fornite differenti varianti che consentono di avere performance migliori a discapito della precisione nei conteggi.

Il software consente di attivare il gestore delle policy più opportuno puntualmente su ogni erogazione o fruizione di API permettendo quindi di valutare i pro e i contro di ogni soluzione calati nel singolo contesto di utilizzo.

È adesso inoltre possibile personalizzare gli header HTTP restituiti al client contenenti informazioni sulle quote e sulle finestre temporali delle policy di rate limiting: è possibile disabilitarne la generazione o modificarne i valori indicati per quanto concerne la quota (indicazione o meno della finestra temporale) e gli intervalli di retry (utilizzo o meno di un tempo di backoff).

È infine stata aggiunta la possibilità di filtrare l'applicabilità di una policy rispetto ai valori presenti in un token OAuth2.
