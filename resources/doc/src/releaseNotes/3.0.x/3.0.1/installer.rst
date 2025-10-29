Miglioramenti all'Installer
---------------------------

Sono state apportati i seguenti miglioramenti all'Installer binario:

-  *Aggiornamento*; L'Installer può ora gestire anche l'aggiornamento
   del Software rispetto ad una precedente versione già installata.

-  *SQL*; corretti gli script sql, prodotti dall'installer, che
   causavano errori se utilizzati sui seguenti database:

   -  *SQLServer*, si otteneva il messaggio di errore: Introducing
      FOREIGN KEY constraint 'fk\_...' on table '...' may cause cycles
      or multiple cascade paths

   -  *MySQL*, venivano segnalati diversi errori come il seguente:
      CONSTRAINT unique\_... UNIQUE (...), - ERROR 1071 (42000):
      Specified key was too long; max key length is 767 bytes
