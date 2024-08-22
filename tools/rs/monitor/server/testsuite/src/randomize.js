function randomize(src, paths) {

    if ( !src || !paths ) {
        throw "Not valid arguments:jsonData:" + src + ", paths:" + paths;
    }

    paths.forEach( function(path) {
                
        // mflag:
        // Codice originale sembra copiato da: https://stackoverflow.com/questions/6491463/accessing-nested-javascript-objects-and-arrays-by-string-path/60553207#60553207
        // le due istruzioni seguenti non servono: gli input non contengono elementi tra quadre e nemmeno . iniziali, ma sono invece path separati da . (es: src.credenziali.certificato.issuer)
        // path = path.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
        // path = path.replace(/^\./, ''); // strip a leading dot

        var pathArray = path.split('.');        
        
        var target = src;
        var i = 0;
        while ( i < pathArray.length - 1 && typeof (target[pathArray[i]]) !== 'undefined') {
            target = target[pathArray[i]];
            i++;
        }

        if ( i == pathArray.length - 1) {
            var rnd = '' + java.util.UUID.randomUUID();
            // Rimuoviamo i - così non disturbiamo eventuali validatori 
            // modificato per non usare regexp: target[pathArray[i]] = target[pathArray[i]] + rnd.replace(/-/g,'')
            var cleanedRnd = '';
	    for (var j = 0; j < rnd.length; j++) {
    		if (rnd[j] !== '-') {
        	cleanedRnd += rnd[j];
                }
            }

            target[pathArray[i]] = target[pathArray[i]] + cleanedRnd;

            // Limitiamo il campo a 32 caratteri
            target[pathArray[i]] = target[pathArray[i]].substring(0,32)
        }
    });
}

