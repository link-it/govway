Feature: Ricerca Temporale Transazioni e Ricerca Eventi

Background: 

    * def ricercaUrl = monitorUrl + '/monitoraggio/transazioni'
    
    * call read('classpath:crud_commons.feature')
    * def setup = callonce read('classpath:prepare_tests.feature')
    * def intervallo_temporale = ({ data_inizio: setup.dataInizio, data_fine: setup.dataFine })
    
    * configure afterFeature = function(){ karate.call('classpath:cleanup_tests.feature'); }

@FiltroTemporale
Scenario: Ricerca per FiltroTemporale
    * def filtro = read('classpath:bodies/ricerca-filtro-temporale.json')
    * eval filtro.intervallo_temporale = intervallo_temporale
    
    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length >= 3

@FiltroApiErogazione
Scenario: Ricerca per FiltroApiErogazione
    * def filtro = read('classpath:bodies/ricerca-filtro-api-erogazione.json')
    * eval filtro.intervallo_temporale = intervallo_temporale
    * eval filtro.api.nome = setup.erogazione_petstore.api_nome
    * eval filtro.api.versione = setup.erogazione_petstore.api_versione

    * def expected_api = 
    """
    { 
        nome: '#(filtro.api.nome)',
        versione: '#(filtro.api.versione)',
        operazione: '#(filtro.azione)',
        erogatore: '#(soggettoDefault)',
        tipo: "gw",
        informazioni_erogatore: "#notnull",
        profilo_collaborazione: "##notnull"
    }
    """

    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And match each response.items contains { api: '#(^expected_api)' }

@TestNotFound
    Scenario: Test Not Found
    * def filtro = read('classpath:bodies/ricerca-filtro-api-erogazione.json')
    * eval filtro.intervallo_temporale = intervallo_temporale
    * eval filtro.api.nome = "$$FiltroInesistente$$"
    
    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then assert ( responseStatus == 200 && response.items.length == 0) || responseStatus == 404


@FiltroEsitoError
Scenario: Ricerca per Esito Erroneo
    * def filtro = read('classpath:bodies/ricerca-filtro-esito-error.json')
    * eval filtro.intervallo_temporale = intervallo_temporale

    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length == 1

@FiltroEsitoPersonalizzato
Scenario: Ricerca per Filtro Esito Personalizzato
    * def filtro = read('classpath:bodies/ricerca-filtro-esito-personalizzato.json')
    * eval filtro.intervallo_temporale = intervallo_temporale

    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1

@FiltroMittenteApplicativo
Scenario: Ricerca per Filtro Mittente Applicativo
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-applicativo.json')
    * eval filtro.intervallo_temporale = intervallo_temporale
    * eval filtro.mittente.id.soggetto = soggettoDefault
    * eval filtro.mittente.id.applicativo = setup.applicativo.nome

    * def expected_mittente = ({ applicativo: filtro.mittente.id.applicativo })

    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }

@FiltroMittenteIdAutenticatoHttp
Scenario: Ricerca per Filtro Mittente con autenticazione http
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-idautenticato.json')
    * eval filtro.mittente.id.id = setup.applicativo.credenziali.username
    * eval filtro.intervallo_temporale = intervallo_temporale

    * def expected_mittente = ({ applicativo: setup.applicativo.nome })

    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }


@FiltroMittenteIdAutenticatoHttps
Scenario: Ricerca per Filtro Mittente con autenticazione https
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-idautenticato.json')
    * set filtro.mittente.id = ({ ricerca_esatta: false, case_sensitive: false, autenticazione: 'ssl', id: "cn=client"})
    * set filtro.intervallo_temporale = intervallo_temporale

    * def expected_mittente = ({ fruitore: setup.soggetto_certificato.nome })

    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }


@FiltroMittenteIdAutenticatoPrincipal
Scenario: Ricerca per Filtro Mittente con autenticazione principal
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-idautenticato.json')
    * eval filtro.mittente.id.id = setup.applicativo_principal.credenziali.userid
    * eval filtro.intervallo_temporale = intervallo_temporale
    * eval filtro.mittente.id.autenticazione = 'principal'

    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length == 1
    
@FiltroMittenteSoggetto
Scenario: Ricerca per Filtro Mittente Soggetto
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-soggetto.json')
    * eval filtro.intervallo_temporale = intervallo_temporale
    * eval filtro.mittente.id.soggetto = setup.soggetto_http.nome

    * def expected_mittente = ({ fruitore: filtro.mittente.id.soggetto })

    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)'}

