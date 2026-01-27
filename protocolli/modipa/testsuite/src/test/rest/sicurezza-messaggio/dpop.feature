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

@tokenPolicyES
Scenario Outline: Test negoziazione ok; chiavi 'ES' presenti in token Policy

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
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
| tipo-test | azione |
| DemoNegoziazioneTokenDPoP-ES256 | base |
| DemoNegoziazioneTokenDPoP-ES256 | full |


@applicativoES
Scenario Outline: Test negoziazione ok; chiavi 'ES' presenti nell'applicativo

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
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
| tipo-test | azione | username | password |
| DemoNegoziazioneTokenDPoP-ES256-DefinitoApplicativo | base | Fruitore-DPoP-ES256-UnicaChiave | 123456 |
| DemoNegoziazioneTokenDPoP-ES256-DefinitoApplicativo | base | Fruitore-DPoP-ES256-ChiaviDifferenti | 123456 |
| DemoNegoziazioneTokenDPoP-ES256-DefinitoApplicativo | base | Fruitore-DPoP-ES256-UnicaChiaveDPoP | 123456 |
| DemoNegoziazioneTokenDPoP-ES256-DefinitoApplicativo | full | Fruitore-DPoP-ES256-UnicaChiave | 123456 |
| DemoNegoziazioneTokenDPoP-ES256-DefinitoApplicativo | full | Fruitore-DPoP-ES256-ChiaviDifferenti | 123456 |
| DemoNegoziazioneTokenDPoP-ES256-DefinitoApplicativo | full | Fruitore-DPoP-ES256-UnicaChiaveDPoP | 123456 |



@applicativoRS
Scenario Outline: Test negoziazione ok; chiavi 'RS' presenti nell'applicativo come archive o come HSM

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
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
| tipo-test | azione | username | password |
| DemoNegoziazioneTokenDPoP-RS256-DefinitoApplicativo | base | Fruitore-DPoP-RS256-UnicaChiaveDPoP-Archive | 123456 |
| DemoNegoziazioneTokenDPoP-RS256-DefinitoApplicativo | base | Fruitore-DPoP-RS256-UnicaChiaveDPoP-HSM | 123456 |



@fruizioneES
Scenario Outline: Test negoziazione ok; chiavi 'ES' presenti nella fruizione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
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
| tipo-test | azione |
| DemoNegoziazioneTokenDPoP-ES256-DefinitoFruizioneRidefinito-UnicaChiave | base |
| DemoNegoziazioneTokenDPoP-ES256-DefinitoFruizioneRidefinito-UnicaChiave | full |
| DemoNegoziazioneTokenDPoP-ES256-DefinitoFruizioneRidefinito-ChiaviDifferenti | base |
| DemoNegoziazioneTokenDPoP-ES256-DefinitoFruizioneRidefinito-ChiaviDifferenti | full |
| DemoNegoziazioneTokenDPoP-ES256-DefinitoFruizioneRidefinito-UnicaChiaveDPoP | base |
| DemoNegoziazioneTokenDPoP-ES256-DefinitoFruizioneRidefinito-UnicaChiaveDPoP | full |

@fruizioneRS
Scenario Outline: Test negoziazione ok; chiavi 'RS' presenti nella fruizione, con keystore di default

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
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
| tipo-test | azione |
| DemoNegoziazioneTokenDPoP-RS256-DefinitoFruizioneRidefinitoDefault-UnicaChiave | base |
| DemoNegoziazioneTokenDPoP-RS256-DefinitoFruizioneRidefinitoDefault-UnicaChiave | full |
| DemoNegoziazioneTokenDPoP-RS256-DefinitoFruizioneRidefinitoDefault-ChiaviDifferenti | base |
| DemoNegoziazioneTokenDPoP-RS256-DefinitoFruizioneRidefinitoDefault-ChiaviDifferenti | full |
| DemoNegoziazioneTokenDPoP-RS256-DefinitoFruizioneRidefinitoDefault-UnicaChiaveDPoP | base |
| DemoNegoziazioneTokenDPoP-RS256-DefinitoFruizioneRidefinitoDefault-UnicaChiaveDPoP | full |



