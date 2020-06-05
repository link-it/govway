.. _fruizione:

Registrazione della fruizione
-----------------------------

Nel processo di fruizione sono coinvolti i client (o applicativi)
interni al dominio che richiedono, tramite accesso sul gateway, un
servizio erogato da un soggetto di un dominio esterno.

In :numref:`scenarioFruizione` è illustrato graficamente il caso della fruizione.

   .. figure:: ../_figure_console/FruizioneScenario.png
    :scale: 80%
    :align: center
    :name: scenarioFruizione

    Scenario di riferimento per la fruizione

Analogamente a quanto descritto per le erogazioni, è possibile procedere
con la configurazione delle fruizioni accedendo alla sezione di menu
*Registro > Fruizioni*.

La configurazione delle fruizioni presenta maschere della GovWayConsole
del tutto analoghe al caso dell'erogazione. È quindi possibile seguire
il processo di configurazione attuando i medesimi passi, illustrati per
le erogazioni, calandole sul contesto delle fruizioni.

L'unica differenza, rispetto al processo di configurazione delle
erogazioni, è rappresentata dalla presenza del campo *Soggetto
Erogatore*, da selezionare come soggetto che eroga il servizio (:numref:`fruizioneNew`).

   .. figure:: ../_figure_console/Fruizione-new.png
    :scale: 50%
    :align: center
    :name: fruizioneNew

    Registrazione di una Fruizione


.. note::
    Benché non vi siano differenze nelle modalità di configurazione del
    *Connettore*, nel caso della fruizione questi consiste nei dati di
    puntamento al servizio erogato sul dominio esterno.

Condivisione dei dati di integrazione
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le richieste di fruizione, pervenute a GovWay, vengono elaborate e, nel
corso dell'operazione, vengono creati i riferimenti alle entità di
configurazione presenti nel registro.

GovWay comunica i dati di contesto ricavati, ai sistemi interlocutori:

-  *GovWay-Message-ID*

-  *GovWay-Relates-To*

-  *GovWay-Conversation-ID*

-  *GovWay-Transaction-ID*

Per ulteriori dettagli si consiglia di consultare la sezione :ref:`headerIntegrazione`.

Errori Generati dal Gateway
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Analogamente a quanto descritto per le erogazioni, la gestione dei casi
di errore nelle comunicazioni mediate da un Gateway devono tenere conto
di ulteriori situazioni che possono presentarsi rispetto alla situazione
di dialogo diretto tra gli applicativi. 

La gestione degli errori viene descritta approfonditamente nella sezione :ref:`erroriGovWay`.

