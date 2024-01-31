@parallel=false
Feature: Reportistica e esportazione grafici in varie dimensioni

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
      },
      "dimensioni": <dimensioni-report>
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
    tipo_identificazione: <filtro-tipo-identificazione-applicativo>,
    dimensioni_report: <dimensioni-report>
})
"""

* def check_response = <path-distribuzione>.contains('distribuzione-esiti') ? "Ok" : <filtro-informazione>.contains('numero_transazioni') ? "Numero Transazioni" : <filtro-informazione>.contains('occupazione_banda') ? "Occupazione Banda" : "Latenza Media"

* def check_response_dimensioni = <dimensioni-report>.contains('3d') ?  "Data" : check_response

Given path <path-distribuzione>
    And params query
    When method get
    Then status 200
    Then match karate.toString(response) contains check_response
    Then match karate.toString(response) contains check_response_dimensioni
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-informazione | filtro-tipo-identificazione-applicativo | dimensioni-report |
| 'distribuzione-errori (verifica 2d)' | 'distribuzione-errori' | null | null | 'numero_transazioni' | null | '2d' |
| 'distribuzione-errori' | 'distribuzione-errori' | null | null | 'numero_transazioni' | null | '3d' |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo | null | 'numero_transazioni' | null | '3d' |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo | null | 'numero_transazioni' | null | '3d' |
| 'distribuzione-api' | 'distribuzione-api' | filtro.esito.tipo | null | 'numero_transazioni' | null | '3d' |
| 'distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo | null | 'numero_transazioni' | null | '3d' |
| 'distribuzione-applicativo' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'numero_transazioni' | null | '3d' |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'numero_transazioni' | 'trasporto' | '3d' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'numero_transazioni' | 'token' | '3d' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo | null | 'numero_transazioni' | null | '3d' |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | filtro.esito.tipo | 'subject' | 'numero_transazioni' | null | '3d' |
| 'distribuzione-token-info-issuer' | 'distribuzione-token-info' | filtro.esito.tipo | 'issuer' | 'numero_transazioni' | null | '3d' |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id' | 'numero_transazioni' | null | '3d' |
| 'distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id_pdnd_informazioni' | 'numero_transazioni' | null | '3d' |
| 'distribuzione-token-info-username' | 'distribuzione-token-info' | filtro.esito.tipo | 'username' | 'numero_transazioni' | null | '3d' |
| 'distribuzione-token-info-email' | 'distribuzione-token-info' | filtro.esito.tipo | 'email' | 'numero_transazioni' | null | '3d' |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo | null | 'numero_transazioni' | null | '3d' |
| 'occupazione_banda-distribuzione-errori (verifica 2d)' | 'distribuzione-errori' | null | null | 'occupazione_banda' | null | '2d' |
| 'occupazione_banda-distribuzione-errori' | 'distribuzione-errori' | null | null | 'occupazione_banda' | null | '3d' |
| 'occupazione_banda-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo | null | 'occupazione_banda' | null | '3d' |
| 'occupazione_banda-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo | null | 'occupazione_banda' | null | '3d' |
| 'occupazione_banda-distribuzione-api' | 'distribuzione-api' | filtro.esito.tipo | null | 'occupazione_banda' | null | '3d' |
| 'occupazione_banda-distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo | null | 'occupazione_banda' | null | '3d' |
| 'occupazione_banda-distribuzione-applicativo' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'occupazione_banda' | null |  '3d' |
| 'occupazione_banda-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo | null | 'occupazione_banda' | null |  '3d' |
| 'occupazione_banda-distribuzione-token-info-subject' | 'distribuzione-token-info' | filtro.esito.tipo | 'subject' | 'occupazione_banda' | null | '3d' |
| 'occupazione_banda-distribuzione-token-info-issuer' | 'distribuzione-token-info' | filtro.esito.tipo | 'issuer' | 'occupazione_banda' | null | '3d' |
| 'occupazione_banda-distribuzione-token-info-client-id' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id' | 'occupazione_banda' | null | '3d' |
| 'occupazione_banda-distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id_pdnd_informazioni' | 'occupazione_banda' | null | '3d' |
| 'occupazione_banda-distribuzione-token-info-username' | 'distribuzione-token-info' | filtro.esito.tipo | 'username' | 'occupazione_banda' | null | '3d' |
| 'occupazione_banda-distribuzione-token-info-email' | 'distribuzione-token-info' | filtro.esito.tipo | 'email' | 'occupazione_banda' | null | '3d' |
| 'occupazione_banda-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo | null | 'occupazione_banda' | null | '3d' |
| 'tempo_medio_risposta-distribuzione-errori (verifica 2d)' | 'distribuzione-errori' | null | null | 'tempo_medio_risposta' | null | '2d' | 
| 'tempo_medio_risposta-distribuzione-errori' | 'distribuzione-errori' | null | null | 'tempo_medio_risposta' | null | '3d' |
| 'tempo_medio_risposta-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null | '3d' | 
| 'tempo_medio_risposta-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null | '3d' |
| 'tempo_medio_risposta-distribuzione-api' | 'distribuzione-api' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null | '3d' | 
| 'tempo_medio_risposta-distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null | '3d' | 
| 'tempo_medio_risposta-distribuzione-applicativo' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null | '3d' | 
| 'tempo_medio_risposta-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null | '3d' | 
| 'tempo_medio_risposta-distribuzione-token-info-subject' | 'distribuzione-token-info' | filtro.esito.tipo | 'subject' | 'tempo_medio_risposta' | null | '3d' |
| 'tempo_medio_risposta-distribuzione-token-info-issuer' | 'distribuzione-token-info' | filtro.esito.tipo | 'issuer' | 'tempo_medio_risposta' | null | '3d' |
| 'tempo_medio_risposta-distribuzione-token-info-client-id' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id' | 'tempo_medio_risposta' | null | '3d' |
| 'tempo_medio_risposta-distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id_pdnd_informazioni' | 'tempo_medio_risposta' | null | '3d' |
| 'tempo_medio_risposta-distribuzione-token-info-username' | 'distribuzione-token-info' | filtro.esito.tipo | 'username' | 'tempo_medio_risposta' | null | '3d' |
| 'tempo_medio_risposta-distribuzione-token-info-email' | 'distribuzione-token-info' | filtro.esito.tipo | 'email' | 'tempo_medio_risposta' | null | '3d' |
| 'tempo_medio_risposta-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo | null | 'tempo_medio_risposta' | null | '3d' |





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
      "tipo_informazione": <filtro-informazione>,
      "dimensioni": <dimensioni-report>
    } 
})
"""  

* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
* eval if(<filtro-claim> != null) filtro.claim = <filtro-claim>
* eval if(<filtro-tipo-identificazione-applicativo> != null) filtro.tipo_identificazione_applicativo = <filtro-tipo-identificazione-applicativo>
    
* def check_response = <stringa-verifica>

* def check_response_dimensioni = <dimensioni-report>.contains('3d') ?  "Data" : check_response

Given path <path-distribuzione>
    And request filtro
    When method post
    Then status 200
    Then match karate.toString(response) contains check_response
    Then match karate.toString(response) contains check_response_dimensioni
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-informazione | filtro-tipo-identificazione-applicativo | stringa-verifica | dimensioni-report |
| 'distribuzione-errori (verifica 2d)' | 'distribuzione-errori' | null | null | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '2d' |
| 'distribuzione-errori' | 'distribuzione-errori' | null | null | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '3d' |
| 'distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '3d' |
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | 'trasporto' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | 'token' | "Numero Transazioni" | '3d' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | 'subject' | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-issuer' | 'distribuzione-token-info' | { "tipo": "ok" } | 'issuer' | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | { "tipo": "ok" } | 'client_id' | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | { "tipo": "ok" } | 'client_id_pdnd_informazioni' | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-username' | 'distribuzione-token-info' | { "tipo": "ok" } | 'username' | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-email' | 'distribuzione-token-info' | { "tipo": "ok" } | 'email' | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '3d' |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | { "tipo":"numero_transazioni" } | null | "Numero Transazioni" | '3d' |
| 'occupazione_banda-distribuzione-errori (verifica 2d)' | 'distribuzione-errori' | null | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null | "Occupazione Banda" | '2d' |
| 'occupazione_banda-distribuzione-errori' | 'distribuzione-errori' | null | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda-distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda-distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda-distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda-distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda-distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_complessiva"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_interna-distribuzione-errori' | 'distribuzione-errori' | null | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_interna-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_interna-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_interna-distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_interna-distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_interna-distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_interna-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_interna-distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_interna-distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_interna-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_interna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_esterna-distribuzione-errori' | 'distribuzione-errori' | null | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_esterna-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_esterna-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_esterna-distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_esterna-distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_esterna-distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_esterna-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_esterna-distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_esterna-distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null | "Occupazione Banda" | '3d' |
| 'occupazione_banda_esterna-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | { "tipo":"occupazione_banda" , "occupazione_banda":"banda_esterna"} | null | "Occupazione Banda" | '3d' |
| 'tempo_medio_risposta-distribuzione-errori (verifica 2d)' | 'distribuzione-errori' | null | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null | "Latenza Media" | '2d' |
| 'tempo_medio_risposta-distribuzione-errori' | 'distribuzione-errori' | null | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta-distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta-distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta-distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta-distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta-distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_totale" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta_interna-distribuzione-errori' | 'distribuzione-errori' | null | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null | "Tempo Medio Risposta" | '3d' |
| 'tempo_medio_risposta_interna-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null | "Tempo Medio Risposta" | '3d' |
| 'tempo_medio_risposta_interna-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null | "Tempo Medio Risposta" | '3d' |
| 'tempo_medio_risposta_interna-distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null | "Tempo Medio Risposta" | '3d' |
| 'tempo_medio_risposta_interna-distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null | "Tempo Medio Risposta" | '3d' |
| 'tempo_medio_risposta_interna-distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null | "Tempo Medio Risposta" | '3d' |
| 'tempo_medio_risposta_interna-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null | "Tempo Medio Risposta" | '3d' |
| 'tempo_medio_risposta_interna-distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null | "Tempo Medio Risposta" | '3d' |
| 'tempo_medio_risposta_interna-distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null | "Tempo Medio Risposta" | '3d' |
| 'tempo_medio_risposta_interna-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_servizio" } | null | "Tempo Medio Risposta" | '3d' |
| 'tempo_medio_risposta_esterna-distribuzione-errori' | 'distribuzione-errori' | null | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta_esterna-distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta_esterna-distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta_esterna-distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta_esterna-distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta_esterna-distribuzione-applicativo' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta_esterna-distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta_esterna-distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta_esterna-distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null | "Latenza Media" | '3d' |
| 'tempo_medio_risposta_esterna-distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | { "tipo":"tempo_medio_risposta", "tempo_medio_risposta":"latenza_gateway" } | null | "Latenza Media" | '3d' |




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
      },
      "dimensioni": <dimensioni-report>
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
    tipo_identificazione: <filtro-tipo-identificazione-applicativo>,
    dimensioni_report: <dimensioni-report>
})
"""

* def check_response = <path-distribuzione>.contains('distribuzione-esiti') ? "Ok" : "Numero Transazioni"

* def check_response_dimensioni = <dimensioni-report>.contains('3d') ?  "Data" : check_response

Given path <path-distribuzione>
    And params query
    When method get
    Then status 200
    Then match karate.toString(response) contains check_response
    Then match karate.toString(response) contains check_response_dimensioni
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-identificazione-applicativo | dimensioni-report | 
| 'distribuzione-errori (verifica 2d)' | 'distribuzione-errori' | null | null | null | '2d' |
| 'distribuzione-errori' | 'distribuzione-errori' | null | null | null | '3d' |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo | null | null | '3d' |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo | null | null | '3d' | 
| 'distribuzione-api' | 'distribuzione-api' | filtro.esito.tipo | null | null | '3d' |
| 'distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo | null | null | '3d' |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'trasporto' | '3d' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'token' | '3d' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo | null | null | '3d' |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | filtro.esito.tipo | 'subject' | null | '3d' |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id' | null | '3d' |
| 'distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id_pdnd_informazioni' | null | '3d' |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo | null | null | '3d' |

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
      },
      "dimensioni": <dimensioni-report>
    } 
})
"""  

* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
* eval if(<filtro-claim> != null) filtro.claim = <filtro-claim>
* eval if(<filtro-tipo-identificazione-applicativo> != null) filtro.tipo_identificazione_applicativo = <filtro-tipo-identificazione-applicativo>
    
* def check_response = <path-distribuzione>.contains('distribuzione-esiti') ? "Ok" : "Numero Transazioni"

* def check_response_dimensioni = <dimensioni-report>.contains('3d') ?  "Data" : check_response

Given path <path-distribuzione>
    And request filtro
    When method post
    Then status 200
    Then match karate.toString(response) contains check_response
    Then match karate.toString(response) contains check_response_dimensioni
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-identificazione-applicativo | dimensioni-report |
| 'distribuzione-errori (verifica 2d)' | 'distribuzione-errori' | null | null | null | '2d' |
| 'distribuzione-errori' | 'distribuzione-errori' | null | null | null | '3d' |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | null | '3d' |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | null | '3d' |
| 'distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | null | '3d' |
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | null | '3d' |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'trasporto' | '3d' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'token' | '3d' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | null | '3d' |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | 'subject' | null | '3d' |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | { "tipo": "ok" } | 'client_id' | null | '3d' |
| 'distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | { "tipo": "ok" } | 'client_id_pdnd_informazioni' | null | '3d' |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | null | '3d' |



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
      },
      "dimensioni": <dimensioni-report>
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
    tipo_identificazione: <filtro-tipo-identificazione-applicativo>,
    dimensioni_report: <dimensioni-report>
})
"""

* def check_response = <path-distribuzione>.contains('distribuzione-esiti') ? "Ok" : "Numero Transazioni"

* def check_response_dimensioni = <dimensioni-report>.contains('3d') ?  "Data" : check_response
		
Given path <path-distribuzione>
    And params query
    When method get
    Then status 200
    Then match karate.toString(response) contains check_response
    Then match karate.toString(response) contains check_response_dimensioni
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-identificazione-applicativo | dimensioni-report |
| 'distribuzione-errori (verifica 2d)' | 'distribuzione-errori' | null | null | null | '2d' |
| 'distribuzione-errori' | 'distribuzione-errori' | null | null | null | '3d' |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | filtro.esito.tipo | null | null | '3d' | 
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | filtro.esito.tipo | null | null | '3d' | 
| 'distribuzione-api' | 'distribuzione-api' | filtro.esito.tipo | null | null | '3d' | 
| 'distribuzione-azione' | 'distribuzione-azione' | filtro.esito.tipo | null | null | '3d' | 
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'trasporto' | '3d' | 
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | filtro.esito.tipo | null | 'token' | '3d' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | filtro.esito.tipo | null | null | '3d' | 
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | filtro.esito.tipo | 'subject' | null | '3d' |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id' | null | '3d' |
| 'distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | filtro.esito.tipo | 'client_id_pdnd_informazioni' | null | '3d' |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | filtro.esito.tipo | null | null | '3d' |

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
      },
      "dimensioni": <dimensioni-report>
    } 
})
"""  

* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
* eval if(<filtro-claim> != null) filtro.claim = <filtro-claim>
* eval if(<filtro-tipo-identificazione-applicativo> != null) filtro.tipo_identificazione_applicativo = <filtro-tipo-identificazione-applicativo>
    
* def check_response = <path-distribuzione>.contains('distribuzione-esiti') ? "Ok" : "Numero Transazioni"

* def check_response_dimensioni = <dimensioni-report>.contains('3d') ?  "Data" : check_response

Given path <path-distribuzione>
    And request filtro
    When method post
    Then status 200
    Then match karate.toString(response) contains check_response
    Then match karate.toString(response) contains check_response_dimensioni
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-identificazione-applicativo | dimensioni-report |
| 'distribuzione-errori (verifica 2d)' | 'distribuzione-errori' | null | null | null | '2d' |
| 'distribuzione-errori' | 'distribuzione-errori' | null | null | null | '3d' |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | null | '3d' |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | null | '3d' |
| 'distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | null | '3d' |
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | null | '3d' |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'trasporto' | '3d' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'token' | '3d' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | null | '3d' |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | 'subject' | null | '3d' |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | { "tipo": "ok" } | 'client_id' | null | '3d' |
| 'distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | { "tipo": "ok" } | 'client_id_pdnd_informazioni' | null | '3d' |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | null | '3d' |



