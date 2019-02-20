.. _fruizione:

Registrazione della fruizione
-----------------------------

Nel processo di fruizione sono coinvolti i client (o applicativi)
interni al dominio che richiedono, tramite accesso sul gateway, un
servizio erogato da un soggetto di un dominio esterno.

In :numref:`scenarioFruizione` è illustrato graficamente il caso della fruizione.

   .. figure:: ../_figure_console/FruizioneScenario.png
    :scale: 100%
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
di dialogo diretto tra gli applicativi. Oltre agli errori conosciuti
dagli applicativi, e quindi previsti nei descrittori del servizio, gli
applicativi client possono ricevere ulteriori errori generati dal
gateway.

Govway genera differenti errori a seconda se l'erogazione o la fruizione
riguarda una API di tipologia SOAP o REST.

-  *REST*: viene generato un oggetto *Problem Details* come definito
   nella specifica *RFC 7807* (https://tools.ietf.org/html/rfc7807).
   Ulteriori dettagli vengono descritti nella sezione :ref:`rfc7807`.

-  *SOAP*: viene generato un SOAPFault contenente un actor (o role in
   SOAP 1.2) valorizzato con *http://govway.org/integration*.
   Nell'elemento *fault string* è presente il dettaglio dell'errore
   mentre nell'elemento *fault code* una codifica di tale errore.
   Ulteriori dettagli vengono descritti nella sezione :ref:`soapFault`.
