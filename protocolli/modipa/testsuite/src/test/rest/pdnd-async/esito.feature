Feature: Scambi di dati asincroni PDND (REST) - registrazione delle fasi in base all'esito della risposta

# Il backend restituisce il codice HTTP indicato nell'header 'govway-testsuite-pdnd-async-status'.
# Una fase viene registrata solamente se il codice HTTP rientra tra quelli configurati (default 200-299):
# in caso contrario l'interazione resta nello stato precedente e la fase può essere ripetuta.

Background:
    * def utils = 'classpath:test/rest/pdnd-async/pdnd-async-utils.feature'
    * def interazione = read('classpath:utils/pdnd_async_interazione.js')
    * def transazione = read('classpath:utils/pdnd_async_transazione.js')
    * def tokenInfo = read('classpath:utils/pdnd_async_token_info.js')


@backend-errore-start
Scenario: start_interaction con backend dell'erogatore in errore: interazione non registrata da entrambi i lati

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta', headersExtra: { 'govway-testsuite-pdnd-async-status': '503' }, statusAtteso: 503 }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def ti = tokenInfo(start.tid)
    * def id = ti.accessToken.interactionId
    * match id == '#notnull'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore == null
    * def fruitore = interazione(id, 'delegata')
    * match fruitore == null


@backend-errore-fasi-ripetibili
Scenario: callback_invocation, get_resource e confirmation con backend in errore: la fase non viene registrata e può essere ripetuta

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId

    # callback: backend del fruitore in errore
    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)', headersExtra: { 'govway-testsuite-pdnd-async-status': '500' }, statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'start_interaction'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)' }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'callback_invocation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'callback_invocation'

    # get_resource: backend dell'erogatore in errore
    * def r = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)', headersExtra: { 'govway-testsuite-pdnd-async-status': '500' }, statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.numero_get_resource == '0'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.numero_get_resource == '0'
    * def r = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)' }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.numero_get_resource == '1'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.numero_get_resource == '1'

    # confirmation: backend dell'erogatore in errore
    * def r = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)', headersExtra: { 'govway-testsuite-pdnd-async-status': '500' }, statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.data_confirmation == null
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.data_confirmation == null
    * def r = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(id)' }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'confirmation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'confirmation'


@esito-ridefinito
Scenario: codici HTTP di esito positivo ridefiniti nella fruizione e nell'erogazione (solo 200)

    # il backend risponde 202: non rientra tra i codici configurati, la start non viene registrata
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-EsitoRidefinito' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def ti = tokenInfo(start.tid)
    * def id = ti.accessToken.interactionId
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore == null
    * def fruitore = interazione(id, 'delegata')
    * match fruitore == null

    # il backend risponde 200: la start viene registrata
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta-EsitoRidefinito', headersExtra: { 'govway-testsuite-pdnd-async-status': '200' }, statusAtteso: 200 }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'start_interaction'


@esito-ridefinito-solo-fruizione
Scenario: codici HTTP di esito positivo ridefiniti solamente nelle fruizioni (solo 200), erogazioni con il default 200-299

    # start: il backend dell'erogatore risponde 202, registrata solamente dall'erogatore
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestEsito' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def ti = tokenInfo(start.tid)
    * def id = ti.accessToken.interactionId
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore == null
    # per il fruitore l'interazione non esiste
    * def r = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestEsito', conversationId: '#(id)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.title == 'AsyncInteractionNotFound'

    # start con risposta 200: registrata da entrambi i lati
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestEsito', headersExtra: { 'govway-testsuite-pdnd-async-status': '200' }, statusAtteso: 200 }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'start_interaction'

    # callback: il backend del fruitore risponde 202, registrata solamente dal fruitore (erogazione della callback con il default)
    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestEsitoCallback', conversationId: '#(id)', headersExtra: { 'govway-testsuite-pdnd-async-status': '202' }, statusAtteso: 202 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'callback_invocation'
    # la callback ripetuta viene rifiutata dal fruitore, che l'ha già registrata
    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestEsitoCallback', conversationId: '#(id)', statusAtteso: 409 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionInvalidState'
    * match r.response.detail == "Asynchronous interaction '" + id + "': callback already invoked"

    # nuova interazione con start e callback registrate da entrambi i lati
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestEsito', headersExtra: { 'govway-testsuite-pdnd-async-status': '200' }, statusAtteso: 200 }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestEsitoCallback', conversationId: '#(id)' }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'callback_invocation'

    # get_resource: il backend dell'erogatore risponde 202, registrata solamente dall'erogatore
    * def r = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestEsito', conversationId: '#(id)', headersExtra: { 'govway-testsuite-pdnd-async-status': '202' }, statusAtteso: 202 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.numero_get_resource == '1'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.numero_get_resource == '0'
    # per il fruitore la risposta non è ancora stata ottenuta: la conferma viene rifiutata
    * def r = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestEsito', conversationId: '#(id)', statusAtteso: 409 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.response.detail == "Asynchronous interaction '" + id + "': response not yet obtained (get_resource), phase 'confirmation' not allowed"
    * def r = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestEsito', conversationId: '#(id)' }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.numero_get_resource == '2'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.numero_get_resource == '1'

    # confirmation: il backend dell'erogatore risponde 202, registrata solamente dall'erogatore
    * def r = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestEsito', conversationId: '#(id)', headersExtra: { 'govway-testsuite-pdnd-async-status': '202' }, statusAtteso: 202 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'confirmation'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.data_confirmation == null
    # la conferma ripetuta supera il fruitore ma viene rifiutata dall'erogatore, che l'ha già registrata
    * def r = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestEsito', conversationId: '#(id)', statusAtteso: 409 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.response.detail == "Asynchronous interaction '" + id + "' already confirmed: phase 'confirmation' not allowed"
