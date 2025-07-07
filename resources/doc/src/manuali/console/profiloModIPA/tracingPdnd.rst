.. _modipa_tracingPdnd:

Tracing PDND
----------------------

Come descritto in `Tracing <https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-tracing>`__, gli scambi di informazioni tra erogatore e fruitore avvengono al di fuori del perimetro dell'infrastruttura tecnica di PDND Interoperabilità, che non ne ha visibilità. Una volta che PDND Interoperabilità ha rilasciato un voucher valido al fruitore, questo può contattare direttamente l'erogatore. Il servizio di `Tracing <https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-tracing>`__ consente alla PDND di raccogliere informazioni quantitative relative a queste transazioni.

La PDND richiede agli enti di caricare quotidianamente le informazioni relative a tutte le transazioni effettuate, utilizzando il servizio `Tracing <https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-tracing>`__ che consente la pubblicazione di report in formato CSV. 

GovWay semplifica l’integrazione con la funzionalità di Tracing della PDND rendendola trasparente al soggetto erogatore o fruitore di servizi su PDND.

La configurazione, da attuare per abilitare il tracing PDND tramite GovWay, viene descritta nella sezione :doc:`tracingPdnd/configurazione/index`.

Alcuni aspetti di configurazione avanzata vengono forniti nella sezione :doc:`tracingPdnd/configurazioneProperties`


.. toctree::
  :maxdepth: 2

  tracingPdnd/panoramica
  tracingPdnd/configurazione/index
  tracingPdnd/configurazioneProperties
