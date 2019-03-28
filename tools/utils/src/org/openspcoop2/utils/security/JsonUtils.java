/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

package org.openspcoop2.utils.security;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.jose.jwe.JweException;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jws.JwsException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * JsonUtils
 * 
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class JsonUtils {

	public static Message newMessage() {
		Message m = new MessageImpl();
		Exchange ex = new ExchangeImpl();
		m.setExchange(ex);
		return m;
	}
	
	public static boolean SIGNATURE = true;
	public static boolean ENCRYPT = false;
	public static boolean DECRYPT = false;
	public static boolean SENDER = true;
	public static boolean RECEIVER = false;
	public static UtilsException convert(JOSESerialization serialization, boolean signature, boolean roleSender, Throwable t) {
		
		StringBuffer bf = new StringBuffer();
		if(serialization!=null) {
			bf.append("[").append(serialization.name()).append("] ");
		}
		
		if(t instanceof JwsException) {
			JwsException exc = (JwsException) t;
			if(exc.getError()==null) {
				if(roleSender) {
					bf.append("Signature failure");
				}
				else {
					bf.append("Signature verification failure");
				}
			}			
			else {
				bf.append(exc.getError().name());
			}
			if(exc.getMessage()==null && exc.getCause()==null && exc.getLocalizedMessage()==null) {
				return new UtilsException(bf.toString(),t);
			}
		}
		else if(t instanceof JweException) {
			JweException exc = (JweException) t;
			if(exc.getError()==null) {
				if(roleSender) {
					bf.append("Encrypt failure");
				}
				else {
					bf.append("Decrypt failure");
				}
			}			
			else {
				bf.append(exc.getError().name());
			}
			if(exc.getMessage()==null && exc.getCause()==null && exc.getLocalizedMessage()==null) {
				return new UtilsException(bf.toString(),t);
			}
		}
		else if(signature) {
			if(roleSender) {
				bf.append("Signature failure");
			}
			else {
				bf.append("Signature verification failure");
			}
		}
		else {
			if(roleSender) {
				bf.append("Encrypt failure");
			}
			else {
				bf.append("Decrypt failure");
			}
		}
				
		String msg = Utilities.getInnerNotEmptyMessageException(t).getMessage();
		
		Throwable innerExc = Utilities.getLastInnerException(t);
		String innerMsg = null;
		if(innerExc!=null){
			innerMsg = innerExc.getMessage();
		}
		
		String messaggio = null;
		if(msg!=null && !"".equals(msg) && !"null".equals(msg)) {
			messaggio = new String(msg);
			if(innerMsg!=null && !"".equals(innerMsg) && !"null".equals(innerMsg) && !innerMsg.equals(msg)) {
				messaggio = messaggio + " ; " + innerMsg;
			}
		}
		else{
			if(innerMsg!=null && !"".equals(innerMsg) && !"null".equals(innerMsg)) {
				messaggio = innerMsg;
			}
		}
		
		if(messaggio!=null) {
			bf.append(": ");
			bf.append(messaggio);
		}
		return new UtilsException(bf.toString(),t);
	}
	
	public static File normalizeProperties(Properties props) throws Exception {
		
		String propertyKeystoreName = JoseConstants.RSSEC_KEY_STORE_FILE;
		String path = props.getProperty(propertyKeystoreName);
		byte[]content = null;
		if(path!=null && (path.startsWith("http") || path.startsWith("https"))) {
			HttpResponse httpResponse = null;
			String trustStoreProperty =  JoseConstants.RSSEC_KEY_STORE_FILE+".ssl";
			String trustStorePasswordProperty =  JoseConstants.RSSEC_KEY_STORE_PSWD+".ssl";
			String trustStoreTypeProperty =  JoseConstants.RSSEC_KEY_STORE_TYPE+".ssl";
			String trustStore = props.getProperty(trustStoreProperty);
			String trustStorePassword = props.getProperty(trustStorePasswordProperty);
			String trustStoreType = props.getProperty(trustStoreTypeProperty);
			if(trustStore!=null) {
				if(trustStorePassword==null) {
					throw new Exception("TrustStore ssl password undefined");
				}
				if(trustStoreType==null) {
					throw new Exception("TrustStore ssl type undefined");
				}
			}
			if( (path.startsWith("https:") && trustStore==null) || path.startsWith("http:") ) {
				//System.out.println("http");
				httpResponse = HttpUtilities.getHTTPResponse(path, 60000, 10000);
			}
			else {
				//System.out.println("https");
				httpResponse = HttpUtilities.getHTTPSResponse(path, 60000, 10000, trustStore, trustStorePassword, trustStoreType);
			}
			if(httpResponse==null || httpResponse.getContent()==null) {
				throw new Exception("Keystore '"+path+"' unavailable");
			}
			if(httpResponse.getResultHTTPOperation()!=200) {
				throw new Exception("Retrieve keystore '"+path+"' failed (returnCode:"+httpResponse.getResultHTTPOperation()+")");
			}
			content = httpResponse.getContent();
		}
		else if(path!=null && (path.startsWith("file"))){
			File f = new File(new URI(path));
			content = FileSystemUtilities.readBytesFromFile(f);
		}
		
		File fTmp = null;
		if(content!=null) {
		
			String tipo = props.getProperty(JoseConstants.RSSEC_KEY_STORE_TYPE);
			if(tipo==null) {
				tipo = "jks";
			}
			
			fTmp = File.createTempFile("keystore", "."+tipo);
			FileSystemUtilities.writeFile(fTmp, content);
			props.remove(propertyKeystoreName);
			props.put(propertyKeystoreName, fTmp.getAbsolutePath());
			
		}
		
		return fTmp;
	}
	
	public static boolean isDynamicProvider(Properties props) throws Exception {
		String alias = props.getProperty(JoseConstants.RSSEC_KEY_STORE_ALIAS);
		if("*".equalsIgnoreCase(alias)) {
			props.remove(JoseConstants.RSSEC_KEY_STORE_ALIAS);
			return true;
		}
		return false;
	}
	
	public static String readAlias(String jsonString) throws Exception {
		if(jsonString.contains(".")==false) {
			throw new Exception("Invalid format (expected COMPACT)");
		}
		String [] tmp = jsonString.split("\\.");
		byte[] header = Base64Utilities.decode(tmp[0].trim());
		JsonNode node = JSONUtils.getInstance().getAsNode(header).get("kid");
		if(node==null) {
			throw new Exception("Claim 'kid' not found");
		}
		String kid = node.asText();
		if(kid==null) {
			throw new Exception("Claim 'kid' without value");
		}
		return kid;
	}

	public static JsonWebKey readKey(JsonWebKeys jsonWebKeys, String alias) throws UtilsException {
		if(alias==null) {
			throw new UtilsException("Alias unknonw");
		}
		JsonWebKey jsonWebKey = jsonWebKeys.getKey(alias);
		if(jsonWebKey==null) {
			throw new UtilsException("Key with alias '"+alias+"' unknonw");
		}
		return jsonWebKey;
	}
}
