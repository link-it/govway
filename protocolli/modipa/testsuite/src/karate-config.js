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
    govway_config_api_path: karate.properties["govway_config_api_path"],
    govway_monitor_api_path: karate.properties["govway_monitor_api_path"],
    batch_path: karate.properties["batch_path"],
    //url_mock: "http://127.0.0.1:" + karate.properties["http_mock_port"],
    url_mock: "http://localhost:8080/govway/rest/in/DemoSoggettoErogatore/MockServerInvocatoDaTestSuite/v1",
    config_api_username: karate.properties["config_api_username"],
    config_api_password: karate.properties["config_api_password"],
    monitor_api_username: karate.properties["monitor_api_username"],
    monitor_api_password: karate.properties["monitor_api_password"],
    jmx_username: karate.properties["jmx_username"],
    jmx_password: karate.properties["jmx_password"],
    govwayDbConfig: { 
      username: karate.properties['db_username'],
      password: karate.properties['db_password'],
      url: karate.properties['db_url'],
      driverClassName: karate.properties['db_driverClassName']
    },
    db_sleep_before_read: karate.properties['db_sleep_before_read']
  }
}
