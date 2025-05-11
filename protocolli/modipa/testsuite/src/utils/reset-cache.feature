Feature: Reset Cache dei Token

Scenario: Reset Cache dei Token


# Svuoto la cache per evitare che venga generato lo stesso token in questo test
* def credenziali =
    """
    ({
        username: jmx_username,
		password: jmx_password
    })
    """
* def basic = read('classpath:utils/basic-auth.js')

Given url govway_base_path + "/check?methodName=resetCache&resourceName=" + cache_name
And header Authorization = call basic (credenziali)
When method get
Then status 200
