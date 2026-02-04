Feature: Test itWallet Fonte Autentica - Dynamic Map e Valori Default

Background:

    * def fonte_autentica_override_url = govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/FonteAutenticaTestOverride/v1'
    * def fonte_autentica_error_url = govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/FonteAutenticaTestError/v1'
    * def fonte_autentica_preserve_url = govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/FonteAutenticaTestPreserve/v1'

    * def decodeToken = read('classpath:utils/decode-token.js')
    * def get_diagnostico = read('classpath:utils/get_diagnostico.js')

    # Request base
    * def request_base =
    """
    {
      "unique_id": "PLONDR23C23LKJH12",
      "object_id": "aaa"
    }
    """

#
# TEST DYNAMIC MAP - Audience da header, Issuer da tokenInfo
#

@test-dynamic-map
@test-dynamic-map-header-audience
Scenario: Dynamic Map - Audience da header custom, Issuer da ${tokenInfo:sub}

    Given url fonte_autentica_override_url
    And path 'AttributeClaims', '123'
    And header Content-Type = 'application/json'
    And header GovWay-Audit-User = 'user'
    And header GovWay-Audit-UserLocation = 'location'
    And header simulazionepdnd-purposeId = 'purposeId'
    And header simulazionepdnd-audience = 'TestFonteAutentica'
    And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
    And header simulazionepdnd-digest-mode = 'proxy'
    And header audience-test = 'override-audience-value'
    And header ttl-test = '300'
    And header issuer-test = 'override-issuer-value'
    And request request_base
    When method post
    Then status 200

    # Verifica che la risposta sia un JWT
    And match header Content-Type contains 'application/jwt'

    # Decodifica il JWT e valida il payload
    * def jwtDecoded = decodeToken(response, 'none')
    * def payload = jwtDecoded.payload

    # Verifica mappatura dinamica dei claims
    # aud valorizzato da header custom 'audience-test'
    * match payload.aud == 'override-audience-value'
    # iss configurato come ${tokenInfo:sub} quindi prende il valore dal token PDND
    * match payload.iss == 'ApplicativoBlockingJWK-CredenzialePrincipal'
    * match payload.exp == '#number'
    * match payload.iat == '#number'
    * match payload.nbf == '#number'
    * match payload.jti == '#uuid'

    # Verifica che exp > iat (TTL applicato correttamente)
    * assert payload.exp > payload.iat

#
# TEST DYNAMIC MAP - Audience da voucher clientId
#

@test-dynamic-map
@test-dynamic-map-voucher-audience
Scenario: Dynamic Map - Audience da voucher clientId (${tokenInfo:sub})

    Given url fonte_autentica_error_url
    And path 'AttributeClaims', '123'
    And header Content-Type = 'application/json'
    And header GovWay-Audit-User = 'user'
    And header GovWay-Audit-UserLocation = 'location'
    And header simulazionepdnd-purposeId = 'purposeId'
    And header simulazionepdnd-audience = 'TestFonteAutentica'
    And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
    And header simulazionepdnd-digest-mode = 'proxy'
    And header audience-test = 'error-audience-value'
    And header ttl-test = '600'
    And header issuer-test = 'error-issuer-value'
    And request request_base
    When method post
    Then status 200

    # Verifica che la risposta sia un JWT
    And match header Content-Type contains 'application/jwt'

    # Decodifica il JWT e valida il payload
    * def jwtDecoded = decodeToken(response, 'none')
    * def payload = jwtDecoded.payload

    # Verifica mappatura dinamica dei claims
    # aud configurato come voucher clientId (${tokenInfo:sub})
    * match payload.aud == 'ApplicativoBlockingJWK-CredenzialePrincipal'
    # iss valorizzato da header custom 'issuer-test'
    * match payload.iss == 'error-issuer-value'
    * match payload.exp == '#number'
    * match payload.iat == '#number'
    * match payload.nbf == '#number'
    * match payload.jti == '#uuid'

    # Verifica che exp > iat (TTL applicato correttamente)
    * assert payload.exp > payload.iat

#
# TEST DYNAMIC MAP - Audience custom statico
#

