@parallel=false
Feature: Reportistica e esportazione grafici con filtro su api

Background:

    * call read('classpath:crud_commons.feature')
    * def setup = callonce read('classpath:prepare_tests.feature')
    * configure afterFeature = function(){ karate.call('classpath:cleanup_tests.feature'); }    

    * callonce read('classpath:lock_db.feature') 

    * def reportisticaUrl = monitorUrl + '/reportistica/analisi-statistica'
    
    * def intervallo_temporale = ({ data_inizio: setup.dataInizioMinuteZero, data_fine: getDate() })

    * url reportisticaUrl
    * configure headers = ({ "Authorization": govwayMonitorCred }) 


@ReportBaseApiImplementata
Scenario Outline: Ricerca di report base statistica <nome-statistica> filtrati per api implementata 

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'erogazione',
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
    uri_api_implementata: <uri-api>,
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
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-identificazione-applicativo | uri-api |
| 'distribuzione-temporale' | 'distribuzione-temporale' | filtro.esito.tipo | null | null | setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null | null | null | setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo | null | null | setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo | null | null | setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione |
| 'distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo | null | null | setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'trasporto' | setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'token' | setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo | null | null | setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | filtro.esito.tipo | 'subject' | null | setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id' | null | setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo | null | null | setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 


@ReportBaseApiImplementataNotFound
Scenario Outline: Ricerca di report base statistica <nome-statistica> filtrati per api implementata 

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'erogazione',
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
    uri_api_implementata: <uri-api>,
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
    Then status 404
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-identificazione-applicativo | uri-api |
| 'distribuzione-temporale' | 'distribuzione-temporale' | filtro.esito.tipo | null | null | setup.erogazione_petstore.api_nome+'errato:'+setup.erogazione_petstore.api_versione |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null | null | null | setup.erogazione_petstore.api_nome+'errato:'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo | null | null | setup.erogazione_petstore.api_nome+'errato:'+setup.erogazione_petstore.api_versione |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo | null | null | setup.erogazione_petstore.api_nome+'errato:'+setup.erogazione_petstore.api_versione |
| 'distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo | null | null | setup.erogazione_petstore.api_nome+'errato:'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'trasporto' | setup.erogazione_petstore.api_nome+'errato:'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'token' | setup.erogazione_petstore.api_nome+'errato:'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo | null | null | setup.erogazione_petstore.api_nome+'errato:'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | filtro.esito.tipo | 'subject' | null | setup.erogazione_petstore.api_nome+'errato:'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id' | null | setup.erogazione_petstore.api_nome+'errato:'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo | null | null | setup.erogazione_petstore.api_nome+'errato:'+setup.erogazione_petstore.api_versione | 


@ReportBaseApiImplementataConReferente
Scenario Outline: Ricerca di report base statistica <nome-statistica> filtrati per api implementata 

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'erogazione',
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
    uri_api_implementata: <uri-api>,
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
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-identificazione-applicativo | uri-api |
| 'distribuzione-temporale' | 'distribuzione-temporale' | filtro.esito.tipo | null | null | 'gw/'+soggettoDefault+':'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null | null | null | 'gw/'+soggettoDefault+':'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo | null | null | 'gw/'+soggettoDefault+':'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo | null | null | 'gw/'+soggettoDefault+':'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione |
| 'distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo | null | null | 'gw/'+soggettoDefault+':'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'trasporto' | 'gw/'+soggettoDefault+':'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'token' | 'gw/'+soggettoDefault+':'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo | null | null | 'gw/'+soggettoDefault+':'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | filtro.esito.tipo | 'subject' | null | 'gw/'+soggettoDefault+':'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id' | null | 'gw/'+soggettoDefault+':'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo | null | null | 'gw/'+soggettoDefault+':'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 

@ReportBaseApiImplementataConReferenteNotFound
Scenario Outline: Ricerca di report base statistica <nome-statistica> filtrati per api implementata 

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'erogazione',
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
    uri_api_implementata: <uri-api>,
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
    Then status 404
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-identificazione-applicativo | uri-api |
| 'distribuzione-temporale' | 'distribuzione-temporale' | filtro.esito.tipo | null | null | 'gw/altroSoggetto:'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null | null | null | 'gw/altroSoggetto:'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo | null | null | 'gw/altroSoggetto:'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo | null | null | 'gw/altroSoggetto:'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione |
| 'distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo | null | null | 'gw/altroSoggetto:'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'trasporto' | 'gw/altroSoggetto:'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'token' | 'gw/altroSoggetto:'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo | null | null | 'gw/altroSoggetto:'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | filtro.esito.tipo | 'subject' | null | 'gw/altroSoggetto:'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id' | null | 'gw/altroSoggetto:'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo | null | null | 'gw/altroSoggetto:'+setup.erogazione_petstore.api_nome+':'+setup.erogazione_petstore.api_versione | 


