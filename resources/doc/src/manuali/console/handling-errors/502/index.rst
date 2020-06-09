.. _errori_502:

Errori 502 (Bad Gateway)
------------------------

In questa sezione vengono riportati tutti i possibili codici di errore generati da GovWay relativi a errori emersi durante la gestione della risposta.

Gli errori descritti in questa sezione, nella configurazione di default di GovWay, sono tutti restituiti al client con il solo codice di errore :ref:`errori_502_InvalidResponse`. La scelta è finalizzata ad evitare disclosure di informazioni relative al domino interno.

È possibile abilitare temporaneamente la generazione dei codici puntuali accendendo alla voce 'Strumenti - Runtime' della console di gestione e selezionando 'Errore Puntuale' per la 'Risposta' nella sezione "Errori generati dal Gateway - Codici di errore 'GovWay-Transaction-ErrorType'" (:numref:`error502specifici`).

   .. figure:: ../../_figure_console/errori502.png
    :scale: 50%
    :align: center
    :name: error502specifici

    Attivazione temporanea degli errori specifici 502 (Bad Gateway)

L'abilitazione permanente può essere invece effettuata disabilitando le seguenti proprietà sul file di proprietà esterno /etc/govway/errori_local.properties:

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
