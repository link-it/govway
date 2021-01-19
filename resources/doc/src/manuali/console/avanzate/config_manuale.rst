.. _confManuale:

Configurazione manuale delle interfacce
---------------------------------------

Nel caso non si disponga del descrittore della API, è possibile in
alternativa fornire manualmente la specifica delle interfacce. Dopo aver
salvato la nuova API, senza aver fornito il descrittore delle
interfacce, si procede individuando il nuovo elemento nella lista delle
API presenti e cliccando sul collegamento presente nella colonna
*Servizi*, nel caso SOAP, o *Risorse* nel caso REST.

Nel caso SOAP, si procede aggiungendo il nuovo servizio tramite il
pulsante *Aggiungi*. Il form da compilare è quello mostrato nella figura
seguente.

   .. figure:: ../_figure_console/AggiungiPortType.png
    :scale: 100%
    :align: center
    :name: addPortType

    Aggiunta di un servizio alla API SOAP

I dati da fornire sono i seguenti:

-  *Nome* del servizio

-  *Descrizione* del servizio

-  *Profilo di collaborazione* del servizio, a scelta tra oneway e
   sincrono

-  *ID Conversazione*. Flag per consentire di specificare nelle
   richieste un valore che identifica una conversazione.

-  *Riferimento ID Richiesta*. Flag per consentire di specificare nelle
   richieste un identificativo relativo ad un messaggio precedente.

Al passo successivo, utilizzando il collegamento nella colonna *Azioni*,
relativamente al servizio appena creato, si procede con l'aggiunta delle
azioni. Il form da compilare è quello mostrato nella figura seguente.

   .. figure:: ../_figure_console/AggiungiAzione.png
    :scale: 100%
    :align: center
    :name: addPortTypeAction

    Aggiunta di un’azione alla API SOAP

I dati da fornire sono i seguenti:

-  *Nome* dell'azione

-  *Profilo*. Si può scegliere se utilizzare le impostazioni già fornite
   a livello del servizio, oppure ridefinirle indicando nuovamente
   Profilo di collaborazione, ID Conversazione e Riferimento ID
   Richiesta.

Nel caso REST, si procede aggiungendo la nuova risorsa tramite il
pulsante *Aggiungi*. Il form da compilare è quello mostrato nella figura
seguente.

   .. figure:: ../_figure_console/AggiungiRisorsaREST.png
    :scale: 100%
    :align: center
    :name: addResource

    Aggiunta di una risorsa alla API REST

I dati da fornire sono i seguenti:

-  *HTTP Method* relativo alla risorsa (GET, POST, DELETE, ecc.)

-  *Path* della risorsa

-  *Nome* della risorsa

-  *Descrizione* della risorsa

-  *ID Conversazione*. Flag per consentire di specificare nelle
   richieste un valore che identifica una conversazione.

-  *Riferimento ID Richiesta*. Flag per consentire di specificare nelle
   richieste un identificativo relativo ad un messaggio precedente.

