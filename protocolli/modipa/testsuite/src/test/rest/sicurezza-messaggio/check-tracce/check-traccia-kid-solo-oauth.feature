Feature: Controllo traccia IDAR01 con authorization token generato da PDND

Scenario: Controllo traccia IDAR01 con authorization token generato da PDND


* def profilo_sicurezza = karate.get('profilo_sicurezza', 'IDAR01')
* def profilo_interazione = karate.get('profilo_interazione', 'bloccante')
* def token_auth = karate.get('token_auth', 'Authorization ModI')
* def kid = karate.get('kid', 'N.D.')

* def other_checks = karate.get('other_checks', [])


* def get_traccia = read('classpath:utils/get_traccia.js')
* def traccia_to_match = 
"""
([
    { name: 'ProfiloInterazione', value: profilo_interazione },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC01' },
    { name: 'ProfiloSicurezzaMessaggio', value: profilo_sicurezza },
    { name: 'GenerazioneTokenIDAuth', value: token_auth }
])
"""

* def traccia_to_match = karate.append(traccia_to_match, other_checks)

* def result = get_traccia(tid,tipo) 
* match result contains deep traccia_to_match



* def id_messaggio_traccia = 
"""
if (tipo=='Risposta') {
   id_messaggio_traccia = token.payload.jti
}
else {
   id_messaggio_traccia = traceMessageId
}
"""


* def requestMessageIdValue = 
"""
if (tipo=='Risposta') {
   requestMessageIdValue = requestMessageId
}
else {
   requestMessageIdValue = 'undefined'
}
"""


* def check_tracciamento_diagnostica = read('classpath:utils/check-tracciamento-diagnostica.feature') 
# Verifico che le tracce e i diagnostici utilizzino i corretti id messaggio
* call check_tracciamento_diagnostica ({ tid: tid, traceMessageId:id_messaggio_traccia, tipo:tipo, requestMessageId:requestMessageIdValue })
