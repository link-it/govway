function remove_test_seeds() {

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
    
    // vengono eliminati solamente i semi delle erogazioni Signal Hub della testsuite, lasciando inalterati gli altri semi presenti sulla base dati
    dbquery = "DELETE FROM servizi_digest_params WHERE id_servizio IN (SELECT serv.id FROM servizi serv, soggetti sog WHERE serv.ID_SOGGETTO = sog.id AND sog.TIPO_SOGGETTO = 'modipa'" +
    	" AND sog.NOME_SOGGETTO IN ('DemoSoggettoErogatore', 'DemoSoggettoErogatore2', 'DemoSoggettoErogatore3') AND serv.NOME_SERVIZIO LIKE 'SignalHub%')"
    karate.log("Query: " + dbquery)
    return db.update(dbquery);
}
