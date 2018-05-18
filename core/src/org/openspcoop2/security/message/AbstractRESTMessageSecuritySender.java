/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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


package org.openspcoop2.security.message;

import java.util.Hashtable;
import java.util.Properties;

import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.constants.SecurityConstants;

/**
 * AbstractRESTMessageSecuritySender
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 13942 $, $Date: 2018-05-14 08:38:29 +0200 (Mon, 14 May 2018) $
 */
public abstract class AbstractRESTMessageSecuritySender implements IMessageSecuritySender{

	
	
	
	// *** DETACHED UTILS ***
	
	protected void setDetachedSignatureInMessage(Hashtable<String,Object> map, OpenSPCoop2RestMessage<?> restMessage, String descriptionEngine, String detachedSignature) throws SecurityException {
		
		String signatureDetachedHeader = null;
		String signatureDetachedPropertyUrl = null;
		
		String mode = (String) map.get(SecurityConstants.SIGNATURE_MODE);
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
					if(restMessage.getTransportRequestContext().getParametersFormBased()==null) {
						restMessage.getTransportRequestContext().setParametersFormBased(new Properties());
					}
					restMessage.getTransportRequestContext().removeParameterFormBased(signatureDetachedPropertyUrl); // sovrascrivo
					restMessage.getTransportRequestContext().getParametersFormBased().put(signatureDetachedPropertyUrl, detachedSignature);
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
				if(restMessage.getTransportRequestContext().getParametersTrasporto()==null) {
					restMessage.getTransportRequestContext().setParametersTrasporto(new Properties());
				}
				restMessage.getTransportRequestContext().removeParameterTrasporto(signatureDetachedHeader); // sovrascrivo
				restMessage.getTransportRequestContext().getParametersTrasporto().put(signatureDetachedHeader, detachedSignature);
			}
			else {
				if(restMessage.getTransportResponseContext()==null) {
					throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") property url '"+signatureDetachedPropertyUrl+"'; transporto context undefined");
				}
				if(restMessage.getTransportResponseContext().getParametersTrasporto()==null) {
					restMessage.getTransportResponseContext().setParametersTrasporto(new Properties());
				}
				restMessage.getTransportResponseContext().removeParameterTrasporto(signatureDetachedHeader); // sovrascrivo
				restMessage.getTransportResponseContext().getParametersTrasporto().put(signatureDetachedHeader, detachedSignature);
			}
			if(detachedSignature==null) {
				throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") header '"+signatureDetachedHeader+"' not found");
			}
		}
	}
	
}
