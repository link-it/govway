.. _scenari_erogazione_soap_modipa_pdnd_auth:

Erogazione API SOAP
=======================

**Obiettivo** 

Esporre un servizio SOAP, definito tramite una interfaccia WSDL, accessibile in accordo al pattern di sicurezza descritto nella sezione :ref:`modipa_pdnd`.

**Sintesi**

Mostriamo in questa sezione come procedere per l'esposizione di un servizio SOAP da erogare nel rispetto della normativa italiana alla base dell'interoperabilità tra i sistemi della pubblica amministrazione.  In particolare andiamo ad illustrare lo scenario in cui un servizio è stato registrato sulla PDND, e i fruitori per poterlo fruire devono ottenere un voucher dalla PDND che successivamente devono inviare all'erogatore insieme alla normale richiesta di servizio.

La figura seguente descrive graficamente questo scenario.

.. figure:: ../../../_figure_scenari/ErogazioneModIPA_idAuth_pdnd_soap.png
 :scale: 70%
 :align: center
 :name: erogazione_modipa_soap_pdnd_fig

 Erogazione di una API SOAP con profilo 'ModI', pattern ID_AUTH_REST_01 via PDND

Le caratteristiche principali di questo scenario sono:

1. un applicativo eroga un servizio, rivolto a fruitori di domini esterni, registrandolo sulla PDND;
2. la comunicazione con i domini esterni avviene su un canale gestito con il pattern di sicurezza canale "ID_AUTH_CHANNEL_01";
3. l'autenticità della comunicazione tra il servizio erogato e ciascun fruitore è garantita tramite sicurezza a livello messaggio con pattern "ID_AUTH_REST_01 via PDND".


.. toctree::
    :maxdepth: 2

    esecuzione
    configurazione
