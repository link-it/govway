Feature: Scambi di dati asincroni PDND (REST) - URL di callback

Background:
    * def utils = 'classpath:test/rest/pdnd-async/pdnd-async-utils.feature'
    * def interazione = read('classpath:utils/pdnd_async_interazione.js')
    * def transazione = read('classpath:utils/pdnd_async_transazione.js')
    * def diagnostici = read('classpath:utils/pdnd_async_diagnostici.js')
    * def tokenInfo = read('classpath:utils/pdnd_async_token_info.js')
    * def Base64Utilities = Java.type('org.openspcoop2.utils.io.Base64Utilities')
    * def HexBinaryUtilities = Java.type('org.openspcoop2.utils.io.HexBinaryUtilities')
    * def StringType = Java.type('java.lang.String')
    * def toBase64 = function(s) { return '' + Base64Utilities.encodeAsString(new StringType(s).getBytes('UTF-8')) }
    * def toHex = function(s) { return '' + HexBinaryUtilities.encodeAsString(new StringType(s).getBytes('UTF-8')) }
    * def urlEncode = function(s) { return '' + Java.type('java.net.URLEncoder').encode(s, 'UTF-8') }

    # URL di invocazione dell'erogazione dell'API di callback (default comunicato alla PDND)
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def urlCallbackDefault = start.response.received.urlCallback
    * match urlCallbackDefault contains '/DemoSoggettoFruitore/PDNDAsyncRestCompattaCallback/v1'


@url-client-header
Scenario: URL di callback fornita dal client tramite header HTTP

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientHeader', headersExtra: { 'govway-pdnd-url-callback': '#(urlCallbackDefault)' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * match start.response.received.urlCallback == urlCallbackDefault
    * def ti = tokenInfo(start.tid)
    * match ti.assertion.urlCallback == urlCallbackDefault
    * def rowCheck1 = interazione(id, 'delegata')
    * match rowCheck1.url_callback == urlCallbackDefault
    * def callback = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)' }
    * match transazione(callback.tid).tipo_servizio_correlato == 'callback_invocation'
    * match callback.response.received.conversationId == id


@url-client-header-assente
Scenario: URL di callback non obbligatoria e non fornita dal client: viene utilizzata la URL dell'erogazione dell'API di callback

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientHeader' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.received.urlCallback == urlCallbackDefault


@url-client-normalizzata
Scenario: URL di callback fornita comprensiva del path della risorsa: il connettore dinamico utilizza la URL normalizzata

    * def urlConRisorsa = urlCallbackDefault + '/notifications'
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientHeader', headersExtra: { 'govway-pdnd-url-callback': '#(urlConRisorsa)' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    # la URL viene comunicata e registrata così come fornita
    * match start.response.received.urlCallback == urlConRisorsa
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.url_callback == urlConRisorsa
    # ${context:pdndAsyncUrlCallback}: il path della risorsa non viene ripetuto
    * def callback = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)' }
    * match transazione(callback.tid).tipo_servizio_correlato == 'callback_invocation'
    * match callback.response.received.conversationId == id
    * def rowCheck2 = interazione(id, 'delegata')
    * match rowCheck2.fase == 'callback_invocation'


@url-original
Scenario: keyword ${context:pdndAsyncUrlCallbackOriginal}: la URL viene utilizzata esattamente come comunicata (il path della risorsa viene ripetuto)

    * def urlCallbackScadenze = urlCallbackDefault.replace('PDNDAsyncRestCompattaCallback', 'PDNDAsyncRestScadenzeCallback')
    * def urlConRisorsa = urlCallbackScadenze + '/notifications'
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestScadenze', headersExtra: { 'govway-pdnd-url-callback': '#(urlConRisorsa)' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * def callback = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestScadenzeCallback', conversationId: '#(id)', statusAtteso: '#? _ != 200' }
    * match transazione(callback.tid).tipo_servizio_correlato == 'callback_invocation'
    # la callback raggiunge '.../notifications/notifications', risorsa non esistente: nessuna registrazione
    * def rowCheck3 = interazione(id, 'applicativa')
    * match rowCheck3.fase == 'start_interaction'
    * def rowCheck4 = interazione(id, 'delegata')
    * match rowCheck4.fase == 'start_interaction'


