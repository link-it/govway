.. _configAvanzataValidazione:

Validazione dei messaggi con OpenAPI / Swagger
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Nella sezione :ref:`configSpecificaValidazione` è stata descritta la funzionalità di validazione dei messaggi applicativi in transito sul gateway.

GovWay supporta tre formati di specifica per le API REST: *Swagger 2.0*, *OpenAPI 3.0* e *OpenAPI 3.1*. Il formato di alto livello (Swagger 2 oppure Open API 3) viene indicato dall'utente nel campo *Formato Specifica* in fase di registrazione dell'API; per le specifiche Open API 3 la distinzione tra 3.0 e 3.1 avviene poi automaticamente sul valore del campo *openapi* dichiarato nella specifica stessa. Per ciascun formato è definito un motore di validazione di default (le librerie utilizzabili e i rispettivi default sono descritti nella sezione :ref:`configAvanzataValidazioneEngine`). Qualora si desideri utilizzare un motore diverso dal default, è possibile sovrascrivere la scelta a livello di singola erogazione o fruizione registrando una delle seguenti :ref:`configProprieta`:

- *validation.swagger.2.library=<libreria>* (default: json_schema): motore di validazione utilizzato per le specifiche Swagger 2.0;
- *validation.openApi.30.library=<libreria>* (default: openapi4j): motore di validazione utilizzato per le specifiche OpenAPI 3.0;
- *validation.openApi.31.library=<libreria>* (default: kappa): motore di validazione utilizzato per le specifiche OpenAPI 3.1.

Per ciascuna property è ammesso solo un motore compatibile con il formato corrispondente (vedi la tabella di compatibilità in :ref:`configAvanzataValidazioneEngine`); l'indicazione di un motore non compatibile è rilevata all'avvio dell'erogazione/fruizione con segnalazione esplicita di errore.

Se si vuole modificare il tipo di validazione effettuata con i motori configurati è possibile farlo abilitando (true) o disabilitando (false) la specifica funzionalità registrando una delle seguenti :ref:`configProprieta` sull'erogazione o sulla fruizione (per default tutte le proprietà elencate sono abilitate):

- *validation.openApi.validateAPISpec* (default: true; supportato dai motori *openapi4j* e *swagger-request-validator*): prima di procedere con la validazione del messaggio, viene controllato che l'interfaccia sia sintatticamente valida;
- *validation.openApi.validateRequestPath* (default: true): viene effettuata la validazione dei parametri che definiscono il path della risorsa;
- *validation.openApi.validateRequestQuery* (default: true): viene effettuata la validazione della query url;
- *validation.openApi.validateRequestHeaders* (default: true): viene effettuata la validazione degli header http della richiesta;
- *validation.openApi.validateResponseHeaders* (default: true): viene effettuata la validazione degli header http della risposta;
- *validation.openApi.validateRequestCookies* (default: true): viene effettuata la validazione dei cookie presenti nella richiesta;
- *validation.openApi.validateRequestBody* (default: true): viene effettuata la validazione del payload http della richiesta;
- *validation.openApi.validateResponseBody* (default: true): viene effettuata la validazione del payload http della risposta;
- *validation.openApi.mergeAPISpec* (default: true): eventuali schemi esterni json o yaml vengono aggiunti all'OpenAPI principale prima di procedere con la validazione (per il motore *json_schema* la fusione è sempre attiva e l'impostazione non ha effetto).

Per i motori di validazione 'openapi4j' e 'kappa' è disponibile l'ulteriore proprietà:

- *validation.openApi.validateMultipartOptimization* (default: false): attiva il processamento ottimizzato delle richieste multipart/form-data (o mixed). L'ottimizzazione sfrutta l'ipotesi che le parti "binarie", che non richiedono una validazione rispetto ad uno schema, sono tipicamente serializzate dopo i metadati (plain o json) e possono quindi essere "saltate" terminando l'analisi dello stream dopo aver validato i metadati in modo da avere benefici prestazionali visto che tipicamente le parti binarie rappresentano la maggior dimensione del messaggio in termini di bytes. Poichè se attivata l'ottimizzazione non consente di individuare se esistono part non definite nella specifica (in presenza di 'additionalProperties=false') il comportamento di default è quello di non usare l'ottimizzazione. L'ottimizzazione richiede inoltre che lo schema multipart dichiari almeno una proprietà di formato diverso da *binary* e *base64* (es. plain, json, integer, ...): se lo schema contiene solo proprietà binarie/base64 la richiesta viene respinta con esito di validazione fallita.

Per il motore di validazione 'swagger-request-validator' sono disponibili le ulteriori proprietà:

