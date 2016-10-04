/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.util.List;

import org.openspcoop2.message.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.reference.Reference;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.Utilities;

/**
 * WSSUtilities
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSSUtilities {

	public static List<Reference> getDirtyElements(MessageSecurityContext messageSecurityContext,OpenSPCoop2Message message) throws SecurityException{
		
		try{
			
			boolean mustUnderstandValue = false;
			Object mustUnderstand = messageSecurityContext.getIncomingProperties().get(SecurityConstants.MUST_UNDERSTAND);
			if(mustUnderstand!=null){
				mustUnderstandValue = Boolean.parseBoolean((String)mustUnderstand);
			}
			String actor = messageSecurityContext.getActor();
			if("".equals(messageSecurityContext.getActor()))
				actor = null;
			
			return message.getWSSDirtyElements(actor, mustUnderstandValue);
			
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
	
	public static void cleanDirtyElements(MessageSecurityContext messageSecurityContext,OpenSPCoop2Message message, List<Reference> elementsToClean) throws SecurityException{
		try{
			
			boolean mustUnderstandValue = false;
			Object mustUnderstand = messageSecurityContext.getIncomingProperties().get(SecurityConstants.MUST_UNDERSTAND);
			if(mustUnderstand!=null){
				mustUnderstandValue = Boolean.parseBoolean((String)mustUnderstand);
			}
			String actor = messageSecurityContext.getActor();
			if("".equals(messageSecurityContext.getActor()))
				actor = null;
			
			message.cleanWSSDirtyElements(actor, mustUnderstandValue, elementsToClean);
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
}
