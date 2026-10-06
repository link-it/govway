.. _osservabilitaConfigurazioneAvanzata:

Configurazione avanzata
~~~~~~~~~~~~~~~~~~~~~~~

Oltre all'abilitazione dei collettori (sezione :ref:`osservabilitaInstall`), il file
*<directory-lavoro>/govway_local.observability.properties* consente di personalizzare le metriche
prodotte. Le proprietà vengono lette all'avvio: una modifica richiede il riavvio del gateway.

Default per segnale
^^^^^^^^^^^^^^^^^^^

È possibile definire dei **default per segnale**, validi per tutti i collettori, omettendo il
segmento ``collector.<name>``:

.. code-block:: properties

   # default applicato a tutti i collettori
   observability.metrics.enabled=true

I valori specifici del collettore (``observability.collector.<name>.<chiave>``) hanno la
precedenza sui default (``observability.<chiave>``).

Metriche di sistema
^^^^^^^^^^^^^^^^^^^

L'esposizione delle metriche di sistema JVM/process (memoria, garbage collector, thread, CPU,
uptime, ecc.) descritte nella sezione :ref:`osservabilitaMetricheJvm` è attivabile/disattivabile con:

.. code-block:: properties

   observability.metrics.system-metrics.enabled=true

Bucket degli istogrammi (SLO)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

I confini (*bucket*) degli istogrammi sono configurabili come liste di valori separati da virgola.
Sono definiti tre insiemi, applicati rispettivamente alla latenza delle richieste, alla dimensione
dei messaggi e alla persistenza del tracciamento. Il significato dei bucket, le metriche a cui si
applica ciascun insieme e il loro utilizzo nelle query sono descritti nella sezione
:ref:`osservabilitaIstogrammi`.

.. code-block:: properties

   # latenza (millisecondi)
   observability.metrics.latency-slotMs=5,10,25,50,100,250,500,1000,2500,5000,10000
   # dimensione messaggi (byte)
   observability.metrics.size-slotByte=256,1024,4096,16384,65536,262144,1048576,4194304,16777216
   # persistenza tracciamento (millisecondi)
   observability.metrics.persistence-slotMs=5,10,25,50,100,250,500,1000,2500,5000,10000
