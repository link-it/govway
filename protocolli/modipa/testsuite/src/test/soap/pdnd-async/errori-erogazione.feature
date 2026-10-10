Feature: Scambi di dati asincroni PDND (SOAP) - errori rilevati lato erogazione (invocazioni dirette con voucher forzati)

# Le erogazioni vengono invocate direttamente, senza passare dalla fruizione, con voucher emessi dalla risorsa '/voucher'
# dell'authorization server di test, che consente di indicare (o omettere) i claim dello scambio asincrono.

Background:
    * def utils = 'classpath:test/soap/pdnd-async/pdnd-async-soap-utils.feature'
    * def restUtils = 'classpath:test/rest/pdnd-async/pdnd-async-utils.feature'
    * def interazione = read('classpath:utils/pdnd_async_interazione.js')
    * def transazione = read('classpath:utils/pdnd_async_transazione.js')
    * def diagnostici = read('classpath:utils/pdnd_async_diagnostici.js')
    * def sleep = function(ms){ java.lang.Thread.sleep(ms) }
    * def uuid = function(){ return '' + java.util.UUID.randomUUID() }
    * def voucher = function(claims){ var r = karate.call(restUtils + '@voucher', { claims: claims }); return r.voucher; }
    # errori rilevati dall'erogazione durante la validazione semantica della richiesta (eccezione di validazione GOVWAY-354)
    * def DIAG_EROGAZIONE = '[GOVWAY-354] - EccezioneValidazioneProtocollo: '
    * def NS_C = 'http://govway.org/pdnd/async/callback'
    * def URL_CALLBACK = 'http://host-callback/callback'
    * def fruizione = function(servizio){ return 'out/DemoSoggettoFruitore/DemoSoggettoErogatore/' + servizio + '/v1' }
    * def fruizioneCallback = function(servizio){ return 'out/DemoSoggettoErogatore/DemoSoggettoFruitore/' + servizio + '/v1' }
    * def faultDetail = function(r){ return karate.xmlPath(r.response, '/Envelope/Body/Fault/detail/problem/detail') }
    * def faultStatus = function(r){ return karate.xmlPath(r.response, '/Envelope/Body/Fault/detail/problem/status') }


@soap-erogatore-start-errori-voucher
Scenario: startInteraction: voucher privo dei claim previsti o con scope errato

    # scope non coerente con la fase dell'azione invocata
    * def v = voucher({ scope: 'get_resource', interactionId: uuid(), urlCallback: URL_CALLBACK })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'startInteraction', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.errorType == 'AsyncInteractionInvalidRequest'
    * match faultStatus(r) == '400'
    * match faultDetail(r) == "The PDND voucher does not contain the scope 'start_interaction' expected for the invoked operation"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + faultDetail(r)

    # interactionId mancante
    * def v = voucher({ scope: 'start_interaction', urlCallback: URL_CALLBACK })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'startInteraction', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match faultDetail(r) == "Claim 'interactionId' not found in the PDND voucher"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + faultDetail(r)

    # urlCallback mancante
    * def v = voucher({ scope: 'start_interaction', interactionId: uuid() })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'startInteraction', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match faultDetail(r) == "Claim 'urlCallback' not found in the PDND voucher"


@soap-erogatore-start-ripetuta
Scenario: startInteraction ripetuta con lo stesso interactionId

    * def id = uuid()
    * def v = voucher({ scope: 'start_interaction', interactionId: id, urlCallback: URL_CALLBACK })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'startInteraction', voucher: '#(v)' }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.response /Envelope/Body/startInteractionResponse/conversationId == id
    * match r.response /Envelope/Body/startInteractionResponse/urlCallback == URL_CALLBACK
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * match erogatore.url_callback == URL_CALLBACK

    * def v = voucher({ scope: 'start_interaction', interactionId: id, urlCallback: URL_CALLBACK })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'startInteraction', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.errorType == 'AsyncInteractionInvalidState'
    * match faultStatus(r) == '409'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "' already started"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + faultDetail(r)


