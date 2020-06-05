.. _configProprieta:

Proprietà
~~~~~~~~~

Ad una API è possibile associare una serie di proprietà consultabili da una qualsiasi delle funzionalità precedentemente descritte.

 (:numref:`toolTipSospensione`).

   .. figure:: ../_figure_console/proprieta.png
    :scale: 60%
    :align: center
    :name: proprieta

    Elenco di proprietà di una API


Questa funzionalità è frequentemente utilizzata in combinazione con le :ref:`trasformazioni` per poter permettere all'utente di configurarne il comportamento senza dover modificare e caricare un nuovo file template di trasformazione. All'interno di un template di trasformazione è possibile accedere alle proprietà tramite la sintassi 'config' come descritto nella sezione :ref:`valoriDinamici`.

Le proprietà permettono inoltre di effettuare la configurazione di aspetti avanzati di una funzionalità che non rientrano nel suo normale utilizzo. Ad esempio è possibile differenziare il comportamento della validazione dei messaggi, tra richiesta e risposta, utilizzando le proprietà descritte nella sezione :ref:`configSpecificaValidazione`.
