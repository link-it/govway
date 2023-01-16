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

   -  **Verifica Hostname** (true/false): Attiva la verifica in fase di
      autenticazione server della corrispondenza tra l'hostname indicato
      nella url e quello presente nel certificato server ritornato dal
      server (nel subject CN=hostname)

-  *Autenticazione Server*

   I certificati server saranno validati tramite la configurazione indicata di seguito. Per accettare qualsiasi certificato restituito dal server è possibile disattivare la **Verifica**.

   -  **Path**: Path dove è localizzato il truststore contenente i
      certificati server trusted.

   -  **Tipo** (jks, pkcs12): Tipologia del TrustStore (default: jks). Se registrati saranno disponibili anche i tipi di keystore PKCS11; per ulteriori dettagli si rimanda alla sezione :ref:`pkcs11`.

   -  **Password**: Password per l'accesso al TrustStore.

   -  **OCSP Policy**: Policy OCSP da utilizzare per validare il certificato server; per ulteriori dettagli si rimanda alla sezione :ref:`ocsp`.

   -  **CRL File(s)**: Path dove è presente una CRL da utilizzare per validare i certificati server. L'indicazione di una CRL è opzionale e ne possono essere indicate più di una separando i path con la virgola.

-  *Autenticazione Client (opzionale)*

   Abilitando la checkbox **Abilitato** è possibile configurare il certificato client che verrà inoltrato al server.

   -  **Dati di Accesso al KeyStore** (usa valori del TrustStore,
      Ridefinisci): Consente di riutilizzare i medesimi riferimenti del
      TrustStore anche per il KeyStore o in alternativa ridefinirli.

   -  **Tipo (solo se Dati di Accesso ridefiniti)** (jks, pkcs12): Tipologia del Keystore (default: jks). Se registrati saranno disponibili anche i tipi di keystore PKCS11; per ulteriori dettagli si rimanda alla sezione :ref:`pkcs11`.

   -  **Password (solo se Dati di Accesso ridefiniti)**: Password per
      l'accesso al Keystore.

   -  **Password Chiave Privata**: Password per accedere alla chiave
      privata presente nel keystore.

   -  **Alias Chiave Privata**: Alias che individua la chiave privata, presente nel keystore, da utilizzare. L'indicazione di un alias è opzionale e se non fornito viene utilizzata la prima chiave trovata.
