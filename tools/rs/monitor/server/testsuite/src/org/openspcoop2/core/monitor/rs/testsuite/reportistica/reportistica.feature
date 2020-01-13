@parallel=false
Feature: Reportistica e esportazione grafici

Background:

    * call read('classpath:crud_commons.feature')
    * def setup = callonce read('classpath:prepare_tests.feature')
#    * configure afterFeature = function(){ karate.call('classpath:cleanup_tests.feature'); }    

    # Rimuovo la lock e do il tempo all'engine di preparare le statistiche.
    * print 'Rimuovo La lock sul db..'
    * callonce setup.delete_lock(setup.db)
    # TODO: Ora che dedichiamo un applicative_id a ciascun intervallo  orario in op2_semaphore, potremmo invece che attendere indefinitamente,
    # rilevare che questa lock sia stata presa e rilasciata almeno una volta.

    * print 'Attendo che vengano generate le statistiche'
    * callonce pause(statsInterval)
    * print 'Attendo di ottenere la lock'
    * callonce setup.wait_for_lock(setup.db);    

    * def reportisticaUrl = monitorUrl + '/reportistica/analisi-statistica'
    
    * def intervallo_temporale = ({ data_inizio: setup.dataInizioMinuteZero, data_fine: getDate() })

    * url reportisticaUrl
    * configure headers = ({ "Authorization": govwayMonitorCred }) 

@DistribuzioneTemporaleFiltroMittenteTokenInfo
Scenario: Ricerca di transazioni filtrate per Token claim
    * call read('classpath:andamento-temporale-filtro-mittente-token-info.feature');

