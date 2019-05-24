Feature: Test delle varie possibili combinazioni di valori del filtro mittente

# Dipende da ricerca-transazioni.feature, che ho fattorizzato in più feature per renderla ordinata
# e perchè gli scenario outline facevano rieseguire la prepare_test.feature.


Background:
    * url ricercaUrl

@FiltroMittenteTokenInfo
Scenario Outline: Ricerca di transazioni filtrate per claim <nome>
    * def filtro = read('classpath:bodies/ricerca-filtro-mittente-tokeninfo.json')
    * eval filtro.mittente.id.id = '<valore>'
    * eval filtro.mittente.id.claim = '<nome>'
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length == 1

    * set filtro.tipo = 'fruizione'
    Given request filtro
    When method post
    Then status 200
    And assert response.items.length == 1

    Examples:
    | setup.filtro_claims |