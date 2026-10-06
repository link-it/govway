.. _titoloConsole:

Titolo delle Console
--------------------

Il titolo riportato nell'intestazione delle console può essere personalizzato, ad esempio per
indicare l'ente o l'ambiente (collaudo, produzione) a cui si riferisce l'installazione, tramite la
proprietà 'console.nome.esteso' da definire:

- per la 'govwayConsole', nel file *<directory-lavoro>/console_local.properties*:

   ::

      console.nome.esteso=GovWay - Gestione <Collaudo/Produzione>

- per la 'govwayMonitor', nel file *<directory-lavoro>/monitor_local.properties*:

   ::

      console.nome.esteso=GovWay - Monitoraggio <Collaudo/Produzione>

.. note::
   Le versioni precedenti della 'govwayMonitor' utilizzavano la proprietà 'appTitle'. Per
   retrocompatibilità, se nel file *<directory-lavoro>/monitor_local.properties* è presente la
   proprietà 'appTitle', il titolo indicato in essa continua ad essere utilizzato e ha precedenza
   su 'console.nome.esteso': per utilizzare la nuova proprietà è necessario rimuovere 'appTitle'.

Il titolo viene visualizzato come testo: eventuali entità o tag HTML (es. '&egrave;') non vengono
interpretati e sono mostrati così come sono indicati.

.. note::
   I file di proprietà presenti nella *<directory-lavoro>* vengono letti con la codifica ISO-8859-1
   (Latin-1), come previsto per i file di proprietà Java. Un carattere accentato scritto in un file
   salvato con codifica UTF-8 viene quindi visualizzato in modo errato (es. 'SocietÃ ' al posto di
   'Società').

   I caratteri accentati devono quindi essere indicati tramite la sequenza di escape Unicode
   ``\uXXXX``, che viene interpretata correttamente qualunque sia la codifica con cui è salvato il file.

Le sequenze di escape dei caratteri accentati più comuni sono:

.. table:: Sequenze di escape Unicode dei caratteri accentati
   :widths: auto

   =========  ============  =========  ============
   Carattere  Escape        Carattere  Escape
   =========  ============  =========  ============
   à          ``\u00e0``    À          ``\u00c0``
   è          ``\u00e8``    È          ``\u00c8``
   é          ``\u00e9``    É          ``\u00c9``
   ì          ``\u00ec``    Ì          ``\u00cc``
   ò          ``\u00f2``    Ò          ``\u00d2``
   ù          ``\u00f9``    Ù          ``\u00d9``
   =========  ============  =========  ============

Ad esempio, per visualizzare il titolo 'GovWay Sanità - Società Esempio' in entrambe le console:

::

   console.nome.esteso=GovWay Sanit\u00e0 - Societ\u00e0 Esempio
