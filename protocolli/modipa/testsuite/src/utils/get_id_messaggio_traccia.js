function get_id_messaggio_traccia(id_transazione, tipo_messaggio) {

    if (!tipo_messaggio) {
        tipo_messaggio = 'Richiesta'
    }
    var getDbConfig = read('classpath:utils/get_db_config.js');

    var govwayDbConfig = getDbConfig()

    db_sleep_before_read = govwayDbConfig.sleep_before_read

    java.lang.Thread.sleep(db_sleep_before_read)

    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    dbquery = "select id_messaggio from tracce where id_transazione='"+id_transazione+"' and tipo_messaggio='"+tipo_messaggio+"'"
    //karate.log("Query: " + dbquery)
    return db.readRows(dbquery);
}
