Feature: Configurazione Rate Limiting Fruizioni

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore = read('../api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault

* def erogatore = read('../soggetto_erogatore.json')
* eval randomize (erogatore, ["nome", "credenziali.username"])

* def api_spcoop = read('../api_spcoop.json')
* eval randomize(api_spcoop, ["nome"])
* eval api_spcoop.referente = soggettoDefault

* def fruizione_petstore = read('../fruizione_petstore.json')
* eval fruizione_petstore.api_nome = api_petstore.nome
* eval fruizione_petstore.api_versione = api_petstore.versione
* eval fruizione_petstore.erogatore = erogatore.nome
* eval fruizione_petstore.api_referente = api_petstore.referente

* def fruizione_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.api_nome + '/' + fruizione_petstore.api_versione
* def fruizione_petstore_path = 'fruizioni/' + fruizione_key
* def api_petstore_path = 'api/' + api_petstore.nome + '/' + api_petstore.versione

* def policy = read('classpath:bodies/rate-limiting-policy-fruizione.json')

* def policy_update = read('classpath:bodies/rate-limiting-policy-fruizione-update.json')

* def policy_types = [ 'numero-richieste', 'occupazione-banda', 'tempo-medio-risposta', 'tempo-complessivo-risposta' ]
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

@RateLimitingAllPolicyTypes
Scenario: Configurazione Rate Limiting Fruizioni

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    # Creo un applicativo da utilizzare nel criterio di collezionamento dati
    * def applicativo = read('classpath:bodies/applicativo_http.json') 
    * eval randomize(applicativo, ["nome", "credenziali.username"])
    * call create ({ resourcePath: 'applicativi', body: applicativo })
    * eval policy.filtro.applicativo_fruitore = applicativo.nome
    
    * def data = build_data(policy_types, policy, policy_update, fruizione_petstore_path)
    * call read('classpath:servizi-configurazione/rate-limiting.feature') data

    * call delete ({ resourcePath: 'applicativi/' + applicativo.nome })
    * call delete ({ resourcePath: fruizione_petstore_path })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@FiltroApplicativoFruitoreInesistente400
Scenario: Configurazione del Rate Limting filtrando per un applicativo inesistente

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    * eval policy.filtro.applicativo_fruitore = "ApplicativoInesistente"
    * eval randomize(policy, ["nome", "filtro.applicativo_fruitore"])

    Given url configUrl
    And path fruizione_petstore_path, 'configurazioni', 'rate-limiting'
    And header Authorization = govwayConfAuth
    And request policy
    And params query_params
    When method post
    Then status 400

    * call delete ({ resourcePath: fruizione_petstore_path })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })


@FiltroSoggettoFruitoreInesistente400
Scenario: Configurazione del Rate Limiting filtrando per un soggetto fruitore inesistente

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    * eval policy.filtro.soggetto_fruitore = "SoggettoInesistente"
    * eval randomize(policy, ["nome", "filtro.soggetto_fruitore"])

    Given url configUrl
    And path fruizione_petstore_path, 'configurazioni', 'rate-limiting'
    And header Authorization = govwayConfAuth
    And request policy
    And params query_params
    When method post
    Then status 400

    * call delete ({ resourcePath: fruizione_petstore_path })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@FiltroAzioneInesistente400
Scenario: Configurazione del Rate Limiting filtrando per un'azione inesistente

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    * eval policy.filtro.azione = "AzioneInesistente"
    * eval randomize(policy, ["nome", "filtro.azione"])

    Given url configUrl
    And path fruizione_petstore_path, 'configurazioni', 'rate-limiting'
    And header Authorization = govwayConfAuth
    And request policy
    And params query_params
    When method post
    Then status 400

    * call delete ({ resourcePath: fruizione_petstore_path })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })
