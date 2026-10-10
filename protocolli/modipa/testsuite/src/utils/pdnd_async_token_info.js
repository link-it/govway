function pdnd_async_token_info(idTransazione) {
    // Ritorna le informazioni sul token salvate nella transazione:
    // - assertion: payload della client assertion inviata all'authorization server (negoziazione)
    // - accessToken: payload del voucher negoziato (negoziazione) o validato (claims)
    // - raw: oggetto token_info

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
    var rows = db.readRows("select token_info from transazioni where id='"+idTransazione+"'")
    if (rows == null || rows.size() == 0) {
        return null;
    }
    var it = rows.get(0).values().iterator()
    var v = it.hasNext() ? it.next() : null
    if (v == null) {
        return null;
    }
    var raw = JSON.parse('' + v)
    var Base64 = Java.type('java.util.Base64')
    var StringType = Java.type('java.lang.String')
    var decode = function(jwt) {
        if (jwt == null) return null;
        var parts = ('' + jwt).split('.');
        return JSON.parse('' + new StringType(Base64.getUrlDecoder().decode(parts[1])));
    }
    var result = { raw: raw, assertion: null, accessToken: null }
    if (raw.request && raw.request.jwtClientAssertion && raw.request.jwtClientAssertion.token) {
        result.assertion = decode(raw.request.jwtClientAssertion.token)
    }
    if (raw.accessToken) {
        result.accessToken = decode(raw.accessToken)
    }
    return result;
}