- *validation.swaggerRequestValidator.validateWildcardSubtypeAsJson* (default: true): consente di indicare se le richieste associate a media type definiti con il carattere '\*' nel subtype (es. application/\*) debbano essere validate come richieste json;
- *validation.swaggerRequestValidator.validateRequestUnexpectedQueryParam* (default: false): se abilitata vengono segnalati gli eventuali parametri non definiti nella specifica;
- *validation.swaggerRequestValidator.resolveFullyApiSpec* (default: false): indica se sostituire inline i $ref nello schema con le loro definizioni. Per default viene utilizzato il valore 'false' poiché quando vengono risolti inline non vengono gestiti correttamente i singoli attributi degli schemi combinati (oneOf, allOf ecc..). La risoluzione inline consente però di avere delle performance maggiori.
- *validation.swaggerRequestValidator.injectingAdditionalPropertiesFalse* (default: false): se abilitata, viene riattivato il transformer della libreria che aggiunge additionalProperties=false in tutti gli oggetti degli schemi. È necessario disattivarlo per poter validare correttamente gli schemi che definiscono tale proprietà a true. La libreria lo utilizza come workaround per validare strutture allOf.

.. _configAvanzataValidazioneEngine:

Motori di validazione supportati
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

I motori di validazione disponibili e selezionabili tramite le proprietà *validation.swagger.2.library*, *validation.openApi.30.library* e *validation.openApi.31.library* sono i seguenti.

- *openapi4j* (https://openapi4j.github.io/openapi4j/): motore predefinito per le specifiche OpenAPI 3.0. Implementa la validazione strutturale del documento OpenAPI e la validazione di richieste/risposte rispetto al modello dichiarato. Non supporta le specifiche Swagger 2.0 né i costrutti introdotti in OpenAPI 3.1.

- *swagger-request-validator* (https://bitbucket.org/atlassian/swagger-request-validator): motore alternativo per Swagger 2.0 e OpenAPI 3.0 basato sulla libreria Atlassian. Offre opzioni avanzate sulla gestione dei *$ref* (risoluzione inline) e degli *additionalProperties*, oltre a un trattamento configurabile dei media type con wildcard nel subtype. Non supporta i costrutti introdotti in OpenAPI 3.1.

- *kappa* (https://github.com/erosb/kappa): motore predefinito per le specifiche OpenAPI 3.1. Derivato da openapi4j, ne estende il funzionamento con il supporto dei costrutti introdotti in OpenAPI 3.1, allineati a JSON Schema 2020-12 (*type-array* in sostituzione di *nullable*, *const*, *exclusiveMinimum/exclusiveMaximum* numerici, *prefixItems*, *if/then/else*, *dependentRequired*, *contentEncoding*, *contentMediaType*, *unevaluatedProperties*, *$dynamicRef/$dynamicAnchor*, *webhooks*). Non supporta le specifiche Swagger 2.0 né OpenAPI 3.0.

- *json_schema*: motore minimale, basato sulle librerie JSON Schema integrate in govway, utile come fallback diagnostico o per scenari in cui non sia richiesta la validazione completa del modello. Selezionabile per tutti i formati (Swagger 2.0, OpenAPI 3.0, OpenAPI 3.1).

La seguente tabella riepiloga la compatibilità tra motori e formati:

+--------------------------------+---------------+---------------+---------------+
| Motore                         | Swagger 2.0   | OpenAPI 3.0   | OpenAPI 3.1   |
+================================+===============+===============+===============+
| *json_schema*                  |       ✓       |       ✓       |       ✓       |
+--------------------------------+---------------+---------------+---------------+
| *openapi4j*                    |       ✗       |       ✓       |       ✗       |
+--------------------------------+---------------+---------------+---------------+
| *swagger-request-validator*    |       ✓       |       ✓       |       ✗       |
+--------------------------------+---------------+---------------+---------------+
| *kappa*                        |       ✗       |       ✗       |       ✓       |
+--------------------------------+---------------+---------------+---------------+

In sintesi: il default per Swagger 2.0 è *json_schema*, per le specifiche OpenAPI 3.0 è *openapi4j* e per le specifiche OpenAPI 3.1 è *kappa*. La selezione di un motore non compatibile con il formato della specifica è rilevata all'avvio dell'erogazione/fruizione con segnalazione esplicita di errore.

.. _configAvanzataValidazioneRetrocompatibilita:

Retrocompatibilità
^^^^^^^^^^^^^^^^^^

Le proprietà elencate in questa sezione sono mantenute per garantire la compatibilità con le configurazioni preesistenti ma sono **deprecate** e potrebbero essere rimosse in versioni future. Si raccomanda di sostituirle con le proprietà del modello a tre famiglie descritto nella sezione principale.

- *validation.openApi4j.enabled=true*: equivalente a *validation.openApi.30.library=openapi4j*.

- *validation.swaggerRequestValidator.enabled=true*: equivalente a *validation.openApi.30.library=swagger_request_validator*.

- *validation.openApi4j.enabled=false* / *validation.swaggerRequestValidator.enabled=false* (entrambi disabilitati): equivalente a *validation.openApi.30.library=json_schema*.

I flag *enabled* descritti sopra agiscono esclusivamente per le specifiche OpenAPI 3.0 e vengono ignorati in presenza dell'override esplicito *validation.openApi.30.library*.
