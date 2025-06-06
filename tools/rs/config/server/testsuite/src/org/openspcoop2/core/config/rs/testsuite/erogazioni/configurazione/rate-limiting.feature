Feature: Configurazione Erogazioni Rate Limiting

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore = read('../api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault

* def erogazione_petstore = read('../erogazione_petstore.json')
* eval erogazione_petstore.api_nome = api_petstore.nome
* eval erogazione_petstore.api_versione = api_petstore.versione

* def petstore_key = erogazione_petstore.api_nome + '/' + erogazione_petstore.api_versione
* def erogazione_petstore_path = 'erogazioni/' + petstore_key
* def api_petstore_path = 'api/' + petstore_key

* def api_modi_rest = read('../api_modi_rest.json')
* eval randomize(api_modi_rest, ["nome"])
* eval api_modi_rest.referente = soggettoDefault

* def policy = read('classpath:bodies/rate-limiting-policy-erogazione.json')
* def policy_update = read('classpath:bodies/rate-limiting-policy-erogazione-update.json')

* def erogazione_modi_rest = read('../erogazione_modi_rest.json')
* eval erogazione_modi_rest.api_nome = api_modi_rest.nome
* eval erogazione_modi_rest.api_versione = api_modi_rest.versione

* def erogazione_modi_rest_key = erogazione_modi_rest.api_nome + '/' + erogazione_modi_rest.api_versione
* def erogazione_modi_rest_path = 'erogazioni/' + erogazione_modi_rest_key
* def api_modi_rest_path = 'api/' + erogazione_modi_rest_key

* def query_param_profilo_modi = {'profilo': 'ModI'}

* def policy_pdnd = read('classpath:bodies/rate-limiting-policy-erogazione-pdnd.json')
* def policy_update_pdnd = read('classpath:bodies/rate-limiting-policy-erogazione-update-pdnd.json')

* def policy_types = [ 'numero-richieste', 'occupazione-banda', 'tempo-medio-risposta', 'tempo-complessivo-risposta', 'numero-richieste-ok', 'numero-richieste-fallite', 'numero-fault-applicativi', 'numero-richieste-fallite-o-fault-applicativi', 'numero-richieste-ok-o-fault-applicativi' ]
* def policy_intervalli = [ 'minuti', 'orario', 'giornaliero' ]
* def build_data = 
    """
    function(policy_types, policy_body, policy_body_update, servizio_path) {
        var ret = [];
        for (var idx=0; idx < policy_types.length; idx++) {
	   for (var idy=0; idy < policy_intervalli.length; idy++) {
              ret.push({servizio_path: servizio_path, policy: policy_body, policy_update: policy_body_update, policy_type: policy_types[idx], policy_intervallo: policy_intervalli[idy] } )
	   }
        }

        return ret;
    }
    """

* def policy_dimensione_messaggio = read('classpath:bodies/rate-limiting-policy-erogazione_dimensione_messaggio.json')
* def policy_update_dimensione_messaggio = read('classpath:bodies/rate-limiting-policy-erogazione-update_dimensione_messaggio.json')
* def policy_types_dimensione_messaggio = [ 'dimensione-massima' ]
* def build_data_dimensione_messaggio = 
    """
    function(policy_types, policy_body, policy_body_update, servizio_path) {
        var ret = [];
        for (var idx=0; idx < policy_types.length; idx++) {
	   for (var idy=0; idy < policy_intervalli.length; idy++) {
              ret.push({servizio_path: servizio_path, policy: policy_body, policy_update: policy_body_update, policy_type: policy_types[idx] } )
	   }
        }

        return ret;
    }
    """

* def policy_types_modi = [ 'numero-richieste' ]
* def policy_intervalli_modi = [ 'minuti' ]
* def build_data_modi = 
    """
    function(policy_types_modi, policy_body, policy_body_update, servizio_path, query_params) {
        var ret = [];
        for (var idx=0; idx < policy_types_modi.length; idx++) {
	   for (var idy=0; idy < policy_intervalli_modi.length; idy++) {
              ret.push({servizio_path: servizio_path, policy: policy_body, policy_update: policy_body_update, policy_type: policy_types_modi[idx], policy_intervallo: policy_intervalli_modi[idy], query_params: query_params } )
	   }
        }

        return ret;
    }
    """

@RateLimitingAllPolicyTypes
Scenario: Configurazione Erogazioni Rate Limiting, tutti i tipi di policy vengono considerati (eccezione: dimensione massima messaggio non presente).
 
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    # Creo il soggetto su cui filtrare la policy di rate-limiting
    * def soggetto_http = read('classpath:bodies/soggetto-esterno-http.json')
    * eval randomize(soggetto_http, ["nome", "credenziali.username"])
    * eval soggetto_http.ruoli = [ ]
    * call create ({ resourcePath: 'soggetti', body: soggetto_http })

    * eval policy.filtro.soggetto_fruitore = soggetto_http.nome

    # Creo un applicativo da utilizzare nel raggruppamento
    * def applicativo = read('classpath:bodies/applicativo_http.json') 
    * eval randomize(applicativo, ["nome", "credenziali.username"])
    * call create ({ resourcePath: 'applicativi', body: applicativo })
    
    * eval policy.filtro.applicativo_fruitore = applicativo.nome
    
    * def data = build_data(policy_types, policy, policy_update, erogazione_petstore_path)
    * def conf = call read('classpath:servizi-configurazione/rate-limiting.feature') data

    * call delete ({ resourcePath: 'applicativi/' + applicativo.nome })
    * call delete ({ resourcePath: 'soggetti/' + soggetto_http.nome })
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@RateLimitingAllPolicyTypesPDNDGroupBy
Scenario: Configurazione Erogazioni Rate Limiting, tutti i tipi di policy vengono considerati (eccezione: dimensione massima messaggio non presente).
 
    * call create ({ resourcePath: 'api', body: api_modi_rest, query_params: query_param_profilo_modi })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_modi_rest, query_params: query_param_profilo_modi })
    
    * def data = build_data_modi(policy_types_modi, policy_pdnd, policy_update_pdnd, erogazione_modi_rest_path, query_param_profilo_modi )
    * def conf = call read('classpath:servizi-configurazione/rate-limiting.feature') data

    * call delete ({ resourcePath: erogazione_modi_rest_path, query_params: query_param_profilo_modi })
    * call delete ({ resourcePath: api_modi_rest_path, query_params: query_param_profilo_modi })

