.. _configSpecificaValidazioneReqRes:

Gestione differente tra Richiesta e Risposta
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Per default, il tipo di validazione dei messaggi impostato riguarderà sia le richieste che le risposte.

È possibile differenziare il tipo di validazione registrando le seguenti :ref:`configProprieta` sull'erogazione o sulla fruizione:

- *validation.request.enabled* o *validation.response.enabled* : consentono di modificare l'impostazione configurata rispettivamente per la richiesta o la risposta. I valori associabili alle proprietà sono 'true', 'false' o 'warning'.
- *validation.request.type* o *validation.response.type* : consentono di modificare il tipo di validazione. Per una validazione basata sulla Specifica dell'API utilizzare il valore 'interface', mentre per utilizzare solamente gli schemi indicare il valore 'xsd'.
- *validation.request.acceptMtom* o *validation.response.acceptMtom* : consentono di modificare l'impostazione configurata per i messaggi che possiedono il formato MTOM. I valori associabili alle proprietà sono 'true' o 'false'.
