.. _modipa_signalhub_properties:

Configurazione Avanzata
---------------------------------------

Per personalizzare il supporto a Signal-Hub, sono registrabili alcune proprietà da aggiungere nel file `modipa_local.properties` presente nella connfigurazione esterna di GovWay. 

**Abilitazione del supporto**

Per disabilitare il supporto a Signal-Hub, è possibile inserire la seguente proprietà:

``org.openspcoop2.protocol.modipa.signalHub.enabled=false``

**Opzionalità della pseudoanonimizzazione**

Per rendere opzionale la pseudoanonimizzazione dell'id degli oggetti e far comparire la relativa checkbox sulla maschera di configurazione di signal-hub, abilitare la properties:

``org.openspcoop2.protocol.modipa.signalHub.pseudonymization.choice.enabled=true``

**Funzioni di hash**

È possibile modificare la lista degli algoritmi disponibili per la generazione dell’hash, così come l’algoritmo di default, attraverso le seguenti proprietà:

``org.openspcoop2.protocol.modipa.signalHub.algorithms=SHA-256,SHA-512/256,SHA-384,SHA-512,SHA3-256,SHA3-384,SHA3-512,SHAKE128,SHAKE256``
``org.openspcoop2.protocol.modipa.signalHub.algorithms.default=SHA-256``

**Dimensione del seme**

È possibile configurare le dimensioni accettate per il seme e la dimensione predefinita tramite le seguenti proprietà:

``org.openspcoop2.protocol.modipa.signalHub.seed.size=16,32,64``
``org.openspcoop2.protocol.modipa.signalHub.seed.size.default=16``

**Periodo di rotazione**

Le proprietà relative alla rotazione delle informazioni crittografiche permettono di abilitare o disabilitare la rotazione e impostare il numero di giorni del periodo di validità:

``org.openspcoop2.protocol.modipa.signalHub.seed.lifetime.unlimited=false``
``org.openspcoop2.protocol.modipa.signalHub.seed.lifetime.days.default=120``

**Storico delle informazioni crittografiche**

Numero di versioni delle informazioni crittografiche da mantenere dopo ogni rotazione per un determinato servizio:

``org.openspcoop2.protocol.modipa.signalHub.seed.history=1``

**Concatenazione messaggio e seme**

Personalizzazione della concatenazione tra messaggio (objectId) e seme per la produzione dell’ID cifrato. Il valore è un template nel quale, a runtime, verranno sostituiti `${message}` e `${salt}` con rispettivamente l’ID dell’oggetto e il seme, prima della pseudoanonimizzazione:

``org.openspcoop2.protocol.modipa.signalHub.hash.composition=${message}${salt}``

**Fruizione deposito segnale**

Nome e versione dell’API della fruizione abilitata al deposito dei segnali:

``org.openspcoop2.protocol.modipa.signalHub.api.name=api-pdnd-push-signals``
``org.openspcoop2.protocol.modipa.signalHub.api.version=1``

.. _modipa_signalhub_exposedAlgorithmName:

**Formato dell'identificativo dell'algoritmo esposto sul servizio di pseudoanonimizzazione**

L'endpoint di pseudoanonimizzazione (sia REST che SOAP) espone, nel campo ``cryptoHashFunction``, l'identificativo dell'algoritmo di hash configurato sull'erogazione. Per default GovWay restituisce l'identificativo standard JCA (es. ``SHA-256``, ``SHA-512/256``, ``SHA3-256``), direttamente utilizzabile dal consumatore come argomento di ``java.security.MessageDigest.getInstance(...)``. Nelle versioni precedenti veniva invece restituito il nome dell'enum interno ``org.openspcoop2.utils.digest.DigestType`` (es. ``SHA256``, ``SHA512_256``, ``SHA3_256``).

Per consentire la retrocompatibilità con consumatori già integrati contro la vecchia versione di GovWay, è possibile riabilitare il comportamento precedente tramite la seguente proprietà, da aggiungere nel file di configurazione locale ``/etc/govway/modipa_local.properties`` (assumendo sia ``/etc/govway`` la directory di configurazione indicata in fase di installazione):

``org.openspcoop2.protocol.modipa.signalHub.algorithms.exposedName.legacy=false``

Quando impostata a *true*, GovWay espone il nome dell'enum interno ``org.openspcoop2.utils.digest.DigestType`` invece dell'identificativo standard JCA. Il valore predefinito è *false*. La mappatura tra i due formati è la seguente:

.. list-table::
   :widths: 50 50
   :header-rows: 1

   * - legacy=true (nome enum)
     - legacy=false (identificativo JCA, default)
   * - ``SHA256``
     - ``SHA-256``
   * - ``SHA512_256``
     - ``SHA-512/256``
   * - ``SHA384``
     - ``SHA-384``
   * - ``SHA512``
     - ``SHA-512``
   * - ``SHA3_256``
     - ``SHA3-256``
   * - ``SHA3_384``
     - ``SHA3-384``
   * - ``SHA3_512``
     - ``SHA3-512``
   * - ``SHAKE128``
     - ``SHAKE128`` (invariato)
   * - ``SHAKE256``
     - ``SHAKE256`` (invariato)

Il flag globale può essere ridefinito puntualmente sulla singola erogazione attraverso la :ref:`configProprieta` ``modi.signalHub.algorithms.exposedName.legacy`` valorizzandola con ``true`` o ``false``. Tale override consente di mantenere il comportamento legacy soltanto per specifiche erogazioni i cui consumatori non siano ancora stati allineati al nuovo formato standard.

Il razionale della scelta di esporre l'identificativo standard JCA, coerente con quanto demandato dal Manuale Operativo Signal Hub di PDND, è argomentato nella sezione :ref:`modipa_signalhub_pseudoanonimizzazione_conformitaPdnd`.

.. _modipa_signalhub_exposeSignalId:

**Esposizione del signalId sul servizio di pseudoanonimizzazione**

L'endpoint di pseudoanonimizzazione (sia REST che SOAP) può includere nel response un campo ``signalId`` che identifica il segnale di tipo ``SEEDUPDATE`` che ha introdotto il seme corrente. L'informazione consente al consumatore di sincronizzarsi correttamente dopo un primo accesso al servizio o dopo una perdita di sincronizzazione, sapendo da quale signalId in poi il seme restituito è applicabile. Per il primo seme storico (mai sostituito da un ``SEEDUPDATE``) il valore esposto è ``0``.

L'esposizione del campo è controllata dalla seguente proprietà, da aggiungere nel file di configurazione locale ``/etc/govway/modipa_local.properties`` (assumendo sia ``/etc/govway`` la directory di configurazione indicata in fase di installazione):

``org.openspcoop2.protocol.modipa.signalHub.pseudonymization.exposeSignalId=true``

Il valore predefinito è *false* (campo non esposto), per garantire la retrocompatibilità con consumatori già integrati contro la vecchia versione di GovWay che validano in modo strict lo schema della risorsa di pseudoanonimizzazione. Impostando il valore a *true* il campo viene incluso nella risposta.

Il flag globale può essere ridefinito puntualmente sulla singola erogazione attraverso la :ref:`configProprieta` ``modi.signalHub.pseudonymization.exposeSignalId`` valorizzandola con ``true`` o ``false``. Tale override consente di attivare l'esposizione del ``signalId`` soltanto per specifiche erogazioni i cui consumatori sono in grado di gestire il nuovo campo.