@url-client-query-base64
Scenario: URL di callback obbligatoria fornita tramite parametro della URL con codifica base64

    * def qs = '?govway_pdnd_url_callback=' + urlEncode(toBase64(urlCallbackDefault))
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientQuery', queryString: '#(qs)' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.received.urlCallback == urlCallbackDefault
    * def rowCheck5 = interazione(start.conversationId, 'delegata')
    * match rowCheck5.url_callback == urlCallbackDefault

    # non fornita
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientQuery', statusAtteso: 400 }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.title == 'AsyncInteractionInvalidRequest'
    * match start.response.detail == "Callback URL not provided (query parameter 'govway_pdnd_url_callback')"

    # codifica non valida
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientQuery', queryString: '?govway_pdnd_url_callback=%25%25%25', statusAtteso: 400 }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.title == 'AsyncInteractionInvalidRequest'
    * match start.response.detail == "Callback URL provided (query parameter 'govway_pdnd_url_callback') is not valid: value is not a valid 'base64' encoded string"


@url-client-hex
Scenario: URL di callback fornita tramite header HTTP con codifica esadecimale

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientHex', headersExtra: { 'govway-pdnd-url-callback': '#(toHex(urlCallbackDefault))' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.received.urlCallback == urlCallbackDefault

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientHex', headersExtra: { 'govway-pdnd-url-callback': 'zz-non-esadecimale' }, statusAtteso: 400 }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.title == 'AsyncInteractionInvalidRequest'
    * match start.response.detail == "Callback URL provided (HTTP header 'GovWay-PDND-Url-Callback') is not valid: value is not a valid 'hex' encoded string"


@verifica-corrispondente
Scenario: verifica della URL di callback rispetto alle fruizioni: URL uguale al connettore della fruizione dell'API di callback

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestVerifica' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.received.urlCallback contains '/DemoSoggettoFruitore/PDNDAsyncRestVerificaCallback/v1'
    * def rowCheck6 = interazione(start.conversationId, 'applicativa')
    * match rowCheck6.fase == 'start_interaction'
    * def callback = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestVerificaCallback', conversationId: '#(start.conversationId)' }
    * match transazione(callback.tid).tipo_servizio_correlato == 'callback_invocation'


@verifica-estensione
Scenario: verifica della URL di callback rispetto alle fruizioni: URL che estende il connettore con il path della risorsa

    * def urlFornita = govway_base_path + '/rest/in/DemoSoggettoFruitore/PDNDAsyncRestVerificaCallback/v1/notifications'
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestVerifica-UrlClient', headersExtra: { 'govway-pdnd-url-callback': '#(urlFornita)' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def rowCheck7 = interazione(start.conversationId, 'applicativa')
    * match rowCheck7.url_callback == urlFornita


@verifica-non-corrispondente
Scenario: verifica della URL di callback rispetto alle fruizioni: URL che non corrisponde ad alcuna fruizione

    * def urlFornita = govway_base_path + '/rest/in/DemoSoggettoFruitore/AltraCallback/v1'
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestVerifica-UrlClient', headersExtra: { 'govway-pdnd-url-callback': '#(urlFornita)' }, statusAtteso: 400 }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.title == 'AsyncInteractionInvalidRequest'
    * match start.response.detail == "Callback URL '" + urlFornita + "' does not match the endpoint of any callback API subscription (fruizione) of the subject 'modipa/DemoSoggettoErogatore'"
    # errore rilevato dall'erogazione e restituito alla fruizione, che registra il problem detail ricevuto
    * def d = diagnostici(start.tid)
    * match d contains "Ricevuto un Problem Detail (RFC 7807) in seguito all'invio della busta di cooperazione"
    * match d contains start.response.detail
    # nessuna interazione registrata, da entrambi i lati
    * def ti = tokenInfo(start.tid)
    * def id = ti.accessToken.interactionId
    * def rowCheck8 = interazione(id, 'applicativa')
    * match rowCheck8 == null
    * def rowCheck9 = interazione(id, 'delegata')
    * match rowCheck9 == null

    # stessa URL estesa con un segmento che non coincide con un path ('/v1x' non estende '/v1')
    * def urlFornita2 = govway_base_path + '/rest/in/DemoSoggettoFruitore/PDNDAsyncRestVerificaCallback/v1x'
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestVerifica-UrlClient', headersExtra: { 'govway-pdnd-url-callback': '#(urlFornita2)' }, statusAtteso: 400 }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.title == 'AsyncInteractionInvalidRequest'


