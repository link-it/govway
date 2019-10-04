@parallel=false
Feature: Reportistica e esportazione grafici

Background:

    * call read('classpath:crud_commons.feature')
    * def setup = callonce read('classpath:prepare_tests.feature')
    * configure afterFeature = function(){ karate.call('classpath:cleanup_tests.feature'); }    

    # Rimuovo la lock e do il tempo all'engine di preparare le statistiche.
    * print 'Rimuovo La lock sul db..'
    * callonce setup.delete_lock(setup.db)
    # TODO: Ora che dedichiamo un applicative_id a ciascun intervallo  orario in op2_semaphore, potremmo invece che attendere indefinitamente,
    # rilevare che questa lock sia stata presa e rilasciata almeno una volta.

    * print 'Attendo che vengano generate le statistiche'
    * callonce pause(statsInterval)
    * print 'Attendo di ottenere la lock'
    * callonce setup.wait_for_lock(setup.db);    

    * def reportisticaUrl = monitorUrl + '/reportistica/analisi-statistica'
    
    * def intervallo_temporale = ({ data_inizio: setup.dataInizioMinuteZero, data_fine: getDate() })

    * url reportisticaUrl
    * configure headers = ({ "Authorization": govwayMonitorCred }) 



@ReportBaseTipoQualsiasi
Scenario Outline: Ricerca di report base statistica <nome-statistica> filtrati per tipo = qualsiasi 

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'qualsiasi',
    report: {
      "formato": "csv",
      "tipo": "table",
      "tipo_informazione": {
        "tipo": "numero_transazioni"
      }
    },
    "esito": {
      "tipo": "ok"
    },
    "claim" : "subject" 
})
"""  
* def query =
"""
({
    data_inizio: filtro.intervallo_temporale.data_inizio,
    data_fine: filtro.intervallo_temporale.data_fine,
    tipo: filtro.tipo,
    esito: <filtro-esito>,
    formato_report: filtro.report.formato,
    unita_tempo: filtro.unita_tempo,
    tipo_report: filtro.report.tipo,
    tipo_informazione_report: filtro.report.tipo_informazione.tipo,
    claim: <filtro-claim>
})
"""
		Given path <path-distribuzione>
    And params query
    When method get
    Then status 200
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | 
| 'distribuzione-temporale' | 'distribuzione-temporale' | filtro.esito.tipo |  null | 
| 'distribuzione-esiti' | 'distribuzione-esiti' | null |  null | 
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo |  null | 
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo |  null | 
| 'distribuzione-api' | 'distribuzione-api' | filtro.esito.tipo |  null | 
| 'distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo |  null | 
| 'distribuzione-applicativo' | 'distribuzione-applicativo' | filtro.esito.tipo |  null | 
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo |  null | 
| 'distribuzione-token-info' | 'distribuzione-token-info' | filtro.esito.tipo | filtro.claim | 
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo |  null | 

@ReportFullTipoQualsiasi
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per tipo = qualsiasi  

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'qualsiasi',
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
    
    Given path <path-distribuzione>
    And request filtro
    When method post
    Then status 200
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | 
| 'distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null | null |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null |
| 'distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null |
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null |
| 'distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null |
| 'distribuzione-token-info' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null |


@ReportBaseTipoFruizione
Scenario Outline: Ricerca di report base statistica <nome-statistica> filtrati per tipo = fruizione 

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'fruizione',
    report: {
      "formato": "csv",
      "tipo": "table",
      "tipo_informazione": {
        "tipo": "numero_transazioni"
      }
    },
    "esito": {
      "tipo": "ok"
    },
    "claim" : "subject" 
})
"""  
* def query =
"""
({
    data_inizio: filtro.intervallo_temporale.data_inizio,
    data_fine: filtro.intervallo_temporale.data_fine,
    tipo: filtro.tipo,
    esito: <filtro-esito>,
    formato_report: filtro.report.formato,
    unita_tempo: filtro.unita_tempo,
    tipo_report: filtro.report.tipo,
    tipo_informazione_report: filtro.report.tipo_informazione.tipo,
    claim: <filtro-claim>
})
"""
		Given path <path-distribuzione>
    And params query
    When method get
    Then status 200
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | 
| 'distribuzione-temporale' | 'distribuzione-temporale' | filtro.esito.tipo |  null | 
| 'distribuzione-esiti' | 'distribuzione-esiti' | null |  null | 
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo |  null | 
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo |  null | 
| 'distribuzione-api' | 'distribuzione-api' | filtro.esito.tipo |  null | 
| 'distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo |  null | 
| 'distribuzione-applicativo' | 'distribuzione-applicativo' | filtro.esito.tipo |  null | 
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo |  null | 
| 'distribuzione-token-info' | 'distribuzione-token-info' | filtro.esito.tipo | filtro.claim | 
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo |  null | 

@ReportFullTipoFruizione
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per tipo = fruizione  

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'fruizione',
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
    
    Given path <path-distribuzione>
    And request filtro
    When method post
    Then status 200
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | 
| 'distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null | null |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null |
| 'distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null |
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null |
| 'distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null |
| 'distribuzione-token-info' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null |

@ReportFullTipoCheckTotali
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per tipo

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: <filtro-tipo-qualsiasi>,
    report: {
      "formato": "json",
      "tipo": "table",
      "tipo_informazione": {
        "tipo": "numero_transazioni"
      }
    } 
})
"""  

* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
* eval if(<filtro-claim> != null) filtro.claim = <filtro-claim>
    
	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	* def response_qualsiasi = response
	
	* eval filtro.tipo = <filtro-tipo-fruizione>
	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	* def response_fruizione = response
	
	* eval filtro.tipo = <filtro-tipo-erogazione>
	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	* def response_erogazione = response
    
  * def sommaQualsiasi = sommaRisultatiGraficoJSON(response_qualsiasi.dati)
  * def sommaFruizione = sommaRisultatiGraficoJSON(response_fruizione.dati)
  * def sommaErogazione = sommaRisultatiGraficoJSON(response_erogazione.dati)
  * match  sommaQualsiasi == ( sommaFruizione + sommaErogazione)
    
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-qualsiasi | filtro-tipo-fruizione | filtro-tipo-erogazione |
| 'distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' |
| 'distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' |
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' |
| 'distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' |
| 'distribuzione-token-info' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' |



