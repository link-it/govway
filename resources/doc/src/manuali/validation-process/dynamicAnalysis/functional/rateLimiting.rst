.. _releaseProcessGovWay_dynamicAnalysis_functional_rateLimiting:

Rate Limiting
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test sono realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ e verificano tutte le funzionalità relative al controllo del traffico.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/>`_ relativamente ai seguenti gruppi:

- `rate_limiting.numero_richieste <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/numero_richieste>`_; vengono verificate policy definite con metriche basate sul numero di richieste simultanee e richieste gestibili in un intervallo temporale.
- `rate_limiting.numero_richieste_fallite <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/numero_richieste_fallite>`_; vengono verificate policy definite con metriche basate sul numero di richieste fallite in un intervallo temporale.
- `rate_limiting.numero_richieste_fallite_o_fault <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/numero_richieste_fallite_o_fault>`_; vengono verificate policy definite con metriche basate sul numero di richieste fallite o fault in un intervallo temporale.
- `rate_limiting.numero_richieste_fault <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/numero_richieste_fault>`_; vengono verificate policy definite con metriche basate sul numero di fault in un intervallo temporale.
- `rate_limiting.numero_richieste_completate_con_successo <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/numero_richieste_completate_con_successo>`_; vengono verificate policy definite con metriche basate sul numero di richieste completate con successo in un intervallo temporale.
- `rate_limiting.dimensione_messaggi <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/dimensione_messaggi>`_; vengono verificate policy definite con metriche basate sulla dimensione massima delle richieste e delle risposte.
- `rate_limiting.occupazione_banda <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/occupazione_banda>`_; vengono verificate policy definite con metriche basate sull'occupazine di banda in un intervallo temporale.
- `rate_limiting.tempo_complessivo_risposta <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/tempo_complessivo_risposta>`_; vengono verificate policy definite con metriche basate sul tempo complessivo di risposta in un intervallo temporale.
- `rate_limiting.tempo_medio_risposta <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/tempo_medio_risposta>`_; vengono verificate policy definite con metriche basate sul tempo medio di risposta in un intervallo temporale.
- `rate_limiting.filtri <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/filtri>`_; vengono verificati i filtri associabili alle policy.
- `rate_limiting.raggruppamento <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/raggruppamento>`_; vengono verificati i criteri di conteggio associabili alle policy.
- `rate_limiting.flusso <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/flusso>`_; vengono verificati i criteri ordinamento dell'applicabilità delle policy.
- `rate_limiting.warning_only <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/warning_only>`_; vengono verificate policy definite con stato warning_only, controllando anche gli eventi generati con stesso stato.
- `rate_limiting.congestione <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/congestione>`_; vengono verificate policy che usano l'applicabilità con degrado prestazionale e/o congestione anche rispetto agli eventi generati.
- `rate_limiting.global_policy <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/global_policy>`_; vengono verificate policy definite a livello globale.
- `rate_limiting.custom_policy <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/rate_limiting/custom_policy>`_; vengono verificate policy che usano intervalli statistici di campionamento.

Evidenze disponibili in:

- `risultati dei test del gruppo 'rate_limiting.numero_richieste' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingNumeroRichieste/html/>`_
- `risultati dei test del gruppo 'rate_limiting.numero_richieste_fallite' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingNumeroRichiesteFallite/html/>`_
- `risultati dei test del gruppo 'rate_limiting.numero_richieste_fallite_o_fault' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingNumeroRichiesteFalliteOFault/html/>`_
- `risultati dei test del gruppo 'rate_limiting.numero_richieste_fault' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingNumeroRichiesteFault/html/>`_
- `risultati dei test del gruppo 'rate_limiting.numero_richieste_completate_con_successo' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingNumeroRichiesteCompletateSuccesso/html/>`_
- `risultati dei test del gruppo 'rate_limiting.dimensione_messaggi per API Rest' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingDimensioneMessaggiREST/html/>`_
- `risultati dei test del gruppo 'rate_limiting.dimensione_messaggi per API Soap' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingDimensioneMessaggiSOAP/html/>`_  
- `risultati dei test del gruppo 'rate_limiting.occupazione_banda' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingOccupazioneBanda/html/>`_
- `risultati dei test del gruppo 'rate_limiting.tempo_complessivo_risposta' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingTempoComplessivoRisposta/html/>`_
- `risultati dei test del gruppo 'rate_limiting.tempo_medio_risposta' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingTempoMedioRisposta/html/>`_
- `risultati dei test del gruppo 'rate_limiting.filtri' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingFiltri/html/>`_
- `risultati dei test del gruppo 'rate_limiting.raggruppamento' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingRaggruppamento/html/>`_
- `risultati dei test del gruppo 'rate_limiting.flusso' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingFlusso/html/>`_
- `risultati dei test del gruppo 'rate_limiting.warning_only' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingWarningOnly/html/>`_
- `risultati dei test del gruppo 'rate_limiting.congestione' per API Rest <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingCongestioneREST/html/>`_
- `risultati dei test del gruppo 'rate_limiting.congestione' per API Soap <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingCongestioneSOAP/html/>`_  
- `risultati dei test del gruppo 'rate_limiting.global_policy' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingGlobalPolicy/html/>`_
- `risultati dei test del gruppo 'rate_limiting.custom_policy' per API Rest <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingCustomPolicyREST/html/>`_
- `risultati dei test del gruppo 'rate_limiting.custom_policy' per API Soap <https://jenkins.link.it/govway-testsuite/trasparente_karate/RateLimitingCustomPolicySOAP/html/>`_  



