Feature: Scambi di dati asincroni PDND (SOAP) - URL di callback

# Per le API SOAP la URL di callback non viene normalizzata: il connettore della fruizione dell'API di callback
# la utilizza così come comunicata (l'azione è identificata dalla SOAPAction o dal path).

Background:
    * def utils = 'classpath:test/soap/pdnd-async/pdnd-async-soap-utils.feature'
    * def interazione = read('classpath:utils/pdnd_async_interazione.js')
    * def transazione = read('classpath:utils/pdnd_async_transazione.js')
    * def tokenInfo = read('classpath:utils/pdnd_async_token_info.js')
    * def diagnostici = read('classpath:utils/pdnd_async_diagnostici.js')
    * def DIAG_FRUIZIONE = '[imbustatore.after-sec.imbustamento]: '
    * def NS_C = 'http://govway.org/pdnd/async/callback'
    * def fruizione = function(servizio){ return 'out/DemoSoggettoFruitore/DemoSoggettoErogatore/' + servizio + '/v1' }
    * def fruizioneCallback = function(servizio){ return 'out/DemoSoggettoErogatore/DemoSoggettoFruitore/' + servizio + '/v1' }
    * def faultDetail = function(r){ return karate.xmlPath(r.response, '/Envelope/Body/Fault/detail/problem/detail') }
    * def faultTitle = function(r){ return karate.xmlPath(r.response, '/Envelope/Body/Fault/detail/problem/title') }
    * def urlRicevuta = function(r){ return karate.xmlPath(r.response, '/Envelope/Body/startInteractionResponse/urlCallback') }
    * def Base64Utilities = Java.type('org.openspcoop2.utils.io.Base64Utilities')
    * def HexBinaryUtilities = Java.type('org.openspcoop2.utils.io.HexBinaryUtilities')
    * def StringType = Java.type('java.lang.String')
    * def toBase64 = function(s) { return '' + Base64Utilities.encodeAsString(new StringType(s).getBytes('UTF-8')) }
    * def toHex = function(s) { return '' + HexBinaryUtilities.encodeAsString(new StringType(s).getBytes('UTF-8')) }
    * def urlEncode = function(s) { return '' + Java.type('java.net.URLEncoder').encode(s, 'UTF-8') }

    # URL di invocazione dell'erogazione dell'API di callback (default comunicato alla PDND)
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def urlCallbackDefault = urlRicevuta(start)
    * match urlCallbackDefault == govway_base_path + '/soap/in/DemoSoggettoFruitore/PDNDAsyncSoapCompattaCallback/v1'


@soap-url-client-header
Scenario: URL di callback fornita dal client tramite header HTTP

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientHeader"))', azione: 'startInteraction', headersExtra: { 'govway-pdnd-url-callback': '#(urlCallbackDefault)' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * match urlRicevuta(start) == urlCallbackDefault
    * def ti = tokenInfo(start.tid)
    * match ti.assertion.urlCallback == urlCallbackDefault
    * def rowCheck1 = interazione(id, 'delegata')
    * match rowCheck1.url_callback == urlCallbackDefault
    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '1' } }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'
    * match cb.response /Envelope/Body/callbackInvocationResponse/conversationId == id


@soap-url-client-header-assente
Scenario: URL di callback non obbligatoria e non fornita dal client: viene utilizzata la URL dell'erogazione dell'API di callback

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientHeader"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match urlRicevuta(start) == urlCallbackDefault


@soap-url-client-con-azione
Scenario: URL di callback comprensiva dell'azione: registrata e utilizzata così come fornita (nessuna normalizzazione per SOAP)

    * def urlConAzione = urlCallbackDefault + '/callbackInvocation'
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientHeader"))', azione: 'startInteraction', headersExtra: { 'govway-pdnd-url-callback': '#(urlConAzione)' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * match urlRicevuta(start) == urlConAzione
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.url_callback == urlConAzione
    # la URL indirizza la stessa azione dell'erogazione dell'API di callback: la callback va a buon fine
    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '1' } }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'
    * match cb.response /Envelope/Body/callbackInvocationResponse/conversationId == id
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'callback_invocation'


@soap-url-client-query-base64
Scenario: URL di callback obbligatoria fornita tramite parametro della URL con codifica base64

    * def qs = '?govway_pdnd_url_callback=' + urlEncode(toBase64(urlCallbackDefault))
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientQuery"))', azione: 'startInteraction', queryString: '#(qs)' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match urlRicevuta(start) == urlCallbackDefault
    * def rowCheck2 = interazione(start.conversationIdRisposta, 'delegata')
    * match rowCheck2.url_callback == urlCallbackDefault

    # non fornita
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientQuery"))', azione: 'startInteraction', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.errorType == 'AsyncInteractionInvalidRequest'
    * match faultDetail(r) == "Callback URL not provided (query parameter 'govway_pdnd_url_callback')"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)

    # codifica non valida
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientQuery"))', azione: 'startInteraction', queryString: '?govway_pdnd_url_callback=%25%25%25', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.errorType == 'AsyncInteractionInvalidRequest'
    * match faultDetail(r) == "Callback URL provided (query parameter 'govway_pdnd_url_callback') is not valid: value is not a valid 'base64' encoded string"


