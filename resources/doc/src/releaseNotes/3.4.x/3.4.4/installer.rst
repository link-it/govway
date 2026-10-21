Miglioramenti all'Installer
---------------------------

Sono stati apportati i seguenti miglioramenti all'installer binario:

- Aggiunto il supporto per WildFly 41.

- MySQL: i datasource generati utilizzano la classe driver 'com.mysql.cj.jdbc.Driver' al posto di quella deprecata 'com.mysql.jdbc.Driver'.

- MySQL 8.4.9 e successive: corretto il fallimento dell'installazione dello schema con l'errore 1118 'Row size too large'.

- SQL Server: eliminate le segnalazioni di superamento della dimensione massima delle chiavi di indice e di chiave primaria, ridimensionando alcune colonne e rivedendo gli indici. Sono stati inoltre rimossi, su MySQL, HyperSQL, SQL Server e DB2, gli indici che duplicavano un vincolo UNIQUE.

- SQL Server: negli script di aggiornamento le istruzioni 'ALTER DATABASE' riportavano il segnaposto '@DATABASE@' al posto del nome del database, impedendo l'esecuzione dello script.

- HyperSQL: ripristinata l'inizializzazione delle tabelle 'users_*_init_seq', commentata negli script generati.

- Negli script SQL completi prodotti in modalità aggiornamento, le utenze delle console, prima riportate con username e password vuoti, sono adesso proposte come INSERT commentate con segnaposto da valorizzare.
