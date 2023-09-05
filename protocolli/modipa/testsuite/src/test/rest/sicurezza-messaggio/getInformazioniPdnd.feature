Feature: Testing get informazioni dalla PDND

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }

    * def clean_remote_store_key = read('classpath:utils/remote_store_key.js')

    * def today = java.time.LocalDateTime.now()
    * def formatter = java.time.format.DateTimeFormatter.ofPattern("YY-MM-dd-HH-mm");
    * def formattedDate = today.format(formatter)

    * def encode_base64 = read('classpath:utils/encode-base64.js')
    * def encode_base64_from_bytes = read('classpath:utils/encode-base64-from-bytes.js')
    * def no_key_base64 = encode_base64('KEY_UNDEFINED');


@getInformazioniClientOrganizationApiIDAUTH
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, senza prima aver mai prelevata la chiave essendo solamente un pattern ID_AUTH

* def result = clean_remote_store_key('KID-ApplicativoBlockingIDA01ExampleClient3')
* def result = clean_remote_store_key_client_id_prefix('DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3')

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDAuth/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient3', password: 'ApplicativoBlockingIDA01ExampleClient3' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDAuth/v1'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'c_c000_'+formattedDate

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3'
* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64
* def data_registrazione = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento = result[0].DATA_AGGIORNAMENTO+''

# Effettuo nuovamente l'invocazione, ora dovrebbe essere in cache

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDAuth/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient3', password: 'ApplicativoBlockingIDA01ExampleClient3' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01ExampleClient3'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDAuth/v1'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header PDND-ExternalId == 'c_c000_'+formattedDate

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3'
* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient3'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* def data_registrazione_after = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_after = result[0].DATA_AGGIORNAMENTO+''
* match data_registrazione_after == data_registrazione
* match data_aggiornamento_after == data_aggiornamento






@getInformazioniClientOrganizationApiIntegrity
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, prima però viene prelevata la chiave essendo solamente un pattern INTEGRITY con truststore PDND

* def result = clean_remote_store_key('KID-ApplicativoBlockingIDA01')
* def result = clean_remote_store_key_client_id_prefix('DemoSoggettoFruitore/ApplicativoBlockingIDA01')

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDIntegrity/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDIntegrity/v1'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def result = get_remote_store_key('KID-ApplicativoBlockingIDA01') 

* match result[0].KID contains 'KID-ApplicativoBlockingIDA01'
* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

* def data_registrazione = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento = result[0].DATA_AGGIORNAMENTO+''

# Effettuo nuovamente l'invocazione, ora dovrebbe essere in cache

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDIntegrity/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDIntegrity/v1'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def result = get_remote_store_key('KID-ApplicativoBlockingIDA01') 

* match result[0].KID contains 'KID-ApplicativoBlockingIDA01'
* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

* def data_registrazione_after = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_after = result[0].DATA_AGGIORNAMENTO+''
* match data_registrazione_after == data_registrazione
* match data_aggiornamento_after == data_aggiornamento





@getInformazioniClientOrganizationApiPrimaAuthPoiIntegrity
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, prima viene invocata un'azione che non fa scaturire il download della chiave poi viene prelevata la chiave usando un'altra azione che prevede pattern INTEGRITY con truststore PDND

* def result = clean_remote_store_key('KID-ApplicativoBlockingJWK')
* def result = clean_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK')

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDIntegrity/v1"
And path 'idauth'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingJWK', password: 'ApplicativoBlockingJWK' })
And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
And header simulazionepdnd-password = 'ApplicativoBlockingJWK'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingJWK'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDIntegrity/v1'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* def data_registrazione_auth = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_auth = result[0].DATA_AGGIORNAMENTO+''

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDIntegrity/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingJWK', password: 'ApplicativoBlockingJWK' })
And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
And header simulazionepdnd-password = 'ApplicativoBlockingJWK'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingJWK'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDIntegrity/v1'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def result = get_remote_store_key('KID-ApplicativoBlockingJWK') 

* match result[0].KID contains 'KID-ApplicativoBlockingJWK'
* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

* def data_registrazione_integrity = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_integrity = result[0].DATA_AGGIORNAMENTO+''



# Effettuo nuovamente l'invocazione, ora dovrebbe essere in cache

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDIntegrity/v1"
And path 'idauth'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingJWK', password: 'ApplicativoBlockingJWK' })
And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
And header simulazionepdnd-password = 'ApplicativoBlockingJWK'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingJWK'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDIntegrity/v1'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* def data_registrazione_after = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_after = result[0].DATA_AGGIORNAMENTO+''
* match data_registrazione_after == data_registrazione_auth
* match data_aggiornamento_after == data_aggiornamento_auth

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDIntegrity/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingJWK', password: 'ApplicativoBlockingJWK' })
And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
And header simulazionepdnd-password = 'ApplicativoBlockingJWK'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingJWK'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDIntegrity/v1'
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def result = get_remote_store_key('KID-ApplicativoBlockingJWK') 

* match result[0].KID contains 'KID-ApplicativoBlockingJWK'
* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

* def data_registrazione_after = result[0].DATA_REGISTRAZIONE+''
* def data_aggiornamento_after = result[0].DATA_AGGIORNAMENTO+''
* match data_registrazione_after == data_registrazione_integrity
* match data_aggiornamento_after == data_aggiornamento_integrity




