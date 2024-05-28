function get_diagnostici(id_transazione, messaggio) {
    var getDbConfig = read('classpath:utils/get_db_config.js');

    var govwayDbConfig = getDbConfig()

    db_sleep_before_read = govwayDbConfig.sleep_before_read

    java.lang.Thread.sleep(db_sleep_before_read)

    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    dbquery = "select * from msgdiagnostici where id_transazione='"+id_transazione+"' AND messaggio like '%"+messaggio+"%'"
    //karate.log("Query search diagnostico: " + dbquery)
    return db.readRows(dbquery);
}
