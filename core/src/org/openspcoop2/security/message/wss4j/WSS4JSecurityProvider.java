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


package org.openspcoop2.security.message.wss4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.opensaml.saml.saml2.core.NameIDType;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;
import org.openspcoop2.security.message.constants.EncryptionKeyTransportAlgorithm;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.saml.SAMLBuilderConfigConstants;
import org.openspcoop2.security.message.xml.XMLCostanti;

/**     
 * SecurityProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSS4JSecurityProvider extends org.openspcoop2.security.message.xml.SecurityProvider  {

	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {

		Properties defaultProperties = MultiPropertiesUtilities.getDefaultProperties(mapProperties);
		Properties samlConfig = mapProperties.get(SecurityConstants.SAML_PROF_REF_ID);
		
		boolean envelopedSaml = false;
		if(defaultProperties.containsKey(SecurityConstants.SAML_ENVELOPED_SAML_SIGNATURE_XMLCONFIG_PREFIX_ID)) {
			String tmp = defaultProperties.getProperty(SecurityConstants.SAML_ENVELOPED_SAML_SIGNATURE_XMLCONFIG_PREFIX_ID);
			envelopedSaml = Boolean.parseBoolean(tmp);
		}
		
		boolean holderOfKey = false;
		// receiver
		if(defaultProperties.containsKey(SecurityConstants.SAML_SUBJECT_CONFIRMATION_VALIDATION_METHOD_XMLCONFIG_ID)) {
			String tmp = defaultProperties.getProperty(SecurityConstants.SAML_SUBJECT_CONFIRMATION_VALIDATION_METHOD_XMLCONFIG_ID);
			holderOfKey = SecurityConstants.SAML_SUBJECT_CONFIRMATION_VALIDATION_METHOD_XMLCONFIG_ID_HOLDER_OF_KEY.equals(tmp);
		}
		// sender
		else if(samlConfig!=null &&
			(samlConfig.containsKey(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD)) 
			){
			String tmp = samlConfig.getProperty(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD);
			holderOfKey = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_HOLDER_OF_KEY.equals(tmp);
		}
		
		if(holderOfKey && !envelopedSaml) {
			throw new ProviderValidationException("Subject Confirmation Method 'Holder of Key' require Enveloped SAML Signature");
		}
		
		boolean bearer = false;
		// sender
		if(samlConfig!=null &&
			(samlConfig.containsKey(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD)) 
			){
			String tmp = samlConfig.getProperty(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD);
			bearer = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_BEARER.equals(tmp);
		}
		
		if(bearer && !envelopedSaml) {
			throw new ProviderValidationException("Subject Confirmation Method 'Bearer' require Enveloped SAML Signature");
		}
		
		super.validate(mapProperties);
	}	
	
	@Override
	public List<String> getValues(String id) throws ProviderException {
		if(SecurityConstants.USERNAME_TOKEN_PW_TYPE.equals(id)) {
			List<String> l = new ArrayList<>();
			l.add(SecurityConstants.USERNAME_TOKEN_PW_TYPE_DIGEST);
			l.add(SecurityConstants.USERNAME_TOKEN_PW_TYPE_TEXT);
			l.add(SecurityConstants.USERNAME_TOKEN_PW_TYPE_NONE);
			return l;
		}
		else if(SecurityConstants.USERNAME_TOKEN_PW_MAP_MODE.equals(id)) {
			List<String> l = new ArrayList<>();
			l.add(SecurityConstants.USERNAME_TOKEN_PW_MAP_MODE_SINGLE);
			l.add(SecurityConstants.USERNAME_TOKEN_PW_MAP_MODE_MAP);
			return l;
		}
		else if(SecurityConstants.SAML_ISSUER_FORMAT_XMLCONFIG_ID_2.equals(id) ||
				SecurityConstants.SAML_SUBJECT_FORMAT_XMLCONFIG_ID_1.equals(id) ||
				SecurityConstants.SAML_SUBJECT_FORMAT_XMLCONFIG_ID_2.equals(id)) {
			List<String> l = new ArrayList<>();
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_UNSPECIFIED);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_EMAIL);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_X509_SUBJECT);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_WIN_DOMAIN_QUALIFIED);
			if(SecurityConstants.SAML_ISSUER_FORMAT_XMLCONFIG_ID_2.equals(id) ||
					SecurityConstants.SAML_SUBJECT_FORMAT_XMLCONFIG_ID_2.equals(id)) {
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_KERBEROS); // 2.0
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_ENTITY); // 2.0
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_PERSISTENT); // 2.0
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_TRANSIENT); // 2.0
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_ENCRYPTED); // 2.0
			}
			return l;
		}
		else if(SecurityConstants.SAML_SUBJECT_CONFIRMATION_METHOD_XMLCONFIG_ID_1.equals(id) ||
				SecurityConstants.SAML_SUBJECT_CONFIRMATION_METHOD_XMLCONFIG_ID_2.equals(id) ) {
			List<String> l = new ArrayList<>();
			if(SecurityConstants.SAML_SUBJECT_CONFIRMATION_METHOD_XMLCONFIG_ID_1.equals(id) ) {
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_ARTIFACT);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_IDENTITY);
			}
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_BEARER);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_HOLDER_OF_KEY);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_SENDER_VOUCHES);
			return l;
		}
		else if(SecurityConstants.SAML_AUTHN_CONTEXT_CLASS_REF_XMLCONFIG_ID_1.equals(id) ||
				SecurityConstants.SAML_AUTHN_CONTEXT_CLASS_REF_XMLCONFIG_ID_2.equals(id) ) {
			List<String> l = new ArrayList<>();
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_UNSPECIFIED);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_PASSWORD);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_KERBEROS);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_TLS);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_X509);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_PGP);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_SRP);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_SPKI);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_DSIG);
			if(SecurityConstants.SAML_AUTHN_CONTEXT_CLASS_REF_XMLCONFIG_ID_1.equals(id) ) {
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_HARDWARE);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_XKMS);
			}
			if(SecurityConstants.SAML_AUTHN_CONTEXT_CLASS_REF_XMLCONFIG_ID_2.equals(id) ) {
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_INTERNET_PROTOCOL);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_INTERNET_PROTOCOL_PASSWORD);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_MOBILE_ONE_FACTOR_UNREGISTERED);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_MOBILE_TWO_FACTOR_UNREGISTERED);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_MOBILE_ONE_FACTOR_CONTRACT);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_MOBILE_TWO_FACTOR_CONTRACT);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_PASSWORD_PROTECTED_TRANSPORT);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_PREVIOUS_SESSION);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_SMARTCARD);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_SMARTCARD_PKI);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_SOFTWARE_PKI);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_TELEPHONY);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_NOMAD_TELEPHONY);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_PERSONAL_TELEPHONY);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_AUTHENTICATED_TELEPHONY);
				l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_TIME_SYNC);
			}
			return l;
		}
		else if(id.startsWith(SecurityConstants.SAML_ATTRIBUTE_STATEMENT_FORMAT_XMLCONFIG_PREFIX_ID)) {
			List<String> l = new ArrayList<>();
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_VALUE_UNSPECIFIED);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_VALUE_URI);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_VALUE_BASIC);
			
			return l;
		}
		else {
			return super.getValues(id);
		}
	}
	
	private static final String PASSWORD = "Password";
	private static final String PASSWORD_PREFIX = PASSWORD+" ";
	
	@Override
	public List<String> getLabels(String id) throws ProviderException {
		if(SecurityConstants.USERNAME_TOKEN_PW_TYPE.equals(id)) {
			List<String> l = new ArrayList<>();
			l.add(PASSWORD_PREFIX+SecurityConstants.USERNAME_TOKEN_PW_TYPE_DIGEST.replace(PASSWORD, ""));
			l.add(PASSWORD_PREFIX+SecurityConstants.USERNAME_TOKEN_PW_TYPE_TEXT.replace(PASSWORD, ""));
			l.add(PASSWORD_PREFIX+SecurityConstants.USERNAME_TOKEN_PW_TYPE_NONE.replace(PASSWORD, ""));
			return l;
		}
		else if(SecurityConstants.USERNAME_TOKEN_PW_MAP_MODE.equals(id)) {
			List<String> l = new ArrayList<>();
			l.add("Default");
			l.add("Mappa");
			return l;
		}
		else if(SecurityConstants.SAML_ISSUER_FORMAT_XMLCONFIG_ID_2.equals(id) ||
				SecurityConstants.SAML_SUBJECT_FORMAT_XMLCONFIG_ID_1.equals(id) ||
				SecurityConstants.SAML_SUBJECT_FORMAT_XMLCONFIG_ID_2.equals(id)) {
			List<String> l = new ArrayList<>();
			l.add(NameIDType.UNSPECIFIED);
			l.add(NameIDType.EMAIL);
			l.add(NameIDType.X509_SUBJECT);
			l.add(NameIDType.WIN_DOMAIN_QUALIFIED);
			if(SecurityConstants.SAML_ISSUER_FORMAT_XMLCONFIG_ID_2.equals(id) ||
					SecurityConstants.SAML_SUBJECT_FORMAT_XMLCONFIG_ID_2.equals(id)) {
				l.add(NameIDType.KERBEROS); // 2.0
				l.add(NameIDType.ENTITY); // 2.0
				l.add(NameIDType.PERSISTENT); // 2.0
				l.add(NameIDType.TRANSIENT); // 2.0
				l.add(NameIDType.ENCRYPTED); // 2.0
			}
			return l;
		}
		else if(SecurityConstants.SAML_SUBJECT_CONFIRMATION_METHOD_XMLCONFIG_ID_1.equals(id) ) {
			List<String> l = new ArrayList<>();
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_ARTIFACT_SAML_10);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_IDENTITY_SAML_10);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_BEARER_SAML_10);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_SAML_10);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_SENDER_VOUCHES_SAML_10);
			return l;
		}
		else if(SecurityConstants.SAML_SUBJECT_CONFIRMATION_METHOD_XMLCONFIG_ID_2.equals(id) ) {
			List<String> l = new ArrayList<>();
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_BEARER_SAML_20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_SAML_20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_SENDER_VOUCHES_SAML_20);
			return l;
		}
		else if(SecurityConstants.SAML_AUTHN_CONTEXT_CLASS_REF_XMLCONFIG_ID_1.equals(id)) {
			List<String> l = new ArrayList<>();
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_UNSPECIFIED_SAML10);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_PASSWORD_SAML10);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_KERBEROS_SAML10);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_TLS_SAML10);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_X509_SAML10);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_PGP_SAML10);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_SRP_SAML10);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_SPKI_SAML10);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_DSIG_SAML10);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_HARDWARE_SAML10);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_XKMS_SAML10);
			return l;
		}
		else if(SecurityConstants.SAML_AUTHN_CONTEXT_CLASS_REF_XMLCONFIG_ID_2.equals(id) ) {
			List<String> l = new ArrayList<>();
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_UNSPECIFIED_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_PASSWORD_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_KERBEROS_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_TLS_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_X509_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_PGP_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_SRP_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_SPKI_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_DSIG_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_INTERNET_PROTOCOL_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_INTERNET_PROTOCOL_PASSWORD_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_MOBILE_ONE_FACTOR_UNREGISTERED_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_MOBILE_TWO_FACTOR_UNREGISTERED_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_MOBILE_ONE_FACTOR_CONTRACT_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_MOBILE_TWO_FACTOR_CONTRACT_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_PASSWORD_PROTECTED_TRANSPORT_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_PREVIOUS_SESSION_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_SMARTCARD_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_SMARTCARD_PKI_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_SOFTWARE_PKI_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_TELEPHONY_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_NOMAD_TELEPHONY_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_PERSONAL_TELEPHONY_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_AUTHENTICATED_TELEPHONY_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_TIME_SYNC_SAML20);
			return l;
		}
		else if(id.startsWith(SecurityConstants.SAML_ATTRIBUTE_STATEMENT_FORMAT_XMLCONFIG_PREFIX_ID)) {
			List<String> l = new ArrayList<>();
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_UNSPECIFIED_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_URI_SAML20);
			l.add(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_BASIC_SAML20);
			
			return l;
		}
		else {
			return super.getLabels(id);
		}
	}
	
	
	@Override
	public String getDefault(String id) throws ProviderException {
		
		if(SecurityConstants.USERNAME_TOKEN_PW_MAP_MODE.equals(id)) {
			return SecurityConstants.USERNAME_TOKEN_PW_MAP_MODE_SINGLE;
		}
		else if(XMLCostanti.ID_ENCRYPT_TRANSPORT_KEY_WRAP_ALGORITHM.equals(id)) {
			return EncryptionKeyTransportAlgorithm.RSA_OAEP.getUri();
		}
		else if(SecurityConstants.SAML_ISSUER_FORMAT_XMLCONFIG_ID_2.equals(id) ||
				SecurityConstants.SAML_SUBJECT_FORMAT_XMLCONFIG_ID_1.equals(id) ||
				SecurityConstants.SAML_SUBJECT_FORMAT_XMLCONFIG_ID_2.equals(id)) {
			return SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_UNSPECIFIED;
		}
		else if(SecurityConstants.SAML_SUBJECT_CONFIRMATION_METHOD_XMLCONFIG_ID_1.equals(id) ||
				SecurityConstants.SAML_SUBJECT_CONFIRMATION_METHOD_XMLCONFIG_ID_2.equals(id) ) {
			return SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_SENDER_VOUCHES;
		}
		else if(SecurityConstants.SAML_AUTHN_CONTEXT_CLASS_REF_XMLCONFIG_ID_1.equals(id) ||
				SecurityConstants.SAML_AUTHN_CONTEXT_CLASS_REF_XMLCONFIG_ID_2.equals(id) ) {
			return SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_UNSPECIFIED;
		}
		else if(id.startsWith(SecurityConstants.SAML_ATTRIBUTE_STATEMENT_FORMAT_XMLCONFIG_PREFIX_ID)) {
			return SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_VALUE_UNSPECIFIED;
		}
		else {
			return super.getDefault(id);
		}
		
	}

}
