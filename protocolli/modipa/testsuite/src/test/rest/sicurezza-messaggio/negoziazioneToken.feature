Feature: Testing negoziazione token

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }

    * def get_diagnostici = read('classpath:utils/get_diagnostici.js')


@negoziazioneOk
Scenario Outline: Test negoziazione ok tramite una fruizione tramite l'applicativo <tipo-test>

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneToken/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test |  username | password |
| Test1 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 |
| Test2 | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 |


@negoziazioneJwkOk
Scenario Outline: Test negoziazione ok tramite una fruizione tramite l'applicativo <tipo-test> configurato per generare solo un kid tramite keystore jwk

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneToken/v1"
And path '/tokenJwtKidOnly/test'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test |  username | password |
| TestJWK | ApplicativoBlockingJWK | ApplicativoBlockingJWK |


@negoziazioneKeyPairOk
Scenario Outline: Test negoziazione ok tramite una fruizione tramite l'applicativo <tipo-test> configurato per generare solo un kid tramite keystore keypair

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneToken/v1"
And path '/tokenJwtKidOnlyKeyPair/test'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test |  username | password |
| TestKeyPair | ApplicativoBlockingKeyPair | ApplicativoBlockingKeyPair |


@negoziazioneKoKidNonDefinito
Scenario Outline: Test negoziazione ko poiche' viene usato un applicativo su cui non è stato definito il kid

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneToken/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 503

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici(tid) 

* match result[0].MESSAGGIO contains 'La modalità di generazione del Key Id (kid), indicata nella token policy \'MODI-NegoziazioneTokenPDND\', non è utilizzabile con l\'applicativo identificato \'ApplicativoBlockingIDA01ExampleClient2 (Soggetto: DemoSoggettoFruitore)\': nella configurazione dell\'applicativo non è stato definito un \'Key Id (kid) del Certificato\' nella sezione \'Authorization OAuth\''

Examples:
| tipo-test |  username | password |
| TestKoNoKID | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 |


@negoziazioneKoKeystoreNonDefinito
Scenario Outline: Test negoziazione ko poiche' viene usato un applicativo su cui non è stato definito il keystore

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneToken/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 503

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici(tid) 

* match result[0].MESSAGGIO contains 'Il tipo di keystore indicato nella token policy \'MODI-NegoziazioneTokenPDND\' non è utilizzabile: Il profilo di sicurezza richiesto \'Token Policy Negoziazione - Signed JWT\' non è applicabile poichè l\'applicativo mittente ApplicativoBlockingIDA01ExampleClientToken1 (DemoSoggettoFruitore) non possiede una configurazione dei parametri di sicurezza messaggio (Keystore)'

Examples:
| tipo-test |  username | password |
| TestKoNoKeystore | ApplicativoBlockingIDA01ExampleClientToken1 | ApplicativoBlockingIDA01ExampleClientToken1 |



@negoziazioneOkparametriStatici
Scenario Outline: Test negoziazione ok tramite una fruizione tramite l'applicativo <tipo-test>, dove i parametri oltre al keystore sono statici sulla token policy

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneToken2/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test |  username | password |
| Test1 possiede il kid | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 |
| Test2 non possiede il kid | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 |




@negoziazioneKoApplicativoNonAutenticato
Scenario: Test negoziazione ko poiche' non viene autenticato un applicativo

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenPubblico/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
When method post
Then status 503

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici(tid) 

* match result[0].MESSAGGIO contains 'Il tipo di keystore indicato nella token policy \'MODI-NegoziazioneTokenPDND\' richiede l\'autenticazione e l\'identificazione di un applicativo fruitore:'




@negoziazioneKoAltroProfilo
Scenario Outline: Test negoziazione ko poiche' viene usato un applicativo registrato su un altro profilo di interoperabiltà

Given url govway_base_path + "/out/ENTE/ENTE/MODI-DemoNegoziazioneToken/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 503

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici(tid) 

* match result[0].MESSAGGIO contains 'Il tipo di keystore indicato nella token policy \'MODI-NegoziazioneTokenPDND\' è utilizzabile solamente con il profilo di interoperabilità \'ModI\''

Examples:
| tipo-test |  username | password |
| TestKoAltroProfilo | MODI-DemoNegoziazioneToken | MODI-DemoNegoziazioneToken |






@negoziazioneFruizioneOk
Scenario Outline: Test negoziazione ok tramite una fruizione che utilizza l'impostazione '<tipo-test>'

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenFruizione<tipo-test>/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione<tipo-test>'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test |
| KeystoreDefault |
| KeystoreRidefinito |
| KeystoreDefaultSicurezzaMessaggio |
| KeystoreRidefinitoSicurezzaMessaggio |



