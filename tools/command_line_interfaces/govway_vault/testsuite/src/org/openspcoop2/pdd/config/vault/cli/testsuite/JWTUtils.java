/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.pdd.config.vault.cli.testsuite;

import java.util.Properties;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWEOptions;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonEncrypt;
import org.openspcoop2.utils.security.JsonSignature;

/**
* JWTUtils
*
* @author Andrea Poli (andrea.poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class JWTUtils {
	
	private JWTUtils() {}

	private static final String FALSE = "false";
	private static final String TRUE = "true";
	
	private static final String PATH_EXAMPLE_1 = "/etc/govway/keys/xca/ExampleClient1.p12";
	private static final String PATH_EXAMPLE_2 = "/etc/govway/keys/xca/ExampleClient2.p12";
	private static final String PATH_EXAMPLE_SERVER = "/etc/govway/keys/xca/ExampleServer.p12";
	
	private static final String CLAIM_END = "\",\n";
	
	public static String buildJwsTokenClient1(boolean signatureNone) throws UtilsException {
		return buildJwtToken(true, false, false, true, signatureNone);
	}
	
	public static String buildJweTokenClient2(boolean signatureNone) throws UtilsException {
		return buildJwtToken(false, false, true, false, signatureNone);
	}
	
	private static String buildJwtToken(boolean client1, boolean server, boolean client2, boolean signature, boolean signatureNone) throws UtilsException {
		
		String path = PATH_EXAMPLE_1;
		String type = "pkcs12";
		String alias = "ExampleClient1"; // uso questo certificato poiche' generato con RSA256. Gli altri sono RSA1
		String password = "123456";
		String passwordKey = "123456";
		if(server) {
			path = PATH_EXAMPLE_SERVER;
			alias = "ExampleServer";
		}
		else if(client2) {
			path = PATH_EXAMPLE_2;
			alias = "ExampleClient2";
		}
		
		String jsonInput = "{\n"+
				"\"iss\": \"http://iss/"+alias+CLAIM_END+
				"\"sub\": \"testSub-"+alias+CLAIM_END+
				"\"client_id\": \"testClientId-"+alias+CLAIM_END+
				"\"azp\": \"testClientId-"+alias+CLAIM_END+
				"\"username\": \"testUser-"+alias+CLAIM_END+
				"\"preferred_username\": \"testUser-"+alias+CLAIM_END+
				"\"aud\": \"testAud-"+alias+CLAIM_END+
				"\"iat\": "+(DateManager.getTimeMillis()/1000)+",\n"+
				"\"exp\": "+((DateManager.getTimeMillis()/1000)+300)+",\n"+
				"\"jti\": \""+java.util.UUID.randomUUID().toString()+"\"\n"+
				"}";
		
		Properties props = new Properties();
		
		if(signature) {
			props.put("rs.security.keystore.file", path);
		}
		else {
			props.put("rs.security.keystore.file", "/etc/govway/keys/xca/trustStore_certificati.jks");
			type = "jks";
		}
		
		props.put("rs.security.keystore.type",type);
		props.put("rs.security.keystore.alias",alias);
		props.put("rs.security.keystore.password",password);
		props.put("rs.security.key.password",passwordKey);
		
		/**System.out.println(jsonInput);*/
		if(signature) {
			JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
			
			if(signatureNone) {
				props.put("rs.security.signature.algorithm","NONE");
				props.put("rs.security.signature.include.cert",FALSE);
				props.put("rs.security.signature.include.key.id",FALSE);
			}
			else {
				props.put("rs.security.signature.algorithm","RS256");
				props.put("rs.security.signature.include.cert",TRUE);
				props.put("rs.security.signature.include.key.id",TRUE);
			}
			props.put("rs.security.signature.include.public.key",FALSE);
			props.put("rs.security.signature.include.cert.sha1",FALSE);
			props.put("rs.security.signature.include.cert.sha256",FALSE);
			
			JsonSignature jsonSignature = new JsonSignature(props, options);
			/**String token = jsonSignature.sign(jsonInput);
			System.out.println(token);
			return token;*/		
			return jsonSignature.sign(jsonInput);
			
		}
		else {
			JWEOptions options = new JWEOptions(JOSESerialization.COMPACT);
			
			props.put("rs.security.encryption.key.algorithm", "RSA1_5");
			props.put("rs.security.encryption.content.algorithm","A256GCM");
			props.put("rs.security.encryption.include.cert",TRUE);
			props.put("rs.security.encryption.include.public.key",FALSE);
			props.put("rs.security.encryption.include.key.id",TRUE);
			props.put("rs.security.encryption.include.cert.sha1",FALSE);
			props.put("rs.security.encryption.include.cert.sha256",FALSE);
			
			JsonEncrypt jsonEncrypt = new JsonEncrypt(props, options);
			/**String token = jsonEncrypt.encrypt(jsonInput);
			System.out.println(token);
			return token;*/	
			return jsonEncrypt.encrypt(jsonInput);
		}

	}
	
}
