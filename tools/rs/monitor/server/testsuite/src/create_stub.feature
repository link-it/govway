Feature: CRUD Create Stub

# Stub per la creazione di una generica risorsa, nello stile degli script 
# bash già creati per gli examples

# RICHIEDE CHE LA FEATURE CHIAMANTE PASSI I SEGUENTI ARGOMENTI:
#   resourcePath
#   body
# E CHE ABBIA DEFINITO NELL'AMBIENTE l'header di autorizzazione in
#   govwayConfAuth

Scenario: Creazione generica entità

Given url configUrl
And path resourcePath
And  header Authorization = govwayConfAuth
And request body
And params query_params
When method post
Then assert responseStatus == 204