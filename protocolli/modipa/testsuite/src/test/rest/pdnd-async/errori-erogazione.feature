Feature: Scambi di dati asincroni PDND (REST) - errori rilevati lato erogazione (invocazioni dirette con voucher forzati)

# Le erogazioni vengono invocate direttamente, senza passare dalla fruizione, con voucher emessi dalla risorsa '/voucher'
# dell'authorization server di test, che consente di indicare (o omettere) i claim dello scambio asincrono.

Background:
    * def utils = 'classpath:test/rest/pdnd-async/pdnd-async-utils.feature'
    * def interazione = read('classpath:utils/pdnd_async_interazione.js')
    * def transazione = read('classpath:utils/pdnd_async_transazione.js')
    * def sleep = function(ms){ java.lang.Thread.sleep(ms) }
    * def uuid = function(){ return '' + java.util.UUID.randomUUID() }
    * def voucher = function(claims){ var r = karate.call(utils + '@voucher', { claims: claims }); return r.voucher; }
    * def URL_CALLBACK = 'http://host-callback/callback'
    * def diagnostici = read('classpath:utils/pdnd_async_diagnostici.js')
    # errori rilevati dall'erogazione durante la validazione semantica della richiesta (eccezione di validazione GOVWAY-354)
    * def DIAG_EROGAZIONE = '[GOVWAY-354] - EccezioneValidazioneProtocollo: '


@erogatore-start-errori-voucher
Scenario: start_interaction: voucher privo dei claim previsti o con scope errato

    # scope non coerente con la fase dell'operazione invocata
    * def v = voucher({ scope: 'get_resource', interactionId: uuid(), urlCallback: URL_CALLBACK })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/requests', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.response.title == 'AsyncInteractionInvalidRequest'
    * match r.response.detail == "The PDND voucher does not contain the scope 'start_interaction' expected for the invoked operation"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + r.response.detail

    # interactionId mancante
    * def v = voucher({ scope: 'start_interaction', urlCallback: URL_CALLBACK })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/requests', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.response.title == 'AsyncInteractionInvalidRequest'
    * match r.response.detail == "Claim 'interactionId' not found in the PDND voucher"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + r.response.detail

    # urlCallback mancante
    * def v = voucher({ scope: 'start_interaction', interactionId: uuid() })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/requests', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.response.title == 'AsyncInteractionInvalidRequest'
    * match r.response.detail == "Claim 'urlCallback' not found in the PDND voucher"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + r.response.detail


@erogatore-start-ripetuta
Scenario: start_interaction ripetuta con lo stesso interactionId

    * def id = uuid()
    * def v = voucher({ scope: 'start_interaction', interactionId: id, urlCallback: URL_CALLBACK })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/requests', voucher: '#(v)', statusAtteso: 202 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.response.received.conversationId == id
    * match r.response.received.urlCallback == URL_CALLBACK
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * match erogatore.url_callback == URL_CALLBACK

    * def v = voucher({ scope: 'start_interaction', interactionId: id, urlCallback: URL_CALLBACK })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/requests', voucher: '#(v)', statusAtteso: 409 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.response.title == 'AsyncInteractionInvalidState'
    * match r.response.detail == "Asynchronous interaction '" + id + "' already started"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + r.response.detail


@erogatore-risorsa-errori
Scenario: get_resource e confirmation: interazione inesistente, di un altro e-service, di un altro consumer, callback non ricevuta, confirmation prima di get_resource, già confermata

    # interazione completa avviata tramite la fruizione
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId

    # callback non ancora ricevuta
    * def v = voucher({ scope: 'get_resource', interactionId: id })
    * def r = call read(utils + '@erogazioneGet') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/results', voucher: '#(v)', statusAtteso: 409 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.title == 'AsyncInteractionInvalidState'
    * match r.response.detail == "Asynchronous interaction '" + id + "': resource not yet available (callback not received), phase 'get_resource' not allowed"

    # confirmation prima della callback
    * def v = voucher({ scope: 'confirmation', interactionId: id })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/results/confirmation', voucher: '#(v)', statusAtteso: 409 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.response.title == 'AsyncInteractionInvalidState'
    * match r.response.detail == "Asynchronous interaction '" + id + "': resource not yet available (callback not received), phase 'confirmation' not allowed"

    * def esitoChiamata = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(id)' }
    * match transazione(esitoChiamata.tid).tipo_servizio_correlato == 'callback_invocation'

    # confirmation dopo la callback ma prima dell'ottenimento della risposta
    * def v = voucher({ scope: 'confirmation', interactionId: id })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/results/confirmation', voucher: '#(v)', statusAtteso: 409 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.response.title == 'AsyncInteractionInvalidState'
    * match r.response.detail == "Asynchronous interaction '" + id + "': response not yet obtained (get_resource), phase 'confirmation' not allowed"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + r.response.detail
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.fase == 'callback_invocation'

    # interazione non esistente
    * def v = voucher({ scope: 'get_resource', interactionId: 'interazione-non-esistente' })
    * def r = call read(utils + '@erogazioneGet') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/results', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.title == 'AsyncInteractionNotFound'
    * match r.response.detail == "Asynchronous interaction 'interazione-non-esistente' not found"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + r.response.detail

    # scope errato
    * def v = voucher({ scope: 'confirmation', interactionId: id })
    * def r = call read(utils + '@erogazioneGet') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/results', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.detail == "The PDND voucher does not contain the scope 'get_resource' expected for the invoked operation"

    # interactionId mancante
    * def v = voucher({ scope: 'get_resource' })
    * def r = call read(utils + '@erogazioneGet') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/results', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.detail == "Claim 'interactionId' not found in the PDND voucher"

    # interazione di un altro consumer
    * def v = voucher({ scope: 'get_resource', interactionId: id, consumerId: 'altro-consumer' })
    * def r = call read(utils + '@erogazioneGet') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/results', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.title == 'AsyncInteractionNotFound'

    # interazione di un altro e-service (erogazione di un'altra API)
    * def v = voucher({ scope: 'get_resource', interactionId: id })
    * def r = call read(utils + '@erogazioneGet') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestVerifica', risorsa: '/results', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.title == 'AsyncInteractionNotFound'

    # interazione di un'altra erogazione della stessa API
    * def r = call read(utils + '@erogazioneGet') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta-UrlClientHeader', risorsa: '/results', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.title == 'AsyncInteractionNotFound'

    # voucher corretto
    * def r = call read(utils + '@erogazioneGet') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/results', voucher: '#(v)', statusAtteso: 200 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.received.conversationId == id
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore.numero_get_resource == '1'

    # conferma e successive richieste rifiutate
    * def v = voucher({ scope: 'confirmation', interactionId: id })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/results/confirmation', voucher: '#(v)', statusAtteso: 200 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * def v = voucher({ scope: 'confirmation', interactionId: id })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/results/confirmation', voucher: '#(v)', statusAtteso: 409 }
    * match transazione(r.tid).tipo_servizio_correlato == 'confirmation'
    * match r.response.title == 'AsyncInteractionInvalidState'
    * match r.response.detail == "Asynchronous interaction '" + id + "' already confirmed: phase 'confirmation' not allowed"
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + r.response.detail
    * def v = voucher({ scope: 'get_resource', interactionId: id })
    * def r = call read(utils + '@erogazioneGet') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/results', voucher: '#(v)', statusAtteso: 409 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.detail == "Asynchronous interaction '" + id + "' already confirmed: phase 'get_resource' not allowed"


