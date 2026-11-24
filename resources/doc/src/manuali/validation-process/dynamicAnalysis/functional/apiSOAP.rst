.. _releaseProcessGovWay_dynamicAnalysis_functional_apiSOAP:

Messaggi su API SOAP
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `TestNG <https://testng.org/doc/>`_ verificano le normali funzionalità di gateway per API SOAP, verificando comunicazioni con profilo oneway e request-response utilizzando messaggi SOAP 1.1 e 1.2 sia con che senza attachments. Vengono inoltre verificate sia le modalità stateless che la modalità con presa in carico. Infine viene verificata la corretta gestione dei SOAP Fault.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/src/.../soap/successful <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/src/org/openspcoop2/protocol/trasparente/testsuite/units/soap/successful/>`_.

Evidenze disponibili in :

- `risultati dei test su API SOAP per fruizioni <https://jenkins.link.it/govway4-testsuite/trasparente/PortaDelegata/default/>`_
- `risultati dei test su API SOAP per erogazioni <https://jenkins.link.it/govway4-testsuite/trasparente/PortaApplicativa/default/>`_

Sono disponibili ulteriori test che verificano le funzionalità SOAP descritte dai seguenti gruppi:

- `SOAPWithAttachments <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/soap/SOAPWithAttachments.java>`_; vengono verificati per tutti i profili di interazione (oneway, request-response, async pull o push) con messaggi SOAP With Attachments.
- `SOAPAction <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/soap/SOAPAction.java>`_; viene verificato che il gateway gestisca correttamente le possibili SOAPAction ricevute nell'header di trasporto HTTP.
- `SOAPBodyEmpty <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/soap/SOAPBodyEmpty.java>`_; viene verificato che il gateway gestisca correttamente messaggi senza SOAPBody (vuoto).
- `SOAPHeaderEmpty <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/soap/SOAPHeaderEmpty.java>`_; viene verificato che il gateway gestisca correttamente messaggi senza SOAPHeader.
- `SOAPMessageScorretti <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/messaggi_malformati>`_; viene verificato che il gateway gestisca correttamente messaggi scorretti sintatticamente o rispetto alla specifica soap (es. ContentType/Namespace diverso da quello atteso, headers non gestiti dalla PdD, strutture xml errate). Il gruppo 'SOAPMessageScorretti.STRUTTURA_ENVELOPE' verifica inoltre il rifiuto, su richiesta e risposta, di Envelope SOAP con struttura non conforme (più elementi Body o Header, ordine errato, Fault non unico figlio del Body, elementi dopo il Body) anche in presenza di firma WS-Security (XML Signature Wrapping).
- `TunnelSOAP <https://github.com/link-it/govway/tree/3.4.x/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/tunnel_soap/TunnelSOAP.java>`_; vengono verificate le funzionalità di imbustamento e sbustamento SOAP.

Evidenze disponibili in:

- `risultati dei gruppi 'SOAPWithAttachments', 'SOAPAction', 'SOAPBodyEmpty' e 'SOAPHeaderEmpty' <https://jenkins.link.it/govway4-testsuite/spcoop/SOAP/default/>`_
- `risultati del gruppo 'SOAPMessageScorretti' <https://jenkins.link.it/govway4-testsuite/spcoop/SOAPMessageScorretti/default/>`_
- `risultati del gruppo 'TunnelSOAP' <https://jenkins.link.it/govway4-testsuite/spcoop/Tunnel/default/>`_

Ulteriori test verificano la struttura dell'Envelope SOAP (SOAP 1.1 e 1.2) su erogazioni e fruizioni, sia sulla richiesta sia sulla risposta, con e senza i controlli opzionali per i messaggi gestiti senza costruzione del DOM (proprietà 'soap.envelopeStructure.reader.fullCheck.enabled' e 'soap.envelopeStructure.forward.check.enabled'), con messaggi entro ed oltre la soglia di bufferizzazione; per ogni caso vengono verificati l'errore restituito, l'esito della transazione, il diagnostico emesso e, con il controllo durante l'inoltro, il contenuto registrato confrontato byte per byte con il messaggio inviato. I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src/.../other/soap <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/other/soap/>`_.

Evidenze disponibili in `risultati dei test sulla struttura dell'Envelope SOAP <https://jenkins.link.it/govway4-testsuite/trasparente_karate/OtherSoap/html/>`_.

Altri test disponibili verificano la corretta gestione dell'header 'Content-Type' valorizzato con altri parametri oltre quelli previsti o valorizzato in maniera errata. I sorgenti sono disponibili in `protocolli/trasparente/testsuite/src/.../soap/integrazione <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/src/org/openspcoop2/protocol/trasparente/testsuite/units/soap/integrazione/>`_.

Evidenze disponibili in `risultati dei test su header Content-Type per API SOAP <https://jenkins.link.it/govway4-testsuite/trasparente/Integrazione/default/>`_.

Sono infine disponibili ulteriori test che verificano la 'funzionalità 'SOAPReader' per la lettura ottimizzata dei messaggi soap:

- `ReaderTest <https://github.com/link-it/govway/tree/3.4.x/core/src/org/openspcoop2/message/soap/reader/test/ReaderTest.java>`_; lettura della parte iniziale dell'Envelope, comprese le anomalie della sua struttura rilevabili durante la lettura;
- `OptimizedHeaderTest <https://github.com/link-it/govway/tree/3.4.x/core/src/org/openspcoop2/message/soap/reader/test/OptimizedHeaderTest.java>`_; gestione ottimizzata dell'header SOAP, eseguita con e senza i controlli sulla struttura dell'Envelope;
- `SoapEnvelopeStructureCheckTest <https://github.com/link-it/govway/tree/3.4.x/core/src/org/openspcoop2/message/soap/reader/test/SoapEnvelopeStructureCheckTest.java>`_; verifica della struttura dell'Envelope effettuata a livello di byte durante la lettura completa e durante l'inoltro in streaming, anche per messaggi multipart.

I test sono eseguiti tramite la classe `core/src/org/openspcoop2/pdd_test/.../message/TestSoapReader.java <https://github.com/link-it/govway/tree/3.4.x/core/src/org/openspcoop2/pdd_test/message/TestSoapReader.java/>`_.

Evidenze disponibili in `risultati dei test per la funzionalità 'SOAPReader' <https://jenkins.link.it/govway4-testsuite/core/pdd/#/>`_
