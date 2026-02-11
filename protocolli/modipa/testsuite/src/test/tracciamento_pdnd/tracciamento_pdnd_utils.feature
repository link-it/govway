Feature: Feature di utilita per il test sul traciamento PDND

@ignore @mock_get_trace
Scenario:
	Given url url_mock + '/control/' + tracing_id + '/get'
	And param pdd = soggetto
	When method get
	Then status 200
	
@ignore @mock_state
Scenario:
	Given url url_mock + '/control/' + tracing_id + '/update'
	And param pdd = soggetto
	And param state = state
	When method get
	Then status 200

@ignore @mock_push
Scenario:
	Given url url_mock + '/control/push'
	And param pdd = soggetto
	And param state = state
	And param date = date
	And param content = content
	And param id = tracing_id
	When method get
	Then status 200

@ignore @mock_fill
Scenario:
	Given url url_mock + '/control/fill'
	And param pdd = soggetto
	And param lastDay = lastDay
	And param size = size
	When method get
	Then status 200
	
@ignore @mock_clear
Scenario:
	Given url url_mock + '/control/clear'
	When method get
	Then status 200

@ignore @mock_disable
Scenario:
	Given url url_mock + '/control/' + operation + '/disable'
	And param pdd = soggetto
	When method get
	Then status 200

@ignore @mock_enable
Scenario:
	Given url url_mock + '/control/' + operation + '/enable'
	And param pdd = soggetto
	When method get
	Then status 200

@ignore @mock_check
Scenario:
	Given url url_mock + '/control/check'
	When method get
	Then status 200
	
@ignore @send_ok
Scenario:
	karate.log(purpose_id)
	Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/' + soggetto + '/TestTracingPDND/v1/ok'
	And request read('classpath:test/rest/sicurezza-messaggio/request.json')
	And header simulazionepdnd-purposeId = purpose_id
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
	When method post
	Then status 200

@ignore @send_error500
Scenario:
	Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/' + soggetto + '/TestTracingPDND/v1/error500'
	And request read('classpath:test/rest/sicurezza-messaggio/request.json')
	And header simulazionepdnd-purposeId = purpose_id
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
	When method post
	Then status 500

@ignore @send_error404
Scenario:
	Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/' + soggetto + '/TestTracingPDND/v1/error404'
	And request read('classpath:test/rest/sicurezza-messaggio/request.json')
	And header simulazionepdnd-purposeId = purpose_id
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
	When method post
	Then status 404