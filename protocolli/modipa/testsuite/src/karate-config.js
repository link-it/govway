function fn() {    
  var env = karate.env; // get system property 'karate.env'
  karate.log('karate.env system property was:', env);
  if (!env) {
    env = 'dev';
  }
  if (env == 'dev') {
    // customize
    // e.g. config.foo = 'bar';
  } else if (env == 'e2e') {
    // customize
  }

  connect_timeout = karate.properties["connect_timeout"] || 1000000
  read_timeout = karate.properties["read_timeout"] || 1000000

  karate.configure('connectTimeout',  connect_timeout);
  karate.configure('readTimeout', read_timeout);
  
  return { 
    govway_base_path: karate.properties["govway_base_path"],
    govwayDbConfig: { 
      username: karate.properties['db_username'],
      password: karate.properties['db_password'],
      url: karate.properties['db_url'],
      driverClassName: karate.properties['db_driverClassName']
    },
    db_sleep_before_read: karate.properties['db_sleep_before_read']
  }
}
