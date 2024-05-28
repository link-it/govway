Feature: Controllo traccia AUDIT_REST con kid

Scenario: Controllo traccia AUDIT_REST con kid


* def profilo_sicurezza = karate.get('profilo_sicurezza', 'IDAR01')
* def profilo_audit = karate.get('profilo_audit', 'AUDIT_REST_01')
* def profilo_audit_schema = karate.get('profilo_audit_schema', 'Linee Guida ModI')
* def profilo_interazione = karate.get('profilo_interazione', 'bloccante')

* def other_checks = karate.get('other_checks', [])


* def get_traccia = read('classpath:utils/get_traccia.js')
* def uniqueAppend = read('classpath:utils/unique-append.js')


* def traccia_to_match = 
"""
([
    { name: 'ProfiloInterazione', value: profilo_interazione },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC01' },
    { name: 'ProfiloSicurezzaAudit', value: profilo_audit },
    { name: 'ProfiloSicurezzaAudit-SchemaDati', value: profilo_audit_schema },
    { name: 'ProfiloSicurezzaMessaggio', value: profilo_sicurezza },
    { name: 'ProfiloSicurezzaMessaggioAudit-IssuedAt', value: '#string' },
    { name: 'ProfiloSicurezzaMessaggioAudit-NotBefore', value: '#string' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Expiration', value: '#string' },
    { name: 'ProfiloSicurezzaMessaggioAudit-MessageId', value: token.payload.jti }
])
"""


* def traccia_to_match = uniqueAppend(traccia_to_match, other_checks)

* def result = get_traccia(tid,tipo) 
* match result contains deep traccia_to_match
