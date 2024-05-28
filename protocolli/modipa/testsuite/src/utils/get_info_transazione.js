function get_info_transazione(id_transazione, nome_colonna) {
    var getDbConfig = read('classpath:utils/get_db_config.js');

    var govwayDbConfig = getDbConfig()

    db_sleep_before_read = govwayDbConfig.sleep_before_read

    java.lang.Thread.sleep(db_sleep_before_read)

    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    dbquery = "select "+nome_colonna+" from transazioni where id='"+id_transazione+"'"
    //karate.log("Query: " + dbquery)
    return db.readRows(dbquery);
}
