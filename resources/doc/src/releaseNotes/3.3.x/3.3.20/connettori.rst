Miglioramenti alla funzionalità dei Connettori
----------------------------------------------

Su API SOAP è stata aggiunta una funzionalità che compensa la mancanza del parametro type nel Content-Type multipart/related (RFC 2387, 3.1) ricevuto da backend o client non conformi. La strategia è configurabile tramite property (reject, inferFromRequest, inferFromBody, forceSoap11, forceSoap12), sia a livello globale sia per singola API.
