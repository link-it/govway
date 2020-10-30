.. _errori_400:

Errori 400 (Bad Request)
------------------------

In questa sezione vengono riportati tutti i possibili codici di errore generati da GovWay relativi ad una richiesta client malformata.

Nella configurazione di default di GovWay, le casistiche di errore 'AttachmentsRequestFailed', 'MessageSecurityRequestFailed', 'InteroperabilityRequestManagementFailed' e 'TransformationRuleRequestFailed'
sono tutte restituiti al client con il solo codice di errore :ref:`errori_400_BadRequest`. La scelta è finalizzata ad evitare disclosure di informazioni relative al domino interno.

È possibile abilitare temporaneamente la generazione dei codici puntuali accendendo alla voce 'Strumenti - Runtime' della console di gestione e selezionando 'Errore Puntuale' per la 'Richiesta' nella sezione "Errori generati dal Gateway - Codici di errore 'GovWay-Transaction-ErrorType'" (:numref:`error400specifici`).

   .. figure:: ../../_figure_console/errori400.png
    :scale: 50%
    :align: center
    :name: error400specifici

    Attivazione temporanea degli errori specifici 400 (Bad Request)

L'abilitazione permanente può essere invece effettuata disabilitando la seguente proprietà sul file di proprietà esterno /etc/govway/errori_local.properties:

	::

		# Gateway non in grado di gestire la richiesta: AttachmentsRequestFailed, MessageSecurityRequestFailed, InteroperabilityRequestManagementFailed, TransformationRuleRequestFailed
		WRAP_400_INTERNAL_BAD_REQUEST.enabled=false

.. toctree::
        :maxdepth: 2
        
        ContentTypeNotProvided
	ContentTypeNotSupported
	SoapMustUnderstandUnknown
        SoapVersionMismatch
	UnprocessableRequestContent
	NotSupportedByProtocol
        CorrelationInformationNotFound
	ApplicationCorrelationIdentificationRequestFailed
	InvalidRequestContent
        UnexpectedInteroperabilityHeader
	InteroperabilityInvalidRequest
	AttachmentsRequestFailed
	MessageSecurityRequestFailed
	InteroperabilityRequestManagementFailed
	TransformationRuleRequestFailed
	BadRequest