@RicercaSempliceTransazioni
Scenario: RicercaSempliceTransazioni tramite richiesta GET
    
    * def filtro = read('classpath:bodies/ricerca-filtro-api-erogazione.json')
    * eval filtro.api.nome = setup.erogazione_petstore.api_nome
    * eval filtro.api.versione = setup.erogazione_petstore.api_versione    
    * eval filtro.intervallo_temporale = intervallo_temporale

    * def query =
    """ ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: 'erogazione',
        nome_servizio: filtro.api.nome,
        versione_servizio: filtro.api.versione,
        azione: filtro.azione,
        esito: 'ok'
    })
    """

    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And params query
    When method get
    Then status 200
    And assert response.items.length > 0 && response.items.length <= 3


@IdMessaggioRisposta
Scenario: Ricerca singola transazione per Id Messaggio (Risposta)

    * def filtro = read('classpath:bodies/ricerca-filtro-api-erogazione.json')
    * eval filtro.api.nome = setup.erogazione_petstore.nome
    * eval filtro.api.versione = setup.erogazione_petstore.versione
    * eval filtro.intervallo_temporale =  ({ data_inizio: setup.dataInizio, data_fine: setup.dataFine })


    # Viene fatta prima una ricerca lasca per recuperare delle transazioni qualsiasi
    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length > 0

    * def transazione = response.items[0]
    * def id_messaggio = transazione.risposta.id

    Given url ricercaUrl
    And path 'id_messaggio'
    And header Authorization = govwayMonitorCred
    And params ({ tipo_messaggio: 'risposta', id: id_messaggio })
    When method get
    Then status 200

    * set transazione.data_emissione = "#ignore"
    * set response.items[0].data_emissione = "#ignore"
    * match response.items[0] == transazione

@IdTransazione
Scenario: Ricerca per Id Transazione.

    * def filtro = read('classpath:bodies/ricerca-filtro-api-erogazione.json')
    * eval filtro.api.nome = setup.erogazione_petstore.nome
    * eval filtro.api.versione = setup.erogazione_petstore.versione
    * eval filtro.intervallo_temporale =  ({ data_inizio: setup.dataInizio, data_fine: setup.dataFine })

     # Viene fatta prima una ricerca lasca per recuperare una transazione qualsiasi
    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length > 0

    * def id_transazione = response.items[0].id_traccia

    Given url ricercaUrl
    And path id_transazione
    And header Authorization = govwayMonitorCred
    When method get
    Then status 200


@RicercaEventi
Scenario: Ricerca Lista Eventi ed evento singolo

    * def query =
    """
    {
        data_inizio: '2012-07-21T17:32:28Z',
        data_fine: '2022-07-21T17:32:28Z',
        severita: 'info',
        tipo: 'StatoGateway',
        codice: 'Start'
    }
    """

    # Faccio prima una ricerca lasca per poi recuperare un evento particolare
    Given url monitorUrl
    And path 'monitoraggio', 'eventi'
    And header Authorization = govwayMonitorCred
    And params query
    When method get
    Then status 200

    * def evento = response.items[0]
    
    Given url monitorUrl
    And path 'monitoraggio', 'eventi', evento.id
    And header Authorization = govwayMonitorCred
    And params query
    When method get
    Then status 200
    And match response == evento

@FiltroMittenteTokenInfo
Scenario: Ricerca di transazioni filtrate per token info
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-tokeninfo.json')
    * eval filtro.mittente.id.id = setup.claims.username
    * eval filtro.mittente.id.claim = 'username'

    # TODO: Portare in una feature esterna, ottenere la transazione e matchare che le token info coincidano
    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length == 1

    * eval filtro.mittente.id.id = setup.claims.issuer
    * eval filtro.mittente.id.claim = 'issuer'
    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length == 1

    * eval filtro.mittente.id.id = setup.claims.client_id
    * eval filtro.mittente.id.claim = 'client_id'
    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length == 1

    * eval filtro.mittente.id.id = setup.claims.subject
    * eval filtro.mittente.id.claim = 'subject'
    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length == 1

    * eval filtro.mittente.id.id = setup.claims.email
    * eval filtro.mittente.id.claim = 'email'
    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length == 1