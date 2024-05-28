function get_id_by_credenziale(tipo, credenziale) {

    var getDbConfig = read('classpath:utils/get_db_config.js');

    var govwayDbConfig = getDbConfig()

    db_sleep_before_read = govwayDbConfig.sleep_before_read

    java.lang.Thread.sleep(db_sleep_before_read)

    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    dbquery = "select id from credenziale_mittente where tipo='"+tipo+"' AND credenziale='"+credenziale+"'"
    karate.log("Query 'get_id_by_credenziale': " + dbquery)
    return db.readValue(dbquery);
}