@ReportFullTipoCheckTotaliNumeroTransazioni
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per tipo (numero-transazioni)

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
      },
      "dimensioni": <dimensioni-report>
    } 
})
"""  

* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
* eval if(<filtro-claim> != null) filtro.claim = <filtro-claim>

* eval if(<nome-statistica>.contains('distribuzione-applicativo-trasporto')) filtro.tipo_identificazione_applicativo = 'trasporto'

* eval if(<nome-statistica>.contains('distribuzione-applicativo-token')) filtro.tipo_identificazione_applicativo = 'token'

* def check_response = <stringa-verifica>

* def data_oggi = 31/01/24
* def today = Java.type('java.time.LocalDate').now()
* def dataOggi = today.format(java.time.format.DateTimeFormatter.ofPattern('dd/MM/yy'))

* def check_response_dimensioni = <filtro-report-formato>.contains('pdf') ? "PDF-1.4" : <filtro-report-formato>.contains('xml') ? check_response : <dimensioni-report>.contains('2d') ? check_response : <filtro-report-formato>.contains('json') ? dataOggi : "Data"
    
	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	Then match karate.toString(response) contains check_response
	Then match karate.toString(response) contains check_response_dimensioni
	* def response_qualsiasi = response
	
	* eval filtro.tipo = <filtro-tipo-fruizione>
	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	Then match karate.toString(response) contains check_response
	Then match karate.toString(response) contains check_response_dimensioni
	* def response_fruizione = response
	
	* eval filtro.tipo = <filtro-tipo-erogazione>
	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	Then match karate.toString(response) contains check_response
	Then match karate.toString(response) contains check_response_dimensioni
	* def response_erogazione = response
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-qualsiasi | filtro-tipo-fruizione | filtro-tipo-erogazione | filtro-report-formato | filtro-report-tipo | filtro-report-informazione | stringa-verifica | dimensioni-report |
| 'distribuzione-errori-csv (verifica 2d)' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '2d' |
| 'distribuzione-errori-csv' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-remoto-csv' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-locale-csv' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-api-csv' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-azione-csv' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-trasporto-csv' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-token-csv' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-id-autenticato-csv' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-subject-csv' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-client-id-pdnd-csv' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-indirizzo-ip-csv' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-errori-xls (verifica 2d)' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '2d' |
| 'distribuzione-errori-xls' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-remoto-xls' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-locale-xls' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-api-xls' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-azione-xls' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-trasporto-xls' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-token-xls' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-id-autenticato-xls' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-subject-xls' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-client-id-pdnd-xls' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-indirizzo-ip-xls' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-errori-pdf (verifica 2d)' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' | "PDF-1.4" | '2d' |
| 'distribuzione-errori-pdf' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' | "PDF-1.4" | '3d' |
| 'distribuzione-soggetto-remoto-pdf' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' | "PDF-1.4" | '3d' |
| 'distribuzione-soggetto-locale-pdf' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' | "PDF-1.4" | '3d' |
| 'distribuzione-api-pdf' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' | "PDF-1.4" | '3d' |
| 'distribuzione-azione-pdf' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' | "PDF-1.4" | '3d' |
| 'distribuzione-applicativo-trasporto-pdf' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' | "PDF-1.4" | '3d' |
| 'distribuzione-applicativo-token-pdf' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' | "PDF-1.4" | '3d' |
| 'distribuzione-id-autenticato-pdf' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' | "PDF-1.4" | '3d' |
| 'distribuzione-token-info-subject-pdf' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' | "PDF-1.4" | '3d' |
| 'distribuzione-token-info-client-id-pdnd-pdf' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' | "PDF-1.4" | '3d' |
| 'distribuzione-indirizzo-ip-pdf' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'numero_transazioni' | "PDF-1.4" | '3d' |
| 'distribuzione-errori-xml' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-remoto-xml' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-locale-xml' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-api-xml' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-azione-xml' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-trasporto-xml' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-token-xml' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-id-autenticato-xml' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-subject-xml' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-client-id-pdnd-xml' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-indirizzo-ip-xml' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-errori-json (verifica 2d)' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '2d' |
| 'distribuzione-errori-json' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-remoto-json' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-locale-json' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-api-json' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-azione-json' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-trasporto-json' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-token-json' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-id-autenticato-json' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-subject-json' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-client-id-pdnd-json' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-indirizzo-ip-json' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-errori-xml-line' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'line' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-errori-json-line (verifica 2d)' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'line' | 'numero_transazioni' | "Numero Transazioni" | '2d' |
| 'distribuzione-errori-json-line' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'line' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-remoto-xml-pie' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-locale-xml-pie' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-api-xml-pie' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-azione-xml-pie' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-trasporto-xml-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-token-xml-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-id-autenticato-xml-pie' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-subject-xml-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-client-id-pdnd-xml-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-indirizzo-ip-xml-pie' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-remoto-json-pie' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-locale-json-pie' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-api-json-pie' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-azione-json-pie' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-trasporto-json-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-token-json-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-id-autenticato-json-pie' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-subject-json-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-client-id-pdnd-json-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-indirizzo-ip-json-pie' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-errori-xml-bar' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-remoto-xml-bar' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-locale-xml-bar' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-api-xml-bar' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-azione-xml-bar' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-trasporto-xml-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-token-xml-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-id-autenticato-xml-bar' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-subject-xml-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-client-id-pdnd-xml-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-indirizzo-ip-xml-bar' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-errori-json-bar (verifica 2d)' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '2d' |
| 'distribuzione-errori-json-bar' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-remoto-json-bar' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-soggetto-locale-json-bar' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-api-json-bar' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-azione-json-bar' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-trasporto-json-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-applicativo-token-json-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-id-autenticato-json-bar' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-subject-json-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-token-info-client-id-pdnd-json-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |
| 'distribuzione-indirizzo-ip-json-bar' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'numero_transazioni' | "Numero Transazioni" | '3d' |



@ReportFullTipoCheckTotaliOccupazioneBanda
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per tipo (occupazione-banda)

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
      },
      "dimensioni": <dimensioni-report>
    } 
})
"""  

* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
* eval if(<filtro-claim> != null) filtro.claim = <filtro-claim>

* eval if(<nome-statistica>.contains('distribuzione-applicativo-trasporto')) filtro.tipo_identificazione_applicativo = 'trasporto'

* eval if(<nome-statistica>.contains('distribuzione-applicativo-token')) filtro.tipo_identificazione_applicativo = 'token'

* def check_response = <stringa-verifica>

* def data_oggi = 31/01/24
* def today = Java.type('java.time.LocalDate').now()
* def dataOggi = today.format(java.time.format.DateTimeFormatter.ofPattern('dd/MM/yy'))

