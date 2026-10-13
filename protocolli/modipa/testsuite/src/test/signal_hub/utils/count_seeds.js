function count_seeds(nomeSoggetto, nomeServizio) {

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
    
    dbquery = "SELECT count(*) FROM servizi_digest_params WHERE id_servizio IN (" + "SELECT serv.id FROM servizi serv, soggetti sog WHERE serv.ID_SOGGETTO = sog.id AND sog.TIPO_SOGGETTO = 'modipa' AND sog.NOME_SOGGETTO = '"+nomeSoggetto+"' AND serv.NOME_SERVIZIO = '"+nomeServizio+"'" + ")"
    karate.log("Query: " + dbquery)
    return db.readValue(dbquery);
}
