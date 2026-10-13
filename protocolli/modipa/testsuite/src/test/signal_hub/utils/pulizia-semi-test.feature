@ignore
Feature: Pulizia iniziale dei semi delle erogazioni Signal Hub della testsuite

# Richiamata tramite 'callonce' dal Background: eseguita una sola volta all'avvio della feature.
# Elimina solamente i semi delle erogazioni della testsuite, lasciando inalterati gli altri semi presenti sulla base dati.

Scenario: pulizia dei semi della testsuite

	* def remove_test_seeds = read('classpath:test/signal_hub/utils/remove_test_seeds.js')
	* def eliminati = remove_test_seeds()
