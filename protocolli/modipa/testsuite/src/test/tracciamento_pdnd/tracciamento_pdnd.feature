Feature: Feature test supporto tracciamento PDND

Background:
	* def generate_tracing = (['bash', '-c', 'cd ' + batch_path + '/generatoreStatistiche &&  ./generaReportPDND.sh'])
	* def pubblish_tracing = (['bash', '-c', 'cd ' + batch_path + '/generatoreStatistiche &&  ./pubblicaReportPDND.sh'])
	* def wither_transactions = read('classpath:test/tracciamento_pdnd/wither_transactions.js')
	* def clear_pdnd_tracing = read('classpath:test/tracciamento_pdnd/clear_pdnd_tracing.js')
	* def get_state = read('classpath:test/tracciamento_pdnd/get_state.js')
	* def check_csv = read('classpath:test/tracciamento_pdnd/check_csv.js')
	* def utils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.TestUtils')
	* def basic = read('classpath:utils/basic-auth.js')

	* def send_ok = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd_utils.feature@send_ok')
	* def send_error500 = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd_utils.feature@send_error500')
	* def send_error404 = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd_utils.feature@send_error404')
	
	* def mock_get_trace = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd_utils.feature@mock_get_trace')
	* def mock_state = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd_utils.feature@mock_state')
	* def mock_push = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd_utils.feature@mock_push')
	* def mock_clear = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd_utils.feature@mock_clear')
	* def mock_disable = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd_utils.feature@mock_disable')
	* def mock_enable = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd_utils.feature@mock_enable')
	* def mock_check = read('classpath:test/tracciamento_pdnd/tracciamento_pdnd_utils.feature@mock_check')
	
	* def auth_api_monitor = ({ username: monitor_api_username, password: monitor_api_password})


@submit
Scenario: Test in cui genero e poi pubblico un tracciato generato alla PDND
	* def purpose_id = 'purposeId'
	
	# Ripulisco il db e il mock server
    * def trace_date = utils.format(utils.addDays(utils.now(), -1), 'yyyy-MM-dd')
	* clear_pdnd_tracing(1)
	* call mock_clear
	
	# invio delle richieste ai servizi per valorizzare il tracciato
	* call send_ok { soggetto: 'DemoSoggettoErogatore', purpose_id: 'purposeId'}
	* call send_ok { soggetto: 'DemoSoggettoErogatore', purpose_id: 'purposeId'}
	* call send_ok { soggetto: 'DemoSoggettoErogatore', purpose_id: 'newPurposeId'}
	* call send_error500 { soggetto: 'DemoSoggettoErogatore2', purpose_id: 'purposeId'}
	* call send_error404 { soggetto: 'DemoSoggettoErogatore2', purpose_id: 'errorPurposeId'}
	
	# invecchio le transazioni di un giorno
	* wither_transactions('TestTracingPDND', 1)
	
	# chiamo il batch di generazione e poi pubblicazione
	* karate.exec(generate_tracing)
	* karate.exec(pubblish_tracing)
	
	* def curr_date = utils.format(utils.addDays(utils.now(), -1), 'yyyy-MM-dd')
	# controllo il contenuto del tracciamento inviato dal batch
	* def mock_request = call mock_get_trace ({ tracing_id: utils.uuidFromInteger(0), soggetto: 'DemoSoggettoErogatore'})
	* def trace = mock_request.response
	* check_csv(trace, [{date: curr_date, purpose_id: 'purposeId', status: '200', requests_count: '2'}, {date: curr_date, purpose_id: 'newPurposeId', status: '200', requests_count: '1'}])
	
	* def mock_request = call mock_get_trace ({ tracing_id: utils.uuidFromInteger(0), soggetto: 'DemoSoggettoErogatore2'})
	* def trace = mock_request.response
	* check_csv(trace, [{date: curr_date, purpose_id: 'purposeId', status: '500', requests_count: '1'}, {date: curr_date, purpose_id: 'errorPurposeId', status: '404', requests_count: '1'}])
	
	# Imposto lo stato del tracciamento nello stato di mock
	* call mock_state ({ tracing_id: utils.uuidFromInteger(0), soggetto: 'DemoSoggettoErogatore', state: 'OK'})
	* call mock_state ({ tracing_id: utils.uuidFromInteger(0), soggetto: 'DemoSoggettoErogatore2', state: 'ERROR'})
	
	# Eseguo nuovamente il batch per aggiornare lo stato dei tracciati
	* karate.exec(pubblish_tracing)
	
	
	# controllo che il batch di pubblicazione abbia aggiornato correttamente gli stati
	* def state = (get_state('DemoSoggettoErogatore', trace_date))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'OK'
	* match state.tracing_id == (utils.uuidFromInteger(0))
	
	* def state = (get_state('DemoSoggettoErogatore2', trace_date))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'ERROR'
	* match state.tracing_id == (utils.uuidFromInteger(0))
	
	# controllo che il server di mock non abbia errori
	* call mock_check

