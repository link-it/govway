Feature: Testing token Autorizzazione Token

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }


@autorizzazioneSicurezzaTokenHttpsRequiredOk
Scenario Outline: Test autorizzazione token puntuale caso ok (applicativo dominio <tipo-test>) con controllo anche del canale

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'httpsRequired'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('requestConHeader.xml')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Token-Application == '<application>'
And match header GovWay-TestSuite-GovWay-Application == '<application>'

Examples:
| tipo-test | fruitore | username | password | application |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 |



@autorizzazioneSicurezzaTokenHttpsRequiredKo
Scenario Outline: Test autorizzazione token puntuale caso ok (applicativo dominio <tipo-test>) con controllo anche del canale dove il canale è differente dall'applicativo

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'httpsRequired2'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 500
And match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | authorization-deny-canale-differente-applicativo.xml |





@autorizzazioneSicurezzaTokenHttpsRequiredOkConIntermediario
Scenario Outline: Test autorizzazione token puntuale caso ok (applicativo dominio <tipo-test>) con controllo anche del canale dove il canale è differente dall'applicativo, ma il soggetto del canale è un intermediario

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'httpsRequired3'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('requestConHeader.xml')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Token-Application == '<application>'
And match header GovWay-TestSuite-GovWay-Application == '<application>'

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
| tipo-test | fruitore | username | password | application |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 |




@autorizzazioneSicurezzaTokenHttpsRequiredKoConIntermediario
Scenario Outline: Test autorizzazione token puntuale caso ok (applicativo dominio <tipo-test>) con controllo anche del canale dove il canale è differente dall'applicativo ed il soggetto del canale è un intermediario non autorizzato

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'httpsRequired4'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 500
And match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/<response>')
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
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | authorization-deny-canale-differente-applicativo-intermediario-non-autorizzato.xml |






