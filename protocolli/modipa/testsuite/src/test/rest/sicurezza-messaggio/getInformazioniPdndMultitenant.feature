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

* def result = clean_remote_store_key('KID-MultitenantApplicativoBlockingIDA01ExampleClient3')
* def result = clean_remote_store_key_client_id_prefix('DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01ExampleClient3')

# Ricalcolo in ogni test per essere meno suscettibile al cambio minuto
* def today = java.time.LocalDateTime.now()
* def formatter = java.time.format.DateTimeFormatter.ofPattern("YY-MM-dd-HH-mm");
* def formattedDate = today.format(formatter)
* def formatterYYYYMMDD = java.time.format.DateTimeFormatter.ofPattern("YYYYMMdd");
* def formattedDateYYYYMMDD = today.format(formatterYYYYMMDD)
* def formatterHHmm = java.time.format.DateTimeFormatter.ofPattern("HHmm");
* def formattedDateHHmm = today.format(formatterHHmm)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDAuth/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'MultitenantApplicativoBlockingIDA01ExampleClient3', password: 'MultitenantApplicativoBlockingIDA01ExampleClient3' })
And header simulazionepdnd-username = 'MultitenantApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-password = 'MultitenantApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-purposeId = 'purposeId-MultitenantApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-audience = 'DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDAuth/v1'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'm_m000_'+formattedDate+'_0001'
And match header X-RateLimit-Remaining == '1'
And match header X-RateLimit-Limit == '2'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationName == 'Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingIDA01ExampleClient3'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationCategory == 'Ministeri'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationExternal == 'IPA m_m000_'+formattedDate+'_0001'

# controlli cache pdnd

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01ExampleClient3') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01ExampleClient3'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01ExampleClient3'
* match result[0].ORGANIZATION_DETAILS contains 'm_m000_'+formattedDate+'_0001"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-bbbb-12345678f8dd"'

* def data_registrazione = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento = result[0].DATA_AGGIORNAMENTO+''

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01ExampleClient3')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingIDA01ExampleClient3'

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"category":"Ministeri"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"m_m000_'+formattedDate+'_0001"'
* match pdndOrganizationJson contains '"name":"Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingIDA01ExampleClient3"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-mmmm-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-bbbb-12345678f8dd"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# Effettuo nuovamente l'invocazione, ora dovrebbe essere in cache

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDAuth/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'MultitenantApplicativoBlockingIDA01ExampleClient3', password: 'MultitenantApplicativoBlockingIDA01ExampleClient3' })
And header simulazionepdnd-username = 'MultitenantApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-password = 'MultitenantApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-purposeId = 'purposeId-MultitenantApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-audience = 'DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDAuth/v1'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'm_m000_'+formattedDate+'_0001'
And match header X-RateLimit-Remaining == '0'
And match header X-RateLimit-Limit == '2'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationName == 'Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingIDA01ExampleClient3'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationCategory == 'Ministeri'
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationExternal == 'IPA m_m000_'+formattedDate+'_0001'

# controlli cache pdnd

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01ExampleClient3') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01ExampleClient3'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01ExampleClient3'
* match result[0].ORGANIZATION_DETAILS contains 'm_m000_'+formattedDate+'_0001"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-bbbb-12345678f8dd"'

* def data_registrazione_after = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_after = result[0].DATA_AGGIORNAMENTO+''
* match data_registrazione_after == data_registrazione
* match data_aggiornamento_after == data_aggiornamento

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01ExampleClient3')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingIDA01ExampleClient3'

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"category":"Ministeri"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"m_m000_'+formattedDate+'_0001"'
* match pdndOrganizationJson contains '"name":"Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingIDA01ExampleClient3"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-mmmm-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-bbbb-12345678f8dd"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# Effettuo nuovamente l'invocazione, ora dovrebbe essere violata la policy di Rate Limiting

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDAuth/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'MultitenantApplicativoBlockingIDA01ExampleClient3', password: 'MultitenantApplicativoBlockingIDA01ExampleClient3' })
And header simulazionepdnd-username = 'MultitenantApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-password = 'MultitenantApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-purposeId = 'purposeId-MultitenantApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-audience = 'DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDAuth/v1'
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
And match header GovWay-TestSuite-Reply-GovWay-Token-PDND-OrganizationExternal == '#notpresent'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'violate(1)'
* match result[0].MESSAGGIO contains 'rispettate(0)'
* def result = get_diagnostico(tiderogazione, 'Il numero massimo di richieste (rilevato:3 soglia:2) risulta raggiunto') 
* match result[0].MESSAGGIO contains 'PDNDOrganizationName'







@getInformazioniClientOrganizationApiIntegrity
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, prima però viene prelevata la chiave essendo solamente un pattern INTEGRITY con truststore PDND

