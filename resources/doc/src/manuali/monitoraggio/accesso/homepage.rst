.. _mon_accesso_homepage:

Homepage
--------

Una volta effettuato l'accesso, viene mostrata una pagina di benvenuto che dipende dall'impostazione associata al profilo dell'utente (:ref:`mon_accesso_profilo`).

Nel caso sia stato selezionata una homepage che visualizzi la ricerca delle transazioni l'utente viene rediretto alla pagina principale di ricerca descritta nella sezione ':ref:`mon_transazioni`' (:numref:`mon_tipoRicerca_fig2`).

.. figure:: ../_figure_monitoraggio/IndiceStorico.png
    :scale: 80%
    :align: center
    :name: mon_tipoRicerca_fig2

    Ricerca delle Transazioni


Se invece come homepage è stato impostato un report statistico verrà visualizzato, tramite un grafico, il volume di
traffico complessivo, suddiviso in base all'esito delle singole
comunicazioni (:numref:`mon_homepage_fig`). Il grafico può essere modificato specificando alcuni
valori tramite gli elementi seguenti:

-  Periodo

   Vengono mostrati i dati, aggiornati alla data odierna, con una tra le
   quattro finestre temporali disponibili: 1 giorno, 1 settimana, 1 mese
   e 1 anno.

-   Tipo

    Scelta tra Erogazioni, Fruizioni o Entrambi

-   API

    Filtro in base ad una specifica API

-   Profilo Interoperabilità

    Consente di filtrare le transazioni rappresentate nel grafico in base al profilo di interoperabilità. Opzione presente solo nel caso non sia stato selezionato un profilo di interoperabilità nella testata.

-  Soggetto Locale

   Consente di filtrare i dati del grafico specificando il soggetto
   locale che partecipa, come fruitore o erogatore, alle comunicazioni
   registrate. Opzione presente solo nel caso non sia stato selezionato un Soggetto nella testata.

.. figure:: ../_figure_monitoraggio/Homepage.png
    :scale: 70%
    :align: center
    :name: mon_homepage_fig

    HomePage con Report Statistico
