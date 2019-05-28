.. _configurazioneRateLimiting_filtriRaggruppamentiPersonalizzati:

Filtro o Raggruppamento Personalizzato
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Nella sezione :ref:`rateLimiting_attivazioneNuovaPolicy` è possibile utilizzare dei criteri di raggruppamento per il valore di soglia o un filtro di applicabilità personalizzato in modo da definire un comportamento specifico per le proprie esigenze di servizio.
Una configurazione personalizzata richiede la realizzazione di un plugin che contiene la logica di filtro e/o il raggruppamento personalizzato; il plugin consiste nell'implementazione di una classe java che implementa l'interfaccia:

         ::

             package org.openspcoop2.pdd.core.controllo_traffico.plugins;
             public interface IRateLimiting {
                 public String estraiValoreFiltro(Logger log,Dati datiRichiesta) throws PluginsException;
                 public String estraiValoreCollezionamentoDati(Logger log,Dati datiRichiesta) throws PluginsException;
             }

         La classe realizzata deve essere successivamente registrata tramite
         una entry da aggiungere all'interno del file (da creare se non esiste) */etc/govway/govway_local.classRegistry.properties* di GovWay:

         ::

             org.openspcoop2.pdd.controlloTraffico.rateLimiting.<tipo>=<fully qualified class name>

         La stringa <tipo> diventa
         utilizzabile come “Tipo Personalizzato” da indicare in fase di configurazione per un criterio di filtro personalizzato (:numref:`filtroPersonalizzato`) e/o per un criterio di raggruppamento personalizzato (:numref:`raggruppamentoPersonalizzato`).

	   .. figure:: ../../_figure_console/RateLimiting_filtroPersonalizzato.png
	    :scale: 100%
	    :align: center
	    :name: filtroPersonalizzato

	    Filtro Personalizzato

	   .. figure:: ../../_figure_console/RateLimiting_raggruppamentoPersonalizzato.png
	    :scale: 100%
	    :align: center
	    :name: raggruppamentoPersonalizzato

	    Raggruppamento Personalizzato