@submit_old
Scenario: scenario in cui il batch di generazione e pubblicazione invii correttamente i tracciati anche se generati per giorni passati
	* def purpose_id = 'purposeId'
	
	* def tracing_id1 = utils.uuidFromInteger(1)
	* def tracing_id2 = utils.uuidFromInteger(1200)
	
	# pulisco il db e il server mock, imposto la data di pubblicazione fino a due giorni fa
	* clear_pdnd_tracing(2)
	* call mock_clear
	
	
	# il mock della PDND dovrebbe avere le entry relative a due giorni prima impostate a missing
	* def past_date = utils.format(utils.addDays(utils.now(), -2), 'yyyy-MM-dd')
	* def curr_date = utils.format(utils.addDays(utils.now(), -1), 'yyyy-MM-dd')
	* call mock_push ({ soggetto: 'DemoSoggettoErogatore', state: 'MISSING', date: past_date, content: '', tracing_id: tracing_id2 })
	* call mock_push ({ soggetto: 'DemoSoggettoErogatore2', state: 'MISSING', date: past_date, content: '', tracing_id: tracing_id2 })

	# simulo delle richieste relative a due giorni prima
	* call send_ok { soggetto: 'DemoSoggettoErogatore'}
	* call send_error500 { soggetto: 'DemoSoggettoErogatore2'}
	* wither_transactions('TestTracingPDND', 2)
	
	# avvio il batch di generazione e pubblicazione
	* karate.exec(generate_tracing)
	* karate.exec(pubblish_tracing)
	
	# controllo il contenuto dei tracciati relativi a due giorni prima
	* def mock_request = call mock_get_trace ({ tracing_id: tracing_id2, soggetto: 'DemoSoggettoErogatore'})
	* karate.log(mock_request.response)
	
	* def mock_request = call mock_get_trace ({ tracing_id: tracing_id2, soggetto: 'DemoSoggettoErogatore2'})
	* karate.log(mock_request.response)
	
	# imposto il nuovo stato nel server mock
	* call mock_state ({ tracing_id: tracing_id1, soggetto: 'DemoSoggettoErogatore', state: 'OK'})
	* call mock_state ({ tracing_id: tracing_id1, soggetto: 'DemoSoggettoErogatore2', state: 'ERROR'})
	* call mock_state ({ tracing_id: tracing_id2, soggetto: 'DemoSoggettoErogatore2', state: 'OK'})
	
	# avvio il batch di pubblicazione che dovrebbe aggiornare gli stati 
	* karate.exec(pubblish_tracing)
	
	# controllo che gli stati nel db corrispondano a quelli impostati nel mock
	* def state = (get_state('DemoSoggettoErogatore', past_date))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'PENDING'
	* match state.tracing_id == (tracing_id2)
	
	* def state = (get_state('DemoSoggettoErogatore2', past_date))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'OK'
	* match state.tracing_id == (tracing_id2)
	
	* def state = (get_state('DemoSoggettoErogatore', curr_date))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'OK'
	* match state.tracing_id == (tracing_id1)
	
	* def state = (get_state('DemoSoggettoErogatore2', curr_date))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'ERROR'
	* match state.tracing_id == (tracing_id1)
	
	# controllo che il server di mock non abbia errori
	* call mock_check

