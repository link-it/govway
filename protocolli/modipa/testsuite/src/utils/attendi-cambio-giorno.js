function(margineSecondi) {

    // Le policy di rate limiting giornaliere conteggiano per giorno di calendario (nel timezone della JVM, la stessa macchina di GovWay):
    // se una sequenza di invocazioni attraversa la mezzanotte il contatore riparte da zero e la richiesta che dovrebbe violare la policy viene accettata.
    // Se mancano meno di 'margineSecondi' alla mezzanotte si attende l'inizio del nuovo giorno prima di procedere.
    var LocalDateTime = Java.type('java.time.LocalDateTime');
    var ChronoUnit = Java.type('java.time.temporal.ChronoUnit');
    var now = LocalDateTime.now();
    var mezzanotte = now.toLocalDate().plusDays(1).atStartOfDay();
    var mancanti = now.until(mezzanotte, ChronoUnit.MILLIS);
    if (mancanti < (margineSecondi * 1000)) {
        var attesa = mancanti + 2000;
        karate.log("Mancano " + mancanti + "ms alla mezzanotte: attendo " + attesa + "ms per non attraversare il cambio giorno durante il test");
        java.lang.Thread.sleep(attesa);
    }
}
