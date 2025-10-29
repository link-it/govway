Miglioramenti alla gestione dei Certificati X.509
------------------------------------------------------------------

I certificati scambiati nelle richieste sono adesso accessibili dal contesto tramite le keyword:

- 'transportContext': consente di accedere ai certificati TLS;
- 'securityToken': consente di accedere ai certificati presenti nei token di sicurezza messaggio ModI e negli access token JWT.

Per ogni certificato è adesso possibile accedere alle seguenti informazioni:

- accedere ai singoli campi di un DN;
- verificare se una keyUsage è presente o meno sul certificato;
- verificare se un purpose (extendedKeyUsage) è presente o meno sul certificato.

L'oggetto associato alla keyword 'securityToken' (org.openspcoop2.protocol.sdk.SecurityToken) consente di ottenere anche solamente la parte relativa all'header o al payload del token ModI (Authorization o Agid-JWT-Signature) o dell'access token JWT. Inoltre consente di accedere puntualmente ai singoli claim presenti nell'header o nel payload del token.

Le informazioni sopra descritte sono ora utilizzabili nella gestione delle seguenti funzionalità di GovWay:

- autorizzazione per Token Claims;

- autorizzazione per Contenuti;

- trasformazioni.
