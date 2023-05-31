.. _modipa_sicurezzaMessaggio_ida01:

[ID_AUTH_SOAP_01 / ID_AUTH_REST_01]
------------------------------------

Il pattern ID_AUTH nelle sue varie declinazioni ha lo scopo di identificare e autorizzare l'accesso del fruitore ad un servizio.

Il pattern differisce rispetto al tipo di trust:

- trust tramite PDND: descritto nella sezione ':ref:`modipa_pdnd`', e applicabile sia per API REST che SOAP, consiste nella negoziazione con la PDND di un voucher spendibile verso l'erogatore.

- trust tra fruitore ed erogatore tramite certificati X509: descritto nella sezione ':ref:`modipa_idar01`' differisce nella sostanza se applicato per API REST, in cui viene prodotto un token JWT, o per API SOAP, in cui viene definito un header SOAP WSSecurity. In entrambi i casi il certificato del mittente viene inserito all'interno del token di sicurezza e validato dall'erogatore tramite un trustStore contenente i certificati X509 attesi.

- trust tramite Authorization Server: descritto nella sezione ':ref:`modipa_oauth`', e applicabile sia per API REST che SOAP, consiste nella negoziazione con un authorization server differente dalla PDND di un voucher spendibile verso l'erogatore.

.. toctree::
        :maxdepth: 2

	pdnd/index
        x509/index
	oauth/index