@negoziazioneFruizioneJWKOk
Scenario Outline: Test negoziazione ok tramite una fruizione che utilizza l'impostazione '<tipo-test>' con keystore JWK

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenFruizione<tipo-test>JWK/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione<tipo-test>'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test |
| KeystoreRidefinito |
| KeystoreRidefinitoSicurezzaMessaggio |



@negoziazioneFruizioneKeyPairOk
Scenario Outline: Test negoziazione ok tramite una fruizione che utilizza l'impostazione '<tipo-test>' con keystore KeyPair

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenFruizione<tipo-test>KeyPair/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione<tipo-test>'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test |
| KeystoreRidefinito |
| KeystoreRidefinitoSicurezzaMessaggio |




@negoziazioneFruizioneJWKKoPatternIntegrityRichiedeX5c
Scenario Outline: Test negoziazione ko poiche' viene usato una fruizione che utilizza l'impostazione '<tipo-test>', su cui non viene riferita una azione che richiede un pattern integrity REST_01 mentre il keystore non possiede un x509

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenFruizione<tipo-test>JWK/v1"
And path 'testX5c'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione<tipo-test>'
When method post
Then status 400

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici(tid) 

* match result[0].MESSAGGIO contains 'Configuration error; pattern \'idam0301\' require x509 certificate, found \'JWK Set\' key'

Examples:
| tipo-test |
| KeystoreRidefinitoSicurezzaMessaggio |




@negoziazioneFruizioneKeyPairKoPatternIntegrityRichiedeX5c
Scenario Outline: Test negoziazione ko poiche' viene usato una fruizione che utilizza l'impostazione '<tipo-test>', su cui non viene riferita una azione che richiede un pattern integrity REST_01 mentre il keystore non possiede un x509

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenFruizione<tipo-test>KeyPair/v1"
And path 'testX5c'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione<tipo-test>'
When method post
Then status 400

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici(tid) 

* match result[0].MESSAGGIO contains 'Configuration error; pattern \'idam0301\' require x509 certificate, found \'Key Pair\' key'

Examples:
| tipo-test |
| KeystoreRidefinitoSicurezzaMessaggio |





@negoziazioneFruizioneKoKidNonDefinito
Scenario Outline: Test negoziazione ko poiche' viene usato una fruizione che utilizza l'impostazione '<tipo-test>', su cui non è stato definito il kid

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenFruizione<tipo-test>_noKID/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione<tipo-test>'
When method post
Then status 503

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici(tid) 

* match result[0].MESSAGGIO contains 'La modalità di generazione del Key Id (kid), indicata nella token policy \'MODI-NegoziazioneTokenPDND-datiInFruizione\', non è utilizzabile con la fruizione \'DemoSoggettoFruitore -> DemoNegoziazioneTokenFruizione<tipo-test>_noKID (Soggetto: DemoSoggettoErogatore)\': nella configurazione \'ModI\' non è stato definito un \'Key Id (kid) del Certificato\''

Examples:
| tipo-test |
| KeystoreDefault |
| KeystoreRidefinito |
| KeystoreDefaultSicurezzaMessaggio |
| KeystoreRidefinitoSicurezzaMessaggio |



@negoziazioneFruizioneKoClientIdNonDefinito
Scenario Outline: Test negoziazione ko poiche' viene usato una fruizione che utilizza l'impostazione '<tipo-test>', su cui non è stato definito il client id

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenFruizione<tipo-test>_noClientId/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione<tipo-test>'
When method post
Then status 503

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici(tid) 

* match result[0].MESSAGGIO contains 'La modalità di generazione dell\'Issuer, indicata nella token policy \'MODI-NegoziazioneTokenPDND-datiInFruizione\', non è utilizzabile con la fruizione \'DemoSoggettoFruitore -> DemoNegoziazioneTokenFruizione<tipo-test>_noClientId (Soggetto: DemoSoggettoErogatore)\': nella configurazione \'ModI\' non è stato definito un \'Identificativo Token ClientId\''

Examples:
| tipo-test |
| KeystoreDefault |
| KeystoreRidefinito |
| KeystoreDefaultSicurezzaMessaggio |
| KeystoreRidefinitoSicurezzaMessaggio |




@negoziazioneFruizioneKoKeystoreNonDefinitoFruizione
Scenario: Test negoziazione ko poiche' viene usato una fruizione che non prevede di ridefinire il keystore sulla fruizione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenFruizioneSicurezzaMessaggio_noKeystoreFruizione/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione<tipo-test>'
When method post
Then status 503

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici(tid) 