@soap-url-client-hex
Scenario: URL di callback fornita tramite header HTTP con codifica esadecimale

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientHex"))', azione: 'startInteraction', headersExtra: { 'govway-pdnd-url-callback': '#(toHex(urlCallbackDefault))' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match urlRicevuta(start) == urlCallbackDefault

    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientHex"))', azione: 'startInteraction', headersExtra: { 'govway-pdnd-url-callback': 'zz-non-esadecimale' }, statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.errorType == 'AsyncInteractionInvalidRequest'
    * match faultDetail(r) == "Callback URL provided (HTTP header 'GovWay-PDND-Url-Callback') is not valid: value is not a valid 'hex' encoded string"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)


@soap-verifica-corrispondente
Scenario: verifica della URL di callback rispetto alle fruizioni: URL uguale al connettore della fruizione dell'API di callback

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapVerifica"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match urlRicevuta(start) == govway_base_path + '/soap/in/DemoSoggettoFruitore/PDNDAsyncSoapVerificaCallback/v1'
    * def rowCheck3 = interazione(start.conversationIdRisposta, 'applicativa')
    * match rowCheck3.fase == 'start_interaction'
    # fruizione della callback con connettore statico
    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapVerificaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(start.conversationIdRisposta)', headersExtra: { 'govway-pdnd-entity-number': '1' } }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'
    * match cb.response /Envelope/Body/callbackInvocationResponse/conversationId == start.conversationIdRisposta


@soap-verifica-estensione
Scenario: verifica della URL di callback rispetto alle fruizioni: URL che estende il connettore con l'azione

    * def urlFornita = govway_base_path + '/soap/in/DemoSoggettoFruitore/PDNDAsyncSoapVerificaCallback/v1/callbackInvocation'
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapVerifica-UrlClient"))', azione: 'startInteraction', headersExtra: { 'govway-pdnd-url-callback': '#(urlFornita)' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def rowCheck4 = interazione(start.conversationIdRisposta, 'applicativa')
    * match rowCheck4.url_callback == urlFornita


@soap-verifica-non-corrispondente
Scenario: verifica della URL di callback rispetto alle fruizioni: URL che non corrisponde ad alcuna fruizione

    * def urlFornita = govway_base_path + '/soap/in/DemoSoggettoFruitore/AltraCallback/v1'
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapVerifica-UrlClient"))', azione: 'startInteraction', headersExtra: { 'govway-pdnd-url-callback': '#(urlFornita)' }, statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * def dettaglioAtteso = "Callback URL '" + urlFornita + "' does not match the endpoint of any callback API subscription (fruizione) of the subject 'modipa/DemoSoggettoErogatore'"
    # SOAP Fault generato dall'erogazione e restituito al client dalla fruizione
    * match faultTitle(r) == 'AsyncInteractionInvalidRequest'
    * match faultDetail(r) == dettaglioAtteso
    * def d = diagnostici(r.tid)
    * match d contains "Ricevuto un SOAPFault in seguito all'invio della busta di cooperazione"
    * match d contains dettaglioAtteso
    # nessuna interazione registrata, da entrambi i lati
    * def ti = tokenInfo(r.tid)
    * def id = ti.accessToken.interactionId
    * def rowCheck5 = interazione(id, 'applicativa')
    * match rowCheck5 == null
    * def rowCheck6 = interazione(id, 'delegata')
    * match rowCheck6 == null

    # URL estesa con un segmento che non coincide con un path ('/v1x' non estende '/v1')
    * def urlFornita2 = govway_base_path + '/soap/in/DemoSoggettoFruitore/PDNDAsyncSoapVerificaCallback/v1x'
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapVerifica-UrlClient"))', azione: 'startInteraction', headersExtra: { 'govway-pdnd-url-callback': '#(urlFornita2)' }, statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match faultTitle(r) == 'AsyncInteractionInvalidRequest'


@soap-verifica-solo-connettori-dinamici
Scenario: verifica della URL di callback abilitata con fruizioni dell'API di callback dotate solamente di connettore dinamico: verifica non effettuata

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapScadenze"))', azione: 'startInteraction', headersExtra: { 'govway-pdnd-url-callback': 'http://host-qualsiasi/callback' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match urlRicevuta(start) == 'http://host-qualsiasi/callback'


@soap-url-client-header-base64-nome-personalizzato
Scenario: URL di callback fornita tramite header HTTP con nome personalizzato e codifica base64

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientHeaderBase64"))', azione: 'startInteraction', headersExtra: { 'govway-test-url-callback': '#(toBase64(urlCallbackDefault))' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match urlRicevuta(start) == urlCallbackDefault
    * def ti = tokenInfo(start.tid)
    * match ti.assertion.urlCallback == urlCallbackDefault

    # l'header con il nome di default non viene considerato: URL non obbligatoria, viene utilizzata l'erogazione dell'API di callback
    * def urlAltra = urlCallbackDefault + '/altra'
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientHeaderBase64"))', azione: 'startInteraction', headersExtra: { 'govway-pdnd-url-callback': '#(toBase64(urlAltra))' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match urlRicevuta(start) == urlCallbackDefault

    # codifica non valida
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientHeaderBase64"))', azione: 'startInteraction', headersExtra: { 'govway-test-url-callback': '%%%' }, statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.errorType == 'AsyncInteractionInvalidRequest'
    * match faultDetail(r) == "Callback URL provided (HTTP header 'GovWay-Test-Url-Callback') is not valid: value is not a valid 'base64' encoded string"


