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
  var govwayUrl = 'http://localhost:8080';

  var config = {
      basicCred: { username: 'amministratore', password: '123456' },
      configUrl: govwayUrl + '/govwayAPIConfig',
      // URL del backend rs-api-config diretto (senza passare dal gateway GovWay),
      // necessaria per i test di permessi che devono autenticare l'utente fornito
      // dal test invece di usare il service-account configurato nel gateway.
      configDirectUrl: govwayUrl + '/govwayAPIConfig',
      // Connessione DB per i test di permessi che creano/eliminano utenze test.
      govwayDbConfig: {
        username: 'govway',
        password: 'govway',
        url: 'jdbc:postgresql://localhost:5432/govway',
        driverClassName: 'org.postgresql.Driver'
      },
      soggettoDefault: 'ENTE'
  };
  return config;
}
