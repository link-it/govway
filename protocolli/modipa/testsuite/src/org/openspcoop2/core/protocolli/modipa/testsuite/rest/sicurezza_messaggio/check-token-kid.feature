Feature: Controllo Token JWT REST con solo kid

Scenario:
    * def decodeToken = read('classpath:utils/decode-token.js')

    * def kind = karate.get('kind', 'Bearer')
    * def token = decodeToken(token, kind)

    * def tok_header =
    """
    {
        'alg': 'RS256',
        'typ': 'JWT',
        'kid': '#string',
        'x5c': '#notpresent',
        'x5t#S256': '#notpresent'
    }
    """
    * def tok_payload = 
    """
    {
        'iat': '#number',
        'nbf': '#number',
        'exp': '#number',
        'jti': '#uuid',         
        'aud': '#string',
        'client_id': '#string',
        'iss': '#string',
        'sub': '#string'
    }
    """

    * def tok_header = karate.merge(tok_header, match_to.header)
    * def tok_payload = karate.merge(tok_payload, match_to.payload)
    
    * match token.header == tok_header
    * match token.payload == tok_payload
