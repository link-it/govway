function clear_pdnd_tracing(db_config, days) {
    
    DbUtils = Java.type('org.openspcoop2.core.monitor.rs.testsuite.DbUtils')

    db = new DbUtils(db_config)
    
    dbquery = "DELETE FROM statistiche_pdnd_tracing"
    karate.log("Query: " + dbquery);
    db.update(dbquery);
    
    date = new Date(0)
    dbquery = "UPDATE statistiche SET data_ultima_generazione = ? WHERE tipo='PdndGenerazioneTracciamento'"
    karate.log(dbquery)
    db.update(dbquery, db.addTimestamp(db.now(), -days));
}