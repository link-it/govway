function get_diagnostici_create_token_audit(id_transazione) {
    var getDbConfig = read('classpath:utils/get_db_config.js');

    var govwayDbConfig = getDbConfig()

    db_sleep_before_read = govwayDbConfig.sleep_before_read

    java.lang.Thread.sleep(db_sleep_before_read)

    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    dbquery = "select * from msgdiagnostici where id_transazione='"+id_transazione+"' and messaggio LIKE 'Creazione security token ModI %AUDIT% della richiesta effettuata con successo%'"
    //karate.log("Query: " + dbquery)
    return db.readRows(dbquery);
}
