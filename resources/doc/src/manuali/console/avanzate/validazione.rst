.. _configAvanzataValidazione:

Validazione dei messaggi con OpenAPI 3.x
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Nella sezione :ref:`configSpecificaValidazione` è stata descritta la funzionalità di validazione dei messaggi applicativi in transito sul gateway.

Dalla versione 3.3.1, per la validazione dei messaggi riguardanti API REST con specifiche di interfaccia OpenAPI 3.x, viene utilizzata la libreria openapi4j (https://openapi4j.github.io/openapi4j/).
È possibile ritornare al precedente motore di validazione registrando la seguente :ref:`configProprieta` sull'erogazione o sulla fruizione:

- *validation.openApi4j.enabled=false*

Se invece si vuole modificare il tipo di validazione effettuata con openapi4j è possibile farlo abilitando (true) o disabilitando (false) la specifica funzionalità registrando una delle seguenti :ref:`configProprieta` (per default tutte le proprietà elencate sono abilitate):

- *validation.openApi4j.validateAPISpec*: prima di procedere con la validazione del messaggio, viene controllato che l'interfaccia OpenAPI 3.x sia sintatticamente valida.
- *validation.openApi4j.validateRequestQuery*: viene effettuata la validazione della query url
- *validation.openApi4j.validateRequestHeaders*: viene effettuata la validazione degli header http della richiesta
- *validation.openApi4j.validateResponseHeaders*: viene effettuata la validazione degli header http della risposta
- *validation.openApi4j.validateRequestCookies*: viene effettuata la validazione dei cookie presenti nella richiesta
- *validation.openApi4j.validateRequestBody*: viene effettuata la validazione del payload http della richiesta
- *validation.openApi4j.validateResponseBody*: viene effettuata la validazione del payload http della risposta
- *validation.openApi4j.mergeAPISpec*: eventuali schemi esterni json o yaml vengono aggiunti all'OpenAPI principale prima di procedere con la validazione


