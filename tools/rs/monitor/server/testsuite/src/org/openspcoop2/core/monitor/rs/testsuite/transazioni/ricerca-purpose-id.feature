@parallel=false
Feature: Ricerca Transazioni tramite purposeId

Background: 
  * configure afterFeature = function(){ karate.call('classpath:prepare_modi_tests.feature@cleanup'); }

  * def ricerca_url = monitorUrl + '/monitoraggio/transazioni/purpose-id'
  * call read('classpath:crud_commons.feature')

  * def setup = callonce read('classpath:prepare_modi_tests.feature@prepare')
  
  * configure headers = ({ "Authorization": govwayMonitorCred }) 

  * def transazione_url = monitorUrl + '/monitoraggio/transazioni/'

  * def ricerca_token_url = monitorUrl + '/monitoraggio/transazioni/id-token'

  * def estraiErogazione =
  """
  function(items) {
    for (var i = 0; i < items.length; i++) {
      if (items[i].ruolo === 'erogazione') {
        return items[i].id_traccia;
      }
    }
    return null;
  }
  """

  * def estraiFruizione =
  """
  function(items) {
    for (var i = 0; i < items.length; i++) {
      if (items[i].ruolo === 'fruizione') {
        return items[i].id_traccia;
      }
    }
    return null;
  }
  """

@get
Scenario: 
	Given url ricerca_url
	And param purpose_id = setup.purpose_ids.id1
	When method get
	Then status 200
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
	
@erogazioneTokenId
Scenario: 
	Given url ricerca_url
	And param purpose_id = setup.purpose_ids.id3
	When method get
	Then status 200
	* match (response.items.length) == 2
	
	# Filtra il primo elemento con ruolo "erogazione"
	* def items = response.items
	* def idTraccia = estraiErogazione(items)
	* print 'ID Traccia Erogazione:', idTraccia

	# Esempio di seconda chiamata con idTraccia
	Given url transazione_url + idTraccia
	When method get
	Then status 200
	* print 'Dettaglio risposta:', response	
	
	# Verifica che il ruolo sia "erogazione"
	* match response.ruolo == 'erogazione'

	# Estrai token_id dalla struttura mittente.informazioni_token
	* def tokenId = response.mittente.informazioni_token.token_id
	* print 'Token ID estratto:', tokenId
	
	# La lista sarà 2 perchè ci sarà anche la fruizione oltre all'erogazione
	Given url ricerca_token_url
	And param id = tokenId
	When method get
	Then status 200
	* match (response.items.length) == 2
	
	# Filtra il primo elemento con ruolo "erogazione"
	* def items = response.items
	* def idTracciaByToken = estraiErogazione(items)
	* print 'ID Traccia Erogazione by Token:', idTracciaByToken
	
	# Verifica che sia la stessa traccia
	* match idTraccia == idTracciaByToken
	
	
@fruizioneTokenId
Scenario: 
	Given url ricerca_url
	And param purpose_id = setup.purpose_ids.id3
	When method get
	Then status 200
	* match (response.items.length) == 2
	
	# Filtra il primo elemento con ruolo "fruizione"
	* def items = response.items
	* def idTraccia = estraiFruizione(items)
	* print 'ID Traccia Fruizione:', idTraccia

	# Esempio di seconda chiamata con idTraccia
	Given url transazione_url + idTraccia
	When method get
	Then status 200
	* print 'Dettaglio risposta:', response	
	
	# Verifica che il ruolo sia "fruizione"
	* match response.ruolo == 'fruizione'

	# Estrai token_id dalla struttura mittente.informazioni_token
	* def tokenId = response.mittente.informazioni_token.token_id
	* print 'Token ID estratto:', tokenId
	
	# La lista sarà 2 perchè ci sarà anche la fruizione oltre alla fruizione
	Given url ricerca_token_url
	And param id = tokenId
	When method get
	Then status 200
	* match (response.items.length) == 2
	
	# Filtra il primo elemento con ruolo "fruizione"
	* def items = response.items
	* def idTracciaByToken = estraiFruizione(items)
	* print 'ID Traccia Fruizione by Token:', idTracciaByToken
	
	# Verifica che sia la stessa traccia
	* match idTraccia == idTracciaByToken	
