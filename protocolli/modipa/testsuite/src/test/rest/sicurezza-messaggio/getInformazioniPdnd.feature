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

    * def get_id_by_credenziale = read('classpath:utils/get_id_by_credenziale.js')
    * def get_credenziale_by_refid = read('classpath:utils/get_credenziale_by_refid.js')
    * def get_credenziale_by_refid_greather_then_id = read('classpath:utils/get_credenziale_by_refid_greather_then_id.js')
    * def get_remote_store_key_client_id_prefix = read('classpath:utils/get_remote_store_key_client_id_prefix.js')
    * def clean_remote_store_key_client_id_prefix = read('classpath:utils/clean_remote_store_key_client_id_prefix.js')
    

    * def credenziale_max_feature = callonce read('classpath:utils/credenziale_mittente_max.feature')




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
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0001'

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
* match pdndOrganizationJson contains '"category":"Comuni e loro Consorzi e Associazioni"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0001"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01ExampleClient3"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-bbbb-12345678f8dd"'


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
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0001'

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
* match pdndOrganizationJson contains '"category":"Comuni e loro Consorzi e Associazioni"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0001"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01ExampleClient3"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0001-bbbb-12345678f8dd"'






@getInformazioniClientOrganizationApiIntegrity
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, prima però viene prelevata la chiave essendo solamente un pattern INTEGRITY con truststore PDND

* def result = clean_remote_store_key('KID-ApplicativoBlockingIDA01')
* def result = clean_remote_store_key_client_id_prefix('DemoSoggettoFruitore/ApplicativoBlockingIDA01')

# Ricalcolo in ogni test per essere meno suscettibile al cambio minuto
* def today = java.time.LocalDateTime.now()
* def formatter = java.time.format.DateTimeFormatter.ofPattern("YY-MM-dd-HH-mm");
* def formattedDate = today.format(formatter)
* def formatterYYYYMMDD = java.time.format.DateTimeFormatter.ofPattern("YYYYMMdd");
* def formattedDateYYYYMMDD = today.format(formatterYYYYMMDD)
* def formatterHHmm = java.time.format.DateTimeFormatter.ofPattern("HHmm");
* def formattedDateHHmm = today.format(formatterHHmm)

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
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0002'

# controlli cache pdnd

* def result = get_remote_store_key('KID-ApplicativoBlockingIDA01') 

* match result[0].KID contains 'KID-ApplicativoBlockingIDA01'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

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
* match pdndOrganizationJson contains '"category":"Comuni e loro Consorzi e Associazioni"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0002"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-bbbb-12345678f8dd"'


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
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0002'

# controlli cache pdnd

* def result = get_remote_store_key('KID-ApplicativoBlockingIDA01') 

* match result[0].KID contains 'KID-ApplicativoBlockingIDA01'
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
* match pdndOrganizationJson contains '"category":"Comuni e loro Consorzi e Associazioni"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0002"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingIDA01"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0002-bbbb-12345678f8dd"'






@getInformazioniClientOrganizationApiPrimaAuthPoiIntegrity
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, prima viene invocata un'azione che non fa scaturire il download della chiave poi viene prelevata la chiave usando un'altra azione che prevede pattern INTEGRITY con truststore PDND

* def result = clean_remote_store_key('KID-ApplicativoBlockingJWK')
* def result = clean_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK')

# Ricalcolo in ogni test per essere meno suscettibile al cambio minuto
* def today = java.time.LocalDateTime.now()
* def formatter = java.time.format.DateTimeFormatter.ofPattern("YY-MM-dd-HH-mm");
* def formattedDate = today.format(formatter)
* def formatterYYYYMMDD = java.time.format.DateTimeFormatter.ofPattern("YYYYMMdd");
* def formattedDateYYYYMMDD = today.format(formatterYYYYMMDD)
* def formatterHHmm = java.time.format.DateTimeFormatter.ofPattern("HHmm");
* def formattedDateHHmm = today.format(formatterHHmm)


# Invocazione con azione che prevede solo voucher

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
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0003'

# controlli cache pdnd

* def result = get_remote_store_key('KID-ApplicativoBlockingJWK') 

* match result == []

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
* match pdndOrganizationJson contains '"category":"Comuni e loro Consorzi e Associazioni"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0003"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingJWK"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'


# Invocazione con azione che prevede voucher e integrity

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
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0003'

# controlli cache pdnd

* def result = get_remote_store_key('KID-ApplicativoBlockingJWK') 

* match result[0].KID contains 'KID-ApplicativoBlockingJWK'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 != no_key_base64

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
* match pdndOrganizationJson contains '"category":"Comuni e loro Consorzi e Associazioni"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0003"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingJWK"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'



# Effettuo nuovamente l'invocazione, ora dovrebbe essere tutto in cache

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
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0003'

# controlli cache pdnd

* def result = get_remote_store_key('KID-ApplicativoBlockingJWK') 

