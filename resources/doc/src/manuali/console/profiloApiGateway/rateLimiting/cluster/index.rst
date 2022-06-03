.. _headerGWRateLimitingCluster:

Rate Limiting in un cluster di nodi
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Se si desidera applicare un rate limiting in presenza di un cluster di più nodi, è necessario attuare una configurazione differente da quella di default.

Se ogni nodo effettua il proprio conteggio, un client potrebbe violare una policy solamente su alcuni nodi e non su altri.

Un semplice modo per attuare il controllo è quello di avvalersi di un bilanciamento del carico basato su sticky sessions in modo che ogni client venga inviato esattamente ad un nodo. Questo approccio presenta lo svantaggio che non è attuabile un'architettura high availability e non è possibile ridimensionare i nodi in presenza di sovraccarico delle richieste.

Un alternativa alla soluzione precedente, è quella di utilizzare un bilanciamento del carico round robin: le richieste verrano distribuite in ordine tra i nodi consentendo di attuare correttamente la policy semplicemente suddividendo la quota per il numero di nodi attivi. Questa soluzione è banalmente attuabile solamente se sui nodi risiede un'unica API e il conteggio avviene esclusivamente per numero di richieste. La soluzione inizia a presentare difetti non appena vi sono più API, poichè il bilanciamento da configurare sul bilanciatore dovrà essere basato sul contesto dell'API invocata. Un ulteriore svantaggio si presenta quando il conteggio deve tener conto del client richiedente (es. 10 richieste al minuto per ogni client) e quindi anche il bilanciamento diventa complesso e non di facile attuazione.

Una soluzione migliore, che consente di non essere legati al bilanciamento del carico, è quella in cui il conteggio viene attuato tramite un archivio dati centralizzato (es. Hazelcast o Redis). Questa soluzione presenta lo svantaggio dovuto all'aumento della latenza che impatterrà su ogni richiesta per via dell'archiviazione dei dati in remoto e per la concorrenza di tali operazioni.

La soluzione scelta per gestire un rate limiting in un cluster di nodi dovrà quindi essere selezionata rispetto allo scenario di utilizzo considerando i pro e i contro di ogni soluzione.

Per selezionare un tipo di gestore delle policy di Rate Limiting differente da quello di default è richiesto l'accesso alla govwayConsole in modalità *avanzata* (sezione :ref:`modalitaAvanzata`).

A partire dall'erogazione o fruizione di una API, accedendo alla sezione :ref:`configSpecifica` in modalità avanzata compare una sezione precedentemente non documentata denominata *Opzioni Avanzate*. All'interno di tale sezione è possibile agire sulla configurazione della voce *Sincronizzazione* nella sezione *Rate Limiting*.

.. figure:: ../../../_figure_console/ConfigurazioneSincronizzazioneRateLimiting.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingDefault

    Sincronizzazione del Rate Limiting in un cluster di nodi

Le modalità di gestione delle policy attivabili su GovWay sono i seguenti:

- *Default*: ogni nodo effettua il proprio conteggio;

- *Locale*: equivalente alla voce 'Default';

- *Locale - quota divisa sui nodi*: gestione delle policy che richiede un bilanciamento del carico. Soluzione descritta nella sezione :ref:`headerGWRateLimitingCluster_quotaDivisaSuiNodi`.

- *Distribuita*: il conteggio viene attuato tramite un archivio dati centralizzato. La soluzione viene descritta nella sezione :ref:`headerGWRateLimitingCluster_distribuita`.

Oltre a personalizzare la gestione puntualmente su una erogazione o fruizione è possibile attuare una configurazione, identica a quanto già precedentemente descritto, a livello globale di GovWay agendo nella sezione *Rate Limiting* presente nella maschera di configurazione del *Controllo del Traffico* (sezione :ref:`configurazioneRateLimiting`).

.. toctree::
   :maxdepth: 2

   quotaDivisaSuiNodi
   distribuita