@missing
Scenario: Test in cui il batch di pubblicazione riceve la lista di missing dal mock della PDND e li aggiorna
	* def purpose_id = 'purposeId'
	
	# pulisco il db e il server della PDND
	* clear_pdnd_tracing(0)
	* call mock_clear
	
	# aggiungo le entry MISSING nel mock della PDND
	* def missing_date1 = utils.format(utils.addDays(utils.now(), -5), 'yyyy-MM-dd')
	* def missing_date2 = utils.format(utils.addDays(utils.now(), -7), 'yyyy-MM-dd')
	* def missing_date3 = utils.format(utils.addDays(utils.now(), -8), 'yyyy-MM-dd')

	* def tracing_id1 = (utils.uuidFromInteger(1432))
	* def tracing_id2 = (utils.uuidFromInteger(2111))
	* def tracing_id3 = (utils.uuidFromInteger(1200))
	
	* call mock_push ({ soggetto: 'DemoSoggettoErogatore', state: 'MISSING', date: missing_date1, content: '', tracing_id: tracing_id1 })
	* call mock_push ({ soggetto: 'DemoSoggettoErogatore2', state: 'MISSING', date: missing_date2, content: '', tracing_id: tracing_id2 })
	* call mock_push ({ soggetto: 'DemoSoggettoErogatore', state: 'MISSING', date: missing_date3, content: '', tracing_id: tracing_id3 })

	
	# Il batch di pubblicazione inizializzera i record con csv a null
	* karate.exec(pubblish_tracing)
	
	# verifico che il batch di pubblicazione non inviera record con csv null
	* karate.exec(pubblish_tracing)
	
	# genero il csv delle nuove entry e le pubblico sul mock della PDND
	* karate.exec(generate_tracing)
	* karate.exec(pubblish_tracing)
	
	
	# controllo lo stato e il contenuto dei mock aggiunti
	* def mock_request = call mock_get_trace ({ tracing_id: tracing_id1, soggetto: 'DemoSoggettoErogatore'})
	* karate.log(mock_request.response)
	* def state = (get_state('DemoSoggettoErogatore', missing_date1))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'PENDING'
	* match state.tracing_id == (tracing_id1)
	
	* def mock_request = call mock_get_trace ({ tracing_id: tracing_id2, soggetto: 'DemoSoggettoErogatore2'})
	* karate.log(mock_request.response)
	* def state = (get_state('DemoSoggettoErogatore2', missing_date2))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'PENDING'
	* match state.tracing_id == (tracing_id2)
	
	* def mock_request = call mock_get_trace ({ tracing_id: tracing_id3, soggetto: 'DemoSoggettoErogatore'})
	* karate.log(mock_request.response)
	* def state = (get_state('DemoSoggettoErogatore', missing_date3))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'PENDING'
	* match state.tracing_id == (tracing_id3)
	
	# controllo che il server di mock non abbia errori
	* call mock_check

@disabled @disabled_submit
Scenario: Scenario in cui controllo il numero massimo di tentativi
	* def purpose_id = 'purposeId'
	
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
	* def tracing_id0 = (utils.uuidFromInteger(0))
	* def mock_request = call mock_get_trace ({ tracing_id: tracing_id0, soggetto: 'DemoSoggettoErogatore2'})
	* karate.log(mock_request.response)
	
	# aggiorno lo stato della transazioni riuscita
	* call mock_state ({ tracing_id: tracing_id0, soggetto: 'DemoSoggettoErogatore2', state: 'ERROR'})
	
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
	* match state.tracing_id == (tracing_id0)
	
	# controllo che il server di mock non abbia errori
	* call mock_check
	
