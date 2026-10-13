function count_seeds_id(idServizio) {

    govwayDbConfig = { 
        username: karate.properties['db_username'],
        password: karate.properties['db_password'],
        url: karate.properties['db_url'],
        driverClassName: karate.properties['db_driverClassName']
     }

    //db_sleep_before_read = karate.properties['db_sleep_before_read']
    
    java.lang.Thread.sleep(db_sleep_before_read)
    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    
    // conteggio tramite l'identificativo dell'erogazione: utilizzabile anche dopo l'eliminazione dell'erogazione
    dbquery = "SELECT count(*) FROM servizi_digest_params WHERE id_servizio = " + idServizio
    karate.log("Query: " + dbquery)
    return db.readValue(dbquery);
}
