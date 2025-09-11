Feature: Testing get informazioni dalla PDND

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }

    * def clean_remote_store_key = read('classpath:utils/remote_store_key.js')

    * def encode_base64 = read('classpath:utils/encode-base64.js')
    * def encode_base64_from_bytes = read('classpath:utils/encode-base64-from-bytes.js')
    * def no_key_base64 = encode_base64('KEY_UNDEFINED');

    * def get_id_by_credenziale = read('classpath:utils/credenziale_mittente.js')
    * def get_credenziale_by_refid = read('classpath:utils/credenziale_mittente.js')

    * def credenziale_max_feature = callonce read('classpath:utils/credenziale_mittente_max.feature')
    
    * def get_diagnostico = read('classpath:utils/get_diagnostico.js')

    * def decode_base64 = read('classpath:utils/decode-base64.js')



@getInformazioniClientOrganizationApiIDAUTH
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, senza prima aver mai prelevata la chiave essendo solamente un pattern ID_AUTH

* def result = clean_remote_store_key('KID-ApplicativoBlockingIDA01ExampleClient3')
* def result = clean_remote_store_key_client_id_prefix('DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3')

# Ricalcolo in ogni test per essere meno suscettibile al cambio minuto
* def today = java.time.LocalDateTime.now()
* def formatter = java.time.format.DateTimeFormatter.ofPattern("YY-MM-dd-HH-mm");
* def formattedDate = today.format(formatter)
* def formatterYYYYMMDD = java.time.format.DateTimeFormatter.ofPattern("YYYYMMdd");
* def formattedDateYYYYMMDD = today.format(formatterYYYYMMDD)
* def formatterHHmm = java.time.format.DateTimeFormatter.ofPattern("HHmm");
* def formattedDateHHmm = today.format(formatterHHmm)
* def formatterClient = java.time.format.DateTimeFormatter.ofPattern("YYYYMMdd-HHmm");
* def formattedDateClient = today.format(formatterClient)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDAuth/v2"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient3', password: 'ApplicativoBlockingIDA01ExampleClient3' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDAuth/v2'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0001'
And match header X-RateLimit-Remaining == '1'
And match header X-RateLimit-Limit == '2'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationName == 'Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01ExampleClient3'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationCategory == 'PA'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationSubUnit == 'AOO'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationExternal == 'IPA c_c000_'+formattedDate+'_0001'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-ClientName == 'client-'+formattedDateClient
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-ClientDescription == 'client1 di test '+formattedDateClient

# controlli cache pdnd

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate+'_0001"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-bbbb-12345678f8dd"'

* def data_registrazione = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento = result[0].DATA_AGGIORNAMENTO+''

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01ExampleClient3'

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"kind":"PA"'
* match pdndOrganizationJson contains '"subUnitType":"AOO"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0001"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01ExampleClient3"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-bbbb-12345678f8dd"'
* match pdndClientJson contains '"name":"'+'client-'+formattedDateClient+'"'
* match pdndClientJson contains '"description":"'+'client1 di test '+formattedDateClient+'"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# Effettuo nuovamente l'invocazione, ora dovrebbe essere in cache

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDAuth/v2"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient3', password: 'ApplicativoBlockingIDA01ExampleClient3' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDAuth/v2'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0001'
And match header X-RateLimit-Remaining == '0'
And match header X-RateLimit-Limit == '2'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationName == 'Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01ExampleClient3'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationCategory == 'PA'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationSubUnit == 'AOO'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationExternal == 'IPA c_c000_'+formattedDate+'_0001'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-ClientName == 'client-'+formattedDateClient
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-ClientDescription == 'client1 di test '+formattedDateClient

# controlli cache pdnd

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate+'_0001"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-bbbb-12345678f8dd"'

