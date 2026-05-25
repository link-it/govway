@parallel=false
Feature: Verifica ACL rs-api-config: solo utenze con permesso 'Servizi' (ruolo 'configuratore') hanno accesso

# La rs-api-config espone una sola ACL (auth.aclDefault) con path=*, method=*, roles=configuratore.
# Il ruolo 'configuratore' viene assegnato dall'AuthenticationProvider esclusivamente alle utenze
# che possiedono il permesso 'Servizi' ('S') in console (vedi
# tools/rs/config/server/src/org/openspcoop2/core/config/rs/server/config/AuthenticationProvider.java).
#
# Questa feature verifica che:
#   - le utenze con permesso 'S' (eventualmente in combinazione con altri) ottengano 200
#   - tutte le altre utenze (permessi monitor D/R/O, permessi console-only C/M/A/U/P, ecc.)
#     ottengano 403, anche se l'autenticazione di per se' passa.

Background:

    * call read('classpath:crud_commons.feature')

    # L'endpoint GET /status e' usato come probe: non ha side effect ed e' coperto dalla
    # ACL catch-all aclDefault esattamente come ogni altra risorsa di rs-api-config.
    * def statusUrl = configDirectUrl + '/status'

    # Setup: crea 5 utenze rappresentative copiando la password hashata dall'utente 'amministratore'
    # (che ha gia' password='123456' nel DB di test) e sovrascrivendo il campo 'permessi'.
    # Cleanup eseguito in afterFeature.
    * def DbUtils = Java.type('org.openspcoop2.core.config.rs.testsuite.DbUtils')
    * def db = new DbUtils(govwayDbConfig)

    * def utenti = [ 'configS', 'configD', 'configO', 'configDR', 'configSDR' ]
    * eval db.update("DELETE FROM users WHERE login IN ('configS','configD','configO','configDR','configSDR')")
    * eval db.update("INSERT INTO users (login, password, tipo_interfaccia, interfaccia_completa, permessi, soggetti_all, servizi_all) SELECT 'configS',   password, tipo_interfaccia, interfaccia_completa, 'S',   soggetti_all, servizi_all FROM users WHERE login='amministratore'")
    * eval db.update("INSERT INTO users (login, password, tipo_interfaccia, interfaccia_completa, permessi, soggetti_all, servizi_all) SELECT 'configD',   password, tipo_interfaccia, interfaccia_completa, 'D',   soggetti_all, servizi_all FROM users WHERE login='amministratore'")
    * eval db.update("INSERT INTO users (login, password, tipo_interfaccia, interfaccia_completa, permessi, soggetti_all, servizi_all) SELECT 'configO',   password, tipo_interfaccia, interfaccia_completa, 'O',   soggetti_all, servizi_all FROM users WHERE login='amministratore'")
    * eval db.update("INSERT INTO users (login, password, tipo_interfaccia, interfaccia_completa, permessi, soggetti_all, servizi_all) SELECT 'configDR',  password, tipo_interfaccia, interfaccia_completa, 'DR',  soggetti_all, servizi_all FROM users WHERE login='amministratore'")
    * eval db.update("INSERT INTO users (login, password, tipo_interfaccia, interfaccia_completa, permessi, soggetti_all, servizi_all) SELECT 'configSDR', password, tipo_interfaccia, interfaccia_completa, 'SDR', soggetti_all, servizi_all FROM users WHERE login='amministratore'")

    * configure afterFeature = function(){ karate.call('classpath:org/openspcoop2/core/config/rs/testsuite/permessi/cleanup-utenze.feature'); }

    * def credConfigS = basic({ username: 'configS', password: '123456' })
    * def credConfigD = basic({ username: 'configD', password: '123456' })
    * def credConfigO = basic({ username: 'configO', password: '123456' })
    * def credConfigDR = basic({ username: 'configDR', password: '123456' })
    * def credConfigSDR = basic({ username: 'configSDR', password: '123456' })


@PermessiConfigACL
Scenario: Utente con solo permesso Servizi (S) -> 200

    Given url statusUrl
    And configure headers = ({ "Authorization": credConfigS })
    When method GET
    Then status 200


@PermessiConfigACL
Scenario: Utente con solo permesso Diagnostica (D) -> 403

    Given url statusUrl
    And configure headers = ({ "Authorization": credConfigD })
    When method GET
    Then status 403


@PermessiConfigACL
Scenario: Utente con solo permesso OperativitaApi (O) -> 403

    Given url statusUrl
    And configure headers = ({ "Authorization": credConfigO })
    When method GET
    Then status 403


@PermessiConfigACL
Scenario: Utente con Diagnostica + Reportistica (alias operatore monitor) -> 403

    # L'alias 'operatore' (D+R) e' un ruolo della rs-api-monitor; non da' accesso alla rs-api-config.
    Given url statusUrl
    And configure headers = ({ "Authorization": credConfigDR })
    When method GET
    Then status 403


@PermessiConfigACL
Scenario: Utente con Servizi + Diagnostica + Reportistica (S + monitor) -> 200

    # Avere altri permessi monitor non blocca l'accesso: e' sufficiente il permesso 'S'.
    Given url statusUrl
    And configure headers = ({ "Authorization": credConfigSDR })
    When method GET
    Then status 200