@test-dynamic-map
@test-dynamic-map-custom-audience
Scenario: Dynamic Map - Valori statici configurati nella fruizione

    # Fruizione preserve configurata con valori statici: ttl=990, aud=test-audience, iss=test-issuer
    Given url fonte_autentica_preserve_url
    And path 'AttributeClaims', '123'
    And header Content-Type = 'application/json'
    And header GovWay-Audit-User = 'user'
    And header GovWay-Audit-UserLocation = 'location'
    And header simulazionepdnd-purposeId = 'purposeId'
    And header simulazionepdnd-audience = 'TestFonteAutentica'
    And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
    And header simulazionepdnd-digest-mode = 'proxy'
    # Header inviati ma NON devono essere usati (la fruizione usa valori statici)
    And header audience-test = 'header-audience-value'
    And header ttl-test = '900'
    And header issuer-test = 'header-issuer-value'
    And request request_base
    When method post
    Then status 200

    # Verifica che la risposta sia un JWT
    And match header Content-Type contains 'application/jwt'

    # Decodifica il JWT e valida il payload
    * def jwtDecoded = decodeToken(response, 'none')
    * def payload = jwtDecoded.payload

    # Verifica valori statici configurati nella fruizione (gli header NON vengono usati)
    # aud configurato come valore statico 'test-audience' (NON 'header-audience-value')
    * match payload.aud == 'test-audience'
    # iss configurato come valore statico 'test-issuer' (NON 'header-issuer-value')
    * match payload.iss == 'test-issuer'
    * match payload.exp == '#number'
    * match payload.iat == '#number'
    * match payload.nbf == '#number'
    * match payload.jti == '#uuid'

    # Verifica che exp > iat (TTL applicato correttamente)
    * assert payload.exp > payload.iat

#
# TEST CLAIMS ESISTENTI - Policy Override
#
# Quando un claim esiste gia' nel JSON del backend, la policy Override lo sovrascrive
#

@test-claims-existing
@test-claims-existing-override
Scenario Outline: Claims Esistenti - Policy Override sovrascrive <claimName>

    Given url fonte_autentica_override_url
    And path 'AttributeClaims', '123'
    And header Content-Type = 'application/json'
    And header GovWay-Audit-User = 'user'
    And header GovWay-Audit-UserLocation = 'location'
    And header simulazionepdnd-purposeId = 'purposeId'
    And header simulazionepdnd-audience = 'TestFonteAutentica'
    And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
    And header simulazionepdnd-digest-mode = 'proxy'
    And header audience-test = 'override-audience-value'
    And header ttl-test = '300'
    And header issuer-test = 'override-issuer-value'
    # Header per far aggiungere il claim esistente al mock
    And header <mockHeader> = '<existingValue>'
    And request request_base
    When method post
    Then status 200

    And match header Content-Type contains 'application/jwt'

    * def jwtDecoded = decodeToken(response, 'none')
    * def payload = jwtDecoded.payload

    # Con policy Override, il claim viene SOVRASCRITTO
    * match payload.<claimName> == <expectedValue>
    * match payload.jti == '#uuid'
    # Verifica che exp = iat + ttl (300 secondi) quando si testa iat
    * if ('<claimName>' == 'iat') karate.match(payload.exp, payload.iat + 300)

    Examples:
    | claimName | mockHeader                    | existingValue                  | expectedValue                                 |
    | aud       | GovWay-TestSuite-Mock-Add-Aud | existing-aud-from-backend      | 'override-audience-value'                     |
    | iss       | GovWay-TestSuite-Mock-Add-Iss | existing-iss-from-backend      | 'ApplicativoBlockingJWK-CredenzialePrincipal' |
    | iat       | GovWay-TestSuite-Mock-Add-Exp | 9999999999                     | '#number'                                     |

#
# TEST CLAIMS ESISTENTI - Policy Error
#
# Quando un claim esiste gia' nel JSON del backend, la policy Error genera errore
#

@test-claims-existing
@test-claims-existing-error
Scenario Outline: Claims Esistenti - Policy Error genera errore se <claimName> esistente

    Given url fonte_autentica_error_url
    And path 'AttributeClaims', '123'
    And header Content-Type = 'application/json'
    And header GovWay-Audit-User = 'user'
    And header GovWay-Audit-UserLocation = 'location'
    And header simulazionepdnd-purposeId = 'purposeId'
    And header simulazionepdnd-audience = 'TestFonteAutentica'
    And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
    And header simulazionepdnd-digest-mode = 'proxy'
    And header audience-test = 'error-audience-value'
    And header ttl-test = '600'
    And header issuer-test = 'error-issuer-value'
    # Header per far aggiungere il claim esistente al mock
    And header <mockHeader> = '<existingValue>'
    And request request_base
    When method post
    Then status 500

    And match response == {"error_description":"Processing error","error":"server_error"}

    # Verifica messaggio diagnostico specifico per il claim
    * def id_transazione = responseHeaders['GovWay-Peer-Transaction-ID'][0]
    * def msgs = get_diagnostico(id_transazione, "Il claim %<claimName>% è già presente nella risposta")
    * if (msgs.length != 1) karate.fail("Messaggio diagnostico per claim '<claimName>' non trovato")

    Examples:
    | claimName | mockHeader                    | existingValue             |
    | aud       | GovWay-TestSuite-Mock-Add-Aud | existing-aud-from-backend |
    | iss       | GovWay-TestSuite-Mock-Add-Iss | existing-iss-from-backend |
    | iat       | GovWay-TestSuite-Mock-Add-Exp | 9999999999                |

