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
        "tipo": <filtro-informazione>
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
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-informazione | filtro-tipo-identificazione-applicativo |
| 'distribuzione-temporale' | 'distribuzione-temporale' | filtro.esito.tipo | null | 'numero_transazioni' | null |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null | null | 'numero_transazioni' | null |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo | null | 'numero_transazioni' | null |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo | null | 'numero_transazioni' | null |
| 'distribuzione-api' | 'distribuzione-api' | filtro.esito.tipo | null | 'numero_transazioni' | null |
| 'distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo | null | 'numero_transazioni' | null |
| 'distribuzione-applicativo' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'numero_transazioni' | null |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'numero_transazioni' | 'trasporto' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'numero_transazioni' | 'token' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo | null | 'numero_transazioni' | null |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | filtro.esito.tipo | 'subject' | 'numero_transazioni' | null |
| 'distribuzione-token-info-issuer' | 'distribuzione-token-info' | filtro.esito.tipo | 'issuer' | 'numero_transazioni' | null |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id' | 'numero_transazioni' | null |
| 'distribuzione-token-info-username' | 'distribuzione-token-info' | filtro.esito.tipo | 'username' | 'numero_transazioni' | null |
| 'distribuzione-token-info-email' | 'distribuzione-token-info' | filtro.esito.tipo | 'email' | 'numero_transazioni' | null |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo | null | 'numero_transazioni' | null |
| 'occupazione_banda-distribuzione-temporale' | 'distribuzione-temporale' | filtro.esito.tipo | null | 'occupazione_banda' | null |
| 'occupazione_banda-distribuzione-esiti' | 'distribuzione-esiti' | null | null | 'occupazione_banda' | null |
| 'occupazione_banda-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo | null | 'occupazione_banda' | null |
| 'occupazione_banda-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo | null | 'occupazione_banda' | null |
| 'occupazione_banda-distribuzione-api' | 'distribuzione-api' | filtro.esito.tipo | null | 'occupazione_banda' | null |
| 'occupazione_banda-distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo | null | 'occupazione_banda' | null |
| 'occupazione_banda-distribuzione-applicativo' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'occupazione_banda' | null | 
| 'occupazione_banda-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo | null | 'occupazione_banda' | null | 
| 'occupazione_banda-distribuzione-token-info' | 'distribuzione-token-info' | filtro.esito.tipo | filtro.claim | 'occupazione_banda' | null | 
| 'occupazione_banda-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo | null | 'occupazione_banda' | null |
| 'tempo_medio_risposta-distribuzione-temporale' | 'distribuzione-temporale' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null |
| 'tempo_medio_risposta-distribuzione-esiti' | 'distribuzione-esiti' | null | null | 'tempo_medio_risposta' | null | 
| 'tempo_medio_risposta-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null | 
| 'tempo_medio_risposta-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null | 
| 'tempo_medio_risposta-distribuzione-api' | 'distribuzione-api' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null | 
| 'tempo_medio_risposta-distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null | 
| 'tempo_medio_risposta-distribuzione-applicativo' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null | 
| 'tempo_medio_risposta-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null | 
| 'tempo_medio_risposta-distribuzione-token-info' | 'distribuzione-token-info' | filtro.esito.tipo | filtro.claim | 'tempo_medio_risposta' | null |
| 'tempo_medio_risposta-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null |

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
      "tipo_informazione": <filtro-informazione>
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
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-informazione | filtro-tipo-identificazione-applicativo |
| 'distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null | null | { "tipo":"numero_transazioni" } | null |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null |
| 'distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null |
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null |
| 'distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | 'trasporto' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | 'token' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | 'subject' | { "tipo":"numero_transazioni" } | null |
| 'distribuzione-token-info-issuer' | 'distribuzione-token-info' | { "tipo": "ok" } | 'issuer' | { "tipo":"numero_transazioni" } | null |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | { "tipo": "ok" } | 'client_id' | { "tipo":"numero_transazioni" } | null |
| 'distribuzione-token-info-username' | 'distribuzione-token-info' | { "tipo": "ok" } | 'username' | { "tipo":"numero_transazioni" } | null |
| 'distribuzione-token-info-email' | 'distribuzione-token-info' | { "tipo": "ok" } | 'email' | { "tipo":"numero_transazioni" } | null |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null |
| 'occupazione_banda-distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda": { "banda_complessiva":true, "banda_interna":false, "banda_esterna":false } } | null |
| 'occupazione_banda-all-distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda": { "banda_complessiva":true, "banda_interna":true, "banda_esterna":true } } | null |
| 'occupazione_banda-interna-distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda", "occupazione_banda": { "banda_complessiva":false, "banda_interna":true, "banda_esterna":false } } | null |
| 'occupazione_banda-esterna-distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda", "occupazione_banda": { "banda_complessiva":false, "banda_interna":false, "banda_esterna":true } } | null |
| 'occupazione_banda-distribuzione-esiti' | 'distribuzione-esiti' | null | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null |
| 'occupazione_banda-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null |
| 'occupazione_banda-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null |
| 'occupazione_banda-distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null |
| 'occupazione_banda-distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null |
| 'occupazione_banda-distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null |
| 'occupazione_banda-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null |
| 'occupazione_banda-distribuzione-token-info' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null |
| 'occupazione_banda-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null |
| 'occupazione_banda_interna-distribuzione-esiti' | 'distribuzione-esiti' | null | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null |
| 'occupazione_banda_interna-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null |
| 'occupazione_banda_interna-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null |
| 'occupazione_banda_interna-distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null |
| 'occupazione_banda_interna-distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null |
| 'occupazione_banda_interna-distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null |
| 'occupazione_banda_interna-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null |
| 'occupazione_banda_interna-distribuzione-token-info' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null |
| 'occupazione_banda_interna-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null |
| 'occupazione_banda_esterna-distribuzione-esiti' | 'distribuzione-esiti' | null | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null |
| 'occupazione_banda_esterna-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null |
| 'occupazione_banda_esterna-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null |
| 'occupazione_banda_esterna-distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null |
| 'occupazione_banda_esterna-distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null |
| 'occupazione_banda_esterna-distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null |
| 'occupazione_banda_esterna-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null |
| 'occupazione_banda_esterna-distribuzione-token-info' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null |
| 'occupazione_banda_esterna-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null |
| 'tempo_medio_risposta-distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta": { "latenza_totale":true, "latenza_servizio":false, "latenza_gateway":false } } | null |
| 'tempo_medio_risposta-all-distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta": { "latenza_totale":true, "latenza_servizio":true, "latenza_gateway":true } } | null |
| 'tempo_medio_risposta-interna-distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta": { "latenza_totale":false, "latenza_servizio":true, "latenza_gateway":false } } | null |
| 'tempo_medio_risposta-esterna-distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta": { "latenza_totale":false, "latenza_servizio":false, "latenza_gateway":true } } | null |
| 'tempo_medio_risposta-distribuzione-esiti' | 'distribuzione-esiti' | null | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null |
| 'tempo_medio_risposta-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null |
| 'tempo_medio_risposta-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null |
| 'tempo_medio_risposta-distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null |
| 'tempo_medio_risposta-distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null |
| 'tempo_medio_risposta-distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null |
| 'tempo_medio_risposta-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null |
| 'tempo_medio_risposta-distribuzione-token-info' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null |
| 'tempo_medio_risposta-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null |
| 'tempo_medio_risposta_interna-distribuzione-esiti' | 'distribuzione-esiti' | null | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null |
| 'tempo_medio_risposta_interna-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null |
| 'tempo_medio_risposta_interna-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null |
| 'tempo_medio_risposta_interna-distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null |
| 'tempo_medio_risposta_interna-distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null |
| 'tempo_medio_risposta_interna-distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null |
| 'tempo_medio_risposta_interna-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null |
| 'tempo_medio_risposta_interna-distribuzione-token-info' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null |
| 'tempo_medio_risposta_interna-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null |
| 'tempo_medio_risposta_esterna-distribuzione-esiti' | 'distribuzione-esiti' | null | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null |
| 'tempo_medio_risposta_esterna-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null |
| 'tempo_medio_risposta_esterna-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null |
| 'tempo_medio_risposta_esterna-distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null |
| 'tempo_medio_risposta_esterna-distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null |
| 'tempo_medio_risposta_esterna-distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null |
| 'tempo_medio_risposta_esterna-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null |
| 'tempo_medio_risposta_esterna-distribuzione-token-info' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null |
| 'tempo_medio_risposta_esterna-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null |


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
    }
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
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | 'subject' | null |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | { "tipo": "ok" } | 'client_id' | null |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | null |