* def check_response_dimensioni = <filtro-report-formato>.contains('pdf') ? "PDF-1.4" : <filtro-report-formato>.contains('xml') ? check_response : <dimensioni-report>.contains('2d') ? check_response : <filtro-report-formato>.contains('json') ? dataOggi : "Data"
    
	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	Then match karate.toString(response) contains check_response
	Then match karate.toString(response) contains check_response_dimensioni
	* def response_qualsiasi = response
	
	* eval filtro.tipo = <filtro-tipo-fruizione>
	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	Then match karate.toString(response) contains check_response
	Then match karate.toString(response) contains check_response_dimensioni
	* def response_fruizione = response
	
	* eval filtro.tipo = <filtro-tipo-erogazione>
	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	Then match karate.toString(response) contains check_response
	Then match karate.toString(response) contains check_response_dimensioni
	* def response_erogazione = response
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-qualsiasi | filtro-tipo-fruizione | filtro-tipo-erogazione | filtro-report-formato | filtro-report-tipo | filtro-report-informazione | stringa-verifica | dimensioni-report |
| 'occupazione-banda-distribuzione-errori-csv (verifica 2d)' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '2d' |
| 'occupazione-banda-distribuzione-errori-csv' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-remoto-csv' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-locale-csv' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-api-csv' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-azione-csv' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-csv' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-token-csv' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-id-autenticato-csv' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-subject-csv' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-client-id-pdnd-csv' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-indirizzo-ip-csv' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-errori-xls (verifica 2d)' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '2d' |
| 'occupazione-banda-distribuzione-errori-xls' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-remoto-xls' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-locale-xls' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-api-xls' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-azione-xls' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-xls' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-token-xls' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-id-autenticato-xls' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-subject-xls' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-client-id-pdnd-xls' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-indirizzo-ip-xls' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-errori-pdf (verifica 2d)' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' | "PDF-1.4" | '2d' |
| 'occupazione-banda-distribuzione-errori-pdf' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' | "PDF-1.4" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-remoto-pdf' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' | "PDF-1.4" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-locale-pdf' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' | "PDF-1.4" | '3d' |
| 'occupazione-banda-distribuzione-api-pdf' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' | "PDF-1.4" | '3d' |
| 'occupazione-banda-distribuzione-azione-pdf' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' | "PDF-1.4" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-pdf' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' | "PDF-1.4" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-token-pdf' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' | "PDF-1.4" | '3d' |
| 'occupazione-banda-distribuzione-id-autenticato-pdf' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' | "PDF-1.4" | '3d' |
| 'occupazione-banda-distribuzione-token-info-subject-pdf' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' | "PDF-1.4" | '3d' |
| 'occupazione-banda-distribuzione-token-info-client-id-pdnd-pdf' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' | "PDF-1.4" | '3d' |
| 'occupazione-banda-distribuzione-indirizzo-ip-pdf' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'occupazione_banda' | "PDF-1.4" | '3d' |
| 'occupazione-banda-distribuzione-errori-xml' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-remoto-xml' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-locale-xml' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-api-xml' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-azione-xml' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-xml' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-token-xml' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-id-autenticato-xml' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-subject-xml' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-client-id-pdnd-xml' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-indirizzo-ip-xml' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-errori-json (verifica 2d)' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '2d' |
| 'occupazione-banda-distribuzione-errori-json' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-remoto-json' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-locale-json' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-api-json' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-azione-json' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-json' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-token-json' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-id-autenticato-json' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-subject-json' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-client-id-pdnd-json' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-indirizzo-ip-json' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-errori-xml-line' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'line' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-errori-json-line (verifica 2d)' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'line' | 'occupazione_banda' | "Occupazione Banda" | '2d' |
| 'occupazione-banda-distribuzione-errori-json-line' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'line' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-remoto-xml-pie' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-locale-xml-pie' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-api-xml-pie' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-azione-xml-pie' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-xml-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-token-xml-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-id-autenticato-xml-pie' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-subject-xml-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-client-id-pdnd-xml-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-indirizzo-ip-xml-pie' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-remoto-json-pie' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-locale-json-pie' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-api-json-pie' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-azione-json-pie' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-json-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-token-json-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-id-autenticato-json-pie' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-subject-json-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-client-id-pdnd-json-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-indirizzo-ip-json-pie' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-errori-xml-bar' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-remoto-xml-bar' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-locale-xml-bar' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-api-xml-bar' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-azione-xml-bar' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-xml-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-token-xml-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-id-autenticato-xml-bar' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-subject-xml-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-client-id-pdnd-xml-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-indirizzo-ip-xml-bar' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-errori-json-bar (verifica 2d)' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '2d' |
| 'occupazione-banda-distribuzione-errori-json-bar' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-remoto-json-bar' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-soggetto-locale-json-bar' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-api-json-bar' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-azione-json-bar' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-trasporto-json-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-applicativo-token-json-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-id-autenticato-json-bar' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-subject-json-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-token-info-client-id-pdnd-json-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |
| 'occupazione-banda-distribuzione-indirizzo-ip-json-bar' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'occupazione_banda' | "Occupazione Banda" | '3d' |



