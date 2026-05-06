.. _modipa_signalhub_pseudoanonimizzazione:

Pseudoanonimizzazione
-----------------------

Il `Manuale Operativo Signal Hub <https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-signal-hub>`__ stabilisce che il formato e le modalità con cui vengono esposte le informazioni crittografiche (seme e identificativo pseudoanonimizzato) siano demandate all'erogatore, che ha l'onere di documentarle ai consumatori del servizio. Le sezioni seguenti descrivono in dettaglio come GovWay realizza la pseudoanonimizzazione, in modo che un consumatore possa correttamente ricalcolare il digest a partire da un identificativo candidato e verificarne la corrispondenza con l'``objectId`` pubblicato in un segnale.

Si raccomanda la lettura preliminare della :doc:`../panoramica`, dove sono descritti il flusso complessivo del servizio Signal-Hub, gli algoritmi di hash supportati e le raccomandazioni PDND su lunghezza del seme e periodo di rotazione.

- :ref:`modipa_signalhub_pseudoanonimizzazione_generazione`: descrive come GovWay produce il seed e l'``objectId``, l'algoritmo di hash applicato e la composizione dell'input.

- :ref:`modipa_signalhub_pseudoanonimizzazione_verifica`: riporta lo pseudocodice Java per la verifica di un segnale a partire da un identificativo candidato, oltre all'errore tipico da evitare.

- :ref:`modipa_signalhub_pseudoanonimizzazione_conformitaPdnd`: argomenta l'allineamento delle scelte implementative di GovWay rispetto a quanto prescritto dal Manuale Operativo Signal Hub di PDND.

.. toctree::
   :maxdepth: 2

   generazione
   verifica
   conformita