@ReportBaseTipoErogazione
Scenario Outline: Ricerca di report base statistica <nome-statistica> filtrati per tipo = erogazione

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

@ReportFullTipoErogazione
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per tipo = erogazione  

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
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | 'subject' | null |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | { "tipo": "ok" } | 'client_id' | null |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | null |



@ReportFullTipoCheckTotali
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per tipo

* def filtro =
"""
({
		intervallo_temporale : intervallo_temporale,
		unita_tempo: 'orario',
		tipo: <filtro-tipo-qualsiasi>,
    report: {
      "formato": <filtro-report-formato>,
      "tipo": <filtro-report-tipo>,
      "tipo_informazione": {
        "tipo": <filtro-report-informazione>
      }
    } 
})
"""  

* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
* eval if(<filtro-claim> != null) filtro.claim = <filtro-claim>

* eval if(<nome-statistica>.contains('distribuzione-applicativo-trasporto')) filtro.tipo_identificazione_applicativo = 'trasporto'

* eval if(<nome-statistica>.contains('distribuzione-applicativo-token')) filtro.tipo_identificazione_applicativo = 'token'
    
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
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-qualsiasi | filtro-tipo-fruizione | filtro-tipo-erogazione | filtro-report-formato | filtro-report-tipo | filtro-report-informazione |
| 'distribuzione-temporale-csv' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' |
| 'distribuzione-esiti-csv' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' |
| 'distribuzione-soggetto-remoto-csv' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' |
| 'distribuzione-soggetto-locale-csv' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' |
| 'distribuzione-api-csv' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' |
| 'distribuzione-azione-csv' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' |
| 'distribuzione-applicativo-trasporto-csv' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' |
| 'distribuzione-applicativo-token-csv' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' |
| 'distribuzione-id-autenticato-csv' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' |
| 'distribuzione-token-info-csv' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' |
| 'distribuzione-indirizzo-ip-csv' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' |
| 'distribuzione-temporale-xls' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' |
| 'distribuzione-esiti-xls' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' |
| 'distribuzione-soggetto-remoto-xls' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' |
| 'distribuzione-soggetto-locale-xls' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' |
| 'distribuzione-api-xls' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' |
| 'distribuzione-azione-xls' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' |
| 'distribuzione-applicativo-trasporto-xls' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' |
| 'distribuzione-applicativo-token-xls' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' |
| 'distribuzione-id-autenticato-xls' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' |
| 'distribuzione-token-info-xls' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' |
| 'distribuzione-indirizzo-ip-xls' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' |
| 'distribuzione-temporale-pdf' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' |
| 'distribuzione-esiti-pdf' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' |
| 'distribuzione-soggetto-remoto-pdf' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' |
| 'distribuzione-soggetto-locale-pdf' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' |
| 'distribuzione-api-pdf' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' |
| 'distribuzione-azione-pdf' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' |
| 'distribuzione-applicativo-trasporto-pdf' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' |
| 'distribuzione-applicativo-token-pdf' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' |
| 'distribuzione-id-autenticato-pdf' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' |
| 'distribuzione-token-info-pdf' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' |
| 'distribuzione-indirizzo-ip-pdf' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' |
| 'distribuzione-temporale-xml' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' |
| 'distribuzione-esiti-xml' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' |
| 'distribuzione-soggetto-remoto-xml' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' |
| 'distribuzione-soggetto-locale-xml' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' |
| 'distribuzione-api-xml' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' |
| 'distribuzione-azione-xml' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' |
| 'distribuzione-applicativo-trasporto-xml' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' |
| 'distribuzione-applicativo-token-xml' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' |
| 'distribuzione-id-autenticato-xml' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' |
| 'distribuzione-token-info-xml' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' |
| 'distribuzione-indirizzo-ip-xml' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' |
| 'distribuzione-temporale-json' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' |
| 'distribuzione-esiti-json' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' |
| 'distribuzione-soggetto-remoto-json' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' |
| 'distribuzione-soggetto-locale-json' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' |
| 'distribuzione-api-json' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' |
| 'distribuzione-azione-json' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' |
| 'distribuzione-applicativo-trasporto-json' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' |
| 'distribuzione-applicativo-token-json' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' |
| 'distribuzione-id-autenticato-json' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' |
| 'distribuzione-token-info-json' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' |
| 'distribuzione-indirizzo-ip-json' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' |
| 'distribuzione-temporale-xml-line' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'line' | 'numero_transazioni' |
| 'distribuzione-esiti-xml-line' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'line' | 'numero_transazioni' |
| 'distribuzione-temporale-json-line' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'line' | 'numero_transazioni' |
| 'distribuzione-esiti-json-line' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'line' | 'numero_transazioni' |
| 'distribuzione-soggetto-remoto-xml-pie' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' |
| 'distribuzione-soggetto-locale-xml-pie' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' |
| 'distribuzione-api-xml-pie' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' |
| 'distribuzione-azione-xml-pie' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' |
| 'distribuzione-applicativo-trasporto-xml-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' |
| 'distribuzione-applicativo-token-xml-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' |
| 'distribuzione-id-autenticato-xml-pie' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' |
| 'distribuzione-token-info-xml-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' |
| 'distribuzione-indirizzo-ip-xml-pie' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' |
| 'distribuzione-soggetto-remoto-json-pie' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' |
| 'distribuzione-soggetto-locale-json-pie' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' |
| 'distribuzione-api-json-pie' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' |
| 'distribuzione-azione-json-pie' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' |
| 'distribuzione-applicativo-trasporto-json-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' |
| 'distribuzione-applicativo-token-json-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' |
| 'distribuzione-id-autenticato-json-pie' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' |
| 'distribuzione-token-info-json-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' |
| 'distribuzione-indirizzo-ip-json-pie' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' |
| 'distribuzione-temporale-xml-bar' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' |
| 'distribuzione-esiti-xml-bar' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' |
| 'distribuzione-soggetto-remoto-xml-bar' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' |
| 'distribuzione-soggetto-locale-xml-bar' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' |
| 'distribuzione-api-xml-bar' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' |
| 'distribuzione-azione-xml-bar' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' |
| 'distribuzione-applicativo-trasporto-xml-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' |
| 'distribuzione-applicativo-token-xml-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' |
| 'distribuzione-id-autenticato-xml-bar' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' |
| 'distribuzione-token-info-xml-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' |
| 'distribuzione-indirizzo-ip-xml-bar' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' |
| 'distribuzione-temporale-json-bar' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' |
| 'distribuzione-esiti-json-bar' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' |
| 'distribuzione-soggetto-remoto-json-bar' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' |
| 'distribuzione-soggetto-locale-json-bar' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' |
| 'distribuzione-api-json-bar' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' |
| 'distribuzione-azione-json-bar' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' |
| 'distribuzione-applicativo-trasporto-json-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' |
| 'distribuzione-applicativo-token-json-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' |
| 'distribuzione-id-autenticato-json-bar' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' |
| 'distribuzione-token-info-json-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' |
| 'distribuzione-indirizzo-ip-json-bar' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' |
| 'occupazione-banda-distribuzione-temporale-csv' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-esiti-csv' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-remoto-csv' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-locale-csv' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-api-csv' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-azione-csv' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-csv' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-token-csv' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-id-autenticato-csv' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-token-info-csv' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-indirizzo-ip-csv' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-temporale-xls' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-esiti-xls' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-remoto-xls' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-locale-xls' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-api-xls' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-azione-xls' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-xls' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-token-xls' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-id-autenticato-xls' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-token-info-xls' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-indirizzo-ip-xls' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-temporale-pdf' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-esiti-pdf' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-remoto-pdf' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-locale-pdf' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-api-pdf' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-azione-pdf' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-pdf' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-token-pdf' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-id-autenticato-pdf' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-token-info-pdf' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-indirizzo-ip-pdf' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-temporale-xml' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-esiti-xml' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-remoto-xml' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-locale-xml' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-api-xml' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-azione-xml' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-xml' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-token-xml' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-id-autenticato-xml' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-token-info-xml' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-indirizzo-ip-xml' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-temporale-json' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-esiti-json' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-remoto-json' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-locale-json' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-api-json' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-azione-json' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-json' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-token-json' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-id-autenticato-json' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-token-info-json' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-indirizzo-ip-json' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-temporale-xml-line' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'line' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-esiti-xml-line' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'line' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-temporale-json-line' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'line' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-esiti-json-line' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'line' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-remoto-xml-pie' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-locale-xml-pie' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-api-xml-pie' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-azione-xml-pie' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-xml-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-token-xml-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-id-autenticato-xml-pie' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-token-info-xml-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-indirizzo-ip-xml-pie' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-remoto-json-pie' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-locale-json-pie' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-api-json-pie' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-azione-json-pie' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-json-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-token-json-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-id-autenticato-json-pie' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-token-info-json-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-indirizzo-ip-json-pie' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-temporale-xml-bar' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-esiti-xml-bar' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-remoto-xml-bar' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-locale-xml-bar' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-api-xml-bar' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-azione-xml-bar' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-xml-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-token-xml-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-id-autenticato-xml-bar' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-token-info-xml-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-indirizzo-ip-xml-bar' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-temporale-json-bar' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-esiti-json-bar' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-remoto-json-bar' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-soggetto-locale-json-bar' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-api-json-bar' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-azione-json-bar' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-json-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-applicativo-token-json-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-id-autenticato-json-bar' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-token-info-json-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' |
| 'occupazione-banda-distribuzione-indirizzo-ip-json-bar' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' |
| 'tempo-medio-risposta-distribuzione-temporale-csv' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-esiti-csv' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-csv' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-csv' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-api-csv' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-azione-csv' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-csv' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-csv' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-csv' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-token-info-csv' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-csv' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-temporale-xls' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-esiti-xls' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-xls' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-xls' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-api-xls' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-azione-xls' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-xls' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-xls' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-xls' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-token-info-xls' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-xls' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-temporale-pdf' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-esiti-pdf' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-pdf' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-pdf' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-api-pdf' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-azione-pdf' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-pdf' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-pdf' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-pdf' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-token-info-pdf' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-pdf' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-temporale-xml' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-esiti-xml' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-xml' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-xml' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-api-xml' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-azione-xml' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-xml' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-xml' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-xml' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-token-info-xml' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-xml' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-temporale-json' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-esiti-json' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-json' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-json' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-api-json' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-azione-json' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-json' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-json' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-json' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-token-info-json' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-json' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-temporale-xml-line' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'line' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-esiti-xml-line' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'line' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-temporale-json-line' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'line' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-esiti-json-line' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'line' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-xml-pie' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-xml-pie' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-api-xml-pie' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-azione-xml-pie' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-xml-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-xml-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-xml-pie' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-token-info-xml-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-xml-pie' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-json-pie' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-json-pie' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-api-json-pie' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-azione-json-pie' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-json-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-json-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-json-pie' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-token-info-json-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-json-pie' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-temporale-xml-bar' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-esiti-xml-bar' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-xml-bar' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-xml-bar' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-api-xml-bar' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-azione-xml-bar' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-xml-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-xml-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-xml-bar' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-token-info-xml-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-xml-bar' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-temporale-json-bar' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-esiti-json-bar' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-json-bar' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-json-bar' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-api-json-bar' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-azione-json-bar' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-json-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-json-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-json-bar' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-token-info-json-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-json-bar' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' |

@ReportFullTipoCheckTotaliConConteggio
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per tipo e conteggio dati (formato report json per fare le verifiche)

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
* eval if(<filtro-tipo-identificazione-applicativo> != null) filtro.tipo_identificazione_applicativo = <filtro-tipo-identificazione-applicativo>
    
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
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-qualsiasi | filtro-tipo-fruizione | filtro-tipo-erogazione | filtro-tipo-identificazione-applicativo |
| 'distribuzione-temporale' | 'distribuzione-temporale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null |
| 'distribuzione-esiti' | 'distribuzione-esiti' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null |
| 'distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null |
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'trasporto' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'token' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | null |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id" | 'qualsiasi' | 'fruizione' | 'erogazione' | null |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null |


