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
	public boolean processIncoming(OpenSPCoop2Message message, Busta busta){
		
		this.receiver = new MessageSecurityReceiver_impl(this);
		
		boolean result = this.receiver.process(message, busta);
		if(!result){
			this.codiceErrore = this.receiver.getCodiceErrore();
			this.msgErrore = this.receiver.getMsgErrore();
			this.listaSubCodiceErrore = this.receiver.getListaSubCodiceErrore();
		}
		return result;
	}
	
	@Override
	public boolean processOutgoing(OpenSPCoop2Message message){
		
		this.sender = new MessageSecuritySender_impl(this);
		
		boolean result = this.sender.process(message);
		if(!result){
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

