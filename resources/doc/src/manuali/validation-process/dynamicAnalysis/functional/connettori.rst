.. _releaseProcessGovWay_dynamicAnalysis_functional_connettori:

Connettore
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ verificano i connettori disponibili e le funzionalità associate.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/>`_ relativamente ai seguenti gruppi:

- `connettori.timeout <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/timeout>`_; vengono verificati i parametri di 'connection timeout' e 'read timeout' impostati relativi alla connessione e alla lettura dei messaggi di richiesta e risposta.
- `connettori.errori <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/errori>`_; verifica che, su API SOAP, eventuali risposte di errore senza un payload o con un payload html vengano gestite correttamente. 
- `connettori.applicativo_server <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/applicativo_server>`_; viene verificato il funzionamento degli applicativi di tipo server.
- `connettori.redirect <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/redirect>`_; viene verificata la funzionalità di 'follow redirect' e renaming dell'header Location come 'proxy pass'.
- `connettori.consegna_condizionale <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/consegna_condizionale>`_; viene verificata la funzionalità dei connettori multipli con tipo 'Consegna Condizionale'.
- `connettori.load_balancer <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/load_balancer>`_; viene verificata la funzionalità dei connettori multipli con tipo 'Load Balancer'.
- `connettori.consegna_multipla <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/consegna_multipla>`_; viene verificata la funzionalità dei connettori multipli con tipo 'Consegna Multipla'.
- `connettori.consegna_con_notifiche <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/consegna_con_notifiche>`_; viene verificata la funzionalità dei connettori multipli con tipo 'Consegna con Notifiche'.
- `connettori.proxy_pass <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/proxy_pass>`_; viene verificata la funzionalità relativa alle regole di proxy pass.
- `connettori.override_jvm <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/override_jvm>`_; viene verificata la funzionalità che consente di modificare la configurazione jvm utilizzata per endpoint https, consentendo di personalizzare i keystore utilizzati tramite la definizione di un file di proprietà.
- `connettori.tls <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/tls>`_; viene verificata l'utilizzo di un keystore pkcs12 creato a partire a sua volta da un altro keystore PKCS12. Il test serve a verificare che non si ripresenti la problematica descritta nell'issue `128 <https://github.com/link-it/govway/issues/128>`_. Vengono inoltre verificati tutti i metodi HTTP (DELETE, GET, POST, PUT, HEAD, OPTIONS, PATCH, TRACE, LINK, UNLINK) sia su connessioni http che https in cui il protocollo viene gestito tramite parametri della JVM o tramite la personalizzazione attuabile tramite il connettore https. Infine vengono verificati l'utilizzo di truststore e keystore jks e pkcs12 accessibili senza password.
- `connettori.forward_proxy <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/forward_proxy>`_; viene verificata la funzionalità :ref:`avanzate_govway_proxy`.
- `connettori.autenticazione <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/autenticazione>`_; viene verificata la funzionalità :ref:`avanzate_connettori_http` e :ref:`avanzate_connettori_apikey`.
- `connettori.header <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/header>`_; viene verificata la funzionalità 'MIME encoded-word' descritta nella sezione :ref:`avanzate_connettori_encodedWord`.
- `connettori.status <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/header>`_; viene verificata la funzionalità che permette di conoscere lo stato di un servizio, descritta nella sezione :ref:`avanzate_connettori_status`.
- `connettori.opzioni_avanzate <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/opzioni_avanzate>`_; viene verificata l'opzione avanzata 'Modalità Data Transfer' descritta nella sezione :ref:`avanzate_connettori_httpOpzioniAvanzate` e la configurazione che consente di utilizzare un proxy http come descritto nella sezione :ref:`avanzate_connettori_proxy`.

Evidenze disponibili in:

- `risultati dei test del gruppo 'connettori.timeout' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriTimeout/html/>`_
- `risultati dei test del gruppo 'connettori.errori' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriErrori/html/>`_
- `risultati dei test del gruppo 'connettori.applicativo_server' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriApplicativoServer/html/>`_
- `risultati dei test del gruppo 'connettori.redirect' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriRedirect/html/>`_ 
- `risultati dei test del gruppo 'connettori.consegna_condizionale' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriConsegnaCondizionale/html/>`_ 
- `risultati dei test del gruppo 'connettori.load_balancer' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriLoadBalancer/html/>`_ 
- `risultati dei test del gruppo 'connettori.consegna_multipla' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriConsegnaMultipla/html/>`_ 
- `risultati dei test del gruppo 'connettori.consegna_con_notifiche' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriConsegnaConNotifiche/html/>`_ 
- `risultati dei test del gruppo 'connettori.proxy_pass' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriProxyPass/html/>`_ 
- `risultati dei test del gruppo 'connettori.override_jvm' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriOverrideJvm/html/>`_ 
- `risultati dei test del gruppo 'connettori.tls' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriTls/html/>`_ 
- `risultati dei test del gruppo 'connettori.forward_proxy' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriForwardProxy/html/>`_ 
- `risultati dei test del gruppo 'connettori.autenticazione' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriAutenticazione/html/>`_ 
- `risultati dei test del gruppo 'connettori.header' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriHeader/html/>`_ 
- `risultati dei test del gruppo 'connettori.status' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriStatus/html/>`_ 
- `risultati dei test del gruppo 'connettori.opzioni_avanzate' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ConnettoriOpzioniAvanzate/html/>`_ 

Sono inoltre disponibili ulteriori test realizzati tramite il tool `TestNG <https://testng.org/doc/>`_ i cui sorgenti sono disponibili in `protocolli/spcoop/testsuite/src <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/connettori>`_ relativamente ai seguenti gruppi:

- `ConnettoriDiversiHTTP <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/connettori/ConnettoriDiversiHTTP.java>`_; vengono verificati i connettori built-in diversi da http e https (es. JMS, File, null, echo).
- `HTTPS <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/connettori/HTTPS.java>`_; verifica il corretto funzionamento del connettore https e dell'autenticazione 'tls'.
- `LetturaCredenzialiIngresso <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/connettori/LetturaCredenzialiIngresso.java>`_; verifica il meccasnimo di plugin per l'implementazione di un gestore delle credenziali.
- `VerificaTimeoutGestioneContentLength <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/connettori/VerificaTimeoutGestioneContentLength.java>`_; vengono verificate che le connessioni gestite tramite content-length impostato nell'header http di risposta non provochino attese dovute all'impostazione di un content length errato.
- `Servizio Integration Manager <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/integration_manager>`_; viene verificato il servizio 'Message Box'.
- `UrlPrefixRewriter <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/others/UrlPrefixRewriter.java>`_; vengono verificate le funzionalita' di 'pd-url-prefix-rewriter' e 'pa-url-prefix-rewriter'.

Evidenze disponibili in:

- `risultati dei test sui connettori <https://jenkins.link.it/govway4-testsuite/spcoop/Connettori/default/>`_
- `risultati dei test sul servizio Integration Manager <https://jenkins.link.it/govway4-testsuite/spcoop/IntegrationManager/default/>`_
- `risultati dei test sulla funzionalità di 'UrlPrefixRewriter' <https://jenkins.link.it/govway4-testsuite/spcoop/Others/default/>`_

