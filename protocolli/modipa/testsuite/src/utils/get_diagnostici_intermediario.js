function get_diagnostici_create_token_audit(id_transazione) {

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
    dbquery = "select * from msgdiagnostici where id_transazione='"+id_transazione+"' and messaggio LIKE 'Richiesta ricevuta dal soggetto intermediario %'"
    //karate.log("Query: " + dbquery)
    return db.readRows(dbquery);
}
