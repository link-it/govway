function clear_pdnd_tracing(db_config, days) {
    
    DbUtils = Java.type('org.openspcoop2.core.monitor.rs.testsuite.DbUtils')

    db = new DbUtils(db_config)
    
    dbquery = "DELETE FROM statistiche_pdnd_tracing WHERE PDD_CODICE IN ('domain/modipa/rs-monitor-DemoSoggettoErogatore','domain/modipa/rs-monitor-DemoSoggettoErogatore2')"
    karate.log("Query: " + dbquery);
    db.update(dbquery);
    
    date = new Date(0)
    dbquery = "UPDATE statistiche SET data_ultima_generazione = ? WHERE tipo='PdndGenerazioneTracciamento'"
    karate.log(dbquery)
    
    prev_date = db.addTimestamp(db.now(), -days)
    effected = db.update(dbquery, prev_date);
    
    if (effected == 0) {
    	dbquery = "INSERT INTO statistiche(data_ultima_generazione, tipo) VALUES (?, 'PdndGenerazioneTracciamento')";
     	karate.log(dbquery)
    	db.update(dbquery, prev_date);
    }
}