@ReportFullTipoCheckTotaliTempoMedioRisposta
Scenario Outline: Ricerca di report full statistica <nome-statistica> filtrati per tipo (tempo-medio-risposta)

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
        "tipo": <filtro-report-informazione>,
        "tempo_medio_risposta": "latenza_totale"
      },
      "dimensioni": <dimensioni-report>
    } 
})
"""  

* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
* eval if(<filtro-claim> != null) filtro.claim = <filtro-claim>

* eval if(<nome-statistica>.contains('distribuzione-applicativo-trasporto')) filtro.tipo_identificazione_applicativo = 'trasporto'

* eval if(<nome-statistica>.contains('distribuzione-applicativo-token')) filtro.tipo_identificazione_applicativo = 'token'

* def check_response = <stringa-verifica>
    
* def data_oggi = 31/01/24
* def today = Java.type('java.time.LocalDate').now()
* def dataOggi = today.format(java.time.format.DateTimeFormatter.ofPattern('dd/MM/yy'))

* def check_response_dimensioni = <filtro-report-formato>.contains('pdf') ? "PDF-1.4" : <filtro-report-formato>.contains('xml') ? check_response : <dimensioni-report>.contains('2d') ? check_response : <filtro-report-formato>.contains('json') ? dataOggi : "Data"

	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	Then match karate.toString(response) contains check_response
	Then match karate.toString(response) contains check_response_dimensioni
	* def response_qualsiasi = response
	
	# non esistono errori in fruizione, per quello viene indicato sempre 'erogazione' nei valori outline
	* eval filtro.tipo = <filtro-tipo-fruizione>
	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	Then match karate.toString(response) contains check_response
	Then match karate.toString(response) contains check_response_dimensioni
	* def response_fruizione = response
	
	* eval filtro.tipo = <filtro-tipo-erogazione>
	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	Then match karate.toString(response) contains check_response
	Then match karate.toString(response) contains check_response_dimensioni
	* def response_erogazione = response
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-qualsiasi | filtro-tipo-fruizione | filtro-tipo-erogazione | filtro-report-formato | filtro-report-tipo | filtro-report-informazione | stringa-verifica | dimensioni-report |
| 'tempo-medio-risposta-distribuzione-errori-csv (verifica 2d)' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '2d' |
| 'tempo-medio-risposta-distribuzione-errori-csv' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-csv' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-csv' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-api-csv' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-azione-csv' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-csv' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-csv' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-csv' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-subject-csv' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-client-id-pdnd-csv' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-csv' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'csv' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-errori-xls (verifica 2d)' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '2d' |
| 'tempo-medio-risposta-distribuzione-errori-xls' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-xls' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-xls' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-api-xls' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-azione-xls' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-xls' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-xls' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-xls' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-subject-xls' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-client-id-pdnd-xls' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-xls' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xls' | 'table' | 'tempo_medio_risposta' | "Latenza Media" | '3d' |
| 'tempo-medio-risposta-distribuzione-errori-pdf (verifica 2d)' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' | "PDF-1.4" | '2d' |
| 'tempo-medio-risposta-distribuzione-errori-pdf' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' | "PDF-1.4" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-pdf' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' | "PDF-1.4" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-pdf' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' | "PDF-1.4" | '3d' |
| 'tempo-medio-risposta-distribuzione-api-pdf' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' | "PDF-1.4" | '3d' |
| 'tempo-medio-risposta-distribuzione-azione-pdf' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' | "PDF-1.4" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-pdf' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' | "PDF-1.4" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-pdf' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' | "PDF-1.4" | '3d' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-pdf' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' | "PDF-1.4" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-subject-pdf' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' | "PDF-1.4" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-client-id-pdnd-pdf' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' | "PDF-1.4" | '3d' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-pdf' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'pdf' | 'table' | 'tempo_medio_risposta' | "PDF-1.4" | '3d' |
| 'tempo-medio-risposta-distribuzione-errori-xml' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-xml' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-xml' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-api-xml' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-azione-xml' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-xml' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-xml' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-xml' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-subject-xml' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-client-id-pdnd-xml' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-xml' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-errori-json (verifica 2d)' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '2d' |
| 'tempo-medio-risposta-distribuzione-errori-json' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-json' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-json' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-api-json' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-azione-json' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-json' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-json' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-json' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-subject-json' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-client-id-pdnd-json' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-json' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'table' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-errori-xml-line' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'xml' | 'line' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-errori-json-line (verifica 2d)' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'json' | 'line' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '2d' |
| 'tempo-medio-risposta-distribuzione-errori-json-line' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'json' | 'line' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-xml-pie' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-xml-pie' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-api-xml-pie' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-azione-xml-pie' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-xml-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-xml-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-xml-pie' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-subject-xml-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-client-id-pdnd-xml-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-xml-pie' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-json-pie' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-json-pie' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-api-json-pie' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-azione-json-pie' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-json-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-json-pie' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-json-pie' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-subject-json-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-client-id-pdnd-json-pie' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-json-pie' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'pie' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-errori-xml-bar' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-xml-bar' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-xml-bar' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-api-xml-bar' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-azione-xml-bar' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-xml-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-xml-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-xml-bar' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-subject-xml-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-client-id-pdnd-xml-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-xml-bar' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'xml' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-errori-json-bar (verifica 2d)' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '2d' |
| 'tempo-medio-risposta-distribuzione-errori-json-bar' | 'distribuzione-errori' | { "tipo": "qualsiasi", "escludi_scartate": false } | null | 'qualsiasi' | 'erogazione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-remoto-json-bar' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-soggetto-locale-json-bar' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-api-json-bar' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-azione-json-bar' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-trasporto-json-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-applicativo-token-json-bar' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-id-autenticato-json-bar' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-subject-json-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-token-info-client-id-pdnd-json-bar' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |
| 'tempo-medio-risposta-distribuzione-indirizzo-ip-json-bar' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'json' | 'bar' | 'tempo_medio_risposta' | "Tempo Medio Risposta" | '3d' |



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
      },
      "dimensioni": <dimensioni-report>
    } 
})
"""  

