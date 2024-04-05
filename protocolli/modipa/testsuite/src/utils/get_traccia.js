function get_traccia(id_transazione, tipo_messaggio) {

    if (!tipo_messaggio) {
        tipo_messaggio = 'Richiesta'
    }

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
    dbquery = "select * from tracce_ext_protocol_info where idtraccia=(select id from tracce where id_transazione='"+id_transazione+"' and tipo_messaggio='"+tipo_messaggio+"')"
    //karate.log("Query: " + dbquery)
    return db.readRows(dbquery);
}

function count_traccia(id_transazione, id_messaggio, tipo_messaggio) {

    if (!tipo_messaggio) {
        tipo_messaggio = 'Richiesta'
    }

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
    dbquery = "select count(id) as count from tracce where id_transazione='"+id_transazione+"' and id_messaggio='"+id_messaggio+"' AND tipo_messaggio='"+tipo_messaggio+"'"
    karate.log("QueryCountTraccia: " + dbquery)
    return db.readRows(dbquery);

}

function exists_traccia(id_transazione, id_messaggio, tipo_messaggio) {

    if (!tipo_messaggio) {
        tipo_messaggio = 'Richiesta'
    }

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
    dbquery = "select id_transazione, rif_messaggio from tracce where id_transazione='"+id_transazione+"' and id_messaggio='"+id_messaggio+"' AND tipo_messaggio='"+tipo_messaggio+"'"
    karate.log("QueryExistsTraccia: " + dbquery)
    return db.readRows(dbquery);

}
