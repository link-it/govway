.. _modipa_sicurezzaMessaggio_ida02:

ID_AUTH_SOAP_02 / ID_AUTH_REST_02
------------------------------------

Il pattern ID_AUTH nelle sue varie declinazioni della versione '02' ha lo scopo, oltre a identificare e autorizzare l'accesso del fruitore ad un servizio, anche quello di evitare Replay Attack poichè ogni richiesta non può essere nuovamente processata.

Analizzando il pattern rispetto al tipo di trust:

- trust tramite PDND: in attesa di ulteriori indicazioni, il pattern non sembra essere utilizzabile con un trust tramite PDND, poichè richiederebbe una negoziazione di un nuovo token per ogni richiesta per garantirne l'univocità.

- trust tra fruitore ed erogatore tramite certificati X509: descritto nella sezione ':ref:`modipa_idar02`' differisce nella sostanza se applicato per API REST, in cui viene prodotto un token JWT con identificativo univoco inserito nel claim 'jti', o per API SOAP, in cui viene definito un header SOAP WSSecurity e un identificativo univoco presente nell'header SOAP WSAddressing:MessageID. In entrambi i casi il certificato del mittente viene inserito all'interno del token di sicurezza e validato dall'erogatore tramite un trustStore contenente i certificati X509 attesi.

.. toctree::
        :maxdepth: 2

        x509/index
