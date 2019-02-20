.. _traffico_controlloCongestione:

Controllo della Congestione
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Questa sezione consente di impostare i parametri relativi al controllo
della congestione. Sono disponibili le seguenti opzioni:

-  *Stato* (abilitato \| disabilitato): Attiva il controllo sul numero
   di richieste simultanee al fine di individuare lo stato di
   congestionamento.

-  *Soglia di Attivazione (%)*: Selezionando l'opzione *abilitato*, al
   passo precedente, questo elemento consente di indicare la soglia
   dello stato di congestionamento. La soglia da indicare è in
   percentuale rispetto al Numero Massimo Richieste Simultanee. Al
   superamento di tale soglia si entra nello stato di congestionamento
   conseguente emissione di un evento e un messaggio diagnostico al
   riguardo.

.. note::
    Sulla base della percentuale indicata come soglia, una dicitura
    riporta nella pagina il valore di congestionamento calcolato in base
    al numero massimo di richieste simultanee.

   .. figure:: ../../_figure_console/ControlloTraffico-Congestione.png
    :scale: 100%
    :align: center
    :name: sogliaCongestione

    Configurazione della soglia di congestionamento
