function get_credenziale_by_refid(tipo, idref) {
    var getDbConfig = read('classpath:utils/get_db_config.js');

    var govwayDbConfig = getDbConfig()

    db_sleep_before_read = govwayDbConfig.sleep_before_read

    java.lang.Thread.sleep(db_sleep_before_read)

    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    dbquery = "select credenziale from credenziale_mittente where tipo='"+tipo+"' AND ref_credenziale="+idref+""
    karate.log("Query 'get_credenziale_by_refid': " + dbquery)
    return db.readValue(dbquery);
}
