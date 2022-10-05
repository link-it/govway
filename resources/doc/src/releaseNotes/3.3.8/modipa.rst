Miglioramenti al Profilo di Interoperabilità 'ModI'
------------------------------------------------------

Sono stati apportati i seguenti miglioramenti:

- aggiunto controllo che verifica che la data indicata nel claim 'iat' (API di tipo REST) e nell'elemento 'Timestamp/Created' (API di tipo SOAP) non rappresenti una data futura (viene applicato un intervallo di tolleranza configurabile, con un default di 5 secondi);

- nel controllo degli accessi, è adesso possibile definire un criterio di autorizzazione per messaggio basato sui ruoli del richiedente;

- è ora possibile personalizzare il comportamento del pattern 'INTEGRITY_REST_01' nella parte relativa alla gestione dell'integrità (calcolo/verifica Digest, gestione claim 'signed_header'), delegabile alla componente applicativa.
