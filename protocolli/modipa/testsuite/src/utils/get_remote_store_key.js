function get_remote_store_key(kid) {

    govwayDbConfig = { 
        username: karate.properties['db_username'],
        password: karate.properties['db_password'],
        url: karate.properties['db_url'],
        driverClassName: karate.properties['db_driverClassName']
     }

//    db_sleep_before_read = karate.properties['db_sleep_before_read']
        db_sleep_before_read = parseInt(karate.properties['db_sleep_before_read'], 10);
    java.lang.Thread.sleep(db_sleep_before_read)
    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    dbquery = "select * from remote_store_key where kid='"+kid+"'"
    //karate.log("Query: " + dbquery)
    return db.readRows(dbquery);
}
