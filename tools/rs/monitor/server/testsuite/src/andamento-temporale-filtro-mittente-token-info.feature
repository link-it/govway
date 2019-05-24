Feature: Analisi statistica per transazioni filtrate per le informazioni sul Token del Mittente

Background:
    * url reportisticaUrl

@DistribuzioneTemporaleFiltroMittenteTokenInfo
Scenario Outline: Statistiche Per Distribuzione Temporale con filtraggio per Token claim <nome>
    * def filtro = read('classpath:bodies/reportistica-andamento-temporale-filtro-mittente-token-info.json')
    * eval filtro.mittente.id.id = '<valore>'
    * eval filtro.mittente.id.claim = '<nome>'

    Given path 'distribuzione-temporale'
    And request filtro
    When method post
    Then status 200

    * set filtro.tipo = 'fruizione'
    Given path 'distribuzione-temporale'
    And request filtro
    When method post
    Then status 200

    Examples:
    | setup.filtro_claims |
