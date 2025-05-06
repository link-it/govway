Feature: Feature test supporto signal hub

Background:

	* def crypto_info_url = govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SignalHubTest/v1/pseudonymization'
	* def push_signal_url = govway_base_path + '/rest/out/DemoSoggettoErogatore/PDND/api-pdnd-push-signals/v1/signals'
	* def push_signal_url_default = govway_base_path + '/rest/out/DemoSoggettoErogatore/PDND/api-pdnd-push-signals-default/v1/signals'
	* def crypto_info_url_erase = govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore2/SignalHubTest/v1/pseudonymization'
	* def api_config_url = govway_config_api_path + ''

	* def check_traccia = read('classpath:utils/check-traccia-idac01.feature')
	* def not_found_resp = read("classpath:test/signal_hub/responses/not_found.json")
	* def generic_error = read("classpath:test/signal_hub/responses/generic_error.json")
	* def bad_request = read("classpath:test/signal_hub/responses/bad_request.json")
	* def unauthorized_error = read("classpath:test/signal_hub/responses/unauthorized_error.json")
	
	* def remove_seeds = read('classpath:test/signal_hub/utils/remove_seeds.js')
	* def get_seed = read('classpath:test/signal_hub/utils/get_seed.js')
	* def count_seeds = read('classpath:test/signal_hub/utils/count_seeds.js')
	* def wither_seeds = read('classpath:test/signal_hub/utils/wither_seeds.js')
	* def get_diagnostico = read('classpath:utils/get_diagnostico.js')
	* def reset_cache = read('classpath:utils/reset-cache.feature')
	* def basic = read('classpath:utils/basic-auth.js')
	
	* def signal_hub_erogazione = read('classpath:test/signal_hub/bodies/erogazione_modi_rest_signalhub.json')
	* def signal_hub_autenticazione = read('classpath:test/signal_hub/bodies/signalhub_autenticazione.json')
	* def signal_hub_autorizzazione = read('classpath:test/signal_hub/bodies/signalhub_autorizzazione.json')
	* def auth_api_config = ({ username: config_api_username, password: config_api_password})
	* def auth_push_signal = { username: 'DemoSignalHub', password: '123456' }
#
# TEST SULL'OTTENIMENTO DELLE INFORMAZIONI DIAGNOSTICHE	
#
#

@test-pseudonymization
@test-pseudonymization-fail
Scenario: Informazioni crittografiche non trovate (identificativo non presente) 

	* def deleted = remove_seeds();
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url crypto_info_url + "?signalId=100"
	And header simulazionepdnd-purposeId = 'purposeId'
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
	And header GovWay-TestSuite-Test-ID = 'crypto_info_not_found'
	 
	When method get
	Then status 200
	And match response == not_found_resp
	
	* def id_transazione = responseHeaders['GovWay-Peer-Transaction-ID'][0]
	* def msgs = get_diagnostico(id_transazione, 'tentato di consegnare con connettore di tipo [signalHubPseudonymization] (location: govway://signalHubPseudonymization): errore http 404')
	* if (msgs.length != 1) karate.fail('messaggio di errore non trovato')
	
@test-pseudonymization
@test-pseudonymization-cache
Scenario: Controlla il corretto funzionamento della cache (rimuove un seed ma rimane salvato in cache)

	* def deleted = remove_seeds();
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url crypto_info_url
	And header simulazionepdnd-purposeId = 'purposeId'
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
	And header GovWay-TestSuite-Test-ID = 'crypto_info_not_found'
	 
	When method get
	Then status 200
	* def seed = get_seed()
	And match response == {seed: '#(seed)', cryptoHashFunction: 'SHA256'}
	
	* def deleted = remove_seeds();
	* def seeds = count_seeds()
	And match seeds == 0
	
	Given url crypto_info_url
	And header simulazionepdnd-purposeId = 'purposeId'
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
	And header GovWay-TestSuite-Test-ID = 'crypto_info_not_found'
	 
	When method get
	Then status 200
	And match response == {seed: '#(seed)', cryptoHashFunction: 'SHA256'}
	
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url crypto_info_url
	And header simulazionepdnd-purposeId = 'purposeId'
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
	And header GovWay-TestSuite-Test-ID = 'crypto_info_not_found'
	 
	When method get
	Then status 200
	* def seed = get_seed()
	And match response == {seed: '#(seed)', cryptoHashFunction: 'SHA256'}
	
