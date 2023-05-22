Feature: Testing Sicurezza Messaggio ModiPA IDAR0402 -> ID_INTEGRITY_REST_02 (Unicità messaggio)

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_traccia_kid = read('check-tracce/check-traccia-kid.feature')
    * def check_traccia_kid_solo_oauth = read('check-tracce/check-traccia-kid-solo-oauth.feature')
    * def check_traccia_self_signed = read('check-tracce/check-traccia-self-signed.feature')
    * def check_traccia_id_messaggio = read('check-tracce/check-traccia-id-messaggio.feature')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def clean_remote_store_key = read('classpath:utils/remote_store_key.js')
    * def result = clean_remote_store_key('KID-ExampleServer')
    * def result = clean_remote_store_key('KID-ApplicativoBlockingIDA01')
    * def result = clean_remote_store_key('KID-ApplicativoBlockingJWK')
    * def result = clean_remote_store_key('de606068-01cb-49a5-824d-fb171b5d5ae4')
    * def result = clean_remote_store_key('KID-ApplicativoBlockingKeyPair')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }


@connettivita-base
Scenario Outline: Test connettività base <tipo-test> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0402-<tipo-test>/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idar0402-<tipo-test-minuscolo>'
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR0402-<tipo-test>/v1'
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest
* def response_digest = get server_token $.payload.signed_headers..digest

* def clientIdExpected = '<clientId>'
* def subExpected = 'RestBlockingIDAR0402-<tipo-test>/v1'
* def issExpected = 'DemoSoggettoFruitore'

* def other_checks_authorization_richiesta = 
"""
([
])
"""

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdExpected }
])
"""

* def clientIdResponseExpected = 'ExampleServer<tipo-test>'
* def subResponseExpected = 'RestBlockingIDAR0402-<tipo-test>/v1'
* def issResponseExpected = 'DemoSoggettoErogatore'

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' },
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subResponseExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: issResponseExpected },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdResponseExpected }
])
"""

* def kidRequest = '<kid>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: 'IDAR0402', other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0402', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia_kid ({ tid: tid, tipo: 'Risposta', token: server_token, kid: 'KID-ExampleServer', profilo_sicurezza: 'IDAR0402', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: 'IDAR0402', other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0402', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia_kid ({ tid: tid, tipo: 'Risposta', token: server_token, kid: 'KID-ExampleServer', profilo_sicurezza: 'IDAR0402', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01-CredenzialePrincipal | de606068-01cb-49a5-824d-fb171b5d5ae4 | RestBlockingIDAR0402-PDND/v1 |
| KeyPair | keypair | servizio che genera una risposta tramite keyPair. La validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01-CredenzialePrincipal | KID-ApplicativoBlockingKeyPair | DemoSoggettoFruitore/KeyPair/ApplicativoBlockingKeyPair |




@riutilizzo-token
Scenario Outline: Riutilizzo dello stesso token, che deve far arrabiare erogazione e fruizione <tipo-test> (<descrizione>)

# Prima facciamo un giro ok per far generare il token alla fruizione e alla erogazione
Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0402-<tipo-test>/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'riutilizzo-token-idar0402-<tipo-test-minuscolo>'
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR0402-<tipo-test>/v1'
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_authorization_token_header = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]
* def client_agid_token_header = responseHeaders['GovWay-TestSuite-GovWay-Client-Agid-Token'][0]
* def server_agid_token_header = responseHeaders['GovWay-TestSuite-GovWay-Server-Agid-Token'][0]

* def client_agid_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Agid-Token'][0], "AGID")
* def request_digest = get client_agid_token $.payload.signed_headers..digest
* def jti_agid = get client_agid_token $.payload.jti

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def jti_authorization = get client_authorization_token $.payload.jti

* def server_agid_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Agid-Token'][0], "AGID")
* def response_digest = get server_agid_token $.payload.signed_headers..digest


# Verifico che l'identificativo utilizzato sia quello di Integrity o quello di Authorization, per l'id della busta, a seconda della configurazione

* def clientIdExpected = '<clientId>'
* def subExpected = 'RestBlockingIDAR0402-<tipo-test>/v1'
* def issExpected = 'DemoSoggettoFruitore'

* def jtiRichiesta = '<id-utilizzato-filtro-duplicato-richiesta>'
* def jtiExpected = 'N.D.'
* eval
"""
if (jtiRichiesta == 'authorization') {
jtiExpected = jti_authorization
}
"""
* eval
"""
if (jtiRichiesta == 'agid') {
jtiExpected = jti_agid
}
"""

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdExpected },
    { name: 'ProfiloSicurezzaMessaggio-MessageId', value: jti_agid }
])
"""

* def kidRequest = '<kid>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_agid_token, kid: kidRequest, profilo_sicurezza: 'IDAR0402', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
# Implementato solo lato erogazione l'associazione del jti del token come id di messaggio per fare il filtro duplicato * call check_traccia_id_messaggio ({ tid: tid, tipo: 'Richiesta', id_messaggio: jtiExpected })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_agid_token, kid: kidRequest, profilo_sicurezza: 'IDAR0402', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia_id_messaggio ({ tid: tid, tipo: 'Richiesta', id_messaggio: jtiExpected })



# Contattiamo direttamente l'erogazione con il token che si ripete

Given url govway_base_path + "/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0402-<tipo-test>/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header Authorization = client_authorization_token_header
And header Agid-JWT-Signature = client_agid_token_header
And header Digest = request_digest[0]
When method post
Then status 409
And match response == read('error-bodies/identificativo-token-riutilizzato.json')
And match header GovWay-Transaction-ErrorType == 'Conflict'


Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0402-<tipo-test>/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'riutilizzo-token-risposta-idar0402-<tipo-test-minuscolo>'
And header GovWay-TestSuite-Server-Token = server_agid_token_header
And header GovWay-TestSuite-Digest = response_digest[0]
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR0402-<tipo-test>/v1'
When method post
Then status 502
And match response == read('error-bodies/identificativo-token-riutilizzato-in-risposta.json')
And match header GovWay-Transaction-ErrorType == 'ConflictResponse'

Examples:
| tipo-test | tipo-test-minuscolo | id-utilizzato-filtro-duplicato-richiesta | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| PDND | pdnd | agid | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01-CredenzialePrincipal | de606068-01cb-49a5-824d-fb171b5d5ae4 | RestBlockingIDAR0402-PDND/v1 |
| KeyPair | keypair | authorization | servizio che genera una risposta tramite keyPair. La validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK-CredenzialePrincipal | KID-ApplicativoBlockingKeyPair | DemoSoggettoFruitore/KeyPair/ApplicativoBlockingKeyPair |


