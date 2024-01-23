Feature: Controllo traccia IDAR01 con kid

Scenario: Controllo traccia IDAR01 con kid


* def profilo_sicurezza = karate.get('profilo_sicurezza', 'IDAR01')
* def profilo_interazione = karate.get('profilo_interazione', 'bloccante')
* def kid = karate.get('kid', 'N.D.')

* def other_checks = karate.get('other_checks', [])


* def get_traccia = read('classpath:utils/get_traccia.js')
* def traccia_to_match = 
"""
([
    { name: 'ProfiloInterazione', value: profilo_interazione },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC01' },
    { name: 'ProfiloSicurezzaMessaggio', value: profilo_sicurezza },
    { name: 'ProfiloSicurezzaMessaggio-IssuedAt', value: '#string' },
    { name: 'ProfiloSicurezzaMessaggio-NotBefore', value: '#string' },
    { name: 'ProfiloSicurezzaMessaggio-Expiration', value: '#string' },
    { name: 'ProfiloSicurezzaMessaggio-MessageId', value: '#uuid' },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: token.payload.client_id },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: token.payload.iss },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: token.payload.sub },
    { name: 'ProfiloSicurezzaMessaggio-Kid', value: kid }
])
"""

* def traccia_to_match = karate.append(traccia_to_match, other_checks)

* def result = get_traccia(tid,tipo) 
* match result contains deep traccia_to_match
