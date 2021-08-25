package org.openspcoop2.core.protocolli.trasparente.testsuite.token.attribute_authority;

import java.io.File;
import java.util.Properties;

import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;

public class JWTUtilities {

	public static String builtJWT_OIDC(String sub,String iss,String username, String clientId,String audience) throws Exception {
		
		File fKeystore = new File("/etc/govway/keys/jose_keystore_example.jks"); 
		
		Properties signatureProps = new Properties();
		signatureProps.put("rs.security.keystore.file", fKeystore.getPath());
		signatureProps.put("rs.security.keystore.type","jks");
		signatureProps.put("rs.security.keystore.alias","openspcoop");
		signatureProps.put("rs.security.keystore.password","123456");
		signatureProps.put("rs.security.key.password","key123456");
		signatureProps.put("rs.security.signature.algorithm","RS256");
		signatureProps.put("rs.security.signature.include.cert","true");
		signatureProps.put("rs.security.signature.include.public.key","false");
		signatureProps.put("rs.security.signature.include.key.id","false");
		signatureProps.put("rs.security.signature.include.cert.sha1","false");
		signatureProps.put("rs.security.signature.include.cert.sha256","false");

		String jsonInput = "{\n"+
				"\"iss\": \""+(iss!=null ? iss : "http://iss.example")+"\",\n"+
				"\"sub\": \""+(sub!=null ? sub : "http://sub.example")+"\",\n"+
				"\"azp\": \""+(clientId!=null ? clientId : "httpClientExample")+"\",\n"+
				"\"preferred_username\": \""+(username!=null ? username : "PaoloRossi")+"\",\n"+
				"\"aud\": \""+(audience!=null ? audience : "https://127.0.0.1/example")+"\",\n"+
				"\"iat\": "+(DateManager.getTimeMillis()/1000)+",\n"+
				"\"exp\": "+((DateManager.getTimeMillis()/1000)+300)+",\n"+
				"\"jti\": \""+java.util.UUID.randomUUID().toString()+"\"\n"+
				"}";
		//System.out.println(jsonInput);
		
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
		JsonSignature jsonSignature = new JsonSignature(signatureProps, options);
		String token = jsonSignature.sign(jsonInput);
		//System.out.println(token);
		return token;
	}
	
}