* def data_registrazione_after = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_after = result[0].DATA_AGGIORNAMENTO+''
* match data_registrazione_after == data_registrazione
* match data_aggiornamento_after == data_aggiornamento

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01ExampleClient3'

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"kind":"PA"'
* match pdndOrganizationJson contains '"subUnitType":"AOO"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0001"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01ExampleClient3"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-bbbb-12345678f8dd"'
* match pdndClientJson contains '"name":"'+'client-'+formattedDateClient+'"'
* match pdndClientJson contains '"description":"'+'client1 di test '+formattedDateClient+'"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# Effettuo nuovamente l'invocazione, ora dovrebbe essere violata la policy di Rate Limiting

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDAuth/v2"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient3', password: 'ApplicativoBlockingIDA01ExampleClient3' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDAuth/v2'
When method post
Then status 429
And match response == read('error-bodies/rate-limiting-violato.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == '#notpresent'
And match header X-RateLimit-Remaining == '0'
And match header X-RateLimit-Limit == '2'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationName == '#notpresent'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationCategory == '#notpresent'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationSubUnit == '#notpresent'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationExternal == '#notpresent'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-ClientName == '#notpresent'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-ClientDescription == '#notpresent'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'violate(1)'
* match result[0].MESSAGGIO contains 'rispettate(0)'
* def result = get_diagnostico(tiderogazione, 'Il numero massimo di richieste (rilevato:3 soglia:2) risulta raggiunto') 
* match result[0].MESSAGGIO contains 'PDNDOrganizationName'







@getInformazioniClientOrganizationApiIntegrity
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, prima però viene prelevata la chiave essendo solamente un pattern INTEGRITY con truststore PDND

* def result = clean_remote_store_key('KID-ApplicativoBlockingIDA01')
* def result = clean_remote_store_key_client_id_prefix('DemoSoggettoFruitore/ApplicativoBlockingIDA01')
* def result = clean_remote_store_key('KID-ExampleServer')

# Ricalcolo in ogni test per essere meno suscettibile al cambio minuto
* def today = java.time.LocalDateTime.now()
* def formatter = java.time.format.DateTimeFormatter.ofPattern("YY-MM-dd-HH-mm");
* def formattedDate = today.format(formatter)
* def formatterYYYYMMDD = java.time.format.DateTimeFormatter.ofPattern("YYYYMMdd");
* def formattedDateYYYYMMDD = today.format(formatterYYYYMMDD)
* def formatterHHmm = java.time.format.DateTimeFormatter.ofPattern("HHmm");
* def formattedDateHHmm = today.format(formatterHHmm)
* def formatterClient = java.time.format.DateTimeFormatter.ofPattern("YYYYMMdd-HHmm");
* def formattedDateClient = today.format(formatterClient)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDIntegrity/v2"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDIntegrity/v2'
And header GovWay-TestSuite-PDND-RateLimiting = 'TestStandard';
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0002'
And match header X-RateLimit-Remaining == '1'
And match header X-RateLimit-Limit == '2'

# controlli govway-token
* def encodedHeaderGWToken = responseHeaders['GovWay-TestSuite-Reply-GovWay-Token'][0]
* def decodedHeaderGWToken = decode_base64(encodedHeaderGWToken)
* karate.log("Decoded: ", decodedHeaderGWToken)
* json headerJsonGWToken = decodedHeaderGWToken
* karate.log("Decoded as json: ", headerJsonGWToken)
* def nomeOrganization = 'Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01'
* def externalIdOrganization = 'c_c000_'+formattedDate+'_0002'
* def nomeClient = 'client-'+formattedDateClient
* def descrizioneClient = 'client1 di test '+formattedDateClient
* def headerJsonGWTokenExpected = 
"""
{ 
   pdnd: {
     organization: {
        name: #(nomeOrganization), 
        category: 'PA', 
        subUnit: 'AOO',
        externalOrigin: 'IPA',
        externalId: #(externalIdOrganization)
     },
     client: {
        name: #(nomeClient),
        description: #(descrizioneClient)
     }
   }
 }
"""
* match headerJsonGWToken contains deep headerJsonGWTokenExpected

# controlli cache pdnd

* def result = get_remote_store_key('KID-ApplicativoBlockingIDA01') 

* match result[0].KID contains 'KID-ApplicativoBlockingIDA01'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

* def resultServer = get_remote_store_key('KID-ExampleServer') 

* match resultServer[0].KID contains 'KID-ExampleServer'
# Effettua la conversione Base64 del campo 'data'
* def actualDataServer = resultServer[0].CONTENT_KEY
* def actualDataServerBase64 = encode_base64_from_bytes(actualDataServer)
* match actualDataServerBase64 != no_key_base64

* match result[0].CLIENT_ID == '#null'
* match result[0].ORGANIZATION_DETAILS == '#null'
* match result[0].CLIENT_DETAILS == '#null'

* def data_registrazione_kid = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_kid = result[0].DATA_AGGIORNAMENTO+''

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/ApplicativoBlockingIDA01') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/ApplicativoBlockingIDA01'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate+'_0002"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-bbbb-12345678f8dd"'

* def data_registrazione_client = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_client = result[0].DATA_AGGIORNAMENTO+''

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/ApplicativoBlockingIDA01')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01' 

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"kind":"PA"'
* match pdndOrganizationJson contains '"subUnitType":"AOO"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0002"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-bbbb-12345678f8dd"'
* match pdndClientJson contains '"name":"'+'client-'+formattedDateClient+'"'
* match pdndClientJson contains '"description":"'+'client1 di test '+formattedDateClient+'"'


# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# Effettuo nuovamente l'invocazione, ora dovrebbe essere in cache

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDIntegrity/v2"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDIntegrity/v2'
And header GovWay-TestSuite-PDND-RateLimiting = 'TestStandard';
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0002'
And match header X-RateLimit-Remaining == '0'
And match header X-RateLimit-Limit == '2'

# controlli govway-token
* def encodedHeaderGWToken = responseHeaders['GovWay-TestSuite-Reply-GovWay-Token'][0]
* def decodedHeaderGWToken = decode_base64(encodedHeaderGWToken)
* karate.log("Decoded: ", decodedHeaderGWToken)
* json headerJsonGWToken = decodedHeaderGWToken
* karate.log("Decoded2: ", headerJsonGWToken)
* def nomeOrganization = 'Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01'
* def externalIdOrganization = 'c_c000_'+formattedDate+'_0002'
* def headerJsonGWTokenExpected = 
"""
{ 
   pdnd: {
     organization: {
        name: #(nomeOrganization), 
        category: 'PA', 
        subUnit: 'AOO',
        externalOrigin: 'IPA',
        externalId: #(externalIdOrganization)
     },
     client: {
        name: #(nomeClient),
        description: #(descrizioneClient)
     }
   }
 }
"""
* match headerJsonGWToken contains deep headerJsonGWTokenExpected

# controlli cache pdnd

* def result = get_remote_store_key('KID-ApplicativoBlockingIDA01') 

* match result[0].KID contains 'KID-ApplicativoBlockingIDA01'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

* def resultServer = get_remote_store_key('KID-ExampleServer') 

* match resultServer[0].KID contains 'KID-ExampleServer'
# Effettua la conversione Base64 del campo 'data'
* def actualDataServer = resultServer[0].CONTENT_KEY
* def actualDataServerBase64 = encode_base64_from_bytes(actualDataServer)
* match actualDataServerBase64 != no_key_base64

* match result[0].CLIENT_ID == '#null'
* match result[0].ORGANIZATION_DETAILS == '#null'
* match result[0].CLIENT_DETAILS == '#null'

* def data_registrazione_after_kid = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_after_kid = result[0].DATA_AGGIORNAMENTO+''

* match data_registrazione_after_kid == data_registrazione_kid
* match data_aggiornamento_after_kid == data_aggiornamento_kid

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/ApplicativoBlockingIDA01') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/ApplicativoBlockingIDA01'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate+'_0002"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-bbbb-12345678f8dd"'

* def data_registrazione_after_client = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_after_client = result[0].DATA_AGGIORNAMENTO+''

* match data_registrazione_after_client == data_registrazione_client
* match data_aggiornamento_after_client == data_aggiornamento_client

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/ApplicativoBlockingIDA01')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01' 

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"kind":"PA"'
* match pdndOrganizationJson contains '"subUnitType":"AOO"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0002"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-bbbb-12345678f8dd"'
* match pdndClientJson contains '"name":"'+'client-'+formattedDateClient+'"'
* match pdndClientJson contains '"description":"'+'client1 di test '+formattedDateClient+'"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# Effettuo nuovamente l'invocazione, ora dovrebbe essere violata la policy di Rate Limiting

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDIntegrity/v2"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDIntegrity/v2'
And header GovWay-TestSuite-PDND-RateLimiting = 'TestStandard';
When method post
Then status 429
And match response == read('error-bodies/rate-limiting-violato.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == '#notpresent'
And match header X-RateLimit-Remaining == '0'
And match header X-RateLimit-Limit == '2'
And match header GovWay-TestSuite-Reply-GovWay-Token == '#notpresent'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'violate(1)'
* match result[0].MESSAGGIO contains 'rispettate(0)'
* def result = get_diagnostico(tiderogazione, 'Il numero massimo di richieste (rilevato:3 soglia:2) risulta raggiunto') 
* match result[0].MESSAGGIO contains 'PDNDOrganizationName'







@getInformazioniClientOrganizationApiPrimaAuthPoiIntegrity
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, prima viene invocata un'azione che non fa scaturire il download della chiave poi viene prelevata la chiave usando un'altra azione che prevede pattern INTEGRITY con truststore PDND

* def result = clean_remote_store_key('KID-ApplicativoBlockingJWK')
* def result = clean_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK')
# va in concorrenza con il test precedente, questa verifica comunque è stata fatta nel test precedente di integrity
#* def result = clean_remote_store_key('KID-ExampleServer')

# Ricalcolo in ogni test per essere meno suscettibile al cambio minuto
* def today = java.time.LocalDateTime.now()
* def formatter = java.time.format.DateTimeFormatter.ofPattern("YY-MM-dd-HH-mm");
* def formattedDate = today.format(formatter)
* def formatterYYYYMMDD = java.time.format.DateTimeFormatter.ofPattern("YYYYMMdd");
* def formattedDateYYYYMMDD = today.format(formatterYYYYMMDD)
* def formatterHHmm = java.time.format.DateTimeFormatter.ofPattern("HHmm");
* def formattedDateHHmm = today.format(formatterHHmm)
* def formatterClient = java.time.format.DateTimeFormatter.ofPattern("YYYYMMdd-HHmm");
* def formattedDateClient = today.format(formatterClient)


# Invocazione con azione che prevede solo voucher

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDIntegrity/v2"
And path 'idauth'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingJWK', password: 'ApplicativoBlockingJWK' })
And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
And header simulazionepdnd-password = 'ApplicativoBlockingJWK'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingJWK'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDIntegrity/v2'
And header GovWay-TestSuite-PDND-RateLimiting = 'TestMisto';
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0003'
And match header X-RateLimit-Remaining == '3'
And match header X-RateLimit-Limit == '4'

# controlli cache pdnd

* def result = get_remote_store_key('KID-ApplicativoBlockingJWK') 

* match result == []

# va in concorrenza con il test precedente, questa verifica comunque è stata fatta nel test precedente di integrity
#* def resultServer = get_remote_store_key('KID-ExampleServer') 
#* match resultServer == []

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate+'_0003"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'

* def data_registrazione_auth = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_auth = result[0].DATA_AGGIORNAMENTO+''

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Comune di Esempio '+formattedDate+' ApplicativoBlockingJWK'

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"kind":"PA"'
* match pdndOrganizationJson contains '"subUnitType":"AOO"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0003"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingJWK"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'
* match pdndClientJson contains '"name":"'+'client-'+formattedDateClient+'"'
* match pdndClientJson contains '"description":"'+'client1 di test '+formattedDateClient+'"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# Invocazione con azione che prevede voucher e integrity

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDIntegrity/v2"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingJWK', password: 'ApplicativoBlockingJWK' })
And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
And header simulazionepdnd-password = 'ApplicativoBlockingJWK'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingJWK'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDIntegrity/v2'
And header GovWay-TestSuite-PDND-RateLimiting = 'TestMisto';
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0003'
And match header X-RateLimit-Remaining == '2'
And match header X-RateLimit-Limit == '4'

# controlli cache pdnd

* def result = get_remote_store_key('KID-ApplicativoBlockingJWK') 

* match result[0].KID contains 'KID-ApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

# va in concorrenza con il test precedente, questa verifica comunque è stata fatta nel test precedente di integrity
#* def resultServer = get_remote_store_key('KID-ExampleServer') 
#* match resultServer[0].KID contains 'KID-ExampleServer'
# Effettua la conversione Base64 del campo 'data'
#* def actualDataServer = resultServer[0].CONTENT_KEY
#* def actualDataServerBase64 = encode_base64_from_bytes(actualDataServer)
#* match actualDataServerBase64 != no_key_base64

* match result[0].CLIENT_ID == '#null'
* match result[0].ORGANIZATION_DETAILS == '#null'
* match result[0].CLIENT_DETAILS == '#null'

* def data_registrazione_integrity_kid = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_integrity_kid = result[0].DATA_AGGIORNAMENTO+''

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate+'_0003"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'

* def data_registrazione_integrity_client = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_integrity_client = result[0].DATA_AGGIORNAMENTO+''

* match data_registrazione_integrity_client == data_registrazione_auth
* match data_aggiornamento_integrity_client == data_aggiornamento_auth

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Comune di Esempio '+formattedDate+' ApplicativoBlockingJWK'

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"kind":"PA"'
* match pdndOrganizationJson contains '"subUnitType":"AOO"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0003"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingJWK"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'
* match pdndClientJson contains '"name":"'+'client-'+formattedDateClient+'"'
* match pdndClientJson contains '"description":"'+'client1 di test '+formattedDateClient+'"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'



# Effettuo nuovamente l'invocazione, ora dovrebbe essere tutto in cache

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDIntegrity/v2"
And path 'idauth'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingJWK', password: 'ApplicativoBlockingJWK' })
And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
And header simulazionepdnd-password = 'ApplicativoBlockingJWK'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingJWK'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDIntegrity/v2'
And header GovWay-TestSuite-PDND-RateLimiting = 'TestMisto';
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0003'
And match header X-RateLimit-Remaining == '1'
And match header X-RateLimit-Limit == '4'

# controlli cache pdnd

* def result = get_remote_store_key('KID-ApplicativoBlockingJWK') 

* match result[0].KID contains 'KID-ApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

# va in concorrenza con il test precedente, questa verifica comunque è stata fatta nel test precedente di integrity
#* def resultServer = get_remote_store_key('KID-ExampleServer') 
#* match resultServer[0].KID contains 'KID-ExampleServer'
# Effettua la conversione Base64 del campo 'data'
#* def actualDataServer = resultServer[0].CONTENT_KEY
#* def actualDataServerBase64 = encode_base64_from_bytes(actualDataServer)
#* match actualDataServerBase64 != no_key_base64

* match result[0].CLIENT_ID == '#null'
* match result[0].ORGANIZATION_DETAILS == '#null'
* match result[0].CLIENT_DETAILS == '#null'

* def data_registrazione_integrity_kid_after = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_integrity_kid_after = result[0].DATA_AGGIORNAMENTO+''
* match data_registrazione_integrity_kid_after == data_registrazione_integrity_kid
* match data_aggiornamento_integrity_kid_after == data_aggiornamento_integrity_kid

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate+'_0003"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'

* def data_registrazione_integrity_client_after = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_integrity_client_after = result[0].DATA_AGGIORNAMENTO+''
* match data_registrazione_integrity_client_after == data_registrazione_auth
* match data_aggiornamento_integrity_client_after == data_aggiornamento_auth

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Comune di Esempio '+formattedDate+' ApplicativoBlockingJWK'

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"kind":"PA"'
* match pdndOrganizationJson contains '"subUnitType":"AOO"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0003"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingJWK"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'
* match pdndClientJson contains '"name":"'+'client-'+formattedDateClient+'"'
* match pdndClientJson contains '"description":"'+'client1 di test '+formattedDateClient+'"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# ulteriore invocazione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDIntegrity/v2"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingJWK', password: 'ApplicativoBlockingJWK' })
And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
And header simulazionepdnd-password = 'ApplicativoBlockingJWK'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingJWK'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDIntegrity/v2'
And header GovWay-TestSuite-PDND-RateLimiting = 'TestMisto';
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0003'
And match header X-RateLimit-Remaining == '0'
And match header X-RateLimit-Limit == '4'

