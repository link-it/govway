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
  karate.configure('connectTimeout',  1000000);
  karate.configure('readTimeout', 1000000);
  var protocol = 'http';
  var govwayUrl = 'http://localhost:8080';

  var config = {

      monitorCred: { username: 'operatore', password: 'Password1!' },
      monitorUrl: govwayUrl + '/govwayAPIMonitor',
      govwayDbConfig: { 
        username: 'govway',
        password: 'govway',
        url: 'jdbc:postgresql://localhost:5432/govway_330',
        driverClassName: 'org.postgresql.Driver'
      },
      configCred:  { username: 'amministratore', password: 'Password1!'},
      configUrl: govwayUrl + '/govwayAPIConfig',
      soggettoDefault: 'Ente',

      statsInterval: 16000

  };
  
  //var setup = karate.callSingle('classpath:prepare_tests.feature', config);
  // and it sets a variable called 'setup' used in other features
  //config.setup = setup;
  return config;
}
