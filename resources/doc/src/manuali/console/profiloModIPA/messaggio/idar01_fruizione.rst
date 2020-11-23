.. _modipa_idar01_fruizione:

Fruizione
---------

Le richieste che provengono dagli applicativi interni del dominio e sono dirette verso altre amministrazioni verranno arricchite del token di sicurezza 'ModI' previsto dall'operazione invocata, come indicato precedentemente nella sezione :ref:`modipa_idar01`. 

Per la configurazione delle fruizioni con i pattern di sicurezza messaggio è necessario registrare ciascun applicativo interno coinvolto al fine principale di associargli una chiave privata e un certificato X509 che GovWay utilizza per firmare il token di sicurezza 'ModI' prodotto. Gli applicativi vengono identificati da GovWay tramite una delle modalità di autenticazione supportate descritte nella sezione :ref:`apiGwAutenticazione` (:numref:`FruizioneModIPA2`). 

   .. figure:: ../../_figure_console/FruizioneModIPA.jpg
    :scale: 70%
    :align: center
    :name: FruizioneModIPA2

    Fruizione con Profilo di Interoperabilità 'ModI'

La registrazione dell'applicativo avviene come già descritto nella sez. :ref:`applicativo`. In questo contesto sarà necessario specificare il dominio "Interno" dell'applicativo e procedere all'inserimento dei dati nel form "ModI" (:numref:`applicativo_interno_fig`).

   .. figure:: ../../_figure_console/modipa_applicativo_interno.png
    :scale: 40%
    :name: applicativo_interno_fig

    Dati ModI relativi ad un applicativo interno

I dati da inserire sono:

    + *Archivio*: il file che corrisponde al keystore contenente la chiave privata utilizzata per la firma dei messaggi
    + *Tipo*: il formato del keystore (jks, pkcs12)
    + *Password*: la password per l'accesso al keystore
    + *Alias Chiave Privata*: l'alias con cui è riferita la chiave privata nel keystore
    + *Password Chiave Privata*: la password della chiave privata
    + *Reply Audience/WSA-To*: identificativo dell’applicativo utilizzato per valorizzare nel token di sicurezza di una richiesta il claim 'client_id' per API REST e l'header 'wsa:From' per API SOAP. Se è abilitata la funzionalità 'Verifica Audience / WSAddressing To' nella configurazione della sicurezza della risposta verrà inoltre verificato che nel token di sicurezza della risposta ricevuto vi sia un claim 'aud' per API REST o un header 'wsa:To' per API SOAP che possiede un valore identico all'identificato fornito.
    + *URL (x5u)*: URL che riferisce un certificato (o certificate chain) X.509 corrispondente alla chiave firmataria del security token. Deve essere obbligatoriamente definito se l'applicativo fruisce di API REST configurate per generare un token di sicurezza tramite il claim 'x5u'

L'interfaccia per la creazione della fruizione, basata su una API con pattern "ID_AUTH_REST_01" (o "ID_AUTH_SOAP_01"), presenta le sezioni "ModI - Richiesta" e "ModI - Risposta":

- ModI - Richiesta (:numref:`fruizione_richiesta_fig`): la maschera relativa alla richiesta prevede la configurazione del meccanismo di firma digitale del messaggio, ad opera dell'applicativo mittente, e la produzione del relativo token di sicurezza:

    + Algoritmo: l'algoritmo che si vuole utilizzare per la firma digitale del messaggio
    + Riferimento X.509: il metodo da utilizzare per l'inserimento del certificato dell'applicativo nel token di sicurezza. I valori possibili sono (differenziati per il caso REST e SOAP) quelli previsti nelle Linee Guida di Interoperabilità:
    + Certificate Chain: se è stata selezionata la modalità 'x5c', è possibile indicare se nel token di sicurezza verrà incluso solo il certificato utilizzato per la firma o l'intera catena.
    + Time to Live: tempo di validità del token prodotto (in secondi)
    + Audience: identificativo dell'applicativo destinatario da indicare come audience nel token di sicurezza; se non viene indicato alcun valore verrà utilizzato la url del connettore.

   .. figure:: ../../_figure_console/modipa_fruizione_richiesta.png
    :scale: 50%
    :name: fruizione_richiesta_fig

    Dati per la configurazione della sicurezza messaggio sulla richiesta di una fruizione


- ModI - Risposta (:numref:`fruizione_risposta_fig`): la maschera relativa alla risposta prevede la configurazione del meccanismo di validazione del token ricevuto da parte dell'applicativo destinatario:

    - Riferimento X.509: il metodo per la localizzazione del certificato del destinatario nel messaggio di risposta. Si può mantenere la medesima impostazione prevista per il messaggio di richiesta o ridefinirla.
    - TrustStore Certificati: Riferimento al truststore che contiene le CA, i certificati e le CRL da utilizzare per poter verificare i token di sicurezza ricevuti nelle risposte. È possibile mantenere l'impostazione di default che è stata fornita al momento dell'installazione del prodotto, oppure definire un diverso riferimento (opzione "Ridefinito") fornendo Path, Tipo, Password del TrustStore e CRL.
    - Verifica Audience: Se abilitata questa opzione, viene effettuata la verifica che il campo Audience, presente nel token di sicurezza della risposta, corrisponda a quello indicato per l'applicativo mittente.

   .. figure:: ../../_figure_console/modipa_fruizione_risposta.png
    :scale: 50%
    :name: fruizione_risposta_fig

    Dati per la configurazione della sicurezza messaggio sulla risposta di una fruizione
