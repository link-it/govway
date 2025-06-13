.. _modipa_tracingpdnd_properties:

Configurazione Avanzata Proprietà
---------------------------------

Nel caso in cui le componenti di generazione e pubblicazione vengano installate sotto forma di batch esterni, le proprietà descritte di seguito dovranno essere inserite nella directory ``batch/generatoreStatistiche/properties``. In alternativa, nella modalità predefinita (componenti gestite tramite timer), tali proprietà possono essere impostate direttamente nel file `govway_local.properties`.

Di seguito sono elencate le proprietà configurabili per personalizzare l'esecuzione di queste componenti:

.. note::
   Le seguenti proprietà seguono la nomenclatura prevista per l'installazione tramite batch. Se invece si utilizza la modalità predefinita (componenti gestite da timer), il nome delle proprietà avrà come prefisso ``org.openspcoop2.pdd.``
   
   Esempio:
   ``org.openspcoop2.pdd.statistiche.pdnd.tracciamento.maxAttempts=3``

**Numero massimo di tentativi**

Nel caso in cui la comunicazione con la PDND fallisca, è possibile ripetere l’invio a ogni esecuzione della componente di pubblicazione. La seguente proprietà, se definita, impone un numero massimo di tentativi:

``statistiche.pdnd.tracciamento.maxAttempts=3``

**Soggetti abilitati**

Questa proprietà definisce, nel caso di ambiente multi-tenant, quali soggetti hanno il supporto abilitato per il tracing PDND. La proprietà può essere sovrascritta nella sezione specifica del soggetto nella console di gestione:

``statistiche.pdnd.tracciamento.soggetti.enabled=[NOME_SOGGETTO1],[NOME_SOGGETTO2]``

**Abilitazione Fruizioni/Erogazioni**

Per abilitare o disabilitare la raccolta delle transazioni che riguardano le erogazioni o le fruizioni, è sufficiente impostare le seguenti proprietà:

``statistiche.pdnd.tracciamento.erogazioni.enabled=true``
``statistiche.pdnd.tracciamento.fruizioni.enabled=true``

**Comunicazione con la PDND**

.. note::
   Le proprietà elencate possono essere definite a livello globale o specifico per soggetto (in ambienti multi-tenant). Per rendere una proprietà specifica per un soggetto, aggiungere il nome del soggetto dopo la parte ``statistiche.pdnd.tracciamento``.

   Esempio:
   ``statistiche.pdnd.tracciamento.ENTE.baseUrl=[URL]`` sovrascrive il valore globale di ``statistiche.pdnd.tracciamento.baseUrl`` solo per il soggetto ``ENTE``.

Le seguenti proprietà devono essere impostate per permetterre alla componente di pubblicazione dei tracciati di comunicare con la fruizione built-in installata automaticamente su GovWay come descritto nella sezione precedente :doc:`fruizioneBuiltIn`

- URL della fruizione usata per la comunicazione:

  ``statistiche.pdnd.tracciamento.baseUrl=[URL]``

- Credenziali username/password (per autenticazione Basic):

  ``statistiche.pdnd.tracciamento.http.username=[USERNAME]``
  ``statistiche.pdnd.tracciamento.http.password=[PASSWORD]``

- Header da includere in ogni richiesta:

  ``statistiche.pdnd.tracciamento.DemoSoggettoErogatore.http.headers=[NOME1]:[VALORE1],[NOME2]:[VALORE2]``

- Parametri da includere nella query di ogni richiesta:

  ``statistiche.pdnd.tracciamento.http.queryParameters=[NOME1]:[VALORE1],[NOME2]:[VALORE2]``

- Parametri relativi ai timeout di connessione:

  ``statistiche.pdnd.tracciamento.readTimeout=[READ_TIMEOUT]``
  ``statistiche.pdnd.tracciamento.connectTimeout=[CONNECTION_TIMEOUT]``

- Proprietà per autenticazione HTTPS:

  ``statistiche.pdnd.tracciamento.https.hostnameVerifier=true``
  ``statistiche.pdnd.tracciamento.https.trustAllCerts=false``
  ``statistiche.pdnd.tracciamento.https.trustStore=[PATH_TRUSTSTORE]``
  ``statistiche.pdnd.tracciamento.https.trustStore.password=[PASSWORD_TRUSTSTORE]``
  ``statistiche.pdnd.tracciamento.https.trustStore.type=jks``
  ``statistiche.pdnd.tracciamento.https.trustStore.crl=[PATH_CRL]``
  ``statistiche.pdnd.tracciamento.https.keyStore=[PATH_KEYSTORE]``
  ``statistiche.pdnd.tracciamento.https.keyStore.password=[PASSWORD_KEYSTORE]``
  ``statistiche.pdnd.tracciamento.https.keyStore.type=jks``
  ``statistiche.pdnd.tracciamento.https.key.alias=[ALIAS]``
  ``statistiche.pdnd.tracciamento.https.key.password=[PASSWORD_KEY]``
