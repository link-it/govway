/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

package org.openspcoop2.protocol.modipa.example.soap.non_blocking.push;

import javax.xml.ws.BindingType;

import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.client.stub.AckMessage;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.client.stub.ErrorMessageException;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.client.stub.ErrorMessageFault;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.client.stub.MRequestResponse;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.client.stub.MRequestResponseResponse;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.client.stub.MResponseType;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.client.stub.SOAPCallbackClient;

/**
 * ServerImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class ServerPushResponseImpl implements SOAPCallbackClient {

	@Override
	public MRequestResponseResponse mRequestResponse(MRequestResponse bodyParam, String correlationId) throws ErrorMessageException {
		
		System.out.println("Ricevuta richiesta (correlationId: "+correlationId+")");
		
		MResponseType body = bodyParam.getReturn();
		if(bodyParam==null || body==null) {
    		String descrizione = "Dati richiesta non forniti";
    		throwFault(400, descrizione);
    	}
		
		MRequestResponseResponse response = new MRequestResponseResponse();
		AckMessage ackMessage = new AckMessage();
    	ackMessage.setOutcome("OK");
		response.setReturn(ackMessage);
		
		return response;
	}
    
	public void throwFault(int code, String descrizione) throws ErrorMessageException {
		
		ErrorMessageFault fault = new ErrorMessageFault();
		fault.setCustomFaultCode("SERVER-ERROR-"+code);
		throw new ErrorMessageException(descrizione, fault);

	}

}
