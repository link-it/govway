Feature: Scambi di dati asincroni PDND (REST) - errori rilevati lato fruizione

Background:
    * def utils = 'classpath:test/rest/pdnd-async/pdnd-async-utils.feature'
    * def interazione = read('classpath:utils/pdnd_async_interazione.js')
    * def sleep = function(ms){ java.lang.Thread.sleep(ms) }
    * def diagnostici = read('classpath:utils/pdnd_async_diagnostici.js')
    * def transazione = read('classpath:utils/pdnd_async_transazione.js')
    # errori rilevati dalla fruizione durante l'imbustamento della richiesta (verifica dello stato dell'interazione)
    * def DIAG_FRUIZIONE = '[imbustatore.after-sec.imbustamento]: '


@get-resource-senza-conversation-id
Scenario: get_resource senza identificativo dell'interazione

    * def r = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestCompatta', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.title == 'AsyncInteractionInvalidRequest'
    * match r.response.detail == "The asynchronous interaction phase 'get_resource' requires the interaction identifier (Conversation-ID)"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + r.response.detail


@get-resource-interazione-non-esistente
Scenario: get_resource e confirmation per un'interazione non esistente

    * def r = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestCompatta', conversationId: 'interazione-non-esistente', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.title == 'AsyncInteractionNotFound'
    * match r.response.detail == "Asynchronous interaction 'interazione-non-esistente' not found"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + r.response.detail
    * def r = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestCompatta', conversationId: 'interazione-non-esistente', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.response.title == 'AsyncInteractionNotFound'


@get-resource-altra-fruizione
Scenario: get_resource per un'interazione avviata con un'altra fruizione

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * def esitoChiamata = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)' }
    * match transazione(esitoChiamata.tid).tipo_servizio_correlato == 'callback_invocation'
    * def r = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestCompatta-UrlClientHeader', conversationId: '#(id)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.title == 'AsyncInteractionNotFound'
    * match r.response.detail == "Asynchronous interaction '" + id + "' not found"


@get-resource-prima-della-callback
Scenario: get_resource e confirmation prima della callback

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * def r = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)', statusAtteso: 409 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.title == 'AsyncInteractionInvalidState'
    * match r.response.detail == "Asynchronous interaction '" + id + "': resource not yet available (callback not received), phase 'get_resource' not allowed"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + r.response.detail
    * def r = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)', statusAtteso: 409 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.response.detail == "Asynchronous interaction '" + id + "': resource not yet available (callback not received), phase 'confirmation' not allowed"


@confirmation-prima-di-get-resource
Scenario: confirmation dopo la callback ma prima di aver ottenuto la risposta: rifiutata dalla fruizione

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * def esitoChiamata = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)' }
    * match transazione(esitoChiamata.tid).tipo_servizio_correlato == 'callback_invocation'
    * def r = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)', statusAtteso: 409 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.response.title == 'AsyncInteractionInvalidState'
    * match r.response.detail == "Asynchronous interaction '" + id + "': response not yet obtained (get_resource), phase 'confirmation' not allowed"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + r.response.detail
    # la fase viene tracciata anche nella transazione rifiutata dalla fruizione
    * def tr = transazione(r.tid)
    * match tr.tipo_servizio_correlato == 'confirmation'
    # la richiesta non raggiunge l'erogatore: nessuna fase registrata su entrambi i lati
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'callback_invocation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'callback_invocation'

    # dopo l'ottenimento della risposta la conferma viene accettata
    * def get1 = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)' }
    * match transazione(get1.tid).tipo_servizio_correlato == 'get_resource'
    * def conf = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)' }
    * match transazione(conf.tid).tipo_servizio_correlato == 'confirmation'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'confirmation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'confirmation'


@callback-errori-richiesta
Scenario: callback_invocation: identificativo dell'interazione e numero di entità mancanti, non validi o oltre il limite

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId

    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionInvalidRequest'
    * match r.response.detail == "The asynchronous interaction phase 'callback_invocation' requires the interaction identifier (Conversation-ID)"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + r.response.detail

    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)', senzaEntityNumber: true, statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionInvalidRequest'
    * match r.response.detail == "Entity number not provided (HTTP header 'GovWay-PDND-Entity-Number')"

    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)', entityNumber: 'abc', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionInvalidRequest'
    * match r.response.detail == "Entity number 'abc' provided (HTTP header 'GovWay-PDND-Entity-Number') is not valid: value must be an integer"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + r.response.detail

    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)', entityNumber: 0, statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.detail == "Entity number '0' provided (HTTP header 'GovWay-PDND-Entity-Number') is not valid: value must be greater than zero"

    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)', entityNumber: 21, statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionInvalidRequest'
    * match r.response.detail == "The entity number '21' exceeds the maximum number of entities per response (20) defined for the asynchronous interaction"

    # nessuna delle richieste errate ha modificato lo stato dell'interazione
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'

    # il limite è comprensivo
    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)', entityNumber: 20 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'


@callback-entity-number-query
Scenario: callback_invocation: numero di entità atteso come parametro della URL (API estesa)

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestEstesa' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestEstesaCallback', conversationId: '#(id)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.detail == "Entity number not provided (query parameter 'govway_pdnd_entity_number')"
    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestEstesaCallback', conversationId: '#(id)', entityNumber: 6, entityNumberQuery: true, statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.detail == "The entity number '6' exceeds the maximum number of entities per response (5) defined for the asynchronous interaction"


@callback-interazione-non-esistente
Scenario: callback_invocation per un'interazione non esistente o relativa ad un altro e-service

    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: 'interazione-non-esistente', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionNotFound'
    * match r.response.detail == "Asynchronous interaction 'interazione-non-esistente' not found"

    # interazione avviata su un e-service diverso da quello correlato all'API di callback invocata
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestVerificaCallback', conversationId: '#(start.conversationId)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionNotFound'


@callback-ripetuta
Scenario: callback_invocation ripetuta

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * def esitoChiamata = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)' }
    * match transazione(esitoChiamata.tid).tipo_servizio_correlato == 'callback_invocation'
    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)', statusAtteso: 409 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionInvalidState'
    * match r.response.detail == "Asynchronous interaction '" + id + "': callback already invoked"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + r.response.detail


@callback-scaduta
Scenario: tempo massimo di risposta scaduto: callback_invocation e get_resource rifiutate

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestScadenze' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * sleep(4500)
    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestScadenzeCallback', conversationId: '#(id)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionExpired'
    * match r.response.detail contains "Maximum callback time (3s) expired at "
    * match r.response.detail contains "for the asynchronous interaction '" + id + "'"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_FRUIZIONE + r.response.detail
    * def r = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestScadenze', conversationId: '#(id)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.title == 'AsyncInteractionExpired'
    * match r.response.detail contains "Maximum callback time (3s) expired at "


@risposta-scaduta
Scenario: durata di disponibilità del dato scaduta: get_resource e confirmation rifiutate

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestScadenze' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * def esitoChiamata = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestScadenzeCallback', conversationId: '#(id)' }
    * match transazione(esitoChiamata.tid).tipo_servizio_correlato == 'callback_invocation'
    * def esitoChiamata = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestScadenze', conversationId: '#(id)' }
    * match transazione(esitoChiamata.tid).tipo_servizio_correlato == 'get_resource'
    * sleep(4500)
    * def r = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestScadenze', conversationId: '#(id)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.title == 'AsyncInteractionExpired'
    * match r.response.detail contains "Resource availability time (3s) expired at "
    * def r = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestScadenze', conversationId: '#(id)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.response.title == 'AsyncInteractionExpired'