* def result = clean_remote_store_key('KID-MultitenantApplicativoBlockingIDA01')
* def result = clean_remote_store_key_client_id_prefix('DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01')

# Ricalcolo in ogni test per essere meno suscettibile al cambio minuto
* def today = java.time.LocalDateTime.now()
* def formatter = java.time.format.DateTimeFormatter.ofPattern("YY-MM-dd-HH-mm");
* def formattedDate = today.format(formatter)
* def formatterYYYYMMDD = java.time.format.DateTimeFormatter.ofPattern("YYYYMMdd");
* def formattedDateYYYYMMDD = today.format(formatterYYYYMMDD)
* def formatterHHmm = java.time.format.DateTimeFormatter.ofPattern("HHmm");
* def formattedDateHHmm = today.format(formatterHHmm)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'MultitenantApplicativoBlockingIDA01', password: 'MultitenantApplicativoBlockingIDA01' })
And header simulazionepdnd-username = 'MultitenantApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'MultitenantApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-MultitenantApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1'
And header GovWay-TestSuite-PDND-RateLimiting = 'TestStandard';
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'm_m000_'+formattedDate+'_0002'
And match header X-RateLimit-Remaining == '1'
And match header X-RateLimit-Limit == '2'

# controlli govway-token
* def encodedHeaderGWToken = responseHeaders['GovWay-TestSuite-Reply-GovWay-Token'][0]
* def decodedHeaderGWToken = decode_base64(encodedHeaderGWToken)
* karate.log("Decoded: ", decodedHeaderGWToken)
* json headerJsonGWToken = decodedHeaderGWToken
* karate.log("Decoded as json: ", headerJsonGWToken)
* def nomeOrganization = 'Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingIDA01'
* def externalIdOrganization = 'm_m000_'+formattedDate+'_0002'
* def headerJsonGWTokenExpected = 
"""
{ 
   pdnd: {
     organization: {
        name: #(nomeOrganization), 
        category: 'Ministeri', 
        externalOrigin: 'IPA',
        externalId: #(externalIdOrganization)
     }
   }
 }
"""
* match headerJsonGWToken contains deep headerJsonGWTokenExpected

# controlli cache pdnd

* def result = get_remote_store_key('KID-MultitenantApplicativoBlockingIDA01') 

* match result[0].KID contains 'KID-MultitenantApplicativoBlockingIDA01'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

* match result[0].CLIENT_ID == '#null'
* match result[0].ORGANIZATION_DETAILS == '#null'
* match result[0].CLIENT_DETAILS == '#null'

* def data_registrazione_kid = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_kid = result[0].DATA_AGGIORNAMENTO+''

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01'
* match result[0].ORGANIZATION_DETAILS contains 'm_m000_'+formattedDate+'_0002"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-bbbb-12345678f8dd"'

* def data_registrazione_client = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_client = result[0].DATA_AGGIORNAMENTO+''

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingIDA01' 

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"category":"Ministeri"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"m_m000_'+formattedDate+'_0002"'
* match pdndOrganizationJson contains '"name":"Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingIDA01"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-mmmm-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-bbbb-12345678f8dd"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# Effettuo nuovamente l'invocazione, ora dovrebbe essere in cache

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'MultitenantApplicativoBlockingIDA01', password: 'MultitenantApplicativoBlockingIDA01' })
And header simulazionepdnd-username = 'MultitenantApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'MultitenantApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-MultitenantApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1'
And header GovWay-TestSuite-PDND-RateLimiting = 'TestStandard';
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'm_m000_'+formattedDate+'_0002'
And match header X-RateLimit-Remaining == '0'
And match header X-RateLimit-Limit == '2'

# controlli govway-token
* def encodedHeaderGWToken = responseHeaders['GovWay-TestSuite-Reply-GovWay-Token'][0]
* def decodedHeaderGWToken = decode_base64(encodedHeaderGWToken)
* karate.log("Decoded: ", decodedHeaderGWToken)
* json headerJsonGWToken = decodedHeaderGWToken
* karate.log("Decoded2: ", headerJsonGWToken)
* def nomeOrganization = 'Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingIDA01'
* def externalIdOrganization = 'm_m000_'+formattedDate+'_0002'
* def headerJsonGWTokenExpected = 
"""
{ 
   pdnd: {
     organization: {
        name: #(nomeOrganization), 
        category: 'Ministeri', 
        externalOrigin: 'IPA',
        externalId: #(externalIdOrganization)
     }
   }
 }
"""
* match headerJsonGWToken contains deep headerJsonGWTokenExpected

# controlli cache pdnd

* def result = get_remote_store_key('KID-MultitenantApplicativoBlockingIDA01') 

* match result[0].KID contains 'KID-MultitenantApplicativoBlockingIDA01'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

