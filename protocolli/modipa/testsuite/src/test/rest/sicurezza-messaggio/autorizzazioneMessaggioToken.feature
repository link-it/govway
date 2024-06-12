Feature: Testing sicurezza messaggio+token e Token, Autorizzazione

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }


@autorizzazioneSicurezzaMessaggioTokenHttpsRequiredOk
Scenario Outline: Test autorizzazione sicurezza messaggio+token puntuale caso ok con controllo anche del canale

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'httpsRequired'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<username>'
And match header GovWay-TestSuite-GovWay-Token-Application == '<username>'

Examples:
| tipo-test | fruitore | username | password |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 |



@autorizzazioneSicurezzaMessaggioTokenHttpsRequiredKo
Scenario Outline: Test autorizzazione sicurezza messaggio+token puntuale caso ko con controllo anche del canale dove il canale è differente dall'applicativo

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
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
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | authorization-deny-canale-differente-applicativo.json |



@autorizzazioneSicurezzaMessaggioTokenHttpsRequiredOkConIntermediario
Scenario Outline: Test autorizzazione sicurezza messaggio+token puntuale caso ok con controllo anche del canale dove il canale è differente dall'applicativo, ma il soggetto del canale è un intermediario

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'httpsRequired3'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<username>'
And match header GovWay-TestSuite-GovWay-Token-Application == '<username>'

* def get_diagnostici_intermediario = read('classpath:utils/get_diagnostici_intermediario.js')
* def tid = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostici_intermediario(tid) 
* match result[0].MESSAGGIO == 'Richiesta ricevuta dal soggetto intermediario DemoSoggettoFruitoreEsternoIntermediario'

* def get_eventi = read('classpath:utils/get-eventi.js')
* def id_eventi = get_eventi(tid) 
* karate.log("ID EVENTI ["+id_eventi+"]")
* def get_credenziale_by_id = read('classpath:utils/credenziale_mittente.js')
* def credenziale = get_credenziale_by_id(id_eventi) 
* karate.log("Credenziale ["+credenziale+"]")
* match credenziale contains 'intermediario=DemoSoggettoFruitoreEsternoIntermediario'

* def get_mittente_transazione = read('classpath:utils/mittente-transazione.js')
* def mittente_transazione = get_mittente_transazione(tid)
* karate.log("Transazioni, mittente ["+mittente_transazione+"]")
* match mittente_transazione == 'DemoSoggettoFruitoreEsterno' 

* def get_idporta_mittente_transazione = read('classpath:utils/idporta-mittente-transazione.js')
* def idporta_mittente_transazione = get_idporta_mittente_transazione(tid) 
* karate.log("Transazioni, idporta mittente ["+idporta_mittente_transazione+"]")
* match idporta_mittente_transazione == 'domain/modipa/DemoSoggettoFruitoreEsterno'  

* def get_servizio_applicativo_mittente_transazione = read('classpath:utils/servizio-applicativo-mittente-transazione.js')
* def servizio_applicativo_mittente_transazione = get_servizio_applicativo_mittente_transazione(tid)
* karate.log("Transazioni, servizio_applicativo_mittente ["+servizio_applicativo_mittente_transazione+"]")
* match servizio_applicativo_mittente_transazione == 'ApplicativoBlockingIDA01ExampleExternalClient1'

* def get_traccia_informazioni_base = read('classpath:utils/get_traccia_informazioni_base.js')
* def traccia_informazioni_base = get_traccia_informazioni_base(tid)
* karate.log("get_traccia_informazioni_base ["+get_traccia_informazioni_base+"]")
* match traccia_informazioni_base contains deep { 'mittente': 'DemoSoggettoFruitoreEsterno' }
* match traccia_informazioni_base contains deep { 'idporta_mittente': 'domain/modipa/DemoSoggettoFruitoreEsterno' }
* match traccia_informazioni_base contains deep { 'sa_fruitore': 'ApplicativoBlockingIDA01ExampleExternalClient1' }

