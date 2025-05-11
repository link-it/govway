Feature: Feature di mock per i test non bloccante push

Background:

    * def isTest =
    """
    function(id) {
        return karate.get("requestHeaders['GovWay-TestSuite-Test-Id'][0]") == id ||
               karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]") == id ||
               karate.get("requestHeaders['govway-testsuite-test-id'][0]") == id
    }
    """

	* def compute_digest = read('classpath:utils/compute_digest.js')
    * def task_id = "fb382380-cf98-4f75-95eb-2a65ba45309e"
    * def get_seed = read('classpath:test/signal_hub/utils/get_seed.js')

# GIRO OK
#
#

Scenario: isTest('push_signal') && request.signalType != 'SEEDUPDATE'


	* match request ==
		"""
		{
			signalId: #number,
			objectType : 'objectType',
  			objectId : '#string',
  			eserviceId : 'eServiceTestID',
  			signalType : 'UPDATE',
		}
		"""
	* def objectId = requestHeaders['GovWay-TestSuite-Plain-Object-ID'][0]
	* def hash = compute_digest('SHA-256', objectId + get_seed())
	* karate.log("plain id: " + objectId + ", seed: " + get_seed())
	* match hash == request.objectId
    * def sigId = request.signalId
    * def responseStatus = 200
    * def response = ({ signalId: sigId })

Scenario: isTest('push_signal') && request.signalType == 'SEEDUPDATE'

	* match request ==
		"""
		{
			signalId: #number,
			objectType : '-',
  			objectId : '-',
  			eserviceId : 'eServiceTestID',
  			signalType : 'SEEDUPDATE',
		}
		"""
    * def sigId = request.signalId
    * def responseStatus = 200
    * def response = ({ signalId: sigId })

Scenario: isTest('seed_failing') && request.signalType != 'SEEDUPDATE'

	* match request ==
		"""
		{
			signalId: #number,
			objectType : 'objectType',
  			objectId : '#string',
  			eserviceId : 'eServiceTestID',
  			signalType : 'UPDATE',
		}
		"""
    * def sigId = request.signalId
    * def responseStatus = 200;
    * def response = ({ signalId: sigId })
        
Scenario: isTest('seed_failing') && request.signalType == 'SEEDUPDATE'

	* match request ==
		"""
		{
			signalId: #number,
			objectType : '-',
  			objectId : '-',
  			eserviceId : 'eServiceTestID',
  			signalType : 'SEEDUPDATE',
		}
		"""
    * def sigId = request.signalId
    * def responseStatus = 503;
    * def response = ({ signalId: sigId })
    
    
