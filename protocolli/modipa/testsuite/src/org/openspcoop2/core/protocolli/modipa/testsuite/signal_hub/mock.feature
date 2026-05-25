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
    
    * def getHeader = 
    """
    function(name) {
        headerArray = (karate.get("requestHeaders['" + name + "']") ||
               karate.get("requestHeaders['" + name.toLowerCase() + "']"))   
        if (headerArray == null)
        	return null;
        return headerArray[0];
    }
    """

	* def compute_digest = read('classpath:utils/compute_digest.js')
    * def task_id = "fb382380-cf98-4f75-95eb-2a65ba45309e"
    * def get_seed = read('classpath:test/signal_hub/utils/get_seed.js')

# GIRO OK
#
#

Scenario: (isTest('push_signal') || isTest('push_signal_no_seed')) && request.signalType != 'SEEDUPDATE'

	# I default sotto valgono per i test REST su SignalHubTest; per altri servizi/algoritmi
	# il testsuite client puo' sovrascrivere via header.
	* def expectedEServiceId = getHeader('GovWay-TestSuite-EService-Id') || 'eServiceTestID'
	* def expectedAlgorithm = getHeader('GovWay-TestSuite-Algorithm') || 'SHA-256'
	* def expectedSeedService = getHeader('GovWay-TestSuite-Service-Name-Seed') || 'SignalHubTest'

	* match request ==
		"""
		{
			signalId: #number,
			objectType : 'objectType',
  			objectId : '#string',
  			eserviceId : '#(expectedEServiceId)',
  			signalType : 'UPDATE',
		}
		"""
	* def objectId = getHeader('GovWay-TestSuite-Plain-Object-ID')
	* def seedValue = get_seed('DemoSoggettoErogatore', expectedSeedService)
	* def hash = compute_digest(expectedAlgorithm, objectId + seedValue)
	* karate.log("algorithm: " + expectedAlgorithm + ", plain id: " + objectId + ", seed: " + seedValue)
	* match hash == request.objectId
    * def sigId = request.signalId
    * def responseStatus = 200
    * def response = ({ signalId: sigId })
    * def responseHeaders = ({ 'GovWay-TestSuite-Signal-Sent': 1})

Scenario: (isTest('push_signal') || isTest('push_signal_no_seed')) && request.signalType == 'SEEDUPDATE'

	* if (isTest('push_signal_no_seed')) karate.fail('Il primo push segnale non dovrebbe inviare una SEEDUPDATE')

	# Permette di sovrascrivere l'eserviceId atteso via header (default: eServiceTestID per i test REST)
	* def expectedEServiceId = getHeader('GovWay-TestSuite-EService-Id') || 'eServiceTestID'

	* match request ==
		"""
		{
			signalId: #number,
			objectType : '-',
  			objectId : '-',
  			eserviceId : '#(expectedEServiceId)',
  			signalType : 'SEEDUPDATE',
		}
		"""
    * def sigId = request.signalId
    * def responseStatus = 200
    * def responseHeaders = ({ 'GovWay-TestSuite-Seed-Sent': 1})
    * def response = ({ signalId: sigId })

Scenario: (isTest('seed_failing') || isTest('seed_failing_no_seed')) && request.signalType != 'SEEDUPDATE'

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
    * def responseHeaders = ({ 'GovWay-TestSuite-Signal-Sent': 1})
    * def response = ({ signalId: sigId })
        
Scenario: (isTest('seed_failing') || isTest('seed_failing_no_seed')) && request.signalType == 'SEEDUPDATE'

	* if (isTest('seed_failing_no_seed')) karate.fail('Il primo push segnale non dovrebbe inviare una SEEDUPDATE')

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
    * def responseHeaders = ({ 'GovWay-TestSuite-Seed-Sent': 1})
    * def response = ({ signalId: sigId })

Scenario: isTest('push_multiple_id')

	* match request ==
		"""
		{
			signalId: #number,
			objectType : 'objectType',
  			objectId : '#string',
  			eserviceId : 'eServiceMultiple',
  			signalType : 'UPDATE',
		}
		"""
	* def objectId = getHeader('GovWay-TestSuite-Plain-Object-ID')
	* def shouldHash = getHeader('GovWay-TestSuite-Hash')
	* def hash = shouldHash == 'true' ? compute_digest('SHA-256', objectId + get_seed('DemoSoggettoErogatore','SignalHubTestYesDigest')) : objectId
	* karate.log("shouldHash: " + shouldHash + ", plain id: " + objectId + ", hash: " + hash)
	* match hash == request.objectId
    * def sigId = request.signalId
    * def responseStatus = 200
    * def response = ({ signalId: sigId })
    * def responseHeaders = ({ 'GovWay-TestSuite-Signal-Sent': 1})
    
    
