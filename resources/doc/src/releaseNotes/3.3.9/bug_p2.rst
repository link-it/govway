.. _3.3.9.2_bug:

Bug Fix 3.3.9.p2
----------------

È stata introdotta una politica di generazione automatica degli header HTTP indicati di seguito, se non ritornati dal backend che implementa l'API, con lo scopo di evitare alcune vulnerabilità a cui possono essere soggette le implementazioni delle API:

- X-Content-Type-Options: nosniff

- Cache-Control: no-cache, no-store, must-revalidate  
                                               
  Pragma: no-cache 
                                                
  Expires: 0

  Vary: *

  .. note::

     Il caching viene disabilitato per evitare che delle risposte vengano inopportunamente messe in cache, come indicato nelle `Linee Guida - raccomandazioni tecniche per REST 'RAC_REST_NAME_010' <https://docs.italia.it/italia/piano-triennale-ict/lg-modellointeroperabilita-docs/it/bozza/doc/04_Raccomandazioni%20di%20implementazione/05_raccomandazioni-tecniche-per-rest/02_progettazione-e-naming.html#rac-rest-name-010-il-caching-http-deve-essere-disabilitato>`_. Il mancato rispetto di questa raccomandazione può portare all’esposizione accidentale di dati personali.

Per la console di gestione sono stati risolti i seguenti bug:

- l'aggiornamento dell'interfaccia OpenAPI o WSDL di una API provocava un errore non atteso.
  Dal log si poteva riscontrare il bug introdotto con la gestione delle vulnerabilità di tipo CSRF nella versione 3.3.9.p1: 'Parametro [_csrf] Duplicato'.