@test-pseudonymization
@test-pseudonymization-create
Scenario: Informazioni crittografiche create alla prima richiesta di seme

	* def deleted = remove_seeds();
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url crypto_info_url
	And header simulazionepdnd-purposeId = 'purposeId'
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
	And header GovWay-TestSuite-Test-ID = 'crypto_info_not_found'
		 
	When method get
	Then status 200
	
	* def seed = get_seed()
	And match response == {seed: '#(seed)', cryptoHashFunction: 'SHA256'}
	

@test-pseudonymization
@test-pseudonymization-id
Scenario: richiesta informazioni crittografiche tramite signalId corrispondente

	* def deleted = remove_seeds();
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 200
	And match response == { signalId: '#number' }
	
	* def seed = get_seed()
	* def signalId = parseInt(response.signalId - 1)
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url crypto_info_url
	And param signalId = (signalId)
	And header simulazionepdnd-purposeId = 'purposeId'
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
	And header GovWay-TestSuite-Test-ID = 'crypto_info_not_found'
	When method get
	Then status 200
	And match response == { seed: '#(seed)', cryptoHashFunction: 'SHA256'}

#
# TEST SUL PUSH DEI SEGNALI
#
#

@test-push
@test-push-serviceId
Scenario: push del segnale (usando serviceId) e parametri custom

	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 200
	And match response == { signalId: '#number' }

@test-push
@test-push-name
Scenario: push del segnale corretto (usando service/serviceVersion)

	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Testsuite-Service = 'SignalHubTest'
	And header GovWay-Testsuite-Service-Version = 1
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 200
	And match response == { signalId: '#number' }

@test-push
@test-push-serviceId-default-header
Scenario: push del segnale (usando serviceId) con gli header di default per il passaggio dei parametri

	Given url push_signal_url_default
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Signal-ObjectType = 'objectType'
	And header GovWay-Signal-ObjectId = 'objectId'
	And header GovWay-Signal-Type = 'UPDATE'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request ''
	When method post
	Then status 200
	And match response == { signalId: '#number' }

@test-push
@test-push-name-default-header
Scenario: push del segnale (usando nome/versione servizio) con gli header di default per il passaggio dei parametri

	Given url push_signal_url_default
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Signal-ObjectType = 'objectType'
	And header GovWay-Signal-ObjectId = 'objectId'
	And header GovWay-Signal-Type = 'UPDATE'
	And header GovWay-Signal-Service = 'SignalHubTest'
	And header GovWay-Signal-Service-Version = 1
	And request ''
	When method post
	Then status 200
	And match response == { signalId: '#number' }
	
@test-push
@test-push-serviceId-default-param
Scenario: push del segnale (usando serviceId) con i parametri della query di default per il passaggio dei parametri

	Given url push_signal_url_default
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And param govway_signal_object_type = 'objectType'
	And param govway_signal_object_id = 'objectId'
	And param govway_signal_type = 'UPDATE'
	And param govway_signal_service_id = 'eServiceTestID'
	And request ''
	When method post
	Then status 200
	And match response == { signalId: '#number' }

@test-push
@test-push-name-default-param
Scenario: push del segnale (usando nome/versione servizio) con i parametri della query di default per il passaggio dei parametri

	Given url push_signal_url_default
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And param govway_signal_object_type = 'objectType'
	And param govway_signal_object_id = 'objectId'
	And param govway_signal_type = 'UPDATE'
	And param govway_signal_service = 'SignalHubTest'
	And param govway_signal_service_version = 1
	And request ''
	When method post
	Then status 200
	And match response == { signalId: '#number' }

@test-push
@test-push-wrong-erogatore
Scenario: push del segnale ad un servizio con un erogatore diverso da quello della fruizione

	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceIDError'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 503
	And match response == generic_error
	
	* def id_transazione = responseHeaders['GovWay-Transaction-ID'][0]
	* def msgs = get_diagnostico(id_transazione, 'Accesso ad un servizio con un erogatore diverso da quello della fruizione di default')
	* if (msgs.length != 1) karate.fail('messaggio di errore non trovato')

@test-push
@test-push-role
Scenario: autorizzazione tramite ruoli

	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = 'Basic RGVtb1NpZ25hbEh1YjE6MTIzNDU2'
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 200
	And match response == { signalId: '#number' }

@test-push
@test-push-name-not-found
Scenario: push del segnale con nome/versione servizio non esistente

	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Testsuite-Service = 'SignalHubTestError'
	And header GovWay-Testsuite-Service-Version = 1
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 503
	And match response == generic_error
	
	* def id_transazione = responseHeaders['GovWay-Transaction-ID'][0]
	* def msgs = get_diagnostico(id_transazione, 'Servizio % erogato dal soggetto % non esiste')
	* if (msgs.length != 1) karate.fail('messaggio di errore non trovato')
	
