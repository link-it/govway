Feature: Scambi di dati asincroni PDND nella configurazione ModI di API, risorse (REST) e azioni (SOAP)

# Gli scenari vengono eseguiti sia per API REST (fasi associate alle risorse) sia per API SOAP (fasi associate alle azioni di un servizio)

Background:

* call read('classpath:crud_commons.feature')

* def query_param_profilo_modi = {'profilo': 'ModI'}
* def SERVIZIO = 'Servizio'

* def modiApi =
"""
function(protocollo, scambioAsincrono, abilitato, generazioneToken) {
	var sm = { "pattern": "auth01", "generazione_token": generazioneToken ? generazioneToken : "pdnd", "applicabilita": "richiesta", "scambio_asincrono": abilitato };
	if (protocollo == 'rest') sm.rest_header = "bearer";
	var m = { "sicurezza_canale": { "pattern": "auth01" }, "sicurezza_messaggio": sm };
	if (scambioAsincrono != null) m.scambio_asincrono = scambioAsincrono;
	return m;
}
"""

* def api =
"""
function(nome, protocollo, modi) {
	var tipo = protocollo == 'soap' ? { "protocollo": "soap", "formato": "Wsdl1.1" } : { "protocollo": "rest", "formato": "OpenApi3.0" };
	return { "referente": soggettoDefault, "tipo_interfaccia": tipo, "nome": nome, "versione": 1, "modi": modi };
}
"""

# crea l'API e, per SOAP, il servizio a cui vengono aggiunte le azioni
* def creaApi =
"""
function(nome, protocollo, modi) {
	karate.call('classpath:create_stub.feature', { resourcePath: 'api', body: api(nome, protocollo, modi), query_params: query_param_profilo_modi });
	if (protocollo == 'soap') {
		karate.call('classpath:create_stub.feature', { resourcePath: 'api/' + nome + '/1/servizi', body: { "nome": SERVIZIO, "profilo_collaborazione": "sincrono" }, query_params: query_param_profilo_modi });
	}
}
"""

* def pathOperazioni =
"""
function(nomeApi, protocollo, servizio) {
	return protocollo == 'soap' ? 'api/' + nomeApi + '/1/servizi/' + (servizio ? servizio : SERVIZIO) + '/azioni' : 'api/' + nomeApi + '/1/risorse';
}
"""

* def operazione =
"""
function(protocollo, nome, fase, sicurezzaRidefinita) {
	var o = null;
	if (protocollo == 'soap') {
		o = { "nome": nome, "profilo_ridefinito": false, "modi": { "interazione": { "pattern": "bloccante" }, "sicurezza_messaggio": { "stato": "api" } } };
	}
	else {
		o = { "http_method": "POST", "path": "/" + nome, "nome": nome, "modi": { "interazione": { "pattern": "crud" }, "sicurezza_messaggio": { "stato": "api" } } };
	}
	if (fase != null) o.modi.fase_asincrona = fase;
	if (sicurezzaRidefinita != null) o.modi.sicurezza_messaggio = { "stato": "ridefinito", "configurazione": sicurezzaRidefinita };
	return o;
}
"""

* def creaOperazione =
"""
function(nomeApi, protocollo, nome, fase, servizio) {
	karate.call('classpath:create_stub.feature', { resourcePath: pathOperazioni(nomeApi, protocollo, servizio), body: operazione(protocollo, nome, fase, null), query_params: query_param_profilo_modi });
}
"""

* def leggi =
"""
function(path) {
	return karate.call('classpath:get_stub.feature', { resourcePath: 'api', key: path, query_params: query_param_profilo_modi }).response;
}
"""

* def elimina =
"""
function(nomi) {
	for (var i = 0; i < nomi.length; i++) {
		karate.call('classpath:delete_stub.feature', { resourcePath: 'api/' + nomi[i] + '/1', query_params: query_param_profilo_modi });
	}
}
"""

* def erogazioneDati = { "ruolo": "erogazione_dati", "tempo_massimo_risposta": 600, "durata_disponibilita": 1200, "conferma_recupero": true, "numero_massimo_risultati": 7 }
* def erogazioneDatiSenzaConferma = { "ruolo": "erogazione_dati", "tempo_massimo_risposta": 600, "durata_disponibilita": 1200, "conferma_recupero": false, "numero_massimo_risultati": 7 }
* def callbackDi = function(nomeApi) { return { "ruolo": "callback", "api_erogazione_dati": { "api_nome": nomeApi, "api_versione": 1 } } }


