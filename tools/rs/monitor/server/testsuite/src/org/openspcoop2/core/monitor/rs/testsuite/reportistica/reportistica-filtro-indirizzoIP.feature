@parallel=false
Feature: Reportistica e esportazione grafici

Background:

    * call read('classpath:crud_commons.feature')
    * def setup = callonce read('classpath:prepare_tests.feature')
    * configure afterFeature = function(){ karate.call('classpath:cleanup_tests.feature'); }    

    * callonce read('classpath:lock_db.feature')  

    * def reportisticaUrl = monitorUrl + '/reportistica/analisi-statistica'
    
    * def intervallo_temporale = ({ data_inizio: setup.dataInizioMinuteZero, data_fine: getDate() })

    * url reportisticaUrl
    * configure headers = ({ "Authorization": govwayMonitorCred }) 


@ReportFullIndirizzoIP
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per indirizzo IP 

* def tag = 'TESTSUITE'
* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'erogazione',
		tag: tag,
    report: {
      "formato": "csv",
      "tipo": "table",
      "tipo_informazione": {
        "tipo": "numero_transazioni"
      }
    },
    "mittente": {
			"identificazione": "indirizzo_ip",
			"ricerca_esatta": true,
			"case_sensitive": true,
			"id": "127.0.0.1",
			"tipo": "client_ip"
		}
})
"""  

* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
    
    Given path <path-distribuzione>
    And request filtro
    When method post
    Then status 200
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito |
| 'distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } |
| 'distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } |
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } |


@ReportFullXForwardedFor
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per indirizzo client inoltrato

* def tag = 'TESTSUITE'
* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'erogazione',
		tag: tag,
    report: {
      "formato": "csv",
      "tipo": "table",
      "tipo_informazione": {
        "tipo": "numero_transazioni"
      }
    },
    "mittente": {
			"identificazione": "indirizzo_ip",
			"ricerca_esatta": true,
			"case_sensitive": true,
			"id": "127.0.0.2",
			"tipo": "x_forwarded_for"
		}
})
"""  

* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
    
    Given path <path-distribuzione>
    And request filtro
    When method post
    Then status 200
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito |
| 'distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } |
| 'distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } |
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } |

