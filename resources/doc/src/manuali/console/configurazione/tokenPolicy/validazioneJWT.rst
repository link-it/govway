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

-  *Claims Parser*: indica il tipo di parser che deve essere utilizzato
   per la validazione del token JWT. I valori possibili sono:

   -  *RFC 7519 - JSON Web Token*

   -  *OpenID Connect - ID Token*

   -  *Google - ID Token*

   -  *Personalizzato*: nel caso del parser personalizzato occorre
      fornire il relativo ClassName della classe con la logica di
      parsing.

-  *KeyStore*: I parametri di configurazione del keystore da utilizzare
   per il servizio di validazione.
