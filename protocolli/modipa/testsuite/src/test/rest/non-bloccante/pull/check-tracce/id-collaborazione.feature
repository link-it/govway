Feature: Controllo id_collaborazione per profilo Non Bloccante Rest

Scenario: Controllo id_collaborazione per profilo Non Bloccante Rest


* def get_id_collaborazione = read('classpath:utils/get-id-collaborazione.js')
* def traccia_to_match = 
"""
([
    { id_collaborazione: id_collaborazione }
])
"""

 * def result = get_id_collaborazione(tid) 
 * match result contains deep traccia_to_match