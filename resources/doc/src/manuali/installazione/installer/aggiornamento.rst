.. _inst_installer_update:

Aggiornamento
-------------

Supponiamo che la scelta sia quella di aggiornare una installazione
precedente. Vediamo come si sviluppa il processo per differenza rispetto
al caso di una nuova installazione:

#. Il primo passo è quello di indicare la versione di GovWay da cui si
   parte per l'aggiornamento.
   
   .. figure:: ../_figure_installazione/installer-update-1.png
    :scale: 100%
    :align: center

    Scelta versione precedente

#. Al passo successivo, dove si indicano le informazioni preliminari, vi
   è il vincolo di indicare la medesima piattaforma database utilizzata
   per l'installazione che si vuole aggiornare.

   .. figure:: ../_figure_installazione/installer-update-2.png
    :scale: 100%
    :align: center

    Scelta piattaforma database identica all'installazione di provenienza

#. Nel successivo passo è possibile indicare se tra gli archivi generati 
   devono essere inclusi i servizi che permettono la configurazione ed il monitoraggio
   di GovWay tramite API REST e i servizi di Health Check basati su API REST e/o SOAP.

   .. _apiREST_fig_update:
   
   .. figure:: ../_figure_installazione/installer-scr3b.jpg
    :scale: 100%
    :align: center

    Configurazione Servizi

#. Nella maschera che permette la scelta dei profili di interoperabilità,
   vi è il vincolo di indicare almeno i medesimi profili utilizzati per l'installazione che si vuole aggiornare.

   .. figure:: ../_figure_installazione/installer-update-3.png
    :scale: 100%
    :align: center

    Scelta Profilo di Interoperabilità

#. I rimanenti passaggi sono uguali al caso della nuova installazione
   con la differenza che non sarà disponibile la funzione per impostare
   le credenziali dei cruscotti grafici.

