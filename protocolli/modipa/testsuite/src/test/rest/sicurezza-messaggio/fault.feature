Feature: Testing Sicurezza Messaggio ModiPA IDAR01 e IDAR03 con messaggi classificati come fault (RFC 7807)

# La sicurezza messaggio non deve essere saltata per una richiesta il cui Content-Type
# corrisponde ad un problem details (application/problem+json, application/problem+xml):
# - in fruizione il token deve essere comunque generato;
# - in erogazione il token deve essere comunque validato e, in sua assenza, la richiesta non deve arrivare al backend.
# Sulle risposte la sicurezza messaggio continua a non essere gestita per i fault, ma deve essere emesso un diagnostico dedicato.
#
# Ogni scenario viene verificato sia su erogazioni senza autorizzazione (variante 'noauthz', RestBlockingIDAR0xFault)
# sia su erogazioni con autorizzazione puntuale per applicativi (variante 'authz', RestBlockingIDAR0xFaultAuthz).

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def get_diagnostico = read('classpath:utils/get_diagnostico.js')
    * def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }

    # Diagnostici attesi
    * def diag_token_richiesta_creato = 'Creazione security token ModI % della richiesta effettuata con successo'
    * def diag_verifica_richiesta_ok = 'Verifica ModI della richiesta effettuata con successo'
    * def diag_autorizzazione_richiesta_ok = 'Autorizzazione ModI della richiesta effettuata con successo'
    # Diagnostici dedicati emessi quando la sicurezza messaggio non viene gestita su una risposta di tipo fault
    * def diag_fault_risposta_erogazione = 'Sicurezza messaggio ModI non applicata alla risposta in quanto si tratta di un messaggio di errore: Content-Type %'
    * def diag_fault_risposta_fruizione = 'Sicurezza messaggio ModI non verificata sulla risposta in quanto si tratta di un messaggio di errore: Content-Type %'

    * def backendNonRaggiunto = 'non-raggiunto'
    * def initBackend = function(testId){ karateCache.add('fault-backend-' + testId, backendNonRaggiunto) }
    * def statoBackend = function(testId){ return karateCache.get('fault-backend-' + testId) }

    * def checkDiagnostico =
    """
    function(tid, messaggio) {
        var msgs = get_diagnostico(tid, messaggio);
        if (msgs.length != 1) {
            karate.fail("Atteso un diagnostico '" + messaggio + "' nella transazione " + tid + ", trovati: " + msgs.length);
        }
    }
    """
    * def checkAutorizzazione =
    """
    function(tid, variante) {
        if (variante == 'authz') {
            checkDiagnostico(tid, diag_autorizzazione_richiesta_ok);
        }
    }
    """


@richiesta-problem
Scenario Outline: Fruizione e erogazione <profilo> (<variante>) di una richiesta con Content-Type <content_type>: il token deve essere generato e validato e, con autorizzazione puntuale, l'applicativo identificato e autorizzato

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<servizio>/v1"
And path 'resources', 1, 'M'
And request read('fault/<body>')
And header Content-Type = '<content_type>'
And header GovWay-TestSuite-Test-ID = 'fault-richiesta-problem-<tipo>-<variante>-<profilo>'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header IDAR03TestHeader = "TestHeaderRequest"
When method post
Then status 200
And match response == read('response.json')

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* eval checkDiagnostico(tid, diag_token_richiesta_creato)

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* eval checkDiagnostico(tid, diag_verifica_richiesta_ok)
* eval checkAutorizzazione(tid, '<variante>')

