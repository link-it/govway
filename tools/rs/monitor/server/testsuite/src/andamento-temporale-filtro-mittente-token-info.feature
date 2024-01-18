Feature: Analisi statistica per transazioni filtrate per le informazioni sul Token del Mittente

Background:
    * url reportisticaUrl

@DistribuzioneTemporaleFiltroMittenteTokenInfoFruizione
Scenario Outline: Statistiche Per Distribuzione Temporale con filtraggio per Token claim <nome> (scenario: fruizione)
    * def filtro = read('classpath:bodies/reportistica-andamento-temporale-filtro-mittente-token-info.json')
    * eval filtro.mittente.id = '<valore>'
    * eval filtro.mittente.claim = '<nome>'
    * set filtro.tipo = 'fruizione'

    Given path 'distribuzione-temporale'
    And request filtro
    When method post
    Then status 200

    Examples:
    | setup.filtro_claims_fruizione |

@DistribuzioneTemporaleFiltroMittenteTokenInfoErogazione
Scenario Outline: Statistiche Per Distribuzione Temporale con filtraggio per Token claim <nome> (scenario: erogazione)
    * def filtro = read('classpath:bodies/reportistica-andamento-temporale-filtro-mittente-token-info.json')
    * eval filtro.mittente.id = '<valore>'
    * eval filtro.mittente.claim = '<nome>'

    Given path 'distribuzione-temporale'
    And request filtro
    When method post
    Then status 200

    Examples:
    | setup.filtro_claims_erogazione |

@DistribuzioneTemporaleFiltroMittenteTokenInfoErogazionePDND
Scenario Outline: Statistiche Per Distribuzione Temporale con filtraggio per Token claim <nome> (scenario: erogazione, info PDND recuperate via API)
    * def filtro = read('classpath:bodies/reportistica-andamento-temporale-filtro-mittente-token-info.json')
    * eval filtro.mittente.id = '<valore>'
    * eval filtro.mittente.claim = '<nome>'
    * eval filtro.mittente.ricerca_esatta = false

    Given path 'distribuzione-temporale'
    And request filtro
    When method post
    Then status 200

    Examples:
    | setup.filtro_claims_pdnd |