@ReportFullApiImplementata
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per api implementata 

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'erogazione',
     api: {
	api_implementata: {
		nome: 'nome',
		versione: 1
	}
    },
    report: {
      "formato": "csv",
      "tipo": "table",
      "tipo_informazione": {
        "tipo": "numero_transazioni"
      }
    } 
})
"""  

* eval filtro.api.api_implementata.nome = setup.erogazione_petstore.api_nome
* eval filtro.api.api_implementata.versione = setup.erogazione_petstore.api_versione
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
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | null |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'trasporto' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'token' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | null |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | null |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id" | null |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | null |


@ReportFullApiImplementataNotFound
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per api implementata  

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'erogazione',
     api: {
	api_implementata: {
		nome: 'nome',
		versione: 1
	}
    },
    report: {
      "formato": "csv",
      "tipo": "table",
      "tipo_informazione": {
        "tipo": "numero_transazioni"
      }
    } 
})
"""  

* eval filtro.api.api_implementata.nome = setup.erogazione_petstore.api_nome+'errata'
* eval filtro.api.api_implementata.versione = setup.erogazione_petstore.api_versione
* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
* eval if(<filtro-claim> != null) filtro.claim = <filtro-claim>
* eval if(<filtro-tipo-identificazione-applicativo> != null) filtro.tipo_identificazione_applicativo = <filtro-tipo-identificazione-applicativo>
    
Given path <path-distribuzione>
    And request filtro
    When method post
    Then status 404
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-identificazione-applicativo | 
| 'distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null | null |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null | null | null |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | null |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | null |
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | null |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'trasporto' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'token' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | null |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | null |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id" | null |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | null |


@ReportFullApiImplementataConReferente
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per api implementata  

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'erogazione',
     api: {
	api_implementata: {
		referente: 'referente',
		nome: 'nome',
		versione: 1
	}
    },
    report: {
      "formato": "csv",
      "tipo": "table",
      "tipo_informazione": {
        "tipo": "numero_transazioni"
      }
    } 
})
"""  

* eval filtro.api.api_implementata.referente = soggettoDefault
* eval filtro.api.api_implementata.nome = setup.erogazione_petstore.api_nome
* eval filtro.api.api_implementata.versione = setup.erogazione_petstore.api_versione
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
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | null |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'trasporto' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'token' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | null |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | null |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id" | null |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | null |


@ReportFullApiImplementataConReferenteNotFound
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per api implementata  

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'erogazione',
     api: {
	api_implementata: {
		referente: 'referente',
		nome: 'nome',
		versione: 1
	}
    },
    report: {
      "formato": "csv",
      "tipo": "table",
      "tipo_informazione": {
        "tipo": "numero_transazioni"
      }
    } 
})
"""  

* eval filtro.api.api_implementata.referente = 'altroSoggetto'
* eval filtro.api.api_implementata.nome = setup.erogazione_petstore.api_nome+'errata'
* eval filtro.api.api_implementata.versione = setup.erogazione_petstore.api_versione
* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
* eval if(<filtro-claim> != null) filtro.claim = <filtro-claim>
* eval if(<filtro-tipo-identificazione-applicativo> != null) filtro.tipo_identificazione_applicativo = <filtro-tipo-identificazione-applicativo>
    
Given path <path-distribuzione>
    And request filtro
    When method post
    Then status 404
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-identificazione-applicativo | 
| 'distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null | null |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null | null | null |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | null |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | null |
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | null |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'trasporto' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'token' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | null |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | null |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id" | null |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | null |




@ReportBaseDistinguiApiImplementata
Scenario Outline: Ricerca di report base statistica <nome-statistica> filtrati per api implementata o meno

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'erogazione',
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
    distingui_api_implementata: <distingui-api>,
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
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-identificazione-applicativo | distingui-api |
| 'distribuzione-api' | 'distribuzione-api' | filtro.esito.tipo | null | null | true |
| 'distribuzione-api' | 'distribuzione-api' | filtro.esito.tipo | null | null | false |


@ReportFullDistinguiApiImplementata
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per api implementata o meno 

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: 'erogazione',
     distingui_api_implementata: true,
    report: {
      "formato": "csv",
      "tipo": "table",
      "tipo_informazione": {
        "tipo": "numero_transazioni"
      }
    } 
})
"""  

* eval filtro.distingui_api_implementata = <distingui-api>
* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
* eval if(<filtro-claim> != null) filtro.claim = <filtro-claim>
* eval if(<filtro-tipo-identificazione-applicativo> != null) filtro.tipo_identificazione_applicativo = <filtro-tipo-identificazione-applicativo>
    
Given path <path-distribuzione>
    And request filtro
    When method post
    Then status 200
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-identificazione-applicativo | distingui-api |
| 'distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | null | true |
| 'distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | null | false |