* match result[0].CLIENT_ID == '#null'
* match result[0].ORGANIZATION_DETAILS == '#null'
* match result[0].CLIENT_DETAILS == '#null'

* def data_registrazione_after_kid = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_after_kid = result[0].DATA_AGGIORNAMENTO+''

* match data_registrazione_after_kid == data_registrazione_kid
* match data_aggiornamento_after_kid == data_aggiornamento_kid

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01'
* match result[0].ORGANIZATION_DETAILS contains 'm_m000_'+formattedDate+'_0002"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-bbbb-12345678f8dd"'

* def data_registrazione_after_client = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_after_client = result[0].DATA_AGGIORNAMENTO+''

* match data_registrazione_after_client == data_registrazione_client
* match data_aggiornamento_after_client == data_aggiornamento_client

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/MultitenantApplicativoBlockingIDA01')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingIDA01' 

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"category":"Ministeri"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"m_m000_'+formattedDate+'_0002"'
* match pdndOrganizationJson contains '"name":"Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingIDA01"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-mmmm-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-bbbb-12345678f8dd"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# Effettuo nuovamente l'invocazione, ora dovrebbe essere violata la policy di Rate Limiting

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'MultitenantApplicativoBlockingIDA01', password: 'MultitenantApplicativoBlockingIDA01' })
And header simulazionepdnd-username = 'MultitenantApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'MultitenantApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-MultitenantApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1'
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

* def result = clean_remote_store_key('KID-MultitenantApplicativoBlockingJWK')
* def result = clean_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK')

# Ricalcolo in ogni test per essere meno suscettibile al cambio minuto
* def today = java.time.LocalDateTime.now()
* def formatter = java.time.format.DateTimeFormatter.ofPattern("YY-MM-dd-HH-mm");
* def formattedDate = today.format(formatter)
* def formatterYYYYMMDD = java.time.format.DateTimeFormatter.ofPattern("YYYYMMdd");
* def formattedDateYYYYMMDD = today.format(formatterYYYYMMDD)
* def formatterHHmm = java.time.format.DateTimeFormatter.ofPattern("HHmm");
* def formattedDateHHmm = today.format(formatterHHmm)


# Invocazione con azione che prevede solo voucher

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1"
And path 'idauth'
And request read('request.json')
And header Authorization = call basic ({ username: 'MultitenantApplicativoBlockingJWK', password: 'MultitenantApplicativoBlockingJWK' })
And header simulazionepdnd-username = 'MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-password = 'MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-purposeId = 'purposeId-MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-audience = 'DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1'
And header GovWay-TestSuite-PDND-RateLimiting = 'TestMisto';
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'm_m000_'+formattedDate+'_0003'
And match header X-RateLimit-Remaining == '3'
And match header X-RateLimit-Limit == '4'

# controlli cache pdnd

* def result = get_remote_store_key('KID-MultitenantApplicativoBlockingJWK') 

* match result == []

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK'
* match result[0].ORGANIZATION_DETAILS contains 'm_m000_'+formattedDate+'_0003"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'

* def data_registrazione_auth = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_auth = result[0].DATA_AGGIORNAMENTO+''

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingJWK'

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"category":"Ministeri"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"m_m000_'+formattedDate+'_0003"'
* match pdndOrganizationJson contains '"name":"Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingJWK"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-mmmm-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# Invocazione con azione che prevede voucher e integrity

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'MultitenantApplicativoBlockingJWK', password: 'MultitenantApplicativoBlockingJWK' })
And header simulazionepdnd-username = 'MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-password = 'MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-purposeId = 'purposeId-MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-audience = 'DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1'
And header GovWay-TestSuite-PDND-RateLimiting = 'TestMisto';
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'm_m000_'+formattedDate+'_0003'
And match header X-RateLimit-Remaining == '2'
And match header X-RateLimit-Limit == '4'

# controlli cache pdnd

* def result = get_remote_store_key('KID-MultitenantApplicativoBlockingJWK') 

* match result[0].KID contains 'KID-MultitenantApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

* match result[0].CLIENT_ID == '#null'
* match result[0].ORGANIZATION_DETAILS == '#null'
* match result[0].CLIENT_DETAILS == '#null'

* def data_registrazione_integrity_kid = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_integrity_kid = result[0].DATA_AGGIORNAMENTO+''

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK'
* match result[0].ORGANIZATION_DETAILS contains 'm_m000_'+formattedDate+'_0003"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'

* def data_registrazione_integrity_client = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_integrity_client = result[0].DATA_AGGIORNAMENTO+''

* match data_registrazione_integrity_client == data_registrazione_auth
* match data_aggiornamento_integrity_client == data_aggiornamento_auth

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingJWK'

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"category":"Ministeri"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"m_m000_'+formattedDate+'_0003"'
* match pdndOrganizationJson contains '"name":"Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingJWK"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-mmmm-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'



