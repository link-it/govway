Feature: Scambi di dati asincroni PDND nella configurazione ModI delle erogazioni (modi_scambio_asincrono)

# Gli scenari vengono eseguiti sia per API REST sia per API SOAP

Background:

* call read('classpath:crud_commons.feature')

* def query_param_profilo_modi = {'profilo': 'ModI'}
* def SERVIZIO = 'Servizio'

* def modiApi =
"""
function(protocollo, scambioAsincrono) {
	var sm = { "pattern": "auth01", "generazione_token": "pdnd", "applicabilita": "richiesta", "scambio_asincrono": scambioAsincrono != null };
	if (protocollo == 'rest') sm.rest_header = "bearer";
	var m = { "sicurezza_canale": { "pattern": "auth01" }, "sicurezza_messaggio": sm };
	if (scambioAsincrono != null) m.scambio_asincrono = scambioAsincrono;
	return m;
}
"""

# crea l'API con le operazioni indicate ([nome, fase]): risorse per REST, azioni di un servizio per SOAP
* def creaApi =
"""
function(nome, protocollo, modi, operazioni, versione, servizio) {
	var v = versione ? versione : 1;
	var s = servizio ? servizio : SERVIZIO;
	var tipo = protocollo == 'soap' ? { "protocollo": "soap", "formato": "Wsdl1.1" } : { "protocollo": "rest", "formato": "OpenApi3.0" };
	if (modi != null) {
		karate.call('classpath:create_stub.feature', { resourcePath: 'api', body: { "referente": soggettoDefault, "tipo_interfaccia": tipo, "nome": nome, "versione": v, "modi": modi }, query_params: query_param_profilo_modi });
	}
	var path = 'api/' + nome + '/' + v + '/risorse';
	if (protocollo == 'soap') {
		karate.call('classpath:create_stub.feature', { resourcePath: 'api/' + nome + '/' + v + '/servizi', body: { "nome": s, "profilo_collaborazione": "sincrono" }, query_params: query_param_profilo_modi });
		path = 'api/' + nome + '/' + v + '/servizi/' + s + '/azioni';
	}
	for (var i = 0; i < operazioni.length; i++) {
		var o = protocollo == 'soap' ?
			{ "nome": operazioni[i][0], "profilo_ridefinito": false, "modi": { "interazione": { "pattern": "bloccante" }, "sicurezza_messaggio": { "stato": "api" } } } :
			{ "http_method": "POST", "path": "/" + operazioni[i][0], "nome": operazioni[i][0], "modi": { "interazione": { "pattern": "crud" }, "sicurezza_messaggio": { "stato": "api" } } };
		if (operazioni[i][1] != null) o.modi.fase_asincrona = operazioni[i][1];
		karate.call('classpath:create_stub.feature', { resourcePath: path, body: o, query_params: query_param_profilo_modi });
	}
}
"""

* def erogazione =
"""
function(protocollo, apiNome, erogazioneNome, scambioAsincrono) {
	var e = { "api_nome": apiNome, "api_versione": 1, "erogazione_nome": erogazioneNome, "connettore": { "endpoint": "https://ginovadifretta.it/petstore" }, "modi": { "protocollo": protocollo } };
	if (protocollo == 'soap') e.api_soap_servizio = SERVIZIO;
	if (scambioAsincrono != null) e.modi_scambio_asincrono = scambioAsincrono;
	return e;
}
"""

* def modiErogazione =
"""
function(protocollo, scambioAsincrono) {
	return { "modi": { "protocollo": protocollo }, "modi_scambio_asincrono": scambioAsincrono };
}
"""

* def leggiModi =
"""
function(nomeErogazione) {
	return karate.call('classpath:get_stub.feature', { resourcePath: 'erogazioni', key: nomeErogazione + '/1/modi', query_params: query_param_profilo_modi }).response;
}
"""

* def pulizia =
"""
function(erogazioni, api) {
	for (var i = 0; i < erogazioni.length; i++) {
		karate.call('classpath:delete_stub.feature', { resourcePath: 'erogazioni/' + erogazioni[i] + '/1', query_params: query_param_profilo_modi });
	}
	for (var j = 0; j < api.length; j++) {
		// nome dell'API, oppure nome/versione per una versione diversa dalla 1
		karate.call('classpath:delete_stub.feature', { resourcePath: 'api/' + (api[j].indexOf('/') > 0 ? api[j] : api[j] + '/1'), query_params: query_param_profilo_modi });
	}
}
"""

