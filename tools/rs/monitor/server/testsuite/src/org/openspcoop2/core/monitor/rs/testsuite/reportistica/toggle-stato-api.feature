@parallel=false
Feature: Toggle stato di una erogazione o fruizione (Operativita' API)

Background:

    * call read('classpath:crud_commons.feature')
    * def setup = callonce read('classpath:prepare_tests.feature')
    * configure afterFeature = function(){ karate.call('classpath:cleanup_tests.feature'); }

    * def statoApiUrl = monitorUrl + '/reportistica/configurazione-api/stato'

    * def operatoreCred = call basic ({ username: 'operatore', password: '123456' })

    * url statoApiUrl
    * configure headers = ({ "Authorization": operatoreCred })


@ToggleStatoApi
Scenario: Disabilita e riabilita una erogazione via full search

    * def body =
    """
    ({
        tipo: 'EROGAZIONE',
        abilitato: false,
        api: {
            nome: setup.api_petstore.nome,
            versione: setup.api_petstore.versione
        }
    })
    """

    # disabilita
    Given request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 200

    # riabilita
    * set body.abilitato = true
    Given request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 200


@ToggleStatoApi
Scenario: Toggle stato di una fruizione via full search

    * def body =
    """
    ({
        tipo: 'FRUIZIONE',
        abilitato: false,
        api: {
            nome: setup.fruizione_petstore.api_nome,
            versione: setup.fruizione_petstore.api_versione,
            erogatore: setup.fruizione_petstore.erogatore
        }
    })
    """

    Given request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 200


@ToggleStatoApi
Scenario: Toggle stato by simple search (erogazione, tutti i gruppi)

    Given param tipo = 'EROGAZIONE'
    And param abilitato = false
    And param nome_servizio = setup.api_petstore.nome
    And param versione_servizio = setup.api_petstore.versione
    And param soggetto = soggettoDefault
    When method PUT
    Then status 200

    Given param tipo = 'EROGAZIONE'
    And param abilitato = true
    And param nome_servizio = setup.api_petstore.nome
    And param versione_servizio = setup.api_petstore.versione
    And param soggetto = soggettoDefault
    When method PUT
    Then status 200


@ToggleStatoApi
Scenario: Toggle stato by simple search (fruizione)

    Given param tipo = 'FRUIZIONE'
    And param abilitato = false
    And param nome_servizio = setup.fruizione_petstore.api_nome
    And param versione_servizio = setup.fruizione_petstore.api_versione
    And param soggetto_remoto = setup.fruizione_petstore.erogatore
    And param soggetto = soggettoDefault
    When method PUT
    Then status 200


@ToggleStatoApi
Scenario: API inesistente => 404

    * def body =
    """
    ({
        tipo: 'EROGAZIONE',
        abilitato: true,
        api: { nome: 'api-non-esistente-' + random(), versione: 1 }
    })
    """

    Given request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 404


@ToggleStatoApi
Scenario: Body invalido (manca tipo) => 400

    * def body = ({ abilitato: true, api: { nome: setup.api_petstore.nome, versione: setup.api_petstore.versione } })

    Given request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 400


@ToggleStatoApi
Scenario: Idempotenza - disabilita due volte di seguito

    * def body =
    """
    ({
        tipo: 'EROGAZIONE',
        abilitato: false,
        api: { nome: setup.api_petstore.nome, versione: setup.api_petstore.versione }
    })
    """

    Given request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 200

    Given request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 200

    # riabilita per cleanup
    * set body.abilitato = true
    Given request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 200
