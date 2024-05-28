function getDbConfig() {
db_sleep_before_read
    var res_username, res_password, res_url, res_driverClassName, res_sleep_before_read;
    try {
        // Prova a ottenere db_username dalle proprietà di Karate
        res_username = karate.properties['db_username'];
        res_password = karate.properties['db_password'];
        res_url = karate.properties['db_url'];
        res_driverClassName = karate.properties['db_driverClassName'];
        res_sleep_before_read = karate.properties['db_sleep_before_read'];
        if (!res_username) throw 'db_username from karate.properties is undefined'; // Forza passaggio al catch se nullo
    } catch (e1) {
        try {
            // Prova a ottenere db_username come variabile globale
            res_username = db_username;
            res_password = db_password;
            res_url = db_url;
            res_driverClassName = db_driverClassName;
	    res_sleep_before_read = db_sleep_before_read;
            if (!res_username) throw 'global db_username is undefined'; // Forza passaggio al catch se nullo
        } catch (e2) {
            karate.log('Errore nel recuperare db config'); 
	    return
        }
    }
    return {
        username: res_username,
        password: res_password,
        url: res_url,
        driverClassName: res_driverClassName,
	sleep_before_read: parseInt(res_sleep_before_read,10)
     }
}

