Feature: Controllo traccia basic IDAS01

Scenario: Controllo traccia basic IDAS01


* def get_traccia = read('classpath:utils/get_traccia.js')

* def profilo_sicurezza = karate.get('profilo_sicurezza', 'IDAS01')

* def traccia_to_match = 
"""
([
    { name: 'ProfiloInterazione', value: 'bloccante' },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC01' },
    { name: 'ProfiloSicurezzaMessaggio', value: profilo_sicurezza }
])
"""

 * def result = get_traccia(tid,tipo) 
 * match result contains deep traccia_to_match