# controlli cache pdnd

* def result = get_remote_store_key('KID-ApplicativoBlockingJWK') 

* match result[0].KID contains 'KID-ApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

# va in concorrenza con il test precedente, questa verifica comunque è stata fatta nel test precedente di integrity
#* def resultServer = get_remote_store_key('KID-ExampleServer') 
#* match resultServer[0].KID contains 'KID-ExampleServer'
## Effettua la conversione Base64 del campo 'data'
#* def actualDataServer = resultServer[0].CONTENT_KEY
#* def actualDataServerBase64 = encode_base64_from_bytes(actualDataServer)
#* match actualDataServerBase64 != no_key_base64

* match result[0].CLIENT_ID == '#null'
* match result[0].ORGANIZATION_DETAILS == '#null'
* match result[0].CLIENT_DETAILS == '#null'

* def data_registrazione_integrity_kid_after = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_integrity_kid_after = result[0].DATA_AGGIORNAMENTO+''
* match data_registrazione_integrity_kid_after == data_registrazione_integrity_kid
* match data_aggiornamento_integrity_kid_after == data_aggiornamento_integrity_kid

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate+'_0003"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'

* def data_registrazione_client_after = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_client_after = result[0].DATA_AGGIORNAMENTO+''
* match data_registrazione_client_after == data_registrazione_auth
* match data_aggiornamento_client_after == data_aggiornamento_auth

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Comune di Esempio '+formattedDate+' ApplicativoBlockingJWK'

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"kind":"PA"'
* match pdndOrganizationJson contains '"subUnitType":"AOO"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0003"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingJWK"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'
* match pdndClientJson contains '"name":"'+'client-'+formattedDateClient+'"'
* match pdndClientJson contains '"description":"'+'client1 di test '+formattedDateClient+'"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# Effettuo nuovamente l'invocazione, ora dovrebbe essere violata la policy di Rate Limiting

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDIntegrity/v2"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingJWK', password: 'ApplicativoBlockingJWK' })
And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
And header simulazionepdnd-password = 'ApplicativoBlockingJWK'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingJWK'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDIntegrity/v2'
And header GovWay-TestSuite-PDND-RateLimiting = 'TestMisto';
When method post
Then status 429
And match response == read('error-bodies/rate-limiting-violato.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == '#notpresent'
And match header X-RateLimit-Remaining == '0'
And match header X-RateLimit-Limit == '4'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'violate(1)'
* match result[0].MESSAGGIO contains 'rispettate(0)'
* def result = get_diagnostico(tiderogazione, 'Il numero massimo di richieste (rilevato:5 soglia:4) risulta raggiunto') 
* match result[0].MESSAGGIO contains 'PDNDOrganizationName'















@getInformazioniClientOrganizationApiIDAUTHGroupByExternalId
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, senza prima aver mai prelevata la chiave essendo solamente un pattern ID_AUTH. È attiva una politica di rate limiting che conteggia per externalId

* def result = clean_remote_store_key('KID-ApplicativoBlockingIDA01ExampleClient3')
* def result = clean_remote_store_key_client_id_prefix('DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3')

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDAuth/v2"
And path 'rateLimitingGroupByExternalId'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient3', password: 'ApplicativoBlockingIDA01ExampleClient3' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDAuth/v2'
And header simulazionepdnd-filtrorate = 'TestGroupByExternalId'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header X-RateLimit-Remaining == '2'
And match header X-RateLimit-Limit == '3'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# Effettuo nuovamente l'invocazione, ora dovrebbe essere in cache

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDAuth/v2"
And path 'rateLimitingGroupByExternalId'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient3', password: 'ApplicativoBlockingIDA01ExampleClient3' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDAuth/v2'
And header simulazionepdnd-filtrorate = 'TestGroupByExternalId'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header X-RateLimit-Remaining == '1'
And match header X-RateLimit-Limit == '3'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'



# Effettuo nuovamente l'invocazione per l'ultima volta ok, ora dovrebbe essere in cache

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDAuth/v2"
And path 'rateLimitingGroupByExternalId'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient3', password: 'ApplicativoBlockingIDA01ExampleClient3' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDAuth/v2'
And header simulazionepdnd-filtrorate = 'TestGroupByExternalId'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header X-RateLimit-Remaining == '0'
And match header X-RateLimit-Limit == '3'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'




# Effettuo nuovamente l'invocazione, ora dovrebbe essere violata la policy di Rate Limiting

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDAuth/v2"
And path 'rateLimitingGroupByExternalId'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient3', password: 'ApplicativoBlockingIDA01ExampleClient3' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDAuth/v2'
And header simulazionepdnd-filtrorate = 'TestGroupByExternalId'
When method post
Then status 429
And match response == read('error-bodies/rate-limiting-violato.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == '#notpresent'
And match header X-RateLimit-Remaining == '0'
And match header X-RateLimit-Limit == '3'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationName == '#notpresent'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationCategory == '#notpresent'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationExternal == '#notpresent'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'violate(1)'
* match result[0].MESSAGGIO contains 'rispettate(0)'
* def result = get_diagnostico(tiderogazione, 'Il numero massimo di richieste (rilevato:4 soglia:3) risulta raggiunto') 
* match result[0].MESSAGGIO contains 'PDNDExternalId'












@getInformazioniClientOrganizationApiIDAUTHGroupByConsumerId
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, senza prima aver mai prelevata la chiave essendo solamente un pattern ID_AUTH. È attiva una politica di rate limiting che conteggia per consumer

* def result = clean_remote_store_key('KID-ApplicativoBlockingIDA01ExampleClient3')
* def result = clean_remote_store_key_client_id_prefix('DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3')

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDAuth/v2"
And path 'rateLimitingGroupByConsumerId'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient3', password: 'ApplicativoBlockingIDA01ExampleClient3' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDAuth/v2'
And header simulazionepdnd-filtrorate = 'TestGroupByConsumerId'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header X-RateLimit-Remaining == '0'
And match header X-RateLimit-Limit == '1'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# Effettuo nuovamente l'invocazione, ora dovrebbe essere in cache

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDAuth/v2"
And path 'rateLimitingGroupByConsumerId'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient3', password: 'ApplicativoBlockingIDA01ExampleClient3' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDAuth/v2'
And header simulazionepdnd-filtrorate = 'TestGroupByConsumerId'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header X-RateLimit-Remaining == '0'
And match header X-RateLimit-Limit == '1'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(%), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(0)'
* match result[0].MESSAGGIO contains 'violate(0)'
* match result[0].MESSAGGIO contains 'violate-warningOnly(1)'
* def result = get_diagnostico(tiderogazione, 'Il numero massimo di richieste (rilevato:2 soglia:1) risulta raggiunto') 
* match result[0].MESSAGGIO contains 'PDNDConsumerId'




