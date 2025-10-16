.. _idmEsterno_oauth:

Integrazione delle Console con un Authorization Server tramite OIDC/OAuth
--------------------------------------------------------------------------

La presente sezione fornisce i dettagli necessari alla configurazione delle console per l’integrazione con un authorization server conforme agli standard OpenID Connect (OIDC) e OAuth 2.0.

Come riferimento viene utilizzato l’authorization server `Keycloak <https://www.keycloak.org/>`_.

**Configurazione di esempio (scenario demo)**

Per un rapido scenario dimostrativo, Keycloak è stato configurato come segue:

- Creazione di un realm denominato 'GovWay'.
- Creazione di un client con client_id e name impostati a 'govway_console', associato all’Authentication Standard Flow, per implementare l’Authorization Code Flow previsto dallo standard OpenID Connect. Sono state definite come valid redirect URIs le seguenti URL:

    - http://127.0.0.1:8080/govwayConsole/oauth2/callback
    - http://127.0.0.1:8080/govwayConsole/login.do*
- Creazione di un secondo client con client_id e name impostati a 'govway_monitor', anch’esso configurato con lo Standard Flow. Sono state definite come valid redirect URIs le seguenti URL:

    - http://127.0.0.1:8080/govwayMonitor/oauth2/callback
    - http://127.0.0.1:8080/govwayMonitor/public/*
- Creazione di due utenze applicative utilizzate come utenze di default per le console:

    - amministratore
    - operatore

- Creazione delle ulteriori utenze applicative i cui username devono corrispondere a quelli registrati nella console di gestione.


**Configurazione lato Console**

Per abilitare l’autenticazione tramite Keycloak è necessario intervenire sui seguenti file di configurazione:

- Console di gestione (govwayConsole): *<directory-lavoro>/console_local.properties*;
- Console di monitoraggio (govwayMonitor): *<directory-lavoro>/monitor_local.properties*

1. Disabilitazione dell’autenticazione locale:

     ::

        login.application=false

2. Definizione della modalità di accesso all’identità autenticata tramite Keycloak:

     ::

        login.tipo=oauth2

3. Configurazione delle URL di Keycloak per la negoziazione dei token:

     ::

        # Url di autenticazione
        login.props.oauth2.authorization.endpoint=https://localhost:8543/realms/GovWay/protocol/openid-connect/auth
        # Url dove richiedere il token
        login.props.oauth2.token.endpoint=https://localhost:8543/realms/GovWay/protocol/openid-connect/token
        # Url dove scaricare le indormazioni utente
        login.props.oauth2.userInfo.endpoint=https://localhost:8543/realms/GovWay/protocol/openid-connect/userinfo
        # URL di validazione dei certificati JWT
        login.props.oauth2.jwks.endpoint=https://localhost:8543/realms/GovWay/protocol/openid-connect/certs
        # URL del servizio logout federato
        login.props.oauth2.logout.endpoint=https://localhost:8543/realms/GovWay/protocol/openid-connect/logout

4. Impostazione del Client ID e della Redirect URI (/oauth2/callback). Per la console di gestione (govway_console):

     ::

        # Client ID rilasciato dal server OAuth2
        login.props.oauth2.clientId=govway_console
        # URL dove viene rediretto l'utente dopo l'autenticazione
        login.props.oauth2.redirectUri=http://127.0.0.1:8080/govwayConsole/oauth2/callback        

   Per la console di monitoraggio (govway_monitor):

     ::

        # Client ID rilasciato dal server OAuth2
        login.props.oauth2.clientId=govway_monitor
        # URL dove viene rediretto l'utente dopo l'autenticazione
        login.props.oauth2.redirectUri=http://127.0.0.1:8080/govwayMonitor/oauth2/callback

5. Definizione dello Scope e del Claim contenente lo username:

     ::

        # Scope dell'autenticazione
        login.props.oauth2.scope=openid
        # Nome del claim da dove leggere il principal
        login.props.oauth2.principalClaim=preferred_username
        
6. Validazione opzionale dei claim nel token. È possibile abilitare controlli aggiuntivi sui claim presenti nel token:
       
     ::

        # Validazione dei claim del token
        # Inserire una riga per ogni claim da validare nella forma: login.props.oauth2.claims.validation.claimName=claimValues (lista di valori separati da virgola)
        #login.props.oauth2.claims.validation.claimName=claimValue1,claimValue2,...
        login.props.oauth2.claims.validation.iss=https://localhost:8543/realms/GovWay
	login.props.oauth2.claims.validation.aud=account

7. Parametri di connessione verso Keycloak:

     ::

        # Parametri timeout connessione verso il server OAuth2
        #login.props.oauth2.readTimeout=15000
        #login.props.oauth2.connectTimeout=10000

        # Truststore https
        #login.props.oauth2.https.hostnameVerifier=true
        #login.props.oauth2.https.trustAllCerts=false
        #login.props.oauth2.https.trustStore=PATH
        #login.props.oauth2.https.trustStore.password=changeme
        #login.props.oauth2.https.trustStore.type=jks
        #login.props.oauth2.https.trustStore.crl=PATH

        # Keystore https
        #login.props.oauth2.https.keyStore=PATH
        #login.props.oauth2.https.keyStore.password=changeme
        #login.props.oauth2.https.keyStore.type=jks
        #login.props.oauth2.https.key.alias=mykey
        #login.props.oauth2.https.key.password=changeme
