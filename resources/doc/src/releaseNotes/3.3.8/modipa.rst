Miglioramenti al Profilo di Interoperabilità 'ModI'
------------------------------------------------------

Sono stati apportati i seguenti miglioramenti:

- aggiunto controllo che verifica che la data indicata nel claim 'iat' (API di tipo REST) e nell'elemento 'Timestamp/Created' (API di tipo SOAP) non rappresenti una data futura (viene attuato per default un intervallo di tolleranza di 5 secondi);

- nel controllo degli accessi, è adesso possibile definire un criterio di autorizzazione per messaggio basato sui ruoli del richiedente;

- gestire un pattern 'INTEGRITY_REST_01' personalizzato nella parte relativa alla gestione dell'integrità (calcolo/verifica Digest, gestione claim 'signed_header') che viene delegata alla componente applicativa.