Examples:
| profilo | variante | servizio                     | tipo | content_type              | body                 |
| idar01  | noauthz  | RestBlockingIDAR01Fault      | json | application/problem+json  | request-problem.json |
| idar01  | noauthz  | RestBlockingIDAR01Fault      | xml  | application/problem+xml   | request-problem.xml  |
| idar01  | authz    | RestBlockingIDAR01FaultAuthz | json | application/problem+json  | request-problem.json |
| idar01  | authz    | RestBlockingIDAR01FaultAuthz | xml  | application/problem+xml   | request-problem.xml  |
| idar03  | noauthz  | RestBlockingIDAR03Fault      | json | application/problem+json  | request-problem.json |
| idar03  | noauthz  | RestBlockingIDAR03Fault      | xml  | application/problem+xml   | request-problem.xml  |
| idar03  | authz    | RestBlockingIDAR03FaultAuthz | json | application/problem+json  | request-problem.json |
| idar03  | authz    | RestBlockingIDAR03FaultAuthz | xml  | application/problem+xml   | request-problem.xml  |


@erogazione-problem-senza-token
Scenario Outline: All'erogazione <profilo> (<variante>) arriva una richiesta <content_type> senza token: deve essere rifiutata e non deve arrivare al backend

* def testId = 'fault-erogazione-problem-<tipo>-senza-token-<variante>-<profilo>'
* eval initBackend(testId)

Given url govway_base_path + "/rest/in/DemoSoggettoErogatore/<servizio>/v1"
And path 'resources', 1, 'M'
And request read('fault/<body>')
And header Content-Type = '<content_type>'
And header GovWay-TestSuite-Test-ID = testId
When method post
Then status 400
And match response == read('<errore>')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

* def stato = statoBackend(testId)
* match stato == backendNonRaggiunto

# l'errore viene restituito nel formato (json o xml) della richiesta
Examples:
| profilo | variante | servizio                     | tipo | content_type              | body                 | errore                                                        |
| idar01  | noauthz  | RestBlockingIDAR01Fault      | json | application/problem+json  | request-problem.json | error-bodies/no-header-authorization.json                     |
| idar01  | noauthz  | RestBlockingIDAR01Fault      | xml  | application/problem+xml   | request-problem.xml  | fault/no-header-authorization.xml                             |
| idar01  | authz    | RestBlockingIDAR01FaultAuthz | json | application/problem+json  | request-problem.json | error-bodies/no-header-authorization.json                     |
| idar01  | authz    | RestBlockingIDAR01FaultAuthz | xml  | application/problem+xml   | request-problem.xml  | fault/no-header-authorization.xml                             |
| idar03  | noauthz  | RestBlockingIDAR03Fault      | json | application/problem+json  | request-problem.json | error-bodies/assenza-header-agid-jwt-signature-richiesta.json |
| idar03  | noauthz  | RestBlockingIDAR03Fault      | xml  | application/problem+xml   | request-problem.xml  | fault/assenza-header-agid-jwt-signature-richiesta.xml         |
| idar03  | authz    | RestBlockingIDAR03FaultAuthz | json | application/problem+json  | request-problem.json | error-bodies/assenza-header-agid-jwt-signature-richiesta.json |
| idar03  | authz    | RestBlockingIDAR03FaultAuthz | xml  | application/problem+xml   | request-problem.xml  | fault/assenza-header-agid-jwt-signature-richiesta.xml         |


@content-type-riscritto-idar01
Scenario Outline: IDAR01 (<variante>): la richiesta firmata dalla fruizione come <content_type> viene inoltrata all'erogazione come <content_type_riscritto>; il token non firma il Content-Type e la richiesta deve essere gestita normalmente

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<servizio>/v1"
And path 'resources', 1, 'M'
And request read('<body>')
And header Content-Type = '<content_type>'
And header GovWay-TestSuite-Test-ID = 'fault-content-type-riscritto-<tipo>-<variante>-idar01'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* eval checkDiagnostico(tid, diag_verifica_richiesta_ok)
* eval checkAutorizzazione(tid, '<variante>')

