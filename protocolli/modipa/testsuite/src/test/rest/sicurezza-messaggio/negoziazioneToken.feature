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

#* match result contains deep other_checks_risposta2

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

#* match result contains deep other_checks_risposta2

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

* match result[0].MESSAGGIO contains 'Il tipo di keystore indicato nella token policy \'MODI-NegoziazioneTokenPDND\' richiede l\'autenticazione e l\'identificazione di un applicativo fruitore: org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound: Nessun Servizio Applicativo trovato.'

#* match result contains deep other_checks_risposta2




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

#* match result contains deep other_checks_risposta2

Examples:
| tipo-test |  username | password |
| TestKoAltroProfilo | MODI-DemoNegoziazioneToken | MODI-DemoNegoziazioneToken |