@disabled @disabled_max_attempt
Scenario: Scenario in cui controllo l'aumentare dei numeri di tentativi e il funzionamento del force publish
	* def purpose_id = 'purposeId'
	* def tracing_id0 = (utils.uuidFromInteger(0))
	
	# pulisco il db e il server della PDND
	* clear_pdnd_tracing(1)
	* call mock_clear
	
	# invio richieste al servizio per valorizzare il tracciato
	* call send_ok { soggetto: 'DemoSoggettoErogatore'}
	* call send_error500 { soggetto: 'DemoSoggettoErogatore2'}
	
	# disabilito l'endpoint /submit del mock della PDND per il soggetto 'DemoSoggettoErogatore'
	* call mock_disable { soggetto: 'DemoSoggettoErogatore', operation: 'submit' }
	* call mock_disable { soggetto: 'DemoSoggettoErogatore2', operation: 'submit' }
	
	* wither_transactions('TestTracingPDND', 1)
	
	# eseguo il batch di generazione e pubblicazione
	* karate.exec(generate_tracing)
	* karate.exec(pubblish_tracing)
	
	# controllo che gli stati nel db corrispondano a quelli avviati nel mock
	* def curr_date = utils.format(utils.addDays(utils.now(), -1), 'yyyy-MM-dd')
	
	* def state = (get_state('DemoSoggettoErogatore', curr_date))
	* match state.stato == 'FAILED'
	* match state.stato_pdnd == 'WAITING'
	* match state.tracing_id == null
	* match state.tentativi_pubblicazione == 1
	
	* def state = (get_state('DemoSoggettoErogatore2', curr_date))
	* match state.stato == 'FAILED'
	* match state.stato_pdnd == 'WAITING'
	* match state.tracing_id == null
	* match state.tentativi_pubblicazione == 1
	
	# avvio nuovamente il batch di pubblicazione
	* karate.exec(pubblish_tracing)
	
	# controllo che gli stati nel db corrispondano a quelli avviati nel mock
	* def curr_date = utils.format(utils.addDays(utils.now(), -1), 'yyyy-MM-dd')
	
	* def state = (get_state('DemoSoggettoErogatore', curr_date))
	* match state.stato == 'FAILED'
	* match state.stato_pdnd == 'WAITING'
	* match state.tracing_id == null
	* match state.tentativi_pubblicazione == 2
	
	* def state = (get_state('DemoSoggettoErogatore2', curr_date))
	* match state.stato == 'FAILED'
	* match state.stato_pdnd == 'WAITING'
	* match state.tracing_id == null
	* match state.tentativi_pubblicazione == 2

	* call mock_enable { soggetto: 'DemoSoggettoErogatore2', operation: 'submit' }
	
	# avvio nuovamente il batch di pubblicazione
	* karate.exec(pubblish_tracing)
	
	* def state = (get_state('DemoSoggettoErogatore', curr_date))
	* match state.stato == 'FAILED'
	* match state.stato_pdnd == 'WAITING'
	* match state.tracing_id == null
	* match state.tentativi_pubblicazione == 3
	
	* def state = (get_state('DemoSoggettoErogatore2', curr_date))
	* match state.stato == 'PUBLISHED'
	* match state.stato_pdnd == 'PENDING'
	* match state.tracing_id == (tracing_id0)
	* match state.tentativi_pubblicazione == 3
	
	#raggiuno il massimo di tentativi non pubblico piu 
	* karate.exec(pubblish_tracing)
	* call mock_enable { soggetto: 'DemoSoggettoErogatore', operation: 'submit' }

	* def state = (get_state('DemoSoggettoErogatore', curr_date))
	* match state.stato == 'FAILED'
	* match state.stato_pdnd == 'WAITING'
	* match state.tracing_id == null
	* match state.tentativi_pubblicazione == 3
	
	
	# ottengo l'id del tracciato con tentativi massimi
	* def data_fine = utils.format(utils.now(), 'yyyy-MM-dd')
	* def data_inizio = curr_date
	
	Given url govway_monitor_api_path + '/reportistica/tracing-pdnd'
	And header Authorization = call basic (auth_api_monitor)
	And param soggetto = 'DemoSoggettoErogatore'
	And param numero_tentativi_tracing = 3
	And param data_inizio = data_inizio
	And param data_fine = data_fine
	When method get
	Then status 200
	
	* def id_record = response.items[0].id
	* karate.log(id_record)
	
	# forzo la pubblicazione del tracciato
	Given url govway_monitor_api_path + '/reportistica/tracing-pdnd/' + id_record + '/force-publish'
	And header Authorization = call basic (auth_api_monitor)
	And request { force_publish: true }
	When method put
	Then status 200
	And match response == { success: true }
	
	#anche se il tentativo massimo è stato raggiunto la pubblicazione risulta forzata
	* call mock_disable { soggetto: 'DemoSoggettoErogatore', operation: 'submit' }
	* karate.exec(pubblish_tracing)
	* call mock_enable { soggetto: 'DemoSoggettoErogatore', operation: 'submit' }

	* def state = (get_state('DemoSoggettoErogatore', curr_date))
	* match state.stato == 'FAILED'
	* match state.stato_pdnd == 'WAITING'
	* match state.tracing_id == null
	* match state.tentativi_pubblicazione == 4
	
	#dopo il primo force il record non viene piu ripubblicato forzatamente
	* karate.exec(pubblish_tracing)
	* call mock_enable { soggetto: 'DemoSoggettoErogatore', operation: 'submit' }

	* def state = (get_state('DemoSoggettoErogatore', curr_date))
	* match state.stato == 'FAILED'
	* match state.stato_pdnd == 'WAITING'
	* match state.tracing_id == null
	* match state.tentativi_pubblicazione == 4
	
	
	# controllo che il server di mock non abbia errori
	* call mock_check
	
	
