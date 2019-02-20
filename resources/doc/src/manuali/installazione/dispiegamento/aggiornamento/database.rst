.. _deploy_upd_database:

Aggiornamento del database
~~~~~~~~~~~~~~~~~~~~~~~~~~

Nella sottodirectory *sql* si trovano gli script SQL da eseguire sul
database attualmente utilizzato per adeguarlo alla nuova versione:

#. Eseguire lo script *sql/GovWay_upgrade_<new_version>.sql* per
   aggiornare lo schema del database.

#. Se si è modificata la tipologia di Application Server rispetto a
   quella utilizzata nell'installazione precedente (es. da jboss a
   tomcat), deve anche essere eseguito lo script
   *sql/utilities/as/upgradeAS_to<new-type>.sql*