@ApiScambioAsincronoCompatta
Scenario Outline: API <protocollo> 'erogazione dati' con conferma e API di callback con un'unica operazione (fase implicita)

	* def nomeEService = 'PDNDAsync' + random()
	* def nomeCallback = nomeEService + 'Callback'

	* eval creaApi(nomeEService, '<protocollo>', modiApi('<protocollo>', erogazioneDati, true))
	* def letta = leggi(nomeEService + '/1/modi')
	* match letta.sicurezza_messaggio.scambio_asincrono == true
	* match letta.scambio_asincrono == erogazioneDati

	* eval creaApi(nomeCallback, '<protocollo>', modiApi('<protocollo>', callbackDi(nomeEService), true))
	* def letta = leggi(nomeCallback + '/1/modi')
	* match letta.scambio_asincrono == callbackDi(nomeEService)

	# fasi mancanti: stato 'error'
	* eval creaOperazione(nomeEService, '<protocollo>', 'startInteraction', 'start_interaction')
	* def stato = leggi(nomeEService + '/1')
	* match stato.stato == 'error'
	* match stato.stato_descrizione contains "associata alle fasi 'get_resource' e 'confirmation'"

	* eval creaOperazione(nomeEService, '<protocollo>', 'getResource', 'get_resource')
	* eval creaOperazione(nomeEService, '<protocollo>', 'confirmation', 'confirmation')
	* eval creaOperazione(nomeEService, '<protocollo>', 'catalog', null)
	* def letta = leggi(pathOperazioni(nomeEService, '<protocollo>').substring(4) + '/getResource')
	* match letta.modi.fase_asincrona == 'get_resource'
	* def letta = leggi(pathOperazioni(nomeEService, '<protocollo>').substring(4) + '/catalog')
	* match letta.modi.fase_asincrona == '#notpresent'
	* def stato = leggi(nomeEService + '/1')
	* match stato.stato == 'ok'

	# callback senza operazioni: stato 'error'; con un'unica operazione la fase è implicita
	* def stato = leggi(nomeCallback + '/1')
	* match stato.stato == 'error'
	* eval creaOperazione(nomeCallback, '<protocollo>', 'callbackInvocation', null)
	* def stato = leggi(nomeCallback + '/1')
	* match stato.stato == 'ok'

	* eval elimina([nomeCallback, nomeEService])

Examples:
| protocollo |
| rest |
| soap |


@ApiScambioAsincronoEstesa
Scenario Outline: API <protocollo> 'erogazione dati' senza conferma, operazioni senza fase e API di callback con fase esplicita

	* def nomeEService = 'PDNDAsync' + random()
	* def nomeCallback = nomeEService + 'Callback'

	* eval creaApi(nomeEService, '<protocollo>', modiApi('<protocollo>', erogazioneDatiSenzaConferma, true))
	* call create_400 ({ resourcePath: pathOperazioni(nomeEService, '<protocollo>'), body: operazione('<protocollo>', 'confirmation', 'confirmation', null), query_params: query_param_profilo_modi })
	* match response.detail == "La fase 'confirmation' richiede che nell'API sia abilitata l'opzione 'Conferma recupero risposta'"
	* eval creaOperazione(nomeEService, '<protocollo>', 'startInteraction', 'start_interaction')
	* eval creaOperazione(nomeEService, '<protocollo>', 'getResource', 'get_resource')
	* eval creaOperazione(nomeEService, '<protocollo>', 'catalog', null)
	* eval creaOperazione(nomeEService, '<protocollo>', 'health', null)
	* def stato = leggi(nomeEService + '/1')
	* match stato.stato == 'ok'

	* eval creaApi(nomeCallback, '<protocollo>', modiApi('<protocollo>', callbackDi(nomeEService), true))
	# più operazioni senza fase: la fase callback_invocation non è determinabile
	* eval creaOperazione(nomeCallback, '<protocollo>', 'notifyError', null)
	* eval creaOperazione(nomeCallback, '<protocollo>', 'health', null)
	* def stato = leggi(nomeCallback + '/1')
	* match stato.stato == 'error'
	# fase prevista solamente per l'API 'erogazione dati'
	* call create_400 ({ resourcePath: pathOperazioni(nomeCallback, '<protocollo>'), body: operazione('<protocollo>', 'getResource', 'get_resource', null), query_params: query_param_profilo_modi })
	* match response.detail contains "dello scambio asincrono non è compatibile con la configurazione ModI dell'API"
	# fase esplicita
	* eval creaOperazione(nomeCallback, '<protocollo>', 'callbackInvocation', 'callback_invocation')
	* def letta = leggi(pathOperazioni(nomeCallback, '<protocollo>').substring(4) + '/callbackInvocation')
	* match letta.modi.fase_asincrona == 'callback_invocation'
	* def stato = leggi(nomeCallback + '/1')
	* match stato.stato == 'ok'

	* eval elimina([nomeCallback, nomeEService])

