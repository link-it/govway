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


package org.openspcoop2.pdd.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.message.ForcedResponseMessage;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.WrappedHttpServletResponse;

/**
 * CORSWrappedHttpServletResponse
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CORSWrappedHttpServletResponse extends WrappedHttpServletResponse {

	private Map<String, List<String>> properties = new HashMap<String, List<String>>();
	private boolean portaApplicativa;
	
	public CORSWrappedHttpServletResponse(boolean portaApplicativa) {
		super(null);
		this.portaApplicativa = portaApplicativa;
	}

	@Override
	public void setHeader(String key, String value) {
		TransportUtils.setHeader(this.properties,key, value);
	}
	@Override
	public void addHeader(String key, String value) {
		TransportUtils.addHeader(this.properties,key, value);
	}
	
	
	private OpenSPCoop2Message message;
	private int status;
	
	public OpenSPCoop2Message buildMessage() {
		
		this.message = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createEmptyMessage(MessageType.BINARY, MessageRole.RESPONSE);
		ForcedResponseMessage forcedResponseMessage = new ForcedResponseMessage();
		forcedResponseMessage.setContent(null); // vuoto
		forcedResponseMessage.setContentType(null); // vuoto
		if(this.portaApplicativa) {
			this.status = OpenSPCoop2Properties.getInstance().getGestioneCORS_returnCode_ricezioneBuste();
		}
		else {
			this.status = OpenSPCoop2Properties.getInstance().getGestioneCORS_returnCode_ricezioneContenutiApplicativi();
		}
		forcedResponseMessage.setResponseCode(this.status+"");
		forcedResponseMessage.setHeadersValues(this.properties);
		this.message.forceResponse(forcedResponseMessage);
		
		return this.message;
	}

	@Override
	public int getStatus() {
		return this.status;
	}
	public Map<String, List<String>> getHeadersValues() {
		return this.properties;
	}
}
