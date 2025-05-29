@parallel=false
Feature: Ricerca Transazioni tramite purposeId

Background: 
  * configure afterFeature = function(){ karate.log('mi ammazzo'); karate.call('classpath:prepare_modi_tests.feature@cleanup'); }

  * def ricerca_url = monitorUrl + '/monitoraggio/transazioni/purpose-id'
  * call read('classpath:crud_commons.feature')

  * def setup = callonce read('classpath:prepare_modi_tests.feature@prepare')
  
  * configure headers = ({ "Authorization": govwayMonitorCred }) 

@get
Scenario: 
	Given url ricerca_url
	And param purpose_id = setup.purpose_ids.id1
	When method get
	Then status 200
	* karate.log(response.items.length)
	* match (response.items.length) == 4
	
	Given url ricerca_url
	And param purpose_id = setup.purpose_ids.id2
	When method get
	Then status 200
	* match (response.items.length) == 2
	
	Given url ricerca_url
	And param purpose_id = setup.purpose_ids.id3
	When method get
	Then status 200
	* match (response.items.length) == 2