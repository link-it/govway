Miglioramenti alla gestione dei Certificati X.509
------------------------------------------------------------------

I certificati scambiati nelle richieste sono adesso accessibili dal contesto tramite le keyword:

- 'transportContext': consente di accedere ai certificati TLS;
- 'securityToken': consente di accedere ai certificati presenti nei token di sicurezza messaggio ModI.

Per ogni certificato è adesso possibile accedere alle seguenti informazioni:

- accedere ai singoli campi di un DN;
- verificare se una keyUsage è presente o meno sul certificato;
- verificare se un purpose (extendedKeyUsage) è presente o meno sul certificato.

L'oggetto associato alla keyword 'securityToken' (org.openspcoop2.protocol.sdk.SecurityToken) consente di ottenere anche solamente l'header o il payload del token ModI Authorization o Agid-JWT-Signature.

Le informazioni sopra descritte sono ora utilizzabili nella gestione delle seguenti funzionalità di GovWay:

- autorizzazione per Token Claims;

- autorizzazione per Contenuti;

- trasformazioni.
