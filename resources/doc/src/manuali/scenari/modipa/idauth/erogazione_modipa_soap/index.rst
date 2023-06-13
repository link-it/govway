.. _scenari_erogazione_soap_modipa_auth:

Erogazione API SOAP
=======================

**Obiettivo** 

Esporre un servizio SOAP, definito tramite una interfaccia WSDL, accessibile in accordo al pattern di sicurezza 'ID_AUTH_SOAP_01' descritto nella sezione :ref:`modipa_idar01`.

**Sintesi**

Mostriamo in questa sezione come procedere per l'esposizione di un servizio SOAP da erogare nel rispetto della normativa italiana alla base dell'interoperabilità tra i sistemi della pubblica amministrazione.  In particolare andiamo ad illustrare lo scenario, tra quelli prospettati nel Modello di Interoperabilità di AGID, che prevede il trust del certificato X.509 in modo da assicurare sia a livello di canale che a livello di messaggio l'autenticazione e autorizzazione del fruitore.

La figura seguente descrive graficamente questo scenario.

.. figure:: ../../../_figure_scenari/ErogazioneModIPA_SOAP.png
 :scale: 70%
 :align: center
 :name: erogazione_modipa_soap_fig

 Erogazione di una API SOAP con profilo 'ModI', pattern ID_AUTH_SOAP_01

Le caratteristiche principali di questo scenario sono:

1. un applicativo eroga un servizio, rivolto a fruitori di domini esterni, in conformità al Modello di Interoperabilità AGID;
2. la comunicazione con i domini esterni avviene su un canale gestito con il pattern di sicurezza canale "ID_AUTH_CHANNEL_02";
3. l'autenticità della comunicazione tra il servizio erogato e ciascun fruitore è garantita tramite sicurezza a livello messaggio con pattern "ID_AUTH_SOAP_01".


.. toctree::
    :maxdepth: 2

    esecuzione
    configurazione
