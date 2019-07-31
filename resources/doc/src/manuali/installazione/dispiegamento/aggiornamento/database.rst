.. _deploy_upd_database:

Aggiornamento del database
~~~~~~~~~~~~~~~~~~~~~~~~~~

Nella sottodirectory *sql* si trovano gli script SQL da eseguire sul
database attualmente utilizzato per adeguarlo alla nuova versione:

#. Eseguire lo script *sql/GovWay_upgrade_<new-version>.sql* per
   aggiornare lo schema del database.

#. Se sono stati selezionati nuovi profili di interoperabilità rispetto alla precedente installazione, devono essere eseguiti anche gli script:

   - *sql/profili/GovWay_upgrade_initialize-profilo-<new-profile>.sql*

#. Se si è modificata la tipologia di Application Server rispetto a
   quella utilizzata nell'installazione precedente (es. da jboss a
   tomcat), deve anche essere eseguito lo script:
   
   - *sql/utilities/as/upgradeAS_to<new-type>.sql*
