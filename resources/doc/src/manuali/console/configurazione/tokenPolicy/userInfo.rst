.. _tokenPolicy_userInfo:

OIDC - UserInfo
~~~~~~~~~~~~~~~

Sezione per attivare la richiesta al servizio di UserInfo per ottenere i
dati inerenti l'utente possessore del token ricevuto (:numref:`userInfo`).

   .. figure:: ../../_figure_console/OIDCUserInfo.png
    :scale: 100%
    :align: center
    :name: userInfo

    Dati di puntamento al servizio di UserInfo

Per il corretto puntamento al servizio di UserInfo devono essere forniti in
prima istanza i parametri generali legati all'endpoint riferito, che
sono in comune con quelli del servizio di Token Introspection, e quindi
già descritti in precedenza.

Successivamente si dovranno fornire i dati di configurazione specifici
per il servizio UserInfo, che sono:

-  *Tipo*: Si seleziona il tipo di servizio UserInfo riferito. I valori
   possibili sono:

   -  *OpenID Connect - UserInfo*: servizio di UserInfo standard OpenID
      Connect.

   -  *Google - UserInfo*: servizio UserInfo di Google. La URL di
      default del servizio viene inserita automaticamente.

   -  *Personalizzato*: si consente di fornire i dati di configurazione
      di un servizio personalizzato di UserInfo. I dati di
      configurazione sono gli stessi già descritti nel caso della
      configurazione del servizio di Token Introspection personalizzato.

-  *URL*: La URL del servizio di UserInfo.

-  *Autenticazione*: La configurazione del metodo di autenticazione,
   quando applicabile.
