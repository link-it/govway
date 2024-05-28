function clean_remote_store_key(kid) {
    var getDbConfig = read('classpath:utils/get_db_config.js');

    var govwayDbConfig = getDbConfig()

    db_sleep_before_read = govwayDbConfig.sleep_before_read

    java.lang.Thread.sleep(db_sleep_before_read)
    DbUtils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils')
    db = new DbUtils(govwayDbConfig)
    dbquery = "delete from remote_store_key where kid='"+kid+"'"
    var n = db.update(dbquery);
    karate.log("Update (row:"+n+"): " + dbquery)
}
