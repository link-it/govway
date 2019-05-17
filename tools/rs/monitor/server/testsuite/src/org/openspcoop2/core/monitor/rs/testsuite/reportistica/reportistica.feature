Feature: Reportistica e esportazione grafici

Background:
    
    * call read('classpath:crud_commons.feature')
    * def setup = callonce read('classpath:prepare_tests.feature')
    * configure afterFeature = function(){ karate.call('classpath:cleanup_tests.feature'); }

    * def reportisticaUrl = monitorUrl + '/reportistica/analisi-statistica'
    * def configurazioneUrl = monitorUrl + '/reportistica/configurazione-api'

    * def tmp = read('classpath:bodies/reportistica-andamento-temporale.json')
    * def intervallo_temporale = ({ data_inizio: setup.dataInizioMinuteZero, data_fine: setup.dataFine })

    * def write_csv =
    """
    function fn(v) {
        karate.write(v,"temp.csv");
    }
    """

    
@DistribuzioneTemporaleApi:
Scenario: Statistiche Per Distribuzione Temporale con filtraggio per API
    * def filtro = read('classpath:bodies/reportistica-andamento-temporale.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * eval filtro.intervallo_temporale = intervallo_temporale

    Given url reportisticaUrl
    And path 'distribuzione-temporale'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200

    * def query =
    """
    ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: filtro.tipo,
        nome_servizio: filtro.api.nome,
        versione_servizio: filtro.api.versione,
        esito: filtro.esito.tipo,
        formato_report: filtro.report.formato,
        unita_tempo: filtro.unita_tempo,
        tipo_report: filtro.report.tipo,
        tipo_informazione_report: filtro.report.tipo_informazione.tipo
    })
    """    
    
    Given url reportisticaUrl
    And path 'distribuzione-temporale'
    And header Authorization = govwayMonitorCred
    And params query
    When method get
    Then status 200

@DistribuzioneTemporaleIdAutenticatoHttp
Scenario: Statistiche Per Distribuzione Temporale con filtraggio per autenticazione http
    * def filtro = read('classpath:bodies/reportistica-andamento-temporale-filtro-idautenticato.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale
    * eval filtro.mittente.id.id = setup.applicativo.credenziali.username
    
    Given url reportisticaUrl
    And path 'distribuzione-temporale'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200


@DistribuzioneTemporaleIdAutenticatoPrincipal
Scenario: Statistiche Per Distribuzione Temporale con filtraggio per Filtro Mittente con autenticazione principal
    * def filtro = read('classpath:bodies/reportistica-andamento-temporale-filtro-idautenticato.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.mittente.id = ({ id: setup.applicativo_principal.credenziali.userid, autenticazione: 'principal' })
    * set filtro.intervallo_temporale = intervallo_temporale

    Given url reportisticaUrl
    And path 'distribuzione-temporale'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200

@DistribuzioneTemporaleFiltroMittenteApplicativo
Scenario: Statistiche Per Distribuzione Temporale con filtraggio per Filtro Mittente Applicativo
    * def filtro = read('classpath:bodies/reportistica-andamento-temporale-filtro-mittente-applicativo.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.mittente.id = ({ soggetto: soggettoDefault, applicativo: setup.applicativo.nome})

    Given url reportisticaUrl
    And path 'distribuzione-temporale'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200

@DistribuzioneTemporaleFiltroMittenteTokenInfo
Scenario: Statistiche Per Distribuzione Temporale con filtraggio per Token Info
    * def filtro = read('classpath:bodies/reportistica-andamento-temporale-filtro-mittente-applicativo.json')
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.mittente = ({ tipo: 'token_info', id: { id: setup.claims.username, claim: 'username'}})

    Given url reportisticaUrl
    And path 'distribuzione-temporale'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200

    * set filtro.mittente = ({ tipo: 'token_info', id: { id: setup.claims.issuer, claim: 'issuer'}})
    Given url reportisticaUrl
    And path 'distribuzione-temporale'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200

    * set filtro.mittente = ({ tipo: 'token_info', id: { id: setup.claims.subject, claim: 'subject'}})
    Given url reportisticaUrl
    And path 'distribuzione-temporale'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200

    * set filtro.mittente = ({ tipo: 'token_info', id: { id: setup.claims.email, claim: 'email'}})
    Given url reportisticaUrl
    And path 'distribuzione-temporale'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200

    * set filtro.mittente = ({ tipo: 'token_info', id: { id: setup.claims.client_id, claim: 'client_id'}})
    Given url reportisticaUrl
    And path 'distribuzione-temporale'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200

    

@DistribuzioneTemporaleFiltroMittenteSoggetto
Scenario: Statistiche Per Distribuzione Temporale con filtraggio per Filtro Mittente Soggetto
    * def filtro = read('classpath:bodies/reportistica-andamento-temporale-filtro-mittente-applicativo.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.mittente.id = ({ soggetto: setup.soggetto_http.nome })
    * eval filtro.mittente.tipo  = 'soggetto'

    Given url reportisticaUrl
    And path 'distribuzione-temporale'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200


 @DistribuzioneEsiti
 Scenario:  Statistiche Per Distribuzione Esiti
     * def filtro = read('classpath:bodies/reportistica-esiti.json')
     * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
     * set filtro.intervallo_temporale = intervallo_temporale
     * set filtro.mittente = ({ tipo:'applicativo', id:{ soggetto: soggettoDefault, applicativo: setup.applicativo.nome } } )

     Given url reportisticaUrl
     And path 'distribuzione-esiti'
     And header Authorization = govwayMonitorCred
     And request filtro
     When method post
     Then status 200

    * def query =
    """
    ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: filtro.tipo,
        nome_servizio: filtro.api.nome,
        versione_servizio: filtro.api.versione,
        formato_report: filtro.report.formato,
        unita_tempo: filtro.unita_tempo,
        tipo_report: filtro.report.tipo,
        tipo_informazione_report: filtro.report.tipo_informazione.tipo
    })
    """    
    
    Given url reportisticaUrl
    And path 'distribuzione-esiti'
    And header Authorization = govwayMonitorCred
    And params query
    When method get
    Then status 200
  

@DistribuzioneSoggettoRemoto
Scenario:  Statistiche Per Distribuzione SoggettoRemoto
    * def filtro = read('classpath:bodies/reportistica-soggetto-remoto.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.mittente = ({ tipo: 'identificativo_autenticato', id: {id: setup.applicativo_principal.credenziali.userid, autenticazione: 'principal' } })

    Given url reportisticaUrl
    And path 'distribuzione-soggetto-remoto'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200

     * def query =
    """
    ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: filtro.tipo,
        nome_servizio: filtro.api.nome,
        versione_servizio: filtro.api.versione,
        formato_report: filtro.report.formato,
        unita_tempo: filtro.unita_tempo,
        tipo_report: filtro.report.tipo,
        tipo_informazione_report: filtro.report.tipo_informazione.tipo,
        esito: filtro.esito.tipo
    })
    """    
    
    Given url reportisticaUrl
    And path 'distribuzione-soggetto-remoto'
    And header Authorization = govwayMonitorCred
    And params query
    When method get
    Then status 200
  

@DistribuzioneSoggettoLocale
Scenario:  Statistiche Per Distribuzione SoggettoLocale
    * def filtro = read('classpath:bodies/reportistica-soggetto-locale.json')
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})

    Given url reportisticaUrl
    And path 'distribuzione-soggetto-locale'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200

    * def query =
    """
    ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: filtro.tipo,
        nome_servizio: filtro.api.nome,
        versione_servizio: filtro.api.versione,
        formato_report: filtro.report.formato,
        unita_tempo: filtro.unita_tempo,
        tipo_report: filtro.report.tipo,
        tipo_informazione_report: filtro.report.tipo_informazione.tipo,
        esito: filtro.esito.tipo
    })
    """    
    
    Given url reportisticaUrl
    And path 'distribuzione-soggetto-locale'
    And header Authorization = govwayMonitorCred
    And params query
    When method get
    Then status 200

@DistribuzioneApi
Scenario:  Statistiche Per Distribuzione API
    * def filtro = read('classpath:bodies/reportistica-distribuzione-api.json')
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.mittente = ({ tipo: 'identificativo_autenticato', id: {id: setup.applicativo_principal.credenziali.userid, autenticazione: 'principal' } })

    Given url reportisticaUrl
    And path 'distribuzione-api'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200

    * def query =
    """
    ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: filtro.tipo,
        formato_report: filtro.report.formato,
        unita_tempo: filtro.unita_tempo,
        tipo_report: filtro.report.tipo,
        tipo_informazione_report: filtro.report.tipo_informazione.tipo,
        esito: filtro.esito.tipo
    })
    """    
    
    Given url reportisticaUrl
    And path 'distribuzione-api'
    And header Authorization = govwayMonitorCred
    And params query
    When method get
    Then status 200


@DistribuzioneAzione
Scenario:  Statistiche Per Distribuzione Azione
    * def filtro = read('classpath:bodies/reportistica-distribuzione-azione.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.mittente = ({ tipo: 'identificativo_autenticato', id: {id: setup.applicativo_principal.credenziali.userid, autenticazione: 'principal' } })
    
    Given url reportisticaUrl
    And path 'distribuzione-azione'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200    

    * def query =
    """
    ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: filtro.tipo,
        formato_report: filtro.report.formato,
        unita_tempo: filtro.unita_tempo,
        tipo_report: filtro.report.tipo,
        tipo_informazione_report: filtro.report.tipo_informazione.tipo,
        esito: filtro.esito.tipo
    })
    """    
    
    Given url reportisticaUrl
    And path 'distribuzione-azione'
    And header Authorization = govwayMonitorCred
    And params query
    When method get
    Then status 200


@DistribuzioneApplicativo
Scenario: Statistiche Per Distribuzione Applicativo
    * def filtro = read('classpath:bodies/reportistica-distribuzione-applicativo.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale

    Given url reportisticaUrl
    And path 'distribuzione-applicativo'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200

    * def query =
    """
    ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: filtro.tipo,
        formato_report: filtro.report.formato,
        unita_tempo: filtro.unita_tempo,
        tipo_report: filtro.report.tipo,
        tipo_informazione_report: filtro.report.tipo_informazione.tipo,
        esito: filtro.esito.tipo
    })
    """    
    
    Given url reportisticaUrl
    And path 'distribuzione-applicativo'
    And header Authorization = govwayMonitorCred
    And params query
    When method get
    Then status 200


@DistribuzioneTokenInfo
Scenario: Statistiche per Distribuzione Token Info
    * def filtro = read('classpath:bodies/reportistica-distribuzione-tokeninfo.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale

    Given url reportisticaUrl
    And path 'distribuzione-token-info'
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200

    * def query =
    """
    ({
        data_inizio: filtro.intervallo_temporale.data_inizio,
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: filtro.tipo,
        formato_report: filtro.report.formato,
        unita_tempo: filtro.unita_tempo,
        tipo_report: filtro.report.tipo,
        tipo_informazione_report: filtro.report.tipo_informazione.tipo,
        esito: filtro.esito.tipo,
        claim: filtro.claim
    })
    """    
    
    Given url reportisticaUrl
    And path 'distribuzione-token-info'
    And header Authorization = govwayMonitorCred
    And params query
    When method get
    Then status 200

@RiepilogoApiSoggetto
Scenario: Riepilogo delle API di un Soggetto
    Given url configurazioneUrl
    And path 'riepilogo'
    And header Authorization = govwayMonitorCred
    When method get
    Then status 200

@RiepilogoApi
Scenario: Riepilogo di una API
    Given url configurazioneUrl
    And path 'riepilogo', 'api'
    And header Authorization = govwayMonitorCred
    And params ({tipo: 'erogazione', nome_servizio: setup.erogazione_petstore.api_nome, versione_servizio: setup.erogazione_petstore.api_versione})
    When method get
    Then status 200

@ConfigurazioneApi
Scenario: Recupero Configurazione API
    Given url configurazioneUrl
    And header Authorization = govwayMonitorCred
    And params ({tipo: 'erogazione'})
    When method get
    Then status 200

@EsportaConfigurazioneApi
Scenario: Esportazione Configurazione API
    Given url configurazioneUrl
    And path 'esporta'
    And header Authorization = govwayMonitorCred
    And params ({tipo: 'erogazione', nome_servizio: setup.erogazione_petstore.api_nome, versione_servizio: setup.erogazione_petstore.api_versione})
    When method get
    Then status 200

    Given url configurazioneUrl
    And path 'esporta'
    And header Authorization = govwayMonitorCred
    And request ({tipo: 'erogazione'})
    When method post
    Then status 200