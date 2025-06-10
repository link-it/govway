@ignore
Feature: Preparazione Test

Background:
	* def delete_lock =
    """
    function(db) {
        db.update("UPDATE op2_semaphore set node_id=NULL, creation_time=NULL, details=NULL, update_time=NULL WHERE node_id='TESTSUITE' AND applicative_id='GenerazioneStatisticheOrarie'");
    }
    """

     * def wait_for_lock =
    """
    function(db){ 
        do {
            karate.log("CHECKO LA LOCK FROM op2_semaphore")
            var result = db.readRows("SELECT * from op2_semaphore WHERE applicative_id='GenerazioneStatisticheOrarie'");
            karate.log(result);
            try {
                java.lang.Thread.sleep(100) 
            } catch (e) {

            }
        } while(result[0].node_id != null)
        db.update("UPDATE op2_semaphore set node_id='TESTSUITE', creation_time=CURRENT_TIMESTAMP, details='Lock testsuite monitoraggio' WHERE applicative_id='GenerazioneStatisticheOrarie'");
        
    }
    """

@prepare
Scenario: Preparazione Test ModI
    * def query_modi = { profilo: 'ModI' }

    * def soggetto_erogatore_pdnd = read('classpath:bodies/modi/soggetto-erogatore-pdnd.json')
    * eval soggetto_erogatore_pdnd.modi.pdnd.tracciamento_pdnd = 'Abilitato'
    * call put ({ resourcePath: 'soggetti/' + soggetto_erogatore_pdnd.nome, body: soggetto_erogatore_pdnd, query_params: query_modi })
    
    * def api_pdnd = read('classpath:bodies/modi/api-pdnd.json')
    * eval randomize(api_pdnd, ["nome"])
    * def api_pdnd_path = 'api/' + api_pdnd.nome + '/' + api_pdnd.versione
    * eval api_pdnd.tags = ['TESTSUITE']
	
    * call create ({ resourcePath: 'api', body: api_pdnd, query_params: query_modi })

    * eval query_modi.soggetto = 'rs-monitor-DemoSoggettoErogatore'
	
    * def erogazione_pdnd = read('classpath:bodies/modi/erogazione-pdnd.json')
    * eval erogazione_pdnd.api_nome = api_pdnd.nome
    * eval erogazione_pdnd.api_versione = api_pdnd.versione
    * def erogazione_pdnd_path = 'erogazioni/' + erogazione_pdnd.api_nome + '/' + erogazione_pdnd.api_versione
    * call create ({ resourcePath: 'erogazioni', body: erogazione_pdnd, query_params: query_modi })

	* def autenticazione_erogazione_pdnd = read('classpath:bodies/modi/erogazione-pdnd-autenticazione.json')
	* call put ({ resourcePath: erogazione_pdnd_path + '/configurazioni/controllo-accessi/gestione-token', body: autenticazione_erogazione_pdnd, query_params: query_modi })
	
	* def autorizzazione_erogazione_pdnd = read('classpath:bodies/modi/erogazione-pdnd-autorizzazione.json')
	* call put ({ resourcePath: erogazione_pdnd_path + '/configurazioni/controllo-accessi/autorizzazione', body: autorizzazione_erogazione_pdnd, query_params: query_modi })

	* eval query_modi.soggetto = 'rs-monitor-DemoSoggettoFruitore'
	
    * def fruizione_pdnd = read('classpath:bodies/modi/fruizione-pdnd.json')
    * set fruizione_pdnd.api_nome = api_pdnd.nome
    * set fruizione_pdnd.api_versione = api_pdnd.versione
    * set fruizione_pdnd.connettore.endpoint = 'http://localhost:8080/govway/rest/in/rs-monitor-DemoSoggettoErogatore/' + erogazione_pdnd.api_nome + '/v1'
    * def fruizione_pdnd_path = 'fruizioni/' +  fruizione_pdnd.erogatore + '/' + fruizione_pdnd.api_nome + '/' + fruizione_pdnd.api_versione
    * call create ({ resourcePath: 'fruizioni', body: fruizione_pdnd, query_params: query_modi })
    
    * def purpose_ids = ({ id1: 'purposeId1', id2: 'purposeId2', id3: 'purposeId3' })
    * randomize(purpose_ids, ['id1', 'id2', 'id3'])
    
    Given url govwayBasePath + '/rest/out/rs-monitor-DemoSoggettoFruitore/rs-monitor-DemoSoggettoErogatore/' + fruizione_pdnd.api_nome + '/v' + fruizione_pdnd.api_versione + '/ok'
	And header simulazionepdnd-purposeId = purpose_ids.id1
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'rs-monitor-ApplicativoBlockingJWK'
	When method get
	Then status 200
	
	Given url govwayBasePath + '/rest/out/rs-monitor-DemoSoggettoFruitore/rs-monitor-DemoSoggettoErogatore/' + fruizione_pdnd.api_nome + '/v' + fruizione_pdnd.api_versione + '/ok'
	And header simulazionepdnd-purposeId = purpose_ids.id1
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'rs-monitor-ApplicativoBlockingJWK'
	When method get
	Then status 200
	
	Given url govwayBasePath + '/rest/out/rs-monitor-DemoSoggettoFruitore/rs-monitor-DemoSoggettoErogatore/' + fruizione_pdnd.api_nome + '/v' + fruizione_pdnd.api_versione + '/ok'
	And header simulazionepdnd-purposeId = purpose_ids.id2
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'rs-monitor-ApplicativoBlockingJWK'
	When method get
	Then status 200
	
	Given url govwayBasePath + '/rest/out/rs-monitor-DemoSoggettoFruitore/rs-monitor-DemoSoggettoErogatore/' + fruizione_pdnd.api_nome + '/v' + fruizione_pdnd.api_versione + '/ok'
	And header simulazionepdnd-purposeId = purpose_ids.id3
	And header simulazionepdnd-audience = 'audience'
	And header simulazionepdnd-username = 'rs-monitor-ApplicativoBlockingJWK'
	When method get
	Then status 200
    
@cleanup
Scenario: pulizia Test ModI
	* configure headers = ({ "Authorization": govwayConfAuth })
	
	* def query_modi = { profilo: 'ModI', soggetto: 'rs-monitor-DemoSoggettoFruitore' }
	
	* def soggetto_erogatore_pdnd = read('classpath:bodies/modi/soggetto-erogatore-pdnd.json')
    * eval soggetto_erogatore_pdnd.modi.pdnd.tracciamento_pdnd = 'Default'
    * call put ({ resourcePath: 'soggetti/' + soggetto_erogatore_pdnd.nome, body: soggetto_erogatore_pdnd, query_params: query_modi })
    
	* call delete ({ resourcePath: setup.fruizione_pdnd_path, query_params: query_modi })
	
	* eval query_modi.soggetto = 'rs-monitor-DemoSoggettoErogatore'

	* call delete ({ resourcePath: setup.erogazione_pdnd_path, query_params: query_modi })
	
	* eval query_modi.soggetto = null
	
	* call delete ({ resourcePath: setup.api_pdnd_path, query_params: query_modi })