@soap-url-client-query-none-nome-personalizzato
Scenario: URL di callback obbligatoria fornita tramite parametro della URL con nome personalizzato e senza codifica

    * def qs = '?test_url_callback=' + urlEncode(urlCallbackDefault)
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientQueryNone"))', azione: 'startInteraction', queryString: '#(qs)' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match urlRicevuta(start) == urlCallbackDefault
    * def ti = tokenInfo(start.tid)
    * match ti.assertion.urlCallback == urlCallbackDefault

    # il parametro con il nome di default non viene considerato: URL obbligatoria non fornita
    * def qs = '?govway_pdnd_url_callback=' + urlEncode(urlCallbackDefault)
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientQueryNone"))', azione: 'startInteraction', queryString: '#(qs)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.errorType == 'AsyncInteractionInvalidRequest'
    * match faultDetail(r) == "Callback URL not provided (query parameter 'test_url_callback')"


@soap-url-client-query-hex
Scenario: URL di callback fornita tramite parametro della URL con codifica esadecimale

    * def qs = '?govway_pdnd_url_callback=' + toHex(urlCallbackDefault)
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientQueryHex"))', azione: 'startInteraction', queryString: '#(qs)' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match urlRicevuta(start) == urlCallbackDefault
    * def rowCheck = interazione(start.conversationIdRisposta, 'delegata')
    * match rowCheck.url_callback == urlCallbackDefault

    # codifica non valida
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientQueryHex"))', azione: 'startInteraction', queryString: '?govway_pdnd_url_callback=zz', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.errorType == 'AsyncInteractionInvalidRequest'
    * match faultDetail(r) == "Callback URL provided (query parameter 'govway_pdnd_url_callback') is not valid: value is not a valid 'hex' encoded string"


@soap-codifica-header-erogazione
Scenario: codifica dell'header con cui l'erogazione inoltra la URL di callback al backend (base64 ed esadecimale)

    # base64: il backend riceve la URL codificata, mentre nelle interazioni registrate la URL è in chiaro
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-CodificaHeaderBase64"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match urlRicevuta(start) == toBase64(urlCallbackDefault)
    * def erogatore = interazione(start.conversationIdRisposta, 'applicativa')
    * match erogatore.url_callback == urlCallbackDefault
    * def fruitore = interazione(start.conversationIdRisposta, 'delegata')
    * match fruitore.url_callback == urlCallbackDefault

    # esadecimale
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-CodificaHeaderHex"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match urlRicevuta(start) == toHex(urlCallbackDefault)
    * def erogatore = interazione(start.conversationIdRisposta, 'applicativa')
    * match erogatore.url_callback == urlCallbackDefault


@soap-url-callback-keyword-original
Scenario: URL di callback verso il mock: per SOAP le keyword ${context:pdndAsyncUrlCallback} e ${context:pdndAsyncUrlCallbackOriginal} utilizzano la stessa URL (nessuna normalizzazione)

    # il mock restituisce nell'elemento 'urlCallback' il percorso ricevuto dopo '/pdnd-async/url-callback'
    * def urlMock = 'http://localhost:' + karate.properties['http_mock_port'] + '/pdnd-async/url-callback/TestSoap/callbackInvocation'

    # fruizione della callback con ${context:pdndAsyncUrlCallback}
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientHeader"))', azione: 'startInteraction', headersExtra: { 'govway-pdnd-url-callback': '#(urlMock)' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id1 = start.conversationIdRisposta
    * def cb1 = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id1)', headersExtra: { 'govway-pdnd-entity-number': '1' } }
    * match transazione(cb1.tid).tipo_servizio_correlato == 'callback_invocation'
    * def percorso1 = karate.xmlPath(cb1.response, '/Envelope/Body/callbackInvocationResponse/urlCallback')
    * assert percorso1.startsWith('/TestSoap/callbackInvocation')
    * def erogatore = interazione(id1, 'applicativa')
    * match erogatore.fase == 'callback_invocation'

    # fruizione della callback con ${context:pdndAsyncUrlCallbackOriginal}
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapScadenze"))', azione: 'startInteraction', headersExtra: { 'govway-pdnd-url-callback': '#(urlMock)' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id2 = start.conversationIdRisposta
    * def cb2 = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapScadenzeCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id2)', headersExtra: { 'govway-pdnd-entity-number': '1' } }
    * match transazione(cb2.tid).tipo_servizio_correlato == 'callback_invocation'
    * def percorso2 = karate.xmlPath(cb2.response, '/Envelope/Body/callbackInvocationResponse/urlCallback')
    * match percorso2 == percorso1
    * def erogatore = interazione(id2, 'applicativa')
    * match erogatore.fase == 'callback_invocation'
