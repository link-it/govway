function(tok) {

    var Base64 = Java.type('java.util.Base64')
    
    var ret = Base64.getEncoder().encodeToString(tok.getBytes())

    //karate.log("Ret: ", ret)

    return ret
}