@fruitore-erogazione-callback-errori
Scenario: erogazione dell'API di callback (GovWay del fruitore) invocata direttamente: interazione inesistente, scope errato, callback ripetuta

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId

    # interazione non esistente
    * def v = voucher({ scope: 'callback_invocation', interactionId: 'interazione-non-esistente' })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoFruitore', servizio: 'PDNDAsyncRestCompattaCallback', risorsa: '/notifications', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionNotFound'

    # scope errato
    * def v = voucher({ scope: 'start_interaction', interactionId: id })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoFruitore', servizio: 'PDNDAsyncRestCompattaCallback', risorsa: '/notifications', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.detail == "The PDND voucher does not contain the scope 'callback_invocation' expected for the invoked operation"

    # interazione relativa ad un e-service non correlato all'API di callback
    * def v = voucher({ scope: 'callback_invocation', interactionId: id })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoFruitore', servizio: 'PDNDAsyncRestVerificaCallback', risorsa: '/notifications', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionNotFound'

    # callback valida, con entityNumber nel voucher inoltrato al backend
    * def v = voucher({ scope: 'callback_invocation', interactionId: id, entityNumber: '7' })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoFruitore', servizio: 'PDNDAsyncRestCompattaCallback', risorsa: '/notifications', voucher: '#(v)', statusAtteso: 200 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.received.conversationId == id
    * match r.response.received.entityNumber == '7'
    * def fruitore = interazione(id, 'delegata')
    * match fruitore.fase == 'callback_invocation'
    * match fruitore.entity_number == '7'

    # callback ripetuta
    * def v = voucher({ scope: 'callback_invocation', interactionId: id })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoFruitore', servizio: 'PDNDAsyncRestCompattaCallback', risorsa: '/notifications', voucher: '#(v)', statusAtteso: 409 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionInvalidState'
    * match r.response.detail == "Asynchronous interaction '" + id + "': callback already invoked"


@scadenze-lato-erogazione
Scenario: scadenze rilevate lato erogazione: callback scaduta (GovWay del fruitore) e risorsa scaduta (GovWay dell'erogatore)

    # callback scaduta: rifiutata dall'erogazione dell'API di callback
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestScadenze' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * sleep(4500)
    * def v = voucher({ scope: 'callback_invocation', interactionId: id })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoFruitore', servizio: 'PDNDAsyncRestScadenzeCallback', risorsa: '/notifications', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionExpired'
    * match r.response.detail contains "Maximum callback time (3s) expired at "
    # get_resource rifiutata dall'erogazione dell'e-service (callback non ricevuta e tempo scaduto)
    * def v = voucher({ scope: 'get_resource', interactionId: id })
    * def r = call read(utils + '@erogazioneGet') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestScadenze', risorsa: '/results', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.title == 'AsyncInteractionExpired'
    * match r.response.detail contains "Maximum callback time (3s) expired at "

    # risposta scaduta
    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestScadenze' }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * def id = start.conversationId
    * def esitoChiamata = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestScadenzeCallback', conversationId: '#(id)' }
    * match transazione(esitoChiamata.tid).tipo_servizio_correlato == 'callback_invocation'
    * sleep(4500)
    * def v = voucher({ scope: 'get_resource', interactionId: id })
    * def r = call read(utils + '@erogazioneGet') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestScadenze', risorsa: '/results', voucher: '#(v)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'get_resource'
    * match r.response.title == 'AsyncInteractionExpired'
    * match r.response.detail contains "Resource availability time (3s) expired at "
    * def d = diagnostici(r.tid)
    * match d contains DIAG_EROGAZIONE + r.response.detail


@erogazione-senza-voucher
Scenario: erogazione invocata senza voucher: rifiutata dalla validazione del token

    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestCompatta', risorsa: '/requests', statusAtteso: 401 }
