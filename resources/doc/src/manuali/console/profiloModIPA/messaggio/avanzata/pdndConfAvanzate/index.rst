.. _modipa_sicurezza_avanzate_pdndConfAvanzata:

Configurazione avanzata dell'integrazione verso le API PDND
---------------------------------------------------------------

L'integrazione con le :ref:`modipa_passiPreliminari_api_pdnd` consentono di ottenere le chiavi pubbliche riferite all'interno dei token di sicurezza JWT (header - kid), verificare la presenza di eventi che riguardano la modifica/eliminazione di chiavi pubbliche e consentono di ottenere maggiori informazioni relative all'identificativo client presente nel payload dei token di sicurezza JWT.

La fruizione delle :ref:`modipa_passiPreliminari_api_pdnd` richiedono un `client di tipo 'api interop' <https://docs.pagopa.it/interoperabilita-1/manuale-operativo/client-e-materiale-crittografico>`_ per poter essere consultate.

Di seguito vengono fornite i dettagli per modificare le configurazioni di default relative ai seguenti temi:

- :ref:`modipa_sicurezza_avanzate_pdndConfAvanzata_api`;
- :ref:`modipa_sicurezza_avanzate_pdndConfAvanzata_multiTenant`;
- :ref:`modipa_sicurezza_avanzate_pdndConfAvanzata_tipoTokenPolicy`.

.. toctree::
        :maxdepth: 1

	api
	multitenant
	tipoTokenPolicy

