Feature: Scambi di dati asincroni PDND (REST) - errori dovuti alla configurazione (5xx)

# Errori non imputabili al client (INTERNAL_REQUEST_ERROR): con la configurazione di default degli errori
# (WRAP_503_INTERNAL_ERROR abilitato) viene restituito 503 'APIUnavailable' con dettaglio generico; la causa è riportata nei diagnostici.

Background:
    * def utils = 'classpath:test/rest/pdnd-async/pdnd-async-utils.feature'
    * def interazione = read('classpath:utils/pdnd_async_interazione.js')
    * def transazione = read('classpath:utils/pdnd_async_transazione.js')
    * def diagnostici = read('classpath:utils/pdnd_async_diagnostici.js')
    * def uuid = function(){ return '' + java.util.UUID.randomUUID() }
    * def voucher = function(claims){ var r = karate.call(utils + '@voucher', { claims: claims }); return r.voucher; }


@erogazione-callback-assente
Scenario: start_interaction: il soggetto fruitore non possiede l'erogazione dell'API di callback

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestSenzaCallback', statusAtteso: 503 }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.title == 'APIUnavailable'
    * match start.response.detail == 'The API Implementation is temporary unavailable'
    * def diag = diagnostici(start.tid)
    * match diag contains "Callback API implementation (erogazione) of the subject 'modipa/DemoSoggettoFruitore' not found: the callback URL cannot be determined"


@erogazione-callback-non-univoca
Scenario: start_interaction: il soggetto fruitore possiede più erogazioni dell'API di callback

    * def start = call read(utils + '@start') { servizio: 'PDNDAsyncRestDuplicata', statusAtteso: 503 }
    * match transazione(start.tid).tipo_servizio_correlato == 'start_interaction'
    * match start.response.title == 'APIUnavailable'
    * match start.response.detail == 'The API Implementation is temporary unavailable'
    * def diag = diagnostici(start.tid)
    * match diag contains "Multiple callback API implementations (erogazioni) of the subject 'modipa/DemoSoggettoFruitore' found: the callback URL cannot be determined"


@verifica-senza-fruizioni
Scenario: start_interaction ricevuta dall'erogatore con verifica della URL abilitata, ma senza fruizioni dell'API di callback

    * def id = uuid()
    * def v = voucher({ scope: 'start_interaction', interactionId: id, urlCallback: 'http://host-callback/callback' })
    * def r = call read(utils + '@erogazionePost') { soggettoErogatore: 'DemoSoggettoErogatore', servizio: 'PDNDAsyncRestSenzaCallback', risorsa: '/requests', voucher: '#(v)', statusAtteso: 503 }
    * match transazione(r.tid).tipo_servizio_correlato == 'start_interaction'
    * match r.response.title == 'APIUnavailable'
    * match r.response.detail == 'The API Implementation is temporary unavailable'
    * def diag = diagnostici(r.tid)
    * match diag contains "Callback URL verification failed: no callback API subscription (fruizione) of the subject 'modipa/DemoSoggettoErogatore' found"
    * def erogatore = interazione(id, 'applicativa')
    * match erogatore == null
