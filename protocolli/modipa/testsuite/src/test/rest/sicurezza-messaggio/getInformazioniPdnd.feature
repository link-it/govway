Feature: Testing get informazioni dalla PDND

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }

    * def clean_remote_store_key = read('classpath:utils/remote_store_key.js')

    * def result = clean_remote_store_key('KID-ApplicativoBlockingIDA01ExampleClient3')
    * def result = clean_remote_store_key('KID-ApplicativoBlockingIDA01')
    * def result = clean_remote_store_key('KID-ApplicativoBlockingJWK')



@getInformazioniClientOrganizationApiIDAUTH
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, senza prima aver mai prelevata la chiave essendo solamente un pattern ID_AUTH

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


@getInformazioniClientOrganizationApiIntegrity
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, prima però viene prelevata la chiave essendo solamente un pattern INTEGRITY con truststore PDND

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



@getInformazioniClientOrganizationApiPrimaAuthPoiIntegrity
Scenario: Test che arricchisce le informazioni sul client prelevandole dalla PDND, prima viene invocata un'azione che non fa scaturire il download della chiave poi viene prelevata la chiave usando un'altra azione che prevede pattern INTEGRITY con truststore PDND

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
