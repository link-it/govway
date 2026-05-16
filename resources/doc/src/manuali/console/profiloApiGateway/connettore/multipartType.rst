.. _multipartTypeMissing:

Content-Type 'multipart/related' privo del parametro 'type'
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

La `RFC 2387, Section 3.1 <https://datatracker.ietf.org/doc/html/rfc2387#section-3.1>`_ impone che il parametro *type* sia sempre presente in un *Content-Type* '*multipart/related*', in modo che il consumer possa determinare il media type della root part del multipart senza dover ispezionare il payload. Per i messaggi SOAP-with-Attachments la W3C Note `"SOAP Messages with Attachments", Section 3 <https://www.w3.org/TR/SOAP-attachments>`_ e il `WS-I Attachments Profile 1.0, R2904 <http://www.ws-i.org/Profiles/AttachmentsProfile-1.0.html#Conformance_Requirements>`_ richiedono inoltre *type="text/xml"* per SOAP 1.1 (e analogamente *"application/soap+xml"* per SOAP 1.2).

In presenza di un backend o di un client che non rispetta tale obbligo, GovWay può applicare una compensazione tampone che consente comunque il processamento del messaggio, registrando contestualmente un diagnostico dedicato che evidenzia l'anomalia rilevata.

È possibile personalizzare il comportamento di GovWay registrando le seguenti :ref:`configProprieta` sulla singola erogazione o fruizione. La strategia di compensazione è configurabile separatamente per richiesta e risposta:

- *connettori.multipart.related.missingType.request.behavior*: strategia applicata alla richiesta ricevuta dal client;

- *connettori.multipart.related.missingType.response.behavior*: strategia applicata alla risposta ricevuta dal backend;

- *connettori.multipart.related.missingType.behavior*: ombrello che imposta la stessa strategia sia per richiesta sia per risposta (valido in mancanza di un'impostazione specifica con le proprietà precedenti).

I valori ammessi per le proprietà di tipo *behavior* sono i seguenti:

- *none*: GovWay non applica alcuna compensazione e lascia che il classificatore standard del Content-Type processi il messaggio. È il valore di default lato richiesta.

- *inferFromRequest*: GovWay deriva il parametro *type* mancante dalla versione SOAP della richiesta associata (*text/xml* per SOAP 1.1, *application/soap+xml* per SOAP 1.2). È il valore di default lato risposta.

- *inferFromBody*: GovWay ispeziona i primi byte del payload alla ricerca del *Content-Type* della root part del multipart e ne deriva il parametro *type* mancante. Il numero massimo di byte ispezionati è configurabile (vedi sotto).

- *forceSoap11*: GovWay forza il parametro *type* al valore *"text/xml"*.

- *forceSoap12*: GovWay forza il parametro *type* al valore *"application/soap+xml"*.

.. note::
   La strategia *inferFromRequest* è applicabile esclusivamente lato risposta, poiché la richiesta è il messaggio in ingresso al gateway e non esiste alcun messaggio precedente da cui dedurre la versione SOAP. Pertanto, se impostata sulla proprietà *connettori.multipart.related.missingType.request.behavior* o sull'ombrello *connettori.multipart.related.missingType.behavior*, la configurazione è considerata non applicabile e viene degradata silenziosamente alla strategia *none* (per le proprietà globali in *govway.properties* la stessa anomalia viene segnalata nei log di boot).

Per la strategia *inferFromBody* è possibile configurare il numero massimo di byte ispezionati con le seguenti proprietà (il valore rappresenta un upper bound; il peek si interrompe non appena il *Content-Type* della root part viene determinato):

- *connettori.multipart.related.missingType.request.peekBytes*: numero massimo di byte ispezionati sulla richiesta;

- *connettori.multipart.related.missingType.response.peekBytes*: numero massimo di byte ispezionati sulla risposta;

- *connettori.multipart.related.missingType.peekBytes*: ombrello per richiesta e risposta.

Il valore di default è *4096* byte, sufficiente a coprire i casi in cui la root part presenta header MIME particolarmente articolati.

.. note::
   Quando la compensazione viene effettivamente applicata viene emesso il diagnostico *contentType.multipart.related.missingType.compensated*, classificato come 'warning' (vedi :ref:`mon_dettaglioErrore`). L'esito della transazione viene marcato come 'Ok (Presenza Anomalie)' per consentire all'amministratore di filtrare facilmente le transazioni che hanno richiesto la compensazione.
