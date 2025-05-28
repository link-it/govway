Feature: Feature test supporto tracciamento PDND

Background:
	* def generate_tracing = (['bash', '-c', 'cd ' + batch_path + '/generatoreStatistiche &&  ./generaTracingPdnd.sh'])
	* def pubblish_tracing = (['bash', '-c', 'cd ' + batch_path + '/generatoreStatistiche &&  ./pubblicaTracingPdnd.sh'])
	* def wither_transactions = read('classpath:test/tracciamento_pdnd/wither_transactions.js')
	* def clear_pdnd_tracing = read('classpath:test/tracciamento_pdnd/clear_pdnd_tracing.js')
	* def get_state = read('classpath:test/tracciamento_pdnd/get_state.js')
	* def utils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.TestUtils')

	* def send_ok = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd.feature@send_ok')
	* def send_error500 = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd.feature@send_error500')
	
	* def mock_get_trace = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd.feature@mock_get_trace')
	* def mock_state = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd.feature@mock_state')
	* def mock_push = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd.feature@mock_push')
	* def mock_clear = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd.feature@mock_clear')
	* def mock_disable = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd.feature@mock_disable')
	* def mock_check = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd.feature@mock_check')

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

@ignore @mock_check
Scenario:
	Given url url_mock + '/control/check'
	When method get
	Then status 200
	
@ignore @send_ok
Scenario:
	Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/' + soggetto + '/TestTracingPDND/v1/ok'
	And request read('classpath:test/rest/sicurezza-messaggio/request.json')
	And header simulazionepdnd-purposeId = 'purposeId'
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
	When method post
	Then status 200

@ignore @send_error500
Scenario:
	Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/' + soggetto + '/TestTracingPDND/v1/error500'
	And request read('classpath:test/rest/sicurezza-messaggio/request.json')
	And header simulazionepdnd-purposeId = 'purposeId'
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
	When method post
	Then status 500

@submit
Scenario: Test in cui genero e poi pubblicio un tracciato generato alla PDND

	# Ripulisco il db e il mock server
    * def trace_date = utils.format(utils.addDays(utils.now(), -1), 'yyyy-MM-dd')
	* clear_pdnd_tracing(1)
	* call mock_clear
	
	# invio delle richieste ai servizi per valorizzare il tracciato
	* call send_ok { soggetto: 'DemoSoggettoErogatore'}
	* call send_error500 { soggetto: 'DemoSoggettoErogatore2'}
	
	# invecchio le transazioni di un giorno
	* wither_transactions('TestTracingPDND', 1)
	
	# chiamo il batch di generazione e poi pubblicazione
	* karate.exec(generate_tracing)
	* karate.exec(pubblish_tracing)
	
	# controllo il contenuto del tracciamento inviato dal batch
	* def mock_request = call mock_get_trace { tracing_id: 0, soggetto: 'DemoSoggettoErogatore'}
	* karate.log(mock_request.response)
	
	* def mock_request = call mock_get_trace { tracing_id: 0, soggetto: 'DemoSoggettoErogatore2'}
	* karate.log(mock_request.response)
	
	# Imposto lo stato del tracciamento nello stato di mock
	* call mock_state { tracing_id: 0, soggetto: 'DemoSoggettoErogatore', state: 'OK'}
	* call mock_state { tracing_id: 0, soggetto: 'DemoSoggettoErogatore2', state: 'ERROR'}
	
	# Eseguo nuovamente il batch per aggiornare lo stato dei tracciati
	* karate.exec(pubblish_tracing)
	
	
	# controllo che il batch di pubblicazione abbia aggiornato correttamente gli stati
	* def state = (get_state('DemoSoggettoErogatore', trace_date))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'OK'
	* match state.tracing_id == '0'
	
	* def state = (get_state('DemoSoggettoErogatore2', trace_date))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'ERROR'
	* match state.tracing_id == '0'
	
	# controllo che il server di mock non abbia errori
	* call mock_check

@submit_old
Scenario: scenario in cui il batch di generazione e pubblicazione invii correttamente i tracciati anche se generati per giorni passati

	# pulisco il db e il server mock, imposto la data di pubblicazione fino a due giorni fa
	* clear_pdnd_tracing(2)
	* call mock_clear
	
	
	# il mock della PDND dovrebbe avere le entry relative a due giorni prima impostate a missing
	* def past_date = utils.format(utils.addDays(utils.now(), -2), 'yyyy-MM-dd')
	* def curr_date = utils.format(utils.addDays(utils.now(), -1), 'yyyy-MM-dd')
	* call mock_push ({ soggetto: 'DemoSoggettoErogatore', state: 'MISSING', date: past_date, content: '', tracing_id: 1200 })
	* call mock_push ({ soggetto: 'DemoSoggettoErogatore2', state: 'MISSING', date: past_date, content: '', tracing_id: 1200 })

	# simulo delle richieste relative a due giorni prima
	* call send_ok { soggetto: 'DemoSoggettoErogatore'}
	* call send_error500 { soggetto: 'DemoSoggettoErogatore2'}
	* wither_transactions('TestTracingPDND', 2)
	
	# avvio il batch di generazione e pubblicazione
	* karate.exec(generate_tracing)
	* karate.exec(pubblish_tracing)
	
	# controllo il contenuto dei tracciati relativi a due giorni prima
	* def mock_request = call mock_get_trace { tracing_id: 1200, soggetto: 'DemoSoggettoErogatore'}
	* karate.log(mock_request.response)
	
	* def mock_request = call mock_get_trace { tracing_id: 1200, soggetto: 'DemoSoggettoErogatore2'}
	* karate.log(mock_request.response)
	
	# imposto il nuovo stato nel server mock
	* call mock_state { tracing_id: 1, soggetto: 'DemoSoggettoErogatore', state: 'OK'}
	* call mock_state { tracing_id: 1, soggetto: 'DemoSoggettoErogatore2', state: 'ERROR'}
	* call mock_state { tracing_id: 1200, soggetto: 'DemoSoggettoErogatore2', state: 'OK'}
	
	# avvio il batch di pubblicazione che dovrebbe aggiornare gli stati 
	* karate.exec(pubblish_tracing)
	
	# controllo che gli stati nel db corrispondano a quelli impostati nel mock
	* def state = (get_state('DemoSoggettoErogatore', past_date))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'PENDING'
	* match state.tracing_id == '1200'
	
	* def state = (get_state('DemoSoggettoErogatore2', past_date))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'OK'
	* match state.tracing_id == '1200'
	
	* def state = (get_state('DemoSoggettoErogatore', curr_date))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'OK'
	* match state.tracing_id == '1'
	
	* def state = (get_state('DemoSoggettoErogatore2', curr_date))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'ERROR'
	* match state.tracing_id == '1'
	
	# controllo che il server di mock non abbia errori
	* call mock_check

