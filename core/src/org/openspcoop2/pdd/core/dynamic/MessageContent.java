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
package org.openspcoop2.pdd.core.dynamic;

import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.protocol.sdk.Context;
import org.w3c.dom.Element;

/**
 * MessageContent
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageContent {

	private OpenSPCoop2MessageFactory messageFactory;
	
	private OpenSPCoop2SoapMessage soapMessage;
	private OpenSPCoop2RestXmlMessage restXmlMessage;
	private OpenSPCoop2RestJsonMessage restJsonMessage;
	
	private String idTransazione;
	private boolean bufferMessage_readOnly;
	
	public MessageContent(OpenSPCoop2SoapMessage soapMessage, boolean bufferMessage_readOnly, Context context) {
		this.soapMessage = soapMessage;
		init(this.soapMessage, bufferMessage_readOnly, context);
	}
	public MessageContent(OpenSPCoop2RestXmlMessage restXmlMessage, boolean bufferMessage_readOnly, Context context) {
		this.restXmlMessage = restXmlMessage;
		init(this.restXmlMessage, bufferMessage_readOnly, context);
	}
	public MessageContent(OpenSPCoop2RestJsonMessage restJsonMessage, boolean bufferMessage_readOnly, Context context) {
		this.restJsonMessage = restJsonMessage;
		init(this.restJsonMessage, bufferMessage_readOnly, context);
	}
	private void init(OpenSPCoop2Message msg, boolean bufferMessage_readOnly, Context context) {
		this.messageFactory = msg.getFactory();
		this.bufferMessage_readOnly = bufferMessage_readOnly;
		if(context!=null) {
			this.idTransazione = (String)context.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		}
	}
	
	public OpenSPCoop2MessageFactory getMessageFactory() {
		return this.messageFactory;
	}

	public boolean isJson() {
		return this.restJsonMessage!=null;
	}
	public boolean isXml() {
		return this.soapMessage!=null || this.restXmlMessage!=null;
	}
	
	private Element element = null;
	public Element getElement() throws MessageException, MessageNotSupportedException {
		if(this.element!=null) {
			return this.element;
		}
		
		boolean checkSoapBodyEmpty = false; // devo poter fare xpath anche su soapBody empty
		if(this.soapMessage!=null) {
			this.element = MessageUtils.getContentElement(this.soapMessage, checkSoapBodyEmpty, this.bufferMessage_readOnly, this.idTransazione);
		}
		else if(this.restXmlMessage!=null) {
			this.element = MessageUtils.getContentElement(this.restXmlMessage, checkSoapBodyEmpty, this.bufferMessage_readOnly, this.idTransazione);
		}
		
		return this.element;
	}
	
	private String elementJson = null;
	public String getElementJson() throws MessageException, MessageNotSupportedException {
		if(this.elementJson!=null) {
			return this.elementJson;
		}
		
		if(this.restJsonMessage!=null) {
			this.elementJson = MessageUtils.getContentString(this.restJsonMessage, this.bufferMessage_readOnly, this.idTransazione);
		}
		
		return this.elementJson;
	}
	
	public void setUpdatable() throws MessageException {
		OpenSPCoop2Message msg = null;
		if(this.soapMessage!=null) {
			msg = this.soapMessage;
		}
		else if(this.restXmlMessage!=null) {
			msg = this.restXmlMessage;
		}
		else if(this.restJsonMessage!=null) {
			msg = this.restJsonMessage;
		}
		MessageUtils.setUpdatable(msg);
	}
}
