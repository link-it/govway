function get_state(soggetto, date) {

    govwayDbConfig = { 
        username: karate.properties['db_username'],
        password: karate.properties['db_password'],
        url: karate.properties['db_url'],
        driverClassName: karate.properties['db_driverClassName']
     }

    //db_sleep_before_read = karate.properties['db_sleep_before_read']
    
    java.lang.Thread.sleep(db_sleep_before_read)
    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    utils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.TestUtils')

    db = new DbUtils(govwayDbConfig)
    
    dbquery = "SELECT identificativo_porta FROM soggetti WHERE nome_soggetto = '" + soggetto + "' AND tipo_soggetto='modipa'"
    karate.log(dbquery)
    pdd = db.readValue(dbquery);
    
    dbquery = "SELECT stato, stato_pdnd, tracing_id, tentativi_pubblicazione FROM statistiche_pdnd_tracing WHERE data_tracciamento= ? AND history=0 AND pdd_codice=?";
    karate.log(dbquery, pdd, utils.parse(date, 'yyyy-MM-dd'))
    return db.readRow(dbquery, utils.parse(date, 'yyyy-MM-dd'), pdd);
}
