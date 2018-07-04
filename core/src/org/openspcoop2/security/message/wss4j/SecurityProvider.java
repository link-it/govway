/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import org.opensaml.saml.saml2.core.NameIDType;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.security.message.constants.EncryptionKeyTransportAlgorithm;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.saml.SAMLBuilderConfigConstants;
import org.openspcoop2.security.message.xml.XMLCostanti;

/**     
 * SecurityProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 14182 $, $Date: 2018-06-23 21:12:23 +0200 (Sat, 23 Jun 2018) $
 */
public class SecurityProvider extends org.openspcoop2.security.message.xml.SecurityProvider  {

	@Override
	public List<String> getValues(String id) throws ProviderException {
		if(SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE.equals(id)) {
			List<String> l = new ArrayList<>();
			l.add(SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE_DIGEST);
			l.add(SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE_TEXT);
			l.add(SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE_NONE);
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
		else {
			return super.getValues(id);
		}
	}
	
	@Override
	public List<String> getLabels(String id) throws ProviderException {
		if(SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE.equals(id)) {
			List<String> l = new ArrayList<>();
			l.add("Password "+SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE_DIGEST.replace("Password", ""));
			l.add("Password "+SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE_TEXT.replace("Password", ""));
			l.add("Password "+SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE_NONE.replace("Password", ""));
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
		else {
			return super.getLabels(id);
		}
	}
	
	
	@Override
	public String getDefault(String id) throws ProviderException {
		
		if(XMLCostanti.ID_ENCRYPT_TRANSPORT_KEY_WRAP_ALGORITHM.equals(id)) {
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
		else {
			return super.getDefault(id);
		}
		
	}

}