#
# TEST CLAIMS ESISTENTI - Policy Preserve
#
# Quando un claim esiste gia' nel JSON del backend, la policy Preserve lo mantiene
# Fruizione preserve configurata con: ttl=990, aud=test-audience, iss=test-issuer
#

@test-claims-existing
@test-claims-existing-preserve
Scenario Outline: Claims Esistenti - Policy Preserve mantiene <claimName>

    Given url fonte_autentica_preserve_url
    And path 'AttributeClaims', '123'
    And header Content-Type = 'application/json'
    And header GovWay-Audit-User = 'user'
    And header GovWay-Audit-UserLocation = 'location'
    And header simulazionepdnd-purposeId = 'purposeId'
    And header simulazionepdnd-audience = 'TestFonteAutentica'
    And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
    And header simulazionepdnd-digest-mode = 'proxy'
    # Header che NON devono sovrascrivere il claim esistente con policy preserve
    And header audience-test = 'header-audience-value'
    And header ttl-test = '900'
    And header issuer-test = 'header-issuer-value'
    # Header per far aggiungere il claim esistente al mock
    And header <mockHeader> = '<existingValue>'
    And request request_base
    When method post
    Then status 200

    And match header Content-Type contains 'application/jwt'

    * def jwtDecoded = decodeToken(response, 'none')
    * def payload = jwtDecoded.payload

    # Con policy Preserve, il claim originale viene MANTENUTO
    * match payload.<claimName> == <expectedValue>
    * match payload.jti == '#uuid'

    Examples:
    | claimName | mockHeader                    | existingValue             | expectedValue               |
    | aud       | GovWay-TestSuite-Mock-Add-Aud | existing-aud-from-backend | 'existing-aud-from-backend' |
    | iss       | GovWay-TestSuite-Mock-Add-Iss | existing-iss-from-backend | 'existing-iss-from-backend' |
    | iat       | GovWay-TestSuite-Mock-Add-Exp | 9999999999                | 9999999699                  |

#
# TEST CODICI HTTP - Verifica firma JWT in base al codice di risposta del backend
#
# Configurazione fruizione override: firma JWT per codici "200,300-400,-100,600-"
# - 200: JWT
# - 300-400: JWT
# - -100 (0-100): JWT
# - 600-: JWT
# Altri codici: risposta non firmata
#

@test-http-codes
Scenario Outline: HTTP Codes - Codice <httpCode> firmato=<isJwt>

    Given url fonte_autentica_override_url
    And path 'AttributeClaims', '123'
    And header Content-Type = 'application/json'
    And header GovWay-Audit-User = 'user'
    And header GovWay-Audit-UserLocation = 'location'
    And header simulazionepdnd-purposeId = 'purposeId'
    And header simulazionepdnd-audience = 'TestFonteAutentica'
    And header simulazionepdnd-username = 'ApplicativoBlockingJWK'
    And header simulazionepdnd-digest-mode = 'proxy'
    And header audience-test = 'test-audience'
    And header ttl-test = '300'
    And header issuer-test = 'test-issuer'
    # Header per impostare il codice di stato del mock (ritorna errore RFC JSON)
    And header GovWay-TestSuite-Mock-Status-Code = '<httpCode>'
    And request request_base
    When method post
    Then status <httpCode>

    # Verifica se la risposta e' JWT o JSON in base alla configurazione
    * def isJwt = <isJwt>
    * if (isJwt) karate.match("header Content-Type contains 'application/jwt'")
    * if (!isJwt) karate.match("header Content-Type contains 'application/json'")

    # Se JWT, verifica la struttura a 3 parti
    * if (isJwt) { var jwtParts = response.split('.'); karate.match(jwtParts.length, 3) }

    # Configurazione: "200,300-400,-100,600-"
    # Solo codici HTTP standard funzionanti
    Examples:
    | httpCode | isJwt |
    | 200      | true  |
    | 201      | false |
    | 300      | true  |
    | 400      | true  |
    | 401      | false |
    | 500      | false |
