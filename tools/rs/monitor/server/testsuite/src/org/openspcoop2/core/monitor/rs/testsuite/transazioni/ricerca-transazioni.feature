@parallel=false
Feature: Ricerca Temporale Transazioni e Ricerca Eventi

Background: 
* configure afterFeature = function(){ karate.call('classpath:cleanup_tests.feature'); }

* def ricercaUrl = monitorUrl + '/monitoraggio/transazioni'
* call read('classpath:crud_commons.feature')

* def setup = callonce read('classpath:prepare_tests.feature')
* def intervallo_temporale = ({ data_inizio: setup.dataInizio, data_fine: setup.dataFine })

* url ricercaUrl
* configure headers = ({ "Authorization": govwayMonitorCred }) 

@FiltroMittenteTokenInfoFruizione
Scenario: Ricerca con le possibili combinazioni del filtro mittente
    * call read('classpath:ricerca-transazioni-filtro-mittente-token-info.feature')

@FiltroMittenteTokenInfoErogazione
Scenario: Ricerca con le possibili combinazioni del filtro mittente
    * call read('classpath:ricerca-transazioni-filtro-mittente-token-info.feature')

@FiltroMittenteTokenInfoErogazionePDND
Scenario: Ricerca con le possibili combinazioni del filtro mittente
    * call read('classpath:ricerca-transazioni-filtro-mittente-token-info.feature')

@FiltroTemporale
Scenario: Ricerca per FiltroTemporale
    * def filtro = read('classpath:bodies/ricerca-filtro-temporale.json')
    * set filtro.intervallo_temporale = intervallo_temporale
    
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 3

    * set filtro.tipo = 'fruizione'
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 3

@FiltroApi
Scenario: Ricerca per FiltroApi
    * def filtro = read('classpath:bodies/ricerca-filtro-api-erogazione.json')
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.api.nome = setup.erogazione_petstore.api_nome
    * set filtro.api.versione = setup.erogazione_petstore.api_versione

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
    Given request filtro
    When method post
    Then status 200
    And match each response.items contains { api: '#(^expected_api)' }

    * set filtro.api.erogatore = setup.erogatore.nome
    * set filtro.tipo = 'fruizione'
    * set expected_api.erogatore = filtro.api.erogatore
    
    Given request filtro
    When method post
    Then status 200
    And match each response.items contains { api: '#(^expected_api)' }
    

@FiltroApiTipoQualsiasi
Scenario: Ricerca per FiltroApi con tipo qualsiasi

    * def filtro = read('classpath:bodies/ricerca-filtro-api-erogazione.json')
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.tipo = 'qualsiasi'
    * remove filtro.api
    * remove filtro.azione
    * set filtro.esito = { 'tipo' : 'ok' }
    * set filtro.limit = 1000

    * set filtro.tipo = 'fruizione'
    Given request filtro
    When method post
    Then status 200
    * match response.items == '#notnull'
    * match response.items[*].ruolo contains 'fruizione'
    * match response.items[*].ruolo !contains 'erogazione'
    * eval numeroFruizioni = response.items.length
    
    * set filtro.tipo = 'erogazione'
    Given request filtro
    When method post
    Then status 200
    * match response.items == '#notnull'
    * match response.items[*].ruolo contains 'erogazione'
    * match response.items[*].ruolo !contains 'fruizione'
    * eval numeroErogazioni = response.items.length
    * eval if( monitorUrl.contains("api-monitor/v1") ) numeroErogazioni = numeroErogazioni +1
    
    * set filtro.tipo = 'qualsiasi'
    Given request filtro
    When method post
    Then status 200
    * match response.items == '#notnull'
    * match response.items == '#[(numeroFruizioni + numeroErogazioni)]'

    # Imposto il solo soggetto_remoto e, dato che siamo in single-tenant, tutte le transazioni restituite
    # devono essere relative ad api erogate dal soggetto_remoto
    * set filtro.api = ({ soggetto_remoto: setup.fruizione_petstore.erogatore})
    Given request filtro
    When method post
    Then status 200
    * match response.items == '#notnull'
    * match each response.items[*].api.erogatore == filtro.api.soggetto_remoto

@FiltroApiErrato
Scenario Outline: Ricerca per FiltroApi Errato
    * def filtro = 
    """
    ({    
        "intervallo_temporale": intervallo_temporale,
        "tipo": <ruolo_transazione>,
        
        "api": {
            "nome": <nome>,
            "versione":  <versione>,
            "erogatore": <erogatore>,
            "tipo":  <tipo>
        },
        "azione": <azione>
    })
    """

    Given request filtro
    When method post
    Then assert responseStatus == 400 || responseStatus == 422

    # Testo le SimpleSearch
    * def qparams =
    """
    ({
        "data_inizio": filtro.intervallo_temporale.data_inizio,
        "data_fine": filtro.intervallo_temporale.data_fine,
        "tipo": filtro.tipo,
        "nome_servizio": filtro.api.nome,
        "versione_servizio": filtro.api.versione,
        "tipo_servizio": filtro.api.tipo,
        "soggetto_remoto": filtro.api.erogatore,
        "azione": filtro.azione
    })
    """

    Given params qparams
    When method get
    Then assert responseStatus == 400 || responseStatus == 422

Examples:
    | ruolo_transazione | nome                   | versione | erogatore | azione            | tipo         |
    | 'erogazione'      | null                   | 1        | null      | null              | 'gw'         |
    | 'erogazione'      | null                   | null     | null      | null              | 'solo_tipo'  |
    | 'erogazione'      | null                   | null     | null      | 'solo_azione'     | null         |
    | 'fruizione'       | 'nome_senza_erogatore' | null     | null      | null              | null         |
    | 'fruizione'       | null                   | null     | null      | null              | 'solo_tipo'  |

@FiltroApiErratoSimpleSearch
Scenario Outline: Filtro Api Errato Simple Search

    * def qparams =
    """
    ({
        "data_inizio": intervallo_temporale.data_inizio,
        "data_fine": intervallo_temporale.data_fine,
        "tipo": <ruolo_transazione>,
        "nome_servizio": <nome>,
        "versione_servizio": <versione>,
        "tipo_servizio": <tipo>,
        "soggetto_remoto": <soggetto_remoto>,
        "azione": <azione>,
        "soggetto_erogatore": <erogatore>
    })
    """

    Given params qparams
    When method get
    Then assert responseStatus == 400 || responseStatus == 422

Examples:
    | ruolo_transazione | nome                   | versione | erogatore                     | azione            | tipo         | soggetto_remoto |
    | 'erogazione'      | null                   | 1        | null                          | null              | 'gw'         |null             |
    | 'erogazione'      | null                   | null     | null                          | null              | 'solotipo'  |null             |
    | 'erogazione'      | null                   | null     | null                          | 'solo_azione'     | null         |null             |
    | 'erogazione'      | 'petstore'             | 1        | 'danonspecificare'          | 'azione'          | 'tipo1'      |null             |
    | 'erogazione'      | 'petstore'             | 1        | null                          | 'azione'          | 'tipo1'      |'danonspecificare' |
    | 'fruizione'       | 'nomesenzaerogatore' | null     | null                          | null              | null         |null             |
    | 'fruizione'       | null                   | null     | null                          | null              | 'solotipo'  |null             |
    | 'fruizione'       | null                   | null     | 'diversodasoggettoremoto'                      | null              | 'solotipo'  |'diversodaerogatore'             |
    | 'qualsiasi'       | null                   | null     | null                          | null              | 'solotipo'  |null             |


@FiltroApiTags
Scenario: Ricerca tramite richiesta POST con tag 'TESTSUITE'
    
    * def tag = 'TESTSUITE'
    * def filtro = read('classpath:bodies/ricerca-filtro-api-erogazione.json')
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.tipo = 'qualsiasi'
    * remove filtro.api
    * remove filtro.azione
    * set filtro.esito = { 'tipo' : 'ok' }
    * set filtro.tag = tag

    Given request filtro
    When method post
    Then status 200
    * match response.items == '#notnull'
    * match response.items[*].api.tags == '#notnull'
    * match response.items[*].api.tags[*] contains tag

@FiltroMittenteApplicativo
Scenario: Ricerca per Filtro Mittente Applicativo
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-applicativo.json')
    * eval filtro.intervallo_temporale = intervallo_temporale
    * eval filtro.mittente.soggetto = soggettoDefault
    * eval filtro.mittente.applicativo = setup.applicativo.nome

    * def expected_mittente = ({ applicativo: filtro.mittente.applicativo })

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }

    * set filtro.tipo = "fruizione"
    * set filtro.mittente.identificazione = "fruizione_applicativo"
    * remove filtro.mittente.soggetto
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }

@FiltroMittenteApplicativoToken
Scenario: Ricerca per Filtro Mittente Applicativo Token
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-applicativo-token.json')
    * eval filtro.intervallo_temporale = intervallo_temporale
    * eval filtro.mittente.soggetto = soggettoDefault
    * eval filtro.mittente.applicativo = setup.applicativo_token.nome

    * def risposta_applicativo_token = read('classpath:bodies/risposta-applicativo-token.json')
    * eval risposta_applicativo_token.applicativo_token.nome = setup.applicativo_token.nome
    * eval risposta_applicativo_token.applicativo_token.soggetto = soggettoDefault
    * eval risposta_applicativo_token.applicativo_token.informazioni_soggetto.codice = 'domain/gw/'+soggettoDefault
    * def expected_mittente = ({ applicativo_token: risposta_applicativo_token.applicativo_token })

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }

    * set filtro.tipo = "fruizione"
    * set filtro.mittente.identificazione = "fruizione_applicativo"
    * remove filtro.mittente.soggetto
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }

@FiltroMittenteIdAutenticatoHttp
Scenario: Ricerca per Filtro Mittente con autenticazione http
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-idautenticato.json')
    * eval filtro.mittente.id = setup.applicativo.credenziali.username
    * eval filtro.intervallo_temporale = intervallo_temporale
    * def expected_mittente = ({ applicativo: setup.applicativo.nome })

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }

    * set filtro.tipo = "fruizione"
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }

@FiltroMittenteIdAutenticatoHttps
Scenario: Ricerca per Filtro Mittente con autenticazione https
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-idautenticato.json')
    * set filtro.mittente = ({ identificazione: 'identificativo_autenticato', ricerca_esatta: false, case_sensitive: false, autenticazione: 'ssl', id: "cn=client"})
    * set filtro.intervallo_temporale = intervallo_temporale
    
    * def expected_mittente = ({ fruitore: setup.soggetto_certificato.nome })

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }
    
    * set filtro.tipo = "fruizione"
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1


@FiltroMittenteIdAutenticatoPrincipal
Scenario: Ricerca per Filtro Mittente con autenticazione principal
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-idautenticato.json')
    * eval filtro.mittente.id = setup.applicativo_principal.credenziali.userid
    * eval filtro.intervallo_temporale = intervallo_temporale
    * eval filtro.mittente.autenticazione = 'principal'

    * def expected_mittente = ({ applicativo: setup.applicativo_principal.nome })

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }

    * set filtro.tipo = "fruizione"
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }
    
@FiltroMittenteSoggetto
Scenario: Ricerca per Filtro Mittente Soggetto
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-soggetto.json')
    * eval filtro.intervallo_temporale = intervallo_temporale
    * eval filtro.mittente.soggetto = setup.soggetto_http.nome

    * def expected_mittente = ({ fruitore: filtro.mittente.soggetto })

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)'}

@FiltroIndirizzoIPClientIP
Scenario: Ricerca per Filtro Indirizzo IP (Client IP)
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-indirizzo-ip.json')
    * eval filtro.intervallo_temporale = intervallo_temporale
    * eval filtro.mittente.id = "127.0.0.1"
    * eval filtro.mittente.tipo = "client_ip"

    * def expected_mittente = ({ indirizzo_client: filtro.mittente.id })

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }

    * set filtro.tipo = "fruizione"
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }

@FiltroIndirizzoIPXForwardedFor
Scenario: Ricerca per Filtro Indirizzo IP (X-Forwarded-For)
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-indirizzo-ip.json')
    * eval filtro.intervallo_temporale = intervallo_temporale
    * eval filtro.mittente.id = "127.0.0.2"
    * eval filtro.mittente.tipo = "x_forwarded_for"

    * def expected_mittente = ({ indirizzo_client_inoltrato: filtro.mittente.id })

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }

    * set filtro.tipo = "fruizione"
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1
    And match each response.items contains { mittente: '#(^expected_mittente)' }

