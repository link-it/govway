Feature: Test delle varie possibili combinazioni di valori del filtro mittente

# Dipende da ricerca-transazioni.feature, che ho fattorizzato in più feature per renderla ordinata
# e perchè gli scenario outline facevano rieseguire la prepare_test.feature.


Background:
    * url ricercaUrl

@FiltroMittenteTokenInfoFruizione
Scenario Outline: Ricerca di transazioni filtrate per claim <nome> (scenario: fruizione)
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-tokeninfo.json')
    * eval filtro.mittente.id = '<valore>'
    * eval filtro.mittente.claim = '<nome>'
    * set filtro.tipo = 'fruizione'

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length == 3

    Examples:
    | setup.filtro_claims_fruizione |

@FiltroMittenteTokenInfoErogazione
Scenario Outline: Ricerca di transazioni filtrate per claim <nome> (scenario: erogazione)
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-tokeninfo.json')
    * eval filtro.mittente.id = '<valore>'
    * eval filtro.mittente.claim = '<nome>'

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length == 3

    Examples:
    | setup.filtro_claims_erogazione |

@FiltroMittenteTokenInfoErogazionePDND
Scenario Outline: Ricerca di transazioni filtrate per claim <nome> (scenario: erogazione, info PDND recuperate via API)
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-tokeninfo.json')
    * eval filtro.mittente.id = '<valore>'
    * eval filtro.mittente.claim = '<nome>'
    * eval filtro.mittente.ricerca_esatta = false

    Given request filtro
    When method post
    Then status 200
    And assert response.items.length == 3

    Examples:
    | setup.filtro_claims_pdnd |
