function wither_transactions(nomeServizio, days) {

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
    dbquery = "SELECT data_accettazione_richiesta, data_ingresso_richiesta, data_uscita_richiesta, data_accettazione_risposta, data_ingresso_risposta, data_uscita_risposta, id FROM transazioni WHERE nome_servizio = '"+  nomeServizio +"'"
    karate.log("Query: " + dbquery);
    rows = db.readRows(dbquery);
    
    fields = [
    	"data_accettazione_richiesta",
    	"data_ingresso_richiesta",
    	"data_uscita_richiesta",
    	"data_accettazione_risposta",
    	"data_ingresso_risposta",
    	"data_uscita_risposta"
    ]
    
    for (i = 0; i < rows.length; i++) {
    	row = rows[i]
    	
    	date0 = db.addTimestamp(row[fields[0]], -days);
    	date1 = db.addTimestamp(row[fields[1]], -days);
    	date2 = db.addTimestamp(row[fields[2]], -days);
    	date3 = db.addTimestamp(row[fields[3]], -days);
    	date4 = db.addTimestamp(row[fields[4]], -days);
    	date5 = db.addTimestamp(row[fields[5]], -days);
    	id = row["id"];
    	
    	dbupdate = "UPDATE transazioni SET data_accettazione_richiesta = ?, data_ingresso_richiesta = ?, data_uscita_richiesta = ?, data_accettazione_risposta = ?, data_ingresso_risposta = ?, data_uscita_risposta = ? WHERE id = '" + id + "'"; 
    	karate.log("Query: " + dbupdate);
    	db.update(dbupdate, date0, date1, date2, date3, date4, date5);
    }
    
    return rows;
}
