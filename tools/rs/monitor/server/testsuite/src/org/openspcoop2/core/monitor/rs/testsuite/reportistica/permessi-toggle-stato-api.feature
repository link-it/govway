@parallel=false
Feature: Verifica permessi sull'endpoint di toggle stato API (Operativita' API)

Background:

    * call read('classpath:crud_commons.feature')
    * def setup = callonce read('classpath:prepare_tests.feature')
    * configure afterFeature = function(){ karate.call('classpath:cleanup_tests.feature'); }

    # I 7 utenti vengono creati automaticamente da prepare_tests.feature (sezione "Seed utenti di test")
    # e rimossi al termine da cleanup_tests.feature.
    * def credOperatoreO   = call basic ({ username: 'operatoreO',   password: '123456' })
    * def credOperatoreR   = call basic ({ username: 'operatoreR',   password: '123456' })
    * def credOperatoreD   = call basic ({ username: 'operatoreD',   password: '123456' })
    * def credOperatoreDR  = call basic ({ username: 'operatoreDR',  password: '123456' })
    * def credOperatoreRO  = call basic ({ username: 'operatoreRO',  password: '123456' })
    * def credOperatoreDO  = call basic ({ username: 'operatoreDO',  password: '123456' })
    * def credOperatoreDRO = call basic ({ username: 'operatoreDRO', password: '123456' })

    * def statoApiUrl = monitorUrl + '/reportistica/configurazione-api/stato'

    * def body =
    """
    ({
        tipo: 'EROGAZIONE',
        abilitato: true,
        api: { nome: setup.api_petstore.nome, versione: setup.api_petstore.versione }
    })
    """


@PermessiToggleStato
Scenario: Utente con solo OperativitaApi -> 200

    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreO })
    And request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 200


@PermessiToggleStato
Scenario: Utente con solo Reportistica -> 403

    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreR })
    And request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 403


@PermessiToggleStato
Scenario: Utente con solo Diagnostica -> 403

    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreD })
    And request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 403


@PermessiToggleStato
Scenario: Utente con Diagnostica + Reportistica (senza OperativitaApi) -> 403

    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreDR })
    And request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 403


@PermessiToggleStato
Scenario: Utente con Reportistica + OperativitaApi -> 200

    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreRO })
    And request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 200


@PermessiToggleStato
Scenario: Utente con Diagnostica + OperativitaApi -> 200

    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreDO })
    And request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 200


@PermessiToggleStato
Scenario: Utente con D + R + OperativitaApi -> 200

    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreDRO })
    And request body
    And param soggetto = soggettoDefault
    When method PUT
    Then status 200