Examples:
| protocollo |
| rest |
| soap |


@ApiScambioAsincronoServizioIncompleto
Scenario: API SOAP con un servizio completo e uno incompleto: stato 'warn'

	* def nomeEService = 'PDNDAsync' + random()
	* eval creaApi(nomeEService, 'soap', modiApi('soap', erogazioneDati, true))
	* eval creaOperazione(nomeEService, 'soap', 'startInteraction', 'start_interaction')
	* eval creaOperazione(nomeEService, 'soap', 'getResource', 'get_resource')
	* eval creaOperazione(nomeEService, 'soap', 'confirmation', 'confirmation')
	* call create ({ resourcePath: 'api/' + nomeEService + '/1/servizi', body: { "nome": "ServizioIncompleto", "profilo_collaborazione": "sincrono" }, query_params: query_param_profilo_modi })
	# la stessa fase può essere associata ad azioni di servizi differenti
	* eval creaOperazione(nomeEService, 'soap', 'startInteraction2', 'start_interaction', 'ServizioIncompleto')
	* def stato = leggi(nomeEService + '/1')
	* match stato.stato == 'warn'
	* match stato.stato_descrizione contains "nel servizio 'ServizioIncompleto' nessuna azione associata alle fasi 'get_resource' e 'confirmation' (servizio non utilizzabile)"

	* eval elimina([nomeEService])


@ApiScambioAsincrono400
Scenario Outline: API <protocollo> con configurazione dello scambio asincrono non valida: <descrizione>

	* def nomeApi = 'PDNDAsync' + random()
	* call create_400 ({ resourcePath: 'api', body: api(nomeApi, '<protocollo>', modiApi('<protocollo>', <sezione>, <abilitato>, '<token>')), query_params: query_param_profilo_modi })
	* match response.detail contains "<errore>"

Examples:
| protocollo | descrizione | sezione | abilitato | token | errore |
| rest | abilitazione senza sezione | null | true | pdnd | Lo scambio di dati asincrono abilitato nella sicurezza messaggio richiede la configurazione 'scambio_asincrono' |
| soap | abilitazione senza sezione | null | true | pdnd | Lo scambio di dati asincrono abilitato nella sicurezza messaggio richiede la configurazione 'scambio_asincrono' |
| rest | sezione senza abilitazione | erogazioneDati | false | pdnd | La configurazione 'scambio_asincrono' richiede che lo scambio di dati asincrono sia abilitato nella sicurezza messaggio |
| soap | sezione senza abilitazione | erogazioneDati | false | pdnd | La configurazione 'scambio_asincrono' richiede che lo scambio di dati asincrono sia abilitato nella sicurezza messaggio |
| rest | generazione token locale | erogazioneDati | true | locale | Lo scambio di dati asincrono richiede che nella sicurezza messaggio sia selezionata la generazione del token 'Authorization PDND' |
| soap | generazione token locale | erogazioneDati | true | locale | Lo scambio di dati asincrono richiede che nella sicurezza messaggio sia selezionata la generazione del token 'Authorization PDND' |
| rest | callback verso API inesistente | callbackDi('NonEsiste') | true | pdnd | indicata come API Erogazione Dati non esiste |
| soap | callback verso API inesistente | callbackDi('NonEsiste') | true | pdnd | indicata come API Erogazione Dati non esiste |


@ApiScambioAsincrono400Schema
Scenario Outline: API <protocollo> con valori non conformi allo schema: <descrizione>

	* def nomeApi = 'PDNDAsync' + random()
	* def sezione = karate.merge(erogazioneDati, <modifica>)
	* eval if (<rimuovi> != null) delete sezione[<rimuovi>]
	* call create_400 ({ resourcePath: 'api', body: api(nomeApi, '<protocollo>', modiApi('<protocollo>', sezione, true)), query_params: query_param_profilo_modi })

