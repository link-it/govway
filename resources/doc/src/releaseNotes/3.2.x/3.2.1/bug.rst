Bug Fix
-------

Sono stati risolti i seguenti bug:

- Non venivano verificati eventuali ruoli associati agli applicativi identificati durante l'invocazione dell'erogazione quando era abilitata l'autorizzazione per ruoli. 

- L'autenticazione http-basic non funzionava con password che contenevano il carattere ':'.

- Corretto un problema nella gestione di messaggi MTOM con struttura Multipart con solamente una singola 'part'. Il messaggio veniva processato correttamente ma poi veniva inoltrato verso il backend senza una struttura Multipart (veniva eliminato il boundary nello stream) lasciando inalterato il Content-Type che invece presentava sempre l'indicazione MultipartRelated. L'effetto di questa inconsistenza era che il backend non riusciva a processare il messaggio ottenuto generando un errore simile al seguente: 'Unable to internalize message'.

- Durante la validazione dei contenuti, in presenza di messaggi con elemento 'xsi:type' definito con un prefisso non utilizzato da altri elementi, si otteneva il seguente errore: 'The value of the attribute "prefix="xmlns",localpart="p",rawname="xmlns"" is invalid. Prefixed namespace bindings may not be empty.'


Sulla console di gestione sono stati risolti i seguenti bug:

- In presenza di multitenant attivo, durante la creazione di una erogazione o fruizione, se non era stato selezionato il soggetto del dominio in gestione (in alto a destra), la selezione della API reimpostava il soggetto erogatore scelto in precedenza nel form.

- Nella sezione di configurazione delle cache era presente un link errato che portava alla configurazione delle regole di proxy pass.

- Aggiunto controllo grafico che, avviata un'operazione, disabilita gli elementi grafici sulla console fino al completamento dell'operazione.

- Aggiunta finestra modale per indicare all'utente che non ha selezionato nessun elemento da esportare o eliminare.


Per l'API di monitoraggio sono stati risolti i seguenti bug:

- L'API utilizza adesso il time zone di default presente sul sistema dove è dispiegata.

- Le operazioni di accesso ad elenchi di transazioni ritornavano degli item che includevano elementi non previsti dall'interfaccia OpenAPI.

- È adesso possibile configurare un database delle transazioni differente da quello dove sono presenti le configurazioni.

