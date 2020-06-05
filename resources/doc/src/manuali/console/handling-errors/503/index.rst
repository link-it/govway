.. _errori_503:

Errori 503 (Service Unavailable)
--------------------------------

In questa sezione vengono raccolti tutti i possibili codici di errore generati da GovWay riguardanti errori emersi durante la gestione della richiesta. Gli errori sono classificabili in:

- Indisponibilità temporanea del backend che implementa l'API; l'errore viene identificato dal codice di errore 'APIUnavailable'.  
- Accesso all'API sospeso su GovWay; l'errore viene identificato dal codice di errore 'APISuspended'.  
- Il gateway non è momentaneamente in grado di gestire la richiesta per errori di vario genere; rientrano in questa casistica i codici di errore: AttachmentsRequestFailed, MessageSecurityRequestFailed, InteroperabilitRequestManagementFailed, TransformationRuleRequestFailed
- Il gateway è momentaneamente indisponibile; rientrano in questa casistica i codici di errore: GatewayInactive, GatewayUnavailable, GatewayError 

Nella configurazione di default di GovWay, gli errori che indicano una incapacità del Gateway di gestire la richiesta sono tutti ritornati al client con il solo codice di errore :ref:`errori_400_BadRequest`.
Inoltre gli errori che indicano una indisponibilità momentanea del gateway sono tutti ritornati al client con il solo codice di errore :ref:`errori_503_APIUnavailable`.
La scelta di non tornare l'errore specifico, nei casi suddetti, cerca di evitare disclosure di informazioni riguardanti il domino interno.

È possibile abilitare temporaneamente la generazione dei codici specifici accendendo alla voce 'Strumenti - Runtime' della console di gestione e abilitando 'Gestione Richiesta' o 'Errori Interni' nella sezione 'Errori generati dal Gateway' (:numref:`error503specifici`).

   .. figure:: ../../_figure_console/errori503.png
    :scale: 50%
    :align: center
    :name: error503specifici

    Attivazione temporanea degli errori specifici 503 (Service Unavailable)

Una abilitazione permanente è invece attuabile agendo sul file di proprietà esterno /etc/govway/errori_local.properties disabilitando le seguenti proprietà:

	::

		# Gateway non in grado di gestire la richiesta: AttachmentsRequestFailed, MessageSecurityRequestFailed, InteroperabilitRequestManagementFailed, TransformationRuleRequestFailed
		WRAP_503_INTERNAL_REQUEST_ERROR.enabled=false
		# Gateway momentaneamente indisponibile: GatewayInactive, GatewayUnavailable, GatewayError 
		WRAP_503_INTERNAL_REQUEST_ERROR_GOVWAY.enabled=false

.. toctree::
        :maxdepth: 2
        
        APIUnavailable
	APISuspended
	AttachmentsRequestFailed
	MessageSecurityRequestFailed
	InteroperabilityRequestManagementFailed
	TransformationRuleRequestFailed
	GatewayInactive
	GatewayUnavailable
	GatewayError