Examples:
| protocollo | descrizione | modifica | rimuovi |
| rest | tempo massimo di risposta pari a zero | ({ "tempo_massimo_risposta": 0 }) | null |
| soap | tempo massimo di risposta pari a zero | ({ "tempo_massimo_risposta": 0 }) | null |
| rest | durata della disponibilità negativa | ({ "durata_disponibilita": -1 }) | null |
| soap | durata della disponibilità negativa | ({ "durata_disponibilita": -1 }) | null |
| rest | numero massimo di risultati mancante | ({}) | 'numero_massimo_risultati' |
| soap | numero massimo di risultati mancante | ({}) | 'numero_massimo_risultati' |


@ApiScambioAsincronoTipoDifferente
Scenario Outline: API di callback <protocollo> che riferisce un'API 'erogazione dati' <altro>

	* def nomeEService = 'PDNDAsync' + random()
	* eval creaApi(nomeEService, '<altro>', modiApi('<altro>', erogazioneDati, true))
	* call create_400 ({ resourcePath: 'api', body: api(nomeEService + 'Callback', '<protocollo>', modiApi('<protocollo>', callbackDi(nomeEService), true)), query_params: query_param_profilo_modi })
	* match response.detail contains "indicata come API Erogazione Dati deve essere di tipo <tipo>"
	* eval elimina([nomeEService])

Examples:
| protocollo | altro | tipo |
| rest | soap | REST |
| soap | rest | SOAP |


@ApiScambioAsincronoVincoli
Scenario Outline: API <protocollo>: vincoli tra API, operazioni e API di callback e aggiornamento della configurazione

	* def nomeEService = 'PDNDAsync' + random()
	* def nomeCallback = nomeEService + 'Callback'
	* eval creaApi(nomeEService, '<protocollo>', modiApi('<protocollo>', erogazioneDati, true))
	* eval creaApi(nomeCallback, '<protocollo>', modiApi('<protocollo>', callbackDi(nomeEService), true))
	* eval creaOperazione(nomeEService, '<protocollo>', 'startInteraction', 'start_interaction')
	* eval creaOperazione(nomeEService, '<protocollo>', 'confirmation', 'confirmation')

	# relazione 1:1 tra API 'erogazione dati' e API di callback
	* call create_400 ({ resourcePath: 'api', body: api(nomeCallback + '2', '<protocollo>', modiApi('<protocollo>', callbackDi(nomeEService), true)), query_params: query_param_profilo_modi })
	* match response.detail contains "risulta già associata all'API di callback"

	# fase già associata ad un'altra operazione
	* call create_400 ({ resourcePath: pathOperazioni(nomeEService, '<protocollo>'), body: operazione('<protocollo>', 'startBis', 'start_interaction', null), query_params: query_param_profilo_modi })
	* match response.detail contains "La fase 'start_interaction' dello scambio asincrono risulta già associata"

	# fase non compatibile con il ruolo dell'API
	* call create_400 ({ resourcePath: pathOperazioni(nomeEService, '<protocollo>'), body: operazione('<protocollo>', 'notif', 'callback_invocation', null), query_params: query_param_profilo_modi })
	* match response.detail == "La fase 'callback_invocation' dello scambio asincrono non è compatibile con la configurazione ModI dell'API"

	# scambio asincrono nella sicurezza messaggio ridefinita di un'operazione
	* call create_400 ({ resourcePath: pathOperazioni(nomeEService, '<protocollo>'), body: operazione('<protocollo>', 'altra', null, modiApi('<protocollo>', null, true).sicurezza_messaggio), query_params: query_param_profilo_modi })
	* match response.detail == "Lo scambio di dati asincrono è configurabile solamente nella sicurezza messaggio dell'API"

	# disabilitazione della conferma o dello scambio asincrono con fasi associate
	* call update_400 ({ resourcePath: 'api/' + nomeEService + '/1/modi', body: modiApi('<protocollo>', erogazioneDatiSenzaConferma, true), query_params: query_param_profilo_modi })
	* match response.detail contains "prima di disabilitare l'opzione 'Conferma recupero risposta' è necessario eliminare l'associazione"
	* call update_400 ({ resourcePath: 'api/' + nomeEService + '/1/modi', body: modiApi('<protocollo>', null, false), query_params: query_param_profilo_modi })
	* match response.detail contains "prima di disabilitare lo scambio di dati asincrono è necessario eliminare l'associazione"

	# un'API riferita da un'API di callback non può diventare a sua volta un'API di callback
	* call update_400 ({ resourcePath: 'api/' + nomeEService + '/1/modi', body: modiApi('<protocollo>', callbackDi(nomeCallback), true), query_params: query_param_profilo_modi })

	# aggiornamento dei parametri dell'API
	* def aggiornata = karate.merge(erogazioneDati, { "tempo_massimo_risposta": 900, "numero_massimo_risultati": 3 })
	* call put ({ resourcePath: 'api/' + nomeEService + '/1/modi', body: modiApi('<protocollo>', aggiornata, true), query_params: query_param_profilo_modi })
	* def letta = leggi(nomeEService + '/1/modi')
	* match letta.scambio_asincrono == aggiornata

	* eval elimina([nomeCallback, nomeEService])

