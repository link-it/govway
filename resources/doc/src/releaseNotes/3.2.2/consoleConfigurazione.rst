Miglioramenti alla Console di Gestione
-----------------------------------------------------

Sono state introdotte le seguenti nuove funzionalità:

- *Nuova Versione API*: nel dettaglio di una API è adesso disponibile il pulsante di creazione di una nuova versione dell'API. Durante la creazione della nuova versione è possibile scegliere se fornire una nuova specifica dell'interfaccia o ereditarla (insieme alle azioni/risorse e agli allegati) dalla precedente.

- *Generazione http-basic password per un Applicativo/Soggetto*: in caso di registrazione di un applicativo o di un soggetto, con autenticazione di tipo http-basic, è necessario registrare una password non banale. A tale scopo è stato aggiunto un bottone per il fill del campo password con una password generata random in accordo a criteri minimi di qualità. È stato inoltre aggiunto un vincolo di univocità sull'username associato ad un applicativo o ad soggetto.

- *Trasformazione del Contesto di Invocazione*: per una API di tipo REST, all'interno della trasformazione della richiesta nella sezione 'trasporto', è adesso possibile modificare sia il metodo http che il path appeso alla base url utilizzata per invocare l'applicativo di backend.

