Feature: Scambi di dati asincroni PDND (REST) - flussi completi

Background:
    * def utils = 'classpath:test/rest/pdnd-async/pdnd-async-utils.feature'
    * def interazione = read('classpath:utils/pdnd_async_interazione.js')
    * def transazione = read('classpath:utils/pdnd_async_transazione.js')
    * def tokenInfo = read('classpath:utils/pdnd_async_token_info.js')
    # differenza in secondi tra due date della tabella PDND_INTERAZIONI_ASYNC
    * def secondi = function(da, a){ var T = Java.type('java.sql.Timestamp'); return Math.round((T.valueOf(a).getTime() - T.valueOf(da).getTime()) / 1000) }


@compatta-flusso-completo
Scenario: API compatta: start_interaction, callback_invocation (fase implicita), get_resource ripetuta, confirmation

    # start_interaction
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * match id == '#notnull'
    * match start.response.outcome == 'ACCEPTED'
    * match start.response.received.conversationId == id
    * match start.response.received.urlCallback contains '/DemoSoggettoFruitore/PDNDAsyncRestCompattaCallback/v1'
    * def urlCallback = start.response.received.urlCallback

    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'start_interaction'
    * match fruitore.fruitore == 'DemoSoggettoFruitore'
    * match fruitore.servizio == 'PDNDAsyncRestCompatta'
    * match fruitore.url_callback == urlCallback
    * match fruitore.purpose_id == 'pdnd-async-purpose'
    * match fruitore.consumer_id == 'pdnd-async-consumer'
    * match fruitore.client_id == 'pdnd-async-client'
    * match fruitore.data_scadenza == '#notnull'
    * match fruitore.id_transazione_start == start.tid
    * match fruitore.tempo_max_callback == '300'
    * match fruitore.tempo_disponibilita == '300'
    * match fruitore.conferma_richiesta == '1'
    * match fruitore.limite_entita == '20'
    * match fruitore.numero_get_resource == '0'
    * match fruitore.data_callback == null
    * match fruitore.data_get_resource == null
    * match fruitore.data_confirmation == null
    # scadenza: inizio dell'interazione + tempo massimo di risposta
    * match secondi(fruitore.data_start, fruitore.data_scadenza) == 300
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * match erogatore.erogatore == 'DemoSoggettoErogatore'
    * match erogatore.url_callback == urlCallback
    * match erogatore.consumer_id == 'pdnd-async-consumer'
    * match erogatore.purpose_id == 'pdnd-async-purpose'
    * match erogatore.client_id == 'pdnd-async-client'
    * match erogatore.limite_entita == '20'
    * match erogatore.servizio == 'PDNDAsyncRestCompatta'
    * match erogatore.id_transazione_start == '#notnull'
    * match erogatore.id_transazione_start != start.tid
    * match erogatore.tempo_max_callback == '300'
    * match erogatore.tempo_disponibilita == '300'
    * match erogatore.conferma_richiesta == '1'
    * match erogatore.data_callback == null
    * match secondi(erogatore.data_start, erogatore.data_scadenza) == 300

    * def tr = transazione(start.tid)
    * match tr.id_collaborazione == id
    * match tr.tipo_servizio_correlato == 'start_interaction'
    * match tr.nome_servizio_correlato == urlCallback
    * def ti = tokenInfo(start.tid)
    * match ti.assertion.scope == 'start_interaction'
    * match ti.assertion.urlCallback == urlCallback
    * match ti.assertion.purposeId == 'pdnd-async-purpose'
    * match ti.accessToken.interactionId == id
    * match ti.accessToken.scope == 'start_interaction'

    # callback_invocation
    * def callback = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)', entityNumber: 4 }
    * match transazione(callback.tid).tipo_servizio_correlato == 'callback_invocation'
    * match callback.response.outcome == 'ACK'
    * match callback.response.received.conversationId == id
    # la PDND non riporta entityNumber nel voucher: non viene inoltrato al backend del fruitore
    * match callback.response.received.entityNumber == null
    * def ti = tokenInfo(callback.tid)
    * match ti.assertion.scope == 'callback_invocation'
    * match ti.assertion.interactionId == id
    * match ti.assertion.entityNumber == 4
    * match ti.assertion.purposeId == '#notpresent'
    * def tr = transazione(callback.tid)
    * match tr.id_collaborazione == id
    * match tr.tipo_servizio_correlato == 'callback_invocation'
    * match tr.nome_servizio_correlato == null

    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'callback_invocation'
    * match fruitore.data_callback == '#notnull'
    * match fruitore.entity_number == null
    * match fruitore.servizio_callback == 'PDNDAsyncRestCompattaCallback'
    # scadenza: invocazione della callback + durata di disponibilità del dato
    * match secondi(fruitore.data_callback, fruitore.data_scadenza) == 300
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'callback_invocation'
    * match erogatore.entity_number == '4'
    * match erogatore.servizio_callback == 'PDNDAsyncRestCompattaCallback'
    * match erogatore.data_callback == '#notnull'
    * match secondi(erogatore.data_callback, erogatore.data_scadenza) == 300

    # get_resource (ripetibile; voucher riutilizzato dalla cache)
    * def get1 = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)' }
    * match transazione(get1.tid).tipo_servizio_correlato == 'get_resource'
    * match get1.response.total == 2
    * match get1.response.received.conversationId == id
    * match get1.conversationIdRisposta == id
    * def get2 = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)' }
    * match transazione(get2.tid).tipo_servizio_correlato == 'get_resource'
    * def ti1 = tokenInfo(get1.tid)
    * def ti2 = tokenInfo(get2.tid)
    * match ti1.assertion.scope == 'get_resource'
    * match ti1.assertion.interactionId == id
    * match ti1.assertion.purposeId == 'pdnd-async-purpose'
    * match ti2.accessToken.jti == ti1.accessToken.jti
    * def tr = transazione(get1.tid)
    * match tr.tipo_servizio_correlato == 'get_resource'

    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'get_resource'
    * match fruitore.numero_get_resource == '2'
    * match fruitore.data_get_resource == '#notnull'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.numero_get_resource == '2'
    * match erogatore.data_get_resource == '#notnull'

    # confirmation (voucher non riutilizzato)
    * def conf = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)' }
    * match transazione(conf.tid).tipo_servizio_correlato == 'confirmation'
    * match conf.response.outcome == 'CONFIRMED'
    * match conf.response.received.conversationId == id
    * def ti = tokenInfo(conf.tid)
    * match ti.assertion.scope == 'confirmation'
    * match ti.assertion.purposeId == 'pdnd-async-purpose'
    * match ti.accessToken.jti != ti1.accessToken.jti
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'confirmation'
    * match fruitore.data_confirmation == '#notnull'
    # scadenza e ultimo aggiornamento coincidono con la conferma: lo svecchiamento elimina l'interazione al termine della conservazione
    * match fruitore.data_scadenza == fruitore.data_confirmation
    * match fruitore.data_aggiornamento == fruitore.data_confirmation
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'confirmation'
    * match erogatore.data_confirmation == '#notnull'
    * match erogatore.data_scadenza == erogatore.data_confirmation

    # dopo la conferma non sono più ammesse get_resource e confirmation
    * def get3 = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)', statusAtteso: 409 }
    * match transazione(get3.tid).tipo_servizio_correlato == 'get_resource'
    * match get3.response.title == 'AsyncInteractionInvalidState'
    * match get3.response.detail == "Asynchronous interaction '" + id + "' already confirmed: phase 'get_resource' not allowed"
    * def conf2 = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)', statusAtteso: 409 }
    * match transazione(conf2.tid).tipo_servizio_correlato == 'confirmation'
    * match conf2.response.title == 'AsyncInteractionInvalidState'
    * match conf2.response.detail == "Asynchronous interaction '" + id + "' already confirmed: phase 'confirmation' not allowed"
    # la callback non è più invocabile
    * def callback2 = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)', statusAtteso: 409 }
    * match transazione(callback2.tid).tipo_servizio_correlato == 'callback_invocation'
    * match callback2.response.title == 'AsyncInteractionInvalidState'
    * match callback2.response.detail == "Asynchronous interaction '" + id + "' already confirmed: phase 'callback_invocation' not allowed"


