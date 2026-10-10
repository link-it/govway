function pdnd_async_interazione(interactionId, ruolo) {
    // Ritorna la riga della tabella PDND_INTERAZIONI_ASYNC (colonne in minuscolo) o null se non presente.
    // ruolo: 'delegata' (GovWay del fruitore) o 'applicativa' (GovWay dell'erogatore)

    var govwayDbConfig = {
        username: karate.properties['db_username'],
        password: karate.properties['db_password'],
        url: karate.properties['db_url'],
        driverClassName: karate.properties['db_driverClassName']
     }

    var db_sleep_before_read = karate.properties['db_sleep_before_read']

    java.lang.Thread.sleep(db_sleep_before_read)
    var DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    var db = new DbUtils(govwayDbConfig)
    var dbquery = "select * from pdnd_interazioni_async where interaction_id='"+interactionId+"' and ruolo='"+ruolo+"'"
    karate.log("Query: " + dbquery)
    var rows = db.readRows(dbquery)
    if (rows == null || rows.size() == 0) {
        return null;
    }
    var row = rows.get(0)
    var result = {}
    var it = row.entrySet().iterator()
    while (it.hasNext()) {
        var e = it.next()
        var v = e.getValue()
        result[(e.getKey() + '').toLowerCase()] = (v == null ? null : (v + ''))
    }
    return result;
}
