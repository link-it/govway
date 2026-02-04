Feature: Test itWallet Fonte Autentica Handler

Background:

    * def fonte_autentica_fruizione_url = govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/FonteAutenticaTestOverride/v1'
    * def fonte_autentica_erogazione_url = govway_base_path + '/rest/in/DemoSoggettoErogatore/FonteAutenticaTestOverride/v1'

    * def request_valid = read('classpath:test/itwallet_fonte_autentica/requests/request-valid.json')
    * def request_invalid = read('classpath:test/itwallet_fonte_autentica/requests/request-invalid.json')

    * def default_response = read('classpath:test/itwallet_fonte_autentica/responses/default_response.json')

    * def decodeToken = read('classpath:utils/decode-token.js')

#
# TEST HANDLER - CASO SUCCESSO
#

@test-handler
@test-handler-base
Scenario: Invocazione base dell'handler FonteAutentica - risposta corretta

    Given url fonte_autentica_fruizione_url
    And path 'AttributeClaims', '123'
    And header Content-Type = 'application/json'
    And header GovWay-Audit-User = 'user'
    And header GovWay-Audit-UserLocation = 'location'
    And header simulazionepdnd-purposeId = 'purposeId'
    And header simulazionepdnd-audience = 'TestFonteAutentica'
    And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
    And header simulazionepdnd-digest-mode = 'proxy'
    And header audience-test = 'test-audience-value'
    And header GovWay-TestSuite-Test-ID = 'handler_base'
    And request request_valid
    When method post
    Then status 200

    # Verifica che la risposta sia un JWT (application/jwt)
    And match header Content-Type contains 'application/jwt'

    # Decodifica il JWT e valida il payload
    * def jwtDecoded = decodeToken(response, 'none')
    * def payload = jwtDecoded.payload

    # Verifica claims standard JWT
    * match payload.aud == 'test-audience-value'
    * match payload.iss == '#present'
    * match payload.exp == '#number'
    * match payload.iat == '#number'
    * match payload.nbf == '#number'
    * match payload.jti == '#uuid'

    # Verifica userClaims
    * match payload.userClaims == '#present'
    * match payload.userClaims.given_name == 'Mario'
    * match payload.userClaims.family_name == 'Rossi'
    * match payload.userClaims.birth_date == '1980-01-10'
    * match payload.userClaims.birth_place == 'Roma'
    * match payload.userClaims.tax_id_code == 'RSSMRA80A10H501X'

    # Verifica attributeClaims
    * match payload.attributeClaims == '#present'
    * match payload.attributeClaims == '#[2]'

    # Verifica metadataClaims
    * match payload.metadataClaims == '#present'
    * match payload.metadataClaims == '#[2]'

#
# TEST HANDLER - ERRORI
#

@test-handler
@token_not_found
Scenario: token_not_found - Token di autorizzazione PDND non presente

    # Chiama l'erogazione SENZA header simulazionepdnd (simula assenza token PDND)
    Given url fonte_autentica_erogazione_url
    And path 'AttributeClaims', '123'
    And header Content-Type = 'application/json'
    And header GovWay-Audit-User = 'user'
    And header GovWay-Audit-UserLocation = 'location'
    And header audience-test = 'test-audience-value'
    And request request_valid
    When method post
    Then status 401
    And match header WWW-Authenticate == 'Bearer error="invalid_token", error_description="Token not present"'
    And match header Content-Type == 'text/plain'

@test-handler
@audience_not_valid
Scenario: audience_not_valid - Token PDND con audience non valida

    # Chiama la fruizione con audience PDND errata
    Given url fonte_autentica_fruizione_url
    And path 'AttributeClaims', '123'
    And header Content-Type = 'application/json'
    And header GovWay-Audit-User = 'user'
    And header GovWay-Audit-UserLocation = 'location'
    And header simulazionepdnd-purposeId = 'purposeId'
    And header simulazionepdnd-audience = 'audience-errata'
    And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
    And header simulazionepdnd-digest-mode = 'proxy'
    And header audience-test = 'test-audience-value'
    And header GovWay-TestSuite-Test-ID = 'itwallet-audience_not_valid'
    And request request_valid
    When method post
    Then status 401
    And match header WWW-Authenticate == 'Bearer error="invalid_token", error_description="Token not valid"'
    And match header Content-Type == 'text/plain'

@test-handler
@resource_not_found
Scenario: resource_not_found - Risorsa non esistente

    # Chiama l'erogazione con path inesistente
    Given url fonte_autentica_erogazione_url
    And path 'RisorsaInesistente'
    And header Content-Type = 'application/json'
    And header GovWay-Audit-User = 'user'
    And header GovWay-Audit-UserLocation = 'location'
    And header audience-test = 'test-audience-value'
    And request request_valid
    When method post
    Then status 404
    And match response == {"error_description":"Resource not found","error":"not_found"}
    And match header Content-Type == 'application/json'
