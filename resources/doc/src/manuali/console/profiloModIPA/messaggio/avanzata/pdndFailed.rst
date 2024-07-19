.. _modipa_sicurezza_avanzate_pdndFailed:

Recupero informazioni client tramite API PDND fallito
------------------------------------------------------------

È possibile modificare il comportamento di default, descritto nella sezione :ref:`modipa_passiPreliminari_api_pdnd`, per far fallire la transazione in caso il recupero delle informazioni sul client o sull'organizzazione tramite :ref:`modipa_passiPreliminari_api_pdnd` fallisca.

Per attivare il fallimento della transazione è necessario registrare le seguenti :ref:`configProprieta`:

- '*pdnd.readByApiInterop.client.failed.abortTransaction*' valorizzata a '*true*' per richiedere il fallimento della transazione nel caso in cui non sia stato possibile recuperare le informazioni sul client attraverso la risorsa 'GET /clients/{clientId}'.
- '*pdnd.readByApiInterop.organization.failed.abortTransaction*' valorizzata a '*true*' per richiedere il fallimento della transazione nel caso in cui non sia stato possibile recuperare le informazioni sull'organizzazione attraverso la risorsa 'GET /organizations/{organizationId}'.

