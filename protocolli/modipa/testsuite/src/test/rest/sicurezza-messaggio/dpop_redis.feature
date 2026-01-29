Feature: Testing DPOP

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_traccia_sub_iss_client_notpresent = read('check-tracce/check-traccia_sub_iss_clientid_notpresent.feature')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def reset_cache_token = read('classpath:utils/reset-cache-token.feature')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }
    
    * def get_diagnostico = read('classpath:utils/get_diagnostico.js')


@tokenPolicyRSCacheEnabledRedisPolicy
Scenario Outline: Test negoziazione ok; chiavi 'RS' presenti in token Policy con cache di 5 secondi per il riuso del DPoP token. Il test verifica anche l'utilizzo della proprietà 'tokenValidation.dpop.htu.baseUrl'. Policy <policy-name>

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
And header GovWay-TestSuite-Test-ID = 'dpop-duplicate-<tipo-filtro>-ok'
And request read('request.json')
And header Content-Type = 'application/json; charset=UTF-8'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def tidfruizione = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostico(tidfruizione, "DPoP backend proof generato") 
* match result[0].MESSAGGIO contains "DPoP backend proof generato"

* def tiderogazione = responseHeaders['GovWay-Peer-Transaction-ID'][0]
* def result = get_diagnostico(tiderogazione, "Validazione del DPoP token effettuata con successo") 
* match result[0].MESSAGGIO contains "Validazione del DPoP token effettuata con successo"

# Nuovo invio deve indicare richiesta già ricevuta

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
And header GovWay-TestSuite-Test-ID = 'dpop-duplicate-<tipo-filtro>-error'
And request read('request.json')
And header Content-Type = 'application/json; charset=UTF-8'
When method post
Then status 401
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header WWW-Authenticate == 'DPoP realm="<policy-name>", error="invalid_dpop_proof", error_description="DPoP proof invalid"'

* def tidfruizione = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostico(tidfruizione, "DPoP backend proof generato") 
* match result[0].MESSAGGIO contains "DPoP backend proof generato (in cache)"

* def tiderogazione = responseHeaders['GovWay-Peer-Transaction-ID'][0]
* def result = get_diagnostico(tiderogazione, "DPoP JTI validation failed") 
* match result[0].MESSAGGIO contains "Validazione del DPoP token fallita: Token non valido: DPoP JTI validation failed: DPoP JTI"
* match result[0].MESSAGGIO contains "has already been used (replay attack detected)"

# Attendo i tempi di cache e faccio un nuovo tentativo che dovrebbe riuscire

* java.lang.Thread.sleep(5000)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
And header GovWay-TestSuite-Test-ID = 'dpop-duplicate-<tipo-filtro>-ok'
And request read('request.json')
And header Content-Type = 'application/json; charset=UTF-8'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def tidfruizione = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostico(tidfruizione, "DPoP backend proof generato") 
* match result[0].MESSAGGIO contains "DPoP backend proof generato"

* def tiderogazione = responseHeaders['GovWay-Peer-Transaction-ID'][0]
* def result = get_diagnostico(tiderogazione, "Validazione del DPoP token effettuata con successo") 
* match result[0].MESSAGGIO contains "Validazione del DPoP token effettuata con successo"


Examples:
| tipo-test | azione | tipo-filtro | policy-name |
| DemoNegoziazioneTokenDPoP-RS256-CacheEnabled | base | redis | ModI-NegoziazionePDND-Validazione-DPoP-RS256-Redis |
