@parallel=false
Feature: Verifica permessi sull'endpoint /reportistica (ACL auth.aclReportistica: ruoli reportistica o operatore)

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

    # Path generico sotto /reportistica/*: NON intercettato dalla ACL piu' specifica
    # auth.aclReportisticaStatoApi (/reportistica/configurazione-api/stato*),
    # quindi viene valutata auth.aclReportistica (roles=reportistica,operatore).
    * def riepilogoUrl = monitorDirectUrl + '/reportistica/configurazione-api/riepilogo'


@PermessiReportistica
Scenario: Utente con solo OperativitaApi -> 403

    Given url riepilogoUrl
    And configure headers = ({ "Authorization": credOperatoreO })
    When method GET
    Then status 403


@PermessiReportistica
Scenario: Utente con solo Reportistica -> 200

    Given url riepilogoUrl
    And configure headers = ({ "Authorization": credOperatoreR })
    When method GET
    Then status 200


@PermessiReportistica
Scenario: Utente con solo Diagnostica -> 403

    Given url riepilogoUrl
    And configure headers = ({ "Authorization": credOperatoreD })
    When method GET
    Then status 403


@PermessiReportistica
Scenario: Utente con Diagnostica + Reportistica (alias operatore) -> 200

    Given url riepilogoUrl
    And configure headers = ({ "Authorization": credOperatoreDR })
    When method GET
    Then status 200


@PermessiReportistica
Scenario: Utente con Reportistica + OperativitaApi -> 200

    Given url riepilogoUrl
    And configure headers = ({ "Authorization": credOperatoreRO })
    When method GET
    Then status 200


@PermessiReportistica
Scenario: Utente con Diagnostica + OperativitaApi -> 403

    Given url riepilogoUrl
    And configure headers = ({ "Authorization": credOperatoreDO })
    When method GET
    Then status 403


@PermessiReportistica
Scenario: Utente con D + R + OperativitaApi -> 200

    Given url riepilogoUrl
    And configure headers = ({ "Authorization": credOperatoreDRO })
    When method GET
    Then status 200


@PermessiReportistica
Scenario: Utente config-only (solo Servizi) -> 403

    # L'utenza ha il permesso 'S' (ruolo 'configuratore' per la rs-api-config) ma
    # nessun ruolo monitor: l'ACL aclReportistica richiede 'reportistica' o 'operatore'.
    Given url riepilogoUrl
    And configure headers = ({ "Authorization": credOperatoreS })
    When method GET
    Then status 403
