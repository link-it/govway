function get_max_id_credenziale() {

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
    dbquery = "select max(id) from credenziale_mittente"
    karate.log("Query 'get_max_id_credenziale': " + dbquery)

    var deadline = java.lang.System.currentTimeMillis() + db_retry_max_total_ms
    var attempt = 0
    while (true) {
        attempt++
        try {
            var res = db.readValue(dbquery)
            karate.log("Query 'get_max_id_credenziale': RESULT" + res)
            return res
        } catch (e) {
            if (java.lang.System.currentTimeMillis() >= deadline) {
                karate.log("Query 'get_max_id_credenziale' giving up after " + attempt + " attempt(s) (budget " + db_retry_max_total_ms + "ms)")
                throw e
            }
            karate.log("Query 'get_max_id_credenziale' attempt " + attempt + " empty, retrying in " + db_retry_interval_ms + "ms")
            java.lang.Thread.sleep(db_retry_interval_ms)
        }
    }
}

function get_id_by_credenziale(tipo, credenziale) {

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
    dbquery = "select id from credenziale_mittente where tipo='"+tipo+"' AND credenziale='"+credenziale+"'"
    karate.log("Query 'get_id_by_credenziale': " + dbquery)

    var deadline = java.lang.System.currentTimeMillis() + db_retry_max_total_ms
    var attempt = 0
    while (true) {
        attempt++
        try {
            return db.readValue(dbquery)
        } catch (e) {
            if (java.lang.System.currentTimeMillis() >= deadline) {
                karate.log("Query 'get_id_by_credenziale' giving up after " + attempt + " attempt(s) (budget " + db_retry_max_total_ms + "ms)")
                throw e
            }
            karate.log("Query 'get_id_by_credenziale' attempt " + attempt + " empty, retrying in " + db_retry_interval_ms + "ms")
            java.lang.Thread.sleep(db_retry_interval_ms)
        }
    }
}

function get_credenziale_by_id(id) {

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
    dbquery = "select credenziale from credenziale_mittente where id='"+id+"'"
    karate.log("Query 'get_credenziale_by_id': " + dbquery)

    var deadline = java.lang.System.currentTimeMillis() + db_retry_max_total_ms
    var attempt = 0
    while (true) {
        attempt++
        try {
            return db.readValue(dbquery)
        } catch (e) {
            if (java.lang.System.currentTimeMillis() >= deadline) {
                karate.log("Query 'get_credenziale_by_id' giving up after " + attempt + " attempt(s) (budget " + db_retry_max_total_ms + "ms)")
                throw e
            }
            karate.log("Query 'get_credenziale_by_id' attempt " + attempt + " empty, retrying in " + db_retry_interval_ms + "ms")
            java.lang.Thread.sleep(db_retry_interval_ms)
        }
    }
}

function get_credenziale_by_refid(tipo, idref) {

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
    dbquery = "select credenziale from credenziale_mittente where tipo='"+tipo+"' AND ref_credenziale="+idref+""
    karate.log("Query 'get_credenziale_by_refid': " + dbquery)

    var deadline = java.lang.System.currentTimeMillis() + db_retry_max_total_ms
    var attempt = 0
    while (true) {
        attempt++
        try {
            return db.readValue(dbquery)
        } catch (e) {
            if (java.lang.System.currentTimeMillis() >= deadline) {
                karate.log("Query 'get_credenziale_by_refid' giving up after " + attempt + " attempt(s) (budget " + db_retry_max_total_ms + "ms)")
                throw e
            }
            karate.log("Query 'get_credenziale_by_refid' attempt " + attempt + " empty, retrying in " + db_retry_interval_ms + "ms")
            java.lang.Thread.sleep(db_retry_interval_ms)
        }
    }
}

function get_credenziale_by_refid_greather_then_id(tipo, idref, id) {

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
    dbquery = "select credenziale from credenziale_mittente where tipo='"+tipo+"' AND ref_credenziale="+idref+" AND id>"+id+""
    karate.log("Query 'get_credenziale_by_refid_greather_then_id': " + dbquery)

    var deadline = java.lang.System.currentTimeMillis() + db_retry_max_total_ms
    var attempt = 0
    while (true) {
        attempt++
        try {
            return db.readValue(dbquery)
        } catch (e) {
            if (java.lang.System.currentTimeMillis() >= deadline) {
                karate.log("Query 'get_credenziale_by_refid_greather_then_id' giving up after " + attempt + " attempt(s) (budget " + db_retry_max_total_ms + "ms)")
                throw e
            }
            karate.log("Query 'get_credenziale_by_refid_greather_then_id' attempt " + attempt + " empty, retrying in " + db_retry_interval_ms + "ms")
            java.lang.Thread.sleep(db_retry_interval_ms)
        }
    }
}