@TestNotFound
    Scenario: Test Not Found
    * def filtro = read('classpath:bodies/ricerca-filtro-api-erogazione.json')
    * eval filtro.intervallo_temporale = intervallo_temporale
    * eval filtro.api.nome = "FiltroApiInesistente"

    Given request filtro
    When method post
    Then assert ( responseStatus == 200 && response.items.length == 0) || responseStatus == 404


@FiltroEsitoError
Scenario: Ricerca per Esito Erroneo
    * def filtro = read('classpath:bodies/ricerca-filtro-esito-error.json')
    * eval filtro.intervallo_temporale = intervallo_temporale

    * def expected_risposta = { codice: '16', descrizione: 'Autenticazione Fallita' }
    * def expected_risposta_validazione_attiva = { codice: '31', descrizione: 'Validazione Richiesta Fallita' }
    * def expected_or = function(x) { return x == '16' || x == '31' }
    * def expected_descr_or = function(x) { return x == 'Autenticazione Fallita' || x == 'Validazione Richiesta Fallita' }

    # Controllo che le richieste con esito error siano proprio quelle non autorizzate fatte nei test
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length > 0
    And match each response.items[*].esito contains { codice: '#? expected_or(_)' }
    And match each response.items[*].esito contains { descrizione: '#? expected_descr_or(_)' }

    * set filtro.tipo = 'fruizione'
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length > 0
    And match each response.items[*].esito contains { codice: '#? expected_or(_)' }
    And match each response.items[*].esito contains { descrizione: '#? expected_descr_or(_)' }

@FiltroEsitoPersonalizzato
Scenario: Ricerca per Filtro Esito Personalizzato
    * def filtro = read('classpath:bodies/ricerca-filtro-esito-personalizzato.json')
    * eval filtro.intervallo_temporale = intervallo_temporale

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1

    * set filtro.tipo = 'fruizione'
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1

@FiltroEsitoOk
Scenario: Ricerca per Filtro Esito Ok
    * def filtro = read('classpath:bodies/ricerca-filtro-esito-ok.json')
    * eval filtro.intervallo_temporale = intervallo_temporale

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1

    * set filtro.tipo = 'fruizione'
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length >= 1

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
    Given params query
    When method get
    Then status 200
    And assert response.items.length > 0 && response.items.length <= 3

    * set query.soggetto_remoto = setup.erogatore.nome
    * set query.tipo = 'fruizione'
    Given params query
    When method get
    Then status 200
    And assert response.items.length > 0 && response.items.length <= 3

@RicercaSempliceTransazioniTipoQualsiasi
Scenario: RicercaSempliceTransazioni tramite richiesta GET con tipo transazione 'qualsiasi'
    
    * def filtro = read('classpath:bodies/ricerca-filtro-api-erogazione.json')
    * eval filtro.api.nome = setup.erogazione_petstore.api_nome
    * eval filtro.api.versione = setup.erogazione_petstore.api_versione    
    * eval filtro.intervallo_temporale = intervallo_temporale

    * def query =
    """ ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: 'qualsiasi',
        esito: 'ok',
	limit: 1000
    })
    """
   # * set query.soggetto_remoto = setup.erogatore.nome
    * set query.tipo = 'fruizione'
    Given params query
    When method get
    Then status 200
    * match response.items == '#notnull'
    * match response.items[*].ruolo contains 'fruizione'
    * match response.items[*].ruolo !contains 'erogazione'
    * eval numeroFruizioni = response.items.length
    
  #  * set query.soggetto_remoto = setup.erogatore.nome
    * set query.tipo = 'erogazione'
    Given params query
    When method get
    Then status 200
    * match response.items == '#notnull'
    * match response.items[*].ruolo contains 'erogazione'
    * match response.items[*].ruolo !contains 'fruizione'
    * eval numeroErogazioni = response.items.length
    * eval if( monitorUrl.contains("api-monitor/v1") ) numeroErogazioni = numeroErogazioni +1
    
  #  * set query.soggetto_remoto = null
    * set query.tipo = 'qualsiasi'
    Given params query
    When method get
    Then status 200
    * match response.items == '#[(numeroFruizioni + numeroErogazioni)]'


    # Imposto il solo soggetto_remoto e, dato che siamo in single-tenant, tutte le transazioni restituite
    # devono essere relative ad api erogate dal soggetto_remoto
    * set query.soggetto_remoto = setup.fruizione_petstore.erogatore
    Given params query
    When method get
    Then status 200
    * match response.items == '#notnull'
    * match each response.items[*].api.erogatore == query.soggetto_remoto
    