* match result[0].MESSAGGIO contains 'La modalità di generazione del Key Id (kid), indicata nella token policy \'MODI-NegoziazioneTokenPDND-datiInFruizione\', non è utilizzabile con la fruizione \'DemoSoggettoFruitore -> DemoNegoziazioneTokenFruizioneSicurezzaMessaggio_noKeystoreFruizione (Soggetto: DemoSoggettoErogatore)\': nella configurazione \'ModI\' non è stato definito un \'Key Id (kid) del Certificato\''




@negoziazioneFruizioneOkparametriStatici
Scenario Outline: Test negoziazione ok tramite una fruizione che utilizza l'impostazione '<tipo-test>', dove i parametri oltre al keystore sono statici sulla token policy

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenFruizione<tipo-test>_policy2/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione<tipo-test>'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test |
| KeystoreDefault |
| KeystoreRidefinito |
| KeystoreDefaultSicurezzaMessaggio |
| KeystoreRidefinitoSicurezzaMessaggio |




@negoziazioneFruizioneKoAltroProfilo
Scenario: Test negoziazione ko poiche' viene usato una policy che riferisce il keystore di una fruizione su un altro profilo di interoperabiltà

Given url govway_base_path + "/out/ENTE/ENTE/MODI-DemoNegoziazioneTokenFruizione/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione<tipo-test>'
When method post
Then status 503

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici(tid) 

* match result[0].MESSAGGIO contains 'Il tipo di keystore indicato nella token policy \'MODI-NegoziazioneTokenPDND-datiInFruizione\' è utilizzabile solamente con il profilo di interoperabilità \'ModI\''









@negoziazioneViaTokenPolicySecurity03Ok
Scenario: Test negoziazione ok tramite l'utilizzo di un keystore PKCS12 definito nella token policy, su una integrity 01'

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenSicurezzaMessaggio03ViaTokenPolicy/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione03ViaTokenPolicy'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'



@negoziazioneViaTokenPolicySecurity04JWKOk
Scenario: Test negoziazione ok tramite l'utilizzo di un keystore JWKS definito nella token policy, su una integrity 02'

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenSicurezzaMessaggio04ViaTokenPolicy-JWK/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione04ViaTokenPolicy'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'



@negoziazioneViaTokenPolicySecurity04KeyPairOk
Scenario: Test negoziazione ok tramite l'utilizzo di un keystore key pair definito nella token policy, su una integrity 02'

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenSicurezzaMessaggio04ViaTokenPolicy-KeyPair/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione04ViaTokenPolicyKeyPair'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'



@negoziazioneViaTokenPolicySecurity04KeyPairClientIdKIDugualiOk
Scenario: Test negoziazione ok tramite l'utilizzo di un keystore key pair definito nella token policy, su una integrity 02', dove nella token policy il kid è definito uguale al clientId

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenSicurezzaMessaggio04ViaTokenPolicy-KeyPair-ClientIdKIDuguali/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione04ViaTokenPolicyKeyPair-ClientIdKIDuguali'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'



@negoziazioneViaTokenPolicySecurityKeystoreDefinitoFruizioneErrore
Scenario: Test negoziazione ko tramite l'utilizzo di un keystore definito nella token policy, su una integrity 02'; nella token policy il keystore viene indicato come definito nella fruizione (loop)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenSicurezzaMessaggio04ViaTokenPolicy-DefinitoFruizione/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione04ViaTokenPolicyFruizioneErrore'
When method post
Then status 400

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici(tid) 

* match result[0].MESSAGGIO contains 'Il profilo di sicurezza richiesto \'idam0401\' richiede l\'assegnazione di una token policy di negoziazione al connettore\; la policy indicata \'MODI-NegoziazioneTokenPDND-datiInPolicy-keystoreFruizione\' non è utilizzabile essendo configurata con una modalità di keystore \'Definito nella fruizione ModI\''



@negoziazioneViaTokenPolicySecurityKeystoreDefinitoApplicativoErrore
Scenario: Test negoziazione ko tramite l'utilizzo di un keystore definito nella token policy, su una integrity 02'; nella token policy il keystore viene indicato come definito nell'applicativo

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenSicurezzaMessaggio04ViaTokenPolicy-DefinitoApplicativo/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'Fruizione04ViaTokenPolicyApplicativoErrore'
When method post
Then status 400

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici(tid) 

* match result[0].MESSAGGIO contains 'Il profilo di sicurezza richiesto \'idam0401\' richiede l\'assegnazione di una token policy di negoziazione al connettore\; la policy indicata \'MODI-NegoziazioneTokenPDND-datiInPolicy-keystoreApplicativo\' non è utilizzabile essendo configurata con una modalità di keystore \'Definito nell\'applicativo ModI\''





