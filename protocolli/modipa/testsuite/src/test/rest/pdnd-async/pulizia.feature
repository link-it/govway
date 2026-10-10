Feature: Scambi di dati asincroni PDND - eliminazione delle interazioni scadute tramite il timer

# Il timer elimina le interazioni la cui scadenza è trascorsa da più del periodo di conservazione.
# Richiede in govway_local.properties i tempi ridotti indicati nel RUN.README:
#   org.openspcoop2.pdd.gestoreInterazioniAsincronePDND.timer.intervalloSecondi=5
#   org.openspcoop2.pdd.gestoreInterazioniAsincronePDND.conservazioneSecondi=20
# L'eliminazione non dipende dal tipo di API: viene verificata solamente su API REST.

Background:
    * def utils = 'classpath:test/rest/pdnd-async/pdnd-async-utils.feature'
    * def interazione = read('classpath:utils/pdnd_async_interazione.js')
    * def transazione = read('classpath:utils/pdnd_async_transazione.js')
    * def sleep = function(ms){ java.lang.Thread.sleep(ms) }
    # attende (al massimo 60 secondi) che l'interazione venga eliminata da entrambi i lati
    * def attendiEliminazione =
    """
    function(id) {
        for (var i = 0; i < 30; i++) {
            if (interazione(id, 'applicativa') == null && interazione(id, 'delegata') == null) {
                return true;
            }
            java.lang.Thread.sleep(2000);
        }
        return false;
    }
    """


@pulizia-interazioni-scadute
Scenario: le interazioni scadute e confermate vengono eliminate al termine del periodo di conservazione, le altre vengono mantenute

    # interazione scaduta: tempo massimo di risposta di 3 secondi senza callback
    * def startScaduta = call read(utils + '@start') { servizio: 'PDNDAsyncRestScadenze' }
    * match transazione(startScaduta.tid).tipo_servizio_correlato == 'start_interaction'
    * def idScaduta = startScaduta.conversationId

    # interazione confermata: la scadenza coincide con la conferma di ricezione
    * def startConfermata = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta' }
    * match transazione(startConfermata.tid).tipo_servizio_correlato == 'start_interaction'
    * def idConfermata = startConfermata.conversationId
    * def esitoChiamata = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestCompattaCallback', conversationId: '#(idConfermata)' }
    * match transazione(esitoChiamata.tid).tipo_servizio_correlato == 'callback_invocation'
    * def esitoChiamata = call read(utils + '@getResource') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(idConfermata)' }
    * match transazione(esitoChiamata.tid).tipo_servizio_correlato == 'get_resource'
    * def esitoChiamata = call read(utils + '@confirmation') { servizio: 'PDNDAsyncRestCompatta', conversationId: '#(idConfermata)' }
    * match transazione(esitoChiamata.tid).tipo_servizio_correlato == 'confirmation'

    # interazione in corso (tempo massimo di risposta di 300 secondi): non deve essere eliminata
    * def startAttiva = call read(utils + '@start') { servizio: 'PDNDAsyncRestCompatta' }
    * match transazione(startAttiva.tid).tipo_servizio_correlato == 'start_interaction'
    * def idAttiva = startAttiva.conversationId

    # durante il periodo di conservazione l'interazione scaduta è ancora presente e le richieste ricevono un errore di scadenza
    * sleep(4500)
    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestScadenzeCallback', conversationId: '#(idScaduta)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionExpired'
    * def erogatore = interazione(idScaduta, 'applicativa')
    * match erogatore != null
    * def fruitore = interazione(idConfermata, 'delegata')
    * match fruitore != null

    # al termine del periodo di conservazione il timer elimina le interazioni scadute e quelle confermate
    * match attendiEliminazione(idScaduta) == true
    * match attendiEliminazione(idConfermata) == true
    # una richiesta per l'interazione eliminata non trova più l'interazione
    * def r = call read(utils + '@callback') { servizioCallback: 'PDNDAsyncRestScadenzeCallback', conversationId: '#(idScaduta)', statusAtteso: 400 }
    * match transazione(r.tid).tipo_servizio_correlato == 'callback_invocation'
    * match r.response.title == 'AsyncInteractionNotFound'

    # l'interazione in corso è ancora presente da entrambi i lati
    * def erogatore = interazione(idAttiva, 'applicativa')
    * match erogatore.fase == 'start_interaction'
    * def fruitore = interazione(idAttiva, 'delegata')
    * match fruitore.fase == 'start_interaction'
