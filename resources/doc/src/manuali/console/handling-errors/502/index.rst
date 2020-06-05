.. _errori_502:

Errori 502 (Bad Gateway)
------------------------

In questa sezione vengono raccolti tutti i possibili codici di errore generati da GovWay riguardanti errori emersi durante la gestione della risposta.

Gli errori descritti in questa sezione, nella configurazione di default di GovWay, sono tutti ritornati al client con il solo codice di errore :ref:`errori_502_InvalidResponse`. La scelta cerca di evitare disclosure di informazioni riguardanti il domino interno.

È possibile abilitare temporaneamente la generazione dei codici specifici accendendo alla voce 'Strumenti - Runtime' della console di gestione e abilitando la 'Gestione Risposta' nella sezione 'Errori generati dal Gateway' (:numref:`error502specifici`).

   .. figure:: ../../_figure_console/errori502.png
    :scale: 50%
    :align: center
    :name: error502specifici

    Attivazione temporanea degli errori specifici 502 (Bad Gateway)

Una abilitazione permanente è invece attuabile agendo sul file di proprietà esterno /etc/govway/errori_local.properties disabilitando le seguenti proprietà:

	::

		WRAP_502_BAD_RESPONSE.enabled=false
		WRAP_502_INTERNAL_RESPONSE_ERROR.enabled=false

.. toctree::
        :maxdepth: 2
        
        InvalidResponse
	UnprocessableResponseContent
	AttachmentsResponseFailed
	ApplicationCorrelationIdentificationResponseFailed
	MessageSecurityResponseFailed
	InvalidResponseContent
	InteroperabilityResponseManagementFailed
	InteroperabilityInvalidResponse
	UnexpectedInteroperabilityResponseHeader
	InteroperabilityResponseError
	TransformationRuleResponseFailed
	ExpectedResponseNotReceived
	ConflictResponse
	BadResponse
	GatewayError
	
	
