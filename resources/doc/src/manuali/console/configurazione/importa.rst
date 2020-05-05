.. _importa:

Importa
-------

L'importazione di entità nel registro può essere effettuata tramite la
sezione accessibile con la voce di menu *Importa* presente nella sezione
*Configurazione*.

Gli archivi che possono essere importati devono essere nel formato atteso da govway e sono ottenibili:

- attraverso un'esportazione effettuabile tramite govwayConsole come indicato nella sezione :ref:`esporta`
- scaricando le govlets disponibili sul sito del progetto che permettono di pre-configurare GovWay per una specifica API

Il form che compare per l'importazione è quello riportato in :numref:`importaFig`. I passi
da eseguire sono i seguenti:

-  *Validazione Documenti* (disponibile solamente con interfaccia in
   modalità avanzata, per default è abilitato): Se attivato, questo flag
   indica che i documenti presenti nell'archivio vengono validati prima
   di essere importati (es. wsdl, xsd, openapi 3, swagger 2 ...).

-  *Aggiornamento*: Se attivato, questo flag indica che l'archivio da
   importare costituisce un aggiornamento del registro attuale. Gli elementi presenti nell'archivio, che risultano già esistere sul registro di GovWay, verranno aggiornati solamente se il flag viene abilitato.

-  *Policy di Configurazione*: eventuali policy globali (Token, Rate Limiting) presenti nell'archivio verranno importate solamente se il flag viene abilitato.

-  *Configurazione di GovWay*: una eventuale configurazione presente nell'archivio verrà importata solamente se il flag viene abilitato.

-  Selezionare dal filesystem il file che corrisponde all'archivio che
   deve essere importato.

   .. figure:: ../_figure_console/Importazione.png
    :scale: 100%
    :align: center
    :name: importaFig

    Importazione di entità nel registro


.. note::
      Attraverso l'abilitazione di configurazioni avanzate relative ai Profili di Interoperabilità può comparire una ulteriore scelta iniziale che serve ad indicare la modalità cui fanno riferimento le entità contenute nell'archivio da importare.
      
      Ad esempio, per il Profilo SPCoop se viene abilitata la proprietà 'org.openspcoop2.protocol.spcoop.packageSICA' nel file locale '/etc/govway/spcoop_local.properties', verrà richiesto quale tipo di archivio si vuole importare a scelta tra:

      -  *spcoop*: il formato standard basato sulle specifiche SPCoop
      -  *govlet*: il formato di govway

