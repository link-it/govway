function(tok, kind) {

    if (!kind) {
        kind = "Bearer"
    }

    if (kind == "Bearer") {
        var prefix = "Bearer"
        tok = tok.slice(prefix.length).trim()
    }
 
    var components = tok.split('.')
    
    var ret = {
        'header': components[0],
        'payload': components[1]
    }
    //karate.log("Ret: ", ret)

    return ret
}