@test-push
@test-push-idService-not-found
Scenario: push del segnale con id eService non esistente

	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID1'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 503
	And match response == generic_error
	
	* def id_transazione = responseHeaders['GovWay-Transaction-ID'][0]
	* def msgs = get_diagnostico(id_transazione, 'icerca fallita: Elementi non trovati che rispettano il filtro di ricerca selezionato: Filtro Servizi:')
	* if (msgs.length != 1) karate.fail('messaggio di errore non trovato')
	
@test-push
@test-push-missing-objectType
Scenario: push del segnale senza l'attributo objectType

	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 503
	And match response == generic_error
	
	* def id_transazione = responseHeaders['GovWay-Transaction-ID'][0]
	* def msgs = get_diagnostico(id_transazione, 'I parametri objectId, objectType and signalType sono necessari')
	* if (msgs.length != 1) karate.fail('messaggio di errore non trovato')

@test-push
@test-push-missing-signalType
Scenario: push del segnale senza l'attributo signalType

	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{}]}
	When method post
	Then status 503
	And match response == generic_error
	
	* def id_transazione = responseHeaders['GovWay-Transaction-ID'][0]
	* def msgs = get_diagnostico(id_transazione, 'I parametri objectId, objectType and signalType sono necessari')
	* if (msgs.length != 1) karate.fail('messaggio di errore non trovato')

@test-push
@test-push-missing-objectId
Scenario: push del segnale senza l'attributo objectId

	Given url push_signal_url
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 503
	And match response == generic_error
	
	* def id_transazione = responseHeaders['GovWay-Transaction-ID'][0]
	* def msgs = get_diagnostico(id_transazione, 'I parametri objectId, objectType and signalType sono necessari')
	* if (msgs.length != 1) karate.fail('messaggio di errore non trovato')

@test-push
@test-push-missing-service
Scenario: push del segnale senza gli attributi serviceId, service, serviceVersion

	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 503
	And match response == generic_error
	
	* def id_transazione = responseHeaders['GovWay-Transaction-ID'][0]
	* def msgs = get_diagnostico(id_transazione, 'Indicare almeno un id eService o il nome/versione di un servizio')
	* if (msgs.length != 1) karate.fail('messaggio di errore non trovato')

@test-push
@test-push-missing-service
Scenario: push del segnale senza gli attributi serviceId, service

	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-Service-Version = 1
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 503
	And match response == generic_error
	
	* def id_transazione = responseHeaders['GovWay-Transaction-ID'][0]
	* def msgs = get_diagnostico(id_transazione, 'Indicare almeno un id eService o il nome/versione di un servizio')
	* if (msgs.length != 1) karate.fail('messaggio di errore non trovato')
	
@test-push
@test-push-missing-service
Scenario: push del segnale senza gli attributi serviceId, serviceVersion

	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-Service = 'SignalHubTest'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 503
	And match response == generic_error
	
	* def id_transazione = responseHeaders['GovWay-Transaction-ID'][0]
	* def msgs = get_diagnostico(id_transazione, 'Indicare almeno un id eService o il nome/versione di un servizio')
	* if (msgs.length != 1) karate.fail('messaggio di errore non trovato')


@test-push
@test-push-wrong-signalType
Scenario: push del segnale signalType non riconosciuto (non appartenente a : [UPDATE, CREATE, DELETE, SEEDUPDATE])

	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "KILL"}]}
	When method post
	Then status 503
	And match response == generic_error
	
	* def id_transazione = responseHeaders['GovWay-Transaction-ID'][0]
	* def msgs = get_diagnostico(id_transazione, 'Il parametro signalType puo\'\' avere solo i seguenti valori: [UPDATE, CREATE, DELETE]')
	* if (msgs.length != 1) karate.fail('messaggio di errore non trovato')

@test-push
@test-push-unauthorized
Scenario: push del segnale tramite servizio applicativo non autorizzzato

	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = 'Basic RGVtb1NpZ25hbEh1YjI6MTIzNDU2'
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 403
	And match response == unauthorized_error
	
	* def id_transazione = responseHeaders['GovWay-Transaction-ID'][0]
	* def msgs = get_diagnostico(id_transazione, 'Autenticazione necessaria per invocare il servizio richiesto')
	* if (msgs.length != 1) karate.fail('messaggio di errore non trovato')
	
@test-push
@test-push-seed-creation
Scenario: push del segnale in cui genero anche il seme correttamnete

	* def deleted = remove_seeds();
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 200
	And match response == { signalId: '#number' }
	* def seeds = count_seeds()
	And match seeds == 1
	
