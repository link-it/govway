function get_ruolo(id_transazione) {
    var getDbConfig = read('classpath:utils/get_db_config.js');

    var govwayDbConfig = getDbConfig()

    db_sleep_before_read = govwayDbConfig.sleep_before_read

    java.lang.Thread.sleep(db_sleep_before_read)

    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    
    dbquery = "select pdd_ruolo from transazioni where id ='"+id_transazione+"'"
    karate.log("Query: " + dbquery)
    return db.readRows(dbquery);
}
