.. _modipa_pdnd_fruizione:

Fruizione (PDND)
----------------

Le richieste che provengono dagli applicativi interni del dominio e sono dirette verso altre amministrazioni verranno arricchite del token di sicurezza 'ModI' previsto dall'operazione invocata, come indicato precedentemente nella sezione :ref:`modipa_pdnd`. 

Per la configurazione delle fruizioni con un pattern di sicurezza via PDND è necessario registrare una Token Policy di Negoziazione del tipo descritto nella sezione ':ref:`tokenNegoziazionePolicy_pdnd`'. 

Una volta effettuata la registrazione della Token Policy, per utilizzarla in una fruizione è sufficiente associarla al connettore della fruizione come descritto nella sezione :ref:`avanzate_connettori_tokenPolicy`. 

Di seguito vengono riportati tutte le informazioni da registrare nella policy:

- Tipo: SignedJWT;

- PDND: flag attivato;

- URL: endpoint esposto dalla PDND su cui è possibile richiedere lo stacco del voucher;

  .. figure:: ../../_figure_console/TokenPDNDNegoziazione1.png
    :scale: 50%
    :align: center
    :name: TokenPDNDNegoziazione1

    Token Policy di Negoziazione PDND (Endpoint)

- JWT Keystore: parametri di accesso al keystore contenente la chiave privata corrispondente al certificato X509 caricato sulla PDND durante la registrazione dell'applicativo client. I parametri variano in funzione del tipo di keystore selezionato:

	- 'JKS', 'PKCS12', 'JWK Set': deve essere definito il path su filesystem dove risiede il keystore, la password per l'accesso al keystore, l'alias con cui è riferita la chiave privata e la password;

          .. figure:: ../../_figure_console/TokenPDNDNegoziazioneKeystorePKCS12.png
            :scale: 60%
            :align: center
            :name: TokenPDNDNegoziazioneKeystorePKCS12

            Token Policy di Negoziazione PDND (Keystore PKCS12)

	- 'Definito nell'applicativo ModI': il keystore utilizzato per firmare l'asserzione JWT inviata alla PDND sarà quello definito nell'applicativo ModI richiedente come descritto nella sezione ':ref:`modipa_idar01_fruizione`';

          .. figure:: ../../_figure_console/TokenPDNDNegoziazioneKeystoreApplicativoModI.png
            :scale: 60%
            :align: center
            :name: TokenPDNDNegoziazioneKeystoreApplicativoModI

            Token Policy di Negoziazione PDND (Keystore definito nell'applicativo ModI)

	- 'Definito nella fruizione ModI': il keystore utilizzato per firmare l'asserzione JWT inviata alla PDND sarà quello definito nella fruizione ModI come descritto nella sezione ':ref:`modipa_sicurezza_avanzate_fruizione_pdnd`';

          .. figure:: ../../_figure_console/TokenPDNDNegoziazioneKeystoreFruizioneModI.png
            :scale: 60%
            :align: center
            :name: TokenPDNDNegoziazioneKeystoreFruizioneModI

            Token Policy di Negoziazione PDND (Keystore definito nella fruizione ModI)

	- Tipi PKCS11: gli altri tipi disponibili sono quelli corrispondenti ai tipi di keystore PKCS11 registrati (':ref:`pkcs11`').

- JWT Signature: algoritmo di firma

  .. figure:: ../../_figure_console/TokenPDNDNegoziazioneFirma.png
    :scale: 50%
    :align: center
    :name: TokenPDNDNegoziazioneFirma

    Token Policy di Negoziazione PDND (Algoritmo di Firma)

- JWT Header: 

	- Type (typ): lasciare il valore 'JWT';

	- Key Id (kid): deve essere indicato l'identificativo univoco (KID) associato al certificato caricato sulla PDND e ottenuto al termine della registrazione dell'applicativo client. Può essere fornito tramite una delle seguenti modalità:

		- 'Personalizzato': selezionando la modalità 'Personalizzato' è possibile indicarlo puntualmente. Il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway (':ref:`valoriDinamici`');

                  .. figure:: ../../_figure_console/TokenPDNDNegoziazioneKIDpersonalizzato.png
                    :scale: 60%
                    :align: center
                    :name: TokenPDNDNegoziazioneKIDpersonalizzato

                    Token Policy di Negoziazione PDND (KID personalizzato)

		- 'Definito nell'applicativo ModI': nel caso in cui è stato indicato un keystore definito nell'applicativo ModI, è possibile selezionare una modalità analoga anche per il KID (:numref:`TokenPDNDNegoziazioneKIDapplicativo`).

                  .. figure:: ../../_figure_console/TokenPDNDNegoziazioneKIDapplicativo.png
                    :scale: 60%
                    :align: center
                    :name: TokenPDNDNegoziazioneKIDapplicativo

                    Token Policy di Negoziazione PDND (KID definito nell'applicativo ModI)

                  Questa modalità richiede che oltre al keystore, nell'applicativo ModI richiedente descritto nella sezione ':ref:`modipa_idar01_fruizione`', venga abilitata anche la sezione 'Authorization OAuth' e venga indicato il KID nel campo 'Key Id del Certificato' (:numref:`ApplicativoInternoAutorizzazioneOAuth`).

                  .. figure:: ../../_figure_console/ApplicativoInternoAutorizzazioneOAuth.png
                    :scale: 60%
                    :align: center
                    :name: ApplicativoInternoAutorizzazioneOAuth

                    Dati Autorizzazione OAuth relativi ad un applicativo interno

		- 'Definito nella fruizione ModI': nel caso in cui è stato indicato un keystore definito nella fruizione ModI, è possibile selezionare una modalità analoga anche per il KID (:numref:`TokenPDNDNegoziazioneKIDfruizione`).

                  .. figure:: ../../_figure_console/TokenPDNDNegoziazioneKIDfruizione.png
                    :scale: 60%
                    :align: center
                    :name: TokenPDNDNegoziazioneKIDfruizione

                    Token Policy di Negoziazione PDND (KID definito nella fruizione ModI)

                  Questa modalità richiede che oltre al keystore, nella fruizione ModI venga abilitata anche la sezione 'Authorization PDND' e venga indicato il KID nel campo 'Key Id del Certificato' come descritto nella sezione ':ref:`modipa_sicurezza_avanzate_fruizione_pdnd`'.

- JWT Payload:

  l'identificativo univoco dell'applicativo client ('*client_id*' o '*sub*') ottenuto al termine della registrazione dell'applicativo sulla PDND deve essere indicato nei seguenti campi:

  - Client ID

  - Issuer

  - Subject

  .. figure:: ../../_figure_console/TokenPDNDNegoziazioneClientId.png
    :scale: 60%
    :align: center
    :name: TokenPDNDNegoziazioneClientId

    Token Policy di Negoziazione PDND (ClientId)

  In alternativa nel caso in cui sia stato indicato un keystore definito nell'applicativo ModI, è possibile selezionare una modalità analoga anche per la tripla clientId/issuer/subject (:numref:`TokenPDNDNegoziazioneClientIdApplicativoModI`).

  .. figure:: ../../_figure_console/TokenPDNDNegoziazioneClientIdApplicativoModI.png
    :scale: 60%
    :align: center
    :name: TokenPDNDNegoziazioneClientIdApplicativoModI

    Token Policy di Negoziazione PDND (ClientId definito nell'applicativo ModI)

  Questa modalità richiede che oltre al keystore, nell'applicativo ModI richiedente descritto nella sezione ':ref:`modipa_idar01_fruizione`', venga abilitata anche la sezione 'Authorization OAuth' e venga indicato il clientId nel campo 'Identificativo' (:numref:`ApplicativoInternoAutorizzazioneOAuth2`).

  .. figure:: ../../_figure_console/ApplicativoInternoAutorizzazioneOAuth.png
    :scale: 60%
    :align: center
    :name: ApplicativoInternoAutorizzazioneOAuth2

    Dati Autorizzazione OAuth relativi ad un applicativo interno

  In alternativa nel caso in cui sia stato indicato un keystore definito nella fruizione ModI, è possibile selezionare una modalità analoga anche per la tripla clientId/issuer/subject (:numref:`TokenPDNDNegoziazioneClientIdFruizioneModI`).

  .. figure:: ../../_figure_console/TokenPDNDNegoziazioneClientIdFruizioneModI.png
    :scale: 60%
    :align: center
    :name: TokenPDNDNegoziazioneClientIdFruizioneModI

    Token Policy di Negoziazione PDND (ClientId definito nella fruizione ModI)

  Questa modalità richiede che oltre al keystore, nella fruizione ModI venga abilitata anche la sezione 'Authorization PDND' e venga indicato il clientId nel campo 'Identificativo' come descritto nella sezione ':ref:`modipa_sicurezza_avanzate_fruizione_pdnd`'.

  Gli altri campi presenti nella sezione 'JWT Payload' rappresentano (:numref:`TokenPDNDNegoziazioneJWTPayload`):

	- Audience: indica il servizio di stacco del voucher della PDND. Il valore, fornito dalla PDND, è indipendente dal servizio per cui si vuole richiedere un voucher e varia solamente in funzione dell'ambiente di validazione o produzione della PDND stessa;

	- Identifier: consente di configurare la modalità di valorizzazione del claim 'jti' presente all'interno del token di richiesta inviato alla PDND. Si suggerisce di valorizzare il campo con la keyword '${transaction:id}' al fine di utilizzare l'identificativo di transazione della richiesta;

	- Time to Live (secondi): consente di indicare la durate del token di richiesta inviato alla PDND (es. 100 sec);

	- Purpose ID: identificativo univoco della finalità per cui si intende fruire di un servizio. Il valore può essere fornito staticamente o può contenere una keyword risolta a runtime in modo da valorizzare il claim purposeId con un valore prelevato dai dati della richiesta. Ad esempio se il censimento dei purposeId viene mantenuti a livello applicativo può essere indicato un header HTTP con cui il richiedente può fornire a GovWay il valore da utilizzare (es. ${header:NOME_HEADER_HTTP}). Se invece il purposeId viene registrato come proprietà di una fruizione può essere valorizzato con il valore '${config:NOME_PROPRIETA}'. Si rimanda alla sezione ':ref:`valoriDinamici`' per le varie modalità dinamiche utilizzabili.

	- Informazioni Sessione: consente di valorizzare il claim 'sessionInfo' previsto dalla PDND. La valorizzazione può essere statica o formata da parti dinamiche risolte a runtime dal Gateway (per maggiori dettagli :ref:`valoriDinamici`).

  .. figure:: ../../_figure_console/TokenPDNDNegoziazioneJWTPayload.png
    :scale: 60%
    :align: center
    :name: TokenPDNDNegoziazioneJWTPayload

    Token Policy di Negoziazione PDND (JWT Payload)

- Dati Richiesta:

	- Resource: indicare l'audience/url del servizio per cui si vuole richiedere un voucher;

	- Client ID: indicare il medesimo valore inserito nel campo 'Client ID' della sezione 'JWT Payload';

  .. figure:: ../../_figure_console/TokenPDNDNegoziazioneDatiRichiesta.png
    :scale: 60%
    :align: center
    :name: TokenPDNDNegoziazioneDatiRichiesta

    Token Policy di Negoziazione PDND (DatiRichiesta)

  Per quanto concerne il campo 'Client ID', nel caso in cui sia stato indicato un keystore definito nell'applicativo ModI, è possibile selezionare una modalità analoga anche per il campo 'Client ID' (:numref:`TokenPDNDNegoziazioneDatiRichiestaApplicativoModI`).

  .. figure:: ../../_figure_console/TokenPDNDNegoziazioneDatiRichiestaApplicativoModI.png
    :scale: 60%
    :align: center
    :name: TokenPDNDNegoziazioneDatiRichiestaApplicativoModI

    Token Policy di Negoziazione PDND (DatiRichiesta, ClientId definito nell'applicativo ModI)

  Nel caso invece in cui sia stato indicato un keystore definito nella fruizione ModI, è possibile selezionare una modalità analoga anche per il campo 'Client ID' (:numref:`TokenPDNDNegoziazioneDatiRichiestaFruizioneModI`).

  .. figure:: ../../_figure_console/TokenPDNDNegoziazioneDatiRichiestaFruizioneModI.png
    :scale: 60%
    :align: center
    :name: TokenPDNDNegoziazioneDatiRichiestaFruizioneModI

    Token Policy di Negoziazione PDND (DatiRichiesta, ClientId definito nella fruizione ModI)

