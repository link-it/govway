Feature: Testing Sicurezza Messaggio Autorizzazione

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }


@autorizzazioneSicurezzaMessaggioHttpsRequiredOk
Scenario Outline: Test autorizzazione sicurezza messaggio+token puntuale caso ok con controllo anche del canale

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneModIRest/v1"
And path 'httpsRequired'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1SoloCertificato |



@autorizzazioneSicurezzaMessaggioHttpsRequiredKo
Scenario Outline: Test autorizzazione sicurezza messaggio+token puntuale caso ko con controllo anche del canale dove il canale è differente dall'applicativo

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneModIRest/v1"
And path 'httpsRequired2'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 403
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | authorization-deny-canale-differente-applicativo-solo-certificato.json |




@autorizzazioneSicurezzaMessaggioPuntualeOk
Scenario Outline: Test autorizzazione sicurezza messaggio puntuale caso ok (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneModIRest/v1"
And path 'puntuale'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 |
| interno (noToken) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient4 | ApplicativoBlockingIDA01ExampleClient4 | ApplicativoBlockingIDA01ExampleClient4 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1SoloCertificato |
| esterno (noToken) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient4 | ApplicativoBlockingIDA01ExampleExternalClient4 | ApplicativoBlockingIDA01ExampleExternalClient4 |


@autorizzazioneSicurezzaMessaggioPuntualeKo
Scenario Outline: Test autorizzazione sicurezza messaggio puntuale caso ko (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneModIRest/v1"
And path 'puntuale'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 403
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | authorization-deny-ApplicativoBlockingIDA01ExampleClient2.json |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 | authorization-deny-ApplicativoBlockingIDA01ExampleExternalClient2SoloCertificato.json |


@autorizzazioneSicurezzaMessaggioRuoliAllOk
Scenario Outline: Test autorizzazione sicurezza messaggio ruolo caso ok (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneModIRest/v1"
And path 'ruoliAll'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2SoloCertificato |


@autorizzazioneSicurezzaMessaggioRuoliAllKoSoloUnRuolo
Scenario Outline: Test autorizzazione sicurezza messaggio ruolo caso ko, dove l'applicativo possiede solo un ruolo (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneModIRest/v1"
And path 'ruoliAll'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 403
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleClient3.json |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleExternalClient3SoloCertificato.json |


@autorizzazioneSicurezzaMessaggioRuoliAllKoNessunRuolo
Scenario Outline: Test autorizzazione sicurezza messaggio ruolo caso ko, dove l'applicativo non possiede ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneModIRest/v1"
And path 'ruoliAll'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 403
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleClient1.json |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleExternalClient1SoloCertificato.json |


@autorizzazioneSicurezzaMessaggioRuoliAnyOkSoloUnRuolo
Scenario Outline: Test autorizzazione sicurezza messaggio ruolo caso ok, in cui l'applicativo possiede solamente uno dei due ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneModIRest/v1"
And path 'ruoliAny'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password |  applicativo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3SoloCertificato |


@autorizzazioneSicurezzaMessaggioRuoliAnyOkTuttiRuoli
Scenario Outline: Test autorizzazione sicurezza messaggio ruolo caso ok, in cui l'applicativo possiede tutti e due i ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneModIRest/v1"
And path 'ruoliAny'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2SoloCertificato |


@autorizzazioneSicurezzaMessaggioRuoliAnyKoNessunRuolo
Scenario Outline: Test autorizzazione sicurezza messaggio ruolo caso ko, dove l'applicativo non possiede ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneModIRest/v1"
And path 'ruoliAny'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 403
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | authorization-roles-deny-notfound-ApplicativoBlockingIDA01ExampleClient1.json |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1| ApplicativoBlockingIDA01ExampleExternalClient1 | authorization-roles-deny-notfound-ApplicativoBlockingIDA01ExampleExternalClient1SoloCertificato.json |



@autorizzazioneSicurezzaMessaggioPuntualeRuoliOkApplicativoCensito
Scenario Outline: Test autorizzazione sicurezza messaggio con autorizzazione sia puntuale che per ruolo caso ok, applicativo censito puntualmente (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneModIRest/v1"
And path 'puntualeRuoli'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 |
| interno (noToken) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient4 | ApplicativoBlockingIDA01ExampleClient4 | ApplicativoBlockingIDA01ExampleClient4 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1SoloCertificato |
| esterno (noToken) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient4 | ApplicativoBlockingIDA01ExampleExternalClient4 | ApplicativoBlockingIDA01ExampleExternalClient4 |



@autorizzazioneSicurezzaMessaggioPuntualeRuoliOkApplicativoConRuoli
Scenario Outline: Test autorizzazione sicurezza messaggio con autorizzazione sia puntuale che per ruolo caso ok, applicativo che possiede i ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneModIRest/v1"
And path 'puntualeRuoli'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2SoloCertificato |



@autorizzazioneSicurezzaMessaggioPuntualeRuoliKo
Scenario Outline: Test autorizzazione sicurezza messaggio ruolo caso ko, dove l'applicativo non possiede tutti i ruoli e non e' censito puntualmente (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneModIRest/v1"
And path 'puntualeRuoli'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 403
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleClient3.json |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient3| ApplicativoBlockingIDA01ExampleExternalClient3 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleExternalClient3SoloCertificato.json |
