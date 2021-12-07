Feature: Controllo traccia IDAS01

Scenario: Controllo traccia IDAS01

* def merge_checks = read('classpath:utils/merge-checks.js')
* def get_traccia = read('classpath:utils/get_traccia.js')

* def profilo_sicurezza = karate.get('profilo_sicurezza', 'IDAS01')

* def other_checks = karate.get('other_checks', [])

* def traccia_to_match = 
"""
([
    { name: 'ProfiloInterazione', value: 'bloccante' },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC01' },
    { name: 'ProfiloSicurezzaMessaggio', value: profilo_sicurezza },
    { name: 'ProfiloSicurezzaMessaggio-X509-Subject', value: x509sub },
    { name: 'ProfiloSicurezzaMessaggio-X509-Issuer', value: 'CN=ExampleCA, O=Example, L=Pisa, ST=Italy, C=IT' },
    { name: 'ProfiloSicurezzaMessaggio-IssuedAt', value: '#string' },
    { name: 'ProfiloSicurezzaMessaggio-Expiration', value: '#string' },
    { name: 'ProfiloSicurezzaMessaggio-WSA-To', value: karate.xmlPath(body, '/Envelope/Header/To') },
    { name: 'ProfiloSicurezzaMessaggio-WSA-From', value: karate.xmlPath(body, '/Envelope/Header/From/Address') },
    { name: 'ProfiloSicurezzaMessaggio-MessageId', value: karate.xmlPath(body, '/Envelope/Header/MessageID') },
])
"""

* print 'Checking traccia ', tipo, ' ', tid
* def traccia_to_match = merge_checks(traccia_to_match, other_checks)

* def result = get_traccia(tid,tipo) 
* print 'Traccia result: ', result
* print 'Traccia to match: ', traccia_to_match
* print 'Other to match: ', other_checks
* match result contains deep traccia_to_match