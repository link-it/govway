Feature: Verifica Stato Gruppo Stub

# Stub per la verifica dello stato (abilitato/disabilitato) di un gruppo
# di azioni o risorse di una erogazione o fruizione, invocando l'endpoint
# rs-api-config 'configurazioni/stato'.
#
# RICHIEDE CHE LA FEATURE CHIAMANTE PASSI I SEGUENTI ARGOMENTI:
#   url            URL completo (es. configUrl + '/erogazioni/.../configurazioni/stato')
#   soggetto       valore del parametro 'soggetto'
#   gruppo         valore del parametro 'gruppo' (nullable: se null restituisce lo stato del gruppo predefinito)
#   abilitato      booleano atteso
# E CHE ABBIA DEFINITO NELL'AMBIENTE l'header di autorizzazione in
#   govwayConfAuth

Scenario: Verifica stato gruppo

Given url url
And params ({ soggetto: soggetto, gruppo: gruppo })
And header Authorization = govwayConfAuth
When method get
Then status 200
And match response.abilitato == abilitato