@autorizzazioneSicurezzaTokenPuntualeOk
Scenario Outline: Test autorizzazione token puntuale caso ok (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'puntuale'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('requestConHeader.xml')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Token-Application == '<application>'
And match header GovWay-TestSuite-GovWay-Application == '<application>'

Examples:
| tipo-test | fruitore | username | password | application |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken1 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken1 | ApplicativoBlockingIDA01ExampleExternalClientToken1 | ApplicativoBlockingIDA01ExampleExternalClientToken1 |


@autorizzazioneSicurezzaTokenPuntualeKo
Scenario Outline: Test autorizzazione token puntuale caso ko (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'puntuale'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 500
And match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | authorization-deny-ApplicativoBlockingIDA01ExampleClient2.xml |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken2-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken2-InternalGenerator | authorization-deny-ApplicativoBlockingIDA01ExampleClientToken2.xml |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 | authorization-deny-ApplicativoBlockingIDA01ExampleExternalClient2.xml |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken2 | ApplicativoBlockingIDA01ExampleExternalClientToken2 | authorization-deny-ApplicativoBlockingIDA01ExampleExternalClientToken2.xml |


@autorizzazioneSicurezzaTokenRuoliAllOk
Scenario Outline: Test autorizzazione token ruolo caso ok (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAll'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('requestConHeader.xml')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Token-Application == '<applicativo>'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken2-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken2-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken2 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken2 | ApplicativoBlockingIDA01ExampleExternalClientToken2 | ApplicativoBlockingIDA01ExampleExternalClientToken2 |


@autorizzazioneSicurezzaTokenRuoliAllKoSoloUnRuolo
Scenario Outline: Test autorizzazione token ruolo caso ko, dove l'applicativo possiede solo un ruolo (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAll'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 500
And match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleClient3.xml |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken3-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken3-InternalGenerator | authorization-roles-deny-ApplicativoBlockingIDA01ExampleClientToken3.xml |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleExternalClient3.xml |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken3 | ApplicativoBlockingIDA01ExampleExternalClientToken3 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleExternalClientToken3.xml |


@autorizzazioneSicurezzaTokenRuoliAllKoNessunRuolo
Scenario Outline: Test autorizzazione token ruolo caso ko, dove l'applicativo non possiede ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAll'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 500
And match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleClient1.xml |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | authorization-roles-deny-ApplicativoBlockingIDA01ExampleClientToken1.xml |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleExternalClient1.xml |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken1 | ApplicativoBlockingIDA01ExampleExternalClientToken1 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleExternalClientToken1.xml |


@autorizzazioneSicurezzaTokenRuoliAnyOkSoloUnRuolo
Scenario Outline: Test autorizzazione token ruolo caso ok, in cui l'applicativo possiede solamente uno dei due ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAny'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('requestConHeader.xml')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Token-Application == '<applicativo>'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken3-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken3-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken3 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3 |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken3 | ApplicativoBlockingIDA01ExampleExternalClientToken3 | ApplicativoBlockingIDA01ExampleExternalClientToken3 |


@autorizzazioneSicurezzaTokenRuoliAnyOkTuttiRuoli
Scenario Outline: Test autorizzazione token ruolo caso ok, in cui l'applicativo possiede tutti e due i ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAny'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('requestConHeader.xml')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Token-Application == '<applicativo>'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken2-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken2-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken2 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken2 | ApplicativoBlockingIDA01ExampleExternalClientToken2 | ApplicativoBlockingIDA01ExampleExternalClientToken2 |


@autorizzazioneSicurezzaTokenRuoliAnyKoNessunRuolo
Scenario Outline: Test autorizzazione token ruolo caso ko, dove l'applicativo non possiede ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAny'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 500
And match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | authorization-roles-deny-notfound-ApplicativoBlockingIDA01ExampleClient1.xml |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | authorization-roles-deny-notfound-ApplicativoBlockingIDA01ExampleClientToken1.xml |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1| ApplicativoBlockingIDA01ExampleExternalClient1 | authorization-roles-deny-notfound-ApplicativoBlockingIDA01ExampleExternalClient1.xml |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken1 | ApplicativoBlockingIDA01ExampleExternalClientToken1 | authorization-roles-deny-notfound-ApplicativoBlockingIDA01ExampleExternalClientToken1.xml |


@autorizzazioneSicurezzaTokenPuntualeRuoliOkApplicativoCensito
Scenario Outline: Test autorizzazione token con autorizzazione sia puntuale che per ruolo caso ok, applicativo censito puntualmente (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'puntualeRuoli'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('requestConHeader.xml')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Token-Application == '<applicativo>'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken1 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 | ApplicativoBlockingIDA01ExampleExternalClient1 |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken1 | ApplicativoBlockingIDA01ExampleExternalClientToken1 | ApplicativoBlockingIDA01ExampleExternalClientToken1 |



@autorizzazioneSicurezzaTokenPuntualeRuoliOkApplicativoConRuoli
Scenario Outline: Test autorizzazione token con autorizzazione sia puntuale che per ruolo caso ok, applicativo che possiede i ruoli (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'puntualeRuoli'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('requestConHeader.xml')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Token-Application == '<applicativo>'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken2-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken2-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken2 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken2 | ApplicativoBlockingIDA01ExampleExternalClientToken2 | ApplicativoBlockingIDA01ExampleExternalClientToken2 |


@autorizzazioneSicurezzaTokenPuntualeRuoliKo
Scenario Outline: Test autorizzazione token ruolo caso ko, dove l'applicativo non possiede tutti i ruoli e non e' censito puntualmente (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'puntualeRuoli'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 500
And match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleClient3.xml |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken3-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken3-InternalGenerator | authorization-roles-deny-ApplicativoBlockingIDA01ExampleClientToken3.xml |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient3| ApplicativoBlockingIDA01ExampleExternalClient3 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleExternalClient3.xml |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken3 | ApplicativoBlockingIDA01ExampleExternalClientToken3 | authorization-roles-deny-ApplicativoBlockingIDA01ExampleExternalClientToken3.xml |



@autorizzazioneSicurezzaTokenRuoliAnyRegistroOkSoloUnRuolo
Scenario Outline: Test autorizzazione token ruolo caso ok, in cui l'applicativo possiede solamente uno dei due ruoli di tipologia 'registro' (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAnyRegistro'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('requestConHeader.xml')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Token-Application == '<applicativo>'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken3-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken3-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken3 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3 |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken3 | ApplicativoBlockingIDA01ExampleExternalClientToken3 | ApplicativoBlockingIDA01ExampleExternalClientToken3 |


@autorizzazioneSicurezzaTokenRuoliAnyRegistroOkTuttiRuoli
Scenario Outline: Test autorizzazione token ruolo caso ok, in cui l'applicativo possiede tutti e due i ruoli di tipologia 'registro' (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAnyRegistro'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 200
And match response == read('requestConHeader.xml')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Token-Application == '<applicativo>'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken2-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken2-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken2 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken2 | ApplicativoBlockingIDA01ExampleExternalClientToken2 | ApplicativoBlockingIDA01ExampleExternalClientToken2 |


@autorizzazioneSicurezzaTokenRuoliAnyRegistroKoNessunRuolo
Scenario Outline: Test autorizzazione token ruolo caso ko, dove l'applicativo non possiede ruoli di tipologia 'registro' (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAnyRegistro'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 500
And match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | authorization-roles-deny-notfound-registro-ApplicativoBlockingIDA01ExampleClient1.xml |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | authorization-roles-deny-notfound-registro-ApplicativoBlockingIDA01ExampleClientToken1.xml |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1| ApplicativoBlockingIDA01ExampleExternalClient1 | authorization-roles-deny-notfound-registro-ApplicativoBlockingIDA01ExampleExternalClient1.xml |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken1 | ApplicativoBlockingIDA01ExampleExternalClientToken1 | authorization-roles-deny-notfound-registro-ApplicativoBlockingIDA01ExampleExternalClientToken1.xml |



@autorizzazioneSicurezzaTokenRuoliAnyEsternoOkSoloUnRuoloInternoConRuoloEsterno
Scenario Outline: Test autorizzazione token ruolo caso ok, in cui l'applicativo possiede solamente uno dei due ruoli di tipologia 'esterna', ma viene ignorato, mentre nel token è presente il ruolo esterno (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAnyEsterno'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = '<ruolo>'
When method post
Then status 200
And match response == read('requestConHeader.xml')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Token-Application == '<applicativo>'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo | ruolo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | modiRole3 |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken3-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken3-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken3 | modiRole3 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3 | modiRole3 |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken3 | ApplicativoBlockingIDA01ExampleExternalClientToken3 | ApplicativoBlockingIDA01ExampleExternalClientToken3 | modiRole3 |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | ModIRuolo1FonteQualsiasi |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken3-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken3-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken3 | ModIRuolo1FonteQualsiasi |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3 | ModIRuolo1FonteQualsiasi |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken3 | ApplicativoBlockingIDA01ExampleExternalClientToken3 | ApplicativoBlockingIDA01ExampleExternalClientToken3 | ModIRuolo1FonteQualsiasi |



@autorizzazioneSicurezzaTokenRuoliAnyEsternoOkSoloRuoloEsterno
Scenario Outline: Test autorizzazione token ruolo caso ok, in cui l'applicativo è sconosciuto ma nel token c'è il ruolo esterno (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAnyEsterno'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = '<ruolo>'
When method post
Then status 200
And match response == read('requestConHeader.xml')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | ruolo |
| non registrato | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientNonCorrispondeANessunApplicativoToken | ApplicativoBlockingIDA01ExampleClientNonCorrispondeANessunApplicativoToken | modiRole3 |
| non registrato | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientNonCorrispondeANessunApplicativoToken | ApplicativoBlockingIDA01ExampleClientNonCorrispondeANessunApplicativoToken | ModIRuolo1FonteQualsiasi |



@autorizzazioneSicurezzaTokenRuoliAnyEsternoKoSoloUnRuoloInternoSenzaRuoloEsterno
Scenario Outline: Test autorizzazione token ruolo caso ko, in cui l'applicativo possiede solamente uno dei due ruoli di tipologia 'esterna', ma viene ignorato, mentre nel token non è presente il ruolo esterno (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAnyEsterno'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 500
And match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient3 | ApplicativoBlockingIDA01ExampleClient3 | authorization-roles-deny-notfound-esterno-ApplicativoBlockingIDA01ExampleClient3.xml |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken3-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken3-InternalGenerator | authorization-roles-deny-notfound-esterno-ApplicativoBlockingIDA01ExampleClientToken3.xml |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient3 | ApplicativoBlockingIDA01ExampleExternalClient3 | authorization-roles-deny-notfound-esterno-ApplicativoBlockingIDA01ExampleExternalClient3.xml |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken3 | ApplicativoBlockingIDA01ExampleExternalClientToken3 | authorization-roles-deny-notfound-esterno-ApplicativoBlockingIDA01ExampleExternalClientToken3.xml |



@autorizzazioneSicurezzaTokenRuoliAnyEsternoKoSenzaRuoloEsterno
Scenario Outline: Test autorizzazione token ruolo caso ko, in cui l'applicativo è sconosciuto e nel token non è presente il ruolo esterno (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAnyEsterno'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 500
And match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| non registrato | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientNonCorrispondeANessunApplicativoToken | ApplicativoBlockingIDA01ExampleClientNonCorrispondeANessunApplicativoToken | authorization-roles-deny-notfound-esterno.xml |


@autorizzazioneSicurezzaTokenRuoliAnyEsternoOkTuttiRuoliInternoConRuoloEsterno
Scenario Outline: Test autorizzazione token ruolo caso ok, in cui l'applicativo possiede tutti e due i ruoli di tipologia 'esterna' (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAnyEsterno'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = '<ruolo>'
When method post
Then status 200
And match response == read('requestConHeader.xml')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Token-Application == '<applicativo>'
And match header GovWay-TestSuite-GovWay-Application == '<applicativo>'

Examples:
| tipo-test | fruitore | username | password | applicativo | ruolo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | modiRole3 |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken2-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken2-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken2 | modiRole3 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 | modiRole3 |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken2 | ApplicativoBlockingIDA01ExampleExternalClientToken2 | ApplicativoBlockingIDA01ExampleExternalClientToken2 | modiRole3 |


@autorizzazioneSicurezzaTokenRuoliAnyEsternoOkTuttiRuoliInternoSenzaRuoloEsterno
Scenario Outline: Test autorizzazione token ruolo caso ok, in cui l'applicativo interno possiede tutti e due i ruoli di tipologia 'esterna' ma nel token non è presente un ruolo (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAnyEsterno'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 500
And match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | authorization-roles-deny-notfound-esterno-ApplicativoBlockingIDA01ExampleClient2.xml |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken2-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken2-InternalGenerator | authorization-roles-deny-notfound-esterno-ApplicativoBlockingIDA01ExampleClientToken2.xml |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient2 | ApplicativoBlockingIDA01ExampleExternalClient2 | authorization-roles-deny-notfound-esterno-ApplicativoBlockingIDA01ExampleExternalClient2.xml |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken2 | ApplicativoBlockingIDA01ExampleExternalClientToken2 | authorization-roles-deny-notfound-esterno-ApplicativoBlockingIDA01ExampleExternalClientToken2.xml |



@autorizzazioneSicurezzaTokenRuoliAnyEsternoOkNessunRuoloInternoConRuoloEsterno
Scenario Outline: Test autorizzazione token ruolo caso ok, dove l'applicativo non possiede ruoli di tipologia 'esterna' e non è presente neanche nel token (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAnyEsterno'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = '<ruolo>'
When method post
Then status 200
And match response == read('requestConHeader.xml')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | ruolo |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | modiRole3 |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | modiRole3 |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1| ApplicativoBlockingIDA01ExampleExternalClient1 | modiRole3 |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken1 | ApplicativoBlockingIDA01ExampleExternalClientToken1 | modiRole3 |



@autorizzazioneSicurezzaTokenRuoliAnyEsternoKoNessunRuoloInternoNessunRuoloEsterno
Scenario Outline: Test autorizzazione token ruolo caso ko, dove l'applicativo non possiede ruoli di tipologia 'esterna' e non è presente neanche nel token (applicativo dominio <tipo-test>)

Given url govway_base_path + "/rest/out/<fruitore>/DemoSoggettoErogatore/DemoAutorizzazioneGenerazioneTokenSoap/v1"
And path 'ruoliAnyEsterno'
And request read('requestConHeader.xml')
And header Content-Type = 'application/soap+xml'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header govway-testsuite-role = 'undefined'
When method post
Then status 500
And match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/<response>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

Examples:
| tipo-test | fruitore | username | password | response |
| interno | DemoSoggettoFruitore | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | authorization-roles-deny-notfound-esterno-ApplicativoBlockingIDA01ExampleClient1.xml |
| interno (noCertificato) | DemoSoggettoFruitore | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | ApplicativoBlockingIDA01ExampleClientToken1-InternalGenerator | authorization-roles-deny-notfound-esterno-ApplicativoBlockingIDA01ExampleClientToken1.xml |
| esterno | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClient1| ApplicativoBlockingIDA01ExampleExternalClient1 | authorization-roles-deny-notfound-esterno-ApplicativoBlockingIDA01ExampleExternalClient1.xml |
| esterno (noCertificato) | DemoSoggettoFruitoreEsternoTestInterno | ApplicativoBlockingIDA01ExampleExternalClientToken1 | ApplicativoBlockingIDA01ExampleExternalClientToken1 | authorization-roles-deny-notfound-esterno-ApplicativoBlockingIDA01ExampleExternalClientToken1.xml |
