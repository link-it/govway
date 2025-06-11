Feature: FindAll 200 Template

Scenario:

    # CREATE 

	# rimuovo il parametro profilo_qualsiasi per la create
	* def profilo = query_params.profilo
	* def profilo_qualsiasi = query_params.profilo_qualsiasi
	* eval query_params.profilo_qualsiasi = null
	
    Given url configUrl
    And path resourcePath
    And  header Authorization = govwayConfAuth
    And request body
    And params query_params
    When method post
    Then assert responseStatus == 201

    # FINDALL

	# metto il parametro profilo se e solo se profilo_qualsiasi era false o null
	* eval query_params.profilo = null
	* eval query_params.profilo_qualsiasi = profilo_qualsiasi
	* if (!query_params.profilo_qualsiasi) { (query_params.profilo = profilo) }
	
    Given url configUrl
    And path resourcePath
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.items == '#[]'
    And def findall_response_body = response

    # DELETE
    # rimuovo il parametro profilo_qualsiasi per la delete
	* eval query_params.profilo_qualsiasi = null
	* eval query_params.profilo = profilo
	
    Given url configUrl
    And path resourcePath , key
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204
