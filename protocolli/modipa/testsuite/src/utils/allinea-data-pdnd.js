function(prima, valoreEffettivo) {

    // Il simulatore PDND valorizza le informazioni sull'organizzazione e sul client con il minuto in cui viene invocato,
    // che può essere successivo a quello calcolato dal test prima della richiesta se nel frattempo il minuto è cambiato.
    // Si restituisce l'istante, tra quello calcolato prima della richiesta e quello attuale, il cui minuto è presente nel valore effettivo;
    // se nessuno dei due è presente si restituisce l'istante calcolato prima della richiesta, così che le verifiche successive falliscano.
    var formatter = Java.type('java.time.format.DateTimeFormatter').ofPattern("YY-MM-dd-HH-mm");
    var dopo = Java.type('java.time.LocalDateTime').now();

    if (valoreEffettivo != null) {
        var v = '' + valoreEffettivo;
        if (v.indexOf(prima.format(formatter)) < 0 && v.indexOf(dopo.format(formatter)) >= 0) {
            karate.log("Minuto cambiato durante la richiesta: date attese allineate a '"+dopo.format(formatter)+"' (calcolato prima '"+prima.format(formatter)+"')")
            return dopo;
        }
    }
    return prima;
}
