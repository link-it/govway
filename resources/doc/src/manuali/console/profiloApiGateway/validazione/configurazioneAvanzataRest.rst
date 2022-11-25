.. _configSpecificaValidazioneRest:

Configurazione per API REST
~~~~~~~~~~~~~~~~~~~~~~~~~~~

È possibile configurare il tipo di validazione attuata su API REST registrando le seguenti :ref:`configProprieta` sull'erogazione o sulla fruizione:

.. note::
	Tutte le proprietà configurate vengono verificate in AND tra di loro. Ad esempio è quindi possibile definire sia il codice http che il Content-Type per cui si desidera abilitare una validazione.

- *validation.emptyResponse.enabled* : consente di disabilitare la validazione della risposta in caso di payload http vuoto. I valori associabili alla proprietà sono 'true' o 'false'. Per default questo controllo è abilitato.

- *validation.problemDetails.enabled* : consente di disabilitare la validazione della risposta nel caso il payload http contenga un oggetto *Problem Details* come definito nella specifica *RFC 7807* (https://tools.ietf.org/html/rfc7807). I valori associabili alle proprietà sono 'true' o 'false'. Per default questo controllo è abilitato.

- *validation.returnCode* : consente di indicare i soli codici http per cui la validazione della risposta verrà effettuata. Possono essere associati differenti valori separati con la virgola, e ogni valore puà essere un codice o un intervallo di codice (es. 200-299,404). Per default viene verificato qualsiasi codice http.

- *validation.returnCode.not* : consente di impostare una validazione della risposta solamente per i messaggi che non corrispondono ai codici http definiti nella proprietà 'validation.returnCode'.

- *validation.contentType* : consente di indicare i soli Content-Type per cui la validazione della risposta verrà effettuata. Possono essere associati differenti Content-Type separati con la virgola, e possono essere utilizzati anche i tipi speciali '<type>/\*' e '\*/\*' (es. text/xml,application/\*). Per default viene verificato qualsiasi Content-Type.

- *validation.contentType.not* : consente di impostare una validazione della risposta solamente per i messaggi che non corrispondono ai Content-Type definiti nella proprietà 'validation.contentType'.

.. note::
    Per la validazione dei messaggi con specifiche di interfaccia OpenAPI 3.x, è possibile attuare una configurazione avanzata del tipo di validazione effettuato. Maggiori dettagli vengono forniti nella sezione :ref:`configAvanzataValidazione`.
