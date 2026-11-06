function get_diagnostici(id_transazione, messaggio) {

    govwayDbConfig = {
        username: karate.properties['db_username'],
        password: karate.properties['db_password'],
        url: karate.properties['db_url'],
        driverClassName: karate.properties['db_driverClassName']
     }

    db_sleep_before_read = karate.properties['db_sleep_before_read']
    db_retry_max_total_ms = parseInt(karate.properties['db_retry_max_total_ms'] || '5000')
    db_retry_interval_ms = parseInt(karate.properties['db_retry_interval_ms'] || '500')

    java.lang.Thread.sleep(db_sleep_before_read)
    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    dbquery = "select * from msgdiagnostici where id_transazione='"+id_transazione+"' AND messaggio like '%"+messaggio+"%'"
    karate.log("Query search diagnostico: " + dbquery)

    // I diagnostici vengono salvati da GovWay dopo aver inviato la risposta: in ambiente carico (jenkins)
    // la scrittura può non essere ancora avvenuta, per cui la query viene ripetuta finché non ritorna righe.
    var deadline = java.lang.System.currentTimeMillis() + db_retry_max_total_ms
    var attempt = 0
    while (true) {
        attempt++
        var res = db.readRows(dbquery)
        if (res.size() > 0 || java.lang.System.currentTimeMillis() >= deadline) {
            if (res.size() == 0) {
                karate.log("Query 'get_diagnostico' giving up after " + attempt + " attempt(s) (budget " + db_retry_max_total_ms + "ms)")
            }
            return res
        }
        karate.log("Query 'get_diagnostico' attempt " + attempt + " empty, retrying in " + db_retry_interval_ms + "ms")
        java.lang.Thread.sleep(db_retry_interval_ms)
    }
}
