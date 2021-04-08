.. _mon_transazioni_identificativo:

Ricerca per Identificativo
~~~~~~~~~~~~~~~~~~~~~~~~~~

Questo gruppo comprende le modalità di ricerca basate sull'identificativo della comunicazione, e comprende:

- ID Transazione
- ID Applicativo
- ID Messaggio

La modalità **ID Transazione** consente di effettuare ricerche tramite l'identificativo della transazione (:numref:`mon_idTransazione_fig`).

.. figure:: ../../_figure_monitoraggio/FiltroIdentificativoTransazione.png
    :scale: 100%
    :align: center
    :name: mon_idTransazione_fig

    Filtro di ricerca delle transazioni per ID Transazione

La modalità **ID Messaggio** che consente di effettuare ricerche sulla base dell'identificativo del messaggio assegnato dal gateway. La maschera di ricerca si compone dei seguenti campi (:numref:`mon_idMessaggio_fig`):

   -  **Tipo**: indica il tipo di identificativo da ricercare:

	   - *Richiesta*: identifica un messaggio di richiesta
	   - *Risposta*: identifica un messaggio di risposta
	   - *Conversazione*: è possibile effettuare una ricerca per invidivuare tutte le transazioni che sono correlate attraverso il medesimo identificativo di conversazione.
	   - *Riferimento Richiesta*: consente di invidivuare una transazione che è correlata ad una precedente richiesta.

   -  **ID**: identificativo da cercare.

.. figure:: ../../_figure_monitoraggio/FiltroIdentificativoMessaggio.png
    :scale: 100%
    :align: center
    :name: mon_idMessaggio_fig

    Filtro di ricerca delle transazioni per ID Messaggio

La modalità **ID Applicativo** consente di effettuare ricerche di transazioni contenenti uno specifico identificativo applicativo estratto dalle comunicazioni in transito tramite la funzionalità di Correlazione Applicativa. Sono disponibili due modalità di ricerca:

- la ricerca base consente di indicare l'identificativo applicativo da ricercare e la posizione del messaggio da dove è stato estratto tra richiesta o risposta (:numref:`mon_idApplicativo_base_fig`).

  .. figure:: ../../_figure_monitoraggio/FiltroIdentificativoApplicativoBase.png
      :scale: 100%
      :align: center
      :name: mon_idApplicativo_base_fig

      Filtro di ricerca base delle transazioni per ID Applicativo

- la ricerca avanzata consente di indicare oltre all'identificativo applicativo diversi altri criteri di ricerca (:numref:`mon_idApplicativo_avanzata_fig`).

  .. figure:: ../../_figure_monitoraggio/FiltroIdentificativoApplicativo.png
      :scale: 100%
      :align: center
      :name: mon_idApplicativo_avanzata_fig

      Filtro di ricerca avanzata delle transazioni per ID Applicativo


