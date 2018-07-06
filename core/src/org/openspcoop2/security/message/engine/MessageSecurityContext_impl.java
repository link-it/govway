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



package org.openspcoop2.security.message.engine;

import java.util.Hashtable;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContextParameters;

/**
 * MessageSecurityContext_impl
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageSecurityContext_impl extends MessageSecurityContext{
	
	public MessageSecurityContext_impl(MessageSecurityContextParameters messageSecurityContextParameters) {
		super(messageSecurityContextParameters);
	}

	private MessageSecurityReceiver receiver;
	private MessageSecuritySender sender;
	
	/** MessageSecurity Process */
	@Override
	public boolean processIncoming(OpenSPCoop2Message message, Busta busta, Hashtable<String, Object> ctx){
		
		this.receiver = new MessageSecurityReceiver_impl(this);
		
		boolean result = this.receiver.process(message, busta);
		if(!result){
			
			if(ctx!=null) {
				if(MessageRole.REQUEST.equals(message.getMessageRole())) {
					ctx.put(org.openspcoop2.core.constants.Costanti.ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA, "true");
				}
				else {
					ctx.put(org.openspcoop2.core.constants.Costanti.ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA, "true");
				}
			}
				
			this.codiceErrore = this.receiver.getCodiceErrore();
			this.msgErrore = this.receiver.getMsgErrore();
			this.listaSubCodiceErrore = this.receiver.getListaSubCodiceErrore();
		}
		return result;
	}
	
	@Override
	public boolean processOutgoing(OpenSPCoop2Message message, Hashtable<String, Object> ctx){
		
		this.sender = new MessageSecuritySender_impl(this);
		
		boolean result = this.sender.process(message);
		if(!result){
			
			if(ctx!=null) {
				if(MessageRole.REQUEST.equals(message.getMessageRole())) {
					ctx.put(org.openspcoop2.core.constants.Costanti.ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA, "true");
				}
				else {
					ctx.put(org.openspcoop2.core.constants.Costanti.ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA, "true");
				}
			}
			
			this.codiceErrore = this.sender.getCodiceErrore();
			this.msgErrore = this.sender.getMsgErrore();
		}
		return result;
	}
    
	@Override
	public String getSubject(){
		if(this.receiver!=null)
			return this.receiver.getSubject();
		else
			return null;
	}

}

