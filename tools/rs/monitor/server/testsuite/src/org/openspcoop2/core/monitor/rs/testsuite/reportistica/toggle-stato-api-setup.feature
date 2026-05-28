@ignore
Feature: Setup gruppo+connettore api-monitor per autenticare le chiamate di toggle stato API

# Il proxy GovWay che fronteggia api-monitor scarta l'auth client e inoltra al backend
# con il proprio service-account ('operatore'). Quel service-account non ha il ruolo
# 'operativitaApi' richiesto dall'ACL backend per le operazioni di toggle stato API,
# quindi qualsiasi chiamata via proxy riceverebbe 403.
#
# Risolviamo creando sull'erogazione api-monitor un gruppo dedicato (in ereditarietà
# dal predefinito) limitato alle sole azioni di toggle, con un connettore che inietta
# le credenziali di un utente operativitaApi-ready ('operatoreO'). Tutte le altre
# azioni dell'erogazione restano sul connettore principale (non vengono toccate).

Background:
* def basic = read('classpath:basic-auth.js')
* def govwayConfAuth = call basic configCred

Scenario: Crea gruppo dedicato e connettore con credenziali operativitaApi

    * def apiMonitorErogazionePath = 'erogazioni/api-monitor/1'
    * def filterPredefinito = function(item){ return item.predefinito == true }

    # 1. Recupera il nome del gruppo predefinito dell'erogazione api-monitor
    Given url configUrl
    And path apiMonitorErogazionePath, 'gruppi'
    And params ({ soggetto: soggettoDefault })
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    * def apiMonitorGruppoPredefinitoNome = karate.filter(response.items, filterPredefinito)[0].nome

    # 2. Recupera il connettore principale dell'erogazione (per replicarne l'endpoint)
    Given url configUrl
    And path apiMonitorErogazionePath, 'connettore'
    And params ({ soggetto: soggettoDefault })
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    * def apiMonitorConnettoreBase = response.connettore

    # 3. Crea il nuovo gruppo in ereditarietà dal predefinito, limitato alle azioni di toggle
    * def toggleAuthGruppo =
    """
    ({
        nome: 'ToggleStatoAuthGruppo',
        azioni: [ 'POST_reportistica.configurazione-api.stato', 'PUT_reportistica.configurazione-api.stato' ],
        configurazione: { modalita: 'eredita', nome: apiMonitorGruppoPredefinitoNome }
    })
    """
    Given url configUrl
    And path apiMonitorErogazionePath, 'gruppi'
    And params ({ soggetto: soggettoDefault })
    And header Authorization = govwayConfAuth
    And request toggleAuthGruppo
    When method post
    Then status 201

    # 4. Sovrascrive il connettore per il gruppo: stesso endpoint del default + basic auth operatoreO
    * def connettoreConAuth = karate.merge(apiMonitorConnettoreBase, { autenticazione_http: { username: 'operatoreO', password: '123456' } })
    * def connettoreBody = ({ connettore: connettoreConAuth })

    Given url configUrl
    And path apiMonitorErogazionePath, 'connettore'
    And params ({ soggetto: soggettoDefault, gruppo: 'ToggleStatoAuthGruppo' })
    And header Authorization = govwayConfAuth
    And request connettoreBody
    When method put
    Then status 204