* eval if(<filtro-esito> != null) filtro.esito = <filtro-esito>
* eval if(<filtro-claim> != null) filtro.claim = <filtro-claim>
* eval if(<filtro-tipo-identificazione-applicativo> != null) filtro.tipo_identificazione_applicativo = <filtro-tipo-identificazione-applicativo>
    
* def check_response = <path-distribuzione>.contains('distribuzione-esiti') ? "Ok" : "Numero Transazioni"

* def check_response_dimensioni = <dimensioni-report>.contains('3d') ?  "Data" : check_response

* def data_oggi = 31/01/24
* def today = Java.type('java.time.LocalDate').now()
* def dataOggi = today.format(java.time.format.DateTimeFormatter.ofPattern('dd/MM/yy'))

* def check_response_dimensioni = <dimensioni-report>.contains('2d') ? check_response : dataOggi


	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	Then match karate.toString(response) contains check_response
	Then match karate.toString(response) contains check_response_dimensioni
	* def response_qualsiasi = response
	
	* eval filtro.tipo = <filtro-tipo-fruizione>
	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	Then match karate.toString(response) contains check_response
	Then match karate.toString(response) contains check_response_dimensioni
	* def response_fruizione = response
	
	* eval filtro.tipo = <filtro-tipo-erogazione>
	Given path <path-distribuzione>
	And request filtro
	When method post
	Then status 200
	Then match karate.toString(response) contains check_response
	Then match karate.toString(response) contains check_response_dimensioni
	* def response_erogazione = response
    
  * def sommaQualsiasi = sommaRisultatiGraficoJSON(response_qualsiasi.dati)
  * def sommaFruizione = sommaRisultatiGraficoJSON(response_fruizione.dati)
  * def sommaErogazione = sommaRisultatiGraficoJSON(response_erogazione.dati)
  * match  sommaQualsiasi == ( sommaFruizione + sommaErogazione)
    
    
Examples:
| nome-statistica | path-distribuzione | filtro-esito | filtro-claim | filtro-tipo-qualsiasi | filtro-tipo-fruizione | filtro-tipo-erogazione | filtro-tipo-identificazione-applicativo | dimensioni-report |
| 'distribuzione-errori (verifica 2d)' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null | '2d' |
| 'distribuzione-errori' | 'distribuzione-errori' | null | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null | '3d' |
| 'distribuzione-soggetto-remoto' | 'distribuzione-soggetto-remoto' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null | '3d' |
| 'distribuzione-soggetto-locale' | 'distribuzione-soggetto-locale' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null | '3d' |
| 'distribuzione-api' | 'distribuzione-api' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null | '3d' |
| 'distribuzione-azione' | 'distribuzione-azione' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null | '3d' |
| 'distribuzione-applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'trasporto' | '3d' |
| 'distribuzione-applicativo-token' | 'distribuzione-applicativo' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | 'token' | '3d' |
| 'distribuzione-id-autenticato' | 'distribuzione-id-autenticato' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null | '3d' |
| 'distribuzione-token-info-subject' | 'distribuzione-token-info' | { "tipo": "ok" } | "subject" | 'qualsiasi' | 'fruizione' | 'erogazione' | null | '3d' |
| 'distribuzione-token-info-client-id' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id" | 'qualsiasi' | 'fruizione' | 'erogazione' | null | '3d' |
| 'distribuzione-token-info-client-id-pdnd' | 'distribuzione-token-info' | { "tipo": "ok" } | "client_id_pdnd_informazioni" | 'qualsiasi' | 'fruizione' | 'erogazione' | null | '3d' |
| 'distribuzione-indirizzo-ip' | 'distribuzione-indirizzo-ip' | { "tipo": "ok" } | null | 'qualsiasi' | 'fruizione' | 'erogazione' | null | '3d' |


