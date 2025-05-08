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

**Opzioni Avanzate**

È possibile personalizzare il comportamento dell’engine di validazione dei token JWT rendendo obbligatoria la presenza dei claim 'iat', 'exp' e 'nbf'.

Nella configurazione predefinita, tali claim sono richiesti esclusivamente per le erogazioni che adottano il profilo di interoperabilità 'ModI'. Per estendere questa obbligatorietà ad altri contesti, è necessario configurare esplicitamente le seguenti proprietà, da applicare a livello di erogazione o fruizione, come descritto nella sezione :ref:configProprieta. Le proprietà supportano i valori booleani 'true' o 'false':

- *tokenValidation.iat.required*
- *tokenValidation.exp.required*
- *tokenValidation.nbf.required*

Questa configurazione consente di rafforzare i controlli di validità temporale dei token, migliorando la sicurezza degli scambi basati su JWT.

In alternativa è possibile agire a livello di configurazione generale editando il file <directory-lavoro>/govway_local.properties:

   ::

      # configurazione globale
      org.openspcoop2.pdd.gestioneToken.iat.required=true
      org.openspcoop2.pdd.gestioneToken.exp.required=true
      org.openspcoop2.pdd.gestioneToken.nbf.required=true   
      
È inoltre possibile agire a livello di configurazione generale personalizzando il comportamento tra erogazioni e fruizioni:

   ::

      # configurazione globale
      org.openspcoop2.pdd.gestioneToken.iat.erogazioni.required=true
      org.openspcoop2.pdd.gestioneToken.iat.fruizioni.required=true
      org.openspcoop2.pdd.gestioneToken.exp.erogazioni.required=true
      org.openspcoop2.pdd.gestioneToken.exp.fruizioni.required=true
      org.openspcoop2.pdd.gestioneToken.nbf.erogazioni.required=true
      org.openspcoop2.pdd.gestioneToken.nbf.fruizioni.required=true      
      
In alternativa è possibile agire a livello di configurazione generale personalizzando il comportamento oltre che per erogazioni e fruizioni anche per profilo di interoperabiltà. Di seguito un esempio per il profilo di interoperabiltà 'ModI':

   ::

      # configurazione globale per profilo modipa
      org.openspcoop2.pdd.gestioneToken.iat.erogazioni.modipa.required=true
      org.openspcoop2.pdd.gestioneToken.iat.fruizioni.modipa.required=true
      org.openspcoop2.pdd.gestioneToken.exp.erogazioni.modipa.required=true
      org.openspcoop2.pdd.gestioneToken.exp.fruizioni.modipa.required=true
      org.openspcoop2.pdd.gestioneToken.nbf.erogazioni.modipa.required=true
      org.openspcoop2.pdd.gestioneToken.nbf.fruizioni.modipa.required=true      
