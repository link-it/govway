Bug Fix
-----------------------------------------------------

Sono stati risolti i seguenti bug:

-  *Contatore Richieste Attive su Rate Limiting*: non venivano decrementati i contatori delle richieste attive di una policy di Rate Limiting, se la transazione aveva un esito per cui era stato disabilitato il tracciamento.

-  *ProxyReverse*: gestita funzionalità ProxyReverse per header 'Location' e 'Content-Location' anche sui codici di risposta non inerenti il Redirect.

-  *MultiTenant dopo aggiornamento*: l'aggiornamento dalla versione 3.0.0 alla versione 3.0.1 non permette di attivare il multi-tenant se nella precedente versione era stato creato più di un soggetto di dominio interno.

