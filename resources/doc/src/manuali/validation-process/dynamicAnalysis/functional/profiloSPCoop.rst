.. _releaseProcessGovWay_dynamicAnalysis_functional_profiloSPCoop:

Profilo "SPCoop"
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `TestNG <https://testng.org/doc/>`_ verificano tutte le funzionalità previsto dal profilo di interoperabiltà 'SPCoop'.

I sorgenti sono disponibili in `protocolli/spcoop/testsuite/src <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/>`_ relativamente ai seguenti gruppi:

- `ProfiliDiCollaborazione <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/profili/ProfiliDiCollaborazione.java/>`_; vengono verificati i quattro profili di collaborazione: oneway, sincrono, asincronoSimmetrico e asincronoAsimmetrico.
- `PortTypes <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/profili/PortTypes.java/>`_; vengono verificati i quattro profili di collaborazione configurati tramite accordi con port types multipli.
- `FiltroDuplicatiEGov <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/profili/FiltroDuplicatiEGov.java/>`_; viene verificata la funzionalita' di filtro e-gov dei duplicati per i 4 profili di collaborazione.
- `FunzionalitaEGov <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/profili/FunzionalitaEGov.java/>`_; vengono verificate tutte le funzionalita prevista per la busta e-Gov quali oltre al filtro dei duplicati: 'scadenza', 'riscontri', 'consegna in ordine'.

- `BusteEGovCampiDuplicati <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/buste/BusteEGovCampiDuplicati.java/>`_; vengono generate buste SPCoop che contengono campi duplicati e viene controllato che l'errore sia correttamente segnalato e gestito.
- `BusteEGovScorrette <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/buste/BusteEGovScorrette.java/>`_; vengono generate buste SPCoop che contengono anomalie e viene controllato che l'errore sia correttamente segnalato e gestito.
- `BusteEGovConEccezioni <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/buste/BusteEGovConEccezioni.java/>`_; vengono generate buste SPCoop che contengono una lista di eccezioni e viene controllato che l'eccezione sia correttamente segnalata e gestita.
- `BusteEGovNamespace <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/buste/BusteEGovNamespace.java/>`_; vengono generate buste SPCoop che contengono strutture dati dove si annidano ridefinizioni di namespace.

- `ProfiliDiCollaborazioneLineeGuida11 <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/profili_linee_guida/ProfiliDiCollaborazioneLineeGuida11.java/>`_; vengono verificati i quattro profili di collaborazione (oneway, sincrono, asincronoSimmetrico e asincronoAsimmetrico) rispetto alle Linee Guida 1.1 della Busta e-Gov 1.1.
- `BusteEGov11LineeGuida11 <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/profili_linee_guida/BusteEGov11LineeGuida11.java/>`_; vengono verificate le anomalie relative rispetto alle Linee Guida 1.1 della Busta e-Gov 1.1.

- `RiconoscimentoProfiloGestione <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/others/RiconoscimentoProfiloGestione.java/>`_; viene verificata la funzionalità di identificazione del corretto profilo di gestone (vecchia versione egov o nuove linee guida).

- `ErroreApplicativoCNIPA e OpenSPCoopDetail <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/errori_applicativi/>`_; viene verificato che l'elemento 'detail' in un SOAPFault sia valorizzato rispetto all'elemento 'eGov_IT_Ecc:MessaggioDiErroreApplicativo' definito dal documento CNIPA 'Porta di Dominio' e all'elemento 'dettaglio-eccezione' nello schema `core/src/schemi/openspcoopDetail.xsd <https://github.com/link-it/govway/tree/master/core/src/schemi/openspcoopDetail.xsd/>`_.


Evidenze disponibili in:

- `risultati dei test per la gestione del profilo SPCoop <https://jenkins.link.it/govway-testsuite/spcoop/Profili/default/>`_
- `risultati dei test per la gestione del profilo SPCoop rispetto alle linee guida 1.1 <https://jenkins.link.it/govway-testsuite/spcoop/ProfiliLineeGuida/default/>`_
- `risultati dei test effettuati con buste non corrette <https://jenkins.link.it/govway-testsuite/spcoop/Buste/default/>`_
- `risultati dei test per il riconoscimento del corretto profilo di gestione <https://jenkins.link.it/govway-testsuite/spcoop/Others/default/>`_
- `risultati dei test per la gestione del dettaglio 'eGov_IT_Ecc:MessaggioDiErroreApplicativo' di un SOAPFault <https://jenkins.link.it/govway-testsuite/spcoop/ErroreApplicativoCNIPA/default/>`_
- `risultati dei test per la gestione del dettaglio 'dettaglio-eccezione' di un SOAPFault <https://jenkins.link.it/govway-testsuite/spcoop/OpenSPCoopDetail/default/>`_



