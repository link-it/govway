function randomize(src, paths) {

    if ( !src || !paths ) {
        throw "Not valid arguments:jsonData:" + src + ", paths:" + paths;
    }

    paths.forEach( function(path) {
                
        path = path.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
        path = path.replace(/^\./, ''); // strip a leading dot
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
            target[pathArray[i]] = target[pathArray[i]] + rnd.replace(/-/g,'')
            // Limitiamo il campo a 32 caratteri
            target[pathArray[i]] = target[pathArray[i]].substring(0,32)
        }

    });
    
}


