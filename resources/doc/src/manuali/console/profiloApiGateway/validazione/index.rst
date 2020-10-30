.. _configSpecificaValidazione:

Validazione dei messaggi
~~~~~~~~~~~~~~~~~~~~~~~~

Per attivare la validazione dei messaggi in transito sul gateway si
accede al collegamento presente nella colonna *Validazione* presente tra
gli elementi di configurazione della specifica erogazione/fruizione.

   .. figure:: ../../_figure_console/Validazione.png
    :scale: 100%
    :align: center
    :name: validazione

    Validazione dei messaggi

Compilare il form di configurazione (:numref:`validazione`):

-  *Stato*: Consente di abilitare/disabilitare la funzionalità di
   validazione sulla voce di configurazione scelta. L'opzione
   *warnignOnly* consente di attivare la funzionalità di validazione
   evitando però che, se tale fase non viene superata, venga bloccato il
   messaggio e restituito un errore. In quest'ultimo caso, gli errori di
   validazione verranno segnalati solo tramite l'emissione di opportuni
   messaggi diagnostici dal servizio di tracciamento.

-  *Tipo*: Nel caso si sia abilitato il servizio di validazione, questo
   campo consente di selezionare la metodologia che si vuole utilizzare.
   I valori selezionabili da questo elenco cambiano in base alla
   tipologia delle API cui fa riferimento l'erogazione/fruizione.

I tipi di validazione previsti sono:

-  *Wsdl 1.1*, la validazione si basa sull'interfaccia wsdl fornita con la
   API. Questo tipo di validazione è più rigorosa in quanto controlla
   non solo la conformità sintattica ma viene validato il messaggio in
   transito verificando che sia idoneo al PortType e Operation in uso.
   Questo tipo di validazione è applicabile solo al caso Soap.

-  *Swagger 2.0 o OpenAPI 3.0*, nei casi in cui si è fornito un
   descrittore formale per una API Rest, la validazione sarà effettuata
   utilizzando gli strumenti associati allo specifico formato.

-  *Schemi XSD*, la validazione si basa sugli schemi xsd allegati alle
   API. Utilizzato per la validazione sintattica dei messaggi XML sia
   nel caso Soap che Rest.

Nel caso di servizi Soap, se i messaggi che transitanto sulla porta di
dominio possiedono il formato MTOM, per poterli validare è necessario
attivare l'opzione *Accetta MTOM*. Tale opzione normalizza i messaggi
prima di effettuarne la validazione e ripristina il formato originale
una volta completato il processo di validazione.

.. note::
    Per la validazione dei messaggi riguardanti API REST con specifiche di interfaccia OpenAPI 3.x, è possibile attuare una configurazione avanzata del tipo di validazione effettuato. Maggiori dettagli vengono forniti nella sezione :ref:`configAvanzataValidazione`.

.. toctree::
   :maxdepth: 2

   richiestaRisposta
   configurazioneAvanzataSoap
   configurazioneAvanzataRest
