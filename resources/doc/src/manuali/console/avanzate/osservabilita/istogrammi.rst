.. _osservabilitaIstogrammi:

Istogrammi e bucket
~~~~~~~~~~~~~~~~~~~

Le latenze, le dimensioni dei messaggi e i tempi di persistenza del tracciamento sono esposti come
metriche di tipo *histogram*. Un istogramma non riporta le singole misurazioni ma, per ciascuna
soglia configurata (*bucket*), il numero di misurazioni che non l'hanno superata. Le soglie sono
definite dalle proprietà descritte nella sezione :ref:`osservabilitaConfigurazioneAvanzata`.

Proprietà e metriche
^^^^^^^^^^^^^^^^^^^^

Ogni proprietà definisce le soglie di un gruppo di metriche:

.. list-table::
   :header-rows: 1
   :widths: 40 60

   * - Proprietà
     - Metriche
   * - ``observability.metrics.latency-slotMs``
     - ``govway_request_duration_seconds``, ``govway_service_request_duration_seconds``,
       ``govway_processing_phase_seconds``
   * - ``observability.metrics.size-slotByte``
     - ``govway_request_size_bytes``, ``govway_service_request_size_bytes``
   * - ``observability.metrics.persistence-slotMs``
     - ``govway_tracing_persistence_seconds``, ``govway_tracing_persistence_components_seconds``,
       ``govway_tracing_persistence_components_details_seconds``

Dalla proprietà all'output
^^^^^^^^^^^^^^^^^^^^^^^^^^

Con la configurazione di default:

.. code-block:: properties

   observability.metrics.persistence-slotMs=5,10,25,50,100,250,500,1000,2500,5000,10000

l'endpoint ``/metrics`` espone, per ogni combinazione di label della metrica, serie come le seguenti
(valori di esempio):

.. code-block:: text

   govway_tracing_persistence_seconds_bucket{phase="POST_OUT_RESPONSE",le="0.005"} 62
   govway_tracing_persistence_seconds_bucket{phase="POST_OUT_RESPONSE",le="0.01"} 88
   govway_tracing_persistence_seconds_bucket{phase="POST_OUT_RESPONSE",le="0.025"} 97
   govway_tracing_persistence_seconds_bucket{phase="POST_OUT_RESPONSE",le="0.05"} 99
   govway_tracing_persistence_seconds_bucket{phase="POST_OUT_RESPONSE",le="0.1"} 100
   ...
   govway_tracing_persistence_seconds_bucket{phase="POST_OUT_RESPONSE",le="10.0"} 100
   govway_tracing_persistence_seconds_bucket{phase="POST_OUT_RESPONSE",le="+Inf"} 100
   govway_tracing_persistence_seconds_count{phase="POST_OUT_RESPONSE"} 100
   govway_tracing_persistence_seconds_sum{phase="POST_OUT_RESPONSE"} 0.656

- ``<name>_bucket``: una serie per ciascuna soglia, indicata dalla label ``le`` (*less or equal*).
  Il valore è il numero di misurazioni minori o uguali alla soglia. I bucket sono **cumulativi**:
  la serie ``le="0.01"`` conta anche le misurazioni già conteggiate in ``le="0.005"``.
- ``le="+Inf"``: bucket aggiunto sempre, che conta tutte le misurazioni.
- ``<name>_count``: numero totale di misurazioni (coincide con il bucket ``+Inf``).
- ``<name>_sum``: somma dei valori misurati; il rapporto ``_sum / _count`` fornisce il valore
  medio.

Nell'esempio, su 100 persistenze, 62 sono terminate entro 5 millisecondi, 88 entro 10 (quindi 26
tra 5 e 10), 97 entro 25, 99 entro 50 e tutte entro 100 millisecondi; la durata media è di 6,56
millisecondi (0,656 secondi / 100). Le durate sono misurate con una risoluzione di un millisecondo.

Le soglie dei tempi sono indicate nelle proprietà in millisecondi ma vengono esposte in secondi,
come previsto dalle convenzioni Prometheus per le metriche con suffisso ``_seconds``: il valore
``5`` diventa ``le="0.005"``. Le soglie delle dimensioni restano invece in byte: il valore ``256``
diventa ``le="256.0"``.

Utilizzo nelle query
^^^^^^^^^^^^^^^^^^^^

**Percentili.** La funzione PromQL ``histogram_quantile`` stima un percentile a partire dai
bucket. Ad esempio il 95° percentile della latenza negli ultimi 5 minuti:

.. code-block:: text

   histogram_quantile(0.95, sum by (le) (rate(govway_request_duration_seconds_bucket[5m])))

Il valore è ottenuto interpolando all'interno del bucket in cui ricade il percentile: la
precisione dipende quindi dalla distanza tra le soglie. Se il percentile ricade tra 250 e 500
millisecondi, il valore restituito è solamente una stima all'interno di quell'intervallo.

**Obiettivi di servizio (SLO).** La quota di richieste servite entro una soglia si ottiene dal
rapporto tra il bucket corrispondente e il totale. Ad esempio la percentuale di richieste servite
entro 500 millisecondi:

.. code-block:: text

   sum(rate(govway_request_duration_seconds_bucket{le="0.5"}[5m]))
     / sum(rate(govway_request_duration_seconds_count[5m]))

Il calcolo è esatto solamente se la soglia di interesse è presente tra quelle configurate.

Scelta delle soglie
^^^^^^^^^^^^^^^^^^^

Ogni soglia produce una serie per ciascuna combinazione di label della metrica (ruolo, esito,
servizio, fase, ...). Aumentare il numero di soglie migliora la precisione dei percentili ma
incrementa proporzionalmente il numero di serie da memorizzare nel sistema di monitoraggio.

Si consiglia quindi di concentrare le soglie attorno ai valori di interesse: ad esempio, se
l'obiettivo di servizio di un'API è di rispondere entro 200 millisecondi, conviene aggiungere il
valore ``200`` alla proprietà ``observability.metrics.latency-slotMs``, così da poter misurare
esattamente la quota di richieste che lo rispettano.