@fruizioneArchivioRS
Scenario Outline: Test negoziazione ok; chiavi 'RS' presenti nella fruizione, con keystore ridefinito tramite archivio

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
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
| tipo-test | azione |
| DemoNegoziazioneTokenDPoP-RS256-DefinitoFruizioneRidefinito-Archivio | base |



@fruizioneHSMRS
Scenario Outline: Test negoziazione ok; chiavi 'RS' presenti nella fruizione, con keystore ridefinito su HSM

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
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
| tipo-test | azione |
| DemoNegoziazioneTokenDPoP-RS256-DefinitoFruizioneRidefinito-HSM | base |



@applicativoNotDefined
Scenario Outline: Test negoziazione errori; keystore richiesto nell'applicativo

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header Content-Type = 'application/json; charset=UTF-8'
When method post
Then status 503
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def tidfruizione = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostico(tidfruizione, "%non possiede una configurazione dei parametri di sicurezza messaggio%") 
* match result[0].MESSAGGIO contains "Il profilo di sicurezza richiesto 'Token Policy Negoziazione - DPoP' non è applicabile poichè l'applicativo mittente Fruitore-DPoP-SenzaKeystore (DemoSoggettoFruitore) non possiede una configurazione dei parametri di sicurezza messaggio (Keystore)"

Examples:
| tipo-test | azione | username | password |
| DemoNegoziazioneTokenDPoP-ES256-DefinitoApplicativo | base | Fruitore-DPoP-SenzaKeystore | 123456 |
| DemoNegoziazioneTokenDPoP-ES256-DefinitoApplicativo | full | Fruitore-DPoP-SenzaKeystore | 123456 |




@fruizioneNotDefined
Scenario Outline: Test negoziazione errori; keystore richiesto nella fruizione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
And request read('request.json')
And header Content-Type = 'application/json; charset=UTF-8'
When method post
Then status 503
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def tidfruizione = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostico(tidfruizione, "%KeyStore non definito") 
* match result[0].MESSAGGIO contains "KeyStore non definito"

Examples:
| tipo-test | azione |
| DemoNegoziazioneTokenDPoP-KeystoreRichiestoFruizioneNonPresente | base |
| DemoNegoziazioneTokenDPoP-KeystoreRichiestoFruizioneNonPresente | full |



@tokenPolicyRSCacheEnabled
Scenario Outline: Test negoziazione ok; chiavi 'RS' presenti in token Policy con cache di 5 secondi per il riuso del DPoP token. Il test verifica anche l'utilizzo della proprietà 'tokenValidation.dpop.htu.baseUrl'

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
| DemoNegoziazioneTokenDPoP-RS256-CacheEnabled | base | local | ModI-NegoziazionePDND-Validazione-DPoP-RS256 |
| DemoNegoziazioneTokenDPoP-RS256-CacheEnabled | base | redis | ModI-NegoziazionePDND-Validazione-DPoP-RS256-Redis |





@tokenPolicyRSCacheEnabledDuplicateFilterDisabled
Scenario Outline: Test negoziazione ok; chiavi 'RS' presenti in token Policy con cache di 5 secondi per il riuso del DPoP token. Il test verifica anche l'utilizzo della proprietà 'tokenValidation.dpop.htu.baseUrl'. Il test, verifica che lo stesso DPoP token viene accettato poichè la configurazione indirizzata contiene un filtro duplicati disabilitato.

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
| DemoNegoziazioneTokenDPoP-RS256-CacheEnabled | base | disabilitato | ModI-NegoziazionePDND-Validazione-DPoP-RS256-FiltroDuplicatiDisabilitato |




@prefixUrlDifferente
Scenario Outline: Test negoziazione ok; chiavi 'ES' presenti in token Policy. Il test verifica anche l'utilizzo della proprietà 'tokenValidation.dpop.htu.prefixUrl'

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
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
| tipo-test | azione |
| DemoNegoziazioneTokenDPoP-ES256-HostnameErogatore | base |




