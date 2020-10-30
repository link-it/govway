Feature: Controllo traccia richiesta IDAC01 su fruizione ed erogazione per profilo Non Bloccante Rest

Scenario: Controllo traccia richiesta IDAC01 su fruizione ed erogazione per profilo Non Bloccante Rest


* def get_traccia = read('classpath:utils/get_traccia.js')
* def traccia_to_match = 
"""
([
    { name: 'ProfiloInterazione', value: 'nonBloccante' },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC01' },
    { name: 'ProfiloInterazioneAsincrona-Tipo', value: 'PUSH' },
    { name: 'ProfiloInterazioneAsincrona-Ruolo', value: 'Richiesta' },
    { name: 'ProfiloInterazioneAsincrona-ReplyTo', value: reply_to }
])
"""

* def result = get_traccia(tid, 'Richiesta') 
* match result contains deep traccia_to_match

# Nella risposta va controllato il correlation id mentre il reply-to non c'è
* def traccia_to_match = 
"""
([
    { name: 'ProfiloInterazione', value: 'nonBloccante' },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC01' },
    { name: 'ProfiloInterazioneAsincrona-Tipo', value: 'PUSH' },
    { name: 'ProfiloInterazioneAsincrona-Ruolo', value: 'Richiesta' },
    { name: 'ProfiloInterazioneAsincrona-CorrelationID', value: cid }
])
"""

* def result = get_traccia(tid, 'Risposta') 
* match result contains deep traccia_to_match
