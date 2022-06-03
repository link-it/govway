.. _headerGWRateLimitingCluster_quotaDivisaSuiNodi:

Sincronizzazione Locale con quota divisa sui nodi
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

TODO
Un alternativa alla soluzione precedente, è quella di utilizzare un bilanciamento del carico round robin: le richieste verrano distribuite in ordine tra i nodi consentendo di attuare correttamente la policy semplicemente suddividendo la quota per il numero di nodi attivi. Questa soluzione è banalmente attuabile solamente se sui nodi risiede un'unica API e il conteggio avviene esclusivamente per numero di richieste. La soluzione inizia a presentare difetti non appena vi sono più API, poichè il bilanciamento da configurare sul bilanciatore dovrà essere basato sul contesto dell'API invocata. Un ulteriore svantaggio si presenta quando il conteggio deve tener conto del client richiedente (es. 10 richieste al minuto per ogni client) e quindi anche il bilanciamento dovrebbe farlo.