@RicercaSempliceTransazioniTags
Scenario: RicercaSempliceTransazioni tramite richiesta GET con tag 'TESTSUITE'
    
    * def tag = 'TESTSUITE'
    * def filtro = read('classpath:bodies/ricerca-filtro-api-erogazione.json')
    * eval filtro.api.nome = setup.erogazione_petstore.api_nome
    * eval filtro.api.versione = setup.erogazione_petstore.api_versione    
    * eval filtro.intervallo_temporale = intervallo_temporale
    
    * def query =
    """ ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: 'qualsiasi',
        tag: tag
    })
    """
    Given params query
    When method get
    Then status 200
    * match response.items == '#notnull'
    * match response.items[*].api.tags == '#notnull'
    * match response.items[*].api.tags[*] contains tag

@IdMessaggioRichiesta
Scenario: Ricerca singola transazione per Id Messaggio (Richiesta)

    * def filtro = read('classpath:bodies/ricerca-filtro-api-erogazione.json')
    * eval filtro.api.nome = setup.erogazione_petstore.api_nome
    * eval filtro.api.versione = setup.erogazione_petstore.api_versione
    * eval filtro.intervallo_temporale =  ({ data_inizio: setup.dataInizio, data_fine: setup.dataFine })


    # Viene fatta prima una ricerca lasca per recuperare delle transazioni qualsiasi
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length > 0

    # Nella lista, gli id dei messaggi non ci sono per via degli indici. Recupero quindi la singola transazione per id di transazione
    * def transazione_item = response.items[0]
    * def id_transazione = transazione_item.id_traccia

    Given path id_transazione
    When method get
    Then status 200

    * def transazione = response
    * def id_messaggio = transazione.richiesta.id

    Given path 'id-messaggio'
    And params ({ tipo_messaggio: 'richiesta', id: id_messaggio })
    When method get
    Then status 200
    And assert response.items.length > 0

    # Nella lista non ci sono tutte le informazioni per via degli indici. Recupero quindi la singola transazione per id di transazione
    * def transazione_item2 = response.items[0]
    * def id_transazione2 = transazione_item2.id_traccia

    Given path id_transazione2
    When method get
    Then status 200

    * set transazione.data_emissione = "#ignore"
    * set response.data_emissione = "#ignore"
    * match response == transazione

@IdMessaggioRisposta
Scenario: Ricerca singola transazione per Id Messaggio (Risposta)

    * def filtro = read('classpath:bodies/ricerca-filtro-api-erogazione.json')
    * eval filtro.api.nome = setup.erogazione_petstore.api_nome
    * eval filtro.api.versione = setup.erogazione_petstore.api_versione
    * eval filtro.intervallo_temporale =  ({ data_inizio: setup.dataInizio, data_fine: setup.dataFine })


    # Viene fatta prima una ricerca lasca per recuperare delle transazioni qualsiasi
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length > 0

    # Nella lista, gli id dei messaggi non ci sono per via degli indici. Recupero quindi la singola transazione per id di transazione
    * def transazione_item = response.items[0]
    * def id_transazione = transazione_item.id_traccia

    Given path id_transazione
    When method get
    Then status 200

    * def transazione = response
    * def id_messaggio = transazione.risposta.id

    Given path 'id-messaggio'
    And params ({ tipo_messaggio: 'risposta', id: id_messaggio })
    When method get
    Then status 200
    And assert response.items.length > 0

    # Nella lista non ci sono tutte le informazioni per via degli indici. Recupero quindi la singola transazione per id di transazione
    * def transazione_item2 = response.items[0]
    * def id_transazione2 = transazione_item2.id_traccia

    Given path id_transazione2
    When method get
    Then status 200

    * set transazione.data_emissione = "#ignore"
    * set response.data_emissione = "#ignore"
    * match response == transazione

