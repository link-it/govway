.. _releaseProcessGovWay_dynamicAnalysis_security_message:

Sicurezza Messaggio
~~~~~~~~~~~~~~~~~~~~~

**WSSecurity per API SOAP**

I test realizzati tramite il tool `TestNG <https://testng.org/doc/>`_ verificano tutte le funzionalità di cifratura, firma, SAML per WSSecurity su API SOAP.

I sorgenti sono disponibili in `protocolli/spcoop/testsuite/src <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src>`_ relativamente ai seguenti package:

- `org.openspcoop2.protocol.spcoop.testsuite.units.sicurezza <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/sicurezza>`_

Il gruppo 'SOAPMessageScorretti.STRUTTURA_ENVELOPE_XSW_WSS_SIGNATURE' (package `org.openspcoop2.protocol.spcoop.testsuite.units.messaggi_malformati <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/messaggi_malformati>`_) verifica inoltre il rifiuto dei messaggi costruiti per attacchi di tipo XML Signature Wrapping (XSW), in cui la firma WS-Security risulta valida ma copre un Body diverso da quello elaborato.

Evidenze disponibili in:

- `WSSecurity <https://jenkins.link.it/govway4-testsuite/spcoop/Sicurezza/default/>`_
- `XML Signature Wrapping (XSW) <https://jenkins.link.it/govway4-testsuite/spcoop/SOAPMessageScorretti/default/>`_

Sono inoltre disponibili ulteriori test che verificano la funzionalità 'Username Token', il rifiuto dei messaggi con struttura dell'Envelope SOAP non conforme o con elemento firmato o cifrato diverso dal Body effettivo, come negli attacchi di tipo XML Signature Wrapping (XSW), e la verifica degli elementi firmati e cifrati attesi in ricezione per tutte le combinazioni configurabili da console (Body, Timestamp, Attachments) rispetto agli elementi protetti in spedizione, anche su messaggi SOAP With Attachments.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src>`_ relativamente ai seguenti package:

- `org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/other/wssecurity>`_

Evidenze disponibili in:

- `WSSecurity (Username, Struttura Envelope, Elementi attesi) <https://jenkins.link.it/govway4-testsuite/trasparente_karate/OtherWSSecurity/html/>`_

**JOSE per API REST**

I test realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ verificano tutte le funzionalità di cifratura e firma per JOSE su API REST.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/src <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/src>`_ relativamente ai seguenti package:

- `org.openspcoop2.protocol.trasparente.testsuite.units.rest.jose <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/src/org/openspcoop2/protocol/trasparente/testsuite/units/rest/jose>`_

Evidenze disponibili in:

- `JOSE <https://jenkins.link.it/govway4-testsuite/trasparente/JOSE/default/>`_
