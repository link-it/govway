function(tok) {
    var Base64 = Java.type('java.util.Base64')
    var StringType = Java.type('java.lang.String')
    var prefix = "Bearer"
    tok = tok.slice(prefix.length).trim()

    var components = tok.split('.')
    
    var ret = {
        'header': JSON.parse(new StringType(Base64.getDecoder().decode(new StringType(components[0])))),
        'payload': JSON.parse(new StringType(Base64.getDecoder().decode(new StringType(components[1]))))
    }
    //karate.log("Ret: ", ret)

    return ret
}