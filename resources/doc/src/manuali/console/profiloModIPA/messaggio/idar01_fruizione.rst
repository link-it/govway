.. _modipa_idar01_fruizione:

Fruizione
---------

Le richieste che provengono dagli applicativi interni del dominio e sono dirette verso altre amministrazioni verranno arricchite del token di sicurezza ModIPA previsto dall'operazione invocata, come indicato precedentemente nella sezione :ref:`modipa_idar01`. 

Per la configurazione delle fruizioni con i profili di sicurezza messaggio è necessario registrare ciascun applicativo interno coinvolto al fine principale di associargli una chiave privata e un certificato X509 che GovWay utilizza per firmare il token di sicurezza ModIPA prodotto. Gli applicativi vengono identificati da GovWay tramite una delle modalità di autenticazione supportate descritte nella sezione :ref:`apiGwAutenticazione` (:numref:`FruizioneModIPA2`). 

   .. figure:: ../../_figure_console/FruizioneModIPA.jpg
    :scale: 70%
    :align: center
    :name: FruizioneModIPA2

    Fruizione con Profilo di Interoperabilità 'ModI PA'

La registrazione dell'applicativo avviene come già descritto nella sez. :ref:`applicativo`. In questo contesto sarà necessario specificare il dominio "Interno" dell'applicativo e procedere all'inserimento dei dati nel form "ModI PA" (:numref:`applicativo_interno_fig`).

   .. figure:: ../../_figure_console/modipa_applicativo_interno.png
    :scale: 40%
    :name: applicativo_interno_fig

    Dati ModI PA relativi ad un applicativo interno

I dati da inserire sono:

    + *Archivio*: il file che corrisponde al keystore contenente la chiave privata utilizzata per la firma dei messaggi
    + *Tipo*: il formato del keystore (jks, pkcs12)
    + *Password*: la password per l'accesso al keystore
    + *Alias Chiave Privata*: l'alias con cui è riferita la chiave privata nel keystore
    + *Password Chiave Privata*: la password della chiave privata
    + *Reply Audience/WSA-To*: identificativo dell'applicativo, utilizzato come clientId in uscita, e verificato con il valore "Audience" eventualmente presente nelle risposte.

L'interfaccia per la creazione della fruizione, basata su una API con profilo IDAR01 (o IDAS01), presenta le sezioni "ModI PA - Richiesta" e "ModI PA - Risposta":

- ModI PA - Richiesta (:numref:`fruizione_richiesta_fig`): la maschera relativa alla richiesta prevede la configurazione del meccanismo di firma digitale del messaggio, ad opera dell'applicativo mittente, e la produzione del relativo token di sicurezza:

    + Algoritmo: l'algoritmo che si vuole utilizzare per la firma digitale del messaggio
    + Riferimento X.509: il metodo da utilizzare per l'inserimento del certificato dell'applicativo nel token di sicurezza. I valori possibili sono (differenziati per il caso REST e SOAP) quelli previsti nelle Linee Guida di Interoperabilità:
    + Time to Live: tempo di validità del token prodotto (in secondi)
    + Audience: identificativo dell'applicativo destinatario da indicare come audience nel token di sicurezza; se non viene indicato alcun valore verrà utilizzato la url del connettore.

   .. figure:: ../../_figure_console/modipa_fruizione_richiesta.png
    :scale: 50%
    :name: fruizione_richiesta_fig

    Dati per la configurazione della sicurezza messaggio sulla richiesta di una fruizione


- ModI PA - Risposta (:numref:`fruizione_risposta_fig`): la maschera relativa alla risposta prevede la configurazione del meccanismo di validazione del token ricevuto da parte dell'applicativo destinatario:

    - Riferimento X.509: il metodo per la localizzazione del certificato del destinatario nel messaggio di risposta. Si può mantenere la medesima impostazione prevista per il messaggio di richiesta o ridefinirla.
    - TrustStore Certificati: Riferimento al truststore che contiene le CA, i certificati e le CRL da utilizzare per poter verificare i token di sicurezza ricevuti nelle risposte. È possibile mantenere l'impostazione di default che è stata fornita al momento dell'installazione del prodotto, oppure definire un diverso riferimento (opzione "Ridefinito") fornendo Path, Tipo, Password del TrustStore e CRL.
    - Verifica Audience: Se abilitata questa opzione, viene effettuata la verifica che il campo Audience, presente nel token di sicurezza della risposta, corrisponda a quello indicato per l'applicativo mittente.

   .. figure:: ../../_figure_console/modipa_fruizione_risposta.png
    :scale: 50%
    :name: fruizione_risposta_fig

    Dati per la configurazione della sicurezza messaggio sulla risposta di una fruizione