@DistribuzioneTemporaleApi
Scenario: Statistiche Per Distribuzione Temporale con filtraggio per API
    
    * def filtro = read('classpath:bodies/reportistica-andamento-temporale.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale
    Given path 'distribuzione-temporale'
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
    Given path 'distribuzione-temporale'
    And params query
    When method get
    Then status 200

    * set filtro.tipo = 'fruizione'
    * set filtro.api = ({nome: setup.fruizione_petstore.api_nome, versione: setup.fruizione_petstore.api_versione, erogatore: setup.fruizione_petstore.erogatore })
    Given path 'distribuzione-temporale'
    And request filtro
    When method post
    Then status 200

    * set query.tipo = 'fruizione'
    * set query.soggetto_remoto = filtro.api.erogatore
    * set query.nome_servizio = filtro.api.nome
    * set query.versione_servizio = filtro.api.versione

    Given path 'distribuzione-temporale'
    And params query
    When method get
    Then status 200
    

@DistribuzioneTemporaleIdAutenticatoHttp
Scenario: Statistiche Per Distribuzione Temporale con filtraggio per autenticazione http
    * def filtro = read('classpath:bodies/reportistica-andamento-temporale-filtro-idautenticato.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale
    * eval filtro.mittente.id.id = setup.applicativo.credenziali.username
    
    Given path 'distribuzione-temporale'
    And request filtro
    When method post
    Then status 200

    * set filtro.tipo = 'fruizione'
    * set filtro.api.erogatore = setup.fruizione_petstore.erogatore
    Given path 'distribuzione-temporale'
    And request filtro
    When method post
    Then status 200   


@DistribuzioneTemporaleIdAutenticatoPrincipal
Scenario: Statistiche Per Distribuzione Temporale con filtraggio per Filtro Mittente con autenticazione principal
    * def filtro = read('classpath:bodies/reportistica-andamento-temporale-filtro-idautenticato.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.mittente.id = ({ id: setup.applicativo_principal.credenziali.userid, autenticazione: 'principal' })
    * set filtro.intervallo_temporale = intervallo_temporale
    Given path 'distribuzione-temporale'
    And request filtro
    When method post
    Then status 200

    * set filtro.tipo = 'fruizione'
    * set filtro.api.erogatore = setup.fruizione_petstore.erogatore
    Given path 'distribuzione-temporale'
    And request filtro
    When method post
    Then status 200   

@DistribuzioneTemporaleFiltroMittenteApplicativo
Scenario: Statistiche Per Distribuzione Temporale con filtraggio per Filtro Mittente Applicativo
    * def filtro = read('classpath:bodies/reportistica-andamento-temporale-filtro-mittente-applicativo.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.mittente.id = ({ soggetto: soggettoDefault, applicativo: setup.applicativo.nome})

    Given path 'distribuzione-temporale'
    And request filtro
    When method post
    Then status 200

    * set filtro.tipo = 'fruizione'
    * set filtro.api.erogatore = setup.fruizione_petstore.erogatore
    * set filtro.mittente.id = ({ applicativo: setup.applicativo.nome})
    Given path 'distribuzione-temporale'
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

    Given path 'distribuzione-temporale'
    And request filtro
    When method post
    Then status 200


 @DistribuzioneEsiti
 Scenario:  Statistiche Per Distribuzione Esiti
     * def filtro = read('classpath:bodies/reportistica-esiti.json')
     * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
     * set filtro.intervallo_temporale = intervallo_temporale
     * set filtro.mittente = ({ tipo:'applicativo', id:{ soggetto: soggettoDefault, applicativo: setup.applicativo.nome } } )
     Given path 'distribuzione-esiti'
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
    Given path 'distribuzione-esiti'
    And params query
    When method get
    Then status 200

    * set filtro.tipo = 'fruizione'
    * set filtro.api.erogatore = setup.fruizione_petstore.erogatore
    * set filtro.mittente.id = ({ applicativo: setup.applicativo.nome })
    Given path 'distribuzione-esiti'
    And request filtro
    When method post
    Then status 200

    * set query.tipo = 'fruizione'
    * set query.soggetto_remoto = filtro.api.erogatore  
    Given path 'distribuzione-esiti'
    And params query
    When method get
    Then status 200

@DistribuzioneSoggettoRemoto
Scenario:  Statistiche Per Distribuzione SoggettoRemoto
    * def filtro = read('classpath:bodies/reportistica-soggetto-remoto.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale
    Given path 'distribuzione-soggetto-remoto'
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
    Given path 'distribuzione-soggetto-remoto'
    And params query
    When method get
    Then status 200

    * set filtro.tipo = 'fruizione'
    * set filtro.api.erogatore = setup.fruizione_petstore.erogatore
    Given path 'distribuzione-soggetto-remoto'
    And request filtro
    When method post
    Then status 200

    * set query.tipo = 'fruizione'
    * set query.soggetto_erogatore = filtro.api.erogatore
    Given path 'distribuzione-soggetto-remoto'
    And params query
    When method get
    Then status 200
  

@DistribuzioneSoggettoLocale
Scenario:  Statistiche Per Distribuzione SoggettoLocale
    * def filtro = read('classpath:bodies/reportistica-soggetto-locale.json')
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    Given path 'distribuzione-soggetto-locale'
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
    Given path 'distribuzione-soggetto-locale'
    And params query
    When method get
    Then status 200

    * set filtro.tipo = 'fruizione'
    * set filtro.api.erogatore = setup.fruizione_petstore.erogatore
    Given path 'distribuzione-soggetto-locale'
    And request filtro
    When method post
    Then status 200

    * set query.tipo = 'fruizione'
    * set query.soggetto_remoto = filtro.api.erogatore
    Given path 'distribuzione-soggetto-locale'
    And params query
    When method get
    Then status 200

@DistribuzioneApi
Scenario:  Statistiche Per Distribuzione API
    * def filtro = read('classpath:bodies/reportistica-distribuzione-api.json')
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.mittente = 
    """
    ({
        tipo: 'identificativo_autenticato', 
        id: {id: setup.applicativo_principal.credenziali.userid, autenticazione: 'principal' } 
    })
    """
    Given path 'distribuzione-api'
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
    Given path 'distribuzione-api'
    And params query
    When method get
    Then status 200

    * set filtro.tipo = 'fruizione'
    Given path 'distribuzione-api'
    And request filtro
    When method post
    Then status 200

    * set query.tipo = 'fruizione'
    Given path 'distribuzione-api'
    And params query
    When method get
    Then status 200


@DistribuzioneAzione
Scenario:  Statistiche Per Distribuzione Azione
    * def filtro = read('classpath:bodies/reportistica-distribuzione-azione.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale
    * set filtro.mittente = ({ tipo: 'identificativo_autenticato', id: {id: setup.applicativo_principal.credenziali.userid, autenticazione: 'principal' } })

    Given path 'distribuzione-azione'
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
        nome_servizio: filtro.api.nome,
        versione_servizio: filtro.api.versione
    })
    """    
    Given path 'distribuzione-azione'
    And params query
    When method get
    Then status 200

    * set filtro.tipo = 'fruizione'
    * set filtro.api.erogatore = setup.fruizione_petstore.erogatore
    Given path 'distribuzione-azione'
    And request filtro
    When method post
    Then status 200    

    * set query.tipo = 'fruizione'
    * set query.soggetto_remoto = filtro.api.erogatore
    Given path 'distribuzione-azione'
    And params query
    When method get
    Then status 200


@DistribuzioneApplicativo
Scenario: Statistiche Per Distribuzione Applicativo
    * def filtro = read('classpath:bodies/reportistica-distribuzione-applicativo.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale

    Given path 'distribuzione-applicativo'
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
        nome_servizio: filtro.api.nome,
        versione_servizio: filtro.api.versione
    })
    """    
    Given path 'distribuzione-applicativo'
    And params query
    When method get
    Then status 200

    * set filtro.tipo = 'fruizione'
    * set filtro.api.erogatore = setup.fruizione_petstore.erogatore
    Given path 'distribuzione-applicativo'
    And request filtro
    When method post
    Then status 200    

    * set query.tipo = 'fruizione'
    * set query.soggetto_remoto = filtro.api.erogatore
    Given path 'distribuzione-applicativo'
    And params query
    When method get
    Then status 200



@DistribuzioneTokenInfo
Scenario: Statistiche per Distribuzione Token Info
    * def filtro = read('classpath:bodies/reportistica-distribuzione-tokeninfo.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale

    Given path 'distribuzione-token-info'
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
        claim: filtro.claim,
        nome_servizio: filtro.api.nome,
        versione_servizio: filtro.api.versione
    })
    """    
    Given path 'distribuzione-token-info'
    And params query
    When method get
    Then status 200

    * set filtro.tipo = 'fruizione'
    * set filtro.api.erogatore = setup.fruizione_petstore.erogatore
    Given path 'distribuzione-token-info'
    And request filtro
    When method post
    Then status 200    

    * set query.tipo = 'fruizione'
    * set query.soggetto_remoto = filtro.api.erogatore
    Given path 'distribuzione-token-info'
    And params query
    When method get
    Then status 200
    
@DistribuzioneIndirizzoIP
Scenario: Statistiche per Distribuzione Indirizzo IP
    * def filtro = read('classpath:bodies/reportistica-distribuzione-applicativo.json')
    * set filtro.api = ({nome: setup.erogazione_petstore.api_nome, versione: setup.erogazione_petstore.api_versione})
    * set filtro.intervallo_temporale = intervallo_temporale

    Given path 'distribuzione-indirizzo-ip'
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
        nome_servizio: filtro.api.nome,
        versione_servizio: filtro.api.versione
    })
    """    
    Given path 'distribuzione-indirizzo-ip'
    And params query
    When method get
    Then status 200

    * set filtro.tipo = 'fruizione'
    * set filtro.api.erogatore = setup.fruizione_petstore.erogatore
    Given path 'distribuzione-indirizzo-ip'
    And request filtro
    When method post
    Then status 200    

    * set query.tipo = 'fruizione'
    * set query.soggetto_remoto = filtro.api.erogatore
    Given path 'distribuzione-indirizzo-ip'
    And params query
    When method get
    Then status 200

