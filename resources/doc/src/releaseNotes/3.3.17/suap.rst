Introduzione supporto per l'integrazione con i servizi SUAP
-------------------------------------------------------------

Utilizzando GovWay per la gestione dell’interoperabilità ModI, non è possibile delegare direttamente al backend SUAP tutti i casi di errore previsti dalla `Specifica Tecnica DPR-160 <https://github.com/AgID/specifiche-tecniche-DPR-160-2010/blob/approved02/specifiche_navigabili/08_e-service%20del%20SSU/08_06/08_06.md/>`_.

Ciò è dovuto al fatto che alcune comunicazioni vengono gestite direttamente da GovWay stesso, in presenza di errori di interoperabilità (ad esempio, token PDND non valido) o di problematiche di connettività verso il backend (ad esempio, connection refused o timeout). Gli errori generati da GovWay (ad esempio errori di autenticazione o indisponibilità del backend) rispettano la specifica *RFC 7807* (https://tools.ietf.org/html/rfc7807), per la rappresentazione strutturata delle informazioni di errore, come previsto dalle Linee Guida di Interoperabilità. Al contrario, il formato degli errori previsto dalla specifica SUAP non risulta conforme, in quanto prevede la trasmissione degli errori attraverso oggetti JSON con una struttura differente, di cui si riporta di seguito un esempio:

   ::

      { "code": "ERROR_401_001", "message": "PDND token not found"}

Per garantire la conformità con i formati di errore attesi è stato quindi realizzato un plugin, attivabile all’interno della configurazione delle erogazioni dei servizi SUAP su GovWay, che consente di gestire i casi di errore e di trasformarli nella struttura JSON attesa, secondo quanto descritto nella `Specifica Tecnica DPR-160 <https://github.com/AgID/specifiche-tecniche-DPR-160-2010/blob/approved02/specifiche_navigabili/08_e-service%20del%20SSU/08_06/08_06.md/>`_. 

Gli errori gestiti da GovWay sono i seguenti:

- *ERROR_400_001 - incorrect request input*: uno o più parametri e/o la forma del body dell'operation non rispettano la sintassi definita nell'IDL OpenAPI.
- *ERROR_401_001 - PDND token not found*: token di autorizzazione della PDND non presente nella richiesta.
- *ERROR_401_002 - Invalid PDND token*: token di autorizzazione della PDND non valido.
- *ERROR_401_003 - AgID-JWT-Signature* token not found: la richiesta non contiene l'header AgID-JWT-Signature.
- *ERROR_401_004 - invalid AgID-JWT-Signature token*: token nell'header AgID-JWT-Signature non valido.
- *ERROR_404_001 - resource not found*: risorsa richiesta non esistente.
- *ERROR_500_007 - response processing error*: copre solamente i due casi seguenti:

    - backend non disponibile: rappresenta la casistica in cui il backend non è raggiungibile per vari motivi (es. connection refused, connection timeout, read timeout).  
    - backend torna una risposta 5xx senza content-type o con un un content-type differente da application/json. 

.. note::
    Rimangono a carico dell'implementazione del backend SUAP gli altri codici di errore.
