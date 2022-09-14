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


@ReportBaseTag
Scenario Outline: Ricerca di report base statistica <nome-statistica> filtrati per tag 

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
    "esito": {
      "tipo": "ok"
    } 
})
"""  
* def query =
"""
({
    data_inizio: filtro.intervallo_temporale.data_inizio,
    data_fine: filtro.intervallo_temporale.data_fine,
    tipo: filtro.tipo,
    tag: filtro.tag,
    esito: <filtro-esito>,
    formato_report: filtro.report.formato,
    unita_tempo: filtro.unita_tempo,
    tipo_report: filtro.report.tipo,
    tipo_informazione_report: filtro.report.tipo_informazione.tipo,
    claim: <filtro-claim>,
    tipo_identificazione: <filtro-tipo-identificazione-applicativo>
})
"""
		Given path <path-distribuzione>
    And params query
    When method get
    Then status 200
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-identificazione-applicativo | 
| 'distribuzione-temporale' | 'distribuzione-temporale' | filtro.esito.tipo | null | null | 
| 'distribuzione-esiti' | 'distribuzione-esiti' | null | null | null | 
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo | null | null | 
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo | null | null | 
| 'distribuzione-api' | 'distribuzione-api' | filtro.esito.tipo | null | null |
| 'distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo | null | null |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'trasporto' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'token' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo | null | null |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | filtro.esito.tipo | 'subject' | null | 
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id' | null | 
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo | null | null |

@ReportFullTag
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per tag 

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
    } 
})
"""  

* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
* eval if(<filtro-claim> != null) filtro.claim = <filtro-claim>
* eval if(<filtro-tipo-identificazione-applicativo> != null) filtro.tipo_identificazione_applicativo = <filtro-tipo-identificazione-applicativo>
    
    Given path <path-distribuzione>
    And request filtro
    When method post
    Then status 200
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-identificazione-applicativo |
| 'distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null | null |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null | null | null |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | null |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | null |
| 'distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | null |
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | null |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'trasporto' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'token' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | null |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | null |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id" | null |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | null |