@RateLimitingPolicyDimensioneMessaggio
Scenario: Configurazione Erogazioni Rate Limiting, policy dimensione massima messaggio
 
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * def data = build_data_dimensione_messaggio(policy_types_dimensione_messaggio, policy_dimensione_messaggio, policy_update_dimensione_messaggio, erogazione_petstore_path)
    * def conf = call read('classpath:servizi-configurazione/rate-limiting-dimensione-messaggio.feature') data

    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })


@FiltroApplicativoFruitoreInesistente400
Scenario: Configurazione del Rate Limting filtrando per un applicativo inesistente

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    * eval policy.filtro.applicativo_fruitore = "ApplicativoInesistente"
    * eval randomize(policy, ["nome", "filtro.applicativo_fruitore"])

    Given url configUrl
    And path erogazione_petstore_path, 'configurazioni', 'rate-limiting'
    And header Authorization = govwayConfAuth
    And request policy
    And params query_params
    When method post
    Then status 400

    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })


@FiltroSoggettoFruitoreInesistente400
Scenario: Configurazione del Rate Limiting filtrando per un soggetto fruitore inesistente

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    * eval policy.filtro.soggetto_fruitore = "SoggettoInesistente"
    * eval randomize(policy, ["nome", "filtro.soggetto_fruitore"])

    Given url configUrl
    And path erogazione_petstore_path, 'configurazioni', 'rate-limiting'
    And header Authorization = govwayConfAuth
    And request policy
    And params query_params
    When method post
    Then status 400

    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@FiltroAzioneInesistente400
Scenario: Configurazione del Rate Limiting filtrando per un'azione inesistente

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    * eval policy.filtro.azione = ["AzioneInesistente"]
# la funzione randomize non preserva l'array, e quindi genera una richiesta errata:        * eval randomize(policy, ["nome", "filtro.azione"])

    Given url configUrl
    And path erogazione_petstore_path, 'configurazioni', 'rate-limiting'
    And header Authorization = govwayConfAuth
    And request policy
    And params query_params
    When method post
    Then status 400

    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@GroupByPDNDErogazioneTrasparente400
Scenario: Configurazione del Rate Limiting raggruppando per criteri PDND su un profilo trasparente

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    * eval policy.raggruppamento.token = ["pdnd_organization_name"]
# la funzione randomize non preserva l'array, e quindi genera una richiesta errata:        * eval randomize(policy, ["nome", "filtro.azione"])

    Given url configUrl
    And path erogazione_petstore_path, 'configurazioni', 'rate-limiting'
    And header Authorization = govwayConfAuth
    And request policy
    And params query_params
    When method post
    Then status 400

    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })
