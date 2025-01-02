/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.security.message;

import java.util.Map;

import org.openspcoop2.security.message.constants.SecurityConstants;

/**
 * MessageSecurityUtilities
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageSecurityUtilities {
	
	private MessageSecurityUtilities() {}

	public static boolean processSOAPFault(Map<String,Object> messageSecurityProperties){
		// Default disabilitati
		boolean processEncryptSOAPFault = false;
		boolean processSignatureSOAPFault = false;
		boolean processUsernameTokenFault = false;
		boolean processSAMLTokenFault = false;
		
		String propertyEncrypt = (String) messageSecurityProperties.get(SecurityConstants.ENCRYPTION_SOAP_FAULT);
		if(propertyEncrypt!=null &&
			SecurityConstants.TRUE.equalsIgnoreCase(propertyEncrypt)){
			processEncryptSOAPFault = true;
		}
		
		String propertySignature = (String) messageSecurityProperties.get(SecurityConstants.SIGNATURE_SOAP_FAULT);
		if(propertySignature!=null &&
			SecurityConstants.TRUE.equalsIgnoreCase(propertySignature)){
			processSignatureSOAPFault = true;
		}
		
		String propertyUsernameToken = (String) messageSecurityProperties.get(SecurityConstants.USERNAME_TOKEN_SOAP_FAULT);
		if(propertyUsernameToken!=null &&
			SecurityConstants.TRUE.equalsIgnoreCase(propertyUsernameToken)){
			processUsernameTokenFault = true;
		}
		
		String propertySAMLToken = (String) messageSecurityProperties.get(SecurityConstants.SAML_TOKEN_SOAP_FAULT);
		if(propertySAMLToken!=null &&
			SecurityConstants.TRUE.equalsIgnoreCase(propertySAMLToken)){
			processSAMLTokenFault = true;
		}
		
		return processFaultEngine(messageSecurityProperties, 
				processEncryptSOAPFault, processSignatureSOAPFault,
				processUsernameTokenFault, processSAMLTokenFault);
	}
	public static boolean processProblemDetails(Map<String,Object> messageSecurityProperties){
		// Default disabilitati
		boolean processEncryptProblemDetails = false;
		boolean processSignatureProblemDetails = false;
		boolean processUsernameTokenProblemDetails = false;
		boolean processSAMLTokenProblemDetails = false;
		
		String propertyEncrypt = (String) messageSecurityProperties.get(SecurityConstants.ENCRYPTION_PROBLEM_DETAILS);
		if(propertyEncrypt!=null &&
			SecurityConstants.TRUE.equalsIgnoreCase(propertyEncrypt)){
			processEncryptProblemDetails = true;
		}
		
		String propertySignature = (String) messageSecurityProperties.get(SecurityConstants.SIGNATURE_PROBLEM_DETAILS);
		if(propertySignature!=null &&
			SecurityConstants.TRUE.equalsIgnoreCase(propertySignature)){
			processSignatureProblemDetails = true;
		}
		
		String propertyUsernameToken = (String) messageSecurityProperties.get(SecurityConstants.USERNAME_TOKEN_PROBLEM_DETAILS);
		if(propertyUsernameToken!=null &&
			SecurityConstants.TRUE.equalsIgnoreCase(propertyUsernameToken)){
			processUsernameTokenProblemDetails = true;
		}
		
		String propertySAMLToken = (String) messageSecurityProperties.get(SecurityConstants.SAML_TOKEN_PROBLEM_DETAILS);
		if(propertySAMLToken!=null &&
			SecurityConstants.TRUE.equalsIgnoreCase(propertySAMLToken)){
			processSAMLTokenProblemDetails = true;
		}
		
		return processFaultEngine(messageSecurityProperties, 
				processEncryptProblemDetails, processSignatureProblemDetails,
				processUsernameTokenProblemDetails, processSAMLTokenProblemDetails);
	}
	private static boolean processFaultEngine(Map<String,Object> messageSecurityProperties,
			boolean processEncryptFault, boolean processSignatureFault,
			boolean processUsernameTokenFault, boolean processSAMLTokenFault){
				
		String action = (String) messageSecurityProperties.remove(SecurityConstants.ACTION);
		
		String [] splitActions = action.split(" ");
		StringBuilder bfNewActions = new StringBuilder();
		for (int i = 0; i < splitActions.length; i++) {
			
			String a = splitActions[i].trim();
			
			if(SecurityConstants.isActionEncryption(a) ||
					SecurityConstants.isActionDecryption(a)){
				if(processEncryptFault){
					if(bfNewActions.length()>0){
						bfNewActions.append(" ");
					}
					bfNewActions.append(a);
				}
			}
			else if(SecurityConstants.SIGNATURE_ACTION.equals(a)){
				if(processSignatureFault){
					if(bfNewActions.length()>0){
						bfNewActions.append(" ");
					}
					bfNewActions.append(a);
				}
			}
			else if(SecurityConstants.isActionUsernameToken(a) ){
				if(processUsernameTokenFault){
					if(bfNewActions.length()>0){
						bfNewActions.append(" ");
					}
					bfNewActions.append(a);
				}
			}
			else if(SecurityConstants.isActionSAMLToken(a) ){
				if(processSAMLTokenFault){
					if(bfNewActions.length()>0){
						bfNewActions.append(" ");
					}
					bfNewActions.append(a);
				}
			}
			else{
				// altra azione la aggiungo
				if(bfNewActions.length()>0){
					bfNewActions.append(" ");
				}
				bfNewActions.append(a);
			}
		}
		
		
		if(bfNewActions.length()>0){
			String newActions = bfNewActions.toString();
			if(SecurityConstants.TIMESTAMP_ACTION.equals(newActions)){
				// se rimane solo TIMESTAMP disabilitiamo
				return false;
			}else{
				messageSecurityProperties.put(SecurityConstants.ACTION,newActions);
				return true;
			}
		}
		else{
			return false;
		}
	}
	
	
}
