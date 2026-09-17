@ignore
Feature: Esegue una distribuzione statistica in formato csv e ne restituisce il totale delle transazioni

# Feature di servizio, non contiene test.
#
# Parametri attesi tramite 'call':
#   path_distribuzione                 distribuzione da interrogare (es. 'distribuzione-azione')
#   data_inizio, data_fine             estremi dell'intervallo temporale
#   nome_servizio, versione_servizio   API su cui limitare il report, per isolare il conteggio
#                                      dal traffico generato dalle altre feature
#   unita_tempo                        opzionale: se non indicato il parametro non viene inviato,
#                                      per poter verificare il comportamento di default
#   esito                              opzionale: se non indicato il parametro non viene inviato
#   escludi_scartate                   opzionale: se non indicato il parametro non viene inviato
#   tipo_identificazione               richiesto dalla distribuzione per applicativo
#   claim                              richiesto dalla distribuzione per token info
#
# Restituisce:
#   csv       il report cosi' come prodotto dal servizio
#   totale    la somma di tutte le celle numeriche del report
#
# La somma considera ogni cella composta di sole cifre: le colonne che riportano le dimensioni
# della distribuzione (azione, API, soggetto, data, ...) non sono mai numeri interi, mentre le
# colonne con i conteggi lo sono sempre. In questo modo la stessa funzione vale sia per i report
# con una sola colonna di conteggio, sia per la distribuzione per esiti che ne espone tre.

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

* def query =
"""
({
    data_inizio: data_inizio,
    data_fine: data_fine,
    tipo: 'erogazione',
    nome_servizio: nome_servizio,
    versione_servizio: versione_servizio,
    formato_report: 'csv',
    tipo_report: 'table',
    tipo_informazione_report: 'numero_transazioni'
})
"""

* eval if (typeof unita_tempo != 'undefined' && unita_tempo != null) query.unita_tempo = unita_tempo
* eval if (typeof esito != 'undefined' && esito != null) query.esito = esito
* eval if (typeof escludi_scartate != 'undefined' && escludi_scartate != null) query.escludi_scartate = escludi_scartate
* eval if (typeof tipo_identificazione != 'undefined' && tipo_identificazione != null) query.tipo_identificazione = tipo_identificazione
* eval if (typeof claim != 'undefined' && claim != null) query.claim = claim

Given url reportisticaUrl
And path path_distribuzione
And params query
And header Authorization = govwayMonitorCred
When method get
# Il servizio di esportazione segnala con 404 un report privo di dati: per il chiamante equivale
# ad un totale nullo, ed e' il caso in cui un filtro non seleziona alcuna transazione
Then assert responseStatus == 200 || responseStatus == 404

* def csv = responseStatus == 200 ? karate.toString(response) : ''
* def totale = responseStatus == 200 ? totaleReportCsv(csv) : 0
