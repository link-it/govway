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

package org.openspcoop2.security.message.saml;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoType;
import org.apache.wss4j.common.saml.SAMLCallback;
import org.apache.wss4j.common.saml.bean.AttributeBean;
import org.apache.wss4j.common.saml.bean.AttributeStatementBean;
import org.apache.wss4j.common.saml.bean.AudienceRestrictionBean;
import org.apache.wss4j.common.saml.bean.AuthenticationStatementBean;
import org.apache.wss4j.common.saml.bean.ConditionsBean;
import org.apache.wss4j.common.saml.bean.KeyInfoBean;
import org.apache.wss4j.common.saml.bean.KeyInfoBean.CERT_IDENTIFIER;
import org.apache.wss4j.common.saml.bean.SubjectBean;
import org.apache.wss4j.common.saml.bean.SubjectConfirmationDataBean;
import org.apache.wss4j.common.saml.bean.SubjectLocalityBean;
import org.apache.wss4j.common.saml.bean.Version;
import org.apache.wss4j.common.saml.builder.SAML1Constants;
import org.apache.wss4j.common.saml.builder.SAML2Constants;

/**
 * SAMLCallbackHandler
 * 	
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SAMLCallbackHandler implements CallbackHandler {


	
	// ---- INSTANCE -----
	
	private SAMLBuilderConfig samlBuilderConfig = null;
	public SAMLCallbackHandler(SAMLBuilderConfig config) throws IOException{
		if(config==null){
			throw new IOException("SAMLBuilderConfig undefined");
		}
		this.samlBuilderConfig = config;
	}
	

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		
		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof SAMLCallback) {
				SAMLCallback callback = (SAMLCallback) callbacks[i];
				
				Instant now = Instant.now();
				
				
				// *** SamlVersion ***
				
				callback.setSamlVersion(this.samlBuilderConfig.getVersion());
				
				
				// *** Issuer ***
				
				callback.setIssuer(this.samlBuilderConfig.getIssuerValue());
				callback.setIssuerQualifier(this.samlBuilderConfig.getIssuerQualifier());
				callback.setIssuerFormat(this.samlBuilderConfig.getIssuerFormat());
				
				
				// *** Signature ***
				
				boolean signAssertion = this.samlBuilderConfig.isSignAssertion();
				callback.setSignAssertion(signAssertion);
				if(signAssertion){
				
					try{
						callback.setIssuerCrypto(this.samlBuilderConfig.getSignAssertionCrypto());
					}catch(Exception e){
						throw new IOException(e.getMessage(),e);
					}
					callback.setIssuerKeyName(this.samlBuilderConfig.getSignAssertionIssuerKeyName());
					callback.setIssuerKeyPassword(this.samlBuilderConfig.getSignAssertionIssuerKeyPassword());
					
					if(this.samlBuilderConfig.getSignAssertionSignatureDigestAlgorithm()!=null)
						callback.setSignatureDigestAlgorithm(this.samlBuilderConfig.getSignAssertionSignatureDigestAlgorithm());
					if(this.samlBuilderConfig.getSignAssertionSignatureAlgorithm()!=null)
						callback.setSignatureAlgorithm(this.samlBuilderConfig.getSignAssertionSignatureAlgorithm());
					if(this.samlBuilderConfig.getSignAssertionCanonicalizationAlgorithm()!=null)
						callback.setCanonicalizationAlgorithm(this.samlBuilderConfig.getSignAssertionCanonicalizationAlgorithm());
					
					callback.setSendKeyValue(this.samlBuilderConfig.isSignAssertionSendKeyValue());
					
				}

				
				// *** Subject ***
				
				SubjectBean subjectBean = null;
				if(this.samlBuilderConfig.isSubjectEnabled()){

					subjectBean = new SubjectBean();
					
					subjectBean.setSubjectName(this.samlBuilderConfig.getSubjectNameIDValue());
					subjectBean.setSubjectNameIDFormat(this.samlBuilderConfig.getSubjectNameIDFormat());			
					subjectBean.setSubjectNameQualifier(this.samlBuilderConfig.getSubjectNameIDQualifier());
					
					subjectBean.setSubjectConfirmationMethod(this.samlBuilderConfig.getSubjectConfirmationMethod());
					
					SubjectConfirmationDataBean subjectConfirmationData = new SubjectConfirmationDataBean();
					Instant subjectConfirmationMethod_notBefore = SAMLUtilities.minutesOperator(now,this.samlBuilderConfig.getSubjectConfirmationDataNotBefore());
					Instant subjectConfirmationMethod_notOnOrAfter = SAMLUtilities.minutesOperator(now,this.samlBuilderConfig.getSubjectConfirmationDataNotOnOrAfter());
					subjectConfirmationData.setNotBefore(subjectConfirmationMethod_notBefore);
					subjectConfirmationData.setNotAfter(subjectConfirmationMethod_notOnOrAfter);
					subjectConfirmationData.setAddress(this.samlBuilderConfig.getSubjectConfirmationDataAddress());
					subjectConfirmationData.setInResponseTo(this.samlBuilderConfig.getSubjectConfirmationDataInResponseTo());
					subjectConfirmationData.setRecipient(this.samlBuilderConfig.getSubjectConfirmationDataRecipient());
					subjectBean.setSubjectConfirmationData(subjectConfirmationData);
					
					if (SAML2Constants.CONF_HOLDER_KEY.equals(this.samlBuilderConfig.getSubjectConfirmationMethod())
							|| SAML1Constants.CONF_HOLDER_KEY.equals(this.samlBuilderConfig.getSubjectConfirmationMethod())) {
						try {
							KeyInfoBean keyInfo = createKeyInfo();
							subjectBean.setKeyInfo(keyInfo);
						} catch (Exception ex) {
							ex.printStackTrace(System.out);
							throw new IOException("Problem creating KeyInfo: " +  ex.getMessage());
						}
					}
	
					callback.setSubject(subjectBean);
				}
				
				
				// *** ConditionsStatement ***
				
				if(this.samlBuilderConfig.isConditionsEnabled()){
					
					ConditionsBean conditions = new ConditionsBean();
					
					Instant conditions_notBefore = SAMLUtilities.minutesOperator(now,this.samlBuilderConfig.getConditionsDataNotBefore());
					Instant conditions_notOnOrAfter = SAMLUtilities.minutesOperator(now,this.samlBuilderConfig.getConditionsDataNotOnOrAfter());
					conditions.setNotBefore(conditions_notBefore);
					conditions.setNotAfter(conditions_notOnOrAfter);
					
					if(this.samlBuilderConfig.getConditionsAudienceURI()!=null){
						
						// per adesso viene gestita una unica audience uri
						
						List<AudienceRestrictionBean> lAudience = new ArrayList<AudienceRestrictionBean>();
						
						AudienceRestrictionBean arb = new AudienceRestrictionBean();
						List<String> uri = new ArrayList<>();
						uri.add(this.samlBuilderConfig.getConditionsAudienceURI());
						arb.setAudienceURIs(uri);
						lAudience.add(arb);
												
						conditions.setAudienceRestrictions(lAudience);
					}
					
					callback.setConditions(conditions);
				}
				
			
				// *** AuthenticationStatement ***
				
				AuthenticationStatementBean authBean = null;
				if(this.samlBuilderConfig.isAuthnStatementEnabled()){
					
					authBean = new AuthenticationStatementBean();
					authBean.setSubject(subjectBean); // necessario per saml 1.1
					
					Instant authnStatement_instant = null;
					if(this.samlBuilderConfig.getAuthnStatementDataInstantDate()!=null) {
						authnStatement_instant = this.samlBuilderConfig.getAuthnStatementDataInstantDate().toInstant();
					}
					else {
						authnStatement_instant = SAMLUtilities.minutesOperator(now,this.samlBuilderConfig.getAuthnStatementDataInstant());
					}
					Instant authnStatement_notOnOrAfter = null;
					if(this.samlBuilderConfig.getAuthnStatementDataNotOnOrAfterDate()!=null) {
						authnStatement_notOnOrAfter = this.samlBuilderConfig.getAuthnStatementDataNotOnOrAfterDate().toInstant();
					}
					else {
						authnStatement_notOnOrAfter = SAMLUtilities.minutesOperator(now,this.samlBuilderConfig.getAuthnStatementDataNotOnOrAfter());
					}
					authBean.setAuthenticationInstant(authnStatement_instant);
					authBean.setSessionNotOnOrAfter(authnStatement_notOnOrAfter);
					
					if(this.samlBuilderConfig.getAuthnSubjectLocalityIpAddress()!=null || this.samlBuilderConfig.getAuthnSubjectLocalityDnsAddress()!=null) {
						SubjectLocalityBean subjectLocality = new SubjectLocalityBean();
						subjectLocality.setIpAddress(this.samlBuilderConfig.getAuthnSubjectLocalityIpAddress());
						subjectLocality.setDnsAddress(this.samlBuilderConfig.getAuthnSubjectLocalityDnsAddress());
						authBean.setSubjectLocality(subjectLocality);
					}
					
					authBean.setAuthenticationMethod(this.samlBuilderConfig.getAuthnStatementClassRef());
					
					callback.setAuthenticationStatementData(Collections.singletonList(authBean));
				}
				if(authBean==null && !Version.SAML_20.equals(this.samlBuilderConfig.getVersion()) && subjectBean!=null){
					// Nelle asserzioni saml1x il subject deve viaggiare dentro authn o authz
					authBean = new AuthenticationStatementBean();
					authBean.setSubject(subjectBean); // necessario per saml 1.1
					callback.setAuthenticationStatementData(Collections.singletonList(authBean));
				}

				
				
				// *** AttributeBean ***
				
				List<SAMLBuilderConfigAttribute> listAttributes = this.samlBuilderConfig.getAttributes();
				if(listAttributes!=null && listAttributes.size()>0){
					
					AttributeStatementBean attrBean = new AttributeStatementBean();
					attrBean.setSubject(subjectBean); // necessario per saml 1.1
					
					List<AttributeBean> attributeBeans = new ArrayList<AttributeBean>();
				
					for (SAMLBuilderConfigAttribute configAttribute : listAttributes) {
						AttributeBean attributeBean = new AttributeBean();
						attributeBean.setSimpleName(configAttribute.getSimpleName());
						attributeBean.setQualifiedName(configAttribute.getQualifiedName());
						attributeBean.setNameFormat(configAttribute.getFormatName());
						attributeBean.setAttributeValues(configAttribute.getValues());
						attributeBeans.add(attributeBean);
					}
					
					attrBean.setSamlAttributes(attributeBeans);
					callback.setAttributeStatementData(Collections.singletonList(attrBean));
				}
				
			}
		}
	}

	/*
		SAML Holder of Key

		This mechanism protects messages with a signed SAML assertion (issued by a trusted authority) 
		carrying client public key and authorization information with integrity and confidentiality protection using mutual certificates. 
		The Holder-of-Key (HOK) method establishes the correspondence between a SOAP message and the SAML assertions added to the SOAP message. 
		The attesting entity includes a signature that can be verified with the key information in the confirmation method of the subject statements 
		of the SAML assertion referenced for key info for the signature. 
		For more information about the Holder of Key method, 
		read the SAML Token Profile document at http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.0.pdf.

		Under this scenario, the service does not trust the client directly, but requires the client to send a SAML assertion issued by a particular SAML authority. 
		The client knows the recipient’s public key, but does not share a direct trust relationship with the recipient. 
		The recipient has a trust relationship with the authority that issues the SAML token. 
		The request is signed with the client’s private key and encrypted with the server certificate. 
		The response is signed using the server’s private key and encrypted using the key provided within the HOK SAML assertion.
	*/
	protected KeyInfoBean createKeyInfo() throws Exception {
		Crypto crypto = this.samlBuilderConfig.getSubjectConfirmationMethod_holderOfKey_crypto();
		CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
		cryptoType.setAlias(this.samlBuilderConfig.getSubjectConfirmationMethod_holderOfKey_cryptoCertificateAlias());
		X509Certificate[] certs = crypto.getX509Certificates(cryptoType);

		KeyInfoBean keyInfo = new KeyInfoBean();
		keyInfo.setCertificate(certs[0]);
		keyInfo.setCertIdentifer(CERT_IDENTIFIER.X509_CERT);

		return keyInfo;
	}

}