@payloadHtmError
Scenario Outline: Test negoziazione ok; chiavi 'ES' presenti in token Policy. Il test modifica in transito il metodo http utilizzato, e questo rende non valido il DPoP token (htm differente)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
And header GovWay-TestSuite-Test-ID = 'dpop-htm'
And request read('request.json')
And header Content-Type = 'application/json; charset=UTF-8'
When method post
Then status 401
And match header WWW-Authenticate == 'DPoP realm="ModI-NegoziazionePDND-Validazione-DPoP-RS256", error="invalid_dpop_proof", error_description="DPoP proof invalid"'
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def tidfruizione = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostico(tidfruizione, "DPoP backend proof generato") 
* match result[0].MESSAGGIO contains "DPoP backend proof generato"

* def tiderogazione = responseHeaders['GovWay-Peer-Transaction-ID'][0]
* def result = get_diagnostico(tiderogazione, "Invalid DPoP token") 
* match result[0].MESSAGGIO contains "Validazione del DPoP token fallita: Token non valido: Invalid DPoP token: claim 'htm' (POST) does not match HTTP method of the request (PUT)"


Examples:
| tipo-test | azione |
| DemoNegoziazioneTokenDPoP-RS256-CacheEnabled | base |



@payloadHtuError
Scenario Outline: Test negoziazione ok; chiavi 'RS' presenti in token Policy. Il test verifica che venga riconosciuto un htu differente da quello atteso.

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
And request read('request.json')
And header Content-Type = 'application/json; charset=UTF-8'
When method post
Then status 401
And match header WWW-Authenticate == 'DPoP realm="ModI-NegoziazionePDND-Validazione-DPoP-RS256", error="invalid_dpop_proof", error_description="DPoP proof invalid"'
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def tidfruizione = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostico(tidfruizione, "DPoP backend proof generato") 
* match result[0].MESSAGGIO contains "DPoP backend proof generato"

* def tiderogazione = responseHeaders['GovWay-Peer-Transaction-ID'][0]
* def result = get_diagnostico(tiderogazione, "Invalid DPoP token") 
* match result[0].MESSAGGIO contains "Validazione del DPoP token fallita: Token non valido: Invalid DPoP token: claim 'htu' (http://erogatore:8080/govway/rest/in/DemoSoggettoErogatore/DemoNegoziazioneTokenDPoP-RS256/v1/base) does not match request URI (http://localhost:8080/govway/rest/in/DemoSoggettoErogatore/DemoNegoziazioneTokenDPoP-RS256/v1/base,http://localhost:8090/base)"


Examples:
| tipo-test | azione |
| DemoNegoziazioneTokenDPoP-RS256-HostnameErogatore | base |



@payloadIatError
Scenario Outline: Test negoziazione ok; chiavi 'RS' presenti in token Policy. Il test verifica che il DPoP token non sia accettato dopo il tempo di TTL impostato (5 secondi per questa configurazione)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<tipo-test>/v1"
And path '<azione>'
And header GovWay-TestSuite-Test-ID = 'dpop-expired'
And request read('request.json')
And header Content-Type = 'application/json; charset=UTF-8'
When method post
Then status 401
And match header WWW-Authenticate == 'DPoP realm="<policy-name>", error="invalid_dpop_proof", error_description="DPoP proof invalid"'
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def tidfruizione = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostico(tidfruizione, "DPoP backend proof generato") 
* match result[0].MESSAGGIO contains "DPoP backend proof generato"

* def tiderogazione = responseHeaders['GovWay-Peer-Transaction-ID'][0]
* def result = get_diagnostico(tiderogazione, "Token non valido") 
* match result[0].MESSAGGIO contains "Validazione del DPoP token fallita: Token non valido: Expired DPoP token (iat:"
* match result[0].MESSAGGIO contains "TTL:"
* match result[0].MESSAGGIO contains "tolerance"

Examples:
| tipo-test | azione | policy-name |
| DemoNegoziazioneTokenDPoP-RS256-CacheEnabled | base | ModI-NegoziazionePDND-Validazione-DPoP-RS256-TTL5Secondi |
