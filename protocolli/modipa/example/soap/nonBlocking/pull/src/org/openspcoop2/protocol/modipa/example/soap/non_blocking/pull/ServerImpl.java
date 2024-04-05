/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull;

import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.Holder;

import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.ErrorMessageException;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.ErrorMessageFault;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MProcessingStatus;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MProcessingStatusResponse;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MRequest;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MRequestResponse;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MResponse;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MResponseResponse;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MResponseType;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MType;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.ProcessingStatus;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.SOAPPull;
import org.openspcoop2.utils.id.UUIDUtilsGenerator;

/**
 * ServerImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@BindingType(value = jakarta.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class ServerImpl implements SOAPPull {

	@Override
	public void mRequest(MRequest bodyParam, Holder<MRequestResponse> response, Holder<String> correlationId)
			throws ErrorMessageException {
		
		System.out.println("Ricevuta richiesta di elaborazione PULL");

		if(bodyParam==null) {
    		String descrizione = "Dati richiesta non forniti";
    		throwFault(400, descrizione);
    	}
		MType body = bodyParam.getM();
    	if(body==null) {
    		String descrizione = "Dati richiesta non forniti";
    		throwFault(400, descrizione);
    	}
    	
    	System.out.println("Parametri richiesta:");
    	System.out.println("\tA1-List: "+(body.getA()!=null ? body.getA().getA1S() : null));
    	System.out.println("\tA2: "+(body.getA()!=null ? body.getA().getA2() : null));
    	System.out.println("\tB: "+body.getB());
		
    	response.value = new MRequestResponse();
    	response.value.setReturn(new ProcessingStatus());
    	response.value.getReturn().setStatus("accepted");
    	response.value.getReturn().setMessage("Preso carico della richiesta");
    	
		try {
			correlationId.value = UUIDUtilsGenerator.newUUID();
		}catch(Exception e) {
    		String descrizione = "Generazione ID Correlazione fallita: "+e.getMessage();
    		throwFault(500, descrizione);
		}
    	
    	return;
		
	}
	
	@Override
	public MProcessingStatusResponse mProcessingStatus(MProcessingStatus bodyParam, String correlationId)
			throws ErrorMessageException {
		
		System.out.println("Ricevuta richiesta stato PULL per idCorrelazione '"+correlationId+"'");
		
		if(correlationId==null) {
    		String descrizione = "IdOperazione non fornito";
    		throwFault(400, descrizione);
    	}
		
		boolean risorsaNonAncoraPronta = correlationId.contains("NOT_READY");
		
		MProcessingStatusResponse response = new MProcessingStatusResponse();
		ProcessingStatus status = new ProcessingStatus();
		response.setReturn(status);
				
		if(risorsaNonAncoraPronta) {
			status.setStatus("processing");
			status.setMessage("Richiesta in fase di processamento");
		}
		else {
			status.setStatus("done");
			status.setMessage("Richiesta completata");
		}
		
		return response;
		
	}

	@Override
	public MResponseResponse mResponse(MResponse bodyParam, String correlationId) throws ErrorMessageException {
		
		System.out.println("Ricevuta richiesta per ottenere la risposta con idCorrelazione '"+correlationId+"'");
		
		if(correlationId==null) {
    		String descrizione = "IdOperazione non fornito";
    		throwFault(400, descrizione);
    	}
		
		MResponseResponse response = new MResponseResponse();
		response.setReturn(new MResponseType());
		response.getReturn().setC("Risultato C");
		return response;
		
	}
	
	public void throwFault(int code, String descrizione) throws ErrorMessageException {
		
		ErrorMessageFault fault = new ErrorMessageFault();
		fault.setCustomFaultCode("SERVER-ERROR-"+code);
		throw new ErrorMessageException(descrizione, fault);

	}



}