@soap-erogatore-risorsa-errori
Scenario: getResource e confirmation: callback non ricevuta, confirmation prima di getResource, interazione inesistente, scope errato, altro consumer, altro e-service, già confermata

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta

    # callback non ancora ricevuta
    * def v = voucher({ scope: 'get_resource', interactionId: id })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'getResource', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.errorType == 'AsyncInteractionInvalidState'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "': resource not yet available (callback not received), phase 'get_resource' not allowed"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + faultDetail(r)

    # confirmation prima della callback
    * def v = voucher({ scope: 'confirmation', interactionId: id })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'confirmation', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.errorType == 'AsyncInteractionInvalidState'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "': resource not yet available (callback not received), phase 'confirmation' not allowed"

    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '1' } }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'

    # confirmation dopo la callback ma prima dell'ottenimento della risposta
    * def v = voucher({ scope: 'confirmation', interactionId: id })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'confirmation', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.errorType == 'AsyncInteractionInvalidState'
    * match faultStatus(r) == '409'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "': response not yet obtained (get_resource), phase 'confirmation' not allowed"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + faultDetail(r)
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'callback_invocation'

    # interazione non esistente
    * def v = voucher({ scope: 'get_resource', interactionId: 'interazione-non-esistente' })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'getResource', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.errorType == 'AsyncInteractionNotFound'
    * match faultDetail(r) == "Asynchronous interaction 'interazione-non-esistente' not found"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + faultDetail(r)

    # scope errato
    * def v = voucher({ scope: 'confirmation', interactionId: id })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'getResource', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match faultDetail(r) == "The PDND voucher does not contain the scope 'get_resource' expected for the invoked operation"

    # interazione di un altro consumer
    * def v = voucher({ scope: 'get_resource', interactionId: id, consumerId: 'altro-consumer' })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'getResource', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.errorType == 'AsyncInteractionNotFound'

    # interazione di un altro e-service (erogazione di un'altra API)
    * def v = voucher({ scope: 'get_resource', interactionId: id })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapVerifica', azione: 'getResource', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.errorType == 'AsyncInteractionNotFound'

    # voucher corretto
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'getResource', voucher: '#(v)' }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response /Envelope/Body/getResourceResponse/conversationId == id
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.numero_get_resource == '1'

    # conferma e successive richieste rifiutate
    * def v = voucher({ scope: 'confirmation', interactionId: id })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'confirmation', voucher: '#(v)' }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * def v = voucher({ scope: 'confirmation', interactionId: id })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'confirmation', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.errorType == 'AsyncInteractionInvalidState'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "' already confirmed: phase 'confirmation' not allowed"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + faultDetail(r)


@soap-fruitore-erogazione-callback-errori
Scenario: erogazione dell'API di callback (GovWay del fruitore) invocata direttamente: interazione inesistente, scope errato, callback ripetuta

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta

    # interazione non esistente
    * def v = voucher({ scope: 'callback_invocation', interactionId: 'interazione-non-esistente' })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoFruitore', servizio: 'PDNDAsyncSoapCompattaCallback', azione: 'callbackInvocation', ns: '#(NS_C)', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.errorType == 'AsyncInteractionNotFound'
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + faultDetail(r)

    # scope errato
    * def v = voucher({ scope: 'start_interaction', interactionId: id })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoFruitore', servizio: 'PDNDAsyncSoapCompattaCallback', azione: 'callbackInvocation', ns: '#(NS_C)', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match faultDetail(r) == "The PDND voucher does not contain the scope 'callback_invocation' expected for the invoked operation"

    # interazione relativa ad un e-service non correlato all'API di callback
    * def v = voucher({ scope: 'callback_invocation', interactionId: id })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoFruitore', servizio: 'PDNDAsyncSoapVerificaCallback', azione: 'callbackInvocation', ns: '#(NS_C)', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.errorType == 'AsyncInteractionNotFound'

    # callback valida, con entityNumber nel voucher inoltrato al backend
    * def v = voucher({ scope: 'callback_invocation', interactionId: id, entityNumber: '7' })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoFruitore', servizio: 'PDNDAsyncSoapCompattaCallback', azione: 'callbackInvocation', ns: '#(NS_C)', voucher: '#(v)' }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response /Envelope/Body/callbackInvocationResponse/conversationId == id
    * match r.response /Envelope/Body/callbackInvocationResponse/entityNumber == '7'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'callback_invocation'
    * match fruitore.entity_number == '7'

    # callback ripetuta
    * def v = voucher({ scope: 'callback_invocation', interactionId: id })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoFruitore', servizio: 'PDNDAsyncSoapCompattaCallback', azione: 'callbackInvocation', ns: '#(NS_C)', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.errorType == 'AsyncInteractionInvalidState'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "': callback already invoked"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + faultDetail(r)


@soap-scadenze-lato-erogazione
Scenario: scadenze rilevate lato erogazione: callback scaduta (GovWay del fruitore) e risorsa scaduta (GovWay dell'erogatore)

    # callback scaduta: rifiutata dall'erogazione dell'API di callback
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapScadenze"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * sleep(4500)
    * def v = voucher({ scope: 'callback_invocation', interactionId: id })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoFruitore', servizio: 'PDNDAsyncSoapScadenzeCallback', azione: 'callbackInvocation', ns: '#(NS_C)', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.errorType == 'AsyncInteractionExpired'
    * match faultDetail(r) contains "Maximum callback time (3s) expired at "
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + faultDetail(r)
    # getResource rifiutata dall'erogazione dell'e-service (callback non ricevuta e tempo scaduto)
    * def v = voucher({ scope: 'get_resource', interactionId: id })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapScadenze', azione: 'getResource', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.errorType == 'AsyncInteractionExpired'
    * match faultDetail(r) contains "Maximum callback time (3s) expired at "

    # risposta scaduta
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapScadenze"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapScadenzeCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '1' } }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'
    * sleep(4500)
    * def v = voucher({ scope: 'get_resource', interactionId: id })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapScadenze', azione: 'getResource', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.errorType == 'AsyncInteractionExpired'
    * match faultDetail(r) contains "Resource availability time (3s) expired at "
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + faultDetail(r)


@soap-erogazione-senza-voucher
Scenario: erogazione invocata senza voucher: rifiutata dalla validazione del token

    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapCompatta', azione: 'startInteraction', voucher: null, statusAtteso: 500 }
    # richiesta respinta dalla validazione del token, prima della gestione dello scambio asincrono: nessuna fase
    * match transazione(r.tid).tipo_servizio_correlato == null
    * match r.errorType == 'TokenAuthenticationRequired'
