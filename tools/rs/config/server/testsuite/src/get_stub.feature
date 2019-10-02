Feature: Get Stub

# Stub per la lettura di una generica risorsa, nello stile degli script 
# bash già creati per gli examples

# RICHIEDE CHE LA FEATURE CHIAMANTE PASSI I SEGUENTI ARGOMENTI:
#   resourcePath
# E CHE ABBIA DEFINITO NELL'AMBIENTE l'header di autorizzazione in
#   govwayConfAuth

Scenario: Lettura di una generica risorsa

Given url configUrl
And path resourcePath , key
And header Authorization = govwayConfAuth
And params query_params
When method get
Then status 200
And def get_response_body = response