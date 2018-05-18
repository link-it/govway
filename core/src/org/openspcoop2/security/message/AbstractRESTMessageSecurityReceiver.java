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
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.constants.SecurityConstants;

/**
 * AbstractRESTMessageSecurityReceiver
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 13942 $, $Date: 2018-05-14 08:38:29 +0200 (Mon, 14 May 2018) $
 */
public abstract class AbstractRESTMessageSecurityReceiver implements IMessageSecurityReceiver{

	@Override
	public boolean checkExistsWSSecurityHeader() {
		return false;
	}

	@Override
	public List<Reference> getDirtyElements(MessageSecurityContext messageSecurityContext,
			OpenSPCoop2SoapMessage message) throws SecurityException {
		return null;
	}

	@Override
	public Map<QName, QName> checkEncryptSignatureParts(MessageSecurityContext messageSecurityContext,
			List<Reference> elementsToClean, OpenSPCoop2SoapMessage message, List<SubErrorCodeSecurity> codiciErrore)
			throws SecurityException {
		return null;
	}

	@Override
	public void checkEncryptionPartElements(Map<QName, QName> notResolved, OpenSPCoop2SoapMessage message,
			List<SubErrorCodeSecurity> erroriRilevati) throws SecurityException {
		
	}

	@Override
	public void cleanDirtyElements(MessageSecurityContext messageSecurityContext, OpenSPCoop2SoapMessage message,
			List<Reference> elementsToClean, boolean detachHeaderWSSecurity, boolean removeAllIdRef)
			throws SecurityException {
		
	}

	
	
	
	// *** DETACHED UTILS ***
	
	private String signatureDetachedHeader = null;
	private String signatureDetachedPropertyUrl = null;
	
	protected String readDetachedSignatureFromMessage(Hashtable<String,Object> map, OpenSPCoop2RestMessage<?> restMessage, String descriptionEngine) throws SecurityException {
		String detachedSignature = null;
		String mode = (String) map.get(SecurityConstants.SIGNATURE_MODE);
		this.signatureDetachedHeader = (String) map.get(SecurityConstants.SIGNATURE_DETACHED_HEADER);
		if(this.signatureDetachedHeader==null || "".equals(this.signatureDetachedHeader.trim())){
			this.signatureDetachedHeader=null; // normalizzo
			if(MessageRole.REQUEST.equals(restMessage.getMessageRole())) {
				this.signatureDetachedPropertyUrl = (String) map.get(SecurityConstants.SIGNATURE_DETACHED_PROPERTY_URL);
				if(this.signatureDetachedPropertyUrl==null || "".equals(this.signatureDetachedPropertyUrl.trim())){
					this.signatureDetachedPropertyUrl=null; // normalizzo
					throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") require '"+SecurityConstants.SIGNATURE_DETACHED_HEADER+"'/'"+SecurityConstants.SIGNATURE_DETACHED_PROPERTY_URL+"' property");
				}
				else {
					if(restMessage.getTransportRequestContext()==null || restMessage.getTransportRequestContext().getParametersFormBased()==null ||
							restMessage.getTransportRequestContext().getParametersFormBased().size()<=0) {
						throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") property url '"+this.signatureDetachedPropertyUrl+"' not found (properties url not exists)");
					}
					detachedSignature = restMessage.getTransportRequestContext().getParameterFormBased(this.signatureDetachedPropertyUrl);
					if(detachedSignature==null) {
						throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") property url '"+this.signatureDetachedPropertyUrl+"' not found");
					}
				}
			}
			else {
				throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") require '"+SecurityConstants.SIGNATURE_DETACHED_HEADER+"' property");
			}
		}
		else {
			if(MessageRole.REQUEST.equals(restMessage.getMessageRole())) {
				if(restMessage.getTransportRequestContext()==null || restMessage.getTransportRequestContext().getParametersTrasporto()==null ||
						restMessage.getTransportRequestContext().getParametersTrasporto().size()<=0) {
					throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") header '"+this.signatureDetachedHeader+"' not found (header empty)");
				}
				detachedSignature = restMessage.getTransportRequestContext().getParameterTrasporto(this.signatureDetachedHeader);
			}
			else {
				if(restMessage.getTransportResponseContext()==null || restMessage.getTransportResponseContext().getParametersTrasporto()==null ||
						restMessage.getTransportResponseContext().getParametersTrasporto().size()<=0) {
					throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") header '"+this.signatureDetachedHeader+"' not found (header empty)");
				}
				detachedSignature = restMessage.getTransportResponseContext().getParameterTrasporto(this.signatureDetachedHeader);
			}
			if(detachedSignature==null) {
				throw new SecurityException(descriptionEngine+" (mode:"+mode+" message-role:"+restMessage.getMessageRole()+") header '"+this.signatureDetachedHeader+"' not found");
			}
		}
		return detachedSignature;
	}
	
	protected void deleteDetachedSignatureFromMessage(OpenSPCoop2RestMessage<?> restMessage, String descriptionEngine) throws SecurityException {
		if(this.signatureDetachedHeader!=null) {
			if(MessageRole.REQUEST.equals(restMessage.getMessageRole())) {
				if(restMessage.getTransportRequestContext()==null || restMessage.getTransportRequestContext().getParametersTrasporto()==null ||
						restMessage.getTransportRequestContext().getParametersTrasporto().size()<=0) {
					return;
				}
				restMessage.getTransportRequestContext().removeParameterTrasporto(this.signatureDetachedHeader);
			}
			else {
				if(restMessage.getTransportResponseContext()==null || restMessage.getTransportResponseContext().getParametersTrasporto()==null ||
						restMessage.getTransportResponseContext().getParametersTrasporto().size()<=0) {
					return;
				}
				restMessage.getTransportResponseContext().removeParameterTrasporto(this.signatureDetachedHeader);
			}
		}
		else if(this.signatureDetachedPropertyUrl!=null) {
			if(restMessage.getTransportRequestContext()==null || restMessage.getTransportRequestContext().getParametersFormBased()==null ||
					restMessage.getTransportRequestContext().getParametersFormBased().size()<=0) {
				return;
			}
			restMessage.getTransportRequestContext().removeParameterFormBased(this.signatureDetachedPropertyUrl);
		}
		else {
			throw new SecurityException(descriptionEngine+"; this method required preProcess detached signature");			
		}
	}
}