@estesa-flusso-completo
Scenario: API estesa: fase callback esplicita, entityNumber nel voucher inoltrato al backend, operazioni sincrone

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestEstesa' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * match id == '#notnull'
    * match start.response.received.urlCallback contains '/DemoSoggettoFruitore/PDNDAsyncRestEstesaCallback/v1'

    # numero di entità fornito tramite parametro della URL; la policy della fruizione utilizza un authorization server che lo riporta nel voucher
    * def callback = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestEstesaCallback', conversationId: '#(id)', entityNumber: 3, entityNumberQuery: true }
    * match transazione(callback.tid).tipo_servizio_correlato == 'callback_invocation'
    * match callback.response.received.conversationId == id
    * match callback.response.received.entityNumber == '3'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'callback_invocation'
    * match fruitore.entity_number == '3'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.entity_number == '3'

    * def get1 = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestEstesa', conversationId: '#(id)' }
    * match transazione(get1.tid).tipo_servizio_correlato == 'get_resource'
    * match get1.response.received.conversationId == id

    # la conferma non è prevista dall'API: la risorsa non è associata ad alcuna fase
    * def conf = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestEstesa', conversationId: '#(id)', statusAtteso: 400 }
    * match transazione(conf.tid).tipo_servizio_correlato == null
    * match conf.response.title == 'AsyncInteractionInvalidRequest'
    * match conf.response.detail == "The invoked operation 'confirmationSenzaFase' is not associated with any phase of the asynchronous interaction, but the identifier of the existing asynchronous interaction '" + id + "' has been provided"

    # operazioni sincrone dell'API
    Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/PDNDAsyncRestEstesa/v1/catalog'
    And configure headers = {}
    When method get
    Then status 200
    And match response.items == ['A01', 'A02']
    And match response.received.conversationId == null

    # identificativo di un'interazione non esistente: l'operazione sincrona viene comunque eseguita
    Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/PDNDAsyncRestEstesa/v1/health'
    And configure headers = { 'govway-conversation-id': 'interazione-non-esistente' }
    When method get
    Then status 200

    # operazione sincrona dell'API di callback con l'identificativo di un'interazione esistente
    Given url govway_base_path + '/rest/out/DemoSoggettoErogatore/DemoSoggettoFruitore/PDNDAsyncRestEstesaCallback/v1/health'
    And configure headers = { 'govway-conversation-id': '#(id)' }
    When method get
    Then status 400
    And match response.title == 'AsyncInteractionInvalidRequest'
    And match response.detail == "The invoked operation 'health' is not associated with any phase of the asynchronous interaction, but the identifier of the existing asynchronous interaction '" + id + "' has been provided"


