Feature: Ricerca per identificativo applicativo

Background:
    
    * call read('classpath:crud_commons.feature')
    * def setup = callonce read('classpath:prepare_tests.feature')
    * configure afterFeature = function(){ karate.call('classpath:cleanup_tests.feature'); }

    * def ricercaUrl = monitorUrl + '/monitoraggio/transazioni/id_applicativo'    
    * def intervallo_temporale = ({ data_inizio: setup.dataInizio, data_fine: setup.dataFine })

    * def filtro = read('classpath:bodies/ricerca-identificativo-applicativo.json')
    * eval filtro.api.nome = setup.erogazione_petstore.api_nome
    * eval filtro.api.versione = setup.erogazione_petstore.api_versione
    * eval filtro.id_applicativo.id = "prova"
    * eval filtro.intervallo_temporale = intervallo_temporale

@Completa+Semplice
Scenario: Ricerca per identificativo applicativo (Correlazione Applicativa)
    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length == 1

    * def risposta1 = response

    * def query =
    """ ({ 
        data_inizio: filtro.intervallo_temporale.data_inizio, 
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: filtro.tipo,
        nome_servizio: filtro.api.nome,
        versione_servizio: filtro.api.versione,
        id_applicativo: filtro.id_applicativo.id,
        case_sensitive: filtro.id_applicativo.case_sensitive,
        ricerca_esatta: filtro.id_applicativo.ricerca_esatta 
    })
    """

    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And params query
    When method get
    Then status 200
    And assert response.items.length == risposta1.items.length

    # Caso Fruizioni

    * set filtro.api.erogatore = setup.erogatore.nome
    * set filtro.tipo = 'fruizione'

    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And request filtro
    When method post
    Then status 200
    And assert response.items.length == 1

    * def query =
    """ ({ 
        data_inizio: filtro.intervallo_temporale.data_inizio, 
        data_fine: filtro.intervallo_temporale.data_fine,
        tipo: filtro.tipo,
        nome_servizio: filtro.api.nome,
        soggetto_remoto: filtro.api.erogatore,
        versione_servizio: filtro.api.versione,
        id_applicativo: filtro.id_applicativo.id,
        case_sensitive: filtro.id_applicativo.case_sensitive,
        ricerca_esatta: filtro.id_applicativo.ricerca_esatta 
    })
    """

    Given url ricercaUrl
    And header Authorization = govwayMonitorCred
    And params query
    When method get
    Then status 200


    
