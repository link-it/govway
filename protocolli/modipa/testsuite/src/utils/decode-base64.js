function(tok) {

    var Base64 = Java.type('java.util.Base64')
    
    var decodedBytes = Base64.getDecoder().decode(tok.getBytes())

    var decodedString = new java.lang.String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);
    
    //karate.log("Ret: ", decodedString)

    return decodedString;
}