@purpose-id
Scenario: invio del purposeId nelle fasi get_resource e confirmation (default abilitato, ridefinito nella fruizione come disabilitato o abilitato)

    # default: inviato
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * def esitoChiamata = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)' }
    * match transazione(esitoChiamata.tid).tipo_servizio_correlato == 'callback_invocation'
    * def get1 = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)' }
    * match transazione(get1.tid).tipo_servizio_correlato == 'get_resource'
    * def tiCheck1 = tokenInfo(get1.tid)
    * match tiCheck1.assertion.purposeId == 'pdnd-async-purpose'

    # fruizione con invio disabilitato
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-PurposeIdDisabilitato' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * def tiCheck2 = tokenInfo(start.tid)
    * match tiCheck2.assertion.purposeId == 'pdnd-async-purpose'
    * def esitoChiamata = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)' }
    * match transazione(esitoChiamata.tid).tipo_servizio_correlato == 'callback_invocation'
    * def get1 = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestCompatta-PurposeIdDisabilitato', conversationId: '#(id)' }
    * match transazione(get1.tid).tipo_servizio_correlato == 'get_resource'
    * def tiCheck3 = tokenInfo(get1.tid)
    * match tiCheck3.assertion.purposeId == '#notpresent'
    * def conf = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestCompatta-PurposeIdDisabilitato', conversationId: '#(id)' }
    * match transazione(conf.tid).tipo_servizio_correlato == 'confirmation'
    * def tiCheck4 = tokenInfo(conf.tid)
    * match tiCheck4.assertion.purposeId == '#notpresent'

    # fruizione con invio abilitato esplicitamente
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-PurposeIdAbilitato' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * def esitoChiamata = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)' }
    * match transazione(esitoChiamata.tid).tipo_servizio_correlato == 'callback_invocation'
    * def get1 = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestCompatta-PurposeIdAbilitato', conversationId: '#(id)' }
    * match transazione(get1.tid).tipo_servizio_correlato == 'get_resource'
    * def tiCheck5 = tokenInfo(get1.tid)
    * match tiCheck5.assertion.purposeId == 'pdnd-async-purpose'
    * def conf = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestCompatta-PurposeIdAbilitato', conversationId: '#(id)' }
    * match transazione(conf.tid).tipo_servizio_correlato == 'confirmation'
    * def tiCheck6 = tokenInfo(conf.tid)
    * match tiCheck6.assertion.purposeId == 'pdnd-async-purpose'
