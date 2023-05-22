Feature: Controllo identificativo messaggio della traccia

Scenario: Controllo identificativo messaggio della traccia


* def id_messaggio = karate.get('id_messaggio', 'N.D.')

* def get_id_messaggio_traccia = read('classpath:utils/get_id_messaggio_traccia.js')
* def traccia_to_match = 
"""
([
    { id_messaggio: id_messaggio}
])
"""

* def result = get_id_messaggio_traccia(tid,tipo) 
* match result contains deep traccia_to_match
