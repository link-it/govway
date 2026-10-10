Feature: Scambi di dati asincroni PDND (SOAP) - flussi completi

# Le fasi sono associate alle azioni del port type; gli errori vengono restituiti come SOAP Fault (HTTP 500)
# con il problem detail (RFC 7807) nell'elemento 'detail'.

Background:
    * def utils = 'classpath:test/soap/pdnd-async/pdnd-async-soap-utils.feature'
    * def interazione = read('classpath:utils/pdnd_async_interazione.js')
    * def transazione = read('classpath:utils/pdnd_async_transazione.js')
    * def tokenInfo = read('classpath:utils/pdnd_async_token_info.js')
    # differenza in secondi tra due date della tabella PDND_INTERAZIONI_ASYNC
    * def secondi = function(da, a){ var T = Java.type('java.sql.Timestamp'); return Math.round((T.valueOf(a).getTime() - T.valueOf(da).getTime()) / 1000) }
    * def diagnostici = read('classpath:utils/pdnd_async_diagnostici.js')
    # errori rilevati dalla fruizione durante l'imbustamento della richiesta (verifica dello stato dell'interazione)
    * def DIAG_FRUIZIONE = '[imbustatore.after-sec.imbustamento]: '
    * def NS_C = 'http://govway.org/pdnd/async/callback'
    * def fruizione = function(servizio){ return 'out/DemoSoggettoFruitore/DemoSoggettoErogatore/' + servizio + '/v1' }
    * def fruizioneCallback = function(servizio){ return 'out/DemoSoggettoErogatore/DemoSoggettoFruitore/' + servizio + '/v1' }
    * def faultDetail = function(r){ return karate.xmlPath(r.response, '/Envelope/Body/Fault/detail/problem/detail') }


