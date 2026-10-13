function wither_seeds(nomeSoggetto, nomeServizio, days) {

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
    
    // vengono arretrate di 'days' giorni solamente le date di registrazione dei semi dell'erogazione indicata
    dbquery = "SELECT data_registrazione, id FROM servizi_digest_params WHERE id_servizio IN (" + "SELECT serv.id FROM servizi serv, soggetti sog WHERE serv.ID_SOGGETTO = sog.id AND sog.TIPO_SOGGETTO = 'modipa' AND sog.NOME_SOGGETTO = '"+nomeSoggetto+"' AND serv.NOME_SERVIZIO = '"+nomeServizio+"'" + ")"
    karate.log("Query: " + dbquery);
    rows = db.readRows(dbquery);
    karate.log(rows)
    
    for (i = 0; i < rows.length; i++) {
    	row = rows[i]
    	date = db.addTimestamp(row['data_registrazione'], -days);
    	id = row["id"];
    	
    	dbupdate = "UPDATE servizi_digest_params SET data_registrazione = ? WHERE id = " + id; 
    	karate.log("Query: " + dbupdate);
    	db.update(dbupdate, date);
    }
    return rows;
}
