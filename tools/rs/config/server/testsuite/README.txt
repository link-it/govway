per eseguire i test comando:
ant run_test -Dkarate.options="applicativi/create.feature --tags=Create204"


se capita l'errore Cannot read the array length because "src" is null classpath... e ci si trova in una situazione simile:
  var encoded = Base64.getEncoder().encodeToString(temp.bytes);
è probabile che la soluzione sia
  var encoded = Base64.getEncoder().encodeToString(temp.getBytes());