@verifica-solo-connettori-dinamici
Scenario: verifica della URL di callback abilitata con fruizioni dell'API di callback dotate solamente di connettore dinamico: verifica non effettuata

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestScadenze', headersExtra: { 'govway-pdnd-url-callback': 'http://host-qualsiasi/callback' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.received.urlCallback == 'http://host-qualsiasi/callback'


@url-client-header-base64-nome-personalizzato
Scenario: URL di callback fornita tramite header HTTP con nome personalizzato e codifica base64

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientHeaderBase64', headersExtra: { 'govway-test-url-callback': '#(toBase64(urlCallbackDefault))' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.received.urlCallback == urlCallbackDefault
    * def ti = tokenInfo(start.tid)
    * match ti.assertion.urlCallback == urlCallbackDefault

    # l'header con il nome di default non viene considerato: URL non obbligatoria, viene utilizzata l'erogazione dell'API di callback
    * def urlAltra = urlCallbackDefault + '/altra'
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientHeaderBase64', headersExtra: { 'govway-pdnd-url-callback': '#(toBase64(urlAltra))' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.received.urlCallback == urlCallbackDefault

    # codifica non valida
    * def r = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientHeaderBase64', headersExtra: { 'govway-test-url-callback': '%%%' }, statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.response.title == 'AsyncInteractionInvalidRequest'
    * match r.response.detail == "Callback URL provided (HTTP header 'GovWay-Test-Url-Callback') is not valid: value is not a valid 'base64' encoded string"


@url-client-query-none-nome-personalizzato
Scenario: URL di callback obbligatoria fornita tramite parametro della URL con nome personalizzato e senza codifica

    * def qs = '?test_url_callback=' + urlEncode(urlCallbackDefault)
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientQueryNone', queryString: '#(qs)' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.received.urlCallback == urlCallbackDefault
    * def ti = tokenInfo(start.tid)
    * match ti.assertion.urlCallback == urlCallbackDefault

    # il parametro con il nome di default non viene considerato: URL obbligatoria non fornita
    * def qs = '?govway_pdnd_url_callback=' + urlEncode(urlCallbackDefault)
    * def r = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientQueryNone', queryString: '#(qs)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.response.title == 'AsyncInteractionInvalidRequest'
    * match r.response.detail == "Callback URL not provided (query parameter 'test_url_callback')"


@url-client-query-hex
Scenario: URL di callback fornita tramite parametro della URL con codifica esadecimale

    * def qs = '?govway_pdnd_url_callback=' + toHex(urlCallbackDefault)
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientQueryHex', queryString: '#(qs)' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.received.urlCallback == urlCallbackDefault
    * def rowCheck = interazione(start.conversationId, 'delegata')
    * match rowCheck.url_callback == urlCallbackDefault

    # codifica non valida
    * def r = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-UrlClientQueryHex', queryString: '?govway_pdnd_url_callback=zz', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.response.title == 'AsyncInteractionInvalidRequest'
    * match r.response.detail == "Callback URL provided (query parameter 'govway_pdnd_url_callback') is not valid: value is not a valid 'hex' encoded string"


