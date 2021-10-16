function(encoded) {
  var Base64 = Java.type('java.util.Base64');
  var decoded = Base64.getDecoder().decode(encoded);
  return decoded;
}