* def get_traccia_trasmissione = read('classpath:utils/get_traccia_trasmissione.js')
* def traccia_trasmissione = get_traccia_trasmissione(tid)
* karate.log("get_traccia_trasmissione ["+get_traccia_trasmissione+"]")
* match traccia_trasmissione contains deep { 'origine': 'DemoSoggettoFruitoreEsterno' }
* match traccia_trasmissione contains deep { 'idporta_origine': 'domain/modipa/DemoSoggettoFruitoreEsterno' }

Examples:
| tipo-test | fruitore | username | password |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 |




@autorizzazioneSicurezzaMessaggioTokenHttpsRequiredKoConIntermediario
Scenario Outline: Test autorizzazione sicurezza messaggio+token puntuale caso ko con controllo anche del canale dove il canale è differente dall'applicativo ed il soggetto del canale è un intermediario non autorizzato

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'httpsRequired4'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 403
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def get_diagnostici_intermediario = read('classpath:utils/get_diagnostici_intermediario.js')
* def tid = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostici_intermediario(tid) 
* match result[0].MESSAGGIO == 'Richiesta ricevuta dal soggetto intermediario DemoSoggettoFruitoreEsternoIntermediario2'

* def get_eventi = read('classpath:utils/get-eventi.js')
* def id_eventi = get_eventi(tid) 
* karate.log("ID EVENTI ["+id_eventi+"]")
* def get_credenziale_by_id = read('classpath:utils/credenziale_mittente.js')
* def credenziale = get_credenziale_by_id(id_eventi) 
* karate.log("Credenziale ["+credenziale+"]")
* match credenziale contains 'intermediario=DemoSoggettoFruitoreEsternoIntermediario2'

* def get_mittente_transazione = read('classpath:utils/mittente-transazione.js')
* def mittente_transazione = get_mittente_transazione(tid)
* karate.log("Transazioni, mittente ["+mittente_transazione+"]")
* match mittente_transazione == 'DemoSoggettoFruitoreEsterno' 

* def get_idporta_mittente_transazione = read('classpath:utils/idporta-mittente-transazione.js')
* def idporta_mittente_transazione = get_idporta_mittente_transazione(tid) 
* karate.log("Transazioni, idporta mittente ["+idporta_mittente_transazione+"]")
* match idporta_mittente_transazione == 'domain/modipa/DemoSoggettoFruitoreEsterno'  

* def get_servizio_applicativo_mittente_transazione = read('classpath:utils/servizio-applicativo-mittente-transazione.js')
* def servizio_applicativo_mittente_transazione = get_servizio_applicativo_mittente_transazione(tid)
* karate.log("Transazioni, servizio_applicativo_mittente ["+servizio_applicativo_mittente_transazione+"]")
* match servizio_applicativo_mittente_transazione == 'ApplicativoBlockingIDA01ExampleExternalClient1'

* def get_traccia_informazioni_base = read('classpath:utils/get_traccia_informazioni_base.js')
* def traccia_informazioni_base = get_traccia_informazioni_base(tid)
* karate.log("get_traccia_informazioni_base ["+get_traccia_informazioni_base+"]")
* match traccia_informazioni_base contains deep { 'mittente': 'DemoSoggettoFruitoreEsterno' }
* match traccia_informazioni_base contains deep { 'idporta_mittente': 'domain/modipa/DemoSoggettoFruitoreEsterno' }
* match traccia_informazioni_base contains deep { 'sa_fruitore': 'ApplicativoBlockingIDA01ExampleExternalClient1' }

* def get_traccia_trasmissione = read('classpath:utils/get_traccia_trasmissione.js')
* def traccia_trasmissione = get_traccia_trasmissione(tid)
* karate.log("get_traccia_trasmissione ["+get_traccia_trasmissione+"]")
* match traccia_trasmissione contains deep { 'origine': 'DemoSoggettoFruitoreEsterno' }
* match traccia_trasmissione contains deep { 'idporta_origine': 'domain/modipa/DemoSoggettoFruitoreEsterno' }

