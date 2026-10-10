function pdnd_async_diagnostici(idTransazione) {
    // Ritorna, concatenati, i messaggi diagnostici di errore/warning (severita<=2) della transazione

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
    var rows = db.readRows("select messaggio from msgdiagnostici where id_transazione='"+idTransazione+"' and severita<=2")
    var msg = ''
    for (var i = 0; i < rows.size(); i++) {
        var it = rows.get(i).values().iterator()
        if (it.hasNext()) {
            msg = msg + it.next() + '\n'
        }
    }
    return msg;
}
