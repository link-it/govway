Feature: Controllo traccia IDAC02 su fruizione ed erogazione

Scenario: Controllo traccia IDAC02 su fruizione ed erogazione

# E' necessario che nell'ambiente della feature siano presenti
# fruizione_tid e erogazione_tid

* def get_traccia = read('classpath:utils/get_traccia.js')
* def traccia_to_match = 
"""
[
    { name: 'ProfiloInterazione', value: 'bloccante' },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC02' }
]
"""

* def result = get_traccia(fruizione_tid, 'Richiesta') 
* match result contains deep traccia_to_match

* def result = get_traccia(fruizione_tid, 'Risposta') 
* match result contains deep traccia_to_match

* def result = get_traccia(erogazione_tid, 'Richiesta') 
* match result contains deep traccia_to_match

* def result = get_traccia(erogazione_tid, 'Risposta') 
* match result contains deep traccia_to_match