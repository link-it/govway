.. _soggetto:

Creazione di un soggetto
^^^^^^^^^^^^^^^^^^^^^^^^

Affinché possano essere utilizzate le funzionalità di autenticazione ed
autorizzazione, associate alle erogazioni, è necessario che vengano
censiti i soggetti fruitori che inviano le richieste di servizio. La
registrazione di un soggetto consente di assegnargli delle credenziali
che lo identificano ed eventuali ruoli provenienti dalla fonte
"Registro".

.. figure:: ../../_figure_console/Soggetto-new-https.png
 :scale: 80%
 :align: center
 :name: soggettoNew

 Creazione di un soggetto

Per creare il soggetto posizionarsi nella sezione *Registro > Soggetti*,
quindi premere il pulsante *Aggiungi*. Compilare il form come segue (:numref:`soggettoNew`):

-  *Profilo Interoperabilità*: La scelta del profilo di interoperabilità sarà richiesta solo nel caso in cui non sia stata effettuata la relativa scelta dal menu in testata.

-  *Nome*: Il nome del soggetto. È necessario che il nome indicato
   risulti univoco rispetto ai nomi già presenti per la modalità
   operativa selezionata (in questo caso API Gateway).

-  *Tipologia*: Indicare se si tratta di un soggetto esclusivamente
   erogatore, esclusivamente fruitore o con entrambi i ruoli.

-  *Descrizione*: Un testo di descrizione per il soggetto.

-  *Modalità di Accesso*: Sezione presente solo nel caso in cui il
   soggetto ricopra il ruolo di fruitore. Tramite il campo *Tipo* si
   seleziona il tipo di credenziali richieste per l'autenticazione del
   soggetto. In base alla scelta effettuata saranno mostrati i campi per
   consentire l'inserimento delle credenziali richieste. Per i dettagli sulla configurazione della modalità di accesso si faccia riferimento alla sezione :ref:`modalitaAccesso`.

Dopo aver creato il soggetto è opzionalmente possibile assegnargli dei
ruoli, tra quelli che sono presenti nel registro e contrassegnati come
*fonte registro*. Per associare ruoli ad un soggetto seguire il
collegamento presente nella colonna *Ruoli*, in corrispondenza del
soggetto scelto. Premere quindi il pulsante *Aggiungi*. Nel form che si
apre (:numref:`soggettoRuoli`) è presente una lista dalla quale è possibile selezionare un
ruolo alla volta, che viene aggiunto confermando con il tasto *Invia*.

.. figure:: ../../_figure_console/SoggettoRuoli.png
 :scale: 90%
 :align: center
 :name: soggettoRuoli

 Assegnazione di ruoli ad un soggetto
