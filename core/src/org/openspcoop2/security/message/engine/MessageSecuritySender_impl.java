/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.IMessageSecuritySender;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityUtilities;
import org.openspcoop2.security.message.constants.SecurityConstants;

/**
 * Classe per la gestione della Sicurezza (role:Sender)
 *
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MessageSecuritySender_impl extends MessageSecuritySender {

	protected MessageSecuritySender_impl(MessageSecurityContext messageSecurityContext) {
	    super(messageSecurityContext);
    }

    @Override
    protected boolean process(OpenSPCoop2Message message) {
		try{ 	
				
			IMessageSecuritySender senderInterface = this.messageSecurityContext.getMessageSecuritySender();
			
			
			// Fix per SOAPFault (quando ci sono le encryptionParts o le signatureParts, la Security fallisce se c'e' un SOAPFault)
			if(message.getSOAPBody().hasFault()){
				
				if(MessageSecurityUtilities.processSOAPFault(this.messageSecurityContext.getOutgoingProperties()) == false){
					return true; // non devo applicare la sicurezza.
				}
				
			}	
			
			String action = (String) this.messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ACTION);
			if(action==null || "".equals(action.trim())){
				return true; // nessuna action: non devo applicare la sicurezza.
			}
			
			// Utilizzo l'engine di sicurezza
			senderInterface.process(this.messageSecurityContext, message);
	    	
		}
		catch(Exception e){
			
			String prefix = "Generatosi errore durante il processamento Message-Security(Sender): ";
			
			this.messageSecurityContext.getLog().error(prefix+e.getMessage(),e);
			
			this.msgErrore =  prefix+e.getMessage();
    	    this.codiceErrore = CodiceErroreCooperazione.SICUREZZA;
			
			if(e instanceof SecurityException){
				SecurityException securityException = (SecurityException) e;
				if(securityException.getMsgErrore()!=null){
					this.msgErrore = prefix+securityException.getMsgErrore();
				}
				if(securityException.getCodiceErrore()!=null){
					this.codiceErrore = securityException.getCodiceErrore();
				}
			} 
			
    	    return false;
		}
		return true;
    }

}





