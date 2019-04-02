.. _avanzate_connettori_https:

Autenticazione Https
~~~~~~~~~~~~~~~~~~~~

Il connettore HTTPS permette di personalizzare i parametri SSL per ogni
connessione che utilizza questo protocollo.

Il connettore HTTPS supporta:

-  **Autenticazione Server**, è possibile definire le trusted keys e
   indicare se si desidera verificare l'hostname rispetto al certificato
   server contenuto nella sessione SSL.

-  **Autenticazione Client**, è opzionale; se abilitata permette di
   definire il keystore contenente la chiave privata che si deve
   utilizzare durante la sessione SSL.

   .. figure:: ../../_figure_console/ConnettoreHTTPS.jpg
    :scale: 100%
    :align: center
    :name: configAutenticazioneHTTPSFig

    Dati di configurazione di un'autenticazione Https

Facendo riferimento alla maschera raffigurata in :numref:`configAutenticazioneHTTPSFig` andiamo a descrivere
il significato dei parametri:

-  *Connettore*

   -  **Url**: indirizzo endpoint del connettore

   -  **Tipologia** (es. TLSv1.2): Tipo e versione del protocollo di trasporto. Sono selezionabili tutti i tipi supportati dalla versione della jvm utilizzata.

   -  **Hostname Verifier** (true/false): Attiva la verifica in fase di
      autenticazione server della corrispondenza tra l'hostname indicato
      nella url e quello presente nel certificato server ritornato dal
      server (nel subject CN=hostname)

-  *Autenticazione Server*

   -  **Path**: Path dove è localizzato il truststore contenente i
      certificati server trusted.

   -  **Tipo** (jks, pkcs12, jceks, bks, uber e gkr): Tipologia del
      TrustStore (default: jks)

   -  **Password**: Password per l'accesso al TrustStore

   -  **Algoritmo**: Algoritmo di firma utilizzato (default: PKIX)

-  *Autenticazione Client (opzionale)*

   -  **Dati di Accesso al KeyStore** (usa valori del TrustStore,
      Ridefinisci): Consente di riutilizzare i medesimi riferimenti del
      TrustStore anche per il KeyStore o in alternativa ridefinirli.

   -  **Tipo (solo se Dati di Accesso ridefiniti)** (jks, pkcs12, jceks,
      bks, uber e gkr): Tipologia del Keystore (default: jks)

   -  **Password (solo se Dati di Accesso ridefiniti)**: Password per
      l'accesso al Keystore

   -  **Password Chiave Privata**: Password per accedere alla chiave
      privata presente nel keystore.

   -  **Algoritmo**: Algoritmo di firma utilizzato (default: SunX509)
