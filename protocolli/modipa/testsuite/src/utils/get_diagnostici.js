function get_diagnostici(id_transazione) {
    var getDbConfig = read('classpath:utils/get_db_config.js');

    var govwayDbConfig = getDbConfig()

    db_sleep_before_read = govwayDbConfig.sleep_before_read

    java.lang.Thread.sleep(db_sleep_before_read)
    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    dbquery = "select * from msgdiagnostici where id_transazione='"+id_transazione+"' and severita<=2"
    karate.log("Query: " + dbquery)
    karate.log("Risultato: " + db.readRows(dbquery))    
return db.readRows(dbquery);
}
