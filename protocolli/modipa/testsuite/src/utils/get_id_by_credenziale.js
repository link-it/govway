function get_id_by_credenziale(tipo, credenziale) {

    govwayDbConfig = { 
        username: karate.properties['db_username'],
        password: karate.properties['db_password'],
        url: karate.properties['db_url'],
        driverClassName: karate.properties['db_driverClassName']
     }

    db_sleep_before_read = karate.properties['db_sleep_before_read']
    
    java.lang.Thread.sleep(db_sleep_before_read)
    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    dbquery = "select id from credenziale_mittente where tipo='"+tipo+"' AND credenziale='"+credenziale+"'"
    karate.log("Query 'get_id_by_credenziale': " + dbquery)
    return db.readValue(dbquery);
}
