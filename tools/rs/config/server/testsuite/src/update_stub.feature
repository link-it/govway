Feature: CRUD Update Stub

# Stub per l'update di una generica risorsa, nello stile degli script 
# bash già creati per gli examples

# RICHIEDE CHE LA FEATURE CHIAMANTE PASSI I SEGUENTI ARGOMENTI:
#   resourcePath
#   body_update
# 	key
# 	query_params
# E CHE ABBIA DEFINITO NELL'AMBIENTE l'header di autorizzazione in
#   govwayConfAuth

Scenario: Update generica entita'

Given url configUrl
And path resourcePath, key
And header Authorization = govwayConfAuth
And request body_update
And params query_params
When method put
Then status 204
