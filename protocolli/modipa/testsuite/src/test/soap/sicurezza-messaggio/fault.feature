Feature: Testing Sicurezza Messaggio ModiPA IDAS01 e IDAS03 con messaggi contenenti un SOAPFault

# La sicurezza messaggio non deve essere saltata per una richiesta il cui Body contiene un SOAPFault:
# - in fruizione la sicurezza deve essere comunque applicata;
# - in erogazione la sicurezza deve essere comunque validata e, in sua assenza, la richiesta non deve arrivare al backend.
# Sulle risposte la sicurezza messaggio continua a non essere gestita per i fault, ma deve essere emesso un diagnostico dedicato.
#
# Ogni scenario viene verificato sia su erogazioni senza autorizzazione (variante 'noauthz', SoapBlockingIDAS0xFault)
# sia su erogazioni con autorizzazione puntuale per applicativi (variante 'authz', SoapBlockingIDAS0xFaultAuthz).

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
    * def diag_fault_risposta_erogazione = 'Sicurezza messaggio ModI non applicata alla risposta in quanto si tratta di un messaggio di errore: SOAPFault%'
    * def diag_fault_risposta_fruizione = 'Sicurezza messaggio ModI non verificata sulla risposta in quanto si tratta di un messaggio di errore: SOAPFault%'

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


@richiesta-fault
Scenario Outline: Fruizione e erogazione <profilo> (<variante>) di una richiesta il cui Body contiene un SOAPFault: la sicurezza deve essere applicata e validata

# L'azione viene indicata nella url poiché non è identificabile dal contenuto (SOAPFault)
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<servizio>/v1/MRequest'

Given url soap_url
And request read('fault/request-fault.xml')
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'fault-soap-richiesta-fault-<variante>-<profilo>'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.xml')

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* eval checkDiagnostico(tid, diag_token_richiesta_creato)

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* eval checkDiagnostico(tid, diag_verifica_richiesta_ok)
* eval checkAutorizzazione(tid, '<variante>')

Examples:
| profilo | variante | servizio                     |
| idas01  | noauthz  | SoapBlockingIDAS01Fault      |
| idas01  | authz    | SoapBlockingIDAS01FaultAuthz |
| idas03  | noauthz  | SoapBlockingIDAS03Fault      |
| idas03  | authz    | SoapBlockingIDAS03FaultAuthz |


@erogazione-fault-senza-sicurezza
Scenario Outline: All'erogazione <profilo> (<variante>) arriva una richiesta con SOAPFault senza header di sicurezza: deve essere rifiutata e non deve arrivare al backend

* def testId = 'fault-soap-erogazione-fault-senza-sicurezza-<variante>-<profilo>'
* eval initBackend(testId)

* def soap_url = govway_base_path + '/soap/in/DemoSoggettoErogatore/<servizio>/v1/MRequest'

Given url soap_url
And request read('fault/request-fault.xml')
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = testId
When method post
Then status 500
And match response == read('error-bodies/no-token-in-richiesta.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

* def stato = statoBackend(testId)
* match stato == backendNonRaggiunto

Examples:
| profilo | variante | servizio                     |
| idas01  | noauthz  | SoapBlockingIDAS01Fault      |
| idas01  | authz    | SoapBlockingIDAS01FaultAuthz |
| idas03  | noauthz  | SoapBlockingIDAS03Fault      |
| idas03  | authz    | SoapBlockingIDAS03FaultAuthz |


@risposta-fault
Scenario Outline: Il backend <profilo> (<variante>) risponde con un SOAPFault: la sicurezza messaggio non viene gestita sulla risposta e viene emesso un diagnostico dedicato sia in erogazione che in fruizione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<servizio>/v1/MRequest'

Given url soap_url
And request read('request.xml')
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'fault-soap-risposta-fault-<variante>-<profilo>'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('fault/response-fault.xml')

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* eval checkDiagnostico(tid, diag_fault_risposta_fruizione)

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* eval checkDiagnostico(tid, diag_fault_risposta_erogazione)
* eval checkAutorizzazione(tid, '<variante>')

Examples:
| profilo | variante | servizio                     |
| idas01  | noauthz  | SoapBlockingIDAS01Fault      |
| idas01  | authz    | SoapBlockingIDAS01FaultAuthz |
| idas03  | noauthz  | SoapBlockingIDAS03Fault      |
| idas03  | authz    | SoapBlockingIDAS03FaultAuthz |
