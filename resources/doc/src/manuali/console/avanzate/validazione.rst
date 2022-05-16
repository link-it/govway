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

Se invece si vuole modificare il tipo di validazione effettuata con i motori 'openapi4j' o 'swagger-request-validator' è possibile farlo abilitando (true) o disabilitando (false) la specifica funzionalità registrando una delle seguenti :ref:`configProprieta` (per default tutte le proprietà elencate sono abilitate):

- *validation.openApi.validateAPISpec* (default: true): prima di procedere con la validazione del messaggio, viene controllato che l'interfaccia OpenAPI 3.x sia sintatticamente valida;
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
- *validation.swaggerRequestValidator.resolveFullyApiSpec* (default: false): indica se sostituire inline i $ref nello schema con le loro definizioni. Per default viene utilizzato il valore 'false' poichè quando vengono risolti inline non vengono gestiti correttamente i singoli attributi degli schemi combinati (oneOf, allOf ecc..). La risoluzione inline consente però di avere delle performance maggiori.
- *validation.swaggerRequestValidator.injectingAdditionalPropertiesFalse* (default: false): se abilitata, viene riattivato il transformer della libreria che aggiunge additionalProperties=false in tutti gli oggetti degli schemi. È necessario disattivarlo per poter validare correttamente gli schemi che definiscono tale proprietà a true. La libreria lo utilizza come workaround per validare strutture allOf. 

