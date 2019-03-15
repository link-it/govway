function() {    
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
  karate.configure('connectTimeout', 5000);
  karate.configure('readTimeout', 5000);  
  var protocol = 'http';
  var govwayUrl = 'http://localhost:8080';

  var config = {
      basicCred: { username: 'amministratore', password: '123456' },
      configUrl: govwayUrl + '/govwayAPIConfig'
  };
  return config;
}
