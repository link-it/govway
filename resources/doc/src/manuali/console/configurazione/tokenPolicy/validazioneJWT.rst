.. _tokenPolicy_validazioneJWT:

Validazione JWT
~~~~~~~~~~~~~~~

Nel caso in cui il token sia di tipo JWT (quindi JWE o JWS), questa
opzione attiva la validazione basata su tale standard (:numref:`validazioneJWT`).

   .. figure:: ../../_figure_console/ValidazioneJWT.png
    :scale: 100%
    :align: center
    :name: validazioneJWT

    Dati di configurazione della validazione JWT

I dati da inserire sono:

-  *Formato Token*: indica il formato atteso del payload contenuto nel token JWT. Maggiori dettagli sul mapping vengono forniti in ':ref:`configMappingToken`'. I valori possibili sono:

   -  *RFC 7519 - JSON Web Token*: claims attesi definiti nel RFC 'https://datatracker.ietf.org/doc/html/rfc7519#section-4';

   -  *OpenID Connect - ID Token*: definiti nel RFC 'https://openid.net/specs/openid-connect-core-1_0.html#IDToken'; 

   -  *Google - ID Token*: claims definiti in 'https://developers.google.com/identity/protocols/oauth2/openid-connect#obtainuserinfo';

   -  *Personalizzato*: consente di definire un mapping puntuale tra il nome di un claim e l'informazione che GovWay cerca di estrarre dal token (:numref:`validazioneJWTpersonalizzato`);

   -  *Plugin*: consente di indicare il nome di una classe che implementa una logica di parsing personalizzata (deve implementare l'interfaccia 'org.openspcoop2.pdd.core.token.parser.ITokenParser').

-  *KeyStore*: I parametri di configurazione del keystore da utilizzare
   per il servizio di validazione.
