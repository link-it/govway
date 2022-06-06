Miglioramenti alla funzionalità di Tracciatura su File
------------------------------------------------------

Tra le informazioni che possono essere riversate nei file di log associati ai topic di file trace, sono adesso disponibili anche le informazioni inerenti i token negoziati con gli Authorization Server:

- retrievedAccessToken: access token ottenuto dall’authorization server configurato nella Token Policy associata al connettore;

- retrievedTokenClaim(nomeClaim): valore del claim indicato come parametro e presente nella risposta ritornata dall’authorization server;

- retrievedTokenRequestTransactionId: identificativo della transazione che ha originato la richiesta verso l’authorization server;

- retrievedTokenRequestGrantType: tipo di grant type utilizzato nella negoziazione del token (clientCredentials, usernamePassword, rfc7523_x509, rfc7523_clientSecret);

- retrievedTokenRequestJwtClientAssertion: asserzione jwt generata durante una negoziazione con grant type "rfc7523_x509";

- retrievedTokenRequestClientId: clientId utilizzato durante la negoziazione del token;

- retrievedTokenRequestClientToken: bearer token utilizzato durante la negoziazione del token;

- retrievedTokenRequestUsername: username utilizzato durante una negoziazione del token con grant type "usernamePassword";

- retrievedTokenRequestUrl: endpoint dell’authorization server.

Aggiunte inoltre ulteriori informazioni, inerenti le comunicazioni gestite dal gateway:

- tokenClaim(nomeClaim): consente di accedere ad un singolo claim di un token OAuth2 validato su GovWay;

- resultCode: consente di ottenere il codice numerico di GovWay che rappresenta l'esito della transazione.

Sono infine stati risolti i seguenti problemi: 

- corretto valore ritornato dalla keyword 'inUrl', dove è stato eliminato il prefisso '[in]' o '[out]' che rimane recuperabile tramite la keyword 'inFunction';

- la tracciatura dell'informazione '${logBase64:errorDetail}' provocava un errore inatteso poichè venivano erroneamente serializzate in base64 le informazioni prima di interpretare il dettaglio dell'errore;

- le richieste errate (es. API not found) non venivano tracciate se la funzionalità veniva attivata tramite una configurazione globale.

