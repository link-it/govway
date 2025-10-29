Miglioramenti alla funzionalità di Validazione dei Contenuti
------------------------------------------------------------

- *Nuovo Engine*: la validazione dei contenuti, tramite interfaccia OpenAPI 3, utilizza adesso la libreria openapi4j (https://openapi4j.github.io/openapi4j/) che consente di validare correttamente messaggi definiti tramite strutture 'oneOf' con discriminator.

- *Validazione solo per la Richiesta*: la validazione è adesso "disattivabile" sulla fase di richiesta o risposta utilizzando le proprietà dell'API.