@soap-compatta-flusso-completo
Scenario: API SOAP compatta: start_interaction, callback_invocation (fase implicita), get_resource ripetuta, confirmation

    # start_interaction
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * match id == '#notnull'
    * match start.response /Envelope/Body/startInteractionResponse/conversationId == id
    * def urlCallback = karate.xmlPath(start.response, '/Envelope/Body/startInteractionResponse/urlCallback')
    # URL di invocazione dell'erogazione SOAP dell'API di callback
    * match urlCallback contains '/soap/in/DemoSoggettoFruitore/PDNDAsyncSoapCompattaCallback/v1'
    * def tr = transazione(start.tid)
    * match tr.id_collaborazione == id
    * match tr.tipo_servizio_correlato == 'start_interaction'
    * match tr.nome_servizio_correlato == urlCallback
    * def ti = tokenInfo(start.tid)
    * match ti.assertion.scope == 'start_interaction'
    * match ti.assertion.urlCallback == urlCallback
    * match ti.assertion.purposeId == 'pdnd-async-purpose'
    * match ti.accessToken.interactionId == id
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'start_interaction'
    * match fruitore.servizio == 'PDNDAsyncSoapCompatta'
    * match fruitore.url_callback == urlCallback
    * match fruitore.consumer_id == 'pdnd-async-consumer'
    * match fruitore.id_transazione_start == start.tid
    * match fruitore.tempo_max_callback == '300'
    * match fruitore.tempo_disponibilita == '300'
    * match fruitore.conferma_richiesta == '1'
    * match fruitore.numero_get_resource == '0'
    * match secondi(fruitore.data_start, fruitore.data_scadenza) == 300
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * match erogatore.url_callback == urlCallback
    * match erogatore.limite_entita == '20'
    * match erogatore.id_transazione_start == '#notnull'
    * match secondi(erogatore.data_start, erogatore.data_scadenza) == 300

    # callback_invocation: unica azione dell'API di callback, fase implicita
    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '2' } }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'
    * match cb.response /Envelope/Body/callbackInvocationResponse/conversationId == id
    # la PDND non riporta entityNumber nel voucher: non viene inoltrato al backend del fruitore
    * def enCheck = karate.xmlPath(cb.response, '/Envelope/Body/callbackInvocationResponse/entityNumber')
    * match enCheck == '#? _ == null || _ == ""'
    * def ti = tokenInfo(cb.tid)
    * match ti.assertion.scope == 'callback_invocation'
    * match ti.assertion.interactionId == id
    * match ti.assertion.entityNumber == 2
    * def tr = transazione(cb.tid)
    * match tr.tipo_servizio_correlato == 'callback_invocation'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'callback_invocation'
    * match fruitore.entity_number == null
    * match fruitore.servizio_callback == 'PDNDAsyncSoapCompattaCallback'
    * match secondi(fruitore.data_callback, fruitore.data_scadenza) == 300
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'callback_invocation'
    * match erogatore.entity_number == '2'
    * match erogatore.servizio_callback == 'PDNDAsyncSoapCompattaCallback'
    * match secondi(erogatore.data_callback, erogatore.data_scadenza) == 300

    # get_resource ripetibile
    * def get1 = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'getResource', conversationId: '#(id)' }
    * match transazione(get1.tid).tipo_servizio_correlato == 'get_resource'
    * match get1.response /Envelope/Body/getResourceResponse/conversationId == id
    * def get2 = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'getResource', conversationId: '#(id)' }
    * match transazione(get2.tid).tipo_servizio_correlato == 'get_resource'
    * def ti = tokenInfo(get1.tid)
    * match ti.assertion.scope == 'get_resource'
    * match ti.assertion.purposeId == 'pdnd-async-purpose'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.numero_get_resource == '2'
    * match fruitore.data_get_resource == '#notnull'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.numero_get_resource == '2'
    * match erogatore.data_get_resource == '#notnull'

    # confirmation
    * def conf = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'confirmation', conversationId: '#(id)' }
    * match transazione(conf.tid).tipo_servizio_correlato == 'confirmation'
    * match conf.response /Envelope/Body/confirmationResponse/conversationId == id
    * def ti = tokenInfo(conf.tid)
    * match ti.assertion.scope == 'confirmation'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'confirmation'
    * match fruitore.data_scadenza == fruitore.data_confirmation
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'confirmation'
    * match erogatore.data_scadenza == erogatore.data_confirmation

    # dopo la conferma: errori restituiti come SOAP Fault
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'getResource', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.errorType == 'AsyncInteractionInvalidState'
    * match r.response /Envelope/Body/Fault/faultcode contains 'AsyncInteractionInvalidState'
    * match r.response /Envelope/Body/Fault/detail/problem/status == '409'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "' already confirmed: phase 'get_resource' not allowed"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'confirmation', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "' already confirmed: phase 'confirmation' not allowed"
    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '2' }, statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match faultDetail(r) == "Asynchronous interaction '" + id + "' already confirmed: phase 'callback_invocation' not allowed"


@soap-estesa-flusso-completo
Scenario: API SOAP estesa: fase callback esplicita, entityNumber nel voucher inoltrato al backend, operazioni senza fase

    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEstesa"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * match start.response /Envelope/Body/startInteractionResponse/urlCallback contains '/DemoSoggettoFruitore/PDNDAsyncSoapEstesaCallback/v1'

    # la policy della fruizione della callback utilizza un authorization server che riporta entityNumber nel voucher
    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapEstesaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '5' } }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'
    * match cb.response /Envelope/Body/callbackInvocationResponse/entityNumber == '5'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'callback_invocation'
    * match fruitore.entity_number == '5'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.entity_number == '5'

    # azione dell'API di callback senza fase, con l'identificativo dell'interazione: rifiutata prima dell'inoltro
    * def r = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapEstesaCallback"))', azione: 'notifyError', ns: '#(NS_C)', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == null
    * match r.errorType == 'AsyncInteractionInvalidRequest'
    * match faultDetail(r) == "The invoked operation 'notifyError' is not associated with any phase of the asynchronous interaction, but the identifier of the existing asynchronous interaction '" + id + "' has been provided"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)

    * def get1 = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEstesa"))', azione: 'getResource', conversationId: '#(id)' }
    * match transazione(get1.tid).tipo_servizio_correlato == 'get_resource'
    * match get1.response /Envelope/Body/getResourceResponse/conversationId == id
    * def conf = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEstesa"))', azione: 'confirmation', conversationId: '#(id)' }
    * match transazione(conf.tid).tipo_servizio_correlato == 'confirmation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'confirmation'

    # azioni senza fase del port type dell'e-service: invocabili senza identificativo, o con quello di un'interazione non esistente
    * def cat = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEstesa"))', azione: 'getCatalogItem' }
    * match transazione(cat.tid).tipo_servizio_correlato == null
    * def convCheck = karate.xmlPath(cat.response, '/Envelope/Body/getCatalogItemResponse/conversationId')
    * match convCheck == '#? _ == null || _ == ""'
    * def h = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEstesa"))', azione: 'health', conversationId: 'interazione-non-esistente' }
    * match transazione(h.tid).tipo_servizio_correlato == null
    # (le azioni senza fase dell'API di callback non sono invocabili senza interazione: il connettore della fruizione
    # utilizza la URL di callback dell'interazione, ${context:pdndAsyncUrlCallback})

    # azione senza fase con l'identificativo di un'interazione esistente
    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapEstesa"))', azione: 'health', conversationId: '#(id)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == null
    * match r.errorType == 'AsyncInteractionInvalidRequest'
    * match faultDetail(r) == "The invoked operation 'health' is not associated with any phase of the asynchronous interaction, but the identifier of the existing asynchronous interaction '" + id + "' has been provided"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + faultDetail(r)


@soap-purpose-id
Scenario: invio del purposeId nelle fasi get_resource e confirmation (default abilitato, ridefinito nella fruizione come disabilitato o abilitato)

    # default: inviato
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '1' } }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'
    * def get1 = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta"))', azione: 'getResource', conversationId: '#(id)' }
    * match transazione(get1.tid).tipo_servizio_correlato == 'get_resource'
    * def tiCheck1 = tokenInfo(get1.tid)
    * match tiCheck1.assertion.purposeId == 'pdnd-async-purpose'

    # fruizione con invio disabilitato: inviato solo nella start_interaction
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-PurposeIdDisabilitato"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * def tiCheck2 = tokenInfo(start.tid)
    * match tiCheck2.assertion.purposeId == 'pdnd-async-purpose'
    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '1' } }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'
    * def get1 = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-PurposeIdDisabilitato"))', azione: 'getResource', conversationId: '#(id)' }
    * match transazione(get1.tid).tipo_servizio_correlato == 'get_resource'
    * def tiCheck3 = tokenInfo(get1.tid)
    * match tiCheck3.assertion.purposeId == '#notpresent'
    * def conf = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-PurposeIdDisabilitato"))', azione: 'confirmation', conversationId: '#(id)' }
    * match transazione(conf.tid).tipo_servizio_correlato == 'confirmation'
    * def tiCheck4 = tokenInfo(conf.tid)
    * match tiCheck4.assertion.purposeId == '#notpresent'

    # fruizione con invio abilitato esplicitamente
    * def start = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-PurposeIdAbilitato"))', azione: 'startInteraction' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationIdRisposta
    * def cb = call read(utils + '@invoca') { path: '#(fruizioneCallback("PDNDAsyncSoapCompattaCallback"))', azione: 'callbackInvocation', ns: '#(NS_C)', conversationId: '#(id)', headersExtra: { 'govway-pdnd-entity-number': '1' } }
    * match transazione(cb.tid).tipo_servizio_correlato == 'callback_invocation'
    * def get1 = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-PurposeIdAbilitato"))', azione: 'getResource', conversationId: '#(id)' }
    * match transazione(get1.tid).tipo_servizio_correlato == 'get_resource'
    * def tiCheck5 = tokenInfo(get1.tid)
    * match tiCheck5.assertion.purposeId == 'pdnd-async-purpose'
    * def conf = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapCompatta-PurposeIdAbilitato"))', azione: 'confirmation', conversationId: '#(id)' }
    * match transazione(conf.tid).tipo_servizio_correlato == 'confirmation'
    * def tiCheck6 = tokenInfo(conf.tid)
    * match tiCheck6.assertion.purposeId == 'pdnd-async-purpose'
