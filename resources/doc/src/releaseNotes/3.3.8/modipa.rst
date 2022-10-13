Miglioramenti al Profilo di Interoperabilità 'ModI'
------------------------------------------------------

Sono stati apportati i seguenti miglioramenti:

- aggiunto controllo che verifica che la data indicata nel claim 'iat' (API di tipo REST) e nell'elemento 'Timestamp/Created' (API di tipo SOAP) non rappresenti una data futura (viene applicato un intervallo di tolleranza configurabile, con un default di 5 secondi);

- nel controllo degli accessi, è adesso possibile definire un criterio di autorizzazione basato sui ruoli dell'applicativo richiedente;

- è ora possibile personalizzare il comportamento del pattern 'INTEGRITY_REST_01', delegando la parte relativa alla gestione dell'integrità (calcolo/verifica Digest, gestione claim 'signed_header' o simile) alla componente applicativa. Con questa modalità è possibile anche personalizzare il nome dell'header HTTP utilizzato.
