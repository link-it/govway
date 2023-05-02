/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.logger.traccia;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.XmlSignature;
import org.w3c.dom.Element;

/**	
 * Signature
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Signature {

	private String keystore_type = KeystoreType.JKS.getNome();
	private String keystore_path = null;
	private String keystore_password = null;
	private String key_alias = null;
	private String key_password = null;
	private String json_signatureAlgorithm = "RS256";
	private JOSESerialization json_signatureSerialization = JOSESerialization.COMPACT;
	private boolean json_signatureDetached = false;
	private boolean json_signaturePayloadEncoding = true;
	private String xml_signatureAlgorithm = XmlSignature.DEFAULT_SIGNATURE_METHOD;
	private String xml_digestAlgorithm = XmlSignature.DEFAULT_DIGEST_METHOD;
	private String xml_canonicalizationAlgorithm = XmlSignature.DEFAULT_CANONICALIZATION_METHOD;
	private boolean xml_addBouncyCastleProvider;
	private boolean xml_addX509KeyInfo = true;
	private boolean xml_addRSAKeyInfo = false;
	
	public Signature() {		
	}
	public Signature(Properties pConf) throws TracciaException {
		try {
			Field [] fields = Signature.class.getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				fieldName = fieldName.replace("_", ".");
				if(pConf.containsKey(fieldName)) {
					String value = pConf.getProperty(fieldName);
					String bCN = boolean.class.getName()+"";
					String joseS = JOSESerialization.class.getName()+"";
					if(bCN.equals(field.getType().getName())) {
						field.set(this, "true".equalsIgnoreCase(value));
					}
					else if(joseS.equals(field.getType().getName())) {
						field.set(this, JOSESerialization.valueOf(value));
					}
					else {
						field.set(this, value);
					}
				}
			}
		}catch(Exception e) {
			throw new TracciaException(e.getMessage(),e);
		}
	}
	
	
	private boolean initialized = false;
	private JsonSignature jsonCompactSignature;
	private XmlSignature xmlSignature;
	private MessageXMLUtils xmlUtils;
	
	public synchronized void init() throws TracciaException {
		
		if(!this.initialized) {
		
			if(this.keystore_path==null) {
				throw new TracciaException("Keystore path undefined");
			}
			if(this.keystore_type==null) {
				throw new TracciaException("Keystore type undefined");
			}
			if(this.keystore_password==null) {
				throw new TracciaException("Keystore type undefined");
			}
			
			if(this.key_alias==null) {
				throw new TracciaException("Alias key undefined");
			}
			if(this.key_password==null) {
				throw new TracciaException("Password key undefined");
			}
			
			if(this.json_signatureAlgorithm==null) {
				throw new TracciaException("Json Signature Algorithm undefined");
			}
			if(this.json_signatureSerialization==null) {
				throw new TracciaException("Json Signature Representation undefined");
			}
			
			if(this.xml_signatureAlgorithm==null) {
				throw new TracciaException("Xml Signature Algorithm undefined");
			}
			if(this.xml_digestAlgorithm==null) {
				throw new TracciaException("Xml Digest Algorithm undefined");
			}
			if(this.xml_canonicalizationAlgorithm==null) {
				throw new TracciaException("Xml Canonicalization Algorithm undefined");
			}
			
			InputStream isKeystore = Signature.class.getResourceAsStream(this.keystore_path);
			try {
				if(isKeystore==null) {
					File f = new File(this.keystore_path);
					if(!f.exists()) {
						throw new TracciaException("Keystore path '"+this.keystore_path+"' not exists");
					}
					if(!f.canRead()) {
						throw new TracciaException("Keystore path '"+this.keystore_path+"' cannot read");
					}
					isKeystore = new FileInputStream(f);
				}
				
				java.security.KeyStore keystore = java.security.KeyStore.getInstance(this.keystore_type);
				keystore.load(isKeystore, this.keystore_password.toCharArray());
				
				JWSOptions jwsOptions = new JWSOptions(this.json_signatureSerialization);
				jwsOptions.setDetached(this.json_signatureDetached);
				jwsOptions.setPayloadEncoding(this.json_signaturePayloadEncoding);
				this.jsonCompactSignature = new JsonSignature(keystore, false, this.key_alias, this.key_password, this.json_signatureAlgorithm, jwsOptions);
				
				this.xmlSignature = new XmlSignature(keystore, this.key_alias, this.key_password, this.xml_addBouncyCastleProvider);
				if(this.xml_addX509KeyInfo) {
					this.xmlSignature.addX509KeyInfo();
				}
				else if(this.xml_addRSAKeyInfo) {
					this.xmlSignature.addRSAKeyInfo();
				}
				
				this.xmlUtils = MessageXMLUtils.DEFAULT;
			}
			catch(Exception e) {
				throw new TracciaException(e.getMessage(),e);
			}
			finally {
				try {
					if(isKeystore!=null) {
						isKeystore.close();
					}
				}catch(Exception eClose) {
					// close
				}
			}
		
			this.initialized = true;
		}
	}
	
	public String jsonSign(String content) throws TracciaException {
		try { 
			return this.jsonCompactSignature.sign(content);
		}
		catch(Exception e) {
			throw new TracciaException(e.getMessage(),e);
		}
	}
	
	public String xmlSign(String content) throws TracciaException {
		return this.xmlSign(content.getBytes());
	}
	public String xmlSign(byte[] content) throws TracciaException {
		try { 
			Element node = this.xmlUtils.newElement(content);
			this.xmlSignature.sign(node);
			return this.xmlUtils.toString(node);
		}
		catch(Exception e) {
			throw new TracciaException(e.getMessage(),e);
		}
	}

	
	public void setKeystore_type(String keystore_type) {
		this.keystore_type = keystore_type;
	}
	public void setKeystore_path(String keystore_path) {
		this.keystore_path = keystore_path;
	}
	public void setKeystore_password(String keystore_password) {
		this.keystore_password = keystore_password;
	}
	public void setKey_alias(String key_alias) {
		this.key_alias = key_alias;
	}
	public void setKey_password(String key_password) {
		this.key_password = key_password;
	}
	public void setJson_signatureAlgorithm(String json_signatureAlgorithm) {
		this.json_signatureAlgorithm = json_signatureAlgorithm;
	}
	public void setJson_signatureSerialization(JOSESerialization json_signatureSerialization) {
		this.json_signatureSerialization = json_signatureSerialization;
	}
	public void setJson_signatureDetached(boolean json_signatureDetached) {
		this.json_signatureDetached = json_signatureDetached;
	}
	public void setJson_signaturePayloadEncoding(boolean json_signaturePayloadEncoding) {
		this.json_signaturePayloadEncoding = json_signaturePayloadEncoding;
	}
	public void setXml_signatureAlgorithm(String xml_signatureAlgorithm) {
		this.xml_signatureAlgorithm = xml_signatureAlgorithm;
	}
	public void setXml_digestAlgorithm(String xml_digestAlgorithm) {
		this.xml_digestAlgorithm = xml_digestAlgorithm;
	}
	public void setXml_canonicalizationAlgorithm(String xml_canonicalizationAlgorithm) {
		this.xml_canonicalizationAlgorithm = xml_canonicalizationAlgorithm;
	}
	public void setXml_addBouncyCastleProvider(boolean xml_addBouncyCastleProvider) {
		this.xml_addBouncyCastleProvider = xml_addBouncyCastleProvider;
	}
	public void setXml_addX509KeyInfo(boolean xml_addX509KeyInfo) {
		this.xml_addX509KeyInfo = xml_addX509KeyInfo;
	}
	public void setXml_addRSAKeyInfo(boolean xml_addRSAKeyInfo) {
		this.xml_addRSAKeyInfo = xml_addRSAKeyInfo;
	}

}
