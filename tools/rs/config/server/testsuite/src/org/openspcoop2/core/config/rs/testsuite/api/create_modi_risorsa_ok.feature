Feature: Creazione Api

Background:

* call read('classpath:crud_commons.feature')

* def query_param_profilo_modi = {'profilo': 'ModI'}
* def query_param_profilo_modipa = {'profilo': 'ModIPA'}

* def api_rest = read('api_modi_rest.json')
* eval randomize(api_rest, ["nome"])
* call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modipa } )

* def getExpected =
"""
function(modi) {
var expected = modi;
return expected;
} 
"""


@CreateRisorsa204_modi
Scenario Outline: Api Create Risorsa 204 con profilo ModI <nome-test>
	* def api_modi_risorsa = read('<nome-test>')
	* eval randomize(api_modi_risorsa, ["nome"])
    * call create ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/risorse', body: api_modi_risorsa, key: api_rest.nome + '/' + api_rest.versione + '/risorse' + api_modi_risorsa.nome, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/risorse/' + api_modi_risorsa.nome})
	* def expected = getExpected(api_modi_risorsa.modi)
    * match response.modi == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione  + '/risorse/' + api_modi_risorsa.nome })


Examples:
| nome-test |
| api_modi_rest_risorsa.json |
