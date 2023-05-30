.. _modipa_sicurezzaMessaggio_ida03:

[INTEGRITY_SOAP_01 / INTEGRITY_REST_01 / INTEGRITY_REST_02 ]
------------------------------------------------------------

Il pattern INTEGRITY nelle sue varie declinazioni ha lo scopo di garantire l'integrità del payload del messaggio.

Il pattern differisce rispetto al tipo di trust:

- trust tramite PDND: descritto nella sezione ':ref:`modipa_idar04`', e applicabile solo per API REST, prevede l'indicazione all'interno del token di un identificativo della chiave pubblica (kid) associata alla chiave privata utilizzata dal client per firmare il token; identificativo kid generato dalla PDND e recuperabile dall'erogatore tramite le API messe a disposizione dalla PDND stessa.

- trust tra fruitore ed erogatore tramite certificati X509: descritto nella sezione ':ref:`modipa_idar03`' differisce nella sostanza se applicato per API REST, in cui viene prodotto un token JWT, o per API SOAP, in cui viene definito un header SOAP WSSecurity. In entrambi i casi il payload viene firmato e ne viene verficata l'integrità dall'erogatore.

- trust ibrido: descritto nella sezione ':ref:`modipa_pdnd_integrity`' prevede un trust tramite PDND per il token ID_AUTH e un trust tra fruitore ed erogatore con certificati x509 per il token INTEGRITY.

.. toctree::
        :maxdepth: 2

	pdnd/index
	x509/index
	x509_pdnd/index
