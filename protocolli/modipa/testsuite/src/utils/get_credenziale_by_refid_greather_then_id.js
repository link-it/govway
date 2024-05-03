function get_credenziale_by_refid_greather_then_id(tipo, idref, id) {

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
    dbquery = "select credenziale from credenziale_mittente where tipo='"+tipo+"' AND ref_credenziale="+idref+" AND id>"+id+""
    karate.log("Query 'get_credenziale_by_refid_greather_then_id': " + dbquery)
    return db.readValue(dbquery);
}