@codifica-header-erogazione
Scenario: codifica dell'header con cui l'erogazione inoltra la URL di callback al backend (base64 ed esadecimale)

    # base64: il backend riceve la URL codificata, mentre nelle interazioni registrate la URL è in chiaro
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-CodificaHeaderBase64' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.received.urlCallback == toBase64(urlCallbackDefault)
    * def erogatore = interazione(start.conversationId, 'applicativa')
    * match erogatore.url_callback == urlCallbackDefault
    * def fruitore = interazione(start.conversationId, 'delegata')
    * match fruitore.url_callback == urlCallbackDefault

    # esadecimale
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-CodificaHeaderHex' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.received.urlCallback == toHex(urlCallbackDefault)
    * def erogatore = interazione(start.conversationId, 'applicativa')
    * match erogatore.url_callback == urlCallbackDefault


@url-callback-normalizzazione
Scenario Outline: URL di callback verso il mock: normalizzazione del path della risorsa (<caso>)

    # la URL comunicata dal fruitore punta direttamente al mock, che restituisce il percorso ricevuto dopo '/pdnd-async/url-callback'
    * def urlMock = 'http://localhost:' + karate.properties['http_mock_port'] + '/pdnd-async/url-callback/Test' + '<suffisso>'
    * def start = call read(utils + '@start') { servizio: '<servizio>', headersExtra: { 'govway-pdnd-url-callback': '#(urlMock)' } }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * match start.response.received.urlCallback == urlMock
    * def callback = call read(utils + '@callback') { servizioCallback: '<servizioCallback>', conversationId: '#(id)' }
    * match transazione(callback.tid).tipo_servizio_correlato == 'callback_invocation'
    * match callback.response.percorso == '<percorsoAtteso>'
    * match callback.response.tenant == <tenantAtteso>
    # la URL viene registrata così come comunicata; l'erogatore registra la callback
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.url_callback == urlMock
    * match erogatore.fase == 'callback_invocation'

    Examples:
    | caso                                                   | servizio                                | servizioCallback               | suffisso                   | percorsoAtteso                    | tenantAtteso |
    | URL senza slash finale                                 | PDNDAsyncRestCompatta-UrlClientHeader   | PDNDAsyncRestCompattaCallback  |                            | /Test/notifications               | null         |
    | URL con slash finale                                   | PDNDAsyncRestCompatta-UrlClientHeader   | PDNDAsyncRestCompattaCallback  | /                          | /Test/notifications               | null         |
    | URL che termina con il path della risorsa              | PDNDAsyncRestCompatta-UrlClientHeader   | PDNDAsyncRestCompattaCallback  | /notifications             | /Test/notifications               | null         |
    | URL che termina con il path della risorsa e slash      | PDNDAsyncRestCompatta-UrlClientHeader   | PDNDAsyncRestCompattaCallback  | /notifications/            | /Test/notifications               | null         |
    | URL con path della risorsa e query string              | PDNDAsyncRestCompatta-UrlClientHeader   | PDNDAsyncRestCompattaCallback  | /notifications?tenant=t1   | /Test/notifications               | 't1'         |
    | URL con query string senza path della risorsa          | PDNDAsyncRestCompatta-UrlClientHeader   | PDNDAsyncRestCompattaCallback  | ?tenant=t1                 | /Test/notifications               | 't1'         |
    | URL che termina con un path simile (non normalizzata)  | PDNDAsyncRestCompatta-UrlClientHeader   | PDNDAsyncRestCompattaCallback  | /mynotifications           | /Test/mynotifications/notifications | null       |
    | keyword Original, URL senza path della risorsa         | PDNDAsyncRestScadenze                   | PDNDAsyncRestScadenzeCallback  |                            | /Test/notifications               | null         |
    | keyword Original, URL con il path della risorsa        | PDNDAsyncRestScadenze                   | PDNDAsyncRestScadenzeCallback  | /notifications             | /Test/notifications/notifications | null         |
