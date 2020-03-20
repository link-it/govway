.. _applicativo:

Creazione di un applicativo
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Affinché possano essere utilizzate le funzionalità di autenticazione ed
autorizzazione, associate alle fruizioni, è necessario che vengano
censiti gli applicativi client, interni al dominio, che inviano le richieste di
servizio. La registrazione di un applicativo, di tipo client, consente di assegnargli
delle credenziali che lo identificano ed eventuali ruoli provenienti
dalla fonte "Registro".

   .. figure:: ../../_figure_console/Applicativo-new.png
    :scale: 100%
    :align: center
    :name: applicativoNew

    Creazione di un applicativo

Per registrare l'applicativo posizionarsi nella sezione *Registro >
Applicativi*, quindi premere il pulsante *Aggiungi*. Compilare il form
come segue (:numref:`applicativoNew`):

-  *Profilo Interoperabilità*: Opzione visibile solo nel caso in cui non sia stata effettuata la relativa scelta sul menu della testata.

-  *Nome*: Assegnare un nome all'applicativo. È necessario che il nome
   indicato risulti univoco rispetto ai nomi già presenti per la
   modalità operativa selezionata (in questo caso API Gateway).

-  *Tipo*: Utilizzare il tipo 'Client' per censire un'applicativo allo scopo di identificarlo ed autorizzarlo durante l’invocazione di erogazioni o fruizioni di API.

-  *Modalità di Accesso*: Tramite il campo *Tipo* si
   seleziona il tipo di credenziali richieste per l'autenticazione
   dell'applicativo. In base alla scelta effettuata saranno mostrati i
   campi per consentire l'inserimento delle credenziali richieste. Per i dettagli sulla configurazione della modalità di accesso si faccia riferimento alla sezione :ref:`modalitaAccesso`.

Dopo aver creato l'applicativo è opzionalmente possibile assegnargli dei
ruoli, tra quelli che sono presenti nel registro e contrassegnati come
*fonte registro*. Per associare ruoli ad un applicativo seguire il
collegamento presente nella colonna *Ruoli*, in corrispondenza
dell'applicativo scelto. Premere quindi il pulsante *Aggiungi*. Nel form
che si apre è presente una lista dalla quale è possibile selezionare un
ruolo alla volta, che viene aggiunto confermando con il tasto *Invia*.
