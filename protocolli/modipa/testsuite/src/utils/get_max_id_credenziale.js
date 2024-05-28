function get_max_id_credenziale() {
    var getDbConfig = read('classpath:utils/get_db_config.js');

    var govwayDbConfig = getDbConfig()

    db_sleep_before_read = govwayDbConfig.sleep_before_read

    java.lang.Thread.sleep(db_sleep_before_read)

    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    dbquery = "select max(id) from credenziale_mittente"
    karate.log("Query 'get_max_id_credenziale': " + dbquery)
    var res = db.readValue(dbquery);
    karate.log("Query 'get_max_id_credenziale': RESULT" + res)
    return res;
}

