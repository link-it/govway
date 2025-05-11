function compute_digest(type, message) {

  	MessageDigest = Java.type("java.security.MessageDigest")
  	String = Java.type("java.lang.String")
  	out = MessageDigest.getInstance(type).digest(new String(message).getBytes())
  	
  	Base64 = Java.type("java.util.Base64")
   
   return Base64.getEncoder().encodeToString(out);
}