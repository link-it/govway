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

    * def task_id = "fb382380-cf98-4f75-95eb-2a65ba45309e"

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
    
    