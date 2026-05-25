function reset_counter() {

    govwayDbConfig = {
        username: karate.properties['db_username'],
        password: karate.properties['db_password'],
        url: karate.properties['db_url'],
        driverClassName: karate.properties['db_driverClassName']
     }

    java.lang.Thread.sleep(db_sleep_before_read)
    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    dbquery = "DELETE FROM id_messaggio_relativo WHERE protocollo = 'modipa' AND info_associata IN ('eServiceTestID', 'eServiceTestID1', 'eServiceTestID2', 'eServiceMultiple', 'eServiceIDError', 'eServiceIDSenzaConfSignalHub', 'SignalHubTestSOAP12')"
    karate.log("Query: " + dbquery)
    return db.update(dbquery);
}
