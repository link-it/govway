Feature: Scambi di dati asincroni PDND (SOAP) - errori dovuti alla configurazione (5xx)

# Errori non imputabili al client (INTERNAL_REQUEST_ERROR): con la configurazione di default degli errori
# (WRAP_503_INTERNAL_ERROR abilitato) viene restituito un SOAP Fault 'APIUnavailable' (problem detail 503) con dettaglio generico;
# la causa è riportata nei diagnostici.

Background:
    * def utils = 'classpath:test/soap/pdnd-async/pdnd-async-soap-utils.feature'
    * def restUtils = 'classpath:test/rest/pdnd-async/pdnd-async-utils.feature'
    * def interazione = read('classpath:utils/pdnd_async_interazione.js')
    * def transazione = read('classpath:utils/pdnd_async_transazione.js')
    * def diagnostici = read('classpath:utils/pdnd_async_diagnostici.js')
    * def uuid = function(){ return '' + java.util.UUID.randomUUID() }
    * def voucher = function(claims){ var r = karate.call(restUtils + '@voucher', { claims: claims }); return r.voucher; }
    * def fruizione = function(servizio){ return 'out/DemoSoggettoFruitore/DemoSoggettoErogatore/' + servizio + '/v1' }
    * def faultDetail = function(r){ return karate.xmlPath(r.response, '/Envelope/Body/Fault/detail/problem/detail') }
    * def faultStatus = function(r){ return karate.xmlPath(r.response, '/Envelope/Body/Fault/detail/problem/status') }


@soap-erogazione-callback-assente
Scenario: startInteraction: il soggetto fruitore non possiede l'erogazione dell'API di callback

    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapSenzaCallback"))', azione: 'startInteraction', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.errorType == 'APIUnavailable'
    * match faultStatus(r) == '503'
    * match faultDetail(r) == 'The API Implementation is temporary unavailable'
    * def d = diagnostici(r.tid)
    * match d contains "Callback API implementation (erogazione) of the subject 'modipa/DemoSoggettoFruitore' not found: the callback URL cannot be determined"


@soap-erogazione-callback-non-univoca
Scenario: startInteraction: il soggetto fruitore possiede più erogazioni dell'API di callback

    * def r = call read(utils + '@invoca') { path: '#(fruizione("PDNDAsyncSoapDuplicata"))', azione: 'startInteraction', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.errorType == 'APIUnavailable'
    * match faultStatus(r) == '503'
    * def d = diagnostici(r.tid)
    * match d contains "Multiple callback API implementations (erogazioni) of the subject 'modipa/DemoSoggettoFruitore' found: the callback URL cannot be determined"


@soap-verifica-senza-fruizioni
Scenario: startInteraction ricevuta dall'erogatore con verifica della URL abilitata, ma senza fruizioni dell'API di callback

    * def id = uuid()
    * def v = voucher({ scope: 'start_interaction', interactionId: id, urlCallback: 'http://host-callback/callback' })
    * def r = call read(utils + '@erogazione') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncSoapSenzaCallback', azione: 'startInteraction', voucher: '#(v)', statusAtteso: 500 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.errorType == 'APIUnavailable'
    * match faultStatus(r) == '503'
    * match faultDetail(r) == 'The API Implementation is temporary unavailable'
    * def d = diagnostici(r.tid)
    * match d contains "Callback URL verification failed: no callback API subscription (fruizione) of the subject 'modipa/DemoSoggettoErogatore' found"
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore == null
