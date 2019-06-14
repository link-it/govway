.. _avanzate_connettori_tokenPolicy:

Autenticazione Token
~~~~~~~~~~~~~~~~~~~~

Quando si configura l'autenticazione per un connettore è possibile
scegliere la modalità di autenticazione per token. Tale funzionalità
permette di iniettare un Token Bearer nella comunicazione http tramite la modalità definita all'interno della policy selezionata (es. tramite header
'Authorization'). Per ulteriori dettagli su come registrare una policy di negoziazione del Bearer Token si rimanda alla sezione :ref:`tokenNegoziazionePolicy`.

   .. figure:: ../../_figure_console/AutenticazioneToken.png
    :scale: 100%
    :align: center
    :name: configAutenticazioneTokenFig

    Dati di configurazione di un'autenticazione Token