* def erogazioneDati = { "ruolo": "erogazione_dati", "tempo_massimo_risposta": 600, "durata_disponibilita": 600, "conferma_recupero": true, "numero_massimo_risultati": 10 }
* def fasiEService = [ ['startInteraction', 'start_interaction'], ['getResource', 'get_resource'], ['confirmation', 'confirmation'] ]


@ErogazioneScambioAsincrono
Scenario Outline: Erogazione di un'API <protocollo> 'erogazione dati': valori di default, aggiornamento e creazione con la configurazione dello scambio asincrono

	* def nomeEService = 'PDNDAsync' + random()
	* eval creaApi(nomeEService, '<protocollo>', modiApi('<protocollo>', erogazioneDati), fasiEService)

	# senza configurazione: valori di default
	* call create ({ resourcePath: 'erogazioni', body: erogazione('<protocollo>', nomeEService, nomeEService, null), query_params: query_param_profilo_modi })
	* def letta = leggiModi(nomeEService)
	* match letta.modi_scambio_asincrono == { "verifica_url_callback": false }

	# aggiornamento
	* def configurazione = { "verifica_url_callback": true, "codifica_header_url_callback": "base64", "codici_http_esito_positivo": "200,202-204" }
	* call put ({ resourcePath: 'erogazioni/' + nomeEService + '/1/modi', body: modiErogazione('<protocollo>', configurazione), query_params: query_param_profilo_modi })
	* def letta = leggiModi(nomeEService)
	* match letta.modi_scambio_asincrono == configurazione

	# codici HTTP non validi
	* call update_400 ({ resourcePath: 'erogazioni/' + nomeEService + '/1/modi', body: modiErogazione('<protocollo>', { "codici_http_esito_positivo": "abc" }), query_params: query_param_profilo_modi })
	* match response.detail contains "Il valore 'abc' indicato per 'Codici HTTP' non è valido"

	# ritorno ai valori di default
	* call put ({ resourcePath: 'erogazioni/' + nomeEService + '/1/modi', body: modiErogazione('<protocollo>', {}), query_params: query_param_profilo_modi })
	* def letta = leggiModi(nomeEService)
	* match letta.modi_scambio_asincrono == { "verifica_url_callback": false }

	# creazione con la configurazione
	* def nomeErogazione2 = nomeEService + '2'
	* def configurazione2 = { "verifica_url_callback": true, "codifica_header_url_callback": "hex" }
	* call create ({ resourcePath: 'erogazioni', body: erogazione('<protocollo>', nomeEService, nomeErogazione2, configurazione2), query_params: query_param_profilo_modi })
	* def letta = leggiModi(nomeErogazione2)
	* match letta.modi_scambio_asincrono == configurazione2

	* eval pulizia([nomeEService, nomeErogazione2], [nomeEService])

Examples:
| protocollo |
| rest |
| soap |


@ErogazioneScambioAsincronoCallback
Scenario Outline: Erogazione di un'API di callback <protocollo>: solamente i codici HTTP di esito positivo

	* def nomeEService = 'PDNDAsync' + random()
	* def nomeCallback = nomeEService + 'Callback'
	* eval creaApi(nomeEService, '<protocollo>', modiApi('<protocollo>', erogazioneDati), fasiEService)
	* eval creaApi(nomeCallback, '<protocollo>', modiApi('<protocollo>', { "ruolo": "callback", "api_erogazione_dati": { "api_nome": nomeEService, "api_versione": 1 } }), [ ['callbackInvocation', null] ])

	* call create_400 ({ resourcePath: 'erogazioni', body: erogazione('<protocollo>', nomeCallback, nomeCallback, { "verifica_url_callback": true }), query_params: query_param_profilo_modi })
	* match response.detail == "Il campo 'modi_scambio_asincrono.verifica_url_callback' non è previsto per un'API con ruolo 'callback'"
	* call create_400 ({ resourcePath: 'erogazioni', body: erogazione('<protocollo>', nomeCallback, nomeCallback, { "codifica_header_url_callback": "hex" }), query_params: query_param_profilo_modi })
	* match response.detail == "Il campo 'modi_scambio_asincrono.codifica_header_url_callback' non è previsto per un'API con ruolo 'callback'"

	# senza configurazione: nessuna informazione specifica
	* call create ({ resourcePath: 'erogazioni', body: erogazione('<protocollo>', nomeCallback, nomeCallback, null), query_params: query_param_profilo_modi })
	* def letta = leggiModi(nomeCallback)
	* match letta.modi_scambio_asincrono == {}
	* call put ({ resourcePath: 'erogazioni/' + nomeCallback + '/1/modi', body: modiErogazione('<protocollo>', { "codici_http_esito_positivo": "200" }), query_params: query_param_profilo_modi })
	* def letta = leggiModi(nomeCallback)
	* match letta.modi_scambio_asincrono == { "codici_http_esito_positivo": "200" }

	* eval pulizia([nomeCallback], [nomeCallback, nomeEService])

