Feature: Testing Sicurezza Messaggio ModiPA IDAR payload vuoti

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }

# Metto questo test in cima perchè se eseguito per ultimo fallisce per via di qualche bug di karate.
@idar03-token-criteri-personalizzati
Scenario: La API ha di default l'applicabilità solo sulla Richiesta, ma viene ridefinita sulla singola azione in Richiesta e Risposta con dei criteri personalizzati.

* def resp =
"""
({
        "a" : {
        "a2": "RGFuJ3MgVG9vbHMgYXJlIGNvb2wh",
        "a1s": [ 1, 2 ]
      },
      "b": "Stringa di esempio"
})
"""

# L'azione personalizzata è la PUT, che ha come criteri di applicabilità di richiesta il content type text/html, che quindi non farà scattare la 
# cornice di sicurezza sulla richiesta, mentre sulla risposta ha come criterio di applicabilità il content type application/json + statusCode 200
# Mancherà dunque il token nella richiesta e sarà presente nella risposta
Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03TokenCriteriPersonalizzati/v1"
And path 'resources', 'object', 1
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar03-token-criteri-personalizzati'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method put
Then status 200
And match response == resp
And match header Authorization == '#notpresent'

* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")
* def response_digest = get server_token $.payload.signed_headers..digest


* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })