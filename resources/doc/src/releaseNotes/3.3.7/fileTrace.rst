Miglioramenti alla funzionalità di Tracciatura su File
------------------------------------------------------

Tra le informazioni che possono essere riversate nei file di log associati ai topic di file trace, sono adesso disponibili anche le seguenti informazioni:

- Token negoziati con gli Authorization Server:

	- retrievedAccessToken: access token ottenuto dall’authorization server configurato nella Token Policy associata al connettore;

	- retrievedTokenClaim(nomeClaim): valore del claim indicato come parametro e presente nella risposta ritornata dall’authorization server;

	- retrievedTokenRequestTransactionId: identificativo della transazione che ha originato la richiesta verso l’authorization server;

	- retrievedTokenRequestGrantType: tipo di grant type utilizzato nella negoziazione del token (clientCredentials, usernamePassword, rfc7523_x509, rfc7523_clientSecret);

	- retrievedTokenRequestJwtClientAssertion: asserzione jwt generata durante una negoziazione con grant type "rfc7523_x509";

	- retrievedTokenRequestClientId: clientId utilizzato durante la negoziazione del token;

	- retrievedTokenRequestClientToken: bearer token utilizzato durante la negoziazione del token;

	- retrievedTokenRequestUsername: username utilizzato durante una negoziazione del token con grant type "usernamePassword";

	- retrievedTokenRequestUrl: endpoint dell’authorization server.

- Certificato TLS Client:

	- clientCertificateSubjectDN: distinguished name del subject relativo al certificato tls client; 

	- clientCertificateSubjectCN: common name del subject relativo al certificato tls client;

	- clientCertificateSubjectDNInfo(String oid): ritorna l'informazione indicata come parametro relativa al subject del certificato tls client;
	
	- clientCertificateIssuerDN: distinguished name dell'issuer relativo al certificato tls client; 

	- clientCertificateIssuerCN: common name dell'issuer relativo al certificato tls client; 

	- clientCertificateIssuerDNInfo(String oid): ritorna l'informazione indicata come parametro relativa all'issuer del certificato tls client.

- Token OAuth2 validato come JWT:

	- tokenClaim(nomeClaim): consente di accedere ad un singolo claim di un token OAuth2 validato su GovWay;

	- tokenRaw: JWT token presente nella richiesta; 

	- tokenHeaderRaw: porzione dell'header relativa al token JWT presente nella richiesta, in formato base64; 

	- tokenPayloadRaw: porzione del payload relativa al token JWT presente nella richiesta, in formato base64; 

	- tokenDecodedHeader: contenuto decodificato dell'header presente nel token JWT; 

	- tokenDecodedPayload: contenuto decodificato del payload presente nel token JWT; 

	- tokenHeaderClaim(nomeClaim): valore del claim indicato come parametro e presente nell'header del token JWT;

	- tokenPayloadClaim(nomeClaim): valore del claim indicato come parametro e presente nel payload del token JWT;
		
	- tokenHeaderClaims(): claims (nome=valore) presenti nell'header del token JWT;
		
	- tokenHeaderClaims(claimSeparator, nameValueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

	- tokenPayloadClaims(): claims (nome=valore) presenti nel payload del token JWT;
		
	- tokenPayloadClaims(claimSeparator, nameValueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

	- tokenCertificateSubjectDN: distinguished name del subject relativo al certificato con cui è stato firmato il token JWT; 

	- tokenCertificateSubjectCN: common name del subject relativo al certificato con cui è stato firmato il token JWT; 

	- tokenCertificateSubjectDNInfo(String oid): ritorna l'informazione indicata come parametro relativa al subject del certificato con cui è stato firmato il token JWT;
		
	- tokenCertificateIssuerDN: distinguished name dell'issuer relativo al certificato con cui è stato firmato il token JWT; 

	- tokenCertificateIssuerCN: common name dell'issuer relativo al certificato con cui è stato firmato il token JWT; 

	- tokenCertificateIssuerDNInfo(String oid): ritorna l'informazione indicata come parametro relativa all'issuer del certificato con cui è stato firmato il token JWT.

- Profilo Interoperabilità 'ModI':

	- tokenModI<tokenType>Raw: security token presente nella richiesta; 

	- tokenModI<tokenType>CertificateSubjectDN: distinguished name del subject relativo al certificato con cui è stato firmato il security token; 

	- tokenModI<tokenType>CertificateSubjectCN: common name del subject relativo al certificato con cui è stato firmato il security token; 

	- tokenModI<tokenType>CertificateSubjectDNInfo(String oid): ritorna l'informazione indicata come parametro relativa al subject del certificato con cui è stato firmato il security token;
		
	- tokenModI<tokenType>CertificateIssuerDN: distinguished name dell'issuer relativo al certificato con cui è stato firmato il security token; 

	- tokenModI<tokenType>CertificateIssuerCN: common name dell'issuer relativo al certificato con cui è stato firmato il security token; 

	- tokenModI<tokenType>CertificateIssuerDNInfo(String oid): ritorna l'informazione indicata come parametro relativa all'issuer del certificato con cui è stato firmato il security token.

	I tipi di token disponibili sono:

	- Authorization: security token ricevuto nell'header HTTP 'Authorization';

	- Integrity: security token ricevuto nell'header HTTP 'Agid-JWT-Signature';

	- Soap: security token ricevuto nell'header SOAP;

	Per i tipi di token 'Authorization' e 'Integrity', relativi ad API di tipo REST, sono disponibili anche le seguenti informazioni:

	- tokenModI<tokenType>HeaderRaw: porzione dell'header relativa al security token presente nella richiesta, in formato base64; 

	- tokenModI<tokenType>PayloadRaw: porzione del payload relativa al security token presente nella richiesta, in formato base64; 

	- tokenModI<tokenType>DecodedHeader: contenuto decodificato dell'header presente nel security token; 

	- tokenModI<tokenType>DecodedPayload: contenuto decodificato del payload presente nel security token; 

	- tokenModI<tokenType>HeaderClaim(nomeClaim): valore del claim indicato come parametro e presente nell'header del security token;

	- tokenModI<tokenType>PayloadClaim(nomeClaim): valore del claim indicato come parametro e presente nel payload del security token;
		
	- tokenModI<tokenType>HeaderClaims(): claims (nome=valore) presenti nell'header del security token;
		
	- tokenModI<tokenType>HeaderClaims(claimSeparator, nameValueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

	- tokenModI<tokenType>PayloadClaims(): claims (nome=valore) presenti nel payload del security token;
		
	- tokenModI<tokenType>PayloadClaims(claimSeparator, nameValueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

- Altro:
	
	- requesterIP: rappresenta l’indirizzo IP del richiedente e assumerà la prima informazione valorizzata, trovata nella richiesta, nel seguente ordine: forwardedIP, clientIP;

	- resultCode: consente di ottenere il codice numerico di GovWay che rappresenta l'esito della transazione.

Sono infine stati risolti i seguenti problemi: 

- corretto valore ritornato dalla keyword 'inUrl', dove è stato eliminato il prefisso '[in]' o '[out]' che rimane recuperabile tramite la keyword 'inFunction';

- la tracciatura dell'informazione '${logBase64:errorDetail}' provocava un errore inatteso poichè venivano erroneamente serializzate in base64 le informazioni prima di interpretare il dettaglio dell'errore;

- le richieste errate (es. API not found) non venivano tracciate se la funzionalità veniva attivata tramite una configurazione globale.

