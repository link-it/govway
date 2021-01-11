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

package org.openspcoop2.protocol.modipa.example.soap.non_blocking.push;

import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;

import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.server.stub.AckMessage;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.server.stub.ErrorMessageException;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.server.stub.ErrorMessageFault;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.server.stub.MRequest;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.server.stub.MRequestResponse;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.server.stub.MType;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.server.stub.SOAPCallback;
import org.openspcoop2.utils.id.UniversallyUniqueIdentifierGenerator;

/**
 * ServerImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class ServerPushRequestImpl implements SOAPCallback {

	private boolean generateCorrelationId = false;
	public void setGenerateCorrelationId(boolean generateCorrelationId) {
		this.generateCorrelationId = generateCorrelationId;
	}
	
	@Override
	public void mRequest(MRequest bodyParam, String replyTo, Holder<MRequestResponse> response, Holder<String> headerResponse)
			throws ErrorMessageException {
		
		MType body = bodyParam.getM();
		if(bodyParam==null || body==null) {
    		String descrizione = "Dati richiesta non forniti";
    		throwFault(400, descrizione);
    	}
		
		System.out.println("Ricevuta richiesta (replyTo: "+replyTo+")");
    	
    	System.out.println("Parametri richiesta:");
    	System.out.println("\tA1-List: "+(body.getA()!=null && body.getA().getA1S()!=null ? body.getA().getA1S() : null));
    	System.out.println("\tA2: "+(body.getA()!=null ? body.getA().getA2() : null));
    	System.out.println("\tB: "+body.getB());
    	
    	
    	MRequestResponse responseStato = new MRequestResponse();
    	AckMessage ackMessage = new AckMessage();
    	ackMessage.setOutcome("ACCEPTED");
    	responseStato.setReturn(ackMessage);
    	response.value = responseStato;
    	
    	if(this.generateCorrelationId) {
    		org.openspcoop2.utils.id.UniversallyUniqueIdentifierGenerator gen = new UniversallyUniqueIdentifierGenerator();
    		try {
    			headerResponse.value = gen.newID().toString();
    		}catch(Exception e) {
        		String descrizione = "Generazione ID Correlazione fallita: "+e.getMessage();
        		throwFault(500, descrizione);
    		}
    	}
		
	}
    
	public void throwFault(int code, String descrizione) throws ErrorMessageException {
		
		ErrorMessageFault fault = new ErrorMessageFault();
		fault.setCustomFaultCode("SERVER-ERROR-"+code);
		throw new ErrorMessageException(descrizione, fault);

	}



}
