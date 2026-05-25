@parallel=false
Feature: Verifica permessi sull'endpoint di toggle stato API (Operativita' API)

Background:

    * call read('classpath:crud_commons.feature')
    * def setup = callonce read('classpath:prepare_tests.feature')
    * configure afterFeature = function(){ karate.call('classpath:cleanup_tests.feature'); }

    # I 7 utenti vengono creati automaticamente da prepare_tests.feature (sezione "Seed utenti di test")
    # e rimossi al termine da cleanup_tests.feature.
    * def credOperatoreO = basic({ username: 'operatoreO', password: '123456' })
    * def credOperatoreR = basic({ username: 'operatoreR', password: '123456' })
    * def credOperatoreD = basic({ username: 'operatoreD', password: '123456' })
    * def credOperatoreDR = basic({ username: 'operatoreDR', password: '123456' })
    * def credOperatoreRO = basic({ username: 'operatoreRO', password: '123456' })
    * def credOperatoreDO = basic({ username: 'operatoreDO', password: '123456' })
    * def credOperatoreDRO = basic({ username: 'operatoreDRO', password: '123456' })
    # Utenza config-only (permesso 'S' = Servizi -> ruolo 'configuratore', nessun ruolo monitor)
    * def credOperatoreS = basic({ username: 'operatoreS', password: '123456' })

    * def statoApiUrl = monitorDirectUrl + '/reportistica/configurazione-api/stato'

    * def body =
    """
    ({
        tipo: 'erogazione',
        abilitato: true,
        api: { nome: setup.api_petstore.nome, versione: setup.api_petstore.versione }
    })
    """


@PermessiToggleStato
Scenario: Utente con solo OperativitaApi -> 204

    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreO })
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204


@PermessiToggleStato
Scenario: Utente con solo Reportistica -> 403

    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreR })
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 403


@PermessiToggleStato
Scenario: Utente con solo Diagnostica -> 403

    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreD })
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 403


@PermessiToggleStato
Scenario: Utente con Diagnostica + Reportistica (senza OperativitaApi) -> 403

    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreDR })
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 403


@PermessiToggleStato
Scenario: Utente con Reportistica + OperativitaApi -> 204

    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreRO })
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204


@PermessiToggleStato
Scenario: Utente con Diagnostica + OperativitaApi -> 204

    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreDO })
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204


@PermessiToggleStato
Scenario: Utente con D + R + OperativitaApi -> 204

    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreDRO })
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204


@PermessiToggleStato
Scenario: Utente config-only (solo Servizi) -> 403

    # L'utenza ha il permesso 'S' (ruolo 'configuratore' per la rs-api-config) ma
    # nessun ruolo monitor: l'ACL aclReportisticaStatoApi richiede 'operativitaApi'.
    Given url statoApiUrl
    And configure headers = ({ "Authorization": credOperatoreS })
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 403
