.. _modipa_sicurezza_avanzate:

Funzionalità Avanzate
-----------------------------------

I Token di sicurezza, dopo essere stati validati da GovWay, vengono eliminati dai messaggi in modo da rendere trasparente agli applicativi la gestione della sicurezza che è stata effettata sul Gateway.

È possibile, se necessario, configurare GovWay al fine di non fargli eliminare il token di sicurezza dai messaggi dopo averli validati.
Per farlo si deve utilizzare la govwayConsole in modalità avanzata (vedi sezione :ref:`modalitaAvanzata`).


Per quanto concerne le richieste inoltrate ad un backend, durante la gestione di una erogazione, è possibile disabilitare l'eliminazione del token di sicurezza intervenendo sul connettore dell'erogazione e disabilitando la voce 'Sbustamento ModI PA' all'interno della sezione 'Trattamento Messaggio
' come mostrato nella figura :numref:`modipa_sbustamento_richiesta`.

   .. figure:: ../../_figure_console/modipa_sbustamento_richiesta.png
    :scale: 50%
    :align: center
    :name: modipa_sbustamento_richiesta

    Funzionalità 'Sbustamento ModI PA' disabilitata per la Richiesta


Sulle risposte ritornate all'applicativo mittente, durante la gestione di una fruizione, è possibile disabilitare l'eliminazione del token di sicurezza intervenendo sull'applicativo e disabilitando la voce 'Sbustamento ModI PA' all'interno della sezione 'Trattamento Messaggio
' come mostrato nella figura :numref:`modipa_sbustamento_risposta`.

   .. figure:: ../../_figure_console/modipa_sbustamento_risposta.png
    :scale: 50%
    :align: center
    :name: modipa_sbustamento_risposta

    Funzionalità 'Sbustamento ModI PA' disabilitata per la Risposta
