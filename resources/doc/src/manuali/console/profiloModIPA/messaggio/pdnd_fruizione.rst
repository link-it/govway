.. _modipa_pdnd_fruizione:

Fruizione (PDND)
----------------

Le richieste che provengono dagli applicativi interni del dominio e sono dirette verso altre amministrazioni verranno arricchite del token di sicurezza 'ModI' previsto dall'operazione invocata, come indicato precedentemente nella sezione :ref:`modipa_pdnd`. 

Per la configurazione delle fruizioni con un pattern di sicurezza via PDND è necessario registrare una Token Policy di Negoziazione del tipo descritto nella sezione ':ref:`tokenNegoziazionePolicy_pdnd`'. Di seguito vengono riportati tutte le informazioni da registrare nella policy:

- Tipo: SignedJWT;

- PDND: flag attivato;

- URL: endpoint esposto dalla PDND su cui è possibile richiedere lo stacco del voucher;

- JWT Keystore: parametri di accesso al keystore contenente la chiave privata corrispondente al certificato X509 caricato sulla PDND durante la registrazione dell'applicativo client;

- JWT Signature: algoritmo di firma

- JWT Header: 

	- Key Id (kid): deve essere selezionata la modalità 'Personalizzato' e deve essere indicato l'identificativo univoco (KID) associato al certificato caricato sulla PDND e ottenuto al termine della registrazione dell'applicativo client;

	- Type (typ): lasciare il valore 'JWT';

- JWT Payload:

	- Client ID: indicare l'identificativo univoco dell'applicativo client ('*client_id*' o '*sub*') ottenuto al termine della registrazione dell'applicativo sulla PDND;

	- Issuer e Subject: indicare il medesimo valore inserito nel campo 'Client ID';

	- Audience: indica il servizio di stacco del voucher della PDND. Il valore, fornito dalla PDND, è indipendente dal servizio per cui si vuole richiedere un voucher e varia solamente in funzione dell'ambiente di validazione o produzione della PDND stessa;

	- Identifier: consente di configurare la modalità di valorizzazione del claim 'jti' presente all'interno del token di richiesta inviato alla PDND. Si suggerisce di valorizzare il campo con la keyword '${transaction:id}' al fine di utilizzare l'identificativo di transazione della richiesta;

	- Time to Live (secondi): consente di indicare la durate del token di richiesta inviato alla PDND (es. 100 sec);

	- Purpose ID: identificativo univoco della finalità per cui si intende fruire di un servizio. Il valore può essere fornito staticamente o può contenere una keyword risolta a runtime in modo da valorizzare il claim purposeId con un valore prelevato dai dati della richiesta. Ad esempio se il censimento dei purposeId viene mantenuti a livello applicativo può essere indicato un header HTTP con cui il richiedente può fornire a GovWay il valore da utilizzare (es. ${header:NOME_HEADER_HTTP}). Se invece il purposeId viene registrato come proprietà di una fruizione può essere valorizzato con il valore '${config:NOME_PROPRIETA}'. Si rimanda alla sezione ':ref:`valoriDinamici`' per le varie modalità dinamiche utilizzabili.

	- Informazioni Sessione: consente di valorizzare il claim 'sessionInfo' previsto dalla PDND. La valorizzazione può essere statica o formata da parti dinamiche risolte a runtime dal Gateway (per maggiori dettagli :ref:`valoriDinamici`).

- Dati Richiesta:

	- Client ID: indicare il medesimo valore inserito nel campo 'Client ID' del 'JWT Payload';

	- Resource: indicare l'audience/url del servizio per cui si vuole richiedere un voucher.

Una volta effettuata la registrazione della Token Policy, per utilizzarla in una fruizione è sufficiente associarla al connettore della fruizione come descritto nella sezione :ref:`avanzate_connettori_tokenPolicy`. 
