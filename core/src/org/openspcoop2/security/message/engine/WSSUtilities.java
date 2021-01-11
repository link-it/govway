/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.security.message.engine;

import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.Utilities;

/**
 * WSSUtilities
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSSUtilities {

	public static List<Reference> getDirtyElements(MessageSecurityContext messageSecurityContext,OpenSPCoop2SoapMessage message) throws SecurityException{
		
		try{
			
			boolean mustUnderstandValue = false;
			Object mustUnderstand = messageSecurityContext.getIncomingProperties().get(SecurityConstants.MUST_UNDERSTAND);
			if(mustUnderstand!=null){
				if(mustUnderstand instanceof String) {
					mustUnderstandValue = Boolean.parseBoolean((String)mustUnderstand);
				}
				else if(mustUnderstand instanceof Boolean) {
					mustUnderstandValue = (Boolean) mustUnderstand;
				}
				else {
					throw new SecurityException("Unexected type '"+mustUnderstand.getClass().getName()+"' for property '"+SecurityConstants.MUST_UNDERSTAND+"'");
				}
			}
			String actor = messageSecurityContext.getActor();
			if("".equals(messageSecurityContext.getActor()))
				actor = null;
			
			List<Reference> references = message.getWSSDirtyElements(actor, mustUnderstandValue);
			messageSecurityContext.setReferences(references);
			return references;
			
		}catch(Exception e){
			SecurityException sec = new SecurityException(e.getMessage(),e);
			if(Utilities.existsInnerMessageException(e, Costanti.FIND_ERROR_SIGNATURE_REFERENCES, true)){
				sec.setCodiceErrore(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA);
			}
			else if(Utilities.existsInnerMessageException(e, Costanti.FIND_ERROR_ENCRYPTED_REFERENCES, true)){
				sec.setCodiceErrore(CodiceErroreCooperazione.SICUREZZA_CIFRATURA_NON_VALIDA);
			}
			throw sec;
		}
	}
	
	public static void cleanDirtyElements(MessageSecurityContext messageSecurityContext,OpenSPCoop2SoapMessage message, List<Reference> elementsToClean,
			boolean detachHeaderWSSecurity, boolean removeAllIdRef) throws SecurityException{
		try{
			
			boolean mustUnderstandValue = false;
			Object mustUnderstand = messageSecurityContext.getIncomingProperties().get(SecurityConstants.MUST_UNDERSTAND);
			if(mustUnderstand!=null){
				if(mustUnderstand instanceof String) {
					mustUnderstandValue = Boolean.parseBoolean((String)mustUnderstand);
				}
				else if(mustUnderstand instanceof Boolean) {
					mustUnderstandValue = (Boolean) mustUnderstand;
				}
				else {
					throw new SecurityException("Unexected type '"+mustUnderstand.getClass().getName()+"' for property '"+SecurityConstants.MUST_UNDERSTAND+"'");
				}
			}
			String actor = messageSecurityContext.getActor();
			if("".equals(messageSecurityContext.getActor()))
				actor = null;
			
			message.cleanWSSDirtyElements(actor, mustUnderstandValue, elementsToClean, detachHeaderWSSecurity, removeAllIdRef);
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	public static boolean isNormalizeToSaajImpl(MessageSecurityContext wssContext){
		Hashtable<?,?> wssProperties = null;
		if(wssContext.isFunctionAsClient())
			wssProperties = wssContext.getOutgoingProperties();
		else
			wssProperties = wssContext.getIncomingProperties();
		if(wssProperties!=null && wssProperties.containsKey(SecurityConstants.NORMALIZE_TO_SAAJ_IMPL)){
			return Boolean.parseBoolean((String)wssProperties.get(SecurityConstants.NORMALIZE_TO_SAAJ_IMPL));
		}
		
		// default viene applicata la normalizzazione solo se siamo client e effettuamo operazioni di signature.
		if(wssProperties!=null && wssProperties.containsKey(SecurityConstants.ACTION)){
			String action = (String) wssProperties.get(SecurityConstants.ACTION);

			// A volte i problemi di signature verification avvengono anche in ricezione. Per questo motivo si è deciso di normalizzare sia lato client che lato server
			if ( action.contains(SecurityConstants.SIGNATURE_ACTION) || 
					action.contains(SecurityConstants.ACTION_SAML_TOKEN_UNSIGNED) || // la decisione di firmare o meno saml è nel file di proprietà
					action.contains(SecurityConstants.ACTION_SAML_TOKEN_SIGNED) ||
					action.contains(SecurityConstants.ACTION_USERNAME_TOKEN_SIGNATURE) ||
					action.contains(SecurityConstants.ACTION_SIGNATURE_DERIVED) ||
					action.contains(SecurityConstants.ACTION_SIGNATURE_WITH_KERBEROS_TOKEN)){
				return true;
			}
			
			if(wssContext.isFunctionAsClient() == false){
				
				if ( action.contains(SecurityConstants.ENCRYPT_ACTION) && wssProperties.containsKey(SecurityConstants.ENCRYPTION_PARTS) ){
					
					String attach = "{"+SecurityConstants.NAMESPACE_ATTACH+"}";
					String attachAll = attach+"{"+SecurityConstants.ATTACHMENT_INDEX_ALL+"}";
					String encryptParts = (String) wssProperties.get(SecurityConstants.ENCRYPTION_PARTS);
					
					if ( encryptParts!=null &&
							encryptParts.contains(attach) &&
							(!encryptParts.contains(attachAll)) ){
						return true;
					}
				}
			}
			
			return false;
		}
		else{
			return false;
		}
	}
}