@missing
Scenario: Test in cui il batch di pubblicazione riceve la lista di missing dal mock della PDND e li aggiorna

	# pulisco il db e il server della PDND
	* clear_pdnd_tracing(0)
	* call mock_clear
	
	# aggiungo le entry MISSING nel mock della PDND
	* def missing_date1 = utils.format(utils.addDays(utils.now(), -5), 'yyyy-MM-dd')
	* def missing_date2 = utils.format(utils.addDays(utils.now(), -7), 'yyyy-MM-dd')
	* def missing_date3 = utils.format(utils.addDays(utils.now(), -8), 'yyyy-MM-dd')

	* call mock_push ({ soggetto: 'DemoSoggettoErogatore', state: 'MISSING', date: missing_date1, content: '', tracing_id: 1432 })
	* call mock_push ({ soggetto: 'DemoSoggettoErogatore2', state: 'MISSING', date: missing_date2, content: '', tracing_id: 2111 })
	* call mock_push ({ soggetto: 'DemoSoggettoErogatore', state: 'MISSING', date: missing_date3, content: '', tracing_id: 1200 })

	
	# Il batch di pubblicazione inizializzera i record con csv a null
	* karate.exec(pubblish_tracing)
	
	# verifico che il batch di pubblicazione non inviera record con csv null
	* karate.exec(pubblish_tracing)
	
	# genero il csv delle nuove entry e le pubblico sul mock della PDND
	* karate.exec(generate_tracing)
	* karate.exec(pubblish_tracing)
	
	
	# controllo lo stato e il contenuto dei mock aggiunti
	* def mock_request = call mock_get_trace { tracing_id: 1432, soggetto: 'DemoSoggettoErogatore'}
	* karate.log(mock_request.response)
	* def state = (get_state('DemoSoggettoErogatore', missing_date1))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'PENDING'
	* match state.tracing_id == '1432'
	
	* def mock_request = call mock_get_trace { tracing_id: 2111, soggetto: 'DemoSoggettoErogatore2'}
	* karate.log(mock_request.response)
	* def state = (get_state('DemoSoggettoErogatore2', missing_date2))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'PENDING'
	* match state.tracing_id == '2111'
	
	* def mock_request = call mock_get_trace { tracing_id: 1200, soggetto: 'DemoSoggettoErogatore'}
	* karate.log(mock_request.response)
	* def state = (get_state('DemoSoggettoErogatore', missing_date3))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'PENDING'
	* match state.tracing_id == '1200'
	
	# controllo che il server di mock non abbia errori
	* call mock_check

@disabled @disabled_submit
Scenario: Scenario in cui controllo il numero massimo di tentativi

	# pulisco il db e il server della PDND
	* clear_pdnd_tracing(1)
	* call mock_clear
	
	# invio richieste al servizio per valorizzare il tracciato
	* call send_ok { soggetto: 'DemoSoggettoErogatore'}
	* call send_error500 { soggetto: 'DemoSoggettoErogatore2'}
	
	# disabilito l'endpoint /submit del mock della PDND per il soggetto 'DemoSoggettoErogatore'
	* call mock_disable { soggetto: 'DemoSoggettoErogatore', operation: 'submit' }
	
	* wither_transactions('TestTracingPDND', 1)
	
	# eseguo il batch di generazione e pubblicazione
	* karate.exec(generate_tracing)
	* karate.exec(pubblish_tracing)
	
	# controllo il tracciato inviato
	* def mock_request = call mock_get_trace { tracing_id: 0, soggetto: 'DemoSoggettoErogatore2'}
	* karate.log(mock_request.response)
	
	# aggiorno lo stato della transazioni riuscita
	* call mock_state { tracing_id: 0, soggetto: 'DemoSoggettoErogatore2', state: 'ERROR'}
	
	# avvio nuovamente il batch di pubblicazione
	* karate.exec(pubblish_tracing)
	
	# controllo che gli stati nel db corrispondano a quelli avviati nel mock
	* def curr_date = utils.format(utils.addDays(utils.now(), -1), 'yyyy-MM-dd')
	
	* def state = (get_state('DemoSoggettoErogatore', curr_date))
	* match state.stato == 'FAILED'
	* match state.stato_pdnd == 'WAITING'
	* match state.tracing_id == null
	
	* def state = (get_state('DemoSoggettoErogatore2', curr_date))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'ERROR'
	* match state.tracing_id == '0'
	
	# controllo che il server di mock non abbia errori
	* call mock_check
	
	