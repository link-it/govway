Feature: Scambi di dati asincroni PDND nella configurazione ModI delle fruizioni (modi_scambio_asincrono)

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

# soggetto erogatore delle fruizioni, con profilo ModI
* def erogatore = read('soggetto_erogatore.json')
* eval randomize (erogatore, ["nome", "credenziali.username"])

* def fruizione =
"""
function(protocollo, apiNome, scambioAsincrono) {
	var f = { "api_nome": apiNome, "api_versione": 1, "fruizione_nome": apiNome, "erogatore": erogatore.nome,
		"connettore": { "endpoint": "https://ginovadifretta.it/petstore", "token_policy": "api-config-test-jwt" },
		"modi": { "protocollo": "oauth" } };
	if (protocollo == 'soap') f.api_soap_servizio = SERVIZIO;
	if (scambioAsincrono != null) f.modi_scambio_asincrono = scambioAsincrono;
	return f;
}
"""

* def modiFruizione =
"""
function(scambioAsincrono) {
	return { "modi": { "protocollo": "oauth" }, "modi_scambio_asincrono": scambioAsincrono };
}
"""

* def leggiModi =
"""
function(nomeFruizione) {
	return karate.call('classpath:get_stub.feature', { resourcePath: 'fruizioni', key: erogatore.nome + '/' + nomeFruizione + '/1/modi', query_params: query_param_profilo_modi }).response;
}
"""

* def pulizia =
"""
function(fruizioni, api) {
	for (var i = 0; i < fruizioni.length; i++) {
		karate.call('classpath:delete_stub.feature', { resourcePath: 'fruizioni/' + erogatore.nome + '/' + fruizioni[i] + '/1', query_params: query_param_profilo_modi });
	}
	karate.call('classpath:delete_stub.feature', { resourcePath: 'soggetti/' + erogatore.nome, query_params: query_param_profilo_modi });
	for (var j = 0; j < api.length; j++) {
		// nome dell'API, oppure nome/versione per una versione diversa dalla 1
		karate.call('classpath:delete_stub.feature', { resourcePath: 'api/' + (api[j].indexOf('/') > 0 ? api[j] : api[j] + '/1'), query_params: query_param_profilo_modi });
	}
}
"""

* def erogazioneDati = { "ruolo": "erogazione_dati", "tempo_massimo_risposta": 600, "durata_disponibilita": 600, "conferma_recupero": true, "numero_massimo_risultati": 10 }
* def fasiEService = [ ['startInteraction', 'start_interaction'], ['getResource', 'get_resource'], ['confirmation', 'confirmation'] ]

* call create ({ resourcePath: 'soggetti', body: erogatore, query_params: query_param_profilo_modi })


@FruizioneScambioAsincrono
Scenario Outline: Fruizione di un'API <protocollo> 'erogazione dati': URL di callback, invio del purposeId, codici HTTP di esito positivo

	* def nomeEService = 'PDNDAsync' + random()
	* eval creaApi(nomeEService, '<protocollo>', modiApi('<protocollo>', erogazioneDati), fasiEService)

	# senza configurazione: URL dell'erogazione dell'API di callback
	* call create ({ resourcePath: 'fruizioni', body: fruizione('<protocollo>', nomeEService, null), query_params: query_param_profilo_modi })
	* def letta = leggiModi(nomeEService)
	* match letta.modi_scambio_asincrono == { "url_callback": { "sorgente": "erogazione" } }

	# URL di callback fornita dal client tramite parametro della URL: nome di default della modalità
	* def configurazione = { "url_callback": { "sorgente": "client", "modalita": "query", "codifica": "hex", "obbligatoria": true }, "invio_purpose_id": false }
	* call put ({ resourcePath: 'fruizioni/' + erogatore.nome + '/' + nomeEService + '/1/modi', body: modiFruizione(configurazione), query_params: query_param_profilo_modi })
	* def letta = leggiModi(nomeEService)
	* match letta.modi_scambio_asincrono == { "url_callback": { "sorgente": "client", "modalita": "query", "nome": "govway_pdnd_url_callback", "codifica": "hex", "obbligatoria": true }, "invio_purpose_id": false }

	# URL fornita dal client tramite header HTTP con nome personalizzato
	* def configurazione2 = { "url_callback": { "sorgente": "client", "modalita": "header", "nome": "X-Callback", "codifica": "base64", "obbligatoria": false }, "invio_purpose_id": true, "codici_http_esito_positivo": "200-299,303" }
	* call put ({ resourcePath: 'fruizioni/' + erogatore.nome + '/' + nomeEService + '/1/modi', body: modiFruizione(configurazione2), query_params: query_param_profilo_modi })
	* def letta = leggiModi(nomeEService)
	* match letta.modi_scambio_asincrono == configurazione2

	# informazioni non previste per il ruolo dell'API, nome dell'header e codici HTTP non validi
	* call update_400 ({ resourcePath: 'fruizioni/' + erogatore.nome + '/' + nomeEService + '/1/modi', body: modiFruizione({ "entity_number": { "modalita": "header" } }), query_params: query_param_profilo_modi })
	* match response.detail == "Il campo 'modi_scambio_asincrono.entity_number' non è previsto per un'API con ruolo 'erogazione_dati'"
	* call update_400 ({ resourcePath: 'fruizioni/' + erogatore.nome + '/' + nomeEService + '/1/modi', body: modiFruizione({ "url_callback": { "sorgente": "client", "nome": "nome non valido" } }), query_params: query_param_profilo_modi })
	* match response.detail contains "contiene caratteri non ammessi in un header HTTP"
	* call update_400 ({ resourcePath: 'fruizioni/' + erogatore.nome + '/' + nomeEService + '/1/modi', body: modiFruizione({ "codici_http_esito_positivo": "600" }), query_params: query_param_profilo_modi })
	* match response.detail contains "Il valore '600' indicato per 'Codici HTTP' non è valido"

	* eval pulizia([nomeEService], [nomeEService])

Examples:
| protocollo |
| rest |
| soap |


@FruizioneScambioAsincronoCallback
Scenario Outline: Fruizione di un'API di callback <protocollo>: modalità con cui il client fornisce il numero di entità

	* def nomeEService = 'PDNDAsync' + random()
	* def nomeCallback = nomeEService + 'Callback'
	* eval creaApi(nomeEService, '<protocollo>', modiApi('<protocollo>', erogazioneDati), fasiEService)
	* eval creaApi(nomeCallback, '<protocollo>', modiApi('<protocollo>', { "ruolo": "callback", "api_erogazione_dati": { "api_nome": nomeEService, "api_versione": 1 } }), [ ['callbackInvocation', null] ])

	* call create_400 ({ resourcePath: 'fruizioni', body: fruizione('<protocollo>', nomeCallback, { "url_callback": { "sorgente": "erogazione" } }), query_params: query_param_profilo_modi })
	* match response.detail == "Il campo 'modi_scambio_asincrono.url_callback' non è previsto per un'API con ruolo 'callback'"
	* call create_400 ({ resourcePath: 'fruizioni', body: fruizione('<protocollo>', nomeCallback, { "invio_purpose_id": true }), query_params: query_param_profilo_modi })
	* match response.detail == "Il campo 'modi_scambio_asincrono.invio_purpose_id' non è previsto per un'API con ruolo 'callback'"

	# senza configurazione: header HTTP con il nome di default
	* call create ({ resourcePath: 'fruizioni', body: fruizione('<protocollo>', nomeCallback, null), query_params: query_param_profilo_modi })
	* def letta = leggiModi(nomeCallback)
	* match letta.modi_scambio_asincrono == { "entity_number": { "modalita": "header", "nome": "GovWay-PDND-Entity-Number" } }

	# parametro della URL: nome di default della modalità
	* call put ({ resourcePath: 'fruizioni/' + erogatore.nome + '/' + nomeCallback + '/1/modi', body: modiFruizione({ "entity_number": { "modalita": "query" }, "codici_http_esito_positivo": "200" }), query_params: query_param_profilo_modi })
	* def letta = leggiModi(nomeCallback)
	* match letta.modi_scambio_asincrono == { "entity_number": { "modalita": "query", "nome": "govway_pdnd_entity_number" }, "codici_http_esito_positivo": "200" }

	# nome non valido
	* call update_400 ({ resourcePath: 'fruizioni/' + erogatore.nome + '/' + nomeCallback + '/1/modi', body: modiFruizione({ "entity_number": { "modalita": "header", "nome": "nome:non valido" } }), query_params: query_param_profilo_modi })
	* match response.detail contains "contiene caratteri non ammessi in un header HTTP"

	* eval pulizia([nomeCallback], [nomeCallback, nomeEService])

Examples:
| protocollo |
| rest |
| soap |


@FruizioneScambioAsincronoNonPrevisto
Scenario Outline: Fruizione di un'API <protocollo> non configurata per gli scambi di dati asincroni

	* def nomeApi = 'PDNDAsync' + random()
	* eval creaApi(nomeApi, '<protocollo>', modiApi('<protocollo>', null), [ ['test', null] ])

	* call create_400 ({ resourcePath: 'fruizioni', body: fruizione('<protocollo>', nomeApi, { "invio_purpose_id": true }), query_params: query_param_profilo_modi })
	* match response.detail == "La configurazione 'modi_scambio_asincrono' non è prevista: l'API implementata non è configurata per gli scambi di dati asincroni"

	* call create ({ resourcePath: 'fruizioni', body: fruizione('<protocollo>', nomeApi, null), query_params: query_param_profilo_modi })
	* def letta = leggiModi(nomeApi)
	* match letta.modi_scambio_asincrono == '#notpresent'
	* match letta.modi.keystore == { "modalita": "default" }

	* eval pulizia([nomeApi], [nomeApi])

Examples:
| protocollo |
| rest |
| soap |

@FruizioneScambioAsincronoApiIncompleta
Scenario Outline: Fruizione di un'API <protocollo> 'erogazione dati' con fasi non associate alle operazioni: API non utilizzabile

	* def nomeEService = 'PDNDAsync' + random()
	* eval creaApi(nomeEService, '<protocollo>', modiApi('<protocollo>', erogazioneDati), [ ['startInteraction', 'start_interaction'] ])
	* call create_400 ({ resourcePath: 'fruizioni', body: fruizione('<protocollo>', nomeEService, null), query_params: query_param_profilo_modi })
	* match response.detail contains "L'API '"
	* match response.detail contains nomeEService
	* match response.detail contains "' non è utilizzabile: Scambio di dati asincrono"

	* eval pulizia([], [nomeEService])

Examples:
| protocollo |
| rest |
| soap |


@FruizioneScambioAsincronoServizioIncompleto
Scenario: Fruizione di un servizio SOAP incompleto di un'API con un servizio completo: solamente il servizio completo è utilizzabile

	* def nomeEService = 'PDNDAsync' + random()
	* eval creaApi(nomeEService, 'soap', modiApi('soap', erogazioneDati), fasiEService)
	* eval creaApi(nomeEService, 'soap', null, [ ['startInteraction2', 'start_interaction'] ], 1, 'ServizioIncompleto')

	* def fruizioneIncompleta = fruizione('soap', nomeEService, null)
	* eval fruizioneIncompleta.api_soap_servizio = 'ServizioIncompleto'
	* eval fruizioneIncompleta.fruizione_nome = nomeEService + 'Incompleto'
	* call create_400 ({ resourcePath: 'fruizioni', body: fruizioneIncompleta, query_params: query_param_profilo_modi })
	* match response.detail contains "Il servizio 'ServizioIncompleto' dell'API '"
	* match response.detail contains "' non è utilizzabile: Scambio di dati asincrono"

	* call create ({ resourcePath: 'fruizioni', body: fruizione('soap', nomeEService, null), query_params: query_param_profilo_modi })

	* eval pulizia([nomeEService], [nomeEService])


@FruizioneScambioAsincronoCambioVersione
Scenario Outline: Cambio di versione dell'API <protocollo> implementata da una fruizione verso una versione con fasi non associate

	* def nomeEService = 'PDNDAsync' + random()
	* eval creaApi(nomeEService, '<protocollo>', modiApi('<protocollo>', erogazioneDati), fasiEService)
	* eval creaApi(nomeEService, '<protocollo>', modiApi('<protocollo>', erogazioneDati), [ ['startInteraction', 'start_interaction'] ], 2)
	* call create ({ resourcePath: 'fruizioni', body: fruizione('<protocollo>', nomeEService, null), query_params: query_param_profilo_modi })

	* call update_400 ({ resourcePath: 'fruizioni/' + erogatore.nome + '/' + nomeEService + '/1/api', body: { "api_versione": 2 }, query_params: query_param_profilo_modi })
	* match response.detail contains "' non è utilizzabile: Scambio di dati asincrono"

	* eval pulizia([nomeEService], [nomeEService, nomeEService + '/2'])

Examples:
| protocollo |
| rest |
| soap |
