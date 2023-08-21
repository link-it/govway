.. _releaseProcessGovWay_dynamicAnalysis_functional_utils:

Utility di base del progetto
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test sono realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ e verificano le utility di base del progetto (certificati, firma, cifratura ...).

I sorgenti sono disponibili in `tools/utils/src/org/openspcoop2/utils/test <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test>`_ relativamente ai seguenti package:

- `org.openspcoop2.utils.test.cache <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/cache>`_; vengono verificate le varie implementazioni di cache disponibili.

- `org.openspcoop2.utils.test.certificate <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/certificate>`_; vengono verificate le utility che consentono di caricare e analizzare keystore (PKCS12, JKS, PKCS11, JWKs), certificati X.509 (DER, PEM) e JWK e protocolli di verifica dei certificati X.509 (CRL e OCSP).

- `org.openspcoop2.utils.test.crypt <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/crypt>`_; i test verificano la generazione casuale delle password e i meccanismi di digest.

- `org.openspcoop2.utils.test.csv <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/csv>`_; vengono verificate le utility che consentono di leggere file csv.

- `org.openspcoop2.utils.test.date <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/date>`_; i test verificano l'utility di formattazione delle date.

- `org.openspcoop2.utils.test.id <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/id>`_; vengono verificate le varie implementazioni che consentono di generare identificativi univoci (numerici, alfanumerici, uuid).

- `org.openspcoop2.utils.test.jdbc <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/jdbc>`_; vengono verificate le utility che consentono di utilizzare funzionalità JDBC specifiche.

- `org.openspcoop2.utils.test.json <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/json>`_; i test verificano le utility che consentono l'esecuzione di JsonPath e la validazione dei messaggi json tramite json schema.

- `org.openspcoop2.utils.test.logger <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/logger>`_; verifica il livello di logger applicativo definito nell'utility.

- `org.openspcoop2.utils.test.openapi <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/openapi>`_; vengono verificate le utility che consentono di leggere e validare interfacce OpenAPI 3 e Swagger 2.

- `org.openspcoop2.utils.test.pdf <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/pdf>`_; vengono verificate le utility che consentono di firmare documenti PDF e accedere ai documenti 'embedded' o 'XFA' interni al pdf stesso.

- `org.openspcoop2.utils.test.random <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/random>`_; i test verificano l'utility che consente di avere implementazioni differenti di generatori di numeri casuali (NativePRNG, SHA1PRNG, Windows-PRNG ...). 

- `org.openspcoop2.utils.test.regexp <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/regexp>`_; viene verificata l'utility che consente l'applicazione di espressioni regolari.

- `org.openspcoop2.utils.test.resource <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/resource>`_; i test verificano le utility che consentono di lavorare sugli stream.

- `org.openspcoop2.utils.test.rest <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/rest>`_; vengono verificate utility utilizzabili in contesto di API rest (es. Problem Detail RFC 7807).

- `org.openspcoop2.utils.test.security <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/security>`_; vengono verificate le utility che consentono di firmare/validare o di cifrare/decifrare messaggi. I test prevedono anche la validazione dei certificati X.509 utilizzati tramite CRL e OCSP. 

- `org.openspcoop2.utils.test.semaphore <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/semaphore>`_; viene verificato un semaforo implementato su database.

- `org.openspcoop2.utils.test.serializer <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/serializer>`_; i test verificano la serializzazione di java bean.

- `org.openspcoop2.utils.test.sonde <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/sonde>`_; i test verificano le utility che consentono di avere sonde applicative.

- `org.openspcoop2.utils.test.sql <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/sql>`_; i test verificano l'utility che consente di definire query SQL tramite oggetti java per poi otterenre lo statement SQL corretto su implementazioni differenti (oracle, postgrsql, mysql, hsql, sqlserver, db2).

- `org.openspcoop2.utils.test.transport <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/transport>`_; i test verificano l'utility che consente di ottenere un CORS Filter e l'utility che consente di trattere header HTTP compatibili con RFC 2047.

- `org.openspcoop2.utils.test.xacml <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/xacml>`_; i test verificano l'utility che consente di processare policy XACML.

- `org.openspcoop2.utils.test.xml <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/xml>`_; i test verificano la corretta gestione delle entity references, le utility che consentono di processare XQuery, effettuare diff di file xml. Inoltre viene verificato che non sia attuabile un attacco XML eXternal Entity injection (XXE).

- `org.openspcoop2.utils.test.xml2json <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/org/openspcoop2/utils/test/xml2json>`_; i test verificano l'utility che consentono la trasformazione di messaggi xml in json e viceversa.


Evidenze disponibili in:

- `Utility di base <https://jenkins.link.it/govway-testsuite/core/utils/#/>`_  
- `Utility di base per il database <https://jenkins.link.it/govway-testsuite/core/utils-sql/#/>`_   
