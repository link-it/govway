per eseguire i test comando:
ant run_test -Dkarate.options="applicativi/create.feature --tags=Create204"


se capita l'errore Cannot read the array length because "src" is null classpath... e ci si trova in una situazione simile:
  var encoded = Base64.getEncoder().encodeToString(temp.bytes);
la soluzione è
  var encoded = Base64.getEncoder().encodeToString(temp.getBytes());

nei test .feature
 call pause(1000) risolta in errore quando eseguito:
     [java]     => org.opentest4j.AssertionFailedError: not a callable feature or js function: [type: NULL, value: null]

 la forma corretta è:
	pause(1000)
