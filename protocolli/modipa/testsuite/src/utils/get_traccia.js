function get_traccia(id_transazione, tipo_messaggio) {
    karate.log("Retrieving traccia with ID: ", id_transazione )
    karate.log("And Tipo: ", tipo_messaggio )

    if (!tipo_messaggio) {
        tipo_messaggio = 'Richiesta'
    }

    govwayDbConfig = {}

    try {
        govwayDbConfig = { 
            username: karate.properties['db_username'],
            password: karate.properties['db_password'],
            url:  karate.properties['db_url'],
            driverClassName: karate.properties['db_driverClassName']
         }
         db_sleep_before_read = karate.properties['db_sleep_before_read']
    } catch (e) {

        govwayDbConfig = { 
            username: db_username,
            password: db_password,
            url: db_url,
            driverClassName: db_driverClassName
        }
    }
    
    java.lang.Thread.sleep(java.lang.Integer.valueOf(db_sleep_before_read))
    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    dbquery = "select * from tracce_ext_protocol_info where idtraccia=(select id from tracce where id_transazione='"+id_transazione+"' and tipo_messaggio='"+tipo_messaggio+"')"
    karate.log("Query: " + dbquery)
    return db.readRows(dbquery);
}