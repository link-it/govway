@ignore
Feature: Pubblicazione di un segnale per l'erogazione SignalHubTestRotazione di DemoSoggettoErogatore3

# Richiamabile tramite '* def x = call read(...) { test_id: '...' }'; utilizza le variabili del chiamante
# push_signal_url_mixed, basic e auth_push_signal_mixed.
# Il mock verifica il digest dell'objectId con il seme piu' recente dell'erogazione presente sulla base dati.

Scenario: push del segnale tramite la fruizione di DemoSoggettoErogatore3

	Given url push_signal_url_mixed
	And param govway_testsuite_objectId = 'objectIdRotazione'
	And header GovWay-TestSuite-Plain-Object-ID = 'objectIdRotazione'
	And header Authorization = call basic (auth_push_signal_mixed)
	And header GovWay-TestSuite-Test-ID = test_id
	And header GovWay-TestSuite-EService-Id = 'eServiceRotazione'
	And header GovWay-TestSuite-Soggetto-Seed = 'DemoSoggettoErogatore3'
	And header GovWay-TestSuite-Service-Name-Seed = 'SignalHubTestRotazione'
	And header GovWay-Testsuite-ObjectType = 'objectType'
	And header GovWay-Signal-ServiceId = 'eServiceRotazione'
	And request {data:[{signalType: "UPDATE"}]}
	When method post
	Then status 200
	And match response == { signalId: '#number' }
