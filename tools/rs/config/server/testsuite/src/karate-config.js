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
  karate.configure('connectTimeout', 5000);
  karate.configure('readTimeout', 60000);
  var protocol = 'http';
  var govwayUrl = 'http://localhost:8081';

  var config = {
      basicCred: { username: 'amministratore', password: 'secret' },
      configUrl: govwayUrl + '/govwayAPIConfig',
      soggettoDefault: 'ENTE'
  };
  return config;
}
