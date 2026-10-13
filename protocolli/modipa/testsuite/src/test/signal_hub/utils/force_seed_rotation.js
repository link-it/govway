function force_seed_rotation(nomeSoggetto, nomeServizio) {

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
    idServizio = db.readValue(dbquery);
    
    dbquery = "SELECT id FROM servizi_digest_params where id_servizio="+idServizio+" ORDER BY data_registrazione DESC"
    karate.log("Query: " + dbquery)
    idSeme = db.readRows(dbquery).get(0).get("id");
    
    // registra la richiesta di rotazione forzata del seme in uso, come effettuato dalla console:
    // l'aggiornamento avviene solo se la rotazione non risulta gia' richiesta (ritorna il numero di righe aggiornate)
    Timestamp = Java.type('java.sql.Timestamp')
    now = new Timestamp(java.lang.System.currentTimeMillis())
    dbupdate = "UPDATE servizi_digest_params SET data_richiesta_rotazione = ? WHERE id = " + idSeme + " AND data_richiesta_rotazione IS NULL"
    karate.log("Query: " + dbupdate)
    return db.update(dbupdate, now);
}
