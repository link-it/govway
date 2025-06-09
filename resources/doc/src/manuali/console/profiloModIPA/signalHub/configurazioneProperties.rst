.. _modipa_signalhub_properties:

Configurazione GovWay Properties
--------------------------------

Per personalizzare il supporto a Signal-Hub, sono disponibili alcune proprietà configurabili nel file `modipa.properties`, elencate di seguito:

**Abilitazione del supporto**

Per abilitare il supporto a Signal-Hub, è necessario inserire la seguente proprietà:

``org.openspcoop2.protocol.modipa.signalHub.enabled=true``

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

**Parametri deposito segnale**

Per ciascun parametro utilizzato nel deposito dei segnali, sono disponibili le seguenti proprietà:

- Etichetta utilizzata per identificare il parametro:

  ``org.openspcoop2.protocol.modipa.signalHub.params.<NOME_PARAMETRO>.label=LABEL_PARAMETRO``

- Regole di default per la valorizzazione del parametro:

  ``org.openspcoop2.protocol.modipa.signalHub.params.<NOME_PARAMETRO>.rule=RULE_1,RULE_2``

- Definizione di obbligatorietà del parametro:

  ``org.openspcoop2.protocol.modipa.signalHub.params.<NOME_PARAMETRO>.required=true``





