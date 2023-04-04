.. _avanzate_connettori_https_override_jvm:

Repository delle configurazioni https
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Per un endpoint https viene utilizzata per default la configurazione https impostata nella JVM dell'Application Server (proprietà javax.net.ssl.*) descritta nella sezione :ref:`install_ssl_client_direct`.

È possibile configurare GovWay in modo che utilizzi una configurazione https differente o definendo tramite la console di gestione un'autenticazione personalizzata (soluzione descritta nella sezione :ref:`avanzate_connettori_https`) o creando un repository di configurazioni definite tramite file di proprietà. Quest'ultima modalità viene descritta in questa sezione.

Per abilitare l'utilizzo di un repository delle configurazioni https devono essere registrate le seguenti :ref:`configProprieta` sull'erogazione o sulla fruizione:

- *connettori.httpsEndpoint.jvmConfigOverride.enabled*: consente di abilitare o disabilitare la funzionalità; i valori associabili alle proprietà sono 'true' o 'false'. Per default la funzionalità è disabilitata.

- *connettori.httpsEndpoint.jvmConfigOverride.repository*: consente di indicare la directory contenente i file di proprietà (default: '/etc/govway/https/erogazioni' per le erogazioni e '/etc/govway/https/fruizioni' per le fruizioni).

- *connettori.httpsEndpoint.jvmConfigOverride.config*: consente di indicare il nome del file di proprietà da utilizzare che deve esistere all'interno del repository riferito nella precedente proprietà (default: '<TIPO_EROGATORE><NOME_EROGATORE>.properties' per le erogazioni e '<TIPO_FRUITORE><NOME_FRUITORE>.properties' per le fruizioni). Il nome del file indicato può contenere delle macro, risolte a runtime dal gateway, per creare dei path dinamici (per ulteriori dettagli si rimanda alla sezione :ref:`valoriDinamici`).

I valori di default, per quanto concerne l'abilitazione della funzionalità e il repository delle configurazione, possono essere impostati anche a livello globale nel file 'govway_local.properties' tramite le proprietà descritte di seguito che variano per erogazioni e fruizioni.

Proprietà a livello globale per le erogazioni:

   ::

      org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.http.urlHttps.overrideDefaultConfiguration=false
      org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.http.urlHttps.repository=<directory-lavoro>/https/erogazioni

Proprietà a livello globale per le fruizioni:

   ::

      org.openspcoop2.pdd.connettori.inoltroBuste.http.urlHttps.overrideDefaultConfiguration=false
      org.openspcoop2.pdd.connettori.inoltroBuste.http.urlHttps.repository=<directory-lavoro>/https/fruizioni


**Proprietà per la personalizzazione della configurazione https**

Le proprietà che consentono di personalizzare l'autenticazione server sono le seguenti:

- trustAllCerts (true/false): se abilitata non viene attuata l'autenticazione del certificato server e viene accettato qualsiasi certificato restituito dal server;

- trustStoreLocation: path dove è localizzato il TrustStore contenente i certificati server trusted;

- trustStoreType (jks, pkcs12): tipologia del TrustStore; se registrati potranno essere indicati anche i tipi di keystore PKCS11 (per ulteriori dettagli si rimanda alla sezione :ref:`pkcs11`);

- trustStorePassword: password per l'accesso al TrustStore;

- trustStoreOCSPPolicy: policy OCSP da utilizzare per validare il certificato server; per ulteriori dettagli si rimanda alla sezione :ref:`ocsp`;

- trustStoreCRLs: path dove è presente una CRL da utilizzare per validare i certificati server; ne possono essere indicate più di una separando i path con la virgola;

- trustManagementAlgorithm: consente di indicare un algoritmo differente da quello di default (PKIX).

Le proprietà che consentono di personalizzare l'autenticazione client, indicando il keystore contenente la chiave privata che si deve utilizzare durante la sessione TLS, sono le seguenti:

- keyStoreLocation: path dove è localizzato il Keystore;

- keyStoreType (jks, pkcs12): tipologia del Keystore; se registrati potranno essere indicati anche i tipi di keystore PKCS11 (per ulteriori dettagli si rimanda alla sezione :ref:`pkcs11`);

- keyStorePassword: password per l'accesso al Keystore;

- keyPassword: password per accedere alla chiave privata presente nel keystore;

- keyAlias: alias che individua la chiave privata, presente nel keystore, da utilizzare. L'indicazione di un alias è opzionale e se non fornito viene utilizzata la prima chiave trovata.

- keyManagementAlgorithm: consente di indicare un algoritmo differente da quello di default (SunX509).

Sono inoltre disponibili le seguenti proprietà:

- hostnameVerifier (true/false): attiva la verifica in fase di autenticazione server della corrispondenza tra l'hostname indicato nella url e quello presente nel certificato server ritornato dal server (nel subject CN=<hostname>).

- sslType: tipo e versione del protocollo di trasporto (es. TLSv1.2). Sono selezionabili tutti i tipi supportati dalla versione della jvm utilizzata.

- secureRandom (true/false): indicazione se deve essere utilizzato un 'Secure Random'.

- secureRandomAlgorithm: consente di indicare un algoritmo 'secure random' differente da quello di default.
