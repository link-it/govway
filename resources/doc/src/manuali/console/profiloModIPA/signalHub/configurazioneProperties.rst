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




