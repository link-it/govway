function clear_pdnd_tracing(days) {

    govwayDbConfig = { 
        username: karate.properties['db_username'],
        password: karate.properties['db_password'],
        url: karate.properties['db_url'],
        driverClassName: karate.properties['db_driverClassName']
     }

    //db_sleep_before_read = karate.properties['db_sleep_before_read']
    
    java.lang.Thread.sleep(db_sleep_before_read)
    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    utils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.TestUtils')

    db = new DbUtils(govwayDbConfig)
    
    dbquery = "DELETE FROM statistiche_pdnd_tracing WHERE PDD_CODICE IN ('domain/modipa/DemoSoggettoErogatore','domain/modipa/DemoSoggettoErogatore2')"
    karate.log("Query: " + dbquery);
    db.update(dbquery);
    
    dbquery = "DELETE FROM transazioni WHERE nome_servizio='TestTracingPDND'"
    karate.log("Query: " + dbquery);
    db.update(dbquery);
    
    date = new Date(0)
    dbquery = "UPDATE statistiche SET data_ultima_generazione = ? WHERE tipo='PdndGenerazioneTracciamento'"
    karate.log(dbquery)
    db.update(dbquery, db.addTimestamp(utils.now(), -days));
}
