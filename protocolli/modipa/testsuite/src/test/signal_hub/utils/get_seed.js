function get_seed(nomeSoggetto, nomeServizio) {

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
    
    dbquery = "SELECT serv.id FROM servizi serv,soggetti sog WHERE serv.NOME_SERVIZIO = '"+nomeServizio+"' AND serv.ID_SOGGETTO =sog.id AND sog.NOME_SOGGETTO ='"+nomeSoggetto+"' AND sog.TIPO_SOGGETTO ='modipa'"
    karate.log("Query: " + dbquery)
    id = db.readValue(dbquery);
    
    dbquery = "SELECT seed FROM servizi_digest_params where id_servizio="+id+" ORDER BY data_registrazione DESC"
    karate.log("Query: " + dbquery)
    return db.readRows(dbquery).get(0).get("seed");
}
