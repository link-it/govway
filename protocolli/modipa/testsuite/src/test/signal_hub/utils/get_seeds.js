function get_seeds(nomeSoggetto, nomeServizio) {

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
    
    // semi conservati per l'erogazione, dal piu' recente; per ognuno: seed, serial (signalId del SEEDUPDATE) e presenza di una richiesta di rotazione
    dbquery = "SELECT seed, serial_number, data_richiesta_rotazione FROM servizi_digest_params where id_servizio="+idServizio+" ORDER BY data_registrazione DESC"
    karate.log("Query: " + dbquery)
    rows = db.readRows(dbquery);
    seeds = []
    for (i = 0; i < rows.length; i++) {
    	row = rows[i]
    	seeds.push({ 
    		seed: row.get('seed'), 
    		serial: row.get('serial_number') != null ? ('' + row.get('serial_number')) : null, 
    		rotazioneRichiesta: row.get('data_richiesta_rotazione') != null 
    	})
    }
    return seeds;
}
