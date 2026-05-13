.. _configAvanzataValidazione:

Validazione dei messaggi con OpenAPI 3.x
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Nella sezione :ref:`configSpecificaValidazione` è stata descritta la funzionalità di validazione dei messaggi applicativi in transito sul gateway.

Dalla versione 3.3.1, per la validazione dei messaggi riguardanti API REST con specifiche di interfaccia OpenAPI 3.x, viene utilizzata la libreria openapi4j (https://openapi4j.github.io/openapi4j/).
È possibile ritornare al precedente motore di validazione registrando la seguente :ref:`configProprieta` sull'erogazione o sulla fruizione:

- *validation.openApi4j.enabled=false*

Dalla versione 3.3.5.p1 è inoltre possibile utilizzare un ulteriore motore di validazione, utilizzando la libreria 'swagger-request-validator' (https://bitbucket.org/atlassian/swagger-request-validator).
È possibile attivare il nuovo motore di validazione registrando la seguente :ref:`configProprieta` sull'erogazione o sulla fruizione:

- *validation.swaggerRequestValidator.enabled=true*

La selezione automatica del motore avviene in base al valore del campo *openapi* dichiarato nella specifica: le specifiche *3.0.x* utilizzano il motore configurato per OpenAPI 3.0, le specifiche *3.1.x* utilizzano il motore configurato per OpenAPI 3.1. Le librerie utilizzabili come motore di validazione sono elencate nella sezione :ref:`configAvanzataValidazioneEngine`; il motore può essere indicato registrando una delle seguenti :ref:`configProprieta` sull'erogazione o sulla fruizione:

- *validation.openApi.library=<libreria>* (default: openapi4j): motore di validazione utilizzato per le specifiche OpenAPI 3.0;
- *validation.openApi.31.library=<libreria>* (default: kappa): motore di validazione utilizzato per le specifiche OpenAPI 3.1. Sono ammessi soltanto i motori che supportano i costrutti introdotti in 3.1 (vedi sezione :ref:`configAvanzataValidazioneEngine`).

Tutte le proprietà di configurazione descritte di seguito sono applicabili indistintamente a specifiche 3.0 e 3.1: il motore selezionato per la versione in uso ne legge il valore una volta sola, all'inizializzazione. Nel caso in cui occorra un comportamento differenziato tra 3.0 e 3.1, è sufficiente registrare la variante con il prefisso *validation.openApi.31.*: tale variante ha precedenza sulla proprietà comune solo per le specifiche 3.1 e ricade automaticamente sul valore comune in sua assenza. Ad esempio, *validation.openApi.31.validateRequestBody=false* disabilita la validazione del payload per le sole specifiche 3.1, lasciando inalterato il comportamento per le 3.0.

Se invece si vuole modificare il tipo di validazione effettuata con i motori configurati è possibile farlo abilitando (true) o disabilitando (false) la specifica funzionalità registrando una delle seguenti :ref:`configProprieta` (per default tutte le proprietà elencate sono abilitate):

- *validation.openApi.validateAPISpec* (default: true): prima di procedere con la validazione del messaggio, viene controllato che l'interfaccia OpenAPI 3.x sia sintatticamente valida;
- *validation.openApi.validateRequestPath* (default: true): viene effettuata la validazione dei parametri che definiscono il path della risorsa;
- *validation.openApi.validateRequestQuery* (default: true): viene effettuata la validazione della query url;
- *validation.openApi.validateRequestHeaders* (default: true): viene effettuata la validazione degli header http della richiesta;
- *validation.openApi.validateResponseHeaders* (default: true): viene effettuata la validazione degli header http della risposta;
- *validation.openApi.validateRequestCookies* (default: true): viene effettuata la validazione dei cookie presenti nella richiesta;
- *validation.openApi.validateRequestBody* (default: true): viene effettuata la validazione del payload http della richiesta;
- *validation.openApi.validateResponseBody* (default: true): viene effettuata la validazione del payload http della risposta;
- *validation.openApi.mergeAPISpec* (default: true): eventuali schemi esterni json o yaml vengono aggiunti all'OpenAPI principale prima di procedere con la validazione.

Per il motore di validazione 'openapi4j' sono disponibili le ultiori proprietà:

- *validation.openApi4j.validateMultipartOptimization* (default: false): attiva il processamento ottimizzato delle richieste multipart/form-data (o mixed). L'ottimizzazione sfrutta l'ipotesi che le parti "binarie", che non richiedono una validazione rispetto ad uno schema, sono tipicamente serializzate dopo i metadati (plain o json) e possono quindi essere "saltate" terminando l'analisi dello stream dopo aver validato i metadati in modo da avere benefici prestazionali visto che tipicamente le parti binarie rappresentano la maggior dimensione del messaggio in termini di bytes. Poichè se attivata l'ottimizzazione non consente di individuare se esistono part non definite nella specifica (in presenza di 'additionalProperties=false') il comportamento di default è quello di non usare l'ottimizzazione.

Per il motore di validazione 'swagger-request-validator' sono disponibili le ultiori proprietà:

- *validation.swaggerRequestValidator.validateWildcardSubtypeAsJson* (default: true): consente di indicare se le richieste associate a media type definiti con il carattere '\*' nel subtype (es. application/\*) debbano essere validate come richieste json;
- *validation.swaggerRequestValidator.validateRequestUnexpectedQueryParam* (default: false): se abilitata vengono segnalati gli eventuali parametri non definiti nella specifica;
- *validation.swaggerRequestValidator.resolveFullyApiSpec* (default: false): indica se sostituire inline i $ref nello schema con le loro definizioni. Per default viene utilizzato il valore 'false' poiché quando vengono risolti inline non vengono gestiti correttamente i singoli attributi degli schemi combinati (oneOf, allOf ecc..). La risoluzione inline consente però di avere delle performance maggiori.
- *validation.swaggerRequestValidator.injectingAdditionalPropertiesFalse* (default: false): se abilitata, viene riattivato il transformer della libreria che aggiunge additionalProperties=false in tutti gli oggetti degli schemi. È necessario disattivarlo per poter validare correttamente gli schemi che definiscono tale proprietà a true. La libreria lo utilizza come workaround per validare strutture allOf.

.. _configAvanzataValidazioneEngine:

Motori di validazione supportati
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

I motori di validazione disponibili e selezionabili tramite le proprietà *validation.openApi.library* e *validation.openApi.31.library* sono i seguenti.

- *openapi4j* (https://openapi4j.github.io/openapi4j/): motore predefinito per le specifiche OpenAPI 3.0. Implementa la validazione strutturale del documento OpenAPI e la validazione di richieste/risposte rispetto al modello dichiarato. Non supporta i costrutti introdotti in OpenAPI 3.1 e non è quindi selezionabile come motore per la 3.1.

- *swagger-request-validator* (https://bitbucket.org/atlassian/swagger-request-validator): motore alternativo per OpenAPI 3.0 basato sulla libreria Atlassian. Offre opzioni avanzate sulla gestione dei *$ref* (risoluzione inline) e degli *additionalProperties*, oltre a un trattamento configurabile dei media type con wildcard nel subtype. Non supporta i costrutti introdotti in OpenAPI 3.1.

- *kappa* (https://github.com/erosb/kappa): motore predefinito per le specifiche OpenAPI 3.1. Derivato da openapi4j, ne mantiene la stessa API d'uso estendendola al supporto dei nuovi costrutti introdotti in OpenAPI 3.1, allineati a JSON Schema 2020-12 (*type-array* in sostituzione di *nullable*, *const*, *exclusiveMinimum/exclusiveMaximum* numerici, *prefixItems*, *if/then/else*, *dependentRequired*, *contentEncoding*, *contentMediaType*, *unevaluatedProperties*, *$dynamicRef/$dynamicAnchor*, *webhooks*). Resta utilizzabile anche per le specifiche 3.0.

- *json_schema*: motore minimale, basato sulle librerie JSON Schema integrate in govway, utile come fallback diagnostico o per scenari in cui non sia richiesta la validazione completa del modello OpenAPI. Selezionabile sia per 3.0 che per 3.1.

In sintesi: il default per le specifiche 3.0 è *openapi4j*, il default per le specifiche 3.1 è *kappa*. Per le specifiche 3.1 sono ammessi esclusivamente i motori che supportano i costrutti 3.1 (*kappa*, *json_schema*); la selezione di un motore non compatibile è rilevata all'avvio di govway con segnalazione esplicita di errore.

