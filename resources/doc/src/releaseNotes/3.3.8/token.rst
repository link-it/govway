Miglioramenti alla funzionalità di Gestione dei Token
-----------------------------------------------------

Migliorata la validazione dei token:

- aggiunto controllo che verifica che la data indicata nel claim 'iat' non rappresenti una data futura;

- aggiunta la possibilità di configurare una token policy di validazione che utilizzi, per la validazione del token, il certificato presente negli header x5c e x5t del JWT.

Migliorata la gestione degli access token negoziati con gli Authorization Server:

- aggiunto supporto per il parametro 'resource' richiesto nella v4.1 della PDND;

- in una policy di tipo 'Signed JWT', se utilizzata con API registrate con profilo di interoperabilità ModI, è adesso possibile firmare l'asserzione e valorizzare i paramtri ClientId e KID utilizzando il keystore e i dati definiti sull'applicativo richiedente;

- i token JWT scambiati durante la negoziazione di un token vengono adesso salvati nella traccia escludendo la parte relativa alla signature.