Examples:
| protocollo |
| rest |
| soap |

@ApiScambioAsincronoModificaOperazioni
Scenario Outline: API <protocollo>: modifica ed eliminazione delle operazioni associate alle fasi

	* def nomeEService = 'PDNDAsync' + random()
	* def path = pathOperazioni(nomeEService, '<protocollo>')
	* def key = path.substring(4)
	* eval creaApi(nomeEService, '<protocollo>', modiApi('<protocollo>', erogazioneDati, true))
	* eval creaOperazione(nomeEService, '<protocollo>', 'startInteraction', 'start_interaction')
	* eval creaOperazione(nomeEService, '<protocollo>', 'getResource', 'get_resource')
	* eval creaOperazione(nomeEService, '<protocollo>', 'catalog', null)
	* def stato = leggi(nomeEService + '/1')
	* match stato.stato == 'error'

	# fase assegnata in modifica ad un'operazione che non l'aveva
	* call put ({ resourcePath: path + '/catalog', body: operazione('<protocollo>', 'catalog', 'confirmation', null), query_params: query_param_profilo_modi })
	* def letta = leggi(key + '/catalog')
	* match letta.modi.fase_asincrona == 'confirmation'
	* def stato = leggi(nomeEService + '/1')
	* match stato.stato == 'ok'

	# fase già associata ad un'altra operazione
	* call update_400 ({ resourcePath: path + '/catalog', body: operazione('<protocollo>', 'catalog', 'get_resource', null), query_params: query_param_profilo_modi })
	* match response.detail contains "La fase 'get_resource' dello scambio asincrono risulta già associata"

	# fase non compatibile con il ruolo dell'API
	* call update_400 ({ resourcePath: path + '/catalog', body: operazione('<protocollo>', 'catalog', 'callback_invocation', null), query_params: query_param_profilo_modi })
	* match response.detail == "La fase 'callback_invocation' dello scambio asincrono non è compatibile con la configurazione ModI dell'API"

	# fase rimossa: l'API risulta nuovamente incompleta
	* call put ({ resourcePath: path + '/catalog', body: operazione('<protocollo>', 'catalog', null, null), query_params: query_param_profilo_modi })
	* def letta = leggi(key + '/catalog')
	* match letta.modi.fase_asincrona == '#notpresent'
	* def stato = leggi(nomeEService + '/1')
	* match stato.stato == 'error'
	* match stato.stato_descrizione contains "associata alla fase 'confirmation'"

	# spostamento di una fase: rimossa da un'operazione e assegnata ad un'altra
	* call put ({ resourcePath: path + '/getResource', body: operazione('<protocollo>', 'getResource', null, null), query_params: query_param_profilo_modi })
	* call put ({ resourcePath: path + '/catalog', body: operazione('<protocollo>', 'catalog', 'get_resource', null), query_params: query_param_profilo_modi })
	* def letta = leggi(key + '/catalog')
	* match letta.modi.fase_asincrona == 'get_resource'
	* call put ({ resourcePath: path + '/getResource', body: operazione('<protocollo>', 'getResource', 'confirmation', null), query_params: query_param_profilo_modi })
	* def stato = leggi(nomeEService + '/1')
	* match stato.stato == 'ok'

	# eliminazione di un'operazione associata ad una fase: l'API risulta incompleta
	* call delete ({ resourcePath: path + '/startInteraction', query_params: query_param_profilo_modi })
	* def stato = leggi(nomeEService + '/1')
	* match stato.stato == 'error'
	* match stato.stato_descrizione contains "associata alla fase 'start_interaction'"

	* eval elimina([nomeEService])

Examples:
| protocollo |
| rest |
| soap |
