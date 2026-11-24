.. _sicurezzaLivelloMessaggioJOSEPayloadEnrichment:

JWS Compact Payload Enrichment
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Lo schema, disponibile solo in spedizione (Sender), arricchisce il payload JSON con i claims standard di un JWT e lo firma in formato JWS Compact: il messaggio inviato è quindi un token JWT firmato il cui payload è costituito dal messaggio originale arricchito.

Vengono aggiunti al payload i seguenti claims:

- *iss*: valore indicato nel campo *Issuer*;
- *aud*: valore indicato nel campo *Audience*;
- *iat* e *nbf*: istante di generazione;
- *exp*: istante di generazione incrementato del valore indicato nel campo *TTL (secondi)*;
- *jti*: identificativo della transazione gestita da GovWay.

I valori di *Issuer* e *Audience* possono contenere parti dinamiche, risolte a runtime, con la sintassi descritta nella sezione :ref:`valoriDinamici`.

La :numref:`josePayloadEnrichmentFig` mostra la maschera configurata sulla risposta di un'erogazione.

   .. figure:: ../../../_figure_console/SicurezzaMessaggioJOSESenderPayloadEnrichment.png
    :scale: 70%
    :align: center
    :name: josePayloadEnrichmentFig

    Spedizione: JWS Compact Payload Enrichment sulla risposta

**Signature**

- *Signature Algorithm* (default: RS256): algoritmo di firma, come per lo schema :ref:`sicurezzaLivelloMessaggioJOSESignature`;
- *Content-Type* (default: 'application/jwt'): Content-Type del messaggio firmato;
- *KeyStore*: keystore contenente la chiave di firma, come per lo schema :ref:`sicurezzaLivelloMessaggioJOSESignature`.

**JWT Claims**

- *Claims Esistenti* (default: *Preserva*): comportamento da adottare se un claim da aggiungere è già presente nel payload: *Preserva* mantiene il valore originale, *Sovrascrivi* lo sostituisce, *Rifiuta con errore* termina la transazione con errore;
- *Audience*: valore del claim *aud*; nella configurazione della risposta di un'erogazione è possibile scegliere *ClientId del voucher*, che utilizza il soggetto (claim *sub*) del token ricevuto nella richiesta, oppure *Configurazione manuale*;
- *Issuer*: valore del claim *iss*;
- *TTL (secondi)*: validità del token, utilizzata per calcolare il claim *exp*.

**Applicabilità** (solo sulla risposta)

- *Codici HTTP* (default: 200): codici di stato HTTP della risposta del backend per i quali viene applicato l'arricchimento; si possono indicare più codici separati da ',' e intervalli (es. '200-299').
