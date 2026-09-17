@ignore
Feature: Esegue una distribuzione statistica tramite ricerca full e ne restituisce il totale delle transazioni

# Feature di servizio, non contiene test. E' il gemello di report-distribuzione-totale, che interroga
# le stesse distribuzioni tramite la ricerca semplice: qui il filtro viene invece trasmesso nel body
# di una POST, percorso che nel layer REST passa da metodi distinti ma condivide con l'altro sia
# ReportisticaHelper.overrideFiltroEsito sia ReportExporter.
#
# Parametri attesi tramite 'call':
#   path_distribuzione                 distribuzione da interrogare (es. 'distribuzione-azione')
#   data_inizio, data_fine             estremi dell'intervallo temporale
#   nome_servizio, versione_servizio   API su cui limitare il report
#   filtro_api                         false per le distribuzioni che non prevedono il filtro sull'API
#                                      perche' ne costituisce la dimensione (distribuzione-api)
#   unita_tempo                        opzionale: se non indicato il campo non viene valorizzato
#   esito                              opzionale: tipo del filtro esito ('qualsiasi', 'ok', ...)
#   escludi_scartate                   opzionale
#   codici                             opzionale: elenco di codici, con esito 'personalizzato'
#   tipo_identificazione               richiesto dalla distribuzione per applicativo
#   claim                              richiesto dalla distribuzione per token info
#
# Restituisce:
#   csv       il report cosi' come prodotto dal servizio
#   totale    la somma di tutte le celle numeriche del report

Scenario:

* def totaleReportCsv =
"""
function(csv) {
    var righe = csv.split('\n');
    var totale = 0;
    for (var i = 1; i < righe.length; i++) {
        var riga = righe[i].replace('\r', '').trim();
        if (riga.length == 0) {
            continue;
        }
        var colonne = riga.split(',');
        for (var j = 0; j < colonne.length; j++) {
            var valore = colonne[j].trim();
            if (/^[0-9]+$/.test(valore)) {
                totale = totale + parseInt(valore, 10);
            }
        }
    }
    return totale;
}
"""

* def filtro =
"""
({
    intervallo_temporale: {
        data_inizio: data_inizio,
        data_fine: data_fine
    },
    tipo: 'erogazione',
    report: {
        formato: 'csv',
        tipo: 'table',
        tipo_informazione: {
            tipo: 'numero_transazioni'
        }
    }
})
"""

* eval if (typeof filtro_api == 'undefined' || filtro_api != false) filtro.api = ({ nome: nome_servizio, versione: versione_servizio })
* eval if (typeof unita_tempo != 'undefined' && unita_tempo != null) filtro.unita_tempo = unita_tempo
* eval if (typeof tipo_identificazione != 'undefined' && tipo_identificazione != null) filtro.tipo_identificazione_applicativo = tipo_identificazione
* eval if (typeof claim != 'undefined' && claim != null) filtro.claim = claim

* eval if (typeof esito != 'undefined' && esito != null) filtro.esito = ({ tipo: esito })
* eval if (typeof codici != 'undefined' && codici != null) filtro.esito = ({ tipo: 'personalizzato', codici: codici })
* eval if (typeof escludi_scartate != 'undefined' && escludi_scartate != null && filtro.esito != null) filtro.esito.escludi_scartate = escludi_scartate

Given url reportisticaUrl
And path path_distribuzione
And header Authorization = govwayMonitorCred
And request filtro
When method post
# Il servizio di esportazione segnala con 404 un report privo di dati: per il chiamante equivale
# ad un totale nullo, ed e' il caso in cui un filtro non seleziona alcuna transazione
Then assert responseStatus == 200 || responseStatus == 404

* def csv = responseStatus == 200 ? karate.toString(response) : ''
* def totale = responseStatus == 200 ? totaleReportCsv(csv) : 0