@IdTransazione
Scenario: Ricerca per Id Transazione.

    * def filtro = read('classpath:bodies/ricerca-filtro-api-erogazione.json')
    * eval filtro.api.nome = setup.erogazione_petstore.api_nome
    * eval filtro.api.versione = setup.erogazione_petstore.api_versione
    * eval filtro.intervallo_temporale =  ({ data_inizio: setup.dataInizio, data_fine: setup.dataFine })

     # Viene fatta prima una ricerca lasca per recuperare una transazione qualsiasi
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length > 0

    * def id_transazione = response.items[0].id_traccia

    Given path id_transazione
    When method get
    Then status 200


@RicercaEventi
Scenario: Ricerca Lista Eventi ed evento singolo

    * def query =
    """
    {
        data_inizio: '2012-07-21T17:32:28Z',
        data_fine: '2042-07-21T17:32:28Z',
        severita: 'info',
        tipo: 'StatoGateway',
        codice: 'Start'
    }
    """

    # Faccio prima una ricerca lasca per poi recuperare un evento particolare
    Given url monitorUrl
    And path 'monitoraggio', 'eventi'
    And params query
    When method get
    Then status 200

    * def evento = response.items[0]
    
    Given url monitorUrl
    And path 'monitoraggio', 'eventi', evento.id
    And params query
    When method get
    Then status 200
    And match response == evento

@RicercaSempliceTransazioniApiImplementata
Scenario: RicercaSempliceTransazioni tramite richiesta GET con api implementata
    
    * def filtro = read('classpath:bodies/ricerca-filtro-api-implementata.json')
    * eval filtro.api.api_implementata.nome = setup.erogazione_petstore.api_nome
    * eval filtro.api.api_implementata.versione = setup.erogazione_petstore.api_versione    
    * eval filtro.intervallo_temporale = intervallo_temporale
    
    * def query =
    """ ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: 'qualsiasi',
        uri_api_implementata: filtro.api.api_implementata.nome+':'+filtro.api.api_implementata.versione
    })
    """
    Given params query
    When method get
    Then status 200
    * match response.items == '#notnull'
    * match response.items[*].api.nome contains setup.erogazione_petstore.api_nome


    * def query_errata =
    """ ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: 'qualsiasi',
        uri_api_implementata: filtro.api.api_implementata.nome+'errato:'+filtro.api.api_implementata.versione
    })
    """
    Given params query_errata
    When method get
    Then status 200
    And assert response.items.length == 0


    * def query =
    """ ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: 'qualsiasi',
        uri_api_implementata: 'gw/'+soggettoDefault+':'+filtro.api.api_implementata.nome+':'+filtro.api.api_implementata.versione
    })
    """
    Given params query
    When method get
    Then status 200
    * match response.items == '#notnull'
    * match response.items[*].api.nome contains setup.erogazione_petstore.api_nome


    * def query_errata =
    """ ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: 'qualsiasi',
        uri_api_implementata: 'gw/'+'AltroSoggetto'+':'+filtro.api.api_implementata.nome+':'+filtro.api.api_implementata.versione
    })
    """
    Given params query_errata
    When method get
    Then status 200
    And assert response.items.length == 0


@RicercaTransazioniApiImplementata
Scenario: RicercaTransazioniApiImplementata tramite richiesta POST con api implementata

    * def filtro = read('classpath:bodies/ricerca-filtro-api-implementata.json')
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.tipo = 'qualsiasi'
    * set filtro.api.api_implementata.nome = setup.erogazione_petstore.api_nome
    * set filtro.api.api_implementata.versione = setup.erogazione_petstore.api_versione  
    * remove filtro.azione
    * set filtro.esito = { 'tipo' : 'ok' }
    * set filtro.limit = 1000

    Given request filtro
    When method post
    Then status 200
    * match response.items == '#notnull'
    * match response.items[*].api.nome contains setup.erogazione_petstore.api_nome


    * set filtro.api.api_implementata.nome = setup.erogazione_petstore.api_nome+'errato'

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length == 0


    * set filtro.api.api_implementata.nome = setup.erogazione_petstore.api_nome
    * set filtro.api.api_implementata.referente = soggettoDefault

    Given request filtro
    When method post
    Then status 200
    * match response.items == '#notnull'
    * match response.items[*].api.nome contains setup.erogazione_petstore.api_nome


    * set filtro.api.api_implementata.referente = 'AltroSoggetto'

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length == 0