# Effettuo nuovamente l'invocazione, ora dovrebbe essere tutto in cache

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1"
And path 'idauth'
And request read('request.json')
And header Authorization = call basic ({ username: 'MultitenantApplicativoBlockingJWK', password: 'MultitenantApplicativoBlockingJWK' })
And header simulazionepdnd-username = 'MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-password = 'MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-purposeId = 'purposeId-MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-audience = 'DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1'
And header GovWay-TestSuite-PDND-RateLimiting = 'TestMisto';
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'm_m000_'+formattedDate+'_0003'
And match header X-RateLimit-Remaining == '1'
And match header X-RateLimit-Limit == '4'

# controlli cache pdnd

* def result = get_remote_store_key('KID-MultitenantApplicativoBlockingJWK') 

* match result[0].KID contains 'KID-MultitenantApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

* match result[0].CLIENT_ID == '#null'
* match result[0].ORGANIZATION_DETAILS == '#null'
* match result[0].CLIENT_DETAILS == '#null'

* def data_registrazione_integrity_kid_after = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_integrity_kid_after = result[0].DATA_AGGIORNAMENTO+''
* match data_registrazione_integrity_kid_after == data_registrazione_integrity_kid
* match data_aggiornamento_integrity_kid_after == data_aggiornamento_integrity_kid

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK'
* match result[0].ORGANIZATION_DETAILS contains 'm_m000_'+formattedDate+'_0003"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'

* def data_registrazione_integrity_client_after = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_integrity_client_after = result[0].DATA_AGGIORNAMENTO+''
* match data_registrazione_integrity_client_after == data_registrazione_auth
* match data_aggiornamento_integrity_client_after == data_aggiornamento_auth

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingJWK'

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"category":"Ministeri"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"m_m000_'+formattedDate+'_0003"'
* match pdndOrganizationJson contains '"name":"Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingJWK"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-mmmm-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# ulteriore invocazione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'MultitenantApplicativoBlockingJWK', password: 'MultitenantApplicativoBlockingJWK' })
And header simulazionepdnd-username = 'MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-password = 'MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-purposeId = 'purposeId-MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-audience = 'DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1'
And header GovWay-TestSuite-PDND-RateLimiting = 'TestMisto';
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'm_m000_'+formattedDate+'_0003'
And match header X-RateLimit-Remaining == '0'
And match header X-RateLimit-Limit == '4'

# controlli cache pdnd

* def result = get_remote_store_key('KID-MultitenantApplicativoBlockingJWK') 

* match result[0].KID contains 'KID-MultitenantApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

* match result[0].CLIENT_ID == '#null'
* match result[0].ORGANIZATION_DETAILS == '#null'
* match result[0].CLIENT_DETAILS == '#null'

* def data_registrazione_integrity_kid_after = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_integrity_kid_after = result[0].DATA_AGGIORNAMENTO+''
* match data_registrazione_integrity_kid_after == data_registrazione_integrity_kid
* match data_aggiornamento_integrity_kid_after == data_aggiornamento_integrity_kid

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK'
* match result[0].ORGANIZATION_DETAILS contains 'm_m000_'+formattedDate+'_0003"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'

* def data_registrazione_client_after = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_client_after = result[0].DATA_AGGIORNAMENTO+''
* match data_registrazione_client_after == data_registrazione_auth
* match data_aggiornamento_client_after == data_aggiornamento_auth

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/KidOnly/MultitenantApplicativoBlockingJWK')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingJWK'

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"category":"Ministeri"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"m_m000_'+formattedDate+'_0003"'
* match pdndOrganizationJson contains '"name":"Ministero di Esempio '+formattedDate+' MultitenantApplicativoBlockingJWK"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-mmmm-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'

# controllo applicazione filtro rate limiting

* def tiderogazione = responseHeaders['GovWay-Transaction-ID-EROGAZIONE'][0]
* def result = get_diagnostico(tiderogazione, 'Verifica Policy di Rate Limiting (%) completata: rispettate(%), violate(%), violate-warningOnly(0), filtrate(%), nonApplicabili(0), disabilitate(0), inErrore(0).') 
* match result[0].MESSAGGIO contains 'rispettate(1)'
* match result[0].MESSAGGIO contains 'violate(0)'


# Effettuo nuovamente l'invocazione, ora dovrebbe essere violata la policy di Rate Limiting

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'MultitenantApplicativoBlockingJWK', password: 'MultitenantApplicativoBlockingJWK' })
And header simulazionepdnd-username = 'MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-password = 'MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-purposeId = 'purposeId-MultitenantApplicativoBlockingJWK'
And header simulazionepdnd-audience = 'DemoSoggettoErogatore2/TestRecuperoInformazioniPDNDIntegrity/v1'
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


