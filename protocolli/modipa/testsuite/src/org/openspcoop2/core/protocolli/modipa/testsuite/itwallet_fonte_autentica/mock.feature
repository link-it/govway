Feature: Mock server per i test itWallet Fonte Autentica

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
        var headerArray = (karate.get("requestHeaders['" + name + "']") ||
               karate.get("requestHeaders['" + name.toLowerCase() + "']"))
        if (headerArray == null)
            return null;
        return headerArray[0];
    }
    """

    # Funzione per costruire la risposta dinamicamente in base agli header
    * def buildResponse =
    """
    function() {
        var response = {
          "userClaims": {
            "given_name": "Mario",
            "family_name": "Rossi",
            "birth_date": "1980-01-10",
            "birth_place": "Roma",
            "tax_id_code": "RSSMRA80A10H501X"
          },
          "attributeClaims": [
            {
              "object_id": "6F9619FF-8B86-D011-B42D-00C04FC964FF",
              "status": "VALID",
              "last_updated": "2025-12-31T23:59:59Z",
              "nationality": "IT",
              "other": "value"
            },
            {
              "object_id": "7A123456-1234-5678-9ABC-00C04FC99999",
              "status": "SUSPENDED",
              "last_updated": "2025-06-15T10:30:00Z",
              "residence_status": "FOREIGN"
            }
          ],
          "metadataClaims": [
            {
              "object_id": "6F9619FF-8B86-D011-B42D-00C04FC964FF",
              "issuance_date": "2025-01-01",
              "expiry_date": "2025-12-31"
            },
            {
              "object_id": "7A123456-1234-5678-9ABC-00C04FC99999",
              "issuance_date": "2024-01-01",
              "expiry_date": "2026-12-31"
            }
          ]
        };

        // Aggiungi claims JWT se richiesto dagli header
        var addAud = karate.get("requestHeaders['GovWay-TestSuite-Mock-Add-Aud']");
        var addIss = karate.get("requestHeaders['GovWay-TestSuite-Mock-Add-Iss']");
        var addExp = karate.get("requestHeaders['GovWay-TestSuite-Mock-Add-Exp']");

        if (addAud != null) {
            response.aud = addAud[0];
        }
        if (addIss != null) {
            response.iss = addIss[0];
        }
        if (addExp != null) {
            response.exp = parseInt(addExp[0]);
            response.iat = parseInt(addExp[0]) - 300;
            response.nbf = parseInt(addExp[0]) - 300;
        }

        return response;
    }
    """

    * def defaultResponse =
    """
    {
      "userClaims": {
        "given_name": "Mario",
        "family_name": "Rossi",
        "birth_date": "1980-01-10",
        "birth_place": "Roma",
        "tax_id_code": "RSSMRA80A10H501X"
      },
      "attributeClaims": [
        {
          "object_id": "6F9619FF-8B86-D011-B42D-00C04FC964FF",
          "status": "VALID",
          "last_updated": "2025-12-31T23:59:59Z",
          "nationality": "IT",
          "other": "value"
        },
        {
          "object_id": "7A123456-1234-5678-9ABC-00C04FC99999",
          "status": "SUSPENDED",
          "last_updated": "2025-06-15T10:30:00Z",
          "residence_status": "FOREIGN"
        }
      ],
      "metadataClaims": [
        {
          "object_id": "6F9619FF-8B86-D011-B42D-00C04FC964FF",
          "issuance_date": "2025-01-01",
          "expiry_date": "2025-12-31"
        },
        {
          "object_id": "7A123456-1234-5678-9ABC-00C04FC99999",
          "issuance_date": "2024-01-01",
          "expiry_date": "2026-12-31"
        }
      ]
    }
    """

#
# SCENARIO: Default - costruisce la risposta dinamicamente in base agli header
#
# Header supportati:
#   - GovWay-TestSuite-Mock-Add-Aud: aggiunge claim "aud" con il valore specificato
#   - GovWay-TestSuite-Mock-Add-Iss: aggiunge claim "iss" con il valore specificato
#   - GovWay-TestSuite-Mock-Add-Exp: aggiunge claims "exp", "iat", "nbf" con i valori calcolati
#   - GovWay-TestSuite-Mock-Status-Code: imposta il codice di stato HTTP della risposta
#
Scenario:

    * karate.log('Mock: costruzione risposta dinamica')
    * karate.log('Headers ricevuti:', requestHeaders)
    * def statusCodeHeader = getHeader('GovWay-TestSuite-Mock-Status-Code')
    * def responseStatus = statusCodeHeader != null ? parseInt(statusCodeHeader) : 200
    # Se e' specificato un codice di stato, ritorna un errore RFC, altrimenti la risposta normale
    * def response = statusCodeHeader != null ? { "error": "server_error", "error_description": "Mock error with code " + statusCodeHeader } : buildResponse()
    * def responseHeaders = { 'Content-Type': 'application/json', 'GovWay-TestSuite-Mock-Called': 'true' }
