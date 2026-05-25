@parallel=false
Feature: Verifica permessi sull'endpoint /monitoraggio (ACL auth.aclMonitoraggio: ruoli diagnostica o operatore)

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

    * def transazioniUrl = monitorDirectUrl + '/monitoraggio/transazioni'

    * def body =
    """
    ({
        intervallo_temporale: { data_inizio: setup.dataInizio, data_fine: setup.dataFine },
        tipo: 'erogazione'
    })
    """


@PermessiMonitoraggio
Scenario: Utente con solo OperativitaApi -> 403

    Given url transazioniUrl
    And configure headers = ({ "Authorization": credOperatoreO })
    And request body
    When method POST
    Then status 403


@PermessiMonitoraggio
Scenario: Utente con solo Reportistica -> 403

    Given url transazioniUrl
    And configure headers = ({ "Authorization": credOperatoreR })
    And request body
    When method POST
    Then status 403


@PermessiMonitoraggio
Scenario: Utente con solo Diagnostica -> 200

    Given url transazioniUrl
    And configure headers = ({ "Authorization": credOperatoreD })
    And request body
    When method POST
    Then status 200


@PermessiMonitoraggio
Scenario: Utente con Diagnostica + Reportistica (alias operatore) -> 200

    Given url transazioniUrl
    And configure headers = ({ "Authorization": credOperatoreDR })
    And request body
    When method POST
    Then status 200


@PermessiMonitoraggio
Scenario: Utente con Reportistica + OperativitaApi -> 403

    Given url transazioniUrl
    And configure headers = ({ "Authorization": credOperatoreRO })
    And request body
    When method POST
    Then status 403


@PermessiMonitoraggio
Scenario: Utente con Diagnostica + OperativitaApi -> 200

    Given url transazioniUrl
    And configure headers = ({ "Authorization": credOperatoreDO })
    And request body
    When method POST
    Then status 200


@PermessiMonitoraggio
Scenario: Utente con D + R + OperativitaApi -> 200

    Given url transazioniUrl
    And configure headers = ({ "Authorization": credOperatoreDRO })
    And request body
    When method POST
    Then status 200


@PermessiMonitoraggio
Scenario: Utente config-only (solo Servizi) -> 403

    # L'utenza ha il permesso 'S' (ruolo 'configuratore' per la rs-api-config) ma
    # nessun ruolo monitor: l'ACL aclMonitoraggio richiede 'diagnostica' o 'operatore'.
    Given url transazioniUrl
    And configure headers = ({ "Authorization": credOperatoreS })
    And request body
    When method POST
    Then status 403
