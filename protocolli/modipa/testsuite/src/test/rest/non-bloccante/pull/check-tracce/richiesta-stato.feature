Feature: Controllo traccia richiesta stato IDAC01 su fruizione ed erogazione per profilo Non Bloccante Rest

Scenario: Controllo traccia richiesta stato IDAC01 su fruizione ed erogazione per profilo Non Bloccante Rest


* def get_traccia = read('classpath:utils/get_traccia.js')
* def traccia_to_match = 
"""
[
    { name: 'ProfiloInterazione', value: 'nonBloccante' },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC01' },
    { name: 'ProfiloInterazioneAsincrona-Tipo', value: 'PULL' },
    { name: 'ProfiloInterazioneAsincrona-Ruolo', value: 'RichiestaStato' },
    { name: 'ProfiloInterazioneAsincrona-RisorsaCorrelata', value: 'POST /tasks/queue' }
]
"""

* def result = get_traccia(tid, 'Richiesta') 
* match result contains deep traccia_to_match


* def additional_check = karate.get('additional_check', [])

* def risposta_to_match = 
"""
[
    { name: 'ProfiloInterazione', value: 'nonBloccante' },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC01' },
    { name: 'ProfiloInterazioneAsincrona-Tipo', value: 'PULL' },
    { name: 'ProfiloInterazioneAsincrona-Ruolo', value: 'RichiestaStato' },
    { name: 'ProfiloInterazioneAsincrona-RisorsaCorrelata', value: 'POST /tasks/queue' }
]
"""


* def risposta_to_match = karate.append(risposta_to_match, additional_check)

* def result = get_traccia(tid, 'Risposta') 
* match result contains deep risposta_to_match