Examples:
| tipo-test | fruitore | username | password | response |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | authorization-deny-canale-differente-applicativo-intermediario-non-autorizzato-auth-messaggio-token.json |





@autorizzazioneSicurezzaMessaggioTokenPuntualeKoApplicativoSenzaToken
Scenario Outline: Test autorizzazione sicurezza messaggio+token puntuale caso ko con applicativo in cui non è stato definito il token (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'puntuale'
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
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient4 | ApplicativoBlockingIDA01ExampleClient4 | authorization-deny-noToken-ApplicativoBlockingIDA01ExampleClient4.json |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient4 | ApplicativoBlockingIDA01ExampleExternalClient4 | authorization-deny-noToken-ApplicativoBlockingIDA01ExampleExternalClient4.json |


@autorizzazioneSicurezzaMessaggioTokenPuntualeKoApplicativoMessaggioDifferenteApplicativoToken
Scenario Outline: Test autorizzazione sicurezza messaggio+token puntuale caso ko con applicativo messaggio identificato differente dall'applicativo token identificato (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'puntuale'
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
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient1ConClientIdTokenClient2 | ApplicativoBlockingIDA01ExampleClient1ConClientIdTokenClient2 | authorization-deny-application-not-match-dominio-interno.json |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1ConClientIdTokenClient2 | ApplicativoBlockingIDA01ExampleExternalClient1ConClientIdTokenClient2 | authorization-deny-application-not-match-dominio-esterno.json |


@autorizzazioneSicurezzaMessaggioTokenPuntualeKoApplicativoSenzaSicurezzaMessaggio
Scenario Outline: Test autorizzazione sicurezza messaggio+token puntuale caso ko con applicativo in cui non è stato definito la sicurezza messaggio (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'puntuale'
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
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | authorization-deny-noToken-ApplicativoBlockingIDA01ExampleClientToken1.json |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken1 | ApplicativoBlockingIDA01ExampleExternalClientToken1 | authorization-deny-noToken-ApplicativoBlockingIDA01ExampleExternalClientToken1.json |




@autorizzazioneSicurezzaMessaggioTokenPuntualeOk
Scenario Outline: Test autorizzazione sicurezza messaggio+token puntuale caso ok (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'puntuale'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<username>'
And match header GovWay-TestSuite-GovWay-Token-Application == '<username>'

Examples:
| tipo-test | fruitore | username | password |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 |



@autorizzazioneSicurezzaMessaggioTokenPuntualeKo
Scenario Outline: Test autorizzazione sicurezza messaggio+token puntuale caso ko (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'puntuale'
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
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | authorization-deny-ApplicativoBlockingIDA01ExampleClient2.json |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 | authorization-deny-ApplicativoBlockingIDA01ExampleExternalClient2.json |


@autorizzazioneSicurezzaMessaggioTokenRuoliAllOk
Scenario Outline: Test autorizzazione sicurezza messaggio+token ruolo caso ok (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'ruoliAll'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<username>'
And match header GovWay-TestSuite-GovWay-Token-Application == '<username>'

Examples:
| tipo-test | fruitore | username | password |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 |


@autorizzazioneSicurezzaMessaggioTokenRuoliAllKoSoloUnRuolo
Scenario Outline: Test autorizzazione sicurezza messaggio+token ruolo caso ko, dove l'applicativo possiede solo un ruolo (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'ruoliAll'
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
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleClient3.json |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleExternalClient3.json |


@autorizzazioneSicurezzaMessaggioTokenRuoliAllKoNessunRuolo
Scenario Outline: Test autorizzazione sicurezza messaggio+token ruolo caso ko, dove l'applicativo non possiede ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'ruoliAll'
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
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleClient1.json |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleExternalClient1.json |


@autorizzazioneSicurezzaMessaggioTokenRuoliAnyOkSoloUnRuolo
Scenario Outline: Test autorizzazione sicurezza messaggio+token ruolo caso ok, in cui l'applicativo possiede solamente uno dei due ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'ruoliAny'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<username>'
And match header GovWay-TestSuite-GovWay-Token-Application == '<username>'

Examples:
| tipo-test | fruitore | username | password |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3 |


@autorizzazioneSicurezzaMessaggioTokenRuoliAnyOkTuttiRuoli
Scenario Outline: Test autorizzazione sicurezza messaggio+token ruolo caso ok, in cui l'applicativo possiede tutti e due i ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'ruoliAny'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<username>'
And match header GovWay-TestSuite-GovWay-Token-Application == '<username>'

Examples:
| tipo-test | fruitore | username | password |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 |


@autorizzazioneSicurezzaMessaggioTokenRuoliAnyKoNessunRuolo
Scenario Outline: Test autorizzazione sicurezza messaggio+token ruolo caso ko, dove l'applicativo non possiede ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'ruoliAny'
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
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | authorization-roles-deny-notfound-ApplicativoBlockingIDA01ExampleClient1.json |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1| ApplicativoBlockingIDA01ExampleExternalClient1 | authorization-roles-deny-notfound-ApplicativoBlockingIDA01ExampleExternalClient1.json |



@autorizzazioneSicurezzaMessaggioTokenPuntualeRuoliOkApplicativoCensito
Scenario Outline: Test autorizzazione sicurezza messaggio+token con autorizzazione sia puntuale che per ruolo caso ok, applicativo censito puntualmente (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'puntualeRuoli'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<username>'
And match header GovWay-TestSuite-GovWay-Token-Application == '<username>'

Examples:
| tipo-test | fruitore | username | password |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 |



@autorizzazioneSicurezzaMessaggioTokenPuntualeRuoliOkApplicativoConRuoli
Scenario Outline: Test autorizzazione sicurezza messaggio+token con autorizzazione sia puntuale che per ruolo caso ok, applicativo che possiede i ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'puntualeRuoli'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<username>'
And match header GovWay-TestSuite-GovWay-Token-Application == '<username>'

Examples:
| tipo-test | fruitore | username | password |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 |



@autorizzazioneSicurezzaMessaggioTokenPuntualeRuoliKo
Scenario Outline: Test autorizzazione sicurezza messaggio+token ruolo caso ko, dove l'applicativo non possiede tutti i ruoli e non e' censito puntualmente (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneModITokenRest/v1"
And path 'puntualeRuoli'
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
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleClient3.json |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient3| ApplicativoBlockingIDA01ExampleExternalClient3 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleExternalClient3.json |






@autorizzazioneSicurezzaTokenOAuthPuntualeOk
Scenario Outline: Test autorizzazione token oauth puntuale caso ok (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneTokenOAuth-<tipo-api>/v1"
And path 'idar04'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = 'purposeId-<username>'
And header simulazionepdnd-audience = 'DemoAutorizzazioneTokenOAuth-<tipo-api>/v1'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Application == '<application>'

Examples:
| tipo-test | tipo-api | tipo-keystore-applicativo | fruitore | username | password | application |
| interno | JWK | pkcs12 | DemoSoggettoFruitore | ExampleExternalClient1-SimulazionePDND-JWK | ExampleExternalClient1-SimulazionePDND-JWK | ExampleExternalClient1-SimulazionePDND-JWK |
| interno | PDND | pkcs12 | DemoSoggettoFruitore | ExampleExternalClient1-SimulazionePDND-JWK | ExampleExternalClient1-SimulazionePDND-JWK | ExampleExternalClient1-SimulazionePDND-JWK |
| esterno (con certificato) | JWK | pkcs12 | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01-SimulazionePDND-JWK |
| esterno (con certificato) | PDND | pkcs12 | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01-SimulazionePDND-JWK |
| interno | JWK | jwk | DemoSoggettoFruitore | ApplicativoBlockingJWK | ApplicativoBlockingJWK | ApplicativoBlockingJWK |
| interno | PDND | jwk | DemoSoggettoFruitore | ApplicativoBlockingJWK | ApplicativoBlockingJWK | ApplicativoBlockingJWK |
| esterno (con certificato) | JWK | jwk | DemoSoggettoFruitore | ExampleExternalClient2-SimulazionePDND-JWK-Interno | ExampleExternalClient2-SimulazionePDND-JWK-Interno | ExampleExternalClient2-SimulazionePDND-JWK |
| esterno (con certificato) | PDND | jwk | DemoSoggettoFruitore | ExampleExternalClient2-SimulazionePDND-JWK-Interno | ExampleExternalClient2-SimulazionePDND-JWK-Interno | ExampleExternalClient2-SimulazionePDND-JWK |
| interno | JWK | keypair | DemoSoggettoFruitore | ApplicativoBlockingKeyPair | ApplicativoBlockingKeyPair | ApplicativoBlockingKeyPair |
| interno | PDND | keypair | DemoSoggettoFruitore | ApplicativoBlockingKeyPair | ApplicativoBlockingKeyPair | ApplicativoBlockingKeyPair |
| esterno | JWK | keypair | DemoSoggettoFruitore | ExampleExternalClient3-SimulazionePDND-JWK-Interno | ExampleExternalClient3-SimulazionePDND-JWK-Interno | ExampleExternalClient3-SimulazionePDND-JWK |
| esterno | PDND | keypair | DemoSoggettoFruitore | ExampleExternalClient3-SimulazionePDND-JWK-Interno | ExampleExternalClient3-SimulazionePDND-JWK-Interno | ExampleExternalClient3-SimulazionePDND-JWK |


@autorizzazioneSicurezzaTokenOAuthPuntualeKo
Scenario Outline: Test autorizzazione token oauth puntuale caso ko (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneTokenOAuth-<tipo-api>/v1"
And path 'idar04'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = 'purposeId-<username>'
And header simulazionepdnd-audience = 'DemoAutorizzazioneTokenOAuth-<tipo-api>/v1'
When method post
Then status 403
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | tipo-api | tipo-keystore-applicativo | fruitore | username | password | application | response |
| esterno | JWK | pkcs12 | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3-SimulazionePDND-JWK | authorization-deny-ApplicativoBlockingIDA01ExampleClient3.json |
| esterno | PDND | pkcs12 | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3-SimulazionePDND-JWK | authorization-deny-ApplicativoBlockingIDA01ExampleClient3.json |
| interno | JWK | jwk | DemoSoggettoFruitore | ExampleExternalClient4-SimulazionePDND-JWK | ExampleExternalClient4-SimulazionePDND-JWK | ExampleExternalClient4-SimulazionePDND-JWK | authorization-deny-ExampleExternalClient4-SimulazionePDND-JWK.json |
| interno | PDND | jwk | DemoSoggettoFruitore | ExampleExternalClient4-SimulazionePDND-JWK | ExampleExternalClient4-SimulazionePDND-JWK | ExampleExternalClient4-SimulazionePDND-JWK | authorization-deny-ExampleExternalClient4-SimulazionePDND-JWK.json |





@autorizzazioneSicurezzaTokenOAuthPuntualeOkIntegrityRest01
Scenario Outline: Test autorizzazione token oauth puntuale caso ok, con utilizzo dell'integrity rest 01 (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneTokenOAuthIntegrity01/v1"
And path 'idar03'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = 'purposeId-<username>'
And header simulazionepdnd-audience = 'DemoAutorizzazioneTokenOAuthIntegrity01/v1'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | application |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 |  ApplicativoBlockingIDA01 |



@autorizzazioneSicurezzaMessaggioTokenPuntualeKoIntegrityRest01
Scenario Outline: Test autorizzazione sicurezza messaggio+token puntuale caso ko (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneTokenOAuthIntegrity01/v1"
And path 'idar03'
And request read('request.json')
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = 'purposeId-<username>'
And header simulazionepdnd-audience = 'DemoAutorizzazioneTokenOAuthIntegrity01/v1'
When method post
Then status 403
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | authorization-deny-ApplicativoBlockingIDA01ExampleClient3-integrity.json |
