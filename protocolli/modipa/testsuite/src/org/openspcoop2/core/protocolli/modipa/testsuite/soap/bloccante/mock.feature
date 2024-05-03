Feature: ModiPA Proxy test

Background:

* def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/ApiDemoBlockingRest/v1'

Scenario: methodIs('post')
    * karate.proceed(url_invocazione_erogazione)

    * def result = get_traccia(karate.response.header('GovWay-Transaction-ID')) 
    #* set response.cacca = 4
    #* def response = { aga: "mennone"}