* match result[0].KID contains 'KID-ApplicativoBlockingJWK'
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
* match pdndOrganizationJson contains '"category":"Comuni e loro Consorzi e Associazioni"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0003"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingJWK"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'


# ulteriore invocazione

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
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header PDND-ExternalId == 'c_c000_'+formattedDate+'_0003'

# controlli cache pdnd

* def result = get_remote_store_key('KID-ApplicativoBlockingJWK') 

* match result[0].KID contains 'KID-ApplicativoBlockingJWK'
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
* match pdndOrganizationJson contains '"category":"Comuni e loro Consorzi e Associazioni"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0003"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingJWK"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0003-bbbb-12345678f8dd"'











@getInformazioniClientOrganizationApiIDAUTH_NonValido
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, senza prima aver mai prelevata la chiave essendo solamente un pattern ID_AUTH

#* def result = clean_remote_store_key('ExampleClient2')
* def result = clean_remote_store_key_client_id_prefix('http://client2')

# Ricalcolo in ogni test per essere meno suscettibile al cambio minuto
* def today = java.time.LocalDateTime.now()
* def formatter = java.time.format.DateTimeFormatter.ofPattern("YY-MM-dd-HH-mm");
* def formattedDate = today.format(formatter)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDAuth/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01ExampleClient2'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01ExampleClient2'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01ExampleClient2'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDAuth/v1'
When method post
Then status 401
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header PDND-ExternalId == null








@getInformazioniClientOrganizationApiIDAUTH_Scaduto
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, senza prima aver mai prelevata la chiave essendo solamente un pattern ID_AUTH. L'invocazione fallisce perchè il token è scaduto

* def result = clean_remote_store_key('KID-ApplicativoBlockingKeyPair')
* def result = clean_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KeyPair/ApplicativoBlockingKeyPair')

# Ricalcolo in ogni test per essere meno suscettibile al cambio minuto
* def today = java.time.LocalDateTime.now()
* def formatter = java.time.format.DateTimeFormatter.ofPattern("YY-MM-dd-HH-mm");
* def formattedDate = today.format(formatter)
* def formatterYYYYMMDD = java.time.format.DateTimeFormatter.ofPattern("YYYYMMdd");
* def formattedDateYYYYMMDD = today.format(formatterYYYYMMDD)
* def formatterHHmm = java.time.format.DateTimeFormatter.ofPattern("HHmm");
* def formattedDateHHmm = today.format(formatterHHmm)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/TestRecuperoInformazioniPDNDAuth-TTLShort/v1"
And path 'test'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingKeyPair', password: 'ApplicativoBlockingKeyPair' })
And header simulazionepdnd-username = 'ApplicativoBlockingKeyPair'
And header simulazionepdnd-password = 'ApplicativoBlockingKeyPair'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingKeyPair'
And header simulazionepdnd-audience = 'TestRecuperoInformazioniPDNDAuth/v1'
When method post
Then status 401

# controlli cache pdnd

* def result = get_remote_store_key_client_id_prefix('DemoSoggettoFruitore/KeyPair/ApplicativoBlockingKeyPair') 

* match result[0].KID contains 'ClientId--DemoSoggettoFruitore/KeyPair/ApplicativoBlockingKeyPair'
# Effettua la conversione Base64 del campo 'data'
* def actualData = result[0].CONTENT_KEY
* def actualDataBase64 = encode_base64_from_bytes(actualData)
* match actualDataBase64 == no_key_base64

* match result[0].CLIENT_ID contains 'DemoSoggettoFruitore/KeyPair/ApplicativoBlockingKeyPair'
* match result[0].ORGANIZATION_DETAILS contains 'c_c000_'+formattedDate+'_0004"'
* match result[0].CLIENT_DETAILS contains formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0004-bbbb-12345678f8dd"'

# controlli tracce credenziali mittente

* def clientIdID = get_id_by_credenziale('token_clientId','DemoSoggettoFruitore/KeyPair/ApplicativoBlockingKeyPair')
* def pdndOrganizationName = get_credenziale_by_refid_greather_then_id('pdnd_org_name',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationName == 'Comune di Esempio '+formattedDate+' ApplicativoBlockingKeyPair'

* def pdndOrganizationJson = get_credenziale_by_refid_greather_then_id('pdnd_org_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndOrganizationJson contains '"category":"Comuni e loro Consorzi e Associazioni"'
* match pdndOrganizationJson contains '"externalId":'
* match pdndOrganizationJson contains '"c_c000_'+formattedDate+'_0004"'
* match pdndOrganizationJson contains '"name":"Comune di Esempio '+formattedDate+' ApplicativoBlockingKeyPair"'

* def pdndClientJson = get_credenziale_by_refid_greather_then_id('pdnd_client_json',clientIdID,credenziale_max_feature['max_id_credenziale'])
* match pdndClientJson contains '"consumerId":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0004-aaaa-82e210e12345"'
* match pdndClientJson contains '"id":"'+formattedDateYYYYMMDD+'-'+formattedDateHHmm+'-0004-bbbb-12345678f8dd"'


