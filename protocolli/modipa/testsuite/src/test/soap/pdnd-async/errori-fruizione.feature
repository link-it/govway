Feature: Scambi di dati asincroni PDND (SOAP) - errori rilevati lato fruizione

# Gli errori vengono restituiti come SOAP Fault (HTTP 500); il codice HTTP del problem detail riporta il codice dell'errore (400/409).

Background:
    * def utils = 'classpath:test/soap/pdnd-async/pdnd-async-soap-utils.feature'
    * def interazione = read('classpath:utils/pdnd_async_interazione.js')
    * def diagnostici = read('classpath:utils/pdnd_async_diagnostici.js')
    * def transazione = read('classpath:utils/pdnd_async_transazione.js')
    * def sleep = function(ms){ java.lang.Thread.sleep(ms) }
    # errori rilevati dalla fruizione durante l'imbustamento della richiesta (verifica dello stato dell'interazione)
    * def DIAG_FRUIZIONE = '[imbustatore.after-sec.imbustamento]: '
    * def NS_C = 'http://govway.org/pdnd/async/callback'
    * def fruizione = function(servizio){ return 'out/DemoSoggettoFruitore/DemoSoggettoErogatore/' + servizio + '/v1' }
    * def fruizioneCallback = function(servizio){ return 'out/DemoSoggettoErogatore/DemoSoggettoFruitore/' + servizio + '/v1' }
    * def faultDetail = function(r){ return karate.xmlPath(r.response, '/Envelope/Body/Fault/detail/problem/detail') }
    * def faultStatus = function(r){ return karate.xmlPath(r.response, '/Envelope/Body/Fault/detail/problem/status') }
    * def entity = function(n){ return { 'govway-pdnd-entity-number': '' + n } }


@soap-get-resource-senza-conversation-id
Scenario: getResource senza identificativo dell'interazione

    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'getResource', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.errorType == 'AsyncInteractionInvalidRequest'
    * match faultStatus(r) == '400'
    * match faultDetail(r) == "The asynchronous interaction phase 'get_resource' requires the interaction identifier (Conversation-ID)"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)


@soap-get-resource-interazione-non-esistente
Scenario: getResource e confirmation per un'interazione non esistente

    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'getResource', conversationId: 'interazione-non-esistente', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.errorType == 'AsyncInteractionNotFound'
    * match faultStatus(r) == '400'
    * match faultDetail(r) == "Asynchronous interaction 'interazione-non-esistente' not found"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'confirmation', conversationId: 'interazione-non-esistente', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.errorType == 'AsyncInteractionNotFound'


@soap-get-resource-altra-fruizione
Scenario: getResource per un'interazione avviata con un'altra fruizione

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: '#(entity(1))' }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-UrlClientHeader"))', azione: 'getResource', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.errorType == 'AsyncInteractionNotFound'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "' not found"


@soap-get-resource-prima-della-callback
Scenario: getResource e confirmation prima della callback

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'getResource', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.errorType == 'AsyncInteractionInvalidState'
    * match faultStatus(r) == '409'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "': resource not yet available (callback not received), phase 'get_resource' not allowed"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'confirmation', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "': resource not yet available (callback not received), phase 'confirmation' not allowed"


@soap-confirmation-prima-di-get-resource
Scenario: confirmation dopo la callback ma prima di aver ottenuto la risposta: rifiutata dalla fruizione

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: '#(entity(1))' }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'confirmation', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.errorType == 'AsyncInteractionInvalidState'
    * match faultStatus(r) == '409'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "': response not yet obtained (get_resource), phase 'confirmation' not allowed"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)
    # la fase viene tracciata anche nella transazione rifiutata dalla fruizione
    * def tr = transazione(r.tid)
    * match tr.tipo_servizio_correlato == 'confirmation'
    # la richiesta non raggiunge l'erogatore: nessuna fase registrata su entrambi i lati
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'callback_invocation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'callback_invocation'

    # dopo l'ottenimento della risposta la conferma viene accettata
    * def get1 = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'getResource', conversationId: '#(id)' }
    * match transazione(get1.tid).tipo_servizio_correlato == 'get_resource'
    * def conf = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'confirmation', conversationId: '#(id)' }
    * match transazione(conf.tid).tipo_servizio_correlato == 'confirmation'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'confirmation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'confirmation'


@soap-callback-errori-richiesta
Scenario: callbackInvocation: identificativo dell'interazione e numero di entità mancanti, non validi o oltre il limite

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta

    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', headersExtra: '#(entity(1))', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.errorType == 'AsyncInteractionInvalidRequest'
    * match faultDetail(r) == "The asynchronous interaction phase 'callback_invocation' requires the interaction identifier (Conversation-ID)"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)

    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match faultDetail(r) == "Entity number not provided (HTTP header 'GovWay-PDND-Entity-Number')"

    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: '#(entity("abc"))', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match faultDetail(r) == "Entity number 'abc' provided (HTTP header 'GovWay-PDND-Entity-Number') is not valid: value must be an integer"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)

    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: '#(entity(0))', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match faultDetail(r) == "Entity number '0' provided (HTTP header 'GovWay-PDND-Entity-Number') is not valid: value must be greater than zero"

    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: '#(entity(21))', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match faultDetail(r) == "The entity number '21' exceeds the maximum number of entities per response (20) defined for the asynchronous interaction"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)

    # nessuna delle richieste errate ha modificato lo stato dell'interazione
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'

    # il limite è comprensivo
    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: '#(entity(20))' }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'


@soap-callback-interazione-non-esistente
Scenario: callbackInvocation per un'interazione non esistente o relativa ad un altro e-service

    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: 'interazione-non-esistente', headersExtra: '#(entity(1))', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.errorType == 'AsyncInteractionNotFound'
    * match faultDetail(r) == "Asynchronous interaction 'interazione-non-esistente' not found"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)

    # interazione avviata su un e-service diverso da quello correlato all'API di callback invocata
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapVerificaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(start.conversationIdRisposta)', headersExtra: '#(entity(1))', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.errorType == 'AsyncInteractionNotFound'


@soap-callback-ripetuta
Scenario: callbackInvocation ripetuta

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: '#(entity(1))' }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'
    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: '#(entity(1))', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.errorType == 'AsyncInteractionInvalidState'
    * match faultStatus(r) == '409'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "': callback already invoked"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)


@soap-callback-scaduta
Scenario: tempo massimo di risposta scaduto: callbackInvocation e getResource rifiutate

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapScadenze"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * sleep(4500)
    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapScadenzeCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: '#(entity(1))', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.errorType == 'AsyncInteractionExpired'
    * match faultStatus(r) == '400'
    * match faultDetail(r) contains "Maximum callback time (3s) expired at "
    * match faultDetail(r) contains "for the asynchronous interaction '" + id + "'"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapScadenze"))', azione: 'getResource', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.errorType == 'AsyncInteractionExpired'
    * match faultDetail(r) contains "Maximum callback time (3s) expired at "


@soap-risposta-scaduta
Scenario: durata di disponibilità del dato scaduta: getResource e confirmation rifiutate

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapScadenze"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapScadenzeCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: '#(entity(1))' }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'
    * def get1 = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapScadenze"))', azione: 'getResource', conversationId: '#(id)' }
    * match transazione(get1.tid).tipo_servizio_correlato == 'get_resource'
    * sleep(4500)
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapScadenze"))', azione: 'getResource', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.errorType == 'AsyncInteractionExpired'
    * match faultDetail(r) contains "Resource availability time (3s) expired at "
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapScadenze"))', azione: 'confirmation', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.errorType == 'AsyncInteractionExpired'
