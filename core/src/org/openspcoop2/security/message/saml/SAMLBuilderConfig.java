/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.security.message.saml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.saml.bean.Version;
import org.opensaml.saml.saml2.core.NameIDType;
import org.openspcoop2.utils.Utilities;

/**
 * SAMLCallbackHandler
 * 	
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SAMLBuilderConfig {

	// ---- STATIC CONFIG CACHE -----
	
	private static Hashtable<String, SAMLBuilderConfig> SAML_CACHE_CONFIG = new Hashtable<String, SAMLBuilderConfig>();
	private static synchronized void addSamlConfig(String propertiesName,SAMLBuilderConfig p){
		if(SAML_CACHE_CONFIG.containsKey(propertiesName)==false){
			SAML_CACHE_CONFIG.put(propertiesName, p);
		}
	}
	public static SAMLBuilderConfig getSamlConfig(String properties) throws IOException {
		InputStream is = null;
		try{
			File f = new File(properties);
			if(f.exists()){
				is = new FileInputStream(f);
			}
			else{
				is = SAMLBuilderConfig.class.getResourceAsStream("/"+properties);
			}
			if(is==null){
				throw new IOException("SAMLPropFile ["+properties+"]: not found"); 
			}
			try{
				return getSamlConfig(Utilities.getAsProperties(is));
			}catch(Exception e){
				throw new IOException("SAMLPropFile ["+properties+"]: "+e.getMessage(),e); 
			}		
		}
		finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){}
		}
	}
	public static SAMLBuilderConfig getSamlConfig(Properties p) throws IOException {
		
		String propertiesName = p.getProperty(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_CONFIG_NAME);
    	if(propertiesName!=null){
    		propertiesName = propertiesName.trim();
    	}
    	else{
    		throw new IOException("SAML Config Builder: property ["+SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_CONFIG_NAME+"] not found");
    	}
		
		if(SAML_CACHE_CONFIG.containsKey(propertiesName)){
			return SAML_CACHE_CONFIG.get(propertiesName);
		}
		try{
			SAMLBuilderConfig config = new SAMLBuilderConfig(p);
			if(config.cached){
				addSamlConfig(propertiesName,config);
			}
			return config;
		}catch(Exception e){
			throw new IOException("Properties config ["+propertiesName+"]: "+e.getMessage(),e);
		}
	}	
	private static boolean isTrue(Properties p,String name, boolean defaultValue) throws IOException{
		String tmp = p.getProperty(name);
		if(tmp!=null){
			try{
				return Boolean.parseBoolean(tmp.trim());
			}catch(Exception e){
				throw new IOException("SAML Config Builder: boolean property ["+name+"] with wrong format: "+e.getMessage());
			}
		}
		return defaultValue;
	}
	private static Integer getIntProperty(Properties p,String name, boolean required) throws IOException{
		String tmp = getProperty(p, name, required);
		if(tmp!=null){
			try{
				return Integer.parseInt(tmp);
			}catch(Exception e){
				throw new IOException("SAML Config Builder: integer property ["+name+"] with wrong format: "+e.getMessage());
			}
		}
		return null;
	}
	private static String getProperty(Properties p,String name, boolean required) throws IOException{
		String tmp = p.getProperty(name);
		if(tmp!=null){
			return tmp.trim();
		}
		else{
			if(required){
				throw new IOException("SAML Config Builder: property ["+name+"] not found");
			}
			else{
				return null;
			}
		}
	}
	
	
	
	// ---- INSTANCE -----
	
	private Properties p;	
	private boolean cached = false;
	
	// Version
	private Version version = null;

	// Issuer
    private String issuerValue;
	private String issuerQualifier;
    private String issuerFormat;
	
	// Signature
    private boolean signAssertion = false;
    private Crypto signAssertionCrypto = null;
	private String signAssertionCryptoProp = null;
    private String signAssertionIssuerKeyPassword = null;
    private String signAssertionIssuerKeyName = null;
    private boolean signAssertionSendKeyValue = false;
    private String signAssertionSignatureAlgorithm;
	private String signAssertionSignatureDigestAlgorithm;
    private String signAssertionCanonicalizationAlgorithm;

	// Subject
	private boolean subjectEnabled = true;
	private String subjectNameIDValue;
	private String subjectNameIDQualifier;
	private String subjectNameIDFormat = NameIDType.UNSPECIFIED;
	private String subjectConfirmationMethod = null;
	private int subjectConfirmationDataNotBefore = 0;
	private int subjectConfirmationDataNotOnOrAfter = 1 * 60; // 1 ora
	private String subjectConfirmationDataAddress;
	private String subjectConfirmationDataInResponseTo;
	private String subjectConfirmationDataRecipient;
	private Crypto subjectConfirmationMethod_holderOfKey_crypto = null;
	private String subjectConfirmationMethod_holderOfKey_cryptoProperties = null;
	private String subjectConfirmationMethod_holderOfKey_cryptoCertificateAlias = null;
	
	// Conditions
	private boolean conditionsEnabled = true;
	private int conditionsDataNotBefore = 0;
	private int conditionsDataNotOnOrAfter = 1 * 60; // 1 ora
	private String conditionsAudienceURI = null;
	
	// Authn
	private boolean authnStatementEnabled = true;
	private int authnStatementDataInstant = 0;
	private int authnStatementDataNotOnOrAfter = 1 * 60; // 1 ora
	private String authnStatementClassRef;
	
	// Attribute
	private List<SAMLBuilderConfigAttribute> attributes = new ArrayList<SAMLBuilderConfigAttribute>();
	
	public SAMLBuilderConfig(Properties p) throws IOException{
		this.p = p;
		this.cached = isTrue(p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_CACHE, false);

		// Version
		boolean saml2 = false;
		String version = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_VERSION, true);
		if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_VERSION_10.equals(version)){
			this.version = Version.SAML_10;
		}
		else 
		if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_VERSION_11.equals(version)){
			this.version = Version.SAML_11;
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_VERSION_20.equals(version)){
			this.version = Version.SAML_20;
			saml2 = true;
		}
		else{
			throw new IOException("SAML Config Builder: property ["+SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_VERSION+"] not supported");
		}
		
		// Issuer
		this.issuerValue = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ISSUER_VALUE, true);
		this.issuerQualifier = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ISSUER_QUALIFIER, false);
		
		String tmp = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ISSUER_FORMAT, false);
		if(tmp!=null){
			this.issuerFormat = getNameIDFormat(tmp);
		}
		

		// Signature	
		this.signAssertion = isTrue(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION, false);
		if(this.signAssertion){
			this.signAssertionCryptoProp = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_CRYPTO_PROP_FILE, true);
			this.signAssertionIssuerKeyName = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_KEY_NAME, true);
			this.signAssertionIssuerKeyPassword = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_KEY_PASSWORD, true);
			this.signAssertionSendKeyValue = isTrue(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_SEND_KEY_VALUE, false);
			this.signAssertionSignatureAlgorithm = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_SIGNATURE_ALGORITHM, false);
			this.signAssertionSignatureDigestAlgorithm = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_SIGNATURE_DIGEST_ALGORITHM, false);
			this.signAssertionCanonicalizationAlgorithm = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_SIGNATURE_CANONICALIZATION_ALGORITHM, false);
		}
		
		// Subject
		//this.subjectEnabled = isTrue(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_ENABLED, true); E' OBBLIGATORIO IL SUBJECT, SENNO VA IN NULL POINTER OPENSAML
		this.subjectNameIDValue = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_VALUE, true);
		this.subjectNameIDQualifier = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_QUALIFIER, false);
		
		tmp = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT, false);
		if(tmp!=null){
			this.subjectNameIDFormat = getNameIDFormat(tmp);
		}
		
		tmp = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD, true);
		this.subjectConfirmationMethod = getSubjectConfirmationMethod(tmp, saml2);
		boolean holderOfKey = this.isHolderOfKeySubjectConfirmationMethod(this.subjectConfirmationMethod);
		
		Integer tmpInt = getIntProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_DATA_NOT_BEFORE, false);
		if(tmpInt!=null){
			this.subjectConfirmationDataNotBefore = tmpInt;
		}
		tmpInt = getIntProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_DATA_NOT_ON_OR_AFTER, false);
		if(tmpInt!=null){
			this.subjectConfirmationDataNotOnOrAfter = tmpInt;
		}
		
		this.subjectConfirmationDataAddress = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_DATA_ADDRESS, false);
		this.subjectConfirmationDataInResponseTo = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_DATA_IN_RESPONSE_TO, false);
		this.subjectConfirmationDataRecipient = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_DATA_RECIPIENT, false);
		
		if(holderOfKey){
			this.subjectConfirmationMethod_holderOfKey_cryptoProperties = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_CRYPTO_PROPERTIES, true);
			this.subjectConfirmationMethod_holderOfKey_cryptoCertificateAlias = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_CRYPTO_ALIAS, true);
		}
		
		
		// Conditions
		
		//this.conditionsEnabled = isTrue(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_CONDITIONS_ENABLED, true); //VENGONO GENERATE COMUNQUE
		tmpInt = getIntProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_CONDITIONS_DATA_NOT_BEFORE, false);
		if(tmpInt!=null){
			this.conditionsDataNotBefore = tmpInt;
		}
		tmpInt = getIntProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_CONDITIONS_DATA_NOT_ON_OR_AFTER, false);
		if(tmpInt!=null){
			this.conditionsDataNotOnOrAfter = tmpInt;
		}
		this.conditionsAudienceURI = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_CONDITIONS_AUDIENCE_URI, false);
		
		
		// Authn
		
		this.authnStatementEnabled = isTrue(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_STATEMENT_ENABLED, true);
		tmpInt = getIntProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_STATEMENT_DATA_INSTANT, false);
		if(tmpInt!=null){
			this.authnStatementDataInstant = tmpInt; // se si vuole andare indietro deve essere fornito un valore negativo nella proprietà
		}
		tmpInt = getIntProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_STATEMENT_DATA_NOT_ON_OR_AFTER, false);
		if(tmpInt!=null){
			this.authnStatementDataNotOnOrAfter = tmpInt;
		}
		tmp = getProperty(this.p, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN, this.authnStatementEnabled);
		if(tmp!=null){
			this.authnStatementClassRef = getAuthStatementMethod(tmp, saml2);
		}
			
		
		// Attribute
		
		Properties pAttribute = null;
		try{
			pAttribute = Utilities.readProperties(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX, this.p);
		}catch(Exception e){
			throw new IOException(e.getMessage(),e);
		}
		if(pAttribute!=null && p.size()>0){
			List<String> attrNames = new ArrayList<String>();
			Enumeration<?> enAttributes = pAttribute.keys();
			if(enAttributes!=null){
				while (enAttributes.hasMoreElements()) {
					Object objectName = (Object) enAttributes.nextElement();
					if(objectName instanceof String){
						String key = (String) objectName;
						if(key.endsWith(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_VALUE)){
							String attrName = key.substring(0, key.indexOf(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_VALUE));
							attrNames.add(attrName);
						}
					}
				}
			}
			for (String attrName : attrNames) {
				SAMLBuilderConfigAttribute attr = new SAMLBuilderConfigAttribute(attrName);
				
				String qualifiedName = attrName + SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_QUALIFIED_NAME;
				String simpleName = attrName + SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_SIMPLE_NAME;
				String qualifiedNameTmp = getProperty(pAttribute, qualifiedName, !saml2); // sono obbligatori entrambi solamente per saml 1.1
				String simpleNameTmp = getProperty(pAttribute, simpleName, !saml2); // sono obbligatori entrambi solamente per saml 1.1
				if(qualifiedNameTmp==null && simpleNameTmp==null){
					throw new IOException("SAML Config Builder: attribute ["+attrName+"] requires at least one of the following properties: "+
							qualifiedName+", "+simpleName);
				}
				attr.setQualifiedName(qualifiedNameTmp);
				attr.setSimpleName(simpleNameTmp);
				
				String format = attrName + SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME;
				if(saml2){
					String formatTmp = getProperty(pAttribute, format, true);
					attr.setFormatName(this.getAttributeFormat(formatTmp));
				}
				else{
					String formatTmp = getProperty(pAttribute, format, false);
					if(formatTmp!=null){
						throw new IOException("SAML Config Builder: name format unsupported in SAML 1.1; found in attribute ["+attrName+"]"); 
					}
				}
				
				String separator = attrName + SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_VALUE_SEPARATOR;
				String separatorTmp = getProperty(pAttribute, separator, false);
				if(separatorTmp==null || "".equals(separatorTmp)){
					separatorTmp = ","; // default
				}
				
				String values = attrName + SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_VALUE;
				String valuesTmp = getProperty(pAttribute, values, true);
				String [] splitValues = valuesTmp.split(separatorTmp);
				if(splitValues==null || splitValues.length<=0){
					throw new IOException("SAML Config Builder: values not found in attribute ["+attrName+"] using separator ["+valuesTmp+"]"); 
				}
				for (int i = 0; i < splitValues.length; i++) {
					attr.addValue(splitValues[i].trim());
				}
				
				this.attributes.add(attr);
			}
		}
	}
	
	
	private String getNameIDFormat(String tmpParam){
		String tmp = tmpParam.trim();
		if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_UNSPECIFIED.equals(tmp)){
			tmp = NameIDType.UNSPECIFIED;
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_EMAIL.equals(tmp)){
			tmp = NameIDType.EMAIL;
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_X509_SUBJECT.equals(tmp)){
			tmp = NameIDType.X509_SUBJECT;
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_WIN_DOMAIN_QUALIFIED.equals(tmp)){
			tmp = NameIDType.WIN_DOMAIN_QUALIFIED;
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_KERBEROS.equals(tmp)){
			tmp = NameIDType.KERBEROS;
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_ENTITY.equals(tmp)){
			tmp = NameIDType.ENTITY;
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_PERSISTENT.equals(tmp)){
			tmp = NameIDType.PERSISTENT;
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_TRANSIENT.equals(tmp)){
			tmp = NameIDType.TRANSIENT;
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_ENCRYPTED.equals(tmp)){
			tmp = NameIDType.ENCRYPTED;
		}
		else{
			// lascio il valore impostato dall'utente nel file di proprietà. Deve inserire un formato valido
			// Cosi' supporto anche eventuali formati futuri.
		}
		return tmp;
	}
	
	private String getSubjectConfirmationMethod(String tmpParam, boolean saml2){
		String tmp = tmpParam.trim();
		if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_ARTIFACT.equals(tmp) && !saml2){
			tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_ARTIFACT_SAML_10;
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_IDENTITY.equals(tmp) && !saml2){
			tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_IDENTITY_SAML_10;
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_BEARER.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_BEARER_SAML_20;
			}else{
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_BEARER_SAML_10;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_HOLDER_OF_KEY.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_SAML_20;
			}else{
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_SAML_10;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_SENDER_VOUCHES.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_SENDER_VOUCHES_SAML_20;
			}else{
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_SENDER_VOUCHES_SAML_10;
			}
		}
		else{
			// lascio il valore impostato dall'utente nel file di proprietà. Deve inserire un formato valido
			// Cosi' supporto anche eventuali formati futuri.
		}
		return tmp;
	}
	
	private boolean isHolderOfKeySubjectConfirmationMethod(String confirmationMethod){
		
		return SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_SAML_20.equals(confirmationMethod)
				||
				SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_SAML_10.equals(confirmationMethod);
		
	}
	
	private String getAuthStatementMethod(String tmpParam, boolean saml2){
		String tmp = tmpParam.trim();
		if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_UNSPECIFIED.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_UNSPECIFIED_SAML20;
			}else{
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_UNSPECIFIED_SAML10;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_PASSWORD.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_PASSWORD_SAML20;
			}else{
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_PASSWORD_SAML10;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_KERBEROS.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_KERBEROS_SAML20;
			}else{
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_KERBEROS_SAML10;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_TLS.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_TLS_SAML20;
			}else{
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_TLS_SAML10;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_X509.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_X509_SAML20;
			}else{
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_X509_SAML10;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_PGP.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_PGP_SAML20;
			}else{
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_PGP_SAML10;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_SRP.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_SRP_SAML20;
			}else{
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_SRP_SAML10;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_SPKI.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_SPKI_SAML20;
			}else{
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_SPKI_SAML10;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_DSIG.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_DSIG_SAML20;
			}else{
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_DSIG_SAML10;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_HARDWARE.equals(tmp)){
			if(!saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_HARDWARE_SAML10;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_XKMS.equals(tmp)){
			if(!saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_XKMS_SAML10;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_INTERNET_PROTOCOL.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_INTERNET_PROTOCOL_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_INTERNET_PROTOCOL_PASSWORD.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_INTERNET_PROTOCOL_PASSWORD_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_MOBILE_ONE_FACTOR_UNREGISTERED.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_MOBILE_ONE_FACTOR_UNREGISTERED_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_MOBILE_TWO_FACTOR_UNREGISTERED.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_MOBILE_TWO_FACTOR_UNREGISTERED_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_MOBILE_ONE_FACTOR_CONTRACT.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_MOBILE_ONE_FACTOR_CONTRACT_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_MOBILE_TWO_FACTOR_CONTRACT.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_MOBILE_TWO_FACTOR_CONTRACT_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_PASSWORD_PROTECTED_TRANSPORT.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_PASSWORD_PROTECTED_TRANSPORT_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_PREVIOUS_SESSION.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_PREVIOUS_SESSION_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_SMARTCARD.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_SMARTCARD_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_SMARTCARD_PKI.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_SMARTCARD_PKI_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_SOFTWARE_PKI.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_SOFTWARE_PKI_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_TELEPHONY.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_TELEPHONY_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_NOMAD_TELEPHONY.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_NOMAD_TELEPHONY_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_PERSONAL_TELEPHONY.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_PERSONAL_TELEPHONY_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_AUTHENTICATED_TELEPHONY.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_AUTHENTICATED_TELEPHONY_SAML20;
			}
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_TIME_SYNC.equals(tmp)){
			if(saml2){
				tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_TIME_SYNC_SAML20;
			}
		}
		else{
			// lascio il valore impostato dall'utente nel file di proprietà. Deve inserire un formato valido
			// Cosi' supporto anche eventuali formati futuri.
		}
		return tmp;
	}
	
	private String getAttributeFormat(String tmpParam){
		String tmp = tmpParam.trim();
		if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_VALUE_UNSPECIFIED.equals(tmp)){
			tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_UNSPECIFIED_SAML20;
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_VALUE_URI.equals(tmp)){
			tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_URI_SAML20;
		}
		else if(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_VALUE_BASIC.equals(tmp)){
			tmp = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_BASIC_SAML20;
		}
		else{
			// lascio il valore impostato dall'utente nel file di proprietà. Deve inserire un formato valido
			// Cosi' supporto anche eventuali formati futuri.
		}
		return tmp;
	}
	

		
	
	// Version
	
	public Version getVersion() {
		return this.version;
	}
	
	// Issuer

    public String getIssuerValue() {
		return this.issuerValue;
	}
	public String getIssuerQualifier() {
		return this.issuerQualifier;
	}
	public String getIssuerFormat() {
		return this.issuerFormat;
	}

	// Signature

    public boolean isSignAssertion() {
		return this.signAssertion;
	}
	public Crypto getSignAssertionCrypto() throws WSSecurityException {
		if(this.signAssertionCrypto==null){
			initSignAssertionCrypto();
		}
		return this.signAssertionCrypto;
	}
	private synchronized void initSignAssertionCrypto() throws WSSecurityException{
		if(this.signAssertionCrypto==null){
			this.signAssertionCrypto = 
				CryptoFactory.getInstance(this.signAssertionCryptoProp);
		}
	}
	public String getSignAssertionCryptoProp() {
		return this.signAssertionCryptoProp;
	}
	public String getSignAssertionIssuerKeyPassword() {
		return this.signAssertionIssuerKeyPassword;
	}
	public String getSignAssertionIssuerKeyName() {
		return this.signAssertionIssuerKeyName;
	}
	public boolean isSignAssertionSendKeyValue() {
		return this.signAssertionSendKeyValue;
	}
    public String getSignAssertionSignatureAlgorithm() {
		return this.signAssertionSignatureAlgorithm;
	}
	public String getSignAssertionSignatureDigestAlgorithm() {
		return this.signAssertionSignatureDigestAlgorithm;
	}
	public String getSignAssertionCanonicalizationAlgorithm() {
		return this.signAssertionCanonicalizationAlgorithm;
	}
	
	// Subject
	
	public boolean isSubjectEnabled() {
		return this.subjectEnabled;
	}
	public String getSubjectNameIDValue() {
		return this.subjectNameIDValue;
	}
	public String getSubjectNameIDQualifier() {
		return this.subjectNameIDQualifier;
	}
	public String getSubjectNameIDFormat() {
		return this.subjectNameIDFormat;
	}
	public String getSubjectConfirmationMethod() {
		return this.subjectConfirmationMethod;
	}
	public int getSubjectConfirmationDataNotBefore() {
		return this.subjectConfirmationDataNotBefore;
	}
	public int getSubjectConfirmationDataNotOnOrAfter() {
		return this.subjectConfirmationDataNotOnOrAfter;
	}
	public String getSubjectConfirmationDataAddress() {
		return this.subjectConfirmationDataAddress;
	}
	public String getSubjectConfirmationDataInResponseTo() {
		return this.subjectConfirmationDataInResponseTo;
	}
	public String getSubjectConfirmationDataRecipient() {
		return this.subjectConfirmationDataRecipient;
	}
	public Crypto getSubjectConfirmationMethod_holderOfKey_crypto() throws WSSecurityException {
		if(this.subjectConfirmationMethod_holderOfKey_crypto==null){
			initSubjectConfirmationMethod_holderOfKey_crypto();
		}
		return this.subjectConfirmationMethod_holderOfKey_crypto;
	}
	private synchronized void initSubjectConfirmationMethod_holderOfKey_crypto() throws WSSecurityException{
		if(this.subjectConfirmationMethod_holderOfKey_crypto==null){
			this.subjectConfirmationMethod_holderOfKey_crypto = 
				CryptoFactory.getInstance(this.subjectConfirmationMethod_holderOfKey_cryptoProperties);
		}
	}
	public String getSubjectConfirmationMethod_holderOfKey_cryptoProperties() {
		return this.subjectConfirmationMethod_holderOfKey_cryptoProperties;
	}
	public String getSubjectConfirmationMethod_holderOfKey_cryptoCertificateAlias() {
		return this.subjectConfirmationMethod_holderOfKey_cryptoCertificateAlias;
	}
	
	// Conditions
	
	public boolean isConditionsEnabled() {
		return this.conditionsEnabled;
	}
	public int getConditionsDataNotBefore() {
		return this.conditionsDataNotBefore;
	}
	public int getConditionsDataNotOnOrAfter() {
		return this.conditionsDataNotOnOrAfter;
	}
	public String getConditionsAudienceURI() {
		return this.conditionsAudienceURI;
	}
	
	// Authn
	
	public boolean isAuthnStatementEnabled() {
		return this.authnStatementEnabled;
	}
	public int getAuthnStatementDataInstant() {
		return this.authnStatementDataInstant;
	}
	public int getAuthnStatementDataNotOnOrAfter() {
		return this.authnStatementDataNotOnOrAfter;
	}
	public String getAuthnStatementClassRef() {
		return this.authnStatementClassRef;
	}
	
	// Attribute
	
	public List<SAMLBuilderConfigAttribute> getAttributes() {
		return this.attributes;
	}
}