@test-push
@test-push-seed-failing
Scenario: push del segnale in cui non riesco a generare il seed dunque non invio nemmeno il segnale

	* def deleted = remove_seeds();
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'seed_failing'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 400
	And match response == bad_request
	* def seeds = count_seeds()
	And match seeds == 0
	
	* def id_transazione = responseHeaders['GovWay-Transaction-ID'][0]
	* def msgs = get_diagnostico(id_transazione, 'seed update non riuscito')
	* if (msgs.length != 1) karate.fail('messaggio di errore non trovato')
	
@test-push
@test-push-seed-replace
Scenario: push del segnale in cui mi tengo lo storico dei semi precedenti e controllo che vengano eliminati i semi troppo vecchi

	* def deleted = remove_seeds();
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 200
	And match response == { signalId: '#number' }
	* def seeds = count_seeds()
	And match seeds == 1
	
	* def invalidates = wither_seeds(100)
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 200
	And match response == { signalId: '#number' }
	* def seeds = count_seeds()
	And match seeds == 2
	
	* def invalidates = wither_seeds(100)
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 200
	And match response == { signalId: '#number' }
	* def seeds = count_seeds()
	And match seeds == 3
	
	* def invalidates = wither_seeds(100)
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 200
	And match response == { signalId: '#number' }
	* def seeds = count_seeds()
	And match seeds == 3
	
@test-push
@test-push-seed-keep
Scenario: push del segnale in cui riutilizzo lo stesso seme fino alla scadenza

	* def deleted = remove_seeds();
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 200
	And match response == { signalId: '#number' }
	* def seeds = count_seeds()
	And match seeds == 1
	
	* def invalidates = wither_seeds(1)
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 200
	And match response == { signalId: '#number' }
	* def seeds = count_seeds()
	And match seeds == 1
	
	* def invalidates = wither_seeds(1)
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 200
	And match response == { signalId: '#number' }
	* def seeds = count_seeds()
	And match seeds == 1
	
	* def invalidates = wither_seeds(3)
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url push_signal_url
	And param govway_testsuite_objectId = 'objectId'
	And header Authorization = call basic (auth_push_signal)
	And header GovWay-TestSuite-Test-ID = 'push_signal'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceTestID'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 200
	And match response == { signalId: '#number' }
	* def seeds = count_seeds()
	And match seeds == 2
	
@test-clean
Scenario: test per verificare la corretta rimozione dei record nella tabella servizi_digest_params alla rimozione di un servizio

	* def deleted = remove_seeds();
	* call reset_cache { cache_name: 'ConfigurazionePdD' }
	
	Given url govway_config_api_path + '/erogazioni'
	And header Authorization = call basic (auth_api_config)
	And param profilo = 'ModIPA'
	And param soggetto = 'DemoSoggettoErogatore'
	And request (signal_hub_erogazione)
	When method post
	Then status 201

	Given url govway_config_api_path + '/erogazioni/' + signal_hub_erogazione.erogazione_nome + '/' + signal_hub_erogazione.erogazione_versione + '/configurazioni/controllo-accessi/gestione-token' 
	And header Authorization = call basic (auth_api_config)
	And param profilo = 'ModIPA'
	And param soggetto = 'DemoSoggettoErogatore'
	And request (signal_hub_autenticazione)
	When method put
	Then status 204

	Given url govway_config_api_path + '/erogazioni/' + signal_hub_erogazione.erogazione_nome + '/' + signal_hub_erogazione.erogazione_versione + '/configurazioni/controllo-accessi/autorizzazione'
	And header Authorization = call basic (auth_api_config)
	And param profilo = 'ModIPA'
	And param soggetto = 'DemoSoggettoErogatore'
	And request (signal_hub_autorizzazione)
	When method put
	Then status 204

	Given url crypto_info_url_erase
	And header simulazionepdnd-purposeId = 'purposeId'
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
	And header GovWay-TestSuite-Test-ID = 'crypto_info_not_found'

	When method get
	Then status 200

	* def seeds = count_seeds()
	And match seeds == 1

	Given url govway_config_api_path + '/erogazioni/' + signal_hub_erogazione.erogazione_nome + '/' + signal_hub_erogazione.erogazione_versione
	And header Authorization = call basic (auth_api_config)
	And param profilo = 'ModIPA'
	And param soggetto = 'DemoSoggettoErogatore'
	When method delete
	Then status 204

	* def seeds = count_seeds()
	And match seeds == 0