Examples:
| protocollo |
| rest |
| soap |


@ErogazioneScambioAsincronoNonPrevisto
Scenario Outline: Erogazione di un'API <protocollo> non configurata per gli scambi di dati asincroni

	* def nomeApi = 'PDNDAsync' + random()
	* eval creaApi(nomeApi, '<protocollo>', modiApi('<protocollo>', null), [ ['test', null] ])

	* call create_400 ({ resourcePath: 'erogazioni', body: erogazione('<protocollo>', nomeApi, nomeApi, { "verifica_url_callback": true }), query_params: query_param_profilo_modi })
	* match response.detail == "La configurazione 'modi_scambio_asincrono' non è prevista: l'API implementata non è configurata per gli scambi di dati asincroni"

	* call create ({ resourcePath: 'erogazioni', body: erogazione('<protocollo>', nomeApi, nomeApi, null), query_params: query_param_profilo_modi })
	* def letta = leggiModi(nomeApi)
	* match letta.modi_scambio_asincrono == '#notpresent'
	* call update_400 ({ resourcePath: 'erogazioni/' + nomeApi + '/1/modi', body: modiErogazione('<protocollo>', {}), query_params: query_param_profilo_modi })

	* eval pulizia([nomeApi], [nomeApi])

Examples:
| protocollo |
| rest |
| soap |

@ErogazioneScambioAsincronoApiIncompleta
Scenario Outline: Erogazione di un'API <protocollo> 'erogazione dati' con fasi non associate alle operazioni: API non utilizzabile

	* def nomeEService = 'PDNDAsync' + random()
	* eval creaApi(nomeEService, '<protocollo>', modiApi('<protocollo>', erogazioneDati), [ ['startInteraction', 'start_interaction'] ])
	* call create_400 ({ resourcePath: 'erogazioni', body: erogazione('<protocollo>', nomeEService, nomeEService, null), query_params: query_param_profilo_modi })
	* match response.detail contains "L'API '"
	* match response.detail contains nomeEService
	* match response.detail contains "' non è utilizzabile: Scambio di dati asincrono"

	* eval pulizia([], [nomeEService])

Examples:
| protocollo |
| rest |
| soap |


@ErogazioneScambioAsincronoServizioIncompleto
Scenario: Erogazione di un servizio SOAP incompleto di un'API con un servizio completo: solamente il servizio completo è utilizzabile

	* def nomeEService = 'PDNDAsync' + random()
	* eval creaApi(nomeEService, 'soap', modiApi('soap', erogazioneDati), fasiEService)
	* eval creaApi(nomeEService, 'soap', null, [ ['startInteraction2', 'start_interaction'] ], 1, 'ServizioIncompleto')

	* def erogazioneIncompleta = erogazione('soap', nomeEService, nomeEService + 'Incompleto', null)
	* eval erogazioneIncompleta.api_soap_servizio = 'ServizioIncompleto'
	* call create_400 ({ resourcePath: 'erogazioni', body: erogazioneIncompleta, query_params: query_param_profilo_modi })
	* match response.detail contains "Il servizio 'ServizioIncompleto' dell'API '"
	* match response.detail contains "' non è utilizzabile: Scambio di dati asincrono"

	* call create ({ resourcePath: 'erogazioni', body: erogazione('soap', nomeEService, nomeEService, null), query_params: query_param_profilo_modi })

	* eval pulizia([nomeEService], [nomeEService])


@ErogazioneScambioAsincronoCambioVersione
Scenario Outline: Cambio di versione dell'API <protocollo> implementata da un'erogazione verso una versione con fasi non associate

	* def nomeEService = 'PDNDAsync' + random()
	* eval creaApi(nomeEService, '<protocollo>', modiApi('<protocollo>', erogazioneDati), fasiEService)
	* eval creaApi(nomeEService, '<protocollo>', modiApi('<protocollo>', erogazioneDati), [ ['startInteraction', 'start_interaction'] ], 2)
	* call create ({ resourcePath: 'erogazioni', body: erogazione('<protocollo>', nomeEService, nomeEService, null), query_params: query_param_profilo_modi })

	* call update_400 ({ resourcePath: 'erogazioni/' + nomeEService + '/1/api', body: { "api_versione": 2 }, query_params: query_param_profilo_modi })
	* match response.detail contains "' non è utilizzabile: Scambio di dati asincrono"

	* eval pulizia([nomeEService], [nomeEService, nomeEService + '/2'])

Examples:
| protocollo |
| rest |
| soap |
