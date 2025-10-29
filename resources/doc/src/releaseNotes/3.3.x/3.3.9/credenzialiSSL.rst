Miglioramenti alla gestione dei Certificati X.509
------------------------------------------------------------------

.. note::

   Nuova Funzionalità introdotta nella versione '3.3.9.p3'

Per ogni certificato è adesso possibile accedere anche alle seguenti informazioni:

- verificare se una Certificate Policy è presente o meno sul certificato ed accedere alle informazioni interne della policy;

- verificare i basic constraints (CA, pathLen);

- ottenere le Authority Information Access presenti nel certificato:

	- CA Issuers;
	- OCSP.

- ottenere i CRL Distribution Points presenti nel certificato.
