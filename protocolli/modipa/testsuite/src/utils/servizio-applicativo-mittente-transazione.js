function get_servizio_applicativo_mittente_transazione(id_transazione) {

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
    dbquery = "select servizio_applicativo_fruitore from transazioni where id ='"+id_transazione+"'"
    //karate.log("Query: " + dbquery)
    return db.readValue(dbquery);
}
