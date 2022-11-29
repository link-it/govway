.. _3.3.9.2_bug:

Bug Fix 3.3.9.p2
----------------

È stata introdotta una politica di generazione automatica degli header HTTP indicati di seguito, se non ritornati dal backend che implementa l'API, con lo scopo di superare alcune vulnerabilità rilevate dal tool ZAProxy:

- X-Content-Type-Options: nosniff

  se non presente vecchie versioni di Internet Explorer e Chrome consentono di effettuare MIME-sniffing sul body della risposta, potenzialmente causando una interpretazione e visualizzazione di una risposta come un content-type piuttosto che un altro;

- Cache-Control: no-cache, no-store, must-revalidate  
                                               
  Pragma: no-cache 
                                                
  Expires: 0

  Vary: *

  se il salvataggio della risposta viene consentita dagli header di 'cache control', i contenuti della risposta potrebbero essere memorizzati nella cache dei server proxy. Successive richieste simili, da parte di altri utenti, potrebbero essere servite recuperando la risposta direttamente dalla cache del proxy, piuttosto che dall'implementazione di backend dell'API, comportando una possibile divulgazione di informazioni sensibili.  Anche le `Linee Guida, nella raccomandazioni tecniche per REST 'RAC_REST_NAME_010' <https://docs.italia.it/italia/piano-triennale-ict/lg-modellointeroperabilita-docs/it/bozza/doc/04_Raccomandazioni%20di%20implementazione/05_raccomandazioni-tecniche-per-rest/02_progettazione-e-naming.html#rac-rest-name-010-il-caching-http-deve-essere-disabilitato>`_, consigliano di disabiltare la possibilità di salvare in cache le risposte.   

Per la console di gestione sono stati risolti i seguenti bug:

- l'aggiornamento dell'interfaccia OpenAPI o WSDL di una API provocava un errore non atteso.
  Dal log si poteva riscontrare il bug introdotto con la gestione delle vulnerabilità di tipo CSRF nella versione 3.3.9.p1: 'Parametro [_csrf] Duplicato'.

