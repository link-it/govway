.. _avanzate_connettori_encodedWord:

Header HTTP
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**MIME encoded-word encoding**

I valori degli header che includono caratteri non ASCII vengono codificati per default attraverso la codifica MIME encoded-word definita nel RFC 2047 con encoding style 'Q' (Quoted-Printable).

È possibile disabilitare la codifica o personalizzarne lo stile su una singola erogazione o fruizione di API attraverso la definizione delle seguenti :ref:`configProprieta`:

- *connettori.header.value.encodingRFC2047.enabled* (true/false default:true): consente di abilitare o disabilitare la codifica;
- *connettori.header.value.encodingRFC2047.type* (Q/B default:Q): specifica lo stile di codifica da utilizzare tra Quoted-Printable o Base64;
- *connettori.header.value.encodingRFC2047.charset* (default: US-ASCII): consente di indicare il charset da utilizzare per determinare se una codifica del valore di un header è necessaria o meno;

È inoltre possibile definire delle proprietà specifiche per il trattamento delle richieste o delle risposte che sovrascrivono il comportamento indicato nelle proprietà generiche:

- *connettori.header.value.encodingRFC2047.request.enabled*;
- *connettori.header.value.encodingRFC2047.response.enabled*;
- *connettori.header.value.encodingRFC2047.request.type*;
- *connettori.header.value.encodingRFC2047.response.type*;
- *connettori.header.value.encodingRFC2047.request.charset*;
- *connettori.header.value.encodingRFC2047.response.charset*.

**RFC 7230 section 3.2. Header Fields**

Viene attuata una validazione sia del nome che del valore di un header, in conformità con quanto indicato nella specifica 'RFC 7230 - sezione 3.2'. Se viene rilevato un errore, l'header HTTP non verrà inoltrato.

È possibile disabilitare la validazione su una singola erogazione o fruizione di API attraverso la definizione della seguente :ref:`configProprieta`:

- *connettori.header.validation.enabled* (true/false default:true): consente di abilitare o disabilitare la validazione;

È inoltre possibile definire delle proprietà specifiche per il trattamento delle richieste o delle risposte che sovrascrivono il comportamento indicato nelle proprietà generiche:

- *connettori.header.validation.request.enabled*;
- *connettori.header.validation.response.enabled*.



