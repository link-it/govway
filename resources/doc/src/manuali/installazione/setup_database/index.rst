.. _inst_setupDB:

========================================
Esempio di setup del database PostgreSQL
========================================

Procedura indicativa, applicabile alla piattaforma RDBMS PostgreSQL, per
la redisposizione del database di GovWay:

#. Creazione Utente

   ::

      [user@localhost]$ su
      Parola d'ordine: XXX
      [root@localhost]# su - postgres
      -bash-3.1$ createuser -P
      Enter name of role to add: govway
      Enter password for new role: govway
      Conferma password: govway
      Shall the new role be a superuser? (y/n) n
      Shall the new role be allowed to create databases? (y/n) n
      Shall the new role be allowed to create more new roles? (y/n) n
      CREATE ROLE
                              

#. Creazione Database

   ::

      [user@localhost]$ su
      Parola d'ordine: XXX
      [root@localhost]# su - postgres
      -bash-3.1$createdb -E UTF8 -O govway govway
      CREATE DATABASE
                              

#. Abilitazione accesso dell'utente al Database, è possibile abilitare
   l'accesso editando il file */var/lib/pgsql/data/pg_hba.conf* (come
   super utente). Abilitiamo quindi l'utente govway ad accedere al db
   govway, aggiungendo le seguenti righe al file:

   ::

      local govway govway md5
      host govway govway 127.0.0.1 255.255.255.255 md5
