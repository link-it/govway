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

-  *Header*: presente solo in caso di token di tipo JWS, consente di abilitare una validazione dei valori dei claim 'typ', 'cty' o 'alg' presenti nell'header.

   .. figure:: ../../_figure_console/ValidazioneJWTHeader.png
    :scale: 100%
    :align: center
    :name: validazioneJWTHeader

    Dati di configurazione della validazione di un header JWS

-  *TrustStore*: I parametri di configurazione del truststore da utilizzare per il servizio di validazione. Nella configurazione proposta per default il certificato utilizzato per validare il token sarà quello presente all'interno del truststore, corrispondente all'identificativo indicato nel campo 'Alias Certificato'. In alternativa il certificato è ottenibile tramite le informazioni presenti nel token jwt (x5c, x5t, x5u) attraverso la modalità indicata nel campo 'Riferimento X.509'. Un certificato ottenuto tramite le informazioni presenti nel token jwt viene sempre validato rispetto al truststore e possono essere abilitati ulteriori criteri di verifica tramite CRL o Policy OCSP (vedi sezione :ref:`ocsp`).
