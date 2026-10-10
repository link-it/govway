Feature: Scambi di dati asincroni PDND (SOAP) - registrazione delle fasi in base all'esito della risposta

# Il backend restituisce il codice HTTP indicato nell'header 'govway-testsuite-pdnd-async-status' (con un codice >= 500 un SOAP Fault).
# Una fase viene registrata solamente se il codice HTTP rientra tra quelli configurati (default 200-299):
# in caso contrario l'interazione resta nello stato precedente e la fase può essere ripetuta.

Background:
    * def utils = 'classpath:test/soap/pdnd-async/pdnd-async-soap-utils.feature'
    * def interazione = read('classpath:utils/pdnd_async_interazione.js')
    * def transazione = read('classpath:utils/pdnd_async_transazione.js')
    * def tokenInfo = read('classpath:utils/pdnd_async_token_info.js')
    * def NS_C = 'http://govway.org/pdnd/async/callback'
    * def fruizione = function(servizio){ return 'out/DemoSoggettoFruitore/DemoSoggettoErogatore/' + servizio + '/v1' }
    * def fruizioneCallback = function(servizio){ return 'out/DemoSoggettoErogatore/DemoSoggettoFruitore/' + servizio + '/v1' }
    * def faultDetail = function(r){ return karate.xmlPath(r.response, '/Envelope/Body/Fault/detail/problem/detail') }
    * def faultTitle = function(r){ return karate.xmlPath(r.response, '/Envelope/Body/Fault/detail/problem/title') }


@soap-backend-errore-start
Scenario: startInteraction con SOAP Fault del backend dell'erogatore: interazione non registrata da entrambi i lati

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'startInteraction', headersExtra: { 'govway-testsuite-pdnd-async-status': '500' }, statusAtteso: 500 }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response /Envelope/Body/Fault/faultstring == 'Errore simulato dal backend di test'
    * def ti = tokenInfo(start.tid)
    * def id = ti.accessToken.interactionId
    * match id == '#notnull'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore == null
    * def fruitore = interazione(id, 'delegata')
    * match fruitore == null


@soap-backend-errore-fasi-ripetibili
Scenario: callbackInvocation, getResource e confirmation con SOAP Fault del backend: la fase non viene registrata e può essere ripetuta

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta

    # callback: backend del fruitore in errore
    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '1', 'govway-testsuite-pdnd-async-status': '500' }, statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response /Envelope/Body/Fault/faultstring == 'Errore simulato dal backend di test'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'start_interaction'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '1' } }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'callback_invocation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'callback_invocation'

    # getResource: backend dell'erogatore in errore
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'getResource', conversationId: '#(id)', headersExtra: { 'govway-testsuite-pdnd-async-status': '500' }, statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.numero_get_resource == '0'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.numero_get_resource == '0'
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'getResource', conversationId: '#(id)' }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.numero_get_resource == '1'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.numero_get_resource == '1'

    # confirmation: backend dell'erogatore in errore
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'confirmation', conversationId: '#(id)', headersExtra: { 'govway-testsuite-pdnd-async-status': '500' }, statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.data_confirmation == null
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.data_confirmation == null
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'confirmation', conversationId: '#(id)' }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'confirmation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'confirmation'


@soap-esito-ridefinito
Scenario: codici HTTP di esito positivo ridefiniti nella fruizione e nell'erogazione (solo 200)

    # il backend risponde 202: non rientra tra i codici configurati, la start non viene registrata
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-EsitoRidefinito"))', azione: 'startInteraction', headersExtra: { 'govway-testsuite-pdnd-async-status': '202' }, statusAtteso: 202 }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def ti = tokenInfo(start.tid)
    * def id = ti.accessToken.interactionId
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore == null
    * def fruitore = interazione(id, 'delegata')
    * match fruitore == null

    # il backend risponde 200: la start viene registrata
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-EsitoRidefinito"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'start_interaction'


@soap-esito-ridefinito-solo-fruizione
Scenario: codici HTTP di esito positivo ridefiniti solamente nelle fruizioni (solo 200), erogazioni con il default 200-299

    # startInteraction: il backend dell'erogatore risponde 202, registrata solamente dall'erogatore
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEsito"))', azione: 'startInteraction', headersExtra: { 'govway-testsuite-pdnd-async-status': '202' }, statusAtteso: 202 }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def ti = tokenInfo(start.tid)
    * def id = ti.accessToken.interactionId
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore == null
    # per il fruitore l'interazione non esiste
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEsito"))', azione: 'getResource', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.errorType == 'AsyncInteractionNotFound'

    # startInteraction con risposta 200: registrata da entrambi i lati
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEsito"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'start_interaction'

    # callbackInvocation: il backend del fruitore risponde 202, registrata solamente dal fruitore (erogazione della callback con il default)
    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapEsitoCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '1', 'govway-testsuite-pdnd-async-status': '202' }, statusAtteso: 202 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'callback_invocation'
    # la callback ripetuta viene rifiutata dal fruitore, che l'ha già registrata
    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapEsitoCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '1' }, statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match faultTitle(r) == 'AsyncInteractionInvalidState'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "': callback already invoked"

    # nuova interazione con startInteraction e callbackInvocation registrate da entrambi i lati
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEsito"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapEsitoCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '1' } }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'callback_invocation'

    # getResource: il backend dell'erogatore risponde 202, registrata solamente dall'erogatore
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEsito"))', azione: 'getResource', conversationId: '#(id)', headersExtra: { 'govway-testsuite-pdnd-async-status': '202' }, statusAtteso: 202 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.numero_get_resource == '1'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.numero_get_resource == '0'
    # per il fruitore la risposta non è ancora stata ottenuta: la conferma viene rifiutata
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEsito"))', azione: 'confirmation', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.errorType == 'AsyncInteractionInvalidState'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "': response not yet obtained (get_resource), phase 'confirmation' not allowed"
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEsito"))', azione: 'getResource', conversationId: '#(id)' }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.numero_get_resource == '2'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.numero_get_resource == '1'

    # confirmation: il backend dell'erogatore risponde 202, registrata solamente dall'erogatore
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEsito"))', azione: 'confirmation', conversationId: '#(id)', headersExtra: { 'govway-testsuite-pdnd-async-status': '202' }, statusAtteso: 202 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'confirmation'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.data_confirmation == null
    # la conferma ripetuta supera il fruitore ma viene rifiutata dall'erogatore, che l'ha già registrata
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEsito"))', azione: 'confirmation', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "' already confirmed: phase 'confirmation' not allowed"
