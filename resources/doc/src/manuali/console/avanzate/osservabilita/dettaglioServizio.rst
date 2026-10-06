.. _osservabilitaMetricheDettaglio:

Metriche di dettaglio per servizio
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le metriche di transazione (numero di richieste, latenze e dimensioni dei messaggi) sono prodotte
per default in forma aggregata, distinte solamente per ruolo (erogazione/fruizione), esito e
profilo di interoperabilità.

Per analizzare latenze, dimensioni e tempi delle singole fasi di elaborazione per erogazione,
fruizione e operazione è possibile abilitare le **metriche di dettaglio**, registrando tra le
:ref:`configProprieta` dell'erogazione o della fruizione la proprietà:

.. code-block:: text

   observability.metrics.details=true

La proprietà agisce sulla configurazione in cui viene registrata: se l'erogazione o la fruizione
possiede dei gruppi (configurazioni specifiche per un insieme di azioni), le metriche di dettaglio
vengono prodotte solamente per le azioni dei gruppi in cui la proprietà è stata registrata. Ad
esempio, registrando la proprietà solamente nella configurazione di un gruppo, le azioni degli altri
gruppi e della configurazione predefinita continuano a produrre le sole metriche aggregate.

Le metriche di dettaglio riportano le label descritte nella sezione :ref:`osservabilitaLabelDettaglio`:
la label ``interface_id`` identifica univocamente l'erogazione o la fruizione (riporta sempre la
configurazione predefinita, anche quando la richiesta è gestita da un gruppo), mentre la label
``group`` indica il gruppo che ha gestito la richiesta. L'elenco delle metriche prodotte è riportato
nella sezione :ref:`osservabilitaCatalogoDettaglio`.

.. note::
   Ogni combinazione di valori delle label genera una serie distinta nel sistema di monitoraggio:
   le metriche di dettaglio producono alcune centinaia di serie per ciascuna operazione invocata,
   a fronte di poche migliaia complessive per le metriche aggregate. In un ambiente di produzione
   si consiglia di abilitarle solamente sulle erogazioni, fruizioni o gruppi relativi ai servizi
   critici, che si desidera analizzare puntualmente.
