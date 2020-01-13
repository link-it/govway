@parallel=false
Feature: Report della configurazione di GovWay

Background:
    * call read('classpath:crud_commons.feature')
    * def setup = callonce read('classpath:prepare_tests.feature')
    * def configurazioneUrl = monitorUrl + '/reportistica/configurazione-api'
    

    * url configurazioneUrl
    * configure headers = ({ "Authorization": govwayMonitorCred }) 

@RiepilogoApiSoggetto
Scenario: Riepilogo delle API di un Soggetto
    
    * def expected = 
    """
    {
        api: '#number',
        fruizioni: '#number',
        erogazioni: '#number',
        soggetti_dominio_esterno: '#number',
        soggetti_dominio_interno: '##number',
        applicativi: '#number'
    }
    """
    
    Given path 'riepilogo'
    When method get
    Then status 200
    And match response == expected

@RiepilogoApi
Scenario: Riepilogo di una API

    Given path 'riepilogo', 'api'
    And params ({tipo: 'erogazione', nome_servizio: setup.erogazione_petstore.api_nome, versione_servizio: setup.erogazione_petstore.api_versione})
    When method get
    Then status 200
    And match response contains ({ erogazioni: 1})

    Given path 'riepilogo', 'api'
    And params ({tipo: 'fruizione', nome_servizio: setup.fruizione_petstore.api_nome, versione_servizio: setup.fruizione_petstore.api_versione, soggetto_remoto: setup.fruizione_petstore.erogatore })
    When method get
    Then status 200
    And match response contains ({ fruizioni: 1})

@ConfigurazioneApi
Scenario: Recupero Configurazione API

    # Prima faccio una get per recuperare il numero totale di confgurazioni
    Given params ({tipo: 'erogazione'})
    When method get
    Then status 200
    And match response.total == '#number'
    * def total = response.total

    * def expected =
    """
    ({
        erogatore: soggettoDefault,
        nome: setup.erogazione_petstore.api_nome,
        versione: setup.erogazione_petstore.api_versione
    })
    """
    Given params ({tipo: 'erogazione', limit: total})
    When method get
    Then status 200
    And match response.items contains ([expected])

    Given params ({tipo: 'fruizione'})
    When method get
    Then status 200
    And match response.total == '#number'
    * def total = response.total
    * def expected =
    """
    ({
        fruitore: soggettoDefault,
        nome: setup.fruizione_petstore.api_nome,
        versione: setup.fruizione_petstore.api_versione,
        erogatore: setup.fruizione_petstore.erogatore
    })
    """
    Given params ({tipo: 'fruizione', limit: total})
    When method get
    Then status 200
    And match response.items contains ([expected])

@EsportaConfigurazioneApi
Scenario: Esportazione Configurazione API

    Given path 'esporta'
    And params ({tipo: 'erogazione', nome_servizio: setup.erogazione_petstore.api_nome, versione_servizio: setup.erogazione_petstore.api_versione})
    When method get
    Then status 200

    Given path 'esporta'
    And request ({tipo: 'erogazione'})
    When method post
    Then status 200

    Given path 'esporta'
    And request ({tipo: 'fruizione'})
    When method post
    Then status 200
