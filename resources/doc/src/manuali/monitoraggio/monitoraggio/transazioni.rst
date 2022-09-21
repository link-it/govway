.. _mon_transazioni:

Monitoraggio delle Transazioni
------------------------------

La sezione "Monitoraggio > Transazioni" presenta inizialmente la scelta tra le diverse tipologie di
consultazione che l'utente può utilizzare (:numref:`mon_tipoRicerca_fig`).

.. figure:: ../_figure_monitoraggio/IndiceStorico.png
    :scale: 80%
    :align: center
    :name: mon_tipoRicerca_fig

    Selezione modalità di ricerca delle transazioni

I criteri di ricerca disponibili sono suddivisi in tre aree:

- :ref:`mon_transazioni_generica`: meccanismo base per la consultazione delle transazioni che consente di filtrare rispetto a criteri generici di ricerca;

- :ref:`mon_transazioni_mittente`: comprende modalità di ricerca che consentono di filtrare, oltre ai criteri generici del gruppo precedente, anche rispetto ad informazioni specifiche del richiedente (es. applicativo, token, ip ...);

- :ref:`mon_transazioni_identificativo`: comprende modalità di ricerca basate su identificativi univoci della comunicazione.

Una volta impostati i criteri di ricerca desiderati, per procedere con
la ricerca si deve utilizzare il pulsante **Cerca**. Se si vogliono
riportare i criteri di ricerca ai valori iniziali è possibile utilizzare
il pulsante **Ripulisci**  (:numref:`mon_bottoniRicerca_fig`).


.. figure:: ../_figure_monitoraggio/BottoniRicerca.png
    :scale: 80%
    :align: center
    :name: mon_bottoniRicerca_fig

    Pulsanti di ricerca delle transazioni

Escludendo le ricerche per Identificativo, dopo aver effettuato una ricerca (tramite il pulsante 'Filtra'), saranno disponibili due nuovi pulsanti (:numref:`mon_bottoniRicerca2_fig`):

-  **Nuova Ricerca**: per effettuare una nuova ricerca utilizzando i parametri presenti nel form.

-  **Filtra Risultati**: per effettuare una ricerca usando come insieme di partenza le transazioni restituite dalla precedente ricerca.


.. figure:: ../_figure_monitoraggio/BottoniRicerca2.png
    :scale: 80%
    :align: center
    :name: mon_bottoniRicerca2_fig

    Pulsanti di aggiornamento della ricerca delle transazioni

Ogni ricerca consente di ottenere una lista di transazioni gestite da GovWay che soddisfano i criteri di ricerca impostati come mostrato nella sezione :ref:`mon_transazioni_lista`.

.. toctree::
    :maxdepth: 2

    ricerca/generica
    ricerca/mittente
    ricerca/identificativo
    ricerca/listaTransazioni
