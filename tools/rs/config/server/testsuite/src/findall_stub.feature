Feature: FindAll Stub

# Stub per la lettura di una generica lista, nello stile degli script 
# bash già creati per gli examples

# RICHIEDE CHE LA FEATURE CHIAMANTE PASSI I SEGUENTI ARGOMENTI:
#   resourcePath
#   query_params
# E CHE ABBIA DEFINITO NELL'AMBIENTE l'header di autorizzazione in
#   govwayConfAuth

Scenario: Lettura di una generica lista

Given url configUrl
And path resourcePath
And header Authorization = govwayConfAuth
And params query_params
When method get
Then status 200
And match response.items == '#[]'
And def findall_response_body = response