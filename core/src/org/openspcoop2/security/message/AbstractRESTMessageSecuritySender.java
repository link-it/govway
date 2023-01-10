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


package org.openspcoop2.security.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.io.Base64Utilities;

/**
 * AbstractRESTMessageSecuritySender
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractRESTMessageSecuritySender implements IMessageSecuritySender{

	
	
	
	// *** DETACHED UTILS ***
	
	protected void setDetachedSignatureInMessage(Map<String,Object> map, OpenSPCoop2RestMessage<?> restMessage, String descriptionEngine, String detachedSignatureParam) throws SecurityException {
		
		String signatureDetachedHeader = null;
		String signatureDetachedPropertyUrl = null;
		
		String mode = (String) map.get(SecurityConstants.SIGNATURE_MODE);
		
		boolean base64Detached = SecurityConstants.SIGNATURE_DETACHED_BASE64_DEFAULT;
		String tmpBase64 = (String) map.get(SecurityConstants.SIGNATURE_DETACHED_BASE64);
		if(tmpBase64!=null && !"".equals(tmpBase64)) {
			try {
				base64Detached = Boolean.parseBoolean(tmpBase64);
			}catch(Exception e) {
				throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") property '"+SecurityConstants.SIGNATURE_DETACHED_BASE64+
						"' with wrong value (expected "+SecurityConstants.SIGNATURE_DETACHED_BASE64_TRUE+"/"+SecurityConstants.SIGNATURE_DETACHED_BASE64_FALSE+"): "+e.getMessage(),e);
			}
		}
		String detachedSignature = null;
		if(base64Detached) {
			detachedSignature = Base64Utilities.encodeAsString(detachedSignatureParam.getBytes());
		}
		else {
			detachedSignature = detachedSignatureParam;
		}
		
		signatureDetachedHeader = (String) map.get(SecurityConstants.SIGNATURE_DETACHED_HEADER);
		if(signatureDetachedHeader==null || "".equals(signatureDetachedHeader.trim())){
			signatureDetachedHeader=null; // normalizzo
			if(MessageRole.REQUEST.equals(restMessage.getMessageRole())) {
				signatureDetachedPropertyUrl = (String) map.get(SecurityConstants.SIGNATURE_DETACHED_PROPERTY_URL);
				if(signatureDetachedPropertyUrl==null || "".equals(signatureDetachedPropertyUrl.trim())){
					signatureDetachedPropertyUrl=null; // normalizzo
					throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") require '"+SecurityConstants.SIGNATURE_DETACHED_HEADER+"'/'"+SecurityConstants.SIGNATURE_DETACHED_PROPERTY_URL+"' property");
				}
				else {
					if(restMessage.getTransportRequestContext()==null) {
						throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") property url '"+signatureDetachedPropertyUrl+"'; transporto context undefined");
					}
					if(restMessage.getTransportRequestContext().getParameters()==null) {
						restMessage.getTransportRequestContext().setParameters(new HashMap<String, List<String>>());
					}
					restMessage.getTransportRequestContext().removeParameter(signatureDetachedPropertyUrl); // sovrascrivo
					//restMessage.getTransportRequestContext().getParametersFormBased().put(signatureDetachedPropertyUrl, detachedSignature);
					restMessage.forceUrlProperty(signatureDetachedPropertyUrl, detachedSignature);
				}
			}
			else {
				throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") require '"+SecurityConstants.SIGNATURE_DETACHED_HEADER+"' property");
			}
		}
		else {
			if(MessageRole.REQUEST.equals(restMessage.getMessageRole())) {
				if(restMessage.getTransportRequestContext()==null) {
					throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") property url '"+signatureDetachedPropertyUrl+"'; transporto context undefined");
				}
				if(restMessage.getTransportRequestContext().getHeaders()==null) {
					restMessage.getTransportRequestContext().setHeaders(new HashMap<String, List<String>>());
				}
				restMessage.getTransportRequestContext().removeHeader(signatureDetachedHeader); // sovrascrivo
				//restMessage.getTransportRequestContext().getParametersTrasporto().put(signatureDetachedHeader, detachedSignature);
				restMessage.forceTransportHeader(signatureDetachedHeader, detachedSignature);
			}
			else {
				if(restMessage.getTransportResponseContext()==null) {
					throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") property url '"+signatureDetachedPropertyUrl+"'; transporto context undefined");
				}
				if(restMessage.getTransportResponseContext().getHeaders()==null) {
					restMessage.getTransportResponseContext().setHeaders(new HashMap<String, List<String>>());
				}
				restMessage.getTransportResponseContext().removeHeader(signatureDetachedHeader); // sovrascrivo
				//restMessage.getTransportResponseContext().getParametersTrasporto().put(signatureDetachedHeader, detachedSignature);
				restMessage.forceTransportHeader(signatureDetachedHeader, detachedSignature);
			}
			if(detachedSignature==null) {
				throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") header '"+signatureDetachedHeader+"' not found");
			}
		}
	}
	
}
