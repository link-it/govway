Feature: Verifica il tracciamento e la diagnostica con gli identificativi messaggio

Scenario: Verifica il tracciamento e la diagnostica con gli identificativi messaggio

* def get_diagnostico = read('classpath:utils/get_diagnostico.js')
* def get_ruolo_transazione = read('classpath:utils/get-ruolo-transazione.js')
* def count_traccia = read('classpath:utils/get_traccia.js')
* def exists_traccia = read('classpath:utils/get_traccia.js')



* def ruolo = get_ruolo_transazione(tid)
* karate.log("ruolo: ", ruolo.toString())





* def result_count = count_traccia(tid,traceMessageId,tipo)

* def count_match = 
"""
([
    { count: 1 }
])
"""
* match result_count contains deep count_match 






* def result_exists = exists_traccia(tid,traceMessageId,tipo)

* def exists_match = 
"""
([
    { id_transazione: tid }
])
"""

* def other_exists = karate.get('other_exists', [])
* eval
"""
if ( tipo=='Risposta'  ) {
	other_exists = ([{ rif_messaggio: requestMessageId }])
}
"""
* def exists_match = karate.append(exists_match, other_exists)

* karate.log("result_exists: ", result_exists)
* karate.log("exists_match: ", exists_match)
* match result_exists contains deep exists_match






* def messaggio1 = 
"""
if ( tipo=='Risposta'  ) {
   if (ruolo.toString().contains('delegata')) {
      messaggio1 = 'Ricevuto messaggio di cooperazione con identificativo ['+traceMessageId+'] inviato dalla parte mittente';
   }
   else {
      messaggio1 = 'Generato messaggio di cooperazione con identificativo ['+traceMessageId+']';
   }
}
else{
   if (ruolo.toString().contains('delegata')) {
      messaggio1 = 'Invio Messaggio di cooperazione con identificativo ['+traceMessageId+'] in corso';
   }
   else {
      messaggio1 = 'Invio Messaggio di cooperazione con identificativo ['+traceMessageId+'] in corso';
   }
}
"""

* def result = get_diagnostico(tid, messaggio1) 
* match result[0].MESSAGGIO contains messaggio1


* def messaggio2 = 
"""
if ( tipo=='Risposta'  ) {
   // esiste solo un diagnostico da verificare
   messaggio2 = messaggio1
}
else{
   if (ruolo.toString().contains('delegata')) {
      messaggio2 = 'Messaggio di cooperazione con identificativo ['+traceMessageId+'] inviato alla parte destinataria';
   }
   else {
      messaggio2 = 'Messaggio applicativo con ID ['+traceMessageId+']';
   }
}
"""

* def result = get_diagnostico(tid, messaggio2) 
* match result[0].MESSAGGIO contains messaggio2


* def messaggio3 = 
"""
if ( tipo=='Risposta'  ) {
   // esiste solo un diagnostico da verificare
   messaggio3 = messaggio1
}
else{
   if (ruolo.toString().contains('delegata')) {
      // non esiste un diagnostico che indica l'avvenuta ricezione
      messaggio3 = messaggio1
   }
   else {
      messaggio3 = 'Ricevuto messaggio di cooperazione con identificativo ['+traceMessageId+']';
   }
}
"""

* def result = get_diagnostico(tid, messaggio3) 
* match result[0].MESSAGGIO contains messaggio3

