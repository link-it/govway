.. _modipa_signalhub_pseudoanonimizzazione_conformitaPdnd:

Conformità alla specifica PDND
--------------------------------

Le scelte implementative adottate da GovWay sono allineate al `Manuale Operativo Signal Hub <https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-signal-hub>`__ secondo i seguenti aspetti:

1. **Schema del segnale**: i campi ``signalId``, ``objectType``, ``objectId``, ``eserviceId``, ``signalType`` (con valori ``CREATE``, ``UPDATE``, ``DELETE`` e ``SEEDUPDATE``) sono prodotti secondo lo schema OpenAPI ufficiale di PDND.

2. **Endpoint informazioni crittografiche**: la risposta espone i due campi ``seed`` e ``cryptoHashFunction`` previsti dal manuale.

3. **Funzioni di hash**: gli algoritmi supportati (SHA-256, SHA-512/256, SHA-384, SHA-512, SHA3-256, SHA3-384, SHA3-512, SHAKE128, SHAKE256) coincidono con la lista raccomandata dal manuale per le diverse tipologie di dato (identificazione diretta, dati sensibili, dati giudiziari).

4. **Dimensione del seme**: la dimensione configurabile (16/32/64 byte raw → 24/44/88 caratteri Base64) soddisfa i minimi di sicurezza prescritti dal manuale per ciascuna tipologia di dato. La generazione tramite ``SecureRandom`` garantisce piena entropia, superiore a quella di un UUID indicato come esempio nel manuale.

5. **Concatenazione input/seed**: il manuale PDND prescrive esplicitamente che *"l'ordine con cui concatenare input e seed sia documentato dall'erogatore"*. L'ordine adottato da GovWay è ``objectId_originale || seed`` ed è personalizzabile come descritto nella sezione :ref:`modipa_signalhub_properties`.

6. **Encoding Base64**: il manuale dichiara ``objectId`` e ``seed`` come campi di tipo ``string`` senza prescrivere uno specifico encoding, demandando all'erogatore la documentazione del formato. La scelta di Base64 deriva dall'esigenza di trasportare in modo sicuro all'interno di campi testuali (JSON, XML) sequenze di byte binari arbitrari prodotti dal generatore casuale (per il seed) e dalla funzione di hash (per l'``objectId``), senza ricorrere a rappresentazioni più verbose come l'esadecimale. Si tratta di una scelta puramente di trasporto: la sicurezza è garantita dall'algoritmo di hash e dal seed, non dall'encoding.

Gli esempi presenti nel `Manuale Operativo Signal Hub <https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-signal-hub>`__ — seed rappresentato come UUID, ``objectId`` rappresentato come stringa esadecimale — sono illustrativi e non normativi: il manuale stesso lascia all'erogatore la facoltà di adottare il formato più appropriato, purché correttamente documentato. La presente pagina costituisce la documentazione di riferimento per i consumatori dei servizi Signal-Hub esposti tramite GovWay.