Examples:
| variante | servizio                     | tipo | content_type     | content_type_riscritto   | body              |
| noauthz  | RestBlockingIDAR01Fault      | json | application/json | application/problem+json | request.json      |
| noauthz  | RestBlockingIDAR01Fault      | xml  | application/xml  | application/problem+xml  | fault/request.xml |
| authz    | RestBlockingIDAR01FaultAuthz | json | application/json | application/problem+json | request.json      |
| authz    | RestBlockingIDAR01FaultAuthz | xml  | application/xml  | application/problem+xml  | fault/request.xml |


@content-type-riscritto-idar03
Scenario Outline: IDAR03 (<variante>): token firmato con content-type <content_type> ma richiesta inoltrata all'erogazione come <content_type_riscritto>; deve essere rilevata la differenza rispetto all'header firmato e la richiesta non deve arrivare al backend

* def testId = 'fault-content-type-riscritto-<tipo>-<variante>-idar03'
* eval initBackend(testId)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<servizio>/v1"
And path 'resources', 1, 'M'
And request read('<body>')
And header Content-Type = '<content_type>'
And header GovWay-TestSuite-Test-ID = testId
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header IDAR03TestHeader = "TestHeaderRequest"
When method post
Then status 400

# l'errore viene restituito nel formato (json o xml) della richiesta inoltrata all'erogazione
* def detail = ('<tipo>' == 'xml') ? karate.xmlPath(response, '/problem/detail') : response.detail
* match detail == "Header HTTP 'content-type' possiede un valore differente rispetto a quello presente negli header firmati"

* def stato = statoBackend(testId)
* match stato == backendNonRaggiunto

Examples:
| variante | servizio                     | tipo | content_type     | content_type_riscritto   | body              |
| noauthz  | RestBlockingIDAR03Fault      | json | application/json | application/problem+json | request.json      |
| noauthz  | RestBlockingIDAR03Fault      | xml  | application/xml  | application/problem+xml  | fault/request.xml |
| authz    | RestBlockingIDAR03FaultAuthz | json | application/json | application/problem+json | request.json      |
| authz    | RestBlockingIDAR03FaultAuthz | xml  | application/xml  | application/problem+xml  | fault/request.xml |


@risposta-problem
Scenario Outline: Il backend <profilo> (<variante>) risponde con un errore <content_type>: la sicurezza messaggio non viene gestita sulla risposta e viene emesso un diagnostico dedicato sia in erogazione che in fruizione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<servizio>/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'fault-risposta-problem-<tipo>-<variante>-<profilo>'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header IDAR03TestHeader = "TestHeaderRequest"
When method post
Then status 500
And match response == read('fault/<body>')
And match header Agid-JWT-Signature == '#notpresent'
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* eval checkDiagnostico(tid, diag_fault_risposta_fruizione + '<content_type>%')

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* eval checkDiagnostico(tid, diag_fault_risposta_erogazione + '<content_type>%')
* eval checkAutorizzazione(tid, '<variante>')

Examples:
| profilo | variante | servizio                     | tipo | content_type              | body                  |
| idar01  | noauthz  | RestBlockingIDAR01Fault      | json | application/problem+json  | response-problem.json |
| idar01  | noauthz  | RestBlockingIDAR01Fault      | xml  | application/problem+xml   | response-problem.xml  |
| idar01  | authz    | RestBlockingIDAR01FaultAuthz | json | application/problem+json  | response-problem.json |
| idar01  | authz    | RestBlockingIDAR01FaultAuthz | xml  | application/problem+xml   | response-problem.xml  |
| idar03  | noauthz  | RestBlockingIDAR03Fault      | json | application/problem+json  | response-problem.json |
| idar03  | noauthz  | RestBlockingIDAR03Fault      | xml  | application/problem+xml   | response-problem.xml  |
| idar03  | authz    | RestBlockingIDAR03FaultAuthz | json | application/problem+json  | response-problem.json |
| idar03  | authz    | RestBlockingIDAR03FaultAuthz | xml  | application/problem+xml   | response-problem.xml